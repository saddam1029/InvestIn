package com.example.investin.Advice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investin.Notification
import com.example.investin.R
import com.example.investin.Search
import com.example.investin.chat.AdviceItem
import com.example.investin.chat.Chat
import com.example.investin.databinding.ActivityAdviceBinding
import com.example.investin.home.Home
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class Advice : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceBinding
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize firebaseFirestore here
        firebaseFirestore = FirebaseFirestore.getInstance()

        adviceRecyclerView()

        bottomNavigation()
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
//        BottomNavigationManager.setupBottomNavigation(this, bottomNavigationView)

        binding.ivPost.setOnClickListener {
            val intent = Intent(this, AdvicePost::class.java)
            startActivity(intent)
        }
    }

    private fun adviceRecyclerView() {
        binding.rvAdvive.layoutManager = LinearLayoutManager(this)

        val allAdviceCollectionRef = firebaseFirestore.collection("InvestIn").document("Advice")
            .collection("all_advice_posts")

        allAdviceCollectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                Log.e("AdviceActivity", "Error fetching advice: ${exception.message}", exception)
                return@addSnapshotListener
            }


            if (snapshot != null && !snapshot.isEmpty) {
                val adviceList = snapshot.documents.map { document ->
                    document.toObject(AdviceItem::class.java)
                }.filterNotNull()

                val adviceAdapter = MyAdviceAdapter(adviceList)
                binding.rvAdvive.adapter = adviceAdapter
            }
        }
    }


    private fun bottomNavigation() {
        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.advice

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

                R.id.advice -> true
                R.id.notification -> {
                    startActivity(Intent(applicationContext, Notification::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }

                R.id.search -> {
                    startActivity(Intent(applicationContext, Search::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }

                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Override the back button to navigate to Home activity
        navigateToHome()
    }

    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // Finish the current activity to prevent going back to it
    }

}