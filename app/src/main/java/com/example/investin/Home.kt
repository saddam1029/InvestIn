package com.example.investin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.investin.databinding.ActivityHomeBinding
import com.example.investin.login.SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.singh.daman.proprogressviews.DoubleArcProgress

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: DoubleArcProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the progress bar
        progressBar = binding.progressBar

        // Setup profile image click handling
        setupProfileImageClick()

        // Setup navigation item selection handling
        setupNavigationItemSelection()

        // Setup bottom navigation handling
        bottomNavigation()

    }

    private fun logout() {
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

    private fun bottomNavigation() {
        // Initialize and assign variable
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.home

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.message -> {
                    startActivity(Intent(applicationContext, Chat::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.home -> true
                R.id.advice -> {
                    startActivity(Intent(applicationContext, Advice::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.notification -> {
                    startActivity(Intent(applicationContext, Notification::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.search -> {
                    startActivity(Intent(applicationContext, Search::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupProfileImageClick() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val ivProfile = findViewById<ImageView>(R.id.ivProfile)

        ivProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    private fun setupNavigationItemSelection() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account -> {
                    // Handle Account item click
                }
                R.id.favorite -> {
                    // Handle Favorite item click
                }
                R.id.setting -> {
                    // Handle Setting item click
                }
                R.id.contact -> {
                    // Handle Setting item click
                }
                R.id.share -> {
                    // Handle Setting item click
                }
                R.id.about_us -> {
                    // Handle Setting item click
                }
                R.id.help -> {
                    // Handle Setting item click
                }
                R.id.logOut -> {
                    logout() // Call the logout function here
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }

            true
        }
    }

    // Function to show the progress bar
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    // when back button press it move out of app
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }

}