package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.cart.CartItem
import app.maul.koperasi.model.cart.CartListResponse
import app.maul.koperasi.model.cart.CartQuantityRequest
import app.maul.koperasi.model.cart.CartRequest
import app.maul.koperasi.model.cart.CartResponse
import retrofit2.Response
import javax.inject.Inject

class CartRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun addCart(cartRequest: CartRequest): CartResponse {
        return apiService.addCart(cartRequest)
    }

    suspend fun getUserCart(userId: Int): CartListResponse {
        return apiService.getUserCart(userId)
    }

    suspend fun removeCartItem(cartItemId: Int): Response<Void> {
        return apiService.removeCartItem(cartItemId)
    }

    suspend fun updateCartItemQuantity(cartItemId: Int, quantity: Int): Response<CartItem> {
        val request = CartQuantityRequest(quantity)
        return apiService.updateCartItemQuantity(cartItemId, request)
    }
}