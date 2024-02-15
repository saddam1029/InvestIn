package com.example.investin.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.investin.Home
import com.example.investin.MainActivity
import com.example.investin.R
import com.example.investin.UserInformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        Handler().postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // User is logged in, check if user information is available in Firestore
            checkUserInformation(currentUser.uid)
        } else {
            // User is not logged in, start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkUserInformation(userId: String) {
        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                // Check if the document exists and contains user information
                if (documentSnapshot.exists()) {
                    // User information available, start Home activity
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    // User information not available, start SignInActivity
                    val intent = Intent(this, SignIn::class.java)
                    startActivity(intent)
                }
                finish()
            }
            .addOnFailureListener { e ->
                // Handle failures
                Log.e("SplashScreen", "Error checking user information: ${e.message}", e)
                // Start SignInActivity on failure
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish()
            }
    }
}


