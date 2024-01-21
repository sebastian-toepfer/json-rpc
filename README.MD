[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sebastian-toepfer_json-rpc&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sebastian-toepfer_json-rpc)

# What is json-rpc

is a json-based protocol for rpc. See [JSON-RPC-Spec](https://www.jsonrpc.org/specification).

# Why still using json-rpc

As developer is sometime difficult to create a good RESTful application for the current problem.

Think about it:
- check if a user has a certain permission
- change state of a proerty
- run a long-running job, e.g. scan a file for viruses
- do something within a transaction

Yes, all the above problems can be solved in a RESTful way, but not all developers are [Stefan Tikov](https://tilkov.com/about) and able to think in resources to solve the problems. For those who cannot think in resources, json-rpc can be a solution as it defines a structure that allows procedural thinking. Without the need to invent everything from scratch.

# When not to use json-rpc

If you have clean resources and need a way to access them via http.
Think of [KODI](https://kodi.tv/):
1. Retrieve all available movie:  
  make a simple GET request to the movie endpoint instead of calling a list method via rpc.
2. search for a movie:  
  make a GET request with query parameters to the movie endpoint instead of calling a list method with search parameters via rpc

## Drawbacks form json-rpc over RESTfull

All requests are `POST`, a welcome from `SOAP`, but `GraphQL` does the same. The provider uses 200 (OK) for all responses, so it is not possible to distinguish between success and failure.

# Some examples

Here are some examples of __*bad*__ RESTful solutions for the above example and a json-rpc solution for it.

## Checking whether a caller has a specific permission

Requirement: we need a way to check if a caller has permission to do something.

### The naive way
1. as a consumer, we perform a GET call against a Uri to verify that we have permission to  
  e.g. GET /permissions/listUsers
2. the provider now checks if the caller has the requested permission and returns
   1. different HTTP status codes
      1. 200 (OK) - if the caller has the permission
      2. 403 (FORBIDDEN) - if the caller does not have the permission
   2. an HTTP response with status code 200 (OK) and a Boolean in the body
3. the consumer must now implement a function to check the response and remember if the current user is able to perform a particular action

### A litlle better RESTful

As a provider, we can use HATEOAS to tell the consumer which endpoints are available to the current caller. Each response can tell the consumer what actions are possible now.
1. as a consumer, we ask the provider what the endpoints are (we see the provider as the master resource), so we do a GET againts the entry-point
2. as a provider, we now check for which endpoints the caller has permission and return it as an object to the consumer, e.g. `{ "listUsers":"/api/listsUsers"}`.
3. the consumer can now use this object to derive the possible next actions

This type of API is the most decoupled type. The provider can change the Uri under which a particular action can be performed, and the consumer can decide which action it needs and ignore all others. But we have to work very hard to get it right. It would go beyond the scope to go into more detail.

### The JSON-RPC way
1. as a consumer we run a POST with `{"jsonrpc": "2.0", "method": "checkPermission", "params": ["listUsers"], "id": 1}` as body
2. the provider now checks if the caller has the requested permission and returns
   1. `{"jsonrpc": "2.0", "result": true, "id": 1}`
   2. `{"jsonrpc": "2.0", "result": false, "id": 1}`
3. the consumer must now implement a function to check the response and remember if the current user is able to perform a particular action

In contrast to the naive way:
We just define better communication, what kind of communication we use and don't provide an API that looks like RESTful but isn't 100%

## Change the state of a resource

Requirement: We need a quick way to change the state in one direction, like change the verification-state of a user to verified.

This example is kind of a poor understanding of RESTful, but I've seen it many times in real world api's. And yes we should never use json-rpc to solve it, but we do it here.

### The poor understanding way

1. as a provider, we define an endpoint to change a property of a resource in one direction, e.g. `/persons/{id}/markAsVerified`. As a method we can use PUT or POST, it just doesn't matter
   1. define 204 (NOCONTENT) for the response
   2. define 200 (OK) with the changed resource as body
2. as a consumer, we call the above endpoint to change the resource
   1. provider returns 204
      1. manipulate our state in our resource.
      2. reload the resource in question from the provider
   2. provider returns 200
      1. replace our resource with the new one
      2. update our resource with the values of the provider

### RESTful ###

1. load as consumer the resource in question e.g. via `GET /persons/{id}`
2. as provider return the full resource `{"id": 5, verified: "false"}`
3. as a consumer, modify the property in question and return it as a whole to the provider via `PUT` e.g. `PUT {"id": 5, verified: "true"} /people/5`
4. as a provider, apply the manipulation to the resource and return 204 (NOCONTENT) if successful
5. as a consumer, check if we have not received 204, and then decide whether to resend or cancel the change

Yes, the whole resource is transferred twice, but we can make sure that the two ends have the same view on the resource. The consumer had sent his to the provider and the provider has confirms or denies the update and also communicates this to the consumer so that the consumer can question his view.

### The JSON-RPC way

1. as a consumer we run a `POST` with `{"jsonrpc": "2.0", "method": "markAsVerified", "params": ["5"], "id": 1}` as body
2. as provider, we do the manipulation
   1. do it as notification and return nothing
   2. return the result of the manipulation `{"jsonrpc": "2.0", "result": {"id": 5, verified: "true"}, "id": 1}`
3. as consumer we must now sync our state
   1. use a notification
      1. manipulate our state in our resource.
      2. reload the resource in question from the provider
   2. use the result from the provider
      1. replace our resource with the new one
      2. update our resource with the values of the provider

In contrast to the naive way:
We just define better communication, what kind of communication we use and don't provide an API that looks like RESTful but isn't 100%.

### Execute a long running job

Requirement: We had to scan files for virus.

### The naive way

1. as a consumer POST the contents of the file to the scanner aka provider
2. as a provider, scan the provided content with one or more scanners and return the result (scannresult as status code or body possible)

It is a simple, synchronous method where the provider leaves it up to the consumer to decide if they need a asynchronous method.

### A more RESTful way

1. as a consumer POST the contents of the file to the scanner aka provider
2. s a provider, create an order or resource for the request and return the id and location of the resource with 201 (CREATED)
3. as a consumer, query the status of the created order, whether it is finished (HEAD or GET possible)
4. as provider if the job is finised return the location of the result to the consumer
5. as consumer retrieve the result

As a provider, we must now provide a memory of all our orders and this longer than necessary. Because it is not certain that the consumer will need the result of the order again later. As consumers, we have to poll for the result, which can lead to a higher load on the provider side.

### The JSON-RPC way

It is possible to create the method as a notification and as a synchronous method. When using Notifaction we need polling on the consumer side.

A synchron sample
1. as a consumer we run a `POST` with `{"jsonrpc": "2.0", "method": "scan", "params": ["filecontentAsBase64"], "id": 1}` as body
2. as a provider, scan the provided content with one or more scanners and return `{"jsonrpc": "2.0", "result": {virusfree: "true"}, "id": 1}`

And again, we have clean communication about how our API works. The consumer doesn't have to learn anything new. And the provider doesn't have to worry about how to communicate the result to the consumer either.
In my eyes, this is one of the best use cases for using rpc style.

### Do something within a transaction

Requirement: We had to track changes together.

### The native way

1. as a consumer, perform a `POST {}` to create a transaction
2. as provider return the location of the new created transaction
3. as a consumer, perform all necessary steps with the newly created transaction

It depends on who determines when a transaction is finished. Both force the provider to store the data longer than necessary. He cannot be sure that no consumer needs the status of the transaction anymore.

### The JSON-RPC way
1. as a consumer we run a `POST` with `{"jsonrpc": "2.0", "method": "insideTransaction", "params": ["action1", "action2"]` as body
2. as the provider, create a new transaction and do all the things you want in this

This example uses a notification that allows the provider to do anything without having to send anything back to the consumer. And again, we have clean communication about how our API works. The consumer doesn't have to learn anything new. 
In my eyes, this is a good use cases for using rpc style.

# Usage

see samples.

## Jakarta EE

Add maven dependency
```xml
<dependency>
    <groupId>io.github.sebastian-toepfer.json.rpc</groupId>
    <artifactId>json-rpc-boundary</artifactId>
    <version>0.6.0</version>
</dependency>
```

provide a rpc-runtime via CDI:
```java
@Produces
@RequestScoped
public JsonRpcRuntime jsonRpcRuntime() {
    return new DefaultJsonRpcRuntime(
        new DefaultJsonRpcExecutionContext()
            .withMethod(
                new DefaultJsonRpcMethod(
                    "subtract",
                    List.of("minuend", "subtrahend"),
                    params -> Json.createValue(params.getInt("minuend") - params.getInt("subtrahend"))
                )
            )
    );
}
```

register the rpc endpoint as jax-rs resource
```java
@ApplicationPath("")
public class SampleApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(RpcResource.class);
    }
}
```

## Quarkus

Add maven dependency
```xml
<dependency>
    <groupId>io.github.sebastian-toepfer.json.rpc</groupId>
    <artifactId>json-rpc-boundary</artifactId>
    <version>0.6.0</version>
</dependency>
```

enable jaxrs extension
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive-jsonb</artifactId>
</dependency>
```

extends application.properties (to index the boundary)
```
quarkus.index-dependency.json-rpc-boundary.group-id=io.github.sebastian-toepfer.json.rpc
quarkus.index-dependency.json-rpc-boundary.artifact-id=json-rpc-boundary
```

provide a rpc-runtime via CDI:
```java
@Produces
@RequestScoped
public JsonRpcRuntime jsonRpcRuntime() {
    return new DefaultJsonRpcRuntime(
        new DefaultJsonRpcExecutionContext()
            .withMethod(
                new DefaultJsonRpcMethod(
                    "subtract",
                    List.of("minuend", "subtrahend"),
                    params -> Json.createValue(params.getInt("minuend") - params.getInt("subtrahend"))
                )
            )
    );
}
```

## spring-boot
Add maven dependency
```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>json-rpc-spring-integration-starter</artifactId>
    <version>0.6.0</version>
</dependency>
```

register the rpc endpoint as jax-rs resource
```java
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RpcResource.class);
    }
}
```

provide a rpc-runtime via Spring-DI
```
@Bean
public JsonRpcRuntime jsonRpcRuntime() {
    return new DefaultJsonRpcRuntime(
        new DefaultJsonRpcExecutionContext()
            .withMethod(
                new DefaultJsonRpcMethod(
                    "subtract",
                    List.of("minuend", "subtrahend"),
                    params -> Json.createValue(params.getInt("minuend") - params.getInt("subtrahend"))
                )
            )
    );
}
```

## Extensions

### OpenRPC
a dynamic way of describing the available methods. for more information see [openrpc](https://open-rpc.org/)

Add maven dependency
```xml
<dependency>
    <groupId>io.github.sebastian-toepfer.json.rpc</groupId>
    <artifactId>json-rpc-extension-openrpc</artifactId>
    <version>0.6.0</version>
