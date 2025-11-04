package domain.usecase

import data.model.CartModel
import domain.repository.CartRepository

class CartUseCase(private val cartRepository: CartRepository) {
    suspend fun createCartItem(item: CartModel): CartModel? = cartRepository.createCartItem(item)
    suspend fun getCartItemsByUserId(userId: Int): List<CartModel> = cartRepository.getCartItemsByUserId(userId)
    suspend fun getCartItemById(id: Int): CartModel? = cartRepository.getCartItemById(id)
    suspend fun updateCartItem(item: CartModel) = cartRepository.updateCartItem(item)
    suspend fun deleteCartItem(id: Int) = cartRepository.deleteCartItem(id)
}