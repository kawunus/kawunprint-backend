package domain.usecase

import data.model.OrderStatusModel
import domain.repository.OrderStatusRepository

class OrderStatusUseCase(private val repository: OrderStatusRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun getById(id: Int) = repository.getById(id)
    suspend fun create(model: OrderStatusModel) = repository.create(model)
    suspend fun update(model: OrderStatusModel) = repository.update(model)
    suspend fun delete(id: Int) = repository.delete(id)
}
