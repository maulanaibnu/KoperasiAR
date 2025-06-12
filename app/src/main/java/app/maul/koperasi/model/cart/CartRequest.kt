package app.maul.koperasi.model.cart

data class CartRequest(
    val product_id: Int,
    val user_id: Int,
    val quantity: Int
)

data class CartQuantityRequest(
    val quantity: Int
)
