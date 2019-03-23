package klient.graphql

import klient.graphql.helpers.*
import klient.graphql.helpers.Response
import klient.graphql.helpers.server
import klient.graphql.helpers.serverPort
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClientTest {

    companion object {

        @BeforeClass
        @JvmStatic fun setup() {
            server.start()
        }

        @AfterClass
        @JvmStatic fun teardown() {
            server.stop(0, 0, TimeUnit.SECONDS)
        }
    }

    private val client = GraphQLClient(
        endpoint = "http://localhost:$serverPort/graphql",
        headers = mapOf(
            "Authorization" to "abc123"
        )
    )

    @Test
    fun `returns the expected response`() {
        val response = client.performRequest<Response>(
            GraphQLRequest(
                query = """ {
                        person {
                             name
                             age
                             school {
                                name
                                address
                             }
                        }

                    } """.trimIndent()
            )
        )

        assertEquals(
            expected = GraphQLResponse(
                data = Response(
                    person = Person(
                        name = "John",
                        age = 18,
                        school = School(
                            name = "elementary",
                            address = "Main street"
                        )
                    )
                )
            ),
            actual = response
        )

        assertFalse {
            response.hasErrors
        }
    }

    @Test
    fun `allows for setting variables`() {
        val response = client.performRequest<ResponseWithoutSchool>(
            GraphQLRequest(
                query = """
                        query getPerson(${"$"}name: String) {
                            person(name: ${"$"}name) {
                                 name
                                 age
                            }
                        }
                    """.trimIndent(),
                variables = mapOf(
                    "name" to "Bob"
                )
            )
        )
        assertEquals(
            expected = GraphQLResponse(
                data = ResponseWithoutSchool(
                    person = PersonWithoutSchool(
                        name = "Bob",
                        age = 19
                    )
                )
            ),
            actual = response
        )
    }

    @Test
    fun `supports operation names`() {

        val response = client.performRequest<ResponseWithoutSchool>(
            GraphQLRequest(
                query = """
                        query Person1 {
                            ...PersonWithoutSchool
                        }

                        query Person2 {
                            ...PersonWithoutSchool
                        }

                        fragment PersonWithoutSchool on Query {
                            person {
                                name
                                age
                            }
                        }
                     """.trimIndent(),
                operationName = "Person2"
            )

        )

        assertEquals(
            expected = GraphQLResponse(
                data = ResponseWithoutSchool(
                    person = PersonWithoutSchool(
                        name = "John",
                        age = 18
                    )
                )
            ),
            actual = response
        )

    }

    @Test
    fun `allows for setting custom headers`() {

        val response = client.performRequest<AuthenticatedResponse>(
            GraphQLRequest(
                query = """
                       {
                        authenticatedViewer {
                            name
                            age
                        }
                       }
                     """.trimIndent()
            )

        )

        assertEquals(
            expected = GraphQLResponse(
                data = AuthenticatedResponse(
                    authenticatedViewer = PersonWithoutSchool(
                        name = "Albert",
                        age = 22
                    )
                )
            ),
            actual = response
        )
    }

    @Test
    fun `errors if the response structure is not the same as the object to which it should be mapped`() {

        val error = assertFails {
            client.performRequest<Response>(
                GraphQLRequest(
                    query = """ {
                        person {
                             name
                             age
                        }

                    } """.trimIndent()
                )
            )
        }

        val expected =
            "value failed for JSON property school"
        assertTrue(
            actual = error.message!!.contains(expected),
            message = "Expected ${error.message} to contain $expected"
        )

    }

    @Test
    fun `does not error if the response has more properties than the response class`() {

        val response = client.performRequest<Response>(
            GraphQLRequest(
                query = """ {
                        person {
                             name
                             age
                             school {
                                name
                                address
                             }
                        }

                    } """.trimIndent()
            )
        )

        assertEquals(
            expected = GraphQLResponse(
                data = Response(
                    person = Person(
                        name = "John",
                        age = 18,
                        school = School(
                            name = "elementary",
                            address = "Main street"
                        )
                    )
                )
            ),
            actual = response
        )
    }

    @Test
    fun `parses an error`() {

        val response = client.performRequest<ErrorResponse>(
            GraphQLRequest(
                query = """
                    {
                        error
                    }
                """.trimIndent()
            )
        )

        assertEquals(
            expected = GraphQLResponse(
                data = ErrorResponse(null),
                errors = listOf(
                    GraphQLError(
                        message = "Exception while fetching data (/error) : Something went wrong",
                        locations = listOf(
                            Location(
                                line = 2,
                                column = 5
                            )
                        ),
                        path = listOf("error"),
                        extensions = mapOf(
                            "reason" to "unknown"
                        )
                    )
                )
            ),
            actual = response
        )


        assertTrue {
            response.hasErrors
        }
    }

    @Test
    fun `throws an exception if the status code is not 400`() {

        val clientWrong = GraphQLClient(
            endpoint = "http://localhost:$serverPort/wrong"
        )

        val error = assertFails {
            clientWrong.performRequest<Response>(
                GraphQLRequest(
                    query = """{
                             person {
                                name
                                age
                            }
                        """.trimIndent()
                )
            )
        }

        assertEquals(
            expected = HTTPException(
                statusCode = 404,
                message = null
            ),
            actual = error
        )
    }

    @Test
    fun `throws an exception if the status code is 500`() {
        val clientWrong = GraphQLClient(
            endpoint = "http://localhost:$serverPort/error"
        )

        val error = assertFails {
            clientWrong.performRequest<Response>(
                GraphQLRequest(
                    query = """{
                             person {
                                name
                                age
                            }
                        """.trimIndent()
                )
            )
        }

        assertEquals(
            expected = HTTPException(
                statusCode = 500,
                message = "Something went wrong"
            ),
            actual = error
        )
    }

}