package com.example.investin.home

data class PostModel(
    val userId: String = "",
    val title: String = "",
    val descriptor: String = "",
    val budget: String = "",
    val location: String = "",
    val skills: List<String> = emptyList(),
    val timestamp: Long = 0 // Add this field to store the timestamp
)
