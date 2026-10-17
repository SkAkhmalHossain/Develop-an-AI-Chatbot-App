package com.example.chatbot

data class Message(
    var text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
