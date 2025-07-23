package app.maul.koperasi.presentation.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.OrderRepository
import app.maul.koperasi.model.order.HistoryItem // [1] Ganti import ke HistoryItem
import app.maul.koperasi.model.order.InvoiceResponse
import app.maul.koperasi.model.order.OrderRequest
import app.maul.koperasi.model.order.OrderRequestList
import app.maul.koperasi.model.order.OrderResponse
import app.maul.koperasi.model.order.OrderResponseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableLiveData<List<HistoryItem>>()
    val orders: LiveData<List<HistoryItem>> get() = _orders

    private val _orderResponse = MutableLiveData<OrderResponse?>()
    val orderResponse: LiveData<OrderResponse?> get() = _orderResponse

    private val _orderResponseList = MutableLiveData<OrderResponseList?>()
    val orderResponseList: LiveData<OrderResponseList?> get() = _orderResponseList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _transactionDetail = MutableLiveData<HistoryItem?>()
    val transactionDetail: LiveData<HistoryItem?> get() = _transactionDetail

    private val _invoiceDetail = MutableLiveData<InvoiceResponse?>()
    val invoiceDetail: LiveData<InvoiceResponse?> get() = _invoiceDetail

    fun createOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = orderRepository.createOrder(orderRequest)
                if (response.isSuccessful) {
                    _orderResponse.postValue(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue("Gagal membuat pesanan: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createOrderList(orderRequest: OrderRequestList) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = orderRepository.createTransaction(orderRequest)
                if (response.isSuccessful) {
                    _orderResponseList.postValue(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.postValue("Gagal membuat pesanan: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAllOrders() { // <--- Perbaikan di sini
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = orderRepository.getAllOrders() // <--- Perbaikan di sini (panggil tanpa userId)
                _orders.postValue(response.data)
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> "Gagal memuat data: Error ${e.code()}"
                    is IOException -> "Tidak ada koneksi internet."
                    else -> "Terjadi kesalahan tidak diketahui: ${e.message}"
                }
                _errorMessage.postValue(message)
                Log.e("OrderViewModel", "Error fetching orders", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getTransactionById(transactionId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true) // Atur status loading
            _errorMessage.postValue("") // Bersihkan pesan error sebelumnya
            try {
                val response = orderRepository.getTransactionById(transactionId)
                if (response.success) {
                    _transactionDetail.postValue(response.data) // Update LiveData dengan detail transaksi
                } else {
                    _transactionDetail.postValue(null) // Set null jika tidak berhasil
                    _errorMessage.postValue(response.message) // Atur pesan error dari backend
                }
            } catch (e: Exception) {
                _transactionDetail.postValue(null) // Set null jika terjadi error
                val message = when (e) {
                    is retrofit2.HttpException -> "Gagal memuat detail: Error ${e.code()}"
                    is java.io.IOException -> "Tidak ada koneksi internet."
                    else -> "Terjadi kesalahan tidak diketahui: ${e.message}"
                }
                _errorMessage.postValue(message) // Atur pesan error
            } finally {
                _isLoading.postValue(false) // Nonaktifkan status loading
            }
        }
    }

    fun getInvoiceDetail(transactionId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                // Memanggil fungsi baru di repository
                val response = orderRepository.getInvoiceDetail(transactionId)
                _invoiceDetail.postValue(response)
            } catch (e: Exception) {
                val message = when (e) {
                    is retrofit2.HttpException -> "Gagal memuat invoice: Error ${e.code()}"
                    is java.io.IOException -> "Tidak ada koneksi internet."
                    else -> "Terjadi kesalahan tidak diketahui: ${e.message}"
                }
                _errorMessage.postValue(message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
