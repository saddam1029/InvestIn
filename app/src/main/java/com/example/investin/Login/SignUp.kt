package com.example.investin.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.investin.UserInformation
import com.example.investin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btCreateAccount.setOnClickListener {

//            val fName = binding.etFirstName.text.toString()
//            val lName = binding.etLastName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val cPassword = binding.etConformPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && cPassword.isNotEmpty()) {
                if (password == cPassword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, UserInformation::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
                                    .show()

                            }
                        }

                } else {
                    Toast.makeText(this, "Password is Not Matching", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Empty Field are not Allow!!!", Toast.LENGTH_LONG).show()

            }
        }

        binding.tlSignInWithGoogle.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}
