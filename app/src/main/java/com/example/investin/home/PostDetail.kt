package com.example.investin.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.investin.R

class PostDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // Initialize the back ImageView
        val ivBack: ImageView = findViewById(R.id.ivBack)

        // Set click listener to navigate back to Home activity
        ivBack.setOnClickListener {
            navigateToHome()
        }
    }

    // Function to navigate back to Home activity
    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // Finish the current activity to prevent going back to it
    }
}