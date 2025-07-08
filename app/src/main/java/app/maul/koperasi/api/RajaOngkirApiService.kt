package app.maul.koperasi.api

import app.maul.koperasi.model.ongkir.DestinationResponse
import app.maul.koperasi.model.ongkir.TariffResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RajaOngkirApiService {

    @GET("tariff/api/v1/destination/search")
    suspend fun searchDestination(
        @Query("keyword") keyword: String
    ): Response<DestinationResponse>

    @GET("tariff/api/v1/calculate")
    suspend fun calculateTariff(
        @Query("shipper_destination_id") shipperDestinationId: Int,
        @Query("receiver_destination_id") receiverDestinationId: Int,
        @Query("weight") weight: Int,
        @Query("item_value") itemValue: Long,
        @Query("cod") cod: String // "yes" or "no"
    ): Response<TariffResponse>
}