package su.kawunprint.domain.usecase

import su.kawunprint.data.model.FilamentModel
import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.domain.repository.FilamentRepository

class FilamentUseCase(private val filamentRepository: FilamentRepository) {
    suspend fun createFilament(filament: FilamentModel) = filamentRepository.createFilament(filament)

    suspend fun getAllFilaments(): List<FilamentModel> = filamentRepository.getAllFilaments()

    suspend fun getFilamentsByType(type: FilamentTypeModel): List<FilamentModel> = filamentRepository.getFilamentsByType(type)

    suspend fun updateFilament(filament: FilamentModel) = filamentRepository.updateFilament(filament)

    suspend fun deleteFilament(filament: FilamentModel) = filamentRepository.deleteFilament(filament)
}