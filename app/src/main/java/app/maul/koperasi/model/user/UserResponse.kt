package app.maul.koperasi.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserResponse (
    val code: Int,
    val status:String
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