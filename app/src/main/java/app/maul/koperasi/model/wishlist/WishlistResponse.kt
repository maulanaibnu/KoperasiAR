package app.maul.koperasi.model.wishlist

import app.maul.koperasi.model.product.Product

class WishlistResponse (
    val message: String,
    val status: Int,
    val data: List<Wishlist>
)

data class Wishlist(
    val userId : Int,
    val productId : Int,
    val product: Product
)