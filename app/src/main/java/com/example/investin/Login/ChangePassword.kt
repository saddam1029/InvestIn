package com.example.investin.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                binding.emailTextInputLayout.error = "Email cannot be empty!"
            }
        }

        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }



    // Override the onBackPressed method
    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
