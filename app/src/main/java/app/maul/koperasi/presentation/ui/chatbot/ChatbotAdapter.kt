package app.maul.koperasi.presentation.ui.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.maul.koperasi.databinding.ItemChatBotBinding
import app.maul.koperasi.databinding.ItemChatTopicsBinding
import app.maul.koperasi.databinding.ItemChatUserBinding
import app.maul.koperasi.model.chatbot.ChatItemModel
import app.maul.koperasi.model.chatbot.TopicItem
import com.google.android.material.chip.Chip

class ChatbotAdapter(
    // Buat listener untuk menangani klik pada tombol topik
    private val onTopicClick: (TopicItem) -> Unit
) : ListAdapter<ChatItemModel, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    // Definisikan tipe view
    companion object {
        private const val TYPE_USER_MESSAGE = 0
        private const val TYPE_BOT_MESSAGE = 1
        private const val TYPE_TOPIC_BUTTONS = 2
    }

    // ViewHolder untuk setiap tipe
    inner class UserMessageViewHolder(private val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItemModel.UserMessage) {
            binding.tvUserMessage.text = item.message
        }
    }

    inner class BotMessageViewHolder(private val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItemModel.BotMessage) {
            binding.tvBotMessage.text = item.message
        }
    }

    inner class TopicButtonsViewHolder(private val binding: ItemChatTopicsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItemModel.TopicButtons) {
            binding.chipGroupTopics.removeAllViews() // Hapus chip lama sebelum menambahkan yang baru
            item.topics.forEach { topic ->
                val chip = Chip(itemView.context).apply {
                    text = topic.title
                    setOnClickListener { onTopicClick(topic) }
                }
                binding.chipGroupTopics.addView(chip)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItemModel.UserMessage -> TYPE_USER_MESSAGE
            is ChatItemModel.BotMessage -> TYPE_BOT_MESSAGE
            is ChatItemModel.TopicButtons -> TYPE_TOPIC_BUTTONS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER_MESSAGE -> {
                val binding = ItemChatUserBinding.inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            TYPE_BOT_MESSAGE -> {
                val binding = ItemChatBotBinding.inflate(inflater, parent, false)
                BotMessageViewHolder(binding)
            }
            TYPE_TOPIC_BUTTONS -> {
                val binding = ItemChatTopicsBinding.inflate(inflater, parent, false)
                TopicButtonsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            when (item) {
                is ChatItemModel.UserMessage -> (holder as UserMessageViewHolder).bind(item)
                is ChatItemModel.BotMessage -> (holder as BotMessageViewHolder).bind(item)
                is ChatItemModel.TopicButtons -> (holder as TopicButtonsViewHolder).bind(item)
            }
        }
    }
}

// DiffUtil untuk performa RecyclerView yang lebih baik
class ChatDiffCallback : DiffUtil.ItemCallback<ChatItemModel>() {
    override fun areItemsTheSame(oldItem: ChatItemModel, newItem: ChatItemModel): Boolean {
        // Jika model punya ID unik, bandingkan ID. Jika tidak, cara ini cukup.
        if (oldItem is ChatItemModel.UserMessage && newItem is ChatItemModel.UserMessage) {
            return oldItem.message == newItem.message
        }
        if (oldItem is ChatItemModel.BotMessage && newItem is ChatItemModel.BotMessage) {
            return oldItem.message == newItem.message
        }
        if (oldItem is ChatItemModel.TopicButtons && newItem is ChatItemModel.TopicButtons) {
            return oldItem.topics == newItem.topics
        }
        return false
    }

    override fun areContentsTheSame(oldItem: ChatItemModel, newItem: ChatItemModel): Boolean {
        return oldItem == newItem
    }
}