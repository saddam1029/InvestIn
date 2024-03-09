package com.example.investin.home

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.example.investin.R
import com.example.investin.databinding.ActivityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Post : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private var shouldShowConfirmationDialog = true
    private val selectedSkills = mutableSetOf<String>()

    var firebaseFirestore = Firebase.firestore
    private lateinit var usersReference: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ivBack: ImageView = findViewById(R.id.ivBack)

        firebaseFirestore = FirebaseFirestore.getInstance()
        usersReference = firebaseFirestore.collection("InvestIn")


        // Call the setup function for each CheckBox
        setupCheckBox(binding.cbBusiness)
        setupCheckBox(binding.cbCommunication)
        setupCheckBox(binding.cbCreativity)
        setupCheckBox(binding.cbMarketing)
        setupCheckBox(binding.cbMoneyManagement)
        setupCheckBox(binding.cbSales)
        setupCheckBox(binding.cbLeadership)
        setupCheckBox(binding.cbPositiveMindset)
        setupCheckBox(binding.cbTechnicalSkills)
        setupCheckBox(binding.cbListening)
        setupCheckBox(binding.cbTimeManagement)
        setupCheckBox(binding.cbStrategy)
        setupCheckBox(binding.cbCustomerService)
        setupCheckBox(binding.rbNetworking)

        ivBack.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btPost.setOnClickListener {
            saveUserPost()
        }
    }

    private fun saveUserPost() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postTitle = binding.etTitle.text.toString()
        val postDescriptor = binding.etDescription.text.toString()
        val postBudget = binding.etBudget.text.toString()
        val postLocation = binding.etLocation.text.toString()

        // Include the selected skills in your data
        val postData = hashMapOf(
            "userId" to userId,
            "title" to postTitle,
            "descriptor" to postDescriptor,
            "budget" to postBudget,
            "location" to postLocation,
            "skills" to selectedSkills.toList(), // Convert Set to List
            "timestamp" to System.currentTimeMillis() // Add timestamp
        )

        val postsCollectionRef = firebaseFirestore.collection("InvestIn")
            .document("posts")
            .collection("all_posts")

        // Add a new document with a generated ID
        postsCollectionRef.add(postData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "User post saved!", Toast.LENGTH_SHORT).show()
                // Optionally, you can navigate to the post detail screen here
            }
            .addOnFailureListener { e ->
                // Failed to save post
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupCheckBox(checkBox: CheckBox) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val skill = checkBox.text.toString()
            if (isChecked) {
                selectedSkills.add(skill)
            } else {
                selectedSkills.remove(skill)
            }
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