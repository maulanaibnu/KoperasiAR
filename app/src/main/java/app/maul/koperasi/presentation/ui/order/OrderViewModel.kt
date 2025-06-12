package app.maul.koperasi.presentation.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.OrderRepository
import app.maul.koperasi.data.WishlistRepository
import app.maul.koperasi.model.order.Order
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.model.wishlist.Wishlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: OrderRepository) : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _orderResponse = MutableLiveData<OrderResponse?>()
    val orderResponse: LiveData<OrderResponse?> get() = _orderResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            try {
                val response = orderRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    _orderResponse.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to create order")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    fun getAllOrders(userId: Int) {
        viewModelScope.launch {
            try {
                val orders = orderRepository.getAllOrders(userId)
                _orders.postValue(orders.data)
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error fetching orders: ${e.message}")
                _errorMessage.postValue("Failed to fetch orders: ${e.message}")
            }
        }
    }
}