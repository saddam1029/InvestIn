package com.example.investin.home

import com.google.firebase.firestore.FirebaseFirestore

// UserRepository.kt
class UserRepository(private val firebaseFirestore: FirebaseFirestore) {

    fun getUserInfo(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId) // Use the user's UID as the document ID

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                // Check if the document exists
                if (documentSnapshot.exists()) {
                    // Retrieve user information
                    val fullName = documentSnapshot.getString("name")
                    val gender = documentSnapshot.getString("gender")
                    val number = documentSnapshot.getString("number")
                    val dateOfBirth = documentSnapshot.getString("dateOfBirth")
                    val address = documentSnapshot.getString("permanentAddress")
                    val role = documentSnapshot.getString("userRole")

                    // Create User object
                    val user = User(fullName, gender, number, dateOfBirth, address, role)

                    // Call the onSuccess callback with the retrieved user information
                    onSuccess(user)
                } else {
                    // Handle the case when the document does not exist
                    onFailure(Exception("User document does not exist."))
                }
            }
            .addOnFailureListener { e ->
                // Call the onFailure callback with the exception
                onFailure(e)
            }
    }
}

// User.kt
data class User(
    val fullName: String? = null,
    val gender: String? = null,
    val number: String? = null,
    val dateOfBirth: String? = null,
    val address: String? = null,
    val role: String? = null
)
