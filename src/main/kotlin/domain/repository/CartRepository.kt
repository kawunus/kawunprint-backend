package domain.repository

import data.model.CartModel

interface CartRepository {
    suspend fun createCartItem(item: CartModel): CartModel?
    suspend fun getCartItemsByUserId(userId: Int): List<CartModel>
    suspend fun getCartItemById(id: Int): CartModel?
    suspend fun updateCartItem(item: CartModel)
    suspend fun deleteCartItem(id: Int)
}