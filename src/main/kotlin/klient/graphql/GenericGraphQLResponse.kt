package klient.graphql

data class GenericGraphQLResponse<Data, Error> (
    val data: Data? = null,
    val errors: List<Error> = emptyList()
) {
    val hasErrors = errors.isNotEmpty()

}