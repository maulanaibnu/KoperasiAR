package app.maul.koperasi.presentation.ui.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.maul.koperasi.data.ChatbotRepository
import app.maul.koperasi.model.chatbot.ChatItemModel
import app.maul.koperasi.model.chatbot.ChatbotItem
import app.maul.koperasi.model.chatbot.ChatbotRequest
import app.maul.koperasi.model.chatbot.TopicItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository
) : ViewModel() {

    // Chatbot history dari backend
    private val _userChat = MutableLiveData<List<ChatbotItem>>()
    val userChat: LiveData<List<ChatbotItem>> get() = _userChat

    // Topik chat (chip)
    private val _chatTopics = MutableLiveData<List<TopicItem>>()
    val chatTopics: LiveData<List<TopicItem>> get() = _chatTopics

    // Untuk kombinasi welcome + topik saat chat kosong
    private val _initialItems = MutableLiveData<List<ChatItemModel>>()
    val initialItems: LiveData<List<ChatItemModel>> get() = _initialItems

    // Untuk adapter utama, agar bisa menampilkan topik + chat history sekaligus
    private val _chatDisplayItems = MutableLiveData<List<ChatItemModel>>()
    val chatDisplayItems: LiveData<List<ChatItemModel>> get() = _chatDisplayItems

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var lastUserId: Int? = null
    private var cachedTopics: List<TopicItem> = emptyList()

    /**
     * Panggil ini di onCreate/awal Activity.
     */
    fun loadChatbot(userId: Int) {
        lastUserId = userId
        viewModelScope.launch {
            try {
//                val chatResponse = chatbotRepository.getUserChat(userId)
//                val chats = chatResponse.data
//                if (chats.isEmpty()) {
//                    loadChatTopics(showWelcome = true)
//                } else {
//                    // Tampilkan chat beserta topics (jika ingin selalu ada chip di atas)
//                    cachedTopics = loadChatTopics(showWelcome = false)
//                    updateChatDisplayList(chats)
//                }
                loadChatTopics(showWelcome = true)
            } catch (e: Exception) {
                _errorMessage.postValue("Gagal memuat chat: ${e.message}")
            }
        }
    }

    /**
     * Load topics, dan jika showWelcome=true tampilkan juga pesan welcome + chip.
     */
    private suspend fun loadChatTopics(showWelcome: Boolean): List<TopicItem> {
        return try {
            val topicsResponse = chatbotRepository.getChatTopics()

            Log.d("ChatbotDebug", "API topicsResponse: ${topicsResponse.data}")
            cachedTopics = topicsResponse.data ?: emptyList()
            _chatTopics.postValue(cachedTopics)

            if (showWelcome) {
                val items = listOf(
                    ChatItemModel.BotMessage("Halo! Ada yang bisa saya bantu? Pilih topik di bawah ini, atau ketik pertanyaanmu."),
                    ChatItemModel.TopicButtons(cachedTopics)
                )
                Log.d("ChatbotDebug", "Topics to display (cachedTopics): $cachedTopics")
                _initialItems.postValue(items)
                _chatDisplayItems.postValue(items)
            }
            cachedTopics
        } catch (e: Exception) {
            _errorMessage.postValue("Gagal memuat topik: ${e.message}")
            emptyList()
        }
    }

    /**
     * Tambahkan chat baru ke backend, lalu refresh list chat.
     */
    fun addChat(message: String, userId: Int) {
        viewModelScope.launch {
            try {
                val request = ChatbotRequest(message, userId)
                val response = chatbotRepository.addChat(request)
                if (response.status == 200) {
                    // Refresh chat history
                    val chatResponse = chatbotRepository.getUserChat(userId)
                    _userChat.postValue(chatResponse.data)
                    updateChatDisplayList(chatResponse.data)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to send chat: ${e.message}")
            }
        }
    }

    /**
     * Utility: Gabungkan topics dengan chat history untuk adapter.
     * Topics selalu di atas, diikuti chat history.
     */
//    private fun updateChatDisplayList(chats: List<ChatbotItem>) {
//        val items = mutableListOf<ChatItemModel>()
//        if (cachedTopics.isNotEmpty()) {
//            items.add(ChatItemModel.TopicButtons(cachedTopics))
//            Log.d("ChatbotDebug", "Add TopicButtons: ${cachedTopics.size}")
//        }
//        chats.forEach {
//            Log.d("ChatbotDebug", "Chat history: user='${it.message}', bot='${it.response}'")
//            if (it.message.isNotEmpty()) items.add(ChatItemModel.UserMessage(it.message))
//            if (it.response.isNotEmpty()) items.add(ChatItemModel.BotMessage(it.response))
//        }
//        Log.d("ChatbotDebug", "Submit list to adapter: size=${items.size}, content=${items.map { it::class.simpleName }}")
//        _chatDisplayItems.postValue(items)
//    }
    private fun updateChatDisplayList(chats: List<ChatbotItem>) {
        val items = mutableListOf<ChatItemModel>()
        if (chats.isEmpty() && cachedTopics.isNotEmpty()) {
            items.add(ChatItemModel.TopicButtons(cachedTopics))
        }
        chats.forEach {
            if (it.message.isNotEmpty()) items.add(ChatItemModel.UserMessage(it.message))
            if (it.response.isNotEmpty()) items.add(ChatItemModel.BotMessage(it.response))
        }
        _chatDisplayItems.postValue(items)
    }

    /**
     * Untuk dipanggil ulang jika ingin reset ke tampilan welcome + topics.
     */
    fun resetToWelcome() {
        viewModelScope.launch {
            loadChatTopics(showWelcome = true)
        }
    }
}