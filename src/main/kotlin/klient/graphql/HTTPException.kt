package klient.graphql

data class HTTPException(
    val statusCode: Int,
    override val message: String?
): Exception()