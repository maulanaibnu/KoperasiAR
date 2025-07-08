package app.maul.koperasi.model.ongkir

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TariffResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("data")
    val data: TariffData?
):Parcelable


@Parcelize
data class TariffData(
    @SerializedName("calculate_reguler")
    val reguler: List<ShippingOption>,
    @SerializedName("calculate_cargo")
    val cargo: List<ShippingOption>,
    @SerializedName("calculate_instant")
    val instant: List<ShippingOption>
):Parcelable

@Parcelize
data class ShippingOption(
    @SerializedName("shipping_name")
    val shippingName: String,
    @SerializedName("service_name")
    val serviceName: String,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("is_cod")
    val isCod: Boolean,
    @SerializedName("shipping_cost")
    val shippingCost: Int,
    @SerializedName("shipping_cashback")
    val shippingCashback: Int,
    @SerializedName("shipping_cost_net")
    val shippingCostNet: Int,
    @SerializedName("grandtotal")
    val grandTotal: Int,
    @SerializedName("service_fee")
    val serviceFee: Int,
    @SerializedName("net_income")
    val netIncome: Int,
    @SerializedName("etd")
    val etd: String
):Parcelable