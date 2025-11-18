package data.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class ConsumeFilamentResponse(
    val success: Boolean,
    val order: OrderResponse,
    val filament: FilamentResponse
)

@Serializable
data class OrderResponse(
    val id: Int,
    val statusId: Int,
    val totalPrice: Double
    // add other fields as needed
)

@Serializable
data class FilamentResponse(
    val id: Int,
    val residue: Double,
    val pricePerGram: Double,
    val color: String,
    val type: FilamentTypeSimple?
)

@Serializable
data class FilamentTypeSimple(
    val id: Int,
    val name: String,
    val description: String
)

