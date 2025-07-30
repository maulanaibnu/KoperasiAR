package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.order.CancelRequest
import app.maul.koperasi.model.order.CancelResponse
import app.maul.koperasi.model.order.HistoryDetailResponse
import app.maul.koperasi.model.order.HistoryResponse
import app.maul.koperasi.model.order.InvoiceResponse
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderRequestList
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.order.OrderResponseList
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun createOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        return apiService.createOrder(orderRequest)
    }

    suspend fun getAllOrders(): HistoryResponse {
        return apiService.getAllOrders()
    }

    suspend fun getTransactionById(transactionId: Int): HistoryDetailResponse {
        return apiService.getTransactionById(transactionId)
    }
    suspend fun getInvoiceDetail(transactionId: Int): InvoiceResponse {
        return apiService.getInvoiceDetail(transactionId)
    }

    suspend fun createTransaction(orderRequest : OrderRequestList): Response<OrderResponseList> {
        return apiService.createOrderList(orderRequest)
    }

    suspend fun cancelTransaction(orderCode: String): CancelResponse {
        val request = CancelRequest(code = orderCode)
        return apiService.cancelTransaction(request)
    }
}