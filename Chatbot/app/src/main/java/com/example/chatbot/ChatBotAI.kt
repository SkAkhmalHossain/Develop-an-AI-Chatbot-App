package com.example.chatbot

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatBotAI {

    private const val MODEL_NAME = "gemini-1.5-flash"
    private var chatSession: Chat? = null
    private var initializedKey: String? = null

    private fun getChatSession(apiKey: String): Chat {
        if ((chatSession == null) || (initializedKey != apiKey)) {
            val generativeModel = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                systemInstruction = content {
                    text("You are a helpful, friendly, and highly intelligent AI Assistant. You must always respond clearly, accurately, comprehensively, and politely in English.")
                },
            )
            chatSession = generativeModel.startChat()
            initializedKey = apiKey
        }
        return chatSession!!
    }

    suspend fun getResponse(query: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        // If user configured a Gemini API Key in local.properties, try live Gemini LLM
        if (apiKey.isNotEmpty() && (apiKey != "YOUR_API_KEY")) {
            try {
                val chat = getChatSession(apiKey)
                val response = chat.sendMessage(query)
                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    return responseText
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Smart English Fallback Assistant Engine
        return getSmartEnglishResponse(query)
    }

    private fun getSmartEnglishResponse(query: String): String {
        val lowerQuery = query.lowercase(Locale.ROOT).trim()

        return when {
            // Greetings
            lowerQuery.contains("hi") || lowerQuery.contains("hello") || lowerQuery.contains("hey") || lowerQuery.contains("greetings") || lowerQuery.contains("salam") ->
                "Hello! I am your AI Assistant. How can I help you today?"

            // How are you
            lowerQuery.contains("how are you") || lowerQuery.contains("how do you do") || lowerQuery.contains("kemon") ->
                "I am doing great, thank you for asking! How are you doing today? How can I assist you?"

            // Identity & Name
            lowerQuery.contains("who are you") || lowerQuery.contains("your name") || lowerQuery.contains("what are you") ->
                "I am an intelligent AI Chatbot assistant created to help answer your questions, assist with coding, general knowledge, problem-solving, and much more."

            // Capabilities & Help
            lowerQuery.contains("help") || lowerQuery.contains("what can you do") || lowerQuery.contains("features") ->
                "I can assist you with a wide variety of topics, including:\n\n• Programming & Software Development (Kotlin, Java, Python, Android)\n• Science, Technology & General Knowledge\n• Mathematics & Problem Solving\n• Date, Time & Daily Queries\n• Entertainment & Casual Conversation"

            // Date & Time
            lowerQuery.contains("time") || lowerQuery.contains("clock") || lowerQuery.contains("what time") -> {
                val sdf = SimpleDateFormat("hh:mm a", Locale.US)
                "The current time is ${sdf.format(Date())}."
            }

            lowerQuery.contains("date") || lowerQuery.contains("today") || lowerQuery.contains("day") -> {
                val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
                "Today's date is ${sdf.format(Date())}."
            }

            // Tech & Programming
            lowerQuery.contains("kotlin") ->
                "Kotlin is a modern, statically typed programming language created by JetBrains. It is the official and recommended language for Android application development due to its conciseness, safety, and full interoperability with Java."

            lowerQuery.contains("android") ->
                "Android is a mobile operating system developed by Google, based on a modified version of the Linux kernel. It powers billions of mobile devices worldwide and supports modern UI development using Jetpack Compose and XML layouts."

            lowerQuery.contains("java") ->
                "Java is a versatile, object-oriented programming language designed to have as few implementation dependencies as possible. It is widely used in enterprise applications, Android development, and backend web services."

            lowerQuery.contains("python") ->
                "Python is an interpreted, high-level, general-purpose programming language. Its design philosophy emphasizes code readability. It is immensely popular in Artificial Intelligence, Machine Learning, Data Science, and Web Development."

            lowerQuery.contains("ai") || lowerQuery.contains("artificial intelligence") || lowerQuery.contains("llm") ->
                "Artificial Intelligence (AI) refers to the simulation of human intelligence in machines programmed to think, learn, and solve problems like humans. Modern Large Language Models (LLMs) like Google Gemini can generate text, answer complex queries, and assist in creative tasks."

            // Knowledge & Science
            lowerQuery.contains("capital of bangladesh") ->
                "The capital of Bangladesh is Dhaka."

            lowerQuery.contains("capital of usa") || lowerQuery.contains("capital of united states") ->
                "The capital of the United States is Washington, D.C."

            lowerQuery.contains("capital of france") ->
                "The capital of France is Paris."

            lowerQuery.contains("weather") ->
                "I don't have access to live real-time weather sensors without an active weather API, but you can check your local weather app for up-to-date forecasts!"

            // Jokes & Entertainment
            lowerQuery.contains("joke") || lowerQuery.contains("funny") || lowerQuery.contains("laugh") ->
                "Here is a joke for you:\nWhy do programmers prefer dark mode?\nBecause light attracts bugs! 😄"

            // Gratitude
            lowerQuery.contains("thank") || lowerQuery.contains("thanks") || lowerQuery.contains("appreciate") ->
                "You're very welcome! Feel free to ask if you need anything else."

            // Bye
            lowerQuery.contains("bye") || lowerQuery.contains("goodbye") || lowerQuery.contains("see you") ->
                "Goodbye! Have a wonderful day ahead! Feel free to chat anytime."

            // Default Smart English Fallback
            else -> {
                "I understand your message: \"$query\". I am ready to answer any questions you have about programming, technology, science, general knowledge, or daily tasks. Feel free to ask!"
            }
        }
    }
}
