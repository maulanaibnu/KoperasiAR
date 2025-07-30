package app.maul.koperasi.data


import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.user.ChangePasswordRequest
import app.maul.koperasi.model.user.ChangePasswordResponse
import app.maul.koperasi.model.user.UpdateResponse
import app.maul.koperasi.model.user.User
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

    suspend fun getUserProfile(token: String): Result<User> {
        return try {
            val response = apiService.getUserDetail()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat profil: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): ChangePasswordResponse {
        return apiService.changePassword(request)
    }





}