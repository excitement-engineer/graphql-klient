package klient.graphql.internal.parsers

import com.fasterxml.jackson.databind.node.ObjectNode
import klient.graphql.GenericGraphQLResponse
import klient.graphql.internal.http.HTTPResponse
import klient.graphql.internal.http.assertIsValid
import klient.graphql.GraphQLResponse

@PublishedApi
internal inline fun <reified Data> parseResponse(httpResponse: HTTPResponse) =
    parseResponse(httpResponse, Data::class.java)

@PublishedApi
internal inline fun <reified Data, reified Error> parseGenericResponse(httpResponse: HTTPResponse) =
        parseGenericResponse(httpResponse, Data::class.java, Error::class.java)


@PublishedApi
internal fun <Data> parseResponse(httpResponse: HTTPResponse, responseClass: Class<Data>): GraphQLResponse<Data> {

    val jsonResponse = parseJsonResponse(httpResponse)

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

@PublishedApi
internal fun <Data, Error> parseGenericResponse(httpResponse: HTTPResponse, responseClass: Class<Data>, errorClass: Class<Error>): GenericGraphQLResponse<Data, Error> {

    val jsonResponse = parseJsonResponse(httpResponse)

    val data = jsonResponse
            .safeGet("data")
            ?.assertIsObject()
            ?.parse(responseClass)

    val errors = jsonResponse
            .safeGet("errors")
            ?.assertIsList()
            ?.parseGenericErrors(errorClass)

    return GenericGraphQLResponse(
            data = data,
            errors = errors ?: emptyList()
    )
}

private fun parseJsonResponse(httpResponse: HTTPResponse): ObjectNode {
    val validResponse = httpResponse.assertIsValid()

    val (response) = validResponse

    val jsonResponse = response
            .toJson()
            .assertIsObject()
    return jsonResponse
}