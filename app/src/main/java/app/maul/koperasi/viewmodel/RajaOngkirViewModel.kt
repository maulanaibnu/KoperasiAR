package app.maul.koperasi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.maul.koperasi.data.RajaOngkirRepository
import app.maul.koperasi.model.ongkir.DestinationData
import app.maul.koperasi.model.ongkir.TariffData

class RajaOngkirViewModel {
    private val repository = RajaOngkirRepository()

    // LiveData untuk hasil pencarian destinasi
    private val _destinationResult = MutableLiveData<List<DestinationData>>()
    val destinationResult: LiveData<List<DestinationData>> = _destinationResult

    // LiveData untuk hasil cek ongkir
    private val _tariffResult = MutableLiveData<TariffData>()
    val tariffResult: LiveData<TariffData> = _tariffResult

    // LiveData untuk pesan error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchDestination(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.searchDestination(keyword)
            result.onSuccess { response ->
                _destinationResult.postValue(response.data)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
            _isLoading.postValue(false)
        }
    }

    fun calculateTariff(shipperId: Int, receiverId: Int, weight: Int, itemValue: Long, cod: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.calculateTariff(shipperId, receiverId, weight, itemValue, cod)
            result.onSuccess { response ->
                _tariffResult.postValue(response.data)
            }.onFailure { error ->
                _errorMessage.postValue(error.message)
            }
            _isLoading.postValue(false)
        }
    }
}