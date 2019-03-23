package klient.graphql

import klient.graphql.internal.http.performHttpRequest
import klient.graphql.internal.parsers.parseResponse
import klient.graphql.internal.serializers.serialize

data class GraphQLClient(
    val endpoint: String,
    val headers: Map<String, String> = emptyMap()
) {
    inline fun <reified Data> performRequest(request: GraphQLRequest): GraphQLResponse<Data> {

        val requestBody = request.serialize()

        val httpResponse = performHttpRequest(
            url = endpoint,
            headers = headers,
            requestBody = requestBody
        )

        return parseResponse(httpResponse)
    }
}



