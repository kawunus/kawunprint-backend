package domain.repository

import data.model.OrderHistoryModel

interface OrderHistoryRepository {
    suspend fun addHistoryEntry(entry: OrderHistoryModel): OrderHistoryModel?
    suspend fun getHistoryForOrder(orderId: Int): List<OrderHistoryModel>
}