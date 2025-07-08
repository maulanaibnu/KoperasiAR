package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.order.HistoryResponse
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.wishlist.WishlistRequest
import app.maul.koperasi.model.wishlist.WishlistResponse
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun createOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        return apiService.createOrder(orderRequest)
    }

    suspend fun getAllOrders(userId: Int): HistoryResponse {
        return apiService.getAllOrders(userId)
    }
}