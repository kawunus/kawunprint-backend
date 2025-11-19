package data.model.requests.order

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    val employeeId: Int?,
    val statusId: Int,
    val totalPrice: Double,
    val comment: String?,
    val completedAt: String? = null
)