package app.maul.koperasi.presentation.ui.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ChatItemBinding
import app.maul.koperasi.model.chatbot.ChatbotItem

class ChatbotAdapter(
    private val chatItems: List<ChatbotItem>
): RecyclerView.Adapter<ChatbotAdapter.ChatbotViewHolder>() {
    inner class ChatbotViewHolder(val binding: ChatItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatbotViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatbotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatbotViewHolder, position: Int) {
        val chatItem = chatItems[position]
        holder.binding.apply {
            tvQuestion.text = chatItem.message
            tvAnswer.text = chatItem.response
        }
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }
}