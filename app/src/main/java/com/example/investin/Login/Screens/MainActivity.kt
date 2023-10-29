package com.example.investin.Login.Screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val pageAdapter = ViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root // Inflate the view

        setContentView(view)

        // Now you can access the viewPager
        binding.viewPager.adapter = pageAdapter

        // Remove this line to allow swiping by touch
        // binding.viewPager.isUserInputEnabled = false

        // Optional: Set a callback for the "Next Page" button
        pageAdapter.setOnNextButtonClickListener { position ->
            binding.viewPager.currentItem = position + 1

            if (position == 2) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // Optionally, close the MainActivity
                finish()
            }
        }
    }
}