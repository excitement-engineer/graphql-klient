package klient.graphql.internal.parsers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue

internal fun String.toJson(): JsonNode {
    return jacksonObjectMapper().readTree(this)
}

internal fun JsonNode.assertIsObject(): ObjectNode {

    if (!isObject) {
        throw Exception("Expected JSON object but got ${this}")
    } else {
        return this as ObjectNode
    }
}

internal fun JsonNode.assertIsList(): ArrayNode {
    if (!isArray) {
        throw Exception("Expected JSON object but got ${this}")
    } else {
        return this as ArrayNode
    }
}

internal fun ObjectNode.toMap(): Map<String, Any> {
    val map = jacksonObjectMapper().treeToValue<Map<String, Any>>(this)

    return map
}


internal fun <T>JsonNode.parse(responseClass: Class<T>): T {

    val mapper = jacksonObjectMapper()

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)

    val response = mapper.readValue(this.toString(), responseClass)

    return response
}

internal fun ObjectNode.safeGet(name: String): JsonNode? =
    if (this.has(name)) {
        val got = this.get(name)
        if (got.isNull) null else got
    } else {
        null
    }

