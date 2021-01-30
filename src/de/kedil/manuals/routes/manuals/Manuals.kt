package de.kedil.manuals.routes.manuals

import authenticateWithFirebase
import de.kedil.manuals.Manuals
import de.kedil.manuals.models.*
import firebaseAuth
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.eq

@OptIn(KtorExperimentalLocationsAPI::class)
@Location("/manual/{id}")
data class ManualById(val id: String)

@OptIn(KtorExperimentalLocationsAPI::class)
@Location("manuals") class ManualsRoute() {
    @Location("/own") data class Own(val parent: ManualsRoute)
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Routing.manuals() {
    authenticateWithFirebase {
        get<ManualById> {
            manualById ->

            val manual = Manuals.repositories.manualCollection.findOneById(manualById.id)
                ?: return@get context.respond(HttpStatusCode.BadRequest, mapOf("error" to "Manual not found"))

            if(!manual.private) {
                return@get context.respond(manual)
            }

            val owner = Manuals.repositories.userCollection.findOneById(manual.owner)
                ?: return@get context.respond(HttpStatusCode.Unauthorized)

            if(owner.firebaseUserId != context.firebaseAuth().token.uid) return@get context.respond(HttpStatusCode.Unauthorized)

            context.respond(manual)
        }
        get<ManualsRoute.Own> {
            val userId = context.firebaseAuth().token.uid

            val user = Manuals.repositories.userCollection.findOneById(userId)
                ?: User(userId, listOf()).also {
                    Manuals.repositories.userCollection.save(it)
                }

            val manuals = Manuals.repositories.manualCollection.find(Manual::owner eq user.firebaseUserId).toList()

            context.respond(ManualReturnSnippet(manuals = manuals))
        }
        post<ManualsRoute> {

            val snippet = try {
                context.receive<ManualCreation>()
            } catch (e: ContentTransformationException) {
                return@post context.respond(HttpStatusCode.BadRequest, mapOf("error" to "cannot transform json"))
            }

            val userId = context.firebaseAuth().token.uid

            val user = Manuals.repositories.userCollection.findOneById(userId)
                ?: User(userId, listOf()).also {
                    Manuals.repositories.userCollection.save(it)
                }

            val manual = snippet.toDatabaseManual(user)

            Manuals.repositories.manualCollection.insertOne(manual)

            context.respond(HttpStatusCode.Created, manual)
        }
    }
}