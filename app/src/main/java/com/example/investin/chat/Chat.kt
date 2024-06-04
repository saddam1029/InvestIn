package com.example.investin.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.Advice.Advice
import com.example.investin.R
import com.example.investin.search.Search
import com.example.investin.home.Home
import com.example.investin.notification.Notification
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class Chat : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myChatAdapter: MyChatAdapter
    private lateinit var searchView: SearchView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        searchView = findViewById(R.id.svChat)
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearchView()
        setupBottomNavigation()
        loadUsersFromFirestore() // Load user data
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvChat)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadUsersFromFirestore() {
        val currentUserId = "currentUserId" // Replace with actual current user ID
        val usersWithChat = mutableListOf<String>()

        // Retrieve user IDs who have chatted with the current user
        firestore.collection("ChatHistory")
            .whereEqualTo("participantIds.$currentUserId", true)
            .get()
            .addOnSuccessListener { chatDocuments ->
                for (chatDocument in chatDocuments) {
                    // Get the IDs of users other than the current user
                    val participantIds = chatDocument["participantIds"] as Map<String, Boolean>
                    val otherUserId = participantIds.keys.find { it != currentUserId }
                    if (otherUserId != null && otherUserId !in usersWithChat) {
                        usersWithChat.add(otherUserId)
                    }
                }
                Log.d("Firestore", "Users with chat: $usersWithChat") // Log to check the contents
                if (usersWithChat.isNotEmpty()) {
                    // If usersWithChat is not empty, query the Users collection to get user details
                    val usersQuery = firestore.collection("Users").whereIn(FieldPath.documentId(), usersWithChat)
                    usersQuery.get()
                        .addOnSuccessListener { userDocuments ->
                            val dataList = userDocuments.map { userDocument ->
                                ChatItem(
                                    userId = userDocument.id,
                                    userName = userDocument.getString("name") ?: "Unknown",
                                    lastMessage = "Hello there!", // Placeholder last message
                                    lastMessageTime = System.currentTimeMillis(), // Placeholder last message time
                                    userProfilePictureUrl = userDocument.getString("profilePictureUrl") ?: ""
                                )
                            }
                            myChatAdapter = MyChatAdapter(dataList)
                            recyclerView.adapter = myChatAdapter
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error loading users: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Handle case where there are no users with chat
                    Log.d("Firestore", "No users with chat found.")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading chat history: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                myChatAdapter.filter(newText)
                return true
            }
        })
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.message

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.message -> true
                R.id.home -> {
                    navigateTo(Home::class.java)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.advice -> {
                    navigateTo(Advice::class.java)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.notification -> {
                    navigateTo(Notification::class.java)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
//                R.id.search -> {
//                    navigateTo(Search::class.java)
//                    true
//                }
                else -> false
            }
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateTo(Home::class.java)
    }
}
