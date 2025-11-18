package su.kawunprint.domain.repository

import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.FilamentTypeModel

interface FilamentRepository {
    suspend fun createFilament(filament: FilamentModel)

    suspend fun getAllFilaments(): List<FilamentModel>

    suspend fun getFilamentsByType(type: FilamentTypeModel): List<FilamentModel>

    suspend fun updateFilament(filament: FilamentModel)

    suspend fun deleteFilament(filament: FilamentModel)

    suspend fun getFilamentById(id: Int): FilamentModel?

    // Consume grams of filament for an order in a single transaction. Returns Pair(updatedOrder, updatedFilament)
    suspend fun consumeFilamentForOrder(
        orderId: Int,
        filamentId: Int,
        gramsUsed: Int,
        employeeId: Int?,
        comment: String?
    ): Pair<Int, Int>
}