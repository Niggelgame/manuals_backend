package de.kedil.manuals

import de.kedil.manuals.config.Config
import de.kedil.manuals.models.Manual
import de.kedil.manuals.models.User
import de.kedil.manuals.routes.manuals.manuals
import de.kedil.manuals.routes.profile.profile
import de.kedil.manuals.util.errorHandler
import firebase
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


object Manuals {
    private val json = Json {
    }

    class Repositories {
        val userCollection: CoroutineCollection<User> = database.getCollection("users")
        val manualCollection: CoroutineCollection<Manual> = database.getCollection("manuals")
    }

    lateinit var repositories: Repositories
    private lateinit var client: CoroutineClient
    lateinit var database: CoroutineDatabase

    fun initializeDb() {
        client = KMongo.createClient().coroutine
        database = client.getDatabase(Config.databaseName)
        repositories = Repositories()
    }

    @Suppress("unused") // Referenced in application.conf
    @kotlin.jvm.JvmOverloads
    fun Application.module(testing: Boolean = false) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Authentication) {
            firebase()
        }

        install(Locations)

        // To catch/handle Exceptions, especially white FirebaseAuth, if principle is null
        errorHandler()

        routing {
            profile()
            manuals()
        }
    }
}