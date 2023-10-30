package com.example.investin.Login.Screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.investin.R
import com.example.investin.databinding.ActivityMainBinding
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val pageAdapter = ViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager) // Make sure to use the correct ID
        val adapter = pageAdapter
        viewPager.adapter = adapter
        wormDotsIndicator.setViewPager2(viewPager)

        binding.viewPager.adapter = pageAdapter

        // Find the button by its ID
        val button = findViewById<Button>(R.id.btLogin)

        // Set an OnClickListener for the button
        button.setOnClickListener {
            // Start the HomeActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}