package app.maul.koperasi.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserResponse (
    val message: String,
    val data: User
)

@Parcelize
data class ForgotPasswordRequest(
    val email: String
) : Parcelable

data class ForgotPasswordResponse(
    val message: String,
    val otp: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)


data class ResetPasswordResponse(
    val message: String
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
