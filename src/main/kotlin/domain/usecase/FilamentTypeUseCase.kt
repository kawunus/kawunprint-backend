package su.kawunprint.domain.usecase

import su.kawunprint.data.model.FilamentTypeModel
import su.kawunprint.domain.repository.FilamentTypeRepository

class FilamentTypeUseCase(private val repository: FilamentTypeRepository) {

    suspend fun getAllFilamentTypes(): List<FilamentTypeModel> = repository.getAllFilamentTypes()

    suspend fun createFilamentType(type: FilamentTypeModel) = repository.createFilamentType(type)

    suspend fun deleteFilamentType(type: FilamentTypeModel) = repository.deleteFilamentType(type)

    suspend fun updateFilamentType(type: FilamentTypeModel) = repository.updateFilamentType(type)

    suspend fun getFilamentTypeById(id: Int): FilamentTypeModel? = repository.getFilamentTypeById(id)
}