package app.maul.koperasi.model.order

data class OrderResponseList(
    val code: Int,
    val status: String,
    val success: Boolean,
    val message: String,
    val data: OrderResponseData
)

data class OrderResponseData(
    val virtual_account: String,
    val expired: String,
    val price: String,
    val code: String,
    val payment_type: String,
    val bank: String,
    val shipping_cost: Int
)

