# Python-Unbound Generator Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create a new OpenAPI generator `python-unbound` that produces async Python API clients using `RequestClient` for NATS-based communication instead of HTTP.

**Architecture:** Copy the existing Python generator, strip out HTTP/REST infrastructure, and replace with calls to `RequestClient.request()`. Simplify to async-only with Pydantic v2 models. Configuration holds headers (including auth) passed to all requests.

**Tech Stack:** Java (generator code), Mustache templates, Python 3.9+ (generated code), Pydantic v2

---

## Task 1: Create Java Generator Class

**Files:**
- Create: `modules/openapi-generator/src/main/java/org/openapitools/codegen/languages/PythonUnboundClientCodegen.java`

**Step 1: Create the generator class**

```java
/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.meta.features.*;

import java.io.File;
import java.util.EnumSet;

public class PythonUnboundClientCodegen extends AbstractPythonCodegen implements CodegenConfig {

    public static final String GENERATOR_NAME = "python-unbound";

    public PythonUnboundClientCodegen() {
        super();

        // Force sortParamsByRequiredFlag to true
        sortParamsByRequiredFlag = true;

        modifyFeatureSet(features -> features
                .includeDocumentationFeatures(DocumentationFeature.Readme)
                .wireFormatFeatures(EnumSet.of(WireFormatFeature.JSON))
                .excludeGlobalFeatures(
                        GlobalFeature.XMLStructureDefinitions,
                        GlobalFeature.Callbacks,
                        GlobalFeature.LinkObjects,
                        GlobalFeature.ParameterStyling
                )
                .includeSchemaSupportFeatures(
                        SchemaSupportFeature.Polymorphism,
                        SchemaSupportFeature.allOf,
                        SchemaSupportFeature.oneOf,
                        SchemaSupportFeature.anyOf
                )
                .excludeParameterFeatures(
                        ParameterFeature.Cookie
                )
        );

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        // Clear import mapping
        importMapping.clear();

        // Type mappings
        typeMapping.put("array", "List");
        typeMapping.put("set", "List");
        typeMapping.put("map", "Dict");
        typeMapping.put("decimal", "decimal.Decimal");
        typeMapping.put("file", "bytearray");
        typeMapping.put("binary", "bytearray");
        typeMapping.put("ByteArray", "bytearray");

        languageSpecificPrimitives.remove("file");
        languageSpecificPrimitives.add("decimal.Decimal");
        languageSpecificPrimitives.add("bytearray");
        languageSpecificPrimitives.add("none_type");

        supportsInheritance = true;
        modelPackage = "models";
        apiPackage = "api";
        outputFolder = "generated-code" + File.separatorChar + "python-unbound";

        modelTemplateFiles.put("model.mustache", ".py");
        apiTemplateFiles.put("api.mustache", ".py");

        embeddedTemplateDir = templateDir = "python-unbound";

        modelDocTemplateFiles.put("model_doc.mustache", ".md");
        apiDocTemplateFiles.put("api_doc.mustache", ".md");

        // Default HIDE_GENERATION_TIMESTAMP to true
        hideGenerationTimestamp = Boolean.TRUE;

        // CLI options
        cliOptions.clear();
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME, "python package name (convention: snake_case).")
                .defaultValue("openapi_client"));
        cliOptions.add(new CliOption(CodegenConstants.PROJECT_NAME, "python project name in setup.py (e.g. petstore-api)."));
        cliOptions.add(new CliOption(CodegenConstants.PACKAGE_VERSION, "python package version.")
                .defaultValue("1.0.0"));
        cliOptions.add(new CliOption(CodegenConstants.HIDE_GENERATION_TIMESTAMP, CodegenConstants.HIDE_GENERATION_TIMESTAMP_DESC)
                .defaultValue(Boolean.TRUE.toString()));
        cliOptions.add(new CliOption(CodegenConstants.SOURCECODEONLY_GENERATION, CodegenConstants.SOURCECODEONLY_GENERATION_DESC)
                .defaultValue(Boolean.FALSE.toString()));
    }

    @Override
    public void processOpts() {
        this.setLegacyDiscriminatorBehavior(false);

        super.processOpts();

        // Map to Dot instead of Period
        specialCharReplacements.put(".", "Dot");

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
            setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
        }

        if (additionalProperties.containsKey(CodegenConstants.PROJECT_NAME)) {
            setProjectName((String) additionalProperties.get(CodegenConstants.PROJECT_NAME));
        } else {
            setProjectName(packageName.replaceAll("_", "-"));
        }

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_VERSION)) {
            setPackageVersion((String) additionalProperties.get(CodegenConstants.PACKAGE_VERSION));
        }

        additionalProperties.put(CodegenConstants.PROJECT_NAME, projectName);
        additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
        additionalProperties.put(CodegenConstants.PACKAGE_VERSION, packageVersion);

        Boolean generateSourceCodeOnly = false;
        if (additionalProperties.containsKey(CodegenConstants.SOURCECODEONLY_GENERATION)) {
            generateSourceCodeOnly = Boolean.valueOf(additionalProperties.get(CodegenConstants.SOURCECODEONLY_GENERATION).toString());
        }

        String modelPath = packagePath() + File.separatorChar + modelPackage.replace('.', File.separatorChar);
        String apiPath = packagePath() + File.separatorChar + apiPackage.replace('.', File.separatorChar);

        // Supporting files
        if (!generateSourceCodeOnly) {
            supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
            supportingFiles.add(new SupportingFile("pyproject.mustache", "", "pyproject.toml"));
            supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
            supportingFiles.add(new SupportingFile("py.typed.mustache", packagePath(), "py.typed"));
        }

        supportingFiles.add(new SupportingFile("configuration.mustache", packagePath(), "configuration.py"));
        supportingFiles.add(new SupportingFile("exceptions.mustache", packagePath(), "exceptions.py"));
        supportingFiles.add(new SupportingFile("__init__package.mustache", packagePath(), "__init__.py"));
        supportingFiles.add(new SupportingFile("__init__model.mustache", modelPath, "__init__.py"));
        supportingFiles.add(new SupportingFile("__init__api.mustache", apiPath, "__init__.py"));

        // Handle nested package names
        String[] packageNameSplits = packageName.split("\\.");
        String currentPackagePath = "";
        for (int i = 0; i < packageNameSplits.length - 1; i++) {
            if (i > 0) {
                currentPackagePath = currentPackagePath + File.separatorChar;
            }
            currentPackagePath = currentPackagePath + packageNameSplits[i];
            supportingFiles.add(new SupportingFile("__init__.mustache", currentPackagePath, "__init__.py"));
        }

        modelPackage = this.packageName + "." + modelPackage;
        apiPackage = this.packageName + "." + apiPackage;
    }

    @Override
    public String toModelImport(String name) {
        String modelImport;
        if (name.startsWith("import") || name.startsWith("from")) {
            modelImport = name;
        } else {
            modelImport = "from ";
            if (!"".equals(modelPackage())) {
                modelImport += modelPackage() + ".";
            }
            modelImport += toModelFilename(name) + " import " + name;
        }
        return modelImport;
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return GENERATOR_NAME;
    }

    @Override
    public String getHelp() {
        return "Generates a Python client library using RequestClient for NATS-based communication.";
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separatorChar + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + File.separatorChar + modelPackage().replace('.', File.separatorChar);
    }

    public String packagePath() {
        return packageName.replace('.', File.separatorChar);
    }

    @Override
    public String generatorLanguageVersion() {
        return "3.9+";
    }
}
```

