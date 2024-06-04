package com.example.investin.home.drawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.example.investin.R
import com.example.investin.databinding.ActivitySearchBinding
import com.example.investin.databinding.ActivitySettingBinding
import com.example.investin.home.Account
import com.example.investin.home.Home
import com.example.investin.login.ChangePassword
import com.example.investin.login.SignIn
import com.example.investin.notification.Notification
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.singh.daman.proprogressviews.DoubleArcProgress

class Setting : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private lateinit var progressBar: DoubleArcProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the progress bar
        progressBar = binding.progressBar

        // Set click listener to navigate back to Home activity
        binding.ivSettingBack.setOnClickListener {
            onBackPressed()
        }

        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")
        val userGender = intent.getStringExtra("gender")
        val userNumber = intent.getStringExtra("number")
        val userDateOfBirth = intent.getStringExtra("dateOfBirth")
        val userAddress = intent.getStringExtra("permanentAddress")
        val userRole = intent.getStringExtra("userRole")

        binding.rlAccount.setOnClickListener {

            val intent = Intent(this, Account::class.java)
            intent.putExtra("userName", userName.toString())
            intent.putExtra("userRole", userRole.toString())
            intent.putExtra("userEmail", userEmail.toString())
            intent.putExtra("gender", userGender.toString())
            intent.putExtra("number", userNumber.toString())
            intent.putExtra("dateOfBirth", userDateOfBirth.toString())
            intent.putExtra("permanentAddress", userAddress.toString())
            startActivity(intent)
        }

        binding.rlNotification.setOnClickListener {

            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
        }

        binding.rlChangePassword.setOnClickListener {

            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }

        binding.rlLogOut.setOnClickListener {
            logout() // Call the logout function here
        }



    }


    private fun logout() {
        // Show confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                // Proceed with logout process
                performLogout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        // Show the progress bar
        showProgressBar()

        // Get the FirebaseAuth instance
        val auth = FirebaseAuth.getInstance()

        // Get the Google Sign In Client
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        // Sign out the Firebase Auth
        auth.signOut()

        // Revoke access to the Google account
        googleSignInClient.revokeAccess().addOnCompleteListener {
            // After revoking, navigate to the login screen (SignInActivity)
            val intent = Intent(this, SignIn::class.java)

            // Add flags to clear the back stack, preventing the user from going back to the UserInformationActivity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Start the SignInActivity
            startActivity(intent)

            // Finish the current activity to prevent going back to it using the back button
            finish()
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }


    // Override the onBackPressed method
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}