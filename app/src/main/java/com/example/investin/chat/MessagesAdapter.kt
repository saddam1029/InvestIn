package com.example.investin.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import java.text.DateFormat
import java.util.Date

class MessagesAdapter(private var messages: MutableList<MessageItem>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    fun updateMessages(newMessages: List<MessageItem>) {
        messages = newMessages.toMutableList()
        notifyDataSetChanged()
    }

    fun addMessage(message: MessageItem) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.tvSender.text = message.senderName // Changed to senderName
        holder.tvMessage.text = message.message
        holder.tvTimestamp.text = DateFormat.getDateTimeInstance().format(Date(message.timestamp))
    }

    override fun getItemCount() = messages.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }
}
