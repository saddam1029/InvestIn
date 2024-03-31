package com.example.investin.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.investin.R
import com.example.investin.databinding.ActivityMessageBinding
import com.example.investin.databinding.ActivityPostDetailBinding

class Message : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("userName")

        binding.tvChatName.text = title

        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
            navigateToChat()
        }


    }

    private fun navigateToChat() {
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}