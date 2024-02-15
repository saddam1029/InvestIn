package com.example.investin

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import com.example.investin.databinding.ActivityPostBinding
import com.example.investin.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Post : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var selectedSkill: String
    private var shouldShowConfirmationDialog = true
    private val selectedSkills = mutableListOf<String>()

    var firebaseFirestore = Firebase.firestore
    private lateinit var usersReference: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and set content view
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ivBack: ImageView = findViewById(R.id.ivBack)

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()
        usersReference = firebaseFirestore.collection("InvestIn")

        // Set up a listener for the RadioGroup to update the selectedSkills list
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId)
            val skill = radioButton.text.toString()

            if (selectedSkills.contains(skill)) {
                selectedSkills.remove(skill)
            } else if (selectedSkills.size < 5) {
                selectedSkills.add(skill)
            }

            // Log the selected skills (optional)
            Log.d("SelectedSkills", selectedSkills.toString())
        }

        ivBack.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btPost.setOnClickListener {
            saveUserPost()
        }
    }

    private fun saveUserPost(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postTitle = binding.etTitle.text.toString()
        val postDescriptor = binding.etDescription.text.toString()
        val postBudget = binding.etBudget.text.toString()
        val postLocation = binding.etLocation.text.toString()

        // Include the selected skills in your data
        val postData = hashMapOf(
            "title" to postTitle,
            "descriptor" to postDescriptor,
            "budget" to postBudget,
            "location" to postLocation,
            "skills" to selectedSkills
        )

        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("post")
            .collection(userId)
            .document()

        // Add a new document with a generated ID
            userDocRef.set(postData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "User information saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Failed to save data
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