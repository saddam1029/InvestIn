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
import java.util.UUID

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

        firebaseFirestore = FirebaseFirestore.getInstance()
        usersReference = firebaseFirestore.collection("InvestIn")

        // Call the function to set up all CheckBoxes
        setupAllCheckBoxes()

        val mode = intent.getStringExtra("mode")
        if (mode == MODE_UPDATE) {
            // For updating existing post
            val postId = intent.getStringExtra("postId")
            val title = intent.getStringExtra("title")
            val descriptor = intent.getStringExtra("descriptor")
            val location = intent.getStringExtra("location")
            val budget = intent.getStringExtra("budget")
            val skills = intent.getStringArrayListExtra("skills")

            // Pre-fill the form fields with the existing post data
            binding.etTitle.setText(title)
            binding.etDescription.setText(descriptor)
            binding.etLocation.setText(location)
            binding.etBudget.setText(budget)

            // Set up selected skills if available
            skills?.forEach { skill ->
                // Find the corresponding CheckBox and check it
                val checkBox = findCheckBoxBySkill(skill)
                checkBox?.isChecked = true
            }

            // Call the function to set up all CheckBoxes
            setupAllCheckBoxes()
        }

        binding.ivBack.setOnClickListener {
            showConfirmationDialog()
        }

        binding.btPost.setOnClickListener {
            if (mode == MODE_UPDATE) {
                // Update existing post
                updatePost()
            } else {
                // Create new post
                saveUserPost()
            }
        }
    }


    private fun saveUserPost() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val postTitle = binding.etTitle.text.toString()
        val postDescriptor = binding.etDescription.text.toString()
        val postBudget = binding.etBudget.text.toString()
        val postLocation = binding.etLocation.text.toString()

        val postId = UUID.randomUUID().toString()
        // Include the selected skills in your data
        val postData = hashMapOf(
            "postId" to postId, // Add postId to the postData map
            "userId" to userId,
            "title" to postTitle,
            "descriptor" to postDescriptor,
            "budget" to postBudget,
            "location" to postLocation,
            "skills" to selectedSkills.toList(),
            "timestamp" to System.currentTimeMillis()
        )

        val postsCollectionRef = firebaseFirestore.collection("InvestIn")
            .document("posts")
            .collection("all_posts")

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

    private fun updatePost() {
        // Retrieve post ID from intent
        val postId = intent.getStringExtra("postId") ?: return

        // Retrieve other data from form fields
        val postTitle = binding.etTitle.text.toString()
        val postDescriptor = binding.etDescription.text.toString()
        val postBudget = binding.etBudget.text.toString()
        val postLocation = binding.etLocation.text.toString()

        // Update the post data in Firestore
        val postRef = firebaseFirestore.collection("InvestIn")
            .document("posts")
            .collection("all_posts")
            .document(postId)

        // Create a map with updated data
        val updatedData = hashMapOf(
            "title" to postTitle,
            "descriptor" to postDescriptor,
            "budget" to postBudget,
            "location" to postLocation,
            "skills" to selectedSkills.toList(),
            "timestamp" to System.currentTimeMillis()
        )

        // Update the document
        postRef.update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Post updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun findCheckBoxBySkill(skill: String): CheckBox? {
        return when (skill) {
            "Business" -> binding.cbBusiness
            "Communication" -> binding.cbCommunication
            "Creativity" -> binding.cbCreativity
            "Marketing" -> binding.cbMarketing
            "Money management" -> binding.cbMoneyManagement // Corrected to match the skill name
            "Sales" -> binding.cbSales
            "Leadership" -> binding.cbLeadership
            "Positive mindset" -> binding.cbPositiveMindset // Corrected to match the skill name
            "Technical skills" -> binding.cbTechnicalSkills // Corrected to match the skill name
            "Listening" -> binding.cbListening
            "Time management" -> binding.cbTimeManagement // Corrected to match the skill name
            "Strategy" -> binding.cbStrategy
            "Customer Service" -> binding.cbCustomerService
            "Networking" -> binding.cbNetworking
            else -> null
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

    private fun setupAllCheckBoxes() {
        val checkBoxes = listOf(
            binding.cbBusiness,
            binding.cbCommunication,
            binding.cbCreativity,
            binding.cbMarketing,
            binding.cbMoneyManagement,
            binding.cbSales,
            binding.cbLeadership,
            binding.cbPositiveMindset,
            binding.cbTechnicalSkills,
            binding.cbListening,
            binding.cbTimeManagement,
            binding.cbStrategy,
            binding.cbCustomerService,
            binding.cbNetworking
        )

        checkBoxes.forEach { checkBox ->
            setupCheckBox(checkBox)
        }
    }


    private fun showConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to go back?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            // User clicked Yes, navigate back
            shouldShowConfirmationDialog = false
            onBackPressed()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // User clicked Cancel, do nothing
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    companion object {
        const val MODE_UPDATE = "update"
    }

    override fun onBackPressed() {
        if (shouldShowConfirmationDialog) {
            showConfirmationDialog()
        } else {
            super.onBackPressed()
        }
    }
}