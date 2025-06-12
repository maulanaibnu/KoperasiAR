package app.maul.koperasi.model.verify

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifyResponse(
    val code: Int,
    val status: Int
) : Parcelable