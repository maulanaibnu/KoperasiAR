package app.maul.koperasi.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserResponse (
    val message: String,
    val data: User
)

data class ForgotPasswordResponse(
    val code: Int,
    val status: String,
    val data: Request
)

data class Request(
    val email: String,
    val otp: String,
)

@Parcelize
data class UpdateUserResponse(
    val status: String?,
    val message: String?,
    val error: String?
): Parcelable

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val gender: String?,
    val email: String,
    val verified: Boolean,
    val password: String?, // bisa diabaikan dari UI
    val phone: String?,
    val role: String,
    val otp: String?,
    val expiry_otp: String?,
    val profile_image: String?,
    val createdAt: String,
    val updatedAt: String
): Parcelable
