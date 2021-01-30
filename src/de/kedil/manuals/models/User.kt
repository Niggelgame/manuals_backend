package de.kedil.manuals.models

import de.kedil.manuals.Manuals
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.eq

@Serializable
data class User(
    @SerialName("_id") val firebaseUserId: String,
    val ownManuals: List<Id<Manual>>
)

suspend fun User.toFullUser(): FullUser {
    val manuals = Manuals.repositories.manualCollection.find(Manual::manualId `in` ownManuals).toList()
    return FullUser(firebaseUserId = firebaseUserId, ownManuals = manuals)
}

suspend fun User.toPublicUser() : FullUser{
    val manuals = Manuals.repositories.manualCollection.find(and(Manual::manualId `in` ownManuals,Manual::private eq false)).toList()
    return FullUser(firebaseUserId = firebaseUserId, ownManuals = manuals)
}

@Serializable
data class FullUser(
    @SerialName("_id") val firebaseUserId: String,
    val ownManuals: List<Manual>
)

fun FullUser.toCompactUser() = CompactUser(
    firebaseUserId = firebaseUserId,
    ownManuals = ownManuals.asCompactManuals()
)


@Serializable
data class CompactUser(
    @SerialName("_id") val firebaseUserId: String,
    val ownManuals: List<CompactManual>
)