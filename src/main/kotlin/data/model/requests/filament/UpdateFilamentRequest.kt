package data.model.requests.filament

import kotlinx.serialization.Serializable

@Serializable
data class UpdateFilamentRequest(
    val color: String,
    val typeId: Int,
    val pricePerGram: Int
)