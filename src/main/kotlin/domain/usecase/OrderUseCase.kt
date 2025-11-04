package domain.usecase

import data.model.OrderModel
import domain.repository.OrderRepository

class OrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun createOrder(order: OrderModel): OrderModel? = orderRepository.createOrder(order)
    suspend fun getAllOrders(): List<OrderModel> = orderRepository.getAllOrders()
    suspend fun getOrdersByCustomerId(customerId: Int): List<OrderModel> =
        orderRepository.getOrdersByCustomerId(customerId)

    suspend fun getOrderById(id: Int): OrderModel? = orderRepository.getOrderById(id)
    suspend fun updateOrder(order: OrderModel) = orderRepository.updateOrder(order)
    suspend fun deleteOrder(id: Int) = orderRepository.deleteOrder(id)
}