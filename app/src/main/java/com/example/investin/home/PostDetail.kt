package com.example.investin.home

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.R
import com.example.investin.databinding.ActivityPostDetailBinding

class PostDetail : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val title = intent.getStringExtra("title")
        val descriptor = intent.getStringExtra("descriptor")
        val location = intent.getStringExtra("location")
        val skills = intent.getStringArrayListExtra("skills")
        val time = intent.getStringExtra("time")
        val budget = intent.getStringExtra("budget")



        // Set data to TextViews
        binding.tvTitle.text = title
        binding.tvDescription.text = descriptor
        binding.tvLocation.text = location
        binding.tvTime.text = time
        binding.tvBudget.text = budget

        // Dynamically add TextViews for skills
        skills?.let { displaySkills(it) }

        // Set click listener to navigate back to Home activity
        binding.ivBack.setOnClickListener {
            navigateToHome()
        }
    }

    // Function to navigate back to Home activity
    private fun navigateToHome() {
        finish() // Finish the current activity to navigate back
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Function to dynamically add TextViews for skills
    private fun displaySkills(skills: List<String>) {
        val skillContainer: LinearLayout = findViewById(R.id.skillContainer)

        for (skill in skills) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            }
            textView.text = skill
            textView.setBackgroundResource(R.drawable.radio_button_background)
            textView.setTextColor(resources.getColor(R.color.black))
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(com.intuit.ssp.R.dimen._9ssp)
            )
            textView.setPadding(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
            )

            skillContainer.addView(textView)
        }
    }







}
