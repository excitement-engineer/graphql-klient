package klient.graphql.internal.http

internal data class HTTPResponse(
    val response: String?,
    val statusCode: Int
)

internal data class ValidHTTPResponse(
    val response: String
)