</dependency>
```

create the method context (used instead of DefaultJsonRpcExecutionContext)
```java
new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("test app", "1.0.0"))
    .withMethod(
        new DescribableJsonRpcMethod(
            new MethodObject(
                "list_pets",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "limit",
                            new JsonSchemaOrReference.Object(
                                JsonSchemas.load(Json.createObjectBuilder().add("type", "integer").build())
                            )
                        )
                            .withDescription("How many items to return at one time (max 100)")
                            .withRequired(false)
                    )
                )
            )
                .withSummary("List all pets")
                .withTags(List.of(new TagOrReference.Object(new TagObject("pets"))))
                .withResult(
                    new MethodObjectResult.Object(
                        new ContentDescriptorObject(
                            "pets",
                            new JsonSchemaOrReference.Reference(
                                new ReferenceObject("#/components/schemas/Pets")
                            )
                        )
                            .withDescription("A paged array of pets")
                    )
                ),
            params -> Json.createArrayBuilder().add("bunnies").add("cats").build()
        )
    )
```
all properties described at https://spec.open-rpc.org/#meta-json-schema can be used. The mandatory properties must be
specified as constructor parameters, optional parameters can be added via with<PropertyName> methods such as
`withDescription("")`. If a parameter can have different types, this is specified via an 'Or' WrapperObject.
e.g. a JsonSchema can be set via Reference or SchemaInstance:
`new JsonSchemaOrReference.Reference(new RefereceObject(""))`
  or
`new JsonSchemaOrReference.Object(new JsonSchemaObject())`.

As of version 0.7.0, it is no longer possible to describe method parameters via a reference or to use a reference as a
parameter.

after deployment the avalible methods can be requested via:
```json
{
  "jsonrpc": "2.0",
  "method": "rpc.discover",
  "id": "1"
}
```
