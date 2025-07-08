package app.maul.koperasi.presentation.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.OrderRepository
import app.maul.koperasi.model.order.HistoryItem // [1] Ganti import ke HistoryItem
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    // [1] Ganti tipe data dari List<Order> menjadi List<HistoryItem>
    private val _orders = MutableLiveData<List<HistoryItem>>()
    val orders: LiveData<List<HistoryItem>> get() = _orders

    private val _orderResponse = MutableLiveData<OrderResponse?>()
    val orderResponse: LiveData<OrderResponse?> get() = _orderResponse

    // [2] Tambahkan LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun createOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true) // [2] Mulai loading
            try {
                val response = orderRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    _orderResponse.postValue(response.body())
                } else {
                    // Pesan error dari server jika ada
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue("Gagal membuat pesanan: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.postValue(false) // [2] Selesai loading
            }
        }
    }

    fun getAllOrders(userId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true) // [2] Mulai loading
            try {
                val response = orderRepository.getAllOrders(userId)
                _orders.postValue(response.data) // [1] Menggunakan response.data yang tipenya List<HistoryItem>
            } catch (e: Exception) { // [3] Penanganan error yang lebih detail
                val message = when (e) {
                    is HttpException -> "Gagal memuat data: Error ${e.code()}"
                    is IOException -> "Tidak ada koneksi internet."
                    else -> "Terjadi kesalahan tidak diketahui."
                }
                _errorMessage.postValue(message)
            } finally {
                _isLoading.postValue(false) // [2] Selesai loading
            }
        }
    }
}