package com.example.investin.home.profile

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.investin.Advice.MyAdviceAdapter
import com.example.investin.chat.AdviceItem
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
    private lateinit var userId: String
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    private lateinit var postAdapter: PostAdapter
    private lateinit var adviceAdapter: AdviceAdapter

    // Get a reference to the root directory of Firebase Storage
    private val storageReference = firebaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize userId (assuming it's obtained from intent extras)
        userId = intent.getStringExtra("userId")
            ?: throw IllegalArgumentException("userId not found in intent extras")

        // Initialize RecyclerViews and their adapters
        postAdapter = PostAdapter(this)
        adviceAdapter = AdviceAdapter(this)

        binding.rvProfile.apply {
            layoutManager = LinearLayoutManager(this@Profile)
            adapter = ConcatAdapter(postAdapter)
        }

        binding.rvProfileAdvice.apply {
            layoutManager = LinearLayoutManager(this@Profile)
            adapter = ConcatAdapter(adviceAdapter)
        }

        // Load user posts and advice
        loadUserPosts()
        loadUserAdvice()

        binding.ivProfileBack.setOnClickListener {
            navigateToHome()
        }

        // Call the function to retrieve and set user Name and Role
        retrieveAndSetUserInfo()

        // Load profile picture if available
        loadProfilePicture()

        binding.ivProfilePicSelect.setOnClickListener {
            // Check if user is signed in before attempting upload
            if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(
                    this,
                    "Please sign in to upload a profile picture",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Open the image picker
            openImagePicker()
        }

        binding.ivBackgroundPicSelect.setOnClickListener {
            // Check if user is signed in before attempting upload
            if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(
                    this,
                    "Please sign in to upload a background picture",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Open the image picker
            openImagePickerForBackground()
        }

    }

    private fun loadProfilePicture() {
        val userDocRef = firestoreDB.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val imageUrl = documentSnapshot.getString("profilePicUrl")
                    imageUrl?.let { url ->
                        // Load the image using Glide and cache it
                        Glide.with(this@Profile)
                            .load(url)
                            .placeholder(com.example.investin.R.drawable.profile_2)  // Display placeholder while loading
                            .error(com.example.investin.R.drawable.profile_2)              // Display error image on failure
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivProfilePic)
                    }


                    val backgroundUrl = documentSnapshot.getString("backgroundPicUrl")
                    backgroundUrl?.let { url ->
                        // Load the background image using Glide and disable caching
                        Glide.with(this@Profile)
                            .load(url)
                            .placeholder(com.example.investin.R.drawable.background_image)  // Display placeholder while loading
                            .error(com.example.investin.R.drawable.background_image)              // Display error image on failure
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivBackground)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Failed to load profile picture: $e")
            }
    }

    override fun onStart() {
        super.onStart()
        // Prefetch the profile picture when the activity starts
        loadProfilePicture()
    }

    // Function to upload image to Firebase Storage and store its URL in Firestore
    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri) {
        val storageRef: StorageReference = storageReference.child("profile_pics/${userId}.jpg")

        // Convert URI to Bitmap
        val bitmap: Bitmap =
            BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImageUri))

        // Compress the bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = storageRef.putBytes(data)
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please sign in to upload a profile picture", Toast.LENGTH_SHORT)
                .show();
            return; // Exit the function if user is not signed in
        }
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL in Firestore
                saveImageUriToFirestore(uri.toString())
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("ProfileActivity", "Failed to upload image to Firebase Storage: $exception")
        }
    }

    private fun uploadImageToFirebaseStorageForBackground(selectedImageUri: Uri) {
        val storageRef: StorageReference = storageReference.child("background_pics/${userId}.jpg")

        // Convert URI to Bitmap
        val bitmap: Bitmap =
            BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImageUri))

        // Compress the bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = storageRef.putBytes(data)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL in Firestore
                saveImageUriToFirestoreForBackground(uri.toString())
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("ProfileActivity", "Failed to upload image to Firebase Storage: $exception")
        }
    }

    // Function to save the image URL in Firestore
    private fun saveImageUriToFirestore(imageUrl: String) {
        val userDocRef = firestoreDB.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        // Update the 'profilePicUrl' field with the image URL
        userDocRef.update("profilePicUrl", imageUrl)
            .addOnSuccessListener {
                // Image URL saved successfully
                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e("ProfileActivity", "Failed to update profile picture URL: $e")
                Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUriToFirestoreForBackground(imageUrl: String) {
        val userDocRef = firestoreDB.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        // Update the 'backgroundPicUrl' field with the image URL
        userDocRef.update("backgroundPicUrl", imageUrl)
            .addOnSuccessListener {
                // Image URL saved successfully
                Toast.makeText(this, "Background picture updated successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e("ProfileActivity", "Failed to update background picture URL: $e")
                Toast.makeText(this, "Failed to update background picture", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    @SuppressLint("IntentReset")
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    @SuppressLint("IntentReset")
    private fun openImagePickerForBackground() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK_BACKGROUND)
    }

    // Inside your onActivityResult method
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                when (requestCode) {
                    REQUEST_IMAGE_PICK -> {
                        // Load the selected image into the profile ImageView
                        binding.ivProfilePic.setImageURI(selectedImageUri)

                        // Upload the selected image to Firebase Storage and store its URL in Firestore
                        uploadImageToFirebaseStorage(selectedImageUri)
                    }

                    REQUEST_IMAGE_PICK_BACKGROUND -> {
                        // Load the selected image into the background ImageView
                        binding.ivBackground.setImageURI(selectedImageUri)

                        // Upload the selected image to Firebase Storage and store its URL in Firestore
                        uploadImageToFirebaseStorageForBackground(selectedImageUri)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_IMAGE_PICK_BACKGROUND = 2
    }

    private fun retrieveAndSetUserInfo() {
        // Retrieve user name and email from Intent extras
        val userName = intent.getStringExtra("userName")
        val userRole = intent.getStringExtra("userRole")

        binding.tvProfileName.text = userName
        binding.tvProfileRole.text = userRole
    }

    // Modify loadUserPosts to update the post adapter
    @SuppressLint("SetTextI18n")
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
            postAdapter.updateData(userPosts)

            // Update the TextView with the total number of user posts
            binding.tvTotalPosts.text = userPosts.size.toString()
        }
    }

    private fun loadUserAdvice() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val allAdviceCollectionRef = FirebaseFirestore.getInstance()
            .collection("InvestIn")
            .document("Advice")
            .collection("all_advice_posts")

        allAdviceCollectionRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val userPostCount = documents.size()

                // Update the TextView with the count
                binding.tvTotalAdvicePosts.text = "Total posts: $userPostCount"

                if (!documents.isEmpty) {
                    val adviceList = documents.mapNotNull { document ->
                        document.toObject(AdviceItem::class.java)
                    }

                    val adviceAdapter = MyAdviceAdapter(adviceList)
                    binding.rvProfileAdvice.adapter = adviceAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdviceActivity", "Error fetching advice: ${exception.message}", exception)
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