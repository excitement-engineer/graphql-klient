package klient.graphql.helpers

data class Person(
    val name: String,
    val age: Int,
    val school: School
)

data class School(
    val name: String,
    val address: String
)

data class Response(
    val person: Person
)

data class ResponseWithoutSchool(
    val person: PersonWithoutSchool
)

data class ErrorResponse(
    val error: String?
)

data class AuthenticatedResponse(
    val authenticatedViewer: PersonWithoutSchool
)

data class PersonWithoutSchool(
    val name: String,
    val age: Int
)

