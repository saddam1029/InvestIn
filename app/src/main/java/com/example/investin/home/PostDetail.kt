package com.example.investin.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import com.example.investin.R
import com.example.investin.databinding.ActivityPostDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDetail : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val postId = intent.getStringExtra("postId") ?: ""
        val title = intent.getStringExtra("title")
        val descriptor = intent.getStringExtra("descriptor")
        val location = intent.getStringExtra("location")
        val skills = intent.getStringArrayListExtra("skills")
        val time = intent.getStringExtra("time")
        val budget = intent.getStringExtra("budget")
//        val userId = intent.getStringExtra("userId") ?: ""


        // Set data to TextViews
        binding.tvTitle.text = title
        binding.tvDescription.text = descriptor
        binding.tvLocation.text = location
        binding.tvTime.text = time
        binding.tvBudget.text = budget

        // Dynamically add TextViews for skills
        skills?.let { displaySkills(it) }

        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
            navigateToHome()
        }

        // Query Firestore to count the number of posts by the same user
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            val allPostsQuery = firestoreDB.collection("InvestIn/posts/all_posts")

            allPostsQuery.get()
                .addOnSuccessListener { documents ->
                    // Filter documents to count only those with matching user ID
                    val userPostsCount = documents.filter { doc ->
                        doc.getString("userId") == currentUserID
                    }.size

                    // Update the TextView with the count
                    val postCountText = "Total posts by this user: $userPostsCount"
                    binding.tvTotalJobPost.text = postCountText
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    Log.e("PostDetail", "Error getting user posts: ", e)
                }
        }



        // Count the number of user posts
//        getUserPostCount(userId)
    }

//    @SuppressLint("SetTextI18n")
//    private fun getUserPostCount(userId: String) {
//        Log.d("PostDetail", "Getting post count for user: $userId")
//        val postsCollectionRef = firestoreDB
//            .collection("InvestIn")
//            .document("posts")
//            .collection("all_posts")
//
//        // Query to retrieve posts for the current user
//        val query = postsCollectionRef.whereEqualTo("userId", userId)
//
//        // Add a snapshot listener to listen for real-time updates
//        query.addSnapshotListener { snapshot, exception ->
//            if (exception != null) {
//                // Handle error
//                Log.e("PostDetail", "Error loading posts: ${exception.message}", exception)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                val userPosts = mutableListOf<PostModel>()
//
//                // Parse the snapshot and add posts to the list
//                for (document in snapshot) {
//                    val postId = document.id
//                    val post = document.toObject(PostModel::class.java).apply {
//                        this.postId = postId // Assign postId from document ID
//                    }
//                    userPosts.add(post)
//                }
//
//                // Update the TextView with the total number of user posts
//                binding.tvTotalJobPost.text = "Total Posts: ${userPosts.size}"
//                Log.d("PostDetail", "Total posts: ${userPosts.size}")
//            } else {
//                Log.d("PostDetail", "Snapshot is null")
//            }
//        }
//    }



    // Function to navigate back to Home activity
    private fun navigateToHome() {
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Function to dynamically add TextViews for skills
    private fun displaySkills(skills: List<String>) {
        val skillContainer: LinearLayout = findViewById(R.id.skillContainer)

        for (skill in skills) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            }
            textView.text = skill
            textView.setBackgroundResource(R.drawable.radio_button_background)
            textView.setTextColor(resources.getColor(R.color.black))
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(com.intuit.ssp.R.dimen._9ssp)
            )
            textView.setPadding(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
            )

            skillContainer.addView(textView)
        }
    }

}
