package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.order.HistoryDetailResponse
import app.maul.koperasi.model.order.HistoryResponse
import app.maul.koperasi.model.order.InvoiceResponse
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderRequestList
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.order.OrderResponseList
import app.maul.koperasi.model.wishlist.WishlistRequest
import app.maul.koperasi.model.wishlist.WishlistResponse
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun createOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        return apiService.createOrder(orderRequest)
    }

    suspend fun getAllOrders(): HistoryResponse {
        return apiService.getAllOrders()
    }

    suspend fun getTransactionById(transactionId: Int): HistoryDetailResponse { // <-- Tambahkan metode ini
        return apiService.getTransactionById(transactionId)
    }
    suspend fun getInvoiceDetail(transactionId: Int): InvoiceResponse {
        return apiService.getInvoiceDetail(transactionId)
    }

    suspend fun createTransaction(orderRequest : OrderRequestList): Response<OrderResponseList> {
        return apiService.createOrderList(orderRequest)
    }
}