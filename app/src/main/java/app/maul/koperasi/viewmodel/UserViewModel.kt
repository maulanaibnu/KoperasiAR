package app.maul.koperasi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.AddressRepository
import app.maul.koperasi.data.UserRepository
import app.maul.koperasi.model.address.AddressData
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

}