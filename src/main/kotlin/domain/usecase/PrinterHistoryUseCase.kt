package domain.usecase

import data.model.PrinterHistoryModel
import domain.repository.PrinterHistoryRepository

class PrinterHistoryUseCase(private val repository: PrinterHistoryRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun getById(id: Int) = repository.getById(id)
    suspend fun create(model: PrinterHistoryModel) = repository.create(model)
    suspend fun update(model: PrinterHistoryModel) = repository.update(model)
    suspend fun delete(id: Int) = repository.delete(id)
}
