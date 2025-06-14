package app.maul.koperasi.model.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AddressResponse(
    val message : String,
    val data : List<AddressData>
) : Parcelable

@Parcelize
data class AddressData(
    val id: Int,
    val recipient_name: String,
    val phone_number: String,
    val street: String,
    val notes: String,
    val label: String,
    val userId: Int
) : Parcelable