**Step 2: Register the generator in the service file**

Add this line to `modules/openapi-generator/src/main/resources/META-INF/services/org.openapitools.codegen.CodegenConfig`:

```
org.openapitools.codegen.languages.PythonUnboundClientCodegen
```

(Add it alphabetically after the other Python generators)

**Step 3: Commit**

```bash
git add modules/openapi-generator/src/main/java/org/openapitools/codegen/languages/PythonUnboundClientCodegen.java
git add modules/openapi-generator/src/main/resources/META-INF/services/org.openapitools.codegen.CodegenConfig
git commit -m "feat(python-unbound): add generator class and register it"
```

---

## Task 2: Create Template Directory Structure

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/` directory
- Copy from: `modules/openapi-generator/src/main/resources/python/`

**Step 1: Create the template directory and copy model templates**

Copy these files from `python/` to `python-unbound/` (they can be used as-is or with minor modifications):
- `partial_header.mustache`
- `model.mustache`
- `model_generic.mustache`
- `model_enum.mustache`
- `model_oneof.mustache`
- `model_anyof.mustache`
- `model_doc.mustache`
- `__init__.mustache`
- `__init__api.mustache`
- `__init__model.mustache`
- `__init__package.mustache`
- `gitignore.mustache`
- `py.typed.mustache`

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/
git commit -m "feat(python-unbound): add model and supporting templates"
```

