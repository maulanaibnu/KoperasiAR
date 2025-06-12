package app.maul.koperasi.model.order

import android.os.Parcelable
import app.maul.koperasi.model.product.Product
import kotlinx.parcelize.Parcelize

data class OrderRequest(
    val id_user: Int,
    val total: Double,
    val payment_type: String,
    val bank_transfer : String,
    val shipping_method: String,
    val customer_name: String,
    val phone_number: String,
    val address: String,
    val orderDetails: List<OrderDetail>
)

data class HistoryResponse(
    val message: String,
    val data: List<Order>
)

data class OrderResponse(
    val code: String,
    val status: String,
    val success: Boolean,
    val message: String,
    val data: Order
)
@Parcelize
data class Order(
    val id: Int,
    val code: String,
    val expired_time: String?,
    val total_price: Double,
    val id_user: Int,
    val payment_status: String,
    val createdAt: String,
    val customer_name: String,
    val phone_number: String,
    val address: String,
    val payment_type: String,
    val bank_transfer: String,
    val shipping_method: String,
    val status : String,
    val order_details: List<OrderDetail> // Added to link order with details
) : Parcelable

@Parcelize
data class OrderDetail(
    val id: Int,
    val id_order: Int,
    val id_product: Int,
    val name_product: String,
    val price: Int,
    val qty: Int,
    val createdAt: String,
    val updatedAt: String
) : Parcelable
