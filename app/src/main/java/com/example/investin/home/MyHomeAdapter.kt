package com.example.investin.home

import android.content.Intent
import android.text.Layout
import android.text.StaticLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyHomeAdapter(private val postList: List<PostModel>) :
    RecyclerView.Adapter<MyHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.tvTitle.text = currentItem.title
        holder.tvLocation.text = currentItem.location
        holder.tvTime.text = formatTimestamp(currentItem.timestamp)// You should replace this with the actual timestamp field

        // Set up "More" functionality within onBindViewHolder
        val maxLinesCollapsed = 2
        var isExpanded = false

        holder.tvDescription.maxLines = maxLinesCollapsed
        holder.tvDescription.ellipsize = TextUtils.TruncateAt.END
        holder.tvDescription.text = currentItem.descriptor // Set item descriptor

        holder.tvMore.setOnClickListener {
            if (!isExpanded) {
                holder.tvDescription.maxLines = Integer.MAX_VALUE
                holder.tvDescription.ellipsize = null
                holder.tvDescription.text = currentItem.descriptor // Ensure the full text is visible
                holder.tvMore.text = "less"
            } else {
                holder.tvDescription.maxLines = maxLinesCollapsed
                holder.tvDescription.ellipsize = TextUtils.TruncateAt.END
                holder.tvDescription.text = getTrimmedText(
                    currentItem.descriptor,
                    maxLinesCollapsed,
                    holder.tvDescription
                )
                holder.tvMore.text = "more"
            }
            isExpanded = !isExpanded
        }


        // Check if the description is in the middle, and adjust layout accordingly
        if (position == postList.size / 2) {
            // Set the description at the start
            holder.tvDescription.layoutParams =
                (holder.tvDescription.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = com.android.car.ui.R.id.parent
                    endToEnd = com.android.car.ui.R.id.parent
                }
        } else {
            // Reset the description layout if not in the middle
            holder.tvDescription.layoutParams =
                (holder.tvDescription.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = com.android.car.ui.R.id.parent
                    endToEnd = com.android.car.ui.R.id.parent // Adjust the end constraint as per your layout
                }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PostDetail::class.java)
            // Pass the post details to the PostDetail activity using intent extras
//            intent.putExtra("postId", currentItem.userId)
            intent.putExtra("title", currentItem.title)
            intent.putExtra("descriptor", currentItem.descriptor)
            intent.putExtra("location", currentItem.location)
            intent.putStringArrayListExtra("skills", ArrayList(currentItem.skills))
            // Add other fields as needed
            context.startActivity(intent)
        }
    }



    private fun getTrimmedText(text: String, maxLines: Int, textView: TextView): String {
        val layout = StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            textView.paint,
            textView.width
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .setMaxLines(maxLines)
            .build()
        val endIndexOfLastLine = layout.getLineEnd(maxLines - 1)
        return text.substring(0, endIndexOfLastLine).trim { it <= ' ' }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }



    override fun getItemCount(): Int {
        return postList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvMore: TextView = itemView.findViewById(R.id.tvMore)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val ivDislike: ImageView = itemView.findViewById(R.id.ivDislike)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
    }
}
