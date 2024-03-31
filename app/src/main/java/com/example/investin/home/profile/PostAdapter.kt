package com.example.investin.home.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Layout
import android.text.StaticLayout
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import com.example.investin.home.PostDetail
import com.example.investin.home.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val context: Context) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private val postList = mutableListOf<PostModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPostList: List<PostModel>) {
        postList.clear()
        postList.addAll(newPostList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_home, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.tvTitle.text = currentItem.title
        holder.tvLocation.text = currentItem.location
        holder.tvTime.text = formatTimestamp(currentItem.timestamp)

        // Set up "More" functionality within onBindViewHolder
        val maxLinesCollapsed = 2
        var isExpanded = false

        holder.tvDescription.maxLines = maxLinesCollapsed
        holder.tvDescription.ellipsize = TextUtils.TruncateAt.END
        holder.tvDescription.text = currentItem.descriptor

        holder.tvMore.setOnClickListener {
            if (!isExpanded) {
                holder.tvDescription.maxLines = Integer.MAX_VALUE
                holder.tvDescription.ellipsize = null
                holder.tvDescription.text = currentItem.descriptor
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
                    endToEnd = com.android.car.ui.R.id.parent
                }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PostDetail::class.java)
            intent.putExtra("title", currentItem.title)
            intent.putExtra("descriptor", currentItem.descriptor)
            intent.putExtra("location", currentItem.location)
            intent.putExtra("time", formatTimestamp(currentItem.timestamp))
            intent.putStringArrayListExtra("skills", ArrayList(currentItem.skills))
            context.startActivity(intent)
        }

        // Inside onBindViewHolder method of ProfileAdapter
        holder.ivMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.menu_profile_item)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        // Delete the post from Firestore
                        deletePost(currentItem.postId)
                        true
                    }
//                    R.id.menu_edit -> {
//                        // Delete the post from Firestore
//
//                        true
//                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

    }

    private fun deletePost(postId: String) {
        val firestoreDB = FirebaseFirestore.getInstance()
        val postsCollectionRef = firestoreDB.collection("InvestIn").document("posts").collection("all_posts")
        val postRef = postsCollectionRef.document(postId)
        postRef.delete()
            .addOnSuccessListener {
                // Post successfully deleted
                // You can also update the UI here if needed
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e(TAG, "Error deleting post", e)
                // You can also display a message to the user that deletion failed
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
        val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu) // Change to ImageView
        val ivDislike: ImageView = itemView.findViewById(R.id.ivDislike)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
    }

}
