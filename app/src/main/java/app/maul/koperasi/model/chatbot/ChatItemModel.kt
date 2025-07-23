package app.maul.koperasi.model.chatbot



sealed class ChatItemModel {
    data class UserMessage(val message: String) : ChatItemModel()
    data class BotMessage(val message: String) : ChatItemModel()
    data class TopicButtons(val topics: List<TopicItem>) : ChatItemModel()
}