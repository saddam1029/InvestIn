package com.example.investin.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.investin.R
import com.example.investin.databinding.ActivityHomeBinding
import com.example.investin.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Call the function to retrieve and set user information
        retrieveAndSetUserInfo()

        binding.ivBackProfile.setOnClickListener {
            navigateToHome()
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

        binding.tvProfileName.text = userName
        binding.tvProfileEmail.text = userEmail
        binding.tvProfileGender.text = userGender
        binding.tvProfileNumber.text = userNumber
        binding.tvProfileDOB.text = userDateOfBirth
        binding.tvProfileAddress.text = userAddress
        binding.tvProfileRole.text = userRole
    }

    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // fade activity when exit
        finish() // Finish the current activity to prevent going back to it
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Override the back button to navigate to Home activity
        navigateToHome()
    }
}
