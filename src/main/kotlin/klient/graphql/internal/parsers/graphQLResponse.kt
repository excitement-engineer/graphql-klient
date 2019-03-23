package klient.graphql.internal.parsers

import klient.graphql.internal.http.HTTPResponse
import klient.graphql.internal.http.assertIsValid
import klient.graphql.GraphQLResponse

@PublishedApi
internal inline fun <reified Data> parseResponse(httpResponse: HTTPResponse) =
    parseResponse(httpResponse, Data::class.java)

@PublishedApi
internal fun <Data> parseResponse(httpResponse: HTTPResponse, responseClass: Class<Data>): GraphQLResponse<Data> {

    val validResponse = httpResponse.assertIsValid()

    val (response) = validResponse

    val jsonResponse = response
        .toJson()
        .assertIsObject()

    val data = jsonResponse
        .safeGet("data")
        ?.assertIsObject()
        ?.parse(responseClass)

    val errors = jsonResponse
        .safeGet("errors")
        ?.assertIsList()
        ?.parseErrors()

    return GraphQLResponse(
        data = data,
        errors = errors ?: emptyList()
    )
}