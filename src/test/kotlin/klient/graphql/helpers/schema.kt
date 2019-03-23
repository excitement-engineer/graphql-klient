package klient.graphql.helpers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.SchemaParser
import graphql.schema.DataFetchingEnvironment

private val schemaDef = """

type School {
    name: String!
    address: String!
}

type Person {
    name: String!
    age: Int!
    school: School!
}

type Query {
    person(name: String): Person
    authenticatedViewer: Person
    error: String
}
"""

internal val schema = SchemaParser
    .newParser()
    .schemaString(schemaDef)
    .resolvers(Query())
    .build()
    .makeExecutableSchema()

internal data class Context(
    val accessToken: String?
)

class Query : GraphQLQueryResolver {
    fun person(name: String?) = if (name == null) {
        Person(
            name = "John",
            age = 18,
            school = School(
                name = "elementary",
                address = "Main street"
            )
        )
    } else {
        Person(
            name = name,
            age = 19,
            school = School(
                name = "basic",
                address = "Side street"
            )
        )
    }

    fun authenticatedViewer(env: DataFetchingEnvironment): Person? {

        val accessToken = env.getContext<Context>().accessToken

        return if (accessToken == null) {
            null
        } else {
            if (accessToken == "abc123") {
                Person(
                    name = "Albert",
                    age = 22,
                    school = School(
                        name = "elementary",
                        address = "Main street"
                    )
                )
            } else {
                throw Exception("Invalid access token $accessToken")
            }
        }
    }

    fun error(): String = throw Exception("Something went wrong")
}