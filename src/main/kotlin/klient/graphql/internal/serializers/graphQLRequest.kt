package klient.graphql.internal.serializers

import klient.graphql.GraphQLRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@PublishedApi
internal fun GraphQLRequest.serialize(): String {

    val requestBody = mutableMapOf<String, Any>()

    requestBody["query"] = query

    if (variables != null) {
        requestBody["variables"] = variables
    }

    if (operationName != null) {
        requestBody["operationName"] = operationName
    }

    return requestBody.toJson()
}

private fun Map<String, Any>.toJson() = jacksonObjectMapper().writeValueAsString(this)


