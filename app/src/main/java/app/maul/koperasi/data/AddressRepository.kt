package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressDetailResponse
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.model.address.AddressResponse
import retrofit2.Response
import javax.inject.Inject

class AddressRepository @Inject constructor(private val apiService: ApiService)  {
    suspend fun getAddresses(): Response<AddressResponse> {
        return apiService.getAddresses()
    }

    suspend fun getAddress(id: Int): Response<AddressDetailResponse> {
        return apiService.getAddress(id)
    }

    suspend fun createAddress(request: AddressRequest): Response<AddressDetailResponse> {
        return apiService.createAddress(request)
    }

    suspend fun updateAddress(id: Int, request: AddressRequest): Response<AddressDetailResponse> {
        return apiService.updateAddress(id, request)
    }

    suspend fun deleteAddress(id: Int): Response<Unit> {
        return apiService.deleteAddress(id)
    }
}