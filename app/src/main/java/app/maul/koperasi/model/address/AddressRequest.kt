package app.maul.koperasi.model.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressRequest(
    val recipient_name: String,
    val phone_number: String,
    val street: String,
    val notes: String,
    val label: String,
    val city : String,
    val id_destination : Int,
) : Parcelable
