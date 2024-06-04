package com.example.investin.notification

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.investin.R
import com.example.investin.chat.Chat
import com.example.investin.chat.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyNotificationAdapter(private var notificationList: List<NotificationItem>) :
    RecyclerView.Adapter<MyNotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_notification, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]

        // Load sender profile image if URL is not null or empty
        if (!currentItem.senderProfileImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.senderProfileImage)
                .into(holder.iconImageView)
        } else {
            // Handle case where profile image URL is null or empty
            holder.iconImageView.setImageResource(R.drawable.profile_2)
        }

        // Set sender name and title
        holder.titleTextView.text = "${currentItem.senderName} Sent you an Invitation"
        holder.messageTextView.text = currentItem.title

        // Convert time to a readable format and display it
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = sdf.format(Date(currentItem.timestamp))
        holder.lastTime.text = formattedTime

        // Set up item click listener
        holder.itemView.setOnClickListener {
            showMoveToChatDialog(holder.itemView.context, currentItem)
        }
    }

    // Function to show dialog
    private fun showMoveToChatDialog(context: Context, notificationItem: NotificationItem) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Move to Chat")
        builder.setMessage("Do you want to move to chat with ${notificationItem.senderName}?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Pass user ID and user name through Intent
            val intent = Intent(context, Message::class.java)
            intent.putExtra("userId", notificationItem.fromUserId)
            intent.putExtra("userName", notificationItem.senderName)
            context.startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }

    // Update the inner ViewHolder class name
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.ivChatProfile)
        val titleTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val messageTextView: TextView = itemView.findViewById(R.id.tvBusinessName)
        val lastTime: TextView = itemView.findViewById(R.id.tvLastTime)
    }

    // Function to set new data to the adapter
    fun setData(newList: List<NotificationItem>) {
        notificationList = newList
        notifyDataSetChanged()
    }
}
