package com.example.investin.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.investin.Home
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
import com.singh.daman.proprogressviews.DoubleArcProgress

class SignIn : AppCompatActivity() {

    // Late-initialized variables
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var progressBar: DoubleArcProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and set content view
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize the progress bar
        progressBar = binding.progressBar

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //Configures Google Sign-In to request the user's ID token and email.
            .requestIdToken(getString(R.string.default_web_client_id)) // Requests an ID token using the default web client ID.
            .requestEmail()  //Requests the user's email address.
            .build()

        // Initialize GoogleSignInClient with the configured options
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set click listener for the sign-in button
        binding.btSighIn.setOnClickListener {
            // Retrieve email and password from input fields
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Check if email and password are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Sign in with email and password through FirebaseAuth
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // On successful sign-in, navigate to UserInformation activity
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                    } else {
                        // Show error toast for incorrect email or password
                        Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                // Show toast for empty fields
                Toast.makeText(this, "Empty fields are not allowed!!!", Toast.LENGTH_LONG).show()
            }
        }

        // Set click listener for the sign-up text
        binding.tvSignup.setOnClickListener {
            // Navigate to SignUp activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Set click listener for the Google sign-in button
        binding?.ivGoogleLogin?.setOnClickListener{
            signInWithGoogle() // Initiates the Google sign-in flow
        }
    }

    // Function to initiate Google sign-in flow
    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent // Gets the intent for the Google Sign-In.
        launcher.launch(signInIntent) // Launches the sign-in intent
    }

    // Activity result launcher for Google sign-in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task) // Handles the result of Google sign-in
        }
    }

    // Function to handle the result of Google sign-in
    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account!=null){
                updateUI(account) // Updates UI with signed-in account details
            }
        }
        else{
            // Show error toast for failed sign-in attempt
            Toast.makeText(this,"SignIn Failed, Try Again", Toast.LENGTH_LONG).show()
        }
    }

    // Function to update UI with signed-in account details
    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar() // Shows the progress bar
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // On successful authentication with Firebase, navigate to UserInformation activity
                val intent = Intent(this, UserInformation::class.java)
                startActivity(intent)
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

    // Function to handle back button press
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }
}
