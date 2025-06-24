package app.maul.koperasi.data


import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.user.UpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun editProfile(
        name : RequestBody,
        gender : RequestBody,
        phone: RequestBody,
        image : MultipartBody.Part?): Response<UpdateResponse> {
        return apiService.updateUser(name, gender, phone, image)
    }



}