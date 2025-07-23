package app.maul.koperasi.presentation.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.CartRepository
import app.maul.koperasi.model.cart.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartRepository: CartRepository): ViewModel() {
    private val _userCart = MutableLiveData<List<CartItem>>()
    val userCart: LiveData<List<CartItem>> get() = _userCart

    private val _deleteCartResponse = MutableLiveData<String>()
    val deleteCartResponse: LiveData<String> = _deleteCartResponse

    private val _updateCartResponse = MutableLiveData<String>()
    val updateCartResponse: LiveData<String> = _updateCartResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getUserCart(userId: Int) {
        viewModelScope.launch {
            try {
                val response = cartRepository.getUserCart(userId)

                // [FIX] Tambahkan pengecekan status yang lebih lengkap
                if (response.status == 200) {
                    _userCart.postValue(response.data ?: emptyList()) // Kirim list kosong jika data null
                    Log.d("CartViewModel", "Data cart diterima, jumlah item: ${response.data?.size ?: 0}")
                } else {
                    // Beri tahu UI jika ada pesan error dari server
                    _errorMessage.postValue("Gagal memuat keranjang: ${response.message}")
                    Log.w("CartViewModel", "API merespons dengan status ${response.status}: ${response.message}")
                }
            } catch (e: Exception) {
                // Tangani error jaringan atau parsing
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
                Log.e("CartViewModel", "Error Exception: ${e.message}")
            }
        }
    }

    fun addCartItem(cartItemId: Int, qty: Int) {
        viewModelScope.launch {
            try {
                val response = cartRepository.updateCartItemQuantity(cartItemId, qty)
                if (response.isSuccessful) {
                    _updateCartResponse.postValue("Item updated")
                }
            } catch (e: Exception) {
                _updateCartResponse.postValue("Failed to remove item")
            }
        }
    }

    fun minCartItem(cartItemId: Int, qty: Int) {
        viewModelScope.launch {
            try {
                val response = cartRepository.updateCartItemQuantity(cartItemId, qty)
                if (response.isSuccessful) {
                    _updateCartResponse.postValue("Item updated")
                }
            } catch (e: Exception) {
                _updateCartResponse.postValue("Failed to remove item")
            }
        }
    }

    fun deleteCartItem(cartItemId: Int) {
        viewModelScope.launch {
            try {
                val response = cartRepository.removeCartItem(cartItemId)
                if (response.isSuccessful) {
                    _deleteCartResponse.postValue("Item removed from cart")
                }
            } catch (e: Exception) {
                _deleteCartResponse.postValue("Failed to remove item")
            }
        }
    }
}