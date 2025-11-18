package data.model

import kotlinx.serialization.Serializable
import su.kawunprint.data.model.FilamentModel

@Serializable
data class FilamentConsumptionResult(
    val order: OrderModel,
    val filament: FilamentModel
)

