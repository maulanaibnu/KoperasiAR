package app.maul.koperasi.viewmodel

import retrofit2.HttpException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.AddressRepository
import app.maul.koperasi.data.UserRepository
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.user.ChangePasswordRequest
import app.maul.koperasi.model.user.ForgotPasswordRequest
import app.maul.koperasi.model.user.ForgotPasswordResponse
import app.maul.koperasi.model.user.User
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

    fun changePassword(token: String, oldPass: String, newPass: String, confirmPass: String) {

        Log.d("ChangePassDebug", "2. Password diterima di ViewModel: [$oldPass]")

        // Validasi dasar di sisi klien
        if (newPass != confirmPass) {
            _error.value = "Password baru dan konfirmasi tidak cocok."
            return
        }
        // ... validasi lainnya ...

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _changePasswordSuccess.value = null

            try {
                val request = ChangePasswordRequest(oldPassword = oldPass, newPassword = newPass)
                // Panggil repository yang akan memanggil API
                val response = userRepository.changePassword(token, request)

                // Jika sukses, kirim pesan dari server ke UI
                _changePasswordSuccess.value = response.message

            } catch (e: Exception) {
                // Blok ini akan menangkap error seperti 400 (password lama salah) atau 404
                val errorMessage = if (e is HttpException) {
                    // Ambil pesan error dari server jika ada
                    val errorJson = e.response()?.errorBody()?.string()
                    // Di sini Anda bisa parse JSON untuk mendapatkan 'message' spesifik
                    // atau tampilkan pesan default
                    "Gagal: ${e.code()}. Password lama mungkin salah."
                } else {
                    "Terjadi kesalahan koneksi."
                }
                _error.value = errorMessage
            } finally {
                _loading.value = false
            }
        }
    }


}