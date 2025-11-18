package data.model.requests.order

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val customerId: Int?,
    val comment: String?,
    val totalPrice: Double,
    val statusId: Int
)