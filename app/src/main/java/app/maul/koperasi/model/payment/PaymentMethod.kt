package app.maul.koperasi.model.payment

import androidx.annotation.DrawableRes

data class PaymentMethod(
    val bankName: String,
    val bankCode: String,
    @DrawableRes
    val logoResId: Int,
    var isSelected: Boolean = false
)