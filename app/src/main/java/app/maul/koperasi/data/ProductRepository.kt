package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.product.DetailProductResponse
import app.maul.koperasi.model.product.ProductResponse
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getProducts(): ProductResponse {
        return apiService.getAllProduct()
    }

    suspend fun getDetailProduct(id: Int, userId: Int): DetailProductResponse {
        return apiService.getDetailProduct(id, userId)
    }
}