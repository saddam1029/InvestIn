package com.example.investin.home.drawer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investin.databinding.ActivityFavoriteBinding
import com.example.investin.home.Home
import com.example.investin.home.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Favorite : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: MyFavoriteAdapter
    private val favoritePostsList = mutableListOf<PostModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView and Adapter
        adapter = MyFavoriteAdapter(favoritePostsList, this)
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.layoutManager = LinearLayoutManager(this)

        // Fetch favorite posts
        fetchFavoritePosts()

        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
            navigateToHome()
        }
    }

    private fun fetchFavoritePosts() {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid ?: ""

        if (currentUserId.isNotEmpty()) {
            val favoritesCollectionRef = firebaseFirestore.collection("InvestIn")
                .document("favorites")
                .collection(currentUserId)

            favoritesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    favoritePostsList.clear() // Clear the list before adding new items
                    for (document in documents) {
                        val post = document.toObject(PostModel::class.java)
                        favoritePostsList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Handle the failure to fetch favorite posts
                    Toast.makeText(this, "Error fetching favorite posts: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case where user ID is empty
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
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
