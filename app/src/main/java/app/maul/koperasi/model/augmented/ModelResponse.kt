package app.maul.koperasi.model.augmented

import com.google.gson.annotations.SerializedName

data class ModelResponse (
    @field:SerializedName("status")
    val status: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: ModelData
)

data class ModelData(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("productId")
    val productId: Int,

    @field:SerializedName("urlname")
    val urlname: String, // Ini yang Anda inginkan

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
