    package com.example.investin.login

    import android.annotation.SuppressLint
    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.ContextCompat
    import com.example.investin.Home
    import com.example.investin.MainActivity
    import com.example.investin.R
    import com.example.investin.UserInformation
    import com.google.firebase.auth.FirebaseAuth

    @SuppressLint("CustomSplashScreen")
    class SplashScreen : AppCompatActivity() {
        private lateinit var firebaseAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Set background color here
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

            setContentView(R.layout.activity_splash_screen)


            firebaseAuth = FirebaseAuth.getInstance()

            // Use a Handler to delay the transition to the next activity
            Handler().postDelayed({
                checkUserStatus()
            }, 2000) // Delay for 2 seconds
        }

        private fun checkUserStatus() {
            val currentUser = firebaseAuth.currentUser

            if (currentUser != null) {
                // User is logged in, start UserInformationActivity
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, start MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            finish() // Close the splash screen activity
        }
    }

