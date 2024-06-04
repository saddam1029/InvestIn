package com.example.investin.chat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investin.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Message : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive user ID and user name from Intent
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")

        // Get the current user's ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the current user's ID is not null
        if (currentUserId.isNullOrEmpty()) {
            // Handle the case where the current user's ID is null or empty
            Toast.makeText(this, "Error: Current user ID is null or empty", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity if the current user's ID is null or empty
            return
        }

        binding.tvChatName.text = userName

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadMessages(currentUserId, userId)

        binding.ivBack.setOnClickListener {
            navigateToChat()
        }

        binding.ivSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(currentUserId, userId, message)
            }
        }
    }


    private fun setupRecyclerView() {
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter(mutableListOf()) // Initialize with a mutable list
        binding.rvMessages.adapter = messagesAdapter
    }


    private fun loadMessages(currentUserId: String?, receiverId: String?) {
        // Check if currentUserId and receiverId are not null
        if (currentUserId.isNullOrEmpty() || receiverId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Current user ID or receiver ID is null or empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Construct the path to the messages collection
        val messagesCollectionRef = firestore.collection("InvestIn")
            .document("chat")
            .collection(currentUserId)
            .document(receiverId)
            .collection("messages")

        // Query messages where senderId is currentUserId and receiverId is receiverId
        messagesCollectionRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = snapshots?.toObjects(MessageItem::class.java) ?: listOf()
                messagesAdapter.updateMessages(messages)
            }
    }

    private fun sendMessage(currentUserId: String?, receiverId: String?, message: String) {
        if (currentUserId.isNullOrEmpty() || receiverId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Current user ID or receiver ID is null or empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch sender's name
        val userDocRef = firestore.collection("InvestIn")
            .document("profile")
            .collection(currentUserId)
            .document(currentUserId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val senderName = document.getString("userName") ?: ""

                val messagesCollectionRef = firestore.collection("InvestIn")
                    .document("chat")
                    .collection(currentUserId)
                    .document(receiverId)
                    .collection("messages")

                val timestamp = System.currentTimeMillis()

                val chatMessage = hashMapOf(
                    "senderId" to currentUserId,
                    "senderName" to senderName, // Include sender's name
                    "receiverId" to receiverId,
                    "message" to message,
                    "timestamp" to timestamp
                )

                messagesCollectionRef
                    .add(chatMessage)
                    .addOnSuccessListener { documentReference ->
                        binding.etMessage.text.clear()

                        val newMessage = MessageItem(
                            senderId = currentUserId,
                            senderName = senderName, // Include sender's name
                            receiverId = receiverId,
                            message = message,
                            timestamp = timestamp
                        )

                        messagesAdapter.addMessage(newMessage)
                        binding.rvMessages.scrollToPosition(messagesAdapter.itemCount - 1)

                        // Update chat list in ChatActivity
                        updateChatList(currentUserId, receiverId, message, timestamp)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Sender profile not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching sender profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChatList(currentUserId: String, receiverId: String, message: String, timestamp: Long) {
        val chatListRef = firestore.collection("ChatList").document(currentUserId)
        chatListRef.update("lastMessage", message, "lastMessageTime", timestamp)
            .addOnSuccessListener {
                Log.d("MessageActivity", "Chat list updated successfully")
            }
            .addOnFailureListener { e ->
                Log.d("MessageActivity", "Error updating chat list: ${e.message}")
            }
    }





    private fun navigateToChat() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
