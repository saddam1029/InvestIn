package com.example.investin.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.investin.R
import com.example.investin.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btSighIn.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty())
            {
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this,UserInformation::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Incorrect email or password.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Empty Field are not Allow!!!", Toast.LENGTH_LONG).show()
            }
        }


        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        if(firebaseAuth.currentUser != null){
//            val intent = Intent(this, UserInformation::class.java)
//            startActivity(intent)
//        }
//    }

    // when back button press it move out of app
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finish all activities in the task and exit the app
    }

}
