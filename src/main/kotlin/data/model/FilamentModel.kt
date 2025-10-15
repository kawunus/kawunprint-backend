package su.kawunprint.data.model

import io.ktor.server.auth.Principal

data class FilamentModel(
    val id: Int,
    val color: String,
    val type: FilamentTypeModel,
    val pricePerGram: Int,
): Principal
