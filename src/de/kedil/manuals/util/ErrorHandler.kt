package de.kedil.manuals.util

import AuthenticationFailedException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.lang.RuntimeException

class BadRequestException(val reason: String) : RuntimeException(reason)

fun Application.errorHandler() {
    install(StatusPages) {
        auth()
        badRequest()
    }
}

private fun StatusPages.Configuration.auth() {
    exception<AuthenticationFailedException> {
        authenticationFailedException ->

        context.respondError(Error(HttpStatusCode.Unauthorized, authenticationFailedException.reason.toString()))
    }
}

private fun StatusPages.Configuration.badRequest() {
    exception<BadRequestException> {
        badRequestException ->
        context.respondError(Error(HttpStatusCode.BadRequest, badRequestException.reason))
    }
    exception<SerializationException> {
        serializationException ->
        context.respondError(Error(HttpStatusCode.BadRequest, serializationException.toString()))
    }
}

suspend fun ApplicationCall.respondError(error: Error): Unit = respond(HttpStatusCode.fromValue(error.code), error)

@Serializable
data class Error internal  constructor(val code: Int, val description: String, val hint: String? = null) {
    constructor(httpCode: HttpStatusCode, hint: String? = null) : this(httpCode.value, httpCode.description, hint)
}
