package app.maul.koperasi.model.ongkir

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class DestinationResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("data")
    val data: List<DestinationData>
):Parcelable

@Parcelize
data class DestinationData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("subdistrict_name")
    val subdistrictName: String,
    @SerializedName("district_name")
    val districtName: String,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("province_name")
    val provinceName: String,
    @SerializedName("zip_code")
    val zipCode: String
):Parcelable

// Model Meta ini bisa digunakan bersama untuk kedua response

@Parcelize
data class Meta(
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val code: Int,
    @SerializedName("status")
    val status: String
):Parcelable