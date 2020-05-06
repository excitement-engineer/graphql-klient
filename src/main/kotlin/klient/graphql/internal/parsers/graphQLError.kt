package klient.graphql.internal.parsers

import klient.graphql.GraphQLError
import klient.graphql.Location
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

internal fun ArrayNode.parseErrors(): List<GraphQLError> = map { errorObject ->
    errorObject
        .assertIsObject()
        .parseError()
}

internal fun <T>ArrayNode.parseGenericErrors(errorClass: Class<T>): List<T> = map { errorObject ->
    errorObject
            .assertIsObject()
            .parse(errorClass)
}

private fun ObjectNode.parseError(): GraphQLError {

    val message = get("message").textValue()

    val path = parsePath()

    val locations = parseLocations()

    val extensions = parseExtensions()

    return GraphQLError(
        message = message,
        locations = locations ?: emptyList(),
        path = path ?: emptyList(),
        extensions = extensions ?: emptyMap()
    )
}

private fun ObjectNode.parsePath(): List<String>? = safeGet("path")
    ?.assertIsList()
    ?.map { it.asText() }

private fun ObjectNode.parseLocations(): List<Location>? = safeGet("locations")
    ?.assertIsList()
    ?.map {
        it.assertIsObject().parse(Location::class.java)
    }

private fun ObjectNode.parseExtensions(): Map<String, Any>? = safeGet("extensions")
    ?.assertIsObject()
    ?.toMap()
