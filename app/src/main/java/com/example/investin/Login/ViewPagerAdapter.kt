package com.example.investin.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import com.example.investin.databinding.PagerItemBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PageViewHolder>() {
    private val pageData = arrayOf(
        PageData("Welcome to 'InvestIn'!", "In a time when jobs are scarce and " +
                "economic \n conditions are tough, 'InvestIn' opens doors \n for ambitious " +
                "entrepreneurs to thrive.", R.drawable.welcome),

        PageData("Bridge the Gap with 'InvestIn'", "Seeking investment or offering support, \n 'InvestIn' " +
                "connects those \n in need with those with means.", R.drawable.bridge_the_gap),

        PageData("Unlock Your Future", "Join 'InvestIn' today, and be a" +
                " part of a \n community where dreams turn into reality.", R.drawable.unlock_your_future)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int){
        holder.bind(pageData[position])
    }

    override fun getItemCount() = pageData.size

    inner class PageViewHolder(private val binding: PagerItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(pageContent: PageData) {
            binding.tvTitle.text = pageContent.title
            binding.tvQuote.text = pageContent.quote
            binding.imageView.setImageResource(pageContent.imageResId)
        }
    }
}

data class PageData(val title: String, val quote: String, val imageResId: Int)
