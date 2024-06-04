package com.example.investin.notification

data class NotificationItem(
    val fromUserId: String = "",
    val toUserId: String = "",
    val title: String = "",
    val timestamp: Long = 0,
    val senderName: String = "",
    val senderProfileImage: String = ""
)
