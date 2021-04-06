public Completes<Response> ${routeSignature} {
    <#list valueObjectInitializers as initializer>
    ${initializer}
    </#list>
    return resolve(${idName})
            .andThenTo(${modelAttribute} -> ${routeHandlerInvocation})
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(${adapterHandlerInvocation}))))
            .otherwise(noGreeting -> Response.of(NotFound))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }
