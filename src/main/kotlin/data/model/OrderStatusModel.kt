package data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusModel(
    val id: Int,
    val name: String,
    val description: String
)