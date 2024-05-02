package com.example.investin.home.drawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.investin.R
import com.example.investin.databinding.ActivityAboutUsBinding
import com.example.investin.databinding.ActivityFavoriteBinding
import com.example.investin.home.Home

class AboutUs : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
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