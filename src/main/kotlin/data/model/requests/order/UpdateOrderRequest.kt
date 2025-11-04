package data.model.requests.order

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    val employeeId: Int?,
    val status: String,
    val totalPrice: Double,
    val comment: String?,
)