package com.example.investin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.investin.databinding.ActivityAdminHomeBinding
import eightbitlab.com.blurview.RenderScriptBlur

class AdminHome : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBlurView()

        binding.toolbar4.setOnClickListener {
            refreshPage()
        }
    }

    private fun setupBlurView() {
        val decorView = window.decorView.rootView
        val rootView = decorView.findViewById<View>(android.R.id.content)
        val windowBackground = window.decorView.background

        binding.blurView.setupWith(rootView as ViewGroup)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(10f)
    }

    private fun showProgressBar() {
        binding.blurView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.blurView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun refreshPage() {
        showProgressBar()

        binding.root.postDelayed({
            hideProgressBar()
        }, 2000) // 2 seconds delay
    }
}
