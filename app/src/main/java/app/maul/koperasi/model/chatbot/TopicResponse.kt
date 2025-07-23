package app.maul.koperasi.model.chatbot

data class TopicResponse(
    val message: String,
    val status: Int,
    val data: List<TopicItem>
)

data class TopicItem(
    val title: String,
    val payload: String
)