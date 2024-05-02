package com.example.investin.Advice

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.investin.R
import com.example.investin.databinding.ActivityAdviceBinding
import com.example.investin.databinding.ActivityAdvicePostBinding
import com.example.investin.databinding.ActivityPostBinding
import com.example.investin.home.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AdvicePost : AppCompatActivity() {

    private lateinit var binding: ActivityAdvicePostBinding
    private var shouldShowConfirmationDialog = true
    private var postId: String? = null // Post ID for updating existing advice

    private lateinit var postsCollectionRef: CollectionReference


    var firebaseFirestore = Firebase.firestore
    private lateinit var usersReference: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvicePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        usersReference = firebaseFirestore.collection("InvestIn")

        // Check if the activity is in update mode
        val mode = intent.getStringExtra("mode")
        if (mode == "update") {
            // Retrieve postId from intent extras
            postId = intent.getStringExtra("postId")
            prepopulateFields() // Prepopulate fields with existing advice data
        }

        binding.ivBack.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btPost.setOnClickListener {
            // Ensure postId is not null before updating advice
            if (postId != null) {
                updateAdvice()
            } else {
                saveUserAdvice()
            }
        }
    }



    private fun updateAdvice() {
        postId?.let { postId ->
            val postTitle = binding.etTitle.text.toString()
            val postDescription = binding.etDescription.text.toString()

            val updateData = hashMapOf(
                "title" to postTitle,
                "description" to postDescription,
                "timestamp" to System.currentTimeMillis()
            )

            val postsCollectionRef = firebaseFirestore.collection("InvestIn")
                .document("Advice")
                .collection("all_advice_posts")

            // Update the existing document with postId
            postsCollectionRef.document(postId)
                .update(updateData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Advice updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating advice: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserAdvice() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postTitle = binding.etTitle.text.toString()
        val postDescription = binding.etDescription.text.toString()

        val postId = UUID.randomUUID().toString() // Generate a unique ID
        // Include the selected skills in your data
        val postData = hashMapOf(
            "postId" to postId, // Add postId to the postData map
            "userId" to userId,
            "title" to postTitle,
            "description" to postDescription,
            "timestamp" to System.currentTimeMillis()
        )

        val postsCollectionRef = firebaseFirestore.collection("InvestIn")
            .document("Advice")
            .collection("all_advice_posts")

        // Set a document with a specific ID
        postsCollectionRef.document(postId).set(postData)
            .addOnSuccessListener {
                Toast.makeText(this, "User post saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Failed to save post
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun prepopulateFields() {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        Log.d("AdvicePost", "Title: $title, Description: $description") // Log the retrieved title and description

        binding.etTitle.setText(title)
        binding.etDescription.setText(description)
    }


    private fun showConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to go back?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            shouldShowConfirmationDialog = false
            navigateBack()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // Do nothing, let the dialog dismiss
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun navigateBack() {
        // Perform your navigation back to the home activity here
        // For example, using Intent
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (shouldShowConfirmationDialog) {
            showConfirmationDialog()
        } else {
            super.onBackPressed()
        }
    }
}