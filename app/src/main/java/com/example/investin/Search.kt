package com.example.investin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class Search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        bottomNavigation()
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

}