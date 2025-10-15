package su.kawunprint.data.model

import io.ktor.server.auth.Principal

data class FilamentTypeModel(
    val id: Int,
    val name: String,
    val description: String,
): Principal