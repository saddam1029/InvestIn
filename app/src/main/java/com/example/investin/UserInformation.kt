package com.example.investin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.investin.databinding.ActivityUserInformationBinding
import com.example.investin.login.SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class UserInformation : AppCompatActivity() {

    private lateinit var binding: ActivityUserInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Now you can access views through binding
        binding.btLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
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

    // when back button press it move out of app
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }

}