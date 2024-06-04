package com.example.investin.notification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.Advice.Advice
import com.example.investin.R
import com.example.investin.search.Search
import com.example.investin.chat.Chat
import com.example.investin.home.Home
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Notification : AppCompatActivity() {

    private lateinit var adapter: MyNotificationAdapter
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestoreDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val recyclerView: RecyclerView = findViewById(R.id.rvChat)

        // Initialize RecyclerView and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyNotificationAdapter(emptyList())
        recyclerView.adapter = adapter

        // Fetch notifications for the current user
        fetchNotifications()

        // Set up bottom navigation
        bottomNavigation()
    }

    // Function to fetch notifications for the current user
    // Modify the fetchNotifications() function to include the timestamp field in the query
    private fun fetchNotifications() {
        currentUser?.uid?.let { userId ->
            firestoreDB.collection("InvestIn")
                .document("invitations")
                .collection("all_invitations")
                .whereEqualTo("toUserId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Add orderBy for the timestamp field
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle any errors
                        return@addSnapshotListener
                    }

                    // Parse the snapshot to NotificationItem objects
                    val notifications = snapshot?.documents?.mapNotNull { document ->
                        document.toObject(NotificationItem::class.java)
                    }

                    // Update the RecyclerView with the fetched notifications
                    notifications?.let { adapter.setData(it) }
                }
        }
    }



    // Set up bottom navigation
    private fun bottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.notification
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            // Handle bottom navigation item clicks
            when (item.itemId) {
                R.id.message -> {
                    startActivity(Intent(applicationContext, Chat::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext, Home::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.advice -> {
                    startActivity(Intent(applicationContext, Advice::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.notification -> true
//                R.id.search -> {
//                    startActivity(Intent(applicationContext, Search::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToHome()
    }

    // Function to navigate to Home activity
    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
