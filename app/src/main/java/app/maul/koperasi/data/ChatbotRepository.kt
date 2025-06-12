package app.maul.koperasi.data

import app.maul.koperasi.api.ApiService
import app.maul.koperasi.model.chatbot.ChatbotListResponse
import app.maul.koperasi.model.chatbot.ChatbotRequest
import javax.inject.Inject

class ChatbotRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getUserChat (userId: Int): ChatbotListResponse {
        return apiService.getUserChat(userId)
    }
    suspend fun addChat (chatbotRequest: ChatbotRequest): ChatbotListResponse {
        return apiService.addChat(chatbotRequest)
    }
}