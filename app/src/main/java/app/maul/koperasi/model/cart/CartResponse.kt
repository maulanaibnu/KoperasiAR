package app.maul.koperasi.model.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CartResponse(
    val message: String,
    val status: Int,
    val data: CartData
)

data class CartData(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val quantity: Int,
    val updatedAt: String,
    val createdAt: String
)

data class CartListResponse(
    val message: String,
    val status: Int,
    val data: List<CartItem>
)

@Parcelize
data class CartItem(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val quantity: Int,
    val product: ProductItem
) : Parcelable

@Parcelize
data class ProductItem(
    val id: Int = 0,
    val name: String = "",
    val kategoriId: Int = 0,
    val description: String = "",
    val status: String = "",
    val images: List<String> = emptyList(),
    val order: Int = 0,
    val quantity: Int = 0,
    val is_featured: Boolean = false,
    val sale_type: Int = 0,
    val price: Int = 0,
    val stock_status: String = "",
    val terjual: String = "0",
) : Parcelable
