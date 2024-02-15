package com.example.investin

import MyHomeAdapter
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.chat.Chat
import com.example.investin.databinding.ActivityHomeBinding
import com.example.investin.login.SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.singh.daman.proprogressviews.DoubleArcProgress

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: DoubleArcProgress
    private lateinit var recyclerView: RecyclerView
    private lateinit var myHomeAdapter: MyHomeAdapter

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private lateinit var textViewName: TextView
    private lateinit var textViewEmail: TextView

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Get current user
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Initialize the progress bar
        progressBar = binding.progressBar

        // Check if the user is still signed in
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            // User is not signed in, navigate to the login screen
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish() // Finish this activity to prevent going back to it
            return
        }

        // Initialize the TextViews
        textViewName = binding.navigationView.getHeaderView(0).findViewById(R.id.textViewName)
        textViewEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.textViewEmail)

        // Retrieve user information from Firestore
        retrieveUserInformation()
        retrieveDisplayNameFromFirestore(currentUser?.uid ?: "")

        binding.ivPost.setOnClickListener {
            val intent = Intent(this, Post::class.java)
            startActivity(intent)
        }

        // Set status bar color only in the Home screen
        setStatusBarColor(R.color.app_color)

        // Setup navigation item selection handling
        setupNavigationItemSelection()

        // Setup profile image click handling
        setupProfileImageClick()

        // Setup bottom navigation handling
        bottomNavigation()

        // RecyclerView calling function
        postRecyclerView()
    }


    private fun retrieveUserInformation() {
        // Check if the current user is not null
        currentUser?.let { user ->
            // Retrieve and set email from Firebase Authentication
            val email = getCurrentUserEmail()
            textViewEmail.text = email

            // Retrieve display name from Firebase Authentication
            val displayName = user.displayName

            // Display the name in the TextView
            if (displayName != null && displayName.isNotEmpty()) {
                // Set username to TextView
                textViewName.text = displayName
            } else {
                // If display name is not available, retrieve it from Firestore
                retrieveDisplayNameFromFirestore(user.uid)
            }
        }
    }

    private fun retrieveDisplayNameFromFirestore(userId: String) {
        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId) // Use the user's UID as the document ID

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                // Check if the document exists
                if (documentSnapshot.exists()) {
                    // Retrieve full name (assuming "name" field contains the full name)
                    val fullName = documentSnapshot.getString("name")

                    // Log retrieved values
                    Log.d("HomeActivity", "Full Name: $fullName")

                    // Set full name to TextView
                    textViewName.text = fullName?.trim()
                } else {
                    // Handle the case when the document does not exist
                    Log.d("HomeActivity", "User document does not exist.")
                }
            }
            .addOnFailureListener { e ->
                // Handle failures
                Log.e("HomeActivity", "Error retrieving user information: ${e.message}", e)
            }
    }



    private fun getCurrentUserEmail(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.email ?: ""
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

    private fun postRecyclerView() {
        recyclerView = findViewById(R.id.rvHome)
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Example data for the RecyclerView
        val dataList = listOf("Pakistan", "UAE", "Iran", "Palestine", "England", "India")

        myHomeAdapter = MyHomeAdapter(dataList)
        recyclerView.adapter = myHomeAdapter

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
                    val intent = Intent(this, AboutUs::class.java)
                    startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setStatusBarColor(colorResId: Int) {
        // Set the system UI visibility to 0 to make the icons light
        window.decorView.systemUiVisibility = 0

        // Set the status bar color to a dark color, such as app_color
        window.statusBarColor = getColor(colorResId)
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