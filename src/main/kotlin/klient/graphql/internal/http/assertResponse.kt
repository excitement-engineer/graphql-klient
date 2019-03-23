package klient.graphql.internal.http

import klient.graphql.HTTPException

internal fun HTTPResponse.assertIsValid(): ValidHTTPResponse {
    if (!statusCode.validStatusCode() || response == null) {
        throw HTTPException(
            statusCode = statusCode,
            message = response
        )
    } else {
        return ValidHTTPResponse(response)
    }
}

private fun Int.validStatusCode(): Boolean = this in 200..299