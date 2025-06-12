package app.maul.koperasi.presentation.ui.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.WishlistRepository
import app.maul.koperasi.model.product.Product
import app.maul.koperasi.model.wishlist.Wishlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(private val wishlistRepository: WishlistRepository): ViewModel() {
    private val _wishlists = MutableLiveData<List<Wishlist>>()
    val wishlists: LiveData<List<Wishlist>> get() = _wishlists

    private val _updateWishlistResponse = MutableLiveData<String>()
    val updateWishlistResponse: LiveData<String> = _updateWishlistResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addWishlist(productId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = wishlistRepository.createWishlist(productId, userId)
                if (response.isSuccessful) {
                    _updateWishlistResponse.postValue("Item updated")
                }
            } catch (e: Exception) {
                _updateWishlistResponse.postValue("Failed to remove item")
            }
        }
    }

    fun removeWishlist(userId: Int, productId: Int) {
        viewModelScope.launch {
            try {
                val response = wishlistRepository.removeWishList(userId, productId)
                if (response.isSuccessful) {
                    _updateWishlistResponse.postValue("Item updated")
                }
            } catch (e: Exception) {
                _updateWishlistResponse.postValue("Failed to remove item")
            }
        }
    }

    fun getAllWishlist(userId: Int) {
        viewModelScope.launch {
            try {
                val response = wishlistRepository.getWishlists(userId)
                _wishlists.value = response.data
                Log.d("WishlistViewModel", "Wishlist: ${response.data}")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch wishlist: ${e.message}"
                Log.e("WishlistViewModel", "Error: ${e.message}")
            }
        }
    }
}