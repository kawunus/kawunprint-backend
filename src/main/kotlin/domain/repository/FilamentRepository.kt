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
}