---

## Task 3: Create Configuration Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/configuration.mustache`

**Step 1: Create simplified configuration class**

```mustache
# coding: utf-8

{{>partial_header}}

from typing import Dict, Optional
from typing_extensions import Self


class Configuration:
    """Configuration for the API client.

    :param headers: Default headers to include in all requests.
        Use this for authentication (Authorization, accountId, etc.)
    :param timeout: Default timeout for requests in seconds.

    Example:

    config = Configuration(
        headers={
            "Authorization": "Bearer your-token",
            "accountId": "your-account-id",
        },
        timeout=10.0,
    )
    """

    _default: Optional[Self] = None

    def __init__(
        self,
        headers: Optional[Dict[str, str]] = None,
        timeout: float = 5.0,
    ) -> None:
        self.headers = headers or {}
        self.timeout = timeout

    def with_headers(self, headers: Dict[str, str]) -> Self:
        """Return new config with additional headers merged in."""
        new_headers = {**self.headers, **headers}
        return self.__class__(headers=new_headers, timeout=self.timeout)

    def with_auth(self, token: str) -> Self:
        """Return new config with Authorization header set."""
        return self.with_headers({"Authorization": f"Bearer {token}"})

    def with_account(self, account_id: str) -> Self:
        """Return new config with accountId header set."""
        return self.with_headers({"accountId": account_id})

    def with_timeout(self, timeout: float) -> Self:
        """Return new config with different timeout."""
        return self.__class__(headers=self.headers.copy(), timeout=timeout)

    @classmethod
    def set_default(cls, default: Optional[Self]) -> None:
        """Set the default configuration instance."""
        cls._default = default

    @classmethod
    def get_default(cls) -> Self:
        """Get the default configuration instance."""
        if cls._default is None:
            cls._default = cls()
        return cls._default
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/configuration.mustache
git commit -m "feat(python-unbound): add configuration template"
```

---

## Task 4: Create Exceptions Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/exceptions.mustache`

**Step 1: Create exceptions module**

```mustache
# coding: utf-8

{{>partial_header}}

from typing import Any, Optional


class ApiException(Exception):
    """Base exception for API errors."""

    def __init__(
        self,
        status_code: Optional[int] = None,
        message: str = "",
        body: Optional[Any] = None,
    ) -> None:
        self.status_code = status_code
        self.message = message
        self.body = body
        super().__init__(message)

    def __str__(self) -> str:
        error_message = f"({self.status_code}) {self.message}"
        if self.body:
            error_message += f"\nBody: {self.body}"
        return error_message


class NoRespondersException(ApiException):
    """No service is handling this endpoint."""

    def __init__(self, message: str = "No responders available") -> None:
        super().__init__(status_code=503, message=message)


class TimeoutException(ApiException):
    """Request timed out."""

    def __init__(self, message: str = "Request timed out") -> None:
        super().__init__(status_code=504, message=message)


class BadRequestException(ApiException):
    """HTTP 400 Bad Request."""
    pass


class UnauthorizedException(ApiException):
    """HTTP 401 Unauthorized."""
    pass


class ForbiddenException(ApiException):
    """HTTP 403 Forbidden."""
    pass


class NotFoundException(ApiException):
    """HTTP 404 Not Found."""
    pass


class ConflictException(ApiException):
    """HTTP 409 Conflict."""
    pass


class UnprocessableEntityException(ApiException):
    """HTTP 422 Unprocessable Entity."""
    pass


class ServiceException(ApiException):
    """HTTP 5xx Server Error."""
    pass


def raise_for_status(status_code: int, message: str = "", body: Any = None) -> None:
    """Raise appropriate exception based on status code."""
    if status_code < 400:
        return

    exception_map = {
        400: BadRequestException,
        401: UnauthorizedException,
        403: ForbiddenException,
        404: NotFoundException,
        409: ConflictException,
        422: UnprocessableEntityException,
    }

    if status_code in exception_map:
        raise exception_map[status_code](status_code=status_code, message=message, body=body)
    elif 500 <= status_code < 600:
        raise ServiceException(status_code=status_code, message=message, body=body)
    else:
        raise ApiException(status_code=status_code, message=message, body=body)
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/exceptions.mustache
git commit -m "feat(python-unbound): add exceptions template"
```

