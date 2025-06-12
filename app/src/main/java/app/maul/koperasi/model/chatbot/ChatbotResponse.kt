package app.maul.koperasi.model.chatbot

data class ChatbotListResponse(
    val message: String,
    val status: Int,
    val data: List<ChatbotItem>
)

data class ChatbotResponse(
    val message: String,
    val status: Int,
    val data: ChatbotItem
)

data class ChatbotItem(
    val id: Int,
    val user_id: Int,
    val message: String,
    val response: String,
    val updatedAt: String,
    val createdAt: String
)
