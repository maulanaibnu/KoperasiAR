package app.maul.koperasi.model.user

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