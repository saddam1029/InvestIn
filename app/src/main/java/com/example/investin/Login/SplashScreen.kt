    package com.example.investin.login

    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import androidx.appcompat.app.AppCompatActivity
    import com.example.investin.MainActivity
    import com.example.investin.R
    import com.example.investin.UserInformation
    import com.google.firebase.auth.FirebaseAuth

    class SplashScreen : AppCompatActivity() {
        private lateinit var firebaseAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
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
                val intent = Intent(this, UserInformation::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, start MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            finish() // Close the splash screen activity
        }
    }

