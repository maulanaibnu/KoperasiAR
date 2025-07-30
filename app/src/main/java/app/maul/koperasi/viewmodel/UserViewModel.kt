package app.maul.koperasi.viewmodel

import retrofit2.HttpException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.UserRepository
import app.maul.koperasi.model.user.ChangePasswordRequest
import app.maul.koperasi.model.user.ChangePasswordResponse
import app.maul.koperasi.model.user.ForgotPasswordResponse
import app.maul.koperasi.model.user.User
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<String?>(null)
    val user: StateFlow<String?> get() = _user

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> get() = _userProfile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> get() = _success

    private val _forgotPasswordResponse = MutableLiveData<ForgotPasswordResponse?>()
    val forgotPasswordResponse: LiveData<ForgotPasswordResponse?> get() = _forgotPasswordResponse

    private val _changePasswordSuccess = MutableStateFlow<String?>(null)
    val changePasswordSuccess: StateFlow<String?> get() = _changePasswordSuccess

    fun updateUser(
        name : RequestBody,
        gender : RequestBody,
        phone: RequestBody,
        image : MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = userRepository.editProfile(name, gender, phone, image)
                if (response.isSuccessful) {
                    _loading.value = false

                    _user.value = response.body()?.message
                    _success.value = response.body()?.message
                    Log.d("TESTED","${ _user.value}")
                } else {
                    _error.value = "Error ${response.code()}: ${response.body()?.message}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun getUserProfile(token: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = userRepository.getUserProfile(token)
                result.onSuccess { user ->
                    _userProfile.value = user
                    _success.value = "Berhasil mengambil profil"
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Terjadi kesalahan saat mengambil profil"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun changePassword(oldPass: String, newPass: String, confirmPass: String) {

        if (newPass != confirmPass) {
            _error.value = "Password baru dan konfirmasi tidak cocok."
            return
        }
        if (!isPasswordValid(newPass)) {
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _changePasswordSuccess.value = null

            try {
                val request = ChangePasswordRequest(oldPassword = oldPass, newPassword = newPass)
                val response = userRepository.changePassword(request)


                _changePasswordSuccess.value = response.message

            } catch (e: Exception) {
                val errorMessage = if (e is HttpException) {
                    try {
                        val errorJson = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorJson, ChangePasswordResponse::class.java)

                        // Tampilkan pesan error spesifik dari API
                        errorResponse.message ?: "Terjadi kesalahan (Kode: ${e.code()})"
                    } catch (jsonError: Exception) {
                        // Jika parsing JSON gagal, tampilkan pesan generik
                        "Terjadi kesalahan server (Kode: ${e.code()})"
                    }
                } else {
                    "Terjadi kesalahan koneksi. Periksa internet Anda."
                }
                _error.value = errorMessage
            } finally {
                _loading.value = false
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        // Minimal 8 karakter
        if (password.length < 8) {
            _error.value = "Password minimal harus 8 karakter."
            return false
        }

        // Huruf depan harus besar (kapital)
        if (!password.matches("^[A-Z].*".toRegex())) {
            _error.value = "Password harus diawali dengan huruf kapital."
            return false
        }

        //  karakter khusus
        if (!password.matches(".*[!@#\$%^&*()].*".toRegex())) {
            _error.value = "Password harus mengandung karakter spesial (contoh: !@#\$%)."
            return false
        }

        return true
    }


}