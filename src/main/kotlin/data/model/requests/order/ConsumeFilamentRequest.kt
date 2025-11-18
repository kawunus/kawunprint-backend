package data.model.requests.order

import kotlinx.serialization.Serializable

@Serializable
data class ConsumeFilamentRequest(
    val filamentId: Int,
    val gramsUsed: Int,
    val comment: String? = null
)

