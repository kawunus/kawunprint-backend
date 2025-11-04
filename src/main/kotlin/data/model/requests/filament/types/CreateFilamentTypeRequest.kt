package su.kawunprint.data.model.requests.filament.types

import kotlinx.serialization.Serializable

@Serializable
data class CreateFilamentTypeRequest(
    val name: String,
    val description: String
)