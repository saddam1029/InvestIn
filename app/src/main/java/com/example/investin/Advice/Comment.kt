package com.example.investin.Advice

data class Comment(
    val userId: String = "",
    val comment: String = "",
    val name: String = "", // Add name property
    val roll: String = "", // Add roll property
    val timestamp: Long = 0
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", "", 0)
}

