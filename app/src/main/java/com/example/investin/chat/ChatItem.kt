package com.example.investin.chat

data class ChatItem(
    val userId: String,
    val userName: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val userProfilePictureUrl: String
)