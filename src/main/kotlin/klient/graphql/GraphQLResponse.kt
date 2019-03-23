package klient.graphql

data class GraphQLResponse<Data>(
    val data: Data? = null,
    val errors: List<GraphQLError> = emptyList()
) {
    val hasErrors = errors.isNotEmpty()
}