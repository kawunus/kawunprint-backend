package data.model.requests.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSelfUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)

