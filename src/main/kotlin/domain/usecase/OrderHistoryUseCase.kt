package domain.usecase

import data.model.OrderHistoryModel
import domain.repository.OrderHistoryRepository

class OrderHistoryUseCase(private val orderHistoryRepository: OrderHistoryRepository) {
    suspend fun addHistoryEntry(entry: OrderHistoryModel): OrderHistoryModel? =
        orderHistoryRepository.addHistoryEntry(entry)

    suspend fun getHistoryForOrder(orderId: Int): List<OrderHistoryModel> =
        orderHistoryRepository.getHistoryForOrder(orderId)
}