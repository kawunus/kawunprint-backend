package data.model

data class PrinterModel(
    val id: Int = 0,
    val name: String,
    val isMulticolor: Boolean,
    val isActive: Boolean,
    val description: String? = null
)
