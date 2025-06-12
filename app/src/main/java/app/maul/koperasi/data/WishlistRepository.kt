package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.wishlist.WishlistRequest
import app.maul.koperasi.model.wishlist.WishlistResponse
import retrofit2.Response
import javax.inject.Inject

class WishlistRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getWishlists(userId: Int): WishlistResponse {
        return apiService.getAllWishlist(userId)
    }

    suspend fun createWishlist(userId: Int, productId: Int): Response<Void> {
        return apiService.createWishlist(WishlistRequest(userId, productId))
    }

    suspend fun removeWishList(userId: Int, productId: Int): Response<Void> {
        return apiService.removeWishlist(userId, productId)
    }
}