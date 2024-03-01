package com.example.investin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.chat.Chat
import com.example.investin.home.Home
import com.google.android.material.bottomnavigation.BottomNavigationView

class Search : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private val daysOfWeek = arrayOf(
        "Investor Industry Preferences", "Investment Range",
        "Geographical Location of Investors", "Investor Rating or Experience", "Investor's Previous Investments"
    )

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)

        // Initialize adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, daysOfWeek)
        listView.adapter = adapter

        // Set item click listener for ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            // Handle item click (you can open a new activity or perform other actions)
            val selectedItem = daysOfWeek[position]
            Toast.makeText(this, "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()
        }

        // Set search query listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the ListView based on the search query
                adapter.filter.filter(newText)
                return true
            }
        })

        bottomNavigation()
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
        bottomNavigationView.selectedItemId = R.id.search

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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
                R.id.notification -> {
                    startActivity(Intent(applicationContext, Notification::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.search -> true
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
