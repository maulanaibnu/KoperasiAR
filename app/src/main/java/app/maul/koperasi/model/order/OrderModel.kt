package app.maul.koperasi.model.order

import android.os.Parcelable
import app.maul.koperasi.model.product.Product
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class OrderRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("id_product")
    val idProduct: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("payment_type")
    val paymentType: String,

    @SerializedName("bank")
    val bank: String,

    @SerializedName("shipping_method")
    val shippingMethod: String,

    @SerializedName("shipping_cost")
    val shippingCost: Int,

    @SerializedName("address")
    val address: String
): Parcelable


@Parcelize
data class OrderResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: TransactionData?
): Parcelable

@Parcelize
data class TransactionData(
    @SerializedName("virtual_account")
    val virtualAccount: String,

    @SerializedName("expired")
    val expired: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("payment_type")
    val paymentType: String,

    @SerializedName("bank")
    val bank: String
):Parcelable





@Parcelize
data class HistoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<HistoryItem>
) : Parcelable

@Parcelize
data class HistoryItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("id_product")
    val idProduct: Int,

    @SerializedName("name_product")
    val nameProduct: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("id_user")
    val idUser: Int,

    @SerializedName("total_price")
    val totalPrice: String,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("payment_type")
    val paymentType: String,

    @SerializedName("shipping_method")
    val shippingMethod: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("customer_name")
    val customerName: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("expire_time")
    val expireTime: String?, // Dibuat nullable karena nilainya bisa null

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("shipping_cost")
    val shippingCost: String,

    @SerializedName("product")
    val product: ProductHistoryDetail
) : Parcelable

// 3. Kelas untuk data detail produk yang ada di dalam setiap item riwayat
@Parcelize
data class ProductHistoryDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("kategoriId")
    val kategoriId: Int,

    @SerializedName("description")
    val description: String,

    @SerializedName("images")
    val images: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price")
    val price: Int, // Tipe data Int, berbeda dengan price di atas

    @SerializedName("stock_status")
    val stockStatus: String?, // Nullable

    @SerializedName("terjual")
    val terjual: Int?, // Nullable

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable


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
    val image_url: String?,
    val createdAt: String,
    val updatedAt: String
) : Parcelable
