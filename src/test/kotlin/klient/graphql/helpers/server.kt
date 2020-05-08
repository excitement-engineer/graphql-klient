package klient.graphql.helpers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import ktor.graphql.config
import ktor.graphql.graphQL
import java.net.ServerSocket

val serverPort: Int = ServerSocket(0).use { it.localPort }

val server: ApplicationEngine = embeddedServer(Jetty, serverPort) {
    routing {
        graphQL("/graphql", schema) {
            config {
                context = Context(
                    accessToken = call.request.header("Authorization")
                )

                formatError = {

                    val error = toSpecification()

                    error["extensions"] = mapOf(
                        "reason" to "unknown"
                    )

                    error
                }
            }
        }

        graphQL("/graphql-generic", schema) {
            config {
                context = Context(
                        accessToken = call.request.header("Authorization")
                )

                formatError = {

                    val error = toSpecification()

                    error["extensions"] = mapOf(
                            "reason" to "unknown"
                    )
                    error["customMessage"] = "customMessage"
                    error["test"] = "test"

                    error
                }
            }
        }

        route("error") {
            post {
                call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
            }
        }

        route("error-no-data") {
            post {
                call.respond(HttpStatusCode.OK, """
                       {
                           "errors":[
                              {
                                 "message":"Invalid Syntax"
                              }
                           ],
                           "data":null,
                           "extensions":null,
                           "dataPresent":false
                        }
                """)
            }
        }
    }
}