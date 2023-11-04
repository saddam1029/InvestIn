    package com.example.investin.login

    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import androidx.appcompat.app.AppCompatActivity
    import com.example.investin.MainActivity
    import com.example.investin.R

    class SplashScreen : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash_screen)

            // Use a Handler to delay the transition to the ViewPagerActivity
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the splash screen activity
            }, 2000) // Delay for 2 seconds
        }
    }
