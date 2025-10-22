package domain.repository

import data.model.OrderModel


interface OrderRepository {
    suspend fun createOrder(order: OrderModel): OrderModel?
    suspend fun getAllOrders(): List<OrderModel>
    suspend fun getOrdersByCustomerId(customerId: Int): List<OrderModel>
    suspend fun getOrderById(id: Int): OrderModel?
    suspend fun updateOrder(order: OrderModel)
    suspend fun deleteOrder(id: Int)
}