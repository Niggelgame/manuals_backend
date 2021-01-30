package de.kedil.manuals

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.nio.file.Files
import java.nio.file.Path

fun main(args: Array<String>) {
    initializeFirebase()
    Manuals.initializeDb()
    io.ktor.server.netty.EngineMain.main(args)
}

private fun initializeFirebase() {
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(Files.newInputStream(Path.of("firebase-admin.json"))))
        .build()

    FirebaseApp.initializeApp(options)
}
