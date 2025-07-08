package app.maul.koperasi.data

import app.maul.koperasi.di.ApiClient
import app.maul.koperasi.model.ongkir.DestinationResponse
import app.maul.koperasi.model.ongkir.TariffResponse

class RajaOngkirRepository {

    private val apiService = ApiClient.rajaOngkirService

    suspend fun searchDestination(keyword: String): Result<DestinationResponse> {
        return try {
            val response = apiService.searchDestination(keyword)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to search destination: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calculateTariff(
        shipperId: Int,
        receiverId: Int,
        weight: Int,
        itemValue: Long,
        cod: String
    ): Result<TariffResponse> {
        return try {
            val response = apiService.calculateTariff(shipperId, receiverId, weight, itemValue, cod)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to calculate tariff: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}