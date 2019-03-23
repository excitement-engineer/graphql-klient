package klient.graphql

data class GraphQLError(
    val message: String,
    val locations: List<Location> = emptyList(),
    val path: List<String> = emptyList(),
    val extensions: Map<String, Any> = emptyMap()
)

