package com.example.investin.chat
data class MessageItem(
    val senderId: String = "",
    val senderName: String = "", // Added field for sender's name
    val receiverId: String? = null,
    val message: String = "",
    val timestamp: Long = 0
)