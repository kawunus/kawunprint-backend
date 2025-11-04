package su.kawunprint.data.model.requests.filament.types

import kotlinx.serialization.Serializable

@Serializable
class UpdateFilamentTypeRequest(
    val name: String,
    val description: String,
)