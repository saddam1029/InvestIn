package com.example.investin.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.investin.R
import com.example.investin.databinding.ActivityAccountBinding
import com.example.investin.databinding.ActivityProfileBinding
import com.example.investin.home.drawer.Setting
import com.example.investin.login.ChangePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Account : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser
    private var isImageLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Get current user
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Call the function to retrieve and set user information
        retrieveAndSetUserInfo()

        binding.ivBackProfile.setOnClickListener {
            onBackPressed()

        }

        binding.ivSetting.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }

        loadProfilePicture()
    }



    override fun onStart() {
        super.onStart()
        if (!isImageLoaded) {
            loadProfilePicture()
        }
    }

    private fun loadProfilePicture() {
        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(currentUser?.uid ?: "")
            .document(currentUser?.uid ?: "")

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val imageUrl = documentSnapshot.getString("profilePicUrl")
                    imageUrl?.let { url ->
                        Glide.with(this@Account)
                            .load(url)
                            .placeholder(R.drawable.profile_2)
                            .error(R.drawable.profile_2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivAccountPic)
                        isImageLoaded = true
                    }
                } else {
                    binding.ivAccountPic.setImageResource(R.drawable.profile_2)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Failed to load profile picture: $e")
                binding.ivAccountPic.setImageResource(R.drawable.madara)
            }
    }



        private fun retrieveAndSetUserInfo() {
            // Retrieve user name and email from Intent extras
            val userName = intent.getStringExtra("userName")
            val userEmail = intent.getStringExtra("userEmail")
            val userGender = intent.getStringExtra("gender")
            val userNumber = intent.getStringExtra("number")
            val userDateOfBirth = intent.getStringExtra("dateOfBirth")
            val userAddress = intent.getStringExtra("permanentAddress")
            val userRole = intent.getStringExtra("userRole")

            binding.tvAccountName.text = userName
            binding.tvAccountEmail.text = userEmail
            binding.tvAccountGender.text = userGender
            binding.tvAccountNumber.text = userNumber
            binding.tvAccountDOB.text = userDateOfBirth
            binding.tvAccountAddress.text = userAddress
            binding.tvAccountRole.text = userRole
        }

//    private fun navigateToHome() {
//        val intent = Intent(this, Home::class.java)
//        startActivity(intent)
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // fade activity when exit
//        finish() // Finish the current activity to prevent going back to it
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}