package com.example.investin.Advice

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.chat.AdviceItem
import com.example.investin.databinding.ActivityAdviceDetailBinding
import com.example.investin.home.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdviceDetail : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceDetailBinding
    private lateinit var postId: String
    private lateinit var commentsAdapter: CommentsAdapter
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve postId from intent extras
        postId = intent.getStringExtra("postId").toString()

        if (postId.isEmpty()) {
            // Handle the case where postId is null or empty
            Toast.makeText(this, "Post details unavailable", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity
            return
        }

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
   // Define currentUser
        val currentUser = FirebaseAuth.getInstance().currentUser

  // Set up click listener for post comment image view
        binding.ivPostCommit.setOnClickListener {
            val comment = binding.etAdvice.text.toString().trim()

            if (comment.isNotEmpty()) {
                val userRepository = UserRepository(FirebaseFirestore.getInstance())

                // Make a call to retrieve user information
                currentUser?.uid?.let { userId ->
                    userRepository.getUserInfo(userId,
                        onSuccess = { user ->
                            // Once you have the user information, you can access the fullName and role
                            val title = user.fullName ?: ""
                            val role = user.role ?: ""

                            // Now, you can save the comment along with the title and role to Firestore
                            saveComment(comment, title, role)
                        },
                        onFailure = { exception ->
                            // Handle failure to retrieve user information
                            Log.e("HomeActivity", "Error retrieving user information: ${exception.message}", exception)
                            // Still attempt to save the comment without user information
                            saveComment(comment, "", "")
                        }
                    )
                } ?: run {
                    Log.e("HomeActivity", "Current user is null.")
                    // Handle case where currentUser is null
                    Toast.makeText(this, "Current user is null.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }


        // Initialize RecyclerView and its adapter
        setupRecyclerView()

        // Fetch comments from Firestore and update RecyclerView
        fetchCommentsFromFirestore()

    }

    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter(emptyList()) // Initially empty list
        binding.rvAdviceDetail.adapter = commentsAdapter
        binding.rvAdviceDetail.layoutManager = LinearLayoutManager(this)
    }


    private fun fetchCommentsFromFirestore() {
        val commentsCollectionRef = FirebaseFirestore.getInstance()
            .collection("InvestIn")
            .document("Advice")
            .collection("comment")
            .document(postId)
            .collection("comments")

        Log.e(TAG, "fetchCommentsFromFirestore: $postId")
        commentsCollectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                Log.e("AdviceActivity", "Error fetching comments: ${exception.message}", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val adviceList = snapshot.documents.map { document ->
                    document.toObject(Comment::class.java)
                }.filterNotNull()

                commentsAdapter = CommentsAdapter(adviceList)
//                commentsAdapter.comments=adviceList
                binding.rvAdviceDetail.adapter = commentsAdapter
            }

        }
    }


    private fun saveComment(comment: String, title: String, role: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val commentData = hashMapOf(
            "userId" to userId,
            "comment" to comment,
            "name" to title, // Use title as name
            "roll" to role,   // Use role as roll
            "timestamp" to System.currentTimeMillis()
        )

        val commentsCollectionRef = FirebaseFirestore.getInstance()
            .collection("InvestIn")
            .document("Advice")
            .collection("comment")
            .document(postId)
            .collection("comments")

        commentsCollectionRef.add(commentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Comment posted successfully", Toast.LENGTH_SHORT).show()
                // Clear EditText after posting comment
                binding.etAdvice.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to post comment: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun navigateToAdvice() {
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}