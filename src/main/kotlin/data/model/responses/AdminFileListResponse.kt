package data.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class AdminFileListResponse(
    val success: Boolean,
    val prefix: String,
    val count: Int,
    val files: List<AdminFileInfo>
)

@Serializable
data class AdminFileInfo(
    val name: String,
    val size: Long? = null,
    val url: String? = null
)

