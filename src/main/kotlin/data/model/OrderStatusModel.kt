package data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusModel(
    val id: Int = 0,
    val description: String,
)
