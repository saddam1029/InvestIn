package com.example.investin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.investin.home.Home

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

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