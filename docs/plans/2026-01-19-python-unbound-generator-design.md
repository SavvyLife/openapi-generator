# Python-Unbound Generator Design

## Overview

A new OpenAPI generator (`python-unbound`) that produces async Python API clients using `RequestClient` for NATS-based communication instead of HTTP.

## Key Decisions

| Decision | Choice |
|----------|--------|
| Generator name | `python-unbound` |
| Base class | `AbstractPythonCodegen` (copy from `python` generator) |
| Communication | `RequestClient` via NATS (not HTTP) |
| Import path | `from unbound_core.request_client import RequestClient` |
| Dependency | Peer dependency (not in pyproject.toml) |
| Async/Sync | Async-only |
| Authentication | Configure at client init, auto-passed to all requests |
| Models | Pydantic v2 |

## Generated Package Structure

```
my_client/
├── __init__.py
├── api/
│   ├── __init__.py
│   ├── users_api.py        # One file per API tag
│   └── roles_api.py
├── models/
│   ├── __init__.py
│   ├── user.py             # Pydantic v2 models
│   └── role.py
├── api_client.py           # Base client with auth/headers config
├── configuration.py        # Client configuration (timeout, headers)
├── exceptions.py           # Wrapped exceptions
└── py.typed                # PEP 561 marker
```

## API Class Pattern

```python
from typing import Optional, List
from unbound_core.request_client import RequestClient
from ..models.user import User, CreateUserRequest
from ..configuration import Configuration


class UsersApi:
    def __init__(self, configuration: Optional[Configuration] = None):
        self.configuration = configuration or Configuration()

    async def get_user(self, user_id: str) -> User:
        """Get a user by ID."""
        response = await RequestClient.request(
            "/v1/users/{userId}",
            method="get",
            path_params={"userId": user_id},
            headers=self.configuration.headers,
            timeout=self.configuration.timeout,
        )
        response.raise_for_status()
        return User.model_validate(response.json())

    async def create_user(self, create_user_request: CreateUserRequest) -> User:
        """Create a new user."""
        response = await RequestClient.request(
            "/v1/users",
            method="post",
            headers=self.configuration.headers,
            body=create_user_request.model_dump(exclude_none=True),
            timeout=self.configuration.timeout,
        )
        response.raise_for_status()
        return User.model_validate(response.json())
```

## Configuration Class

```python
from typing import Dict, Optional


class Configuration:
    def __init__(
        self,
        headers: Optional[Dict[str, str]] = None,
        timeout: float = 5.0,
    ):
        self.headers = headers or {}
        self.timeout = timeout

    def with_auth(self, token: str) -> "Configuration":
        """Return new config with Authorization header set."""
        new_headers = {**self.headers, "Authorization": f"Bearer {token}"}
        return Configuration(headers=new_headers, timeout=self.timeout)

    def with_account(self, account_id: str) -> "Configuration":
        """Return new config with accountId header set."""
        new_headers = {**self.headers, "accountId": account_id}
        return Configuration(headers=new_headers, timeout=self.timeout)
```

## Error Handling

```python
from typing import Optional, Any


class ApiException(Exception):
    """Base exception for API errors."""
    def __init__(
        self,
        status_code: Optional[int] = None,
        message: str = "",
        body: Optional[Any] = None,
    ):
        self.status_code = status_code
        self.message = message
        self.body = body
        super().__init__(message)


class NoRespondersException(ApiException):
    """No service is handling this endpoint."""
    pass


class TimeoutException(ApiException):
    """Request timed out."""
    pass


class ValidationException(ApiException):
    """Response validation failed."""
    pass
```

API methods wrap NATS errors:

```python
from nats.errors import TimeoutError, NoRespondersError

async def get_user(self, user_id: str) -> User:
    try:
        response = await RequestClient.request(...)
        response.raise_for_status()
        return User.model_validate(response.json())
    except NoRespondersError:
        raise NoRespondersException(message=f"No handler for /v1/users/{user_id}")
    except TimeoutError:
        raise TimeoutException(message="Request timed out")
    except httpx.HTTPStatusError as e:
        raise ApiException(status_code=e.response.status_code, body=e.response.text)
```

## Implementation Plan

### Files to Create

1. **Java Generator Class:**
   ```
   modules/openapi-generator/src/main/java/org/openapitools/codegen/languages/PythonUnboundClientCodegen.java
   ```
   - Extends `AbstractPythonCodegen`
   - Sets generator name to `python-unbound`
   - Configures async-only, Pydantic v2
   - Sets template directory

2. **Mustache Templates:**
   ```
   modules/openapi-generator/src/main/resources/python-unbound/
   ├── api.mustache              # API class template
   ├── api_client.mustache       # Base client (minimal)
   ├── configuration.mustache    # Configuration class
   ├── exceptions.mustache       # Exception classes
   ├── model.mustache            # Pydantic v2 models
   ├── __init__.mustache         # Package init files
   ├── pyproject.mustache        # pyproject.toml
   └── py.typed.mustache         # Type marker
   ```

3. **Service Registration:**
   ```
   modules/openapi-generator/src/main/resources/META-INF/services/org.openapitools.codegen.CodegenConfig
   ```
   - Add line: `org.openapitools.codegen.languages.PythonUnboundClientCodegen`

### Approach

Copy the existing `python` generator templates as a starting point, then modify:
- Remove HTTP/REST client code
- Replace with `RequestClient.request()` calls
- Simplify to async-only
- Update configuration for headers-based auth
