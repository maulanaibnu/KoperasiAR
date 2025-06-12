package app.maul.koperasi.presentation.ui.chatbot

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.MainActivity
import app.maul.koperasi.R
import app.maul.koperasi.databinding.ActivityChatbotBinding
import app.maul.koperasi.model.chatbot.ChatbotItem
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backToHome()
        getUserChat()
        sendChat()
    }

    private fun backToHome (){
        binding.backButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun sendChat(){
        binding.btnSend.setOnClickListener {
            val message = binding.EtChat.text.toString()
            val userId = Preferences.getId(this)
            if (message.isNotEmpty()){
                postChat(message, userId)
                binding.EtChat.text.clear()
            }
        }
    }

    private fun postChat(message: String, userId: Int){
        chatbotViewModel.addChat(message, userId)
        observeChat()
    }

    private fun getUserChat(){
        val userId = Preferences.getId(this)
        chatbotViewModel.getUserChat(userId)
        observeChat()
    }

    private fun observeChat(){
        chatbotViewModel.userChat.observe(this) { chat ->
            setupRecyclerView(chat)
            println("chatbot>> ${chat}}")
        }
    }

    private fun setupRecyclerView(chatbotItem: List<ChatbotItem>){
        chatbotAdapter = ChatbotAdapter(chatbotItem)
        binding.RvChatbot.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
            adapter = chatbotAdapter
            scrollToPosition(chatbotAdapter.itemCount-1)
            scrollState
        }
    }

}