---

## Task 5: Create API Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/api.mustache`

**Step 1: Create the main API template**

```mustache
# coding: utf-8

{{>partial_header}}

from typing import Any, Dict, List, Optional, Union
{{#imports}}
{{import}}
{{/imports}}

from unbound_core.request_client import RequestClient
from nats.errors import TimeoutError, NoRespondersError

from {{packageName}}.configuration import Configuration
from {{packageName}}.exceptions import (
    ApiException,
    NoRespondersException,
    TimeoutException,
    raise_for_status,
)


{{#operations}}
class {{classname}}:
    """NOTE: This class is auto generated by OpenAPI Generator
    Ref: https://openapi-generator.tech

    Do not edit the class manually.
    """

    def __init__(self, configuration: Optional[Configuration] = None) -> None:
        self.configuration = configuration or Configuration.get_default()
{{#operation}}

    async def {{operationId}}(
        self,
{{#allParams}}
{{#required}}
        {{paramName}}: {{#isArray}}List[{{#items}}{{dataType}}{{/items}}]{{/isArray}}{{^isArray}}{{dataType}}{{/isArray}},
{{/required}}
{{/allParams}}
{{#allParams}}
{{^required}}
        {{paramName}}: Optional[{{#isArray}}List[{{#items}}{{dataType}}{{/items}}]{{/isArray}}{{^isArray}}{{dataType}}{{/isArray}}] = None,
{{/required}}
{{/allParams}}
    ) -> {{#returnType}}{{{.}}}{{/returnType}}{{^returnType}}None{{/returnType}}:
        """{{#summary}}{{{.}}}{{/summary}}{{^summary}}{{operationId}}{{/summary}}

{{#notes}}
        {{{.}}}

{{/notes}}
{{#allParams}}
        :param {{paramName}}:{{#description}} {{{.}}}{{/description}}{{#required}} (required){{/required}}
{{/allParams}}
{{#returnType}}
        :return: {{{.}}}
{{/returnType}}
        """
        # Build path parameters
        _path_params: Dict[str, str] = {}
{{#pathParams}}
        if {{paramName}} is not None:
            _path_params["{{baseName}}"] = {{#isEnumRef}}{{paramName}}.value{{/isEnumRef}}{{^isEnumRef}}str({{paramName}}){{/isEnumRef}}
{{/pathParams}}

        # Build query parameters
        _query_params: Dict[str, Any] = {}
{{#queryParams}}
        if {{paramName}} is not None:
            _query_params["{{baseName}}"] = {{#isEnumRef}}{{paramName}}.value{{/isEnumRef}}{{^isEnumRef}}{{paramName}}{{/isEnumRef}}
{{/queryParams}}

        # Build request body
        _body: Optional[Any] = None
{{#bodyParam}}
        if {{paramName}} is not None:
            {{#isArray}}
            _body = [item.model_dump(by_alias=True, exclude_none=True) if hasattr(item, "model_dump") else item for item in {{paramName}}]
            {{/isArray}}
            {{^isArray}}
            _body = {{paramName}}.model_dump(by_alias=True, exclude_none=True) if hasattr({{paramName}}, "model_dump") else {{paramName}}
            {{/isArray}}
{{/bodyParam}}

        try:
            response = await RequestClient.request(
                "{{{path}}}",
                method="{{#lambda.lowercase}}{{httpMethod}}{{/lambda.lowercase}}",
                path_params=_path_params if _path_params else None,
                query_params=_query_params if _query_params else None,
                headers=self.configuration.headers,
                body=_body,
                timeout=self.configuration.timeout,
            )
        except NoRespondersError:
            raise NoRespondersException(message=f"No handler for {{httpMethod}} {{{path}}}")
        except TimeoutError:
            raise TimeoutException(message=f"Request timed out: {{httpMethod}} {{{path}}}")

        raise_for_status(response.status_code, body=response.text if response.content else None)

{{#returnType}}
        if response.status_code == 204:
            return None  # type: ignore

        data = response.json()
{{#returnTypeIsPrimitive}}
        return data
{{/returnTypeIsPrimitive}}
{{^returnTypeIsPrimitive}}
{{#isArray}}
        return [{{returnBaseType}}.model_validate(item) for item in data]
{{/isArray}}
{{^isArray}}
        return {{returnType}}.model_validate(data)
{{/isArray}}
{{/returnTypeIsPrimitive}}
{{/returnType}}
{{^returnType}}
        return None
{{/returnType}}

{{/operation}}
{{/operations}}
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/api.mustache
git commit -m "feat(python-unbound): add api template with RequestClient integration"
```

