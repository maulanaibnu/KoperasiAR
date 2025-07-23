package app.maul.koperasi.model.product

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val message: String,
    val data: List<Product>
)

data class DetailProductResponse(
    val message: String,
    val data: Product
)

data class Product(
    val id: Int,
    val name: String,
    val kategoriId: Int,
    val description: String,
    val status: String,
    val images: List<String>,
    val order: Int,
    val quantity: Int,
    val isFeatured: Boolean,
    val saleType: Int,
    val price: Int,
    val createdAt: String?,
    val updated: String?,
    val stockStatus: String?,
    @SerializedName("sold_count")
    val soldCount: String,
    val updatedAt: String,
    val category: Category,
    val model: Model?,
    val isWishlisted : Int
)


data class Model(
    val id: Int,
    val productId: Int,
    val urlname: String,
    val createdAt: String,
    val updatedAt: String
)

data class Category(
    val id: Int,
    val perabotan: Boolean,
    val miniatur: Boolean,
    val aksesoris: Boolean,
    val createdAt: String,
    val updatedAt: String
)
