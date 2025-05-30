-module(openapi_server).
-moduledoc """
This is a sample server Petstore server. For this sample, you can use the api key `special-key` to test the authorization filters.
""".

-define(DEFAULT_LOGIC_HANDLER, openapi_logic_handler).

-export([start/2]).
-ignore_xref([start/2]).

-spec start(term(), #{transport      => tcp | ssl,
                      transport_opts => ranch:opts(),
                      protocol_opts  => cowboy:opts(),
                      logic_handler  => module()}) ->
    {ok, pid()} | {error, any()}.
start(ID, Params) ->
    Transport = maps:get(transport, Params, tcp),
    TransportOpts = maps:get(transport_opts, Params, #{}),
    ProtocolOpts = maps:get(procotol_opts, Params, #{}),
    LogicHandler = maps:get(logic_handler, Params, ?DEFAULT_LOGIC_HANDLER),
    CowboyOpts = get_cowboy_config(LogicHandler, ProtocolOpts),
    case Transport of
        ssl ->
            cowboy:start_tls(ID, TransportOpts, CowboyOpts);
        tcp ->
            cowboy:start_clear(ID, TransportOpts, CowboyOpts)
    end.

get_cowboy_config(LogicHandler, ExtraOpts) ->
    DefaultOpts = get_default_opts(LogicHandler),
    maps:fold(fun get_cowboy_config/3, DefaultOpts, ExtraOpts).

get_cowboy_config(env, #{dispatch := _Dispatch} = Env, AccIn) ->
    AccIn#{env => Env};
get_cowboy_config(env, NewEnv, #{env := OldEnv} = AccIn) ->
    Env = maps:merge(OldEnv, NewEnv),
    AccIn#{env => Env};
get_cowboy_config(Key, Value, AccIn) ->
    AccIn#{Key => Value}.

get_default_dispatch(LogicHandler) ->
    Paths = openapi_router:get_paths(LogicHandler),
    #{dispatch => cowboy_router:compile(Paths)}.

get_default_opts(LogicHandler) ->
    #{env => get_default_dispatch(LogicHandler)}.
