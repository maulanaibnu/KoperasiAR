package app.maul.koperasi.presentation.ui.chatbot

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.databinding.ActivityChatbotBinding
import app.maul.koperasi.model.chatbot.ChatItemModel
import app.maul.koperasi.model.chatbot.TopicItem
import app.maul.koperasi.preference.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private val chatbotViewModel by viewModels<ChatbotViewModel>()
    private lateinit var chatbotAdapter: ChatbotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        backToHome()
        sendChat()

        // Ambil userId, langsung panggil loadChatbot (bukan getUserChat lagi)
        val userId = Preferences.getId(this)
        chatbotViewModel.loadChatbot(userId)

        observeViewModel()
    }

    private fun backToHome() { /* ... */ }

    private fun setupRecyclerView() {
        chatbotAdapter = ChatbotAdapter { topic ->
            // Saat topik diklik, kirim 'payload'-nya sebagai pesan
            postChat(topic.payload)
        }
        binding.RvChatbot.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
            adapter = chatbotAdapter
        }
    }

    private fun sendChat() {
        binding.btnSend.setOnClickListener {
            val message = binding.EtChat.text.toString().trim()
            if (message.isNotEmpty()) {
                postChat(message)
                binding.EtChat.text.clear()
            }
        }
    }

    private fun postChat(message: String) {
        val userId = Preferences.getId(this)
        chatbotViewModel.addChat(message, userId)
    }

    private fun observeViewModel() {
        chatbotViewModel.chatDisplayItems.observe(this) { items ->
            Log.d("ChatbotUI", "Receive items: ${items.map { it::class.simpleName }}")
            chatbotAdapter.submitList(items)
            scrollToBottom()
        }

        chatbotViewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToBottom() {
        binding.RvChatbot.post {
            binding.RvChatbot.scrollToPosition(chatbotAdapter.itemCount - 1)
        }
    }
}