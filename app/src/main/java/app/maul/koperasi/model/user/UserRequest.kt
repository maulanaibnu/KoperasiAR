package app.maul.koperasi.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRequest (
    val name: String,
    val gender: String,
    val phone: String

) : Parcelable