package com.example.chatbot

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private val messageList = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom),
            )
            insets
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        chatAdapter = ChatAdapter(messageList)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChat.layoutManager = linearLayoutManager
        recyclerViewChat.adapter = chatAdapter

        updateChatVisibility()

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessageToAI(text)
            }
        }
    }

    private fun updateChatVisibility() {
        if (messageList.isEmpty()) {
            emptyStateContainer.visibility = View.VISIBLE
            recyclerViewChat.visibility = View.GONE
        } else {
            emptyStateContainer.visibility = View.GONE
            recyclerViewChat.visibility = View.VISIBLE
        }
    }

    private fun sendMessageToAI(userText: String) {
        addUserMessage(userText)
        etMessage.text.clear()

        // Placeholder bot response ("Thinking...")
        addBotMessage()
        val botPosition = messageList.size - 1

        btnSend.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            val responseText = ChatBotAI.getResponse(userText)
            withContext(Dispatchers.Main) {
                if ((botPosition >= 0) && (botPosition < messageList.size)) {
                    messageList[botPosition].text = responseText
                    chatAdapter.notifyItemChanged(botPosition)
                    recyclerViewChat.scrollToPosition(messageList.size - 1)
                }
                btnSend.isEnabled = true
            }
        }
    }

    private fun addUserMessage(text: String) {
        messageList.add(Message(text, isUser = true))
        updateChatVisibility()
        chatAdapter.notifyItemInserted(messageList.size - 1)
        recyclerViewChat.scrollToPosition(messageList.size - 1)
    }

    private fun addBotMessage(messageText: String = "Thinking...") {
        messageList.add(Message(messageText, isUser = false))
        updateChatVisibility()
        chatAdapter.notifyItemInserted(messageList.size - 1)
        recyclerViewChat.scrollToPosition(messageList.size - 1)
    }
}
