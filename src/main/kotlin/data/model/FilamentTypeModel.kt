package su.kawunprint.data.model

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class FilamentTypeModel(
    val id: Int = 0,
    val name: String,
    val description: String,
): Principal