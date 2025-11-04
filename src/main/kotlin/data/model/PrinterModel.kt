package data.model

import kotlinx.serialization.Serializable

@Serializable
data class PrinterModel(
    val id: Int = 0,
    val name: String,
    val isMulticolor: Boolean,
    val isActive: Boolean,
    val description: String? = null
)
