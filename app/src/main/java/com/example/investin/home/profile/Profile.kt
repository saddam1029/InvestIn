package com.example.investin.home.profile

//noinspection SuspiciousImport
import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investin.databinding.ActivityProfileBinding
import com.example.investin.home.Home
import com.example.investin.home.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var userId: String
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    // Get a reference to the root directory of Firebase Storage
    private val storageReference = firebaseStorage.reference
//    private val REQUEST_IMAGE_PICK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize userId (assuming it's obtained from intent extras)
//        userId = intent.getStringExtra("userId")
//            ?: throw IllegalArgumentException("userId not found in intent extras")

        // Initialize RecyclerView and its adapter
        profileAdapter = ProfileAdapter(this) // Pass an empty list initially

        binding.rvProfile.apply {
            layoutManager = LinearLayoutManager(this@Profile)
            adapter = profileAdapter
        }

        // Load and display user's own posts
        loadUserPosts()

        binding.ivProfileBack.setOnClickListener {
            navigateToHome()
        }

        // Call the function to retrieve and set user Name and Role
        retrieveAndSetUserInfo()

//        binding.ivProfilePicSelect.setOnClickListener {
//            // Intent to open gallery
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivityForResult(intent, REQUEST_IMAGE_PICK)
//            } else {
//                // Handle case where no apps can handle the intent
//                Toast.makeText(this, "No app found to select image", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    // Function to upload image to Firebase Storage and store its URL in Firestore
//    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri) {
//        val storageRef: StorageReference = storageReference.child("profile_pics/${userId}.jpg")
//
//        // Convert URI to Bitmap
//        val bitmap: Bitmap =
//            BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImageUri))
//
//        // Compress the bitmap
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        // Upload the compressed bitmap to Firebase Storage
//        val uploadTask = storageRef.putBytes(data)
//        uploadTask.addOnSuccessListener { taskSnapshot ->
//            // Image uploaded successfully, get the download URL
//            storageRef.downloadUrl.addOnSuccessListener { uri ->
//                // Save the download URL in Firestore
//                saveImageUriToFirestore(uri.toString())
//            }
//        }.addOnFailureListener { exception ->
//            // Handle any errors
//            Log.e("ProfileActivity", "Failed to upload image to Firebase Storage: $exception")
//        }
//    }
//
//    // Function to save the image URL in Firestore
//    private fun saveImageUriToFirestore(imageUrl: String) {
//        val userRef = firestoreDB.collection("users").document(userId)
//
//        // Update the 'profilePicUrl' field with the image URL
//        userRef.update("profilePicUrl", imageUrl)
//            .addOnSuccessListener {
//                // Image URL saved successfully
//                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            .addOnFailureListener { e ->
//                // Handle any errors
//                Log.e("ProfileActivity", "Failed to update profile picture URL: $e")
//                Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    // Inside your onActivityResult method
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
//            val selectedImageUri = data?.data ?: return  // Handle if data is null
//
//            // Load the selected image into the ImageView
//            binding.ivProfilePic.setImageURI(selectedImageUri)
//
//            // Upload the selected image to Firebase Storage and store its URL in Firestore
//            uploadImageToFirebaseStorage(selectedImageUri)
//        }
//    }

    private fun retrieveAndSetUserInfo() {
        // Retrieve user name and email from Intent extras
        val userName = intent.getStringExtra("userName")
        val userRole = intent.getStringExtra("userRole")

        binding.tvProfileName.text = userName
        binding.tvProfileRole.text = userRole
    }


    private fun loadUserPosts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postsCollectionRef = FirebaseFirestore.getInstance()
            .collection("InvestIn")
            .document("posts")
            .collection("all_posts")

        // Query to retrieve posts for the current user
        val query = postsCollectionRef.whereEqualTo("userId", userId)

        // Add a snapshot listener to listen for real-time updates
        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                Log.e(TAG, "Error loading posts: ${exception.message}", exception)
                return@addSnapshotListener
            }

            // Clear the existing list of posts
            val userPosts = mutableListOf<PostModel>()

            // Parse the snapshot and add posts to the list
            for (document in snapshot!!) {
                val postId = document.id
                val post = document.toObject(PostModel::class.java).apply {
                    this.postId = postId // Assign postId from document ID
                }
                userPosts.add(post)
            }

            // Update the adapter with the new list of posts
            profileAdapter.updateData(userPosts)
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out) // fade activity when exit
        finish() // Finish the current activity to prevent going back to it
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // Override the back button to navigate to Home activity
        navigateToHome()
    }
}
