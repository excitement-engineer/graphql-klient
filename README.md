# GraphQL Klient

An easy to use GraphQL client for Kotlin.

## Installation

## Maven

Add bintray repository:

```xml
<repositories>
    <repository>
        <id>bintray-excitement-engineer-graphql-klient</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/excitement-engineer/graphql-klient</url>
    </repository>
</repositories>
```

Add dependency:

```xml
<dependency>
    <groupId>com.github.excitement-engineer</groupId>
    <artifactId>graphql-klient</artifactId>
    <version>{version}</version>
</dependency>
```

## Gradle

Add bintray repository:

```
repositories {
    maven {
        url  "https://dl.bintray.com/excitement-engineer/graphql-klient"
    }
}
```

Add dependency:

```
compile 'com.github.excitement-engineer:graphql-klient:{version}'
```

## Quickstart

In order to make requests to a graphQL server we first need to defined a `GraphQLClient`.

```kt
val client = GraphQLClient("http://example.com/graphql)
```

Next we define data classes corresponding to the query

```kt
data class Response(
    user: User
)

data class User(
    val id: String,
    val name: String
)
```

Finally we make the request

```kt

val query = """
   {
        user {
            id
            name
        }
   }
"""

val request = GraphQLRequest(query)

val response = client.performRequest<Response>(request)

// Retrieve the data
println(response.data?.user)

// Check for any errors
if (response.hasErrors) {
    println(response.errors)
}
```


## Examples

### Authentication via HTTP header


```kt
val client = GraphQLClient(
    endpoint = "http://example.com/graphql",
    headers = mapOf(
        "Authorization" to "Bearer TOKEN"
    )
)

val query = """
   {
        user {
            id
            name
        }
   }
"""

val request = GraphQLRequest(query)

val response = client.performRequest<Response>(request)
```

### Using variables

```kt
val client = GraphQLClient("http://example.com/graphql")

val query = ```
    query getUser($id: ID!) {
        user(id: $id) {
            id
            name
        }
    }
```

val request = GraphQLRequest(
    query = query,
    variables = mapOf(
        "id" to "1"
    )
)

val response = client.performRequest<Response>(request)
```

### Using an operation name

```kt
val client = GraphQLClient("http://example.com/graphql")

val query = ```
    query singleUser {
        user {
            id
            name
        }
    }

    query allUsers {
        users {
            id
            name
        }
    }
```

val response = client.performRequest<Response>(
        GraphQLRequest(
            query = query,
            operationName = "singleUser"
        )
    )

```
