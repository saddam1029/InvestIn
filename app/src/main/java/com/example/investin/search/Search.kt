package com.example.investin.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investin.Advice.Advice
import com.example.investin.R
import com.example.investin.chat.Chat
import com.example.investin.databinding.ActivitySearchBinding
import com.example.investin.home.Home
import com.example.investin.notification.Notification
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var userList: MutableList<SearchModel>
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = Firebase.firestore
        userList = mutableListOf()
        initializeRecyclerView()
//        bottomNavigation()
        retrieveUsersAndShowData()
    }

    private fun initializeRecyclerView() {
        searchAdapter = SearchAdapter(userList)
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = searchAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun retrieveUsersAndShowData() {
        val profilesCollectionRef = firebaseFirestore.collection("InvestIn")
            .document("profile")

        profilesCollectionRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userIdsCollectionRef = profilesCollectionRef.collection("userProfiles")
                    userIdsCollectionRef.get().addOnSuccessListener { userIdDocuments ->
                        userList.clear() // Clear the existing list to avoid duplicates
                        for (userIdDocument in userIdDocuments) {
                            val userId = userIdDocument.id
                            val userDocumentRef = profilesCollectionRef.collection(userId).document(userId)
                            userDocumentRef.get().addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    val userName = userDoc.getString("name") ?: ""
                                    val userRole = userDoc.getString("userRole") ?: ""
                                    val userModel = SearchModel(userId, userName, userRole)
                                    userList.add(userModel)
                                    searchAdapter.notifyDataSetChanged()
                                }
                            }.addOnFailureListener { exception ->
                                // Handle errors in retrieving user document
                                Log.e("SearchActivity", "Error fetching user profile: ${exception.message}", exception)
                            }
                        }
                    }.addOnFailureListener { exception ->
                        // Handle errors in retrieving user IDs
                        Log.e("SearchActivity", "Error fetching user IDs: ${exception.message}", exception)
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle errors in retrieving profiles collection
                Log.e("SearchActivity", "Error fetching profiles: ${exception.message}", exception)
            }
    }


    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // Finish the current activity to prevent going back to it
    }

//    private fun bottomNavigation() {
//        // Initialize and assign variable
//        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
//
//        // Set Home selected
//        bottomNavigationView.selectedItemId = R.id.search
//
//        // Perform item selected listener
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.message -> {
//                    startActivity(Intent(applicationContext, Chat::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
//                R.id.home -> {
//                    startActivity(Intent(applicationContext, Home::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
//                R.id.advice -> {
//                    startActivity(Intent(applicationContext, Advice::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
//                R.id.notification -> {
//                    startActivity(Intent(applicationContext, Notification::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
//                R.id.search -> true
//                else -> false
//            }
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Override the back button to navigate to Home activity
        navigateToHome()
    }
}