---

## Task 6: Create API Documentation Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/api_doc.mustache`

**Step 1: Create the API documentation template**

```mustache
# {{classname}}

All URIs are relative to NATS subjects (no base URL)

Method | Description
------------- | -------------
{{#operations}}
{{#operation}}
[**{{operationId}}**]({{classname}}.md#{{operationId}}) | {{summary}}
{{/operation}}
{{/operations}}

{{#operations}}
{{#operation}}
# **{{operationId}}**
> {{#returnType}}{{{.}}} {{/returnType}}{{operationId}}({{#allParams}}{{#required}}{{paramName}}{{^-last}}, {{/-last}}{{/required}}{{/allParams}}{{#hasOptionalParams}}{{#hasRequiredParams}}, {{/hasRequiredParams}}{{#allParams}}{{^required}}{{paramName}}={{paramName}}{{^-last}}, {{/-last}}{{/required}}{{/allParams}}{{/hasOptionalParams}})

{{{summary}}}{{#notes}}

{{{.}}}{{/notes}}

### Example

```python
from {{packageName}}.configuration import Configuration
from {{packageName}}.api.{{classFilename}} import {{classname}}
{{#hasParams}}
{{#allParams}}
{{#-first}}
{{#isModel}}
from {{packageName}}.models.{{model.classFilename}} import {{dataType}}
{{/isModel}}
{{/-first}}
{{/allParams}}
{{/hasParams}}

# Configure the client
config = Configuration(
    headers={
        "Authorization": "Bearer YOUR_TOKEN",
        "accountId": "YOUR_ACCOUNT_ID",
    }
)

api = {{classname}}(configuration=config)
{{#allParams}}
{{#required}}
{{paramName}} = {{{example}}}  # {{{dataType}}} | {{{description}}}
{{/required}}
{{/allParams}}
{{#allParams}}
{{^required}}
{{paramName}} = {{{example}}}  # {{{dataType}}} | {{{description}}} (optional)
{{/required}}
{{/allParams}}

result = await api.{{{operationId}}}({{#allParams}}{{#required}}{{paramName}}{{^-last}}, {{/-last}}{{/required}}{{/allParams}}{{#hasOptionalParams}}{{#hasRequiredParams}}, {{/hasRequiredParams}}{{#allParams}}{{^required}}{{paramName}}={{paramName}}{{^-last}}, {{/-last}}{{/required}}{{/allParams}}{{/hasOptionalParams}})
print(result)
```

### Parameters

{{^allParams}}This endpoint does not need any parameter.{{/allParams}}
{{#allParams}}
{{#-first}}
Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
{{/-first}}
{{/allParams}}
{{#allParams}}
 **{{paramName}}** | {{#isFile}}**{{dataType}}**{{/isFile}}{{#isPrimitiveType}}**{{dataType}}**{{/isPrimitiveType}}{{^isPrimitiveType}}{{^isFile}}[**{{dataType}}**]({{baseType}}.md){{/isFile}}{{/isPrimitiveType}}| {{description}} | {{^required}}[optional] {{/required}}{{#defaultValue}}[default to {{.}}]{{/defaultValue}}
{{/allParams}}

### Return type

{{#returnType}}{{#returnTypeIsPrimitive}}**{{{returnType}}}**{{/returnTypeIsPrimitive}}{{^returnTypeIsPrimitive}}[**{{{returnType}}}**]({{returnBaseType}}.md){{/returnTypeIsPrimitive}}{{/returnType}}{{^returnType}}void (empty response body){{/returnType}}

{{/operation}}
{{/operations}}
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/api_doc.mustache
git commit -m "feat(python-unbound): add api documentation template"
```

---

## Task 7: Create pyproject.toml Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/pyproject.mustache`

**Step 1: Create pyproject.toml template**

```mustache
[project]
name = "{{{projectName}}}"
version = "{{{packageVersion}}}"
description = "{{{appName}}}"
readme = "README.md"
requires-python = ">=3.9"
license = "{{{licenseInfo}}}{{^licenseInfo}}MIT{{/licenseInfo}}"
keywords = ["OpenAPI", "OpenAPI-Generator", "{{{appName}}}", "NATS"]
authors = [
    { name = "{{infoName}}{{^infoName}}OpenAPI Generator Community{{/infoName}}", email = "{{infoEmail}}{{^infoEmail}}team@openapitools.org{{/infoEmail}}" }
]
dependencies = [
    "pydantic>=2",
    "orjson>=3.9.0",
    "httpx>=0.25.0",
]

[project.optional-dependencies]
dev = [
    "pytest>=7.2.1",
    "pytest-asyncio>=0.21.0",
    "mypy>=1.5",
]

[build-system]
requires = ["setuptools>=61.0"]
build-backend = "setuptools.build_meta"

[tool.setuptools.packages.find]
include = ["{{{packageName}}}*"]

[tool.mypy]
files = ["{{{packageName}}}"]
strict = true

[tool.pytest.ini_options]
asyncio_mode = "auto"
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/pyproject.mustache
git commit -m "feat(python-unbound): add pyproject.toml template"
```

---

## Task 8: Create README Template

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/README.mustache`

**Step 1: Create README template**

```mustache
# {{{projectName}}}

{{{appDescription}}}

This client uses `RequestClient` for NATS-based communication instead of HTTP.

## Requirements

- Python 3.9+
- `unbound_core` package (provides `RequestClient`)

## Installation

```bash
pip install {{{projectName}}}
```

## Usage

```python
from {{{packageName}}}.configuration import Configuration
{{#apiInfo}}
{{#apis}}
{{#-first}}
from {{{packageName}}}.api.{{classFilename}} import {{classname}}
{{/-first}}
{{/apis}}
{{/apiInfo}}

# Configure the client with authentication
config = Configuration(
    headers={
        "Authorization": "Bearer YOUR_TOKEN",
        "accountId": "YOUR_ACCOUNT_ID",
    },
    timeout=10.0,
)

# Or use builder methods
config = Configuration().with_auth("YOUR_TOKEN").with_account("YOUR_ACCOUNT_ID")

{{#apiInfo}}
{{#apis}}
{{#-first}}
# Create API instance
api = {{classname}}(configuration=config)

# Make requests (all methods are async)
result = await api.some_method()
{{/-first}}
{{/apis}}
{{/apiInfo}}
```

## Configuration

The `Configuration` class accepts:
- `headers`: Dict of headers to include in all requests (use for auth)
- `timeout`: Request timeout in seconds (default: 5.0)

Helper methods:
- `with_auth(token)`: Add Bearer token
- `with_account(account_id)`: Add accountId header
- `with_headers(headers)`: Merge additional headers
- `with_timeout(timeout)`: Set different timeout

## API Documentation

{{#apiInfo}}
{{#apis}}
- [{{{classname}}}](docs/{{{classname}}}.md)
{{/apis}}
{{/apiInfo}}

## Models

{{#models}}
{{#model}}
- [{{{classname}}}](docs/{{{classname}}}.md)
{{/model}}
{{/models}}
```

**Step 2: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/README.mustache
git commit -m "feat(python-unbound): add README template"
```

---

## Task 9: Create __init__ Templates

**Files:**
- Create: `modules/openapi-generator/src/main/resources/python-unbound/__init__package.mustache`
- Create: `modules/openapi-generator/src/main/resources/python-unbound/__init__api.mustache`
- Create: `modules/openapi-generator/src/main/resources/python-unbound/__init__model.mustache`
- Create: `modules/openapi-generator/src/main/resources/python-unbound/__init__.mustache`

**Step 1: Create __init__package.mustache**

```mustache
# coding: utf-8

# flake8: noqa

{{>partial_header}}

__version__ = "{{{packageVersion}}}"

# Import Configuration
from {{packageName}}.configuration import Configuration

# Import exceptions
from {{packageName}}.exceptions import (
    ApiException,
    NoRespondersException,
    TimeoutException,
    BadRequestException,
    UnauthorizedException,
    ForbiddenException,
    NotFoundException,
    ConflictException,
    UnprocessableEntityException,
    ServiceException,
)

# Import APIs
{{#apiInfo}}
{{#apis}}
from {{packageName}}.api.{{classFilename}} import {{classname}}
{{/apis}}
{{/apiInfo}}

# Import models
{{#models}}
{{#model}}
from {{packageName}}.models.{{classFilename}} import {{classname}}
{{/model}}
{{/models}}
```

**Step 2: Create __init__api.mustache**

```mustache
# coding: utf-8

# flake8: noqa

{{>partial_header}}

{{#apiInfo}}
{{#apis}}
from {{packageName}}.api.{{classFilename}} import {{classname}}
{{/apis}}
{{/apiInfo}}
```

**Step 3: Create __init__model.mustache**

```mustache
# coding: utf-8

# flake8: noqa

{{>partial_header}}

{{#models}}
{{#model}}
from {{packageName}}.models.{{classFilename}} import {{classname}}
{{/model}}
{{/models}}
```

**Step 4: Create __init__.mustache**

```mustache
# coding: utf-8

# flake8: noqa

{{>partial_header}}
```

**Step 5: Commit**

```bash
git add modules/openapi-generator/src/main/resources/python-unbound/__init__*.mustache
git add modules/openapi-generator/src/main/resources/python-unbound/__init__.mustache
git commit -m "feat(python-unbound): add __init__ templates"
```

---

## Task 10: Build and Test the Generator

**Step 1: Build the project**

```bash
cd /Users/mark/Dev/openapi-generator
mvn clean install -DskipTests
```

**Step 2: Verify the generator is registered**

```bash
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar list | grep python-unbound
```

Expected output should include `python-unbound`

**Step 3: Test generate with a sample OpenAPI spec**

```bash
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
  -i https://raw.githubusercontent.com/openapitools/openapi-generator/master/modules/openapi-generator/src/test/resources/3_0/petstore.yaml \
  -g python-unbound \
  -o /tmp/python-unbound-test \
  --package-name petstore_client
```

**Step 4: Inspect generated output**

Check the generated files in `/tmp/python-unbound-test/` to verify:
- API classes use `RequestClient.request()`
- Configuration has `headers` and `timeout`
- Exceptions include NATS-specific ones
- Models are Pydantic v2

**Step 5: Commit any fixes needed**

If templates need adjustment based on test output, fix and commit.

---

## Summary

After completing all tasks, you will have:
1. A new `python-unbound` generator registered in openapi-generator
2. Templates that generate async Python clients using `RequestClient`
3. Simplified configuration for headers-based auth
4. NATS-specific exception handling
5. Pydantic v2 models (reused from base Python generator)
