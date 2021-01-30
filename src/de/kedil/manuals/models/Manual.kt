package de.kedil.manuals.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class Manual(
    @SerialName("_id") val manualId: Id<Manual> = newId(),
    val title: String,
    val explanation: String,
    val private: Boolean,
    @SerialName("image_url") val imageUrl: String,
    val steps: List<Step>,
    val owner: String,
)

fun List<Manual>.asCompactManuals() = map {
    CompactManual(
        manualId = it.manualId,
        title = it.title,
        explanation = it.explanation,
        private = it.private,
        imageUrl = it.imageUrl,
        owner = it.owner
    )
}

@Serializable
data class CompactManual(
    @SerialName("_id") val manualId: Id<Manual> = newId(),
    val title: String,
    val explanation: String,
    val private: Boolean,
    @SerialName("image_url") val imageUrl: String,
    val owner: String
)

fun ManualCreation.toDatabaseManual(user: User) = Manual(
    title = title,
    explanation = explanation,
    private = private,
    imageUrl = imageUrl,
    owner = user.firebaseUserId,
    steps = steps.map(StepSnippet::toDatabaseStep)
)

@Serializable
data class ManualCreation(
    val title: String,
    val explanation: String,
    val private: Boolean,
    @SerialName("image_url") val imageUrl: String,
    val steps: List<StepSnippet>,
)

data class ManualReturnSnippet(
    val manuals: List<Manual>
)