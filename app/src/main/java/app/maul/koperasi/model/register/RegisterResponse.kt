package app.maul.koperasi.model.register

import com.google.gson.annotations.SerializedName


data class RegisterResponse (
    val msg: String,
    @SerializedName("status")
    val statusSuccess : String?,
    val error: String?,
    val message: String,
    @SerializedName("data")
    val user: RegisterRequest
)

data class RegisterRequest(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val otp: String,
    val isverify: Boolean,
    val updatedAt: String,
    val createdAt: String,
    val images: String?
)