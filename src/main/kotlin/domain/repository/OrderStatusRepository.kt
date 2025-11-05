package domain.repository

import data.model.OrderStatusModel

interface OrderStatusRepository {
    suspend fun getAll(): List<OrderStatusModel>
    suspend fun getById(id: Int): OrderStatusModel?
    suspend fun create(status: OrderStatusModel): OrderStatusModel?
    suspend fun update(status: OrderStatusModel)
    suspend fun delete(id: Int)
}
