package app.maul.koperasi.model.user

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("oldPassword")
    val oldPassword: String,

    @SerializedName("newPassword")
    val newPassword: String
)


data class ChangePasswordResponse(
    @SerializedName("message")
    val message: String
)