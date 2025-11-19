package domain.usecase

import data.model.OrderModel
import domain.repository.OrderFileRepository
import domain.repository.OrderRepository
import su.kawunprint.services.FirebaseStorageService

class OrderUseCase(
    private val orderRepository: OrderRepository,
    private val orderFileRepository: OrderFileRepository,
    private val firebaseStorageService: FirebaseStorageService
) {
    suspend fun createOrder(order: OrderModel): OrderModel? = orderRepository.createOrder(order)
    suspend fun getAllOrders(): List<OrderModel> = orderRepository.getAllOrders()
    suspend fun getOrdersByCustomerId(customerId: Int): List<OrderModel> =
        orderRepository.getOrdersByCustomerId(customerId)

    suspend fun getOrderById(id: Int): OrderModel? = orderRepository.getOrderById(id)
    suspend fun updateOrder(order: OrderModel) = orderRepository.updateOrder(order)

    suspend fun deleteOrder(id: Int) {
        // Delete all files from Firebase Storage before deleting order
        val files = orderFileRepository.getFilesByOrderId(id)
        files.forEach { file ->
            firebaseStorageService.deleteFile(file.firebaseStoragePath)
        }

        // Database cascade will handle order_files table deletion
        orderRepository.deleteOrder(id)
    }
}