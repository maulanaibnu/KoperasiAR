package app.maul.koperasi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.AddressRepository
import app.maul.koperasi.model.address.AddressData
import app.maul.koperasi.model.address.AddressRequest
import app.maul.koperasi.model.address.AddressResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<AddressData>>(emptyList())
    val addresses: StateFlow<List<AddressData>?> get() = _addresses

    private val _address = MutableStateFlow<AddressData?>(null)
    val address: StateFlow<AddressData?> get() = _address

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> get() = _success

    private val _selectedId = MutableStateFlow<Int?>(null)
    val selectedId: StateFlow<Int?> get() = _selectedId

    fun setSelectedId(id: Int?) {
        _selectedId.value = id
    }

    fun getAddresses() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = addressRepository.getAddresses()
                if (response.isSuccessful) {
                    _loading.value = false

                    _addresses.value = response.body()?.data ?: emptyList()
                    Log.d("TESTED","${ _addresses.value}")
                } else {
                    _error.value = "Gagal mengambil data address"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun createAddress(request: AddressRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = addressRepository.createAddress(request)
                if (response.isSuccessful) {
                    _success.value = "Alamat berhasil ditambahkan"
                    getAddresses()
                } else {
                    _error.value = "Gagal menambah address"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun updateAddress(id: Int, request: AddressRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = addressRepository.updateAddress(id, request)
                if (response.isSuccessful) {
                    _success.value = "Alamat berhasil diupdate"
                    getAddresses()
                } else {
                    _error.value = "Gagal mengupdate address"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = addressRepository.deleteAddress(id)
                if (response.isSuccessful) {
                    _success.value = "Alamat berhasil dihapus"
                    getAddresses()
                } else {
                    _error.value = "Gagal menghapus address"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun getAddressById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                val response = addressRepository.getAddress(id)
                if (response.isSuccessful) {
                    _address.value = response.body()?.data
                    Log.d("TESTED","${_address.value}")
                } else {
                    _error.value = "Gagal mengambil data address"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Terjadi kesalahan"
            }
            _loading.value = false
        }
    }

    fun resetSuccess() {
        _success.value = null
    }

    fun resetError() {
        _error.value = null
    }
}
