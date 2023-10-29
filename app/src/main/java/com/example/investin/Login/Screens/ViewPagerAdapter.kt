package com.example.investin.Login.Screens

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.databinding.PagerItemBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PageViewHolder>() {
    private val pageData = arrayOf("Page 1", "Page 2", "Page 3")

    private var onNextButtonClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pageData[position], position)
    }

    override fun getItemCount() = pageData.size

    inner class PageViewHolder(private val binding: PagerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pageContent: String, position: Int) {
            binding.tvTitle.text = pageContent // Ensure this ID matches the one in your layout
            binding.buttonNext.setOnClickListener {
                onNextButtonClickListener?.invoke(position)

                if (position == 2) {
                    // Navigate to SecondActivity when on the third screen
                    val intent = Intent(binding.root.context, HomeActivity::class.java)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }

    // Optional: Set a callback for the "Next Page" button click
    fun setOnNextButtonClickListener(listener: (Int) -> Unit) {
        onNextButtonClickListener = listener
    }
}
