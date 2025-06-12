package app.maul.koperasi.model.login

import com.google.gson.annotations.SerializedName

//@Parcelize
//data class LoginResponse (
//    val id : Int,
//    val name : String,
//    val pasword : String,
//): Parcelable

data class LoginResult(
    val id: Int,
    val name: String,
    val gender: String?,
    val email: String,
    val verified: Boolean,
    val phone: String?,
    val role: String,
    val otp: String,
    @SerializedName("expiry_otp") val expiryOtp: String,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    val token: String
)

data class LoginResponse(
    val msg : String,
    val status : Int,
    val data : LoginResult
)