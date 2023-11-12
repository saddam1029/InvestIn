package com.example.investin.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.investin.databinding.ActivityUserInformationBinding
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

        // Sign out the current user
        auth.signOut()

        // After signing out, navigate to the login screen (SignInActivity)
        val intent = Intent(this, SignInActivity::class.java)

        // Add flags to clear the back stack, preventing the user from going back to the UserInformationActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Start the SignInActivity
        startActivity(intent)

        // Finish the current activity to prevent going back to it using the back button
        finish()
    }

    // when back button press it move out of app
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }

}