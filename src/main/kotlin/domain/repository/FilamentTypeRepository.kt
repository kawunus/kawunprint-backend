package su.kawunprint.domain.repository

import su.kawunprint.data.model.FilamentTypeModel

interface FilamentTypeRepository {

    suspend fun getAllFilamentTypes(): List<FilamentTypeModel>

    suspend fun createFilamentType(type: FilamentTypeModel)

    suspend fun deleteFilamentType(type: FilamentTypeModel)

    suspend fun updateFilamentType(type: FilamentTypeModel)

    suspend fun getFilamentTypeById(id: Int): FilamentTypeModel?
}