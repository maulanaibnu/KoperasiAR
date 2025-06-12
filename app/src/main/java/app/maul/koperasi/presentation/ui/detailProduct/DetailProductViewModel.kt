package app.maul.koperasi.presentation.ui.detailProduct

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.CartRepository
import app.maul.koperasi.data.ProductRepository
import app.maul.koperasi.model.cart.CartRequest
import app.maul.koperasi.model.cart.CartResponse
import app.maul.koperasi.model.product.DetailProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(private val productRepository: ProductRepository, private val cartRepository: CartRepository):ViewModel(){
    private val _productDetail = MutableLiveData<DetailProductResponse>()
    val productDetail: LiveData<DetailProductResponse> get() = _productDetail

    private val _cartResponse = MutableLiveData<CartResponse>()
    val cartResponse: LiveData<CartResponse> get() = _cartResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchProductDetail(id: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = productRepository.getDetailProduct(id, userId)
                _productDetail.postValue(response)
            } catch (e: Exception) {
                // Handle the error
                _errorMessage.value = "Failed to fetch products: ${e.message}"
                Log.e("HomeViewModel", "Error: ${e.message}")
            }
        }
    }

    fun addCart(productId: Int, userId: Int, quantity: Int) {
        viewModelScope.launch {
            try {
                val request = CartRequest(productId, userId, quantity)
                val response = cartRepository.addCart(request)
                _cartResponse.postValue(response)
            } catch (e: Exception) {
                // Handle error case
                _errorMessage.value = "Failed to fetch products: ${e.message}"
                Log.e("HomeViewModel", "Error: ${e.message}")
            }
        }
    }
}