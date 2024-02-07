package com.example.investin.chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.Advice
import com.example.investin.Home
import com.example.investin.Notification
import com.example.investin.R
import com.example.investin.Search
import com.google.android.material.bottomnavigation.BottomNavigationView

class Chat : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myChatAdapter : MyChatAdapter
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        searchView = findViewById(R.id.svChat)

        chatRecyclerView()

        bottomNavigation()

        setupSearchView()


    }

    private fun chatRecyclerView() {
        recyclerView = findViewById(R.id.rvChat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the back ImageView
        val ivBack: ImageView = findViewById(R.id.ivBack)

        // Set click listener to navigate back to Home activity
        ivBack.setOnClickListener {
            navigateToHome()
        }

        val dataList = listOf(
            ChatItem("Imran Khan", "Hii what's up?", R.drawable.imran_khan),
            ChatItem("Salahudin Ayyubi", "Hello there!", R.drawable.salahudein),
            ChatItem("Adolf Hitler", "Hello there!", R.drawable.hitler),
            ChatItem("Babar Azam", "Hello there!", R.drawable.babar),
            ChatItem("Muhammad Ali", "Hello there!", R.drawable.muhamad_ali),
            ChatItem("Bruce Lee", "Hello there!", R.drawable.brus_lee),
            ChatItem("Michal Jordan", "Hello there!", R.drawable.jordon),
            ChatItem("Andrew Tate", "Hello there!", R.drawable.tate),
            ChatItem("Cristiano Ronaldo", "Hello there!", R.drawable.ronaldo),
            ChatItem("Tony Stark", "Hello there!", R.drawable.stark),
            ChatItem("Madara Uchiha", "Hello there!", R.drawable.madara),
            ChatItem("Undertaker", "Hello there!", R.drawable.undertacker),

        )

        myChatAdapter = MyChatAdapter(dataList)
        recyclerView.adapter = myChatAdapter
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text change
                myChatAdapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // Finish the current activity to prevent going back to it
    }

    private fun bottomNavigation() {
        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.message

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.message -> true
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
                R.id.notification -> {
                    startActivity(Intent(applicationContext, Notification::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
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