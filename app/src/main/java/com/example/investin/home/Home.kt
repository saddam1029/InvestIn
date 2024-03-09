package com.example.investin.home

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.AboutUs
import com.example.investin.Advice
import com.example.investin.Notification
import com.example.investin.R
import com.example.investin.Search
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
import com.singh.daman.proprogressviews.DoubleArcProgress

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var progressBar: DoubleArcProgress
    private lateinit var myHomeAdapter: MyHomeAdapter

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private lateinit var textViewName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewDateOfBirth: TextView
    private lateinit var textViewNumber: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var textViewRole: TextView
    private lateinit var nevProfile: ImageView

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


        textViewName = binding.navigationView.getHeaderView(0).findViewById(R.id.textViewName)
        textViewEmail = binding.navigationView.getHeaderView(0).findViewById(R.id.textViewEmail)
        nevProfile = binding.navigationView.getHeaderView(0).findViewById(R.id.ivNavProfile)
        textViewAddress = TextView(this)
        textViewGender = TextView(this)
        textViewDateOfBirth= TextView(this)
        textViewNumber= TextView(this)
        textViewRole= TextView(this)

        // Retrieve user information from Firestore
        retrieveUserInformation()
        retrieveDisplayNameFromFirestore(currentUser?.uid ?: "")

        binding.ivPost.setOnClickListener {
            val intent = Intent(this, Post::class.java)
            startActivity(intent)
        }

        nevProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            // Pass user name and email as extras
            intent.putExtra("userName", textViewName.text.toString())
            intent.putExtra("userEmail", textViewEmail.text.toString())
            intent.putExtra("gender", textViewGender.text.toString())
            intent.putExtra("number", textViewNumber.text.toString())
            intent.putExtra("dateOfBirth", textViewDateOfBirth.text.toString())
            intent.putExtra("permanentAddress", textViewAddress.text.toString())
            intent.putExtra("userRole", textViewRole.text.toString())
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
                    val gender = documentSnapshot.getString("gender")
                    val number = documentSnapshot.getString("number")
                    val dateOfBirth = documentSnapshot.getString("dateOfBirth")
                    val address = documentSnapshot.getString("permanentAddress")
                    val role = documentSnapshot.getString("userRole")


                    // Set full name to TextView
                    textViewName.text = fullName?.trim() ?: ""
                    textViewGender.text = gender?.trim() ?: ""
                    textViewNumber.text = number?.trim() ?: ""
                    textViewDateOfBirth.text = dateOfBirth?.trim() ?: ""
                    textViewAddress.text = address?.trim() ?: ""
                    textViewRole.text = role?.trim() ?: ""

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
        binding.rvHome.layoutManager = LinearLayoutManager(this)

        val allPostsCollectionRef = firebaseFirestore.collection("InvestIn").document("posts")
            .collection("all_posts")

        allPostsCollectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                Log.e("HomeActivity", "Error fetching posts: ${exception.message}", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val postList = snapshot.documents.map { document ->
                    document.toObject(PostModel::class.java)
                }.filterNotNull()

                myHomeAdapter = MyHomeAdapter(postList)
                binding.rvHome.adapter = myHomeAdapter
            }
        }
    }



    private fun bottomNavigation() {
        // Set Home selected
        binding.bottomNavigationBar.selectedItemId = R.id.home

        // Perform item selected listener
        binding.bottomNavigationBar.setOnNavigationItemSelectedListener { item ->
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
        binding.ivProfile.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupNavigationItemSelection() {

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
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
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
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