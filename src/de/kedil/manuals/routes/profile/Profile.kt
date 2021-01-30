package de.kedil.manuals.routes.profile

import authenticateWithFirebase
import de.kedil.manuals.Manuals
import de.kedil.manuals.models.*
import firebaseAuth
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@OptIn(KtorExperimentalLocationsAPI::class)
@Location("/profile")
class Profile() {
    @Location("/{userId}")
    data class User(val parent: Profile, val userId: Int)
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Routing.profile() {
    authenticateWithFirebase {
        get<Profile> {
            val userId = context.firebaseAuth().token.uid

            val user = Manuals.repositories.userCollection.findOneById(userId)
                ?: User(userId, listOf()).also {
                    Manuals.repositories.userCollection.save(it)
                }

            context.respond(user.toFullUser().toCompactUser())
        }
    }
    get<Profile.User> {
            requestedUser ->

        val user = Manuals.repositories.userCollection.findOneById(requestedUser.userId)
            ?: return@get context.respond(HttpStatusCode.BadRequest, mapOf("error" to "User not found"))

        context.respond(user.toPublicUser().toCompactUser())
    }
}