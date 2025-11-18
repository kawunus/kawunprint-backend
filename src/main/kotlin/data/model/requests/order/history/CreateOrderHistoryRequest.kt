package data.model.requests.order.history

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderHistoryRequest(
    val statusId: Int,
    val comment: String,
)