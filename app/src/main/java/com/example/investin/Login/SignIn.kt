package com.example.investin.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.investin.home.Home
import com.example.investin.R
import com.example.investin.UserInformation
import com.example.investin.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.singh.daman.proprogressviews.DoubleArcProgress

class SignIn : AppCompatActivity() {

    // Late-initialized variables
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var progressBar: DoubleArcProgress
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and set content view
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Initialize the progress bar
        progressBar = binding.progressBar

        // Configure Google Sign In
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //Configures Google Sign-In to request the user's ID token and email.
                .requestIdToken(getString(R.string.default_web_client_id)) // Requests an ID token using the default web client ID.
                .requestEmail()  //Requests the user's email address.
                .build()

        // Initialize GoogleSignInClient with the configured options
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set click listener for the sign-in button
        binding.btSighIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val usernameTextInputLayout = binding.usernameTextInputLayout
            val passwordTextInputLayout = binding.passwordTextInputLayout

            if (email.isNotEmpty() && password.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signInTask ->
                    hideProgressBar()
                    if (signInTask.isSuccessful) {
                        // Check if user information is available in Firestore
                        showProgressBar() // Show progress bar when sign-in process starts
                        checkUserInformation(firebaseAuth.currentUser?.uid ?: "")
                    } else {
                        passwordTextInputLayout.error = "Incorrect email or password."
                    }
                }
            } else {
                if (email.isEmpty()) {
                    usernameTextInputLayout.error = "Username cannot be empty!"
                } else {
                    usernameTextInputLayout.error = null
                }

                if (password.isEmpty()) {
                    passwordTextInputLayout.error = "Password cannot be empty!"
                } else {
                    passwordTextInputLayout.error = null
                }
            }
        }


        // Set click listener for the sign-up text
        binding.tvSignup.setOnClickListener {
            // Navigate to SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Set click listener for the Google sign-in button
        binding?.ivGoogleLogin?.setOnClickListener {
            signInWithGoogle() // Initiates the Google sign-in flow
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }
    }

    // Function to check user information in Firestore
    private fun checkUserInformation(userId: String) {
        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // User information available, move to Home activity
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    // User information not available, request additional details
                    val intent = Intent(this, UserInformation::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                // Handle failures
                Log.e("SignInActivity", "Error checking user information: ${e.message}", e)
            }
    }

    // Function to initiate Google sign-in flow
    private fun signInWithGoogle() {
        val signInIntent =
            googleSignInClient.signInIntent // Gets the intent for the Google Sign-In.
        launcher.launch(signInIntent) // Launches the sign-in intent
    }

    // Activity result launcher for Google sign-in
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResult(task) // Handles the result of Google sign-in
            }
        }

    // Function to handle the result of Google sign-in
    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account) // Updates UI with signed-in account details
            }
        } else {
            // Show error toast for failed sign-in attempt
            Toast.makeText(this, "SignIn Failed, Try Again", Toast.LENGTH_LONG).show()
        }
    }

    // Function to update UI with signed-in account details
    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar() // Shows the progress bar
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val isNewUser = authResult.result?.additionalUserInfo?.isNewUser ?: false
                val user = firebaseAuth.currentUser

                if (isNewUser) {
                    // New user, navigate to UserInformation activity to gather additional information
                    val intent = Intent(this, UserInformation::class.java)
                    startActivity(intent)
                } else {
                    // Existing user, check if user information is available in Firestore
                    checkUserInformation(user?.uid ?: "")
                }
            } else {
                // Show authentication failed toast
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Function to show the progress bar
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    // Function to hide the progress bar
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    // Function to handle back button press
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }
}