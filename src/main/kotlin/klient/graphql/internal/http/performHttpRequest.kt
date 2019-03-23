package klient.graphql.internal.http

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@PublishedApi
internal fun performHttpRequest(url: String, headers: Map<String, String>, requestBody: String): HTTPResponse {
    val connection = createConnection(
        url = url,
        headers = headers,
        requestBody = requestBody
    )

    return connection.getResponse()
}

private fun createConnection(
    url: String,
    headers: Map<String, String>,
    requestBody: String
): HttpURLConnection {

    val con = URL(url).openConnection() as HttpURLConnection

    con.requestMethod = "POST"

    con.doOutput = true
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("Accept", "application/json");
    headers.forEach { header, value -> con.setRequestProperty(header, value) }

    val os = con.outputStream

    os.write(requestBody.toByteArray(Charset.forName("UTF-8")))

    os.close()

    return con
}

private fun HttpURLConnection.getResponse(): HTTPResponse {

    val response = if (responseInputStream != null) {
        val `in` = BufferedReader(
            InputStreamReader(responseInputStream)
        )
        val content = StringBuffer()

        var inputLine = `in`.readLine()

        while (inputLine != null) {
            content.append(inputLine)

            inputLine = `in`.readLine()
        }
        `in`.close()

        val response = content.toString()

        response
    } else {
        null
    }

    return HTTPResponse(
        response = response,
        statusCode = responseCode
    )
}

private val HttpURLConnection.responseInputStream: InputStream?
    get() {
        return if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            inputStream
        } else {
            errorStream
        }
    }