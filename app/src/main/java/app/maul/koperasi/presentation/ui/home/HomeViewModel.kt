package app.maul.koperasi.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.ProductRepository
import app.maul.koperasi.model.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productRepository: ProductRepository): ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getAllProducts() {
        viewModelScope.launch {
            try {
                val response = productRepository.getProducts()
                _products.value = response.data
                Log.d("HomeViewModel", "Products: ${response.data}")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch products: ${e.message}"
                Log.e("HomeViewModel", "Error: ${e.message}")
            }
        }
    }
}