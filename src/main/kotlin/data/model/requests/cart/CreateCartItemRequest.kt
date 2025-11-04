package data.model.requests.cart

import kotlinx.serialization.Serializable

@Serializable
data class CreateCartItemRequest(
    val filamentId: Int?,
    val filePath: String,
    val weight: Double,
    val estimatedPrice: Double,
    val comment: String?,
)