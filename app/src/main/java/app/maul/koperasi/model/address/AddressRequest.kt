package app.maul.koperasi.model.address

data class AddressRequest(
    val recipient_name: String,
    val phone_number: String,
    val street: String,
    val notes: String,
    val label: String
)
