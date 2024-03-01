package com.example.investin

import MyNotificationAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.chat.Chat
import com.example.investin.home.Home
import com.google.android.material.bottomnavigation.BottomNavigationView

class Notification : AppCompatActivity() {

    private lateinit var adapter: MyNotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val recyclerView: RecyclerView = findViewById(R.id.rvChat)

        // Assuming you have a list of NotificationItems
        val notificationList = mutableListOf(
            NotificationItem(R.drawable.profile_2, "Manaan Ali Riaz Comment Your Post", "this business need more Investment", "11:12 am"),
            NotificationItem(R.drawable.profile_2, "Iman Khald Comment Your Post", "this business need more Investment", "12:30 pm"),
            NotificationItem(R.drawable.profile_2, "Tayab Bhi Comment Your Post", "this business need more Investment", "03:30 pm"),
            NotificationItem(R.drawable.profile_2, "Rabia Choti Comment Your Post", "this business need more Investment", "10:30 pm"),
            NotificationItem(R.drawable.profile_2, "Shazena Yousf Comment Your Post 2", "this business need more Investment", "11:30 pm"),
            NotificationItem(R.drawable.profile_2, "Ali Usman Comment Your Post", "this business need more Investment", "06:30 pm"),
            NotificationItem(R.drawable.profile_2, "Abdullah Proya Comment Your Post", "this business need more Investment", "09:30 pm"),
            NotificationItem(R.drawable.profile_2, "Amna Khalid Comment Your Post", "this business need more Investment", "01:30 pm")
            // Add more items as needed
        )

        adapter = MyNotificationAdapter(notificationList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        bottomNavigation()
    }

    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // fade activity when exit
        finish() // Finish the current activity to prevent going back to it
    }

    private fun bottomNavigation() {
        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.notification

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.message -> {
                    startActivity(Intent(applicationContext, Chat::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.home ->{
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
                R.id.search -> {
                    startActivity(Intent(applicationContext, Search::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Override the back button to navigate to Home activity
        navigateToHome()
    }

}