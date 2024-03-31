package com.example.investin.chat


data class AdviceItem(
    var postId: String = "", // Add postId property
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0 // Add this field to store the timestamp
)