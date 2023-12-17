package com.example.investin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.login.SignIn
import com.example.investin.login.ViewPagerAdapter
import com.example.investin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val pageAdapter = ViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wormDotsIndicator =binding.wormDotsIndicator
        val viewPager = binding.viewPager // Make sure to use the correct ID
        val adapter = pageAdapter
        viewPager.adapter = adapter
        wormDotsIndicator.setViewPager2(viewPager)

        binding.viewPager.adapter = pageAdapter

        // Find the button by its ID
        val button = findViewById<Button>(R.id.btLogin)

        // Set an OnClickListener for the button
        button.setOnClickListener {
            // Start the HomeActivity
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}