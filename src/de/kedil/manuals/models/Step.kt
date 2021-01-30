package de.kedil.manuals.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class Step(
    @SerialName("_id") val stepId: Id<Step> = newId(),
    val title: String,
    val explanation: String,
    @SerialName("image_url") val imageUrl: String
)

@Serializable
data class StepSnippet(
    val title: String,
    val explanation: String,
    @SerialName("image_url") val imageUrl: String
)

fun StepSnippet.toDatabaseStep() = Step(title = title, explanation = explanation, imageUrl = imageUrl)