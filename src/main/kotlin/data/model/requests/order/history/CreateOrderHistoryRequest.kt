package data.model.requests.order.history

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderHistoryRequest(
    val status: String?,
    val comment: String,
)