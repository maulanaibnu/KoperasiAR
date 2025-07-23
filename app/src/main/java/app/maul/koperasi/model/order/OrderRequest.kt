package app.maul.koperasi.model.order

data class OrderRequestList(
    val user_id: Int,
    val payment_type: String,
    val bank: String,
    val shipping_method: String,
    val shipping_cost: Int,
    val address: String,
    val products: List<ProductRequest>
)

data class ProductRequest(
    val id_product: Int,
    val name_product: String,
    val quantity: Int,
    val price: Int
)

