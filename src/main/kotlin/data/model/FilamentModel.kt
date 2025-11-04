package su.kawunprint.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FilamentModel(
    val id: Int = 0,
    val color: String,
    val type: FilamentTypeModel,
    val pricePerGram: Double,
    val residue: Int,
    val hexColor: String,
)
