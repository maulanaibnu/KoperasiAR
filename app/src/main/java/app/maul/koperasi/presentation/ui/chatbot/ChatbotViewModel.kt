package app.maul.koperasi.presentation.ui.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.ChatbotRepository
import app.maul.koperasi.model.chatbot.ChatbotItem
import app.maul.koperasi.model.chatbot.ChatbotRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(private val chatbotRepository: ChatbotRepository): ViewModel() {
    private val _userChat = MutableLiveData<List<ChatbotItem>>()
    val userChat: LiveData<List<ChatbotItem>> get() = _userChat

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getUserChat(userId: Int){
        viewModelScope.launch {
            try {
                val response = chatbotRepository.getUserChat(userId)
                if (response.status == 200){
                    _userChat.postValue(response.data)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch carts: ${e.message}"
                Log.e("CartViewModel", "Error: ${e.message}")
            }
        }
    }

    fun addChat(message: String, userId: Int){
        viewModelScope.launch {
            try {
                val request = ChatbotRequest(message, userId)
                val response = chatbotRepository.addChat(request)
                if (response.status == 200){
                    _userChat.postValue(response.data)
                }
            }catch (e: Exception){
                _errorMessage.value = "Failed to fetch carts: ${e.message}"
                Log.e("CartViewModel", "Error: ${e.message}")
            }
        }
    }
}