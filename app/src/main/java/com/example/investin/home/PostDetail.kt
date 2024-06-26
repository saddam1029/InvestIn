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
import android.widget.Toast
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
        val title = intent.getStringExtra("title")
        val descriptor = intent.getStringExtra("descriptor")
        val location = intent.getStringExtra("location")
        val skills = intent.getStringArrayListExtra("skills")
        val time = intent.getStringExtra("time")
        val budget = intent.getStringExtra("budget")
        val userId = intent.getStringExtra("userId") ?: ""


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

        // Initially set the total post count text to a loading message
        binding.tvTotalJobPost.text = "Loading..."


//       Count the number of user posts
        getUserPostCount(userId)

        val fromProfile = intent.getBooleanExtra("fromProfile", false) // Retrieve the flag

        // Hide the ImageView when opened from profile and it's the user's own post
        if (fromProfile || currentUser?.uid == userId) {
            binding.ivInvest.visibility = View.GONE
        } else {
            binding.ivInvest.visibility = View.VISIBLE
        }

        // Set click listener for the ivInvest image
        binding.ivInvest.setOnClickListener {
            sendInvitation(userId)
        }

    }

    // Function to send an invitation
    private fun sendInvitation(postOwnerId: String) {
        currentUser?.uid?.let { senderId ->
            val userDocRef = firestoreDB.collection("InvestIn")
                .document("profile")
                .collection(senderId)
                .document(senderId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val senderName = documentSnapshot.getString("name") ?: "Unknown"
                    val senderProfileImage = documentSnapshot.getString("profilePicUrl") ?: ""

                    val invitation = hashMapOf(
                        "fromUserId" to senderId,
                        "toUserId" to postOwnerId,
                        "title" to binding.tvTitle.text.toString(),
                        "timestamp" to System.currentTimeMillis(),
                        "senderName" to senderName,
                        "senderProfileImage" to senderProfileImage
                    )

                    firestoreDB.collection("InvestIn")
                        .document("invitations")
                        .collection("all_invitations")
                        .add(invitation)
                        .addOnSuccessListener { documentReference ->
                            Log.d("PostDetail", "Invitation sent successfully")
                            Toast.makeText(this, "Invitation sent successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("PostDetail", "Error sending invitation", e)
                            Toast.makeText(this, "Error sending invitation", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("PostDetail", "Error fetching sender details", e)
                    Toast.makeText(this, "Error fetching sender details", Toast.LENGTH_SHORT).show()
                }
        }
    }




    @SuppressLint("SetTextI18n")
    private fun getUserPostCount(userId: String) {
        // Perform query to count the user's posts
        firestoreDB.collection("InvestIn")
            .document("posts")
            .collection("all_posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Get the count of user's posts
                val userPostsCount = querySnapshot.size()

                // Set the text to display the count of user's posts
                binding.tvTotalJobPost.text = "Total posts by this user: $userPostsCount"
            }
            .addOnFailureListener { e ->
                // Handle failure
                // Set an error message or retry option if needed
                binding.tvTotalJobPost.text = "Failed to load posts count"
            }
    }

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