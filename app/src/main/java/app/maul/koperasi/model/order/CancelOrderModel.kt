package app.maul.koperasi.model.order

import com.google.gson.annotations.SerializedName

data class CancelRequest(
    val code: String
)

data class CancelResponse(
    val success: Boolean,
    val message: String,
    val data: CancelData?
)

data class CancelData(
    @SerializedName("transaction_status")
    val transactionStatus: String
)