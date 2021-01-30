
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.auth.*
import io.ktor.routing.*

private const val name = "firebase_auth_routing_provider_thing"

class FirebaseAuthJwtPrincipal(val token: FirebaseToken) : Principal

class AuthenticationFailedException(val reason: AuthenticationFailedCause): RuntimeException(reason.toString())

class FirebaseAuthProvider(config: Configuration) : AuthenticationProvider(config) {
    class Configuration(name: String) : AuthenticationProvider.Configuration(name)
}

fun ApplicationCall.firebaseAuth(): FirebaseAuthJwtPrincipal {
    if (authentication.allFailures.isNotEmpty()) {
        throw AuthenticationFailedException(authentication.allFailures.first())
    }
    return principal()!!
}

fun Authentication.Configuration.firebase() {
    val provider = FirebaseAuthProvider(FirebaseAuthProvider.Configuration(name))

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { authenticationContext ->
        val header: HttpAuthHeader = context.request.parseAuthorizationHeader()
            ?: return@intercept authenticationContext.error("Missing Header", AuthenticationFailedCause.NoCredentials)

        if(header !is HttpAuthHeader.Single ||header.authScheme != "Bearer") return@intercept authenticationContext.error("Invalid Header", AuthenticationFailedCause.InvalidCredentials)

        val token = header.blob

        val firebaseToken = try {
            FirebaseAuth.getInstance().verifyIdToken(token)
        } catch (e: FirebaseAuthException) {
            return@intercept authenticationContext.error("Token could not get verified", AuthenticationFailedCause.InvalidCredentials)
        }

        authenticationContext.principal(FirebaseAuthJwtPrincipal(firebaseToken))
    }

    register(provider)
}

fun Route.authenticateWithFirebase(block: Route.() -> Unit) {
    authenticate(name, build = block)
}