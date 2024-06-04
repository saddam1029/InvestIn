package com.example.investin.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.investin.R
import java.text.DateFormat
import java.util.Date

class MyChatAdapter(private var chatItems: List<ChatItem>) :
    RecyclerView.Adapter<MyChatAdapter.ViewHolder>() {

    // Store the original list of chat items
    private val originalChatItems: List<ChatItem> = chatItems.toList()
    private var filteredChatItems: List<ChatItem> = chatItems.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = filteredChatItems[position]
        holder.tvUserName.text = chatItem.userName
        holder.tvUserLastMessage.text = chatItem.lastMessage
        holder.tvLastTime.text = DateFormat.getDateTimeInstance().format(Date(chatItem.lastMessageTime))

        Glide.with(holder.itemView.context)
            .load(chatItem.userProfilePictureUrl)
            .placeholder(R.drawable.profile_2) // Placeholder image
            .into(holder.ivChatProfile)
    }

    override fun getItemCount() = filteredChatItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivChatProfile: ImageView = itemView.findViewById(R.id.ivChatProfile)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserLastMessage: TextView = itemView.findViewById(R.id.tvUserLastMessage)
        val tvLastTime: TextView = itemView.findViewById(R.id.tvLastTime)
    }

    // Filter method for search functionality
    fun filter(query: String?) {
        filteredChatItems = if (query.isNullOrEmpty()) {
            originalChatItems
        } else {
            originalChatItems.filter {
                it.userName.contains(query, ignoreCase = true) || it.lastMessage.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
