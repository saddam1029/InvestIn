package com.example.investin.Advice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.investin.databinding.ActivityAdviceDetailBinding

class AdviceDetail : AppCompatActivity() {
    private lateinit var binding: ActivityAdviceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val title = intent.getStringExtra("title")
        val descriptor = intent.getStringExtra("description")
        val time = intent.getStringExtra("time")


        // Set data to TextViews
        binding.tvTitle.text = title
        binding.tvDescription.text = descriptor
        binding.tvTime.text = time

        binding.ivBack.setOnClickListener {
            navigateToAdvice()
        }
    }

    private fun navigateToAdvice() {
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}