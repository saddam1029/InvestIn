package com.example.investin.Advice

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    var firebaseFirestore = Firebase.firestore
    private lateinit var usersReference: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvicePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        usersReference = firebaseFirestore.collection("InvestIn")

        binding.ivBack.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btPost.setOnClickListener {
            saveUserAdvice()
        }
    }

    private fun saveUserAdvice() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postTitle = binding.etTitle.text.toString()
        val postDescription = binding.etDescription.text.toString()

        val postId = UUID.randomUUID().toString()
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

        // Add a new document with a generated ID
        postsCollectionRef.add(postData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "User post saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Failed to save post
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to go back?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            // User clicked Yes, navigate back
            shouldShowConfirmationDialog = false
            navigateBack()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // User clicked Cancel, do nothing
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