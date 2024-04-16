package com.example.investin.Advice

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.investin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentsAdapter(var comments: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = comments[position]

        // Bind comment details
        holder.textViewComment.text = currentItem.comment
        holder.textViewTime.text = formatTimestamp(currentItem.timestamp)

        // Bind user details
        holder.textViewName.text = currentItem.name
        holder.textViewRoll.text = currentItem.roll

        loadProfilePicture(currentItem.userId, holder.imageViewPic)

        // Set up "More" functionality within onBindViewHolder
        val maxLinesCollapsed = 2
        var isExpanded = false

        holder.textViewComment.maxLines = maxLinesCollapsed
        holder.textViewComment.ellipsize = TextUtils.TruncateAt.END
        holder.textViewComment.text = currentItem.comment // Set item descriptor

        holder.textViewMore.setOnClickListener {
            if (!isExpanded) {
                holder.textViewComment.maxLines = Integer.MAX_VALUE
                holder.textViewComment.ellipsize = null
                holder.textViewComment.text = currentItem.comment // Ensure the full text is visible
                holder.textViewMore.text = "less"
            } else {
                holder.textViewComment.maxLines = maxLinesCollapsed
                holder.textViewComment.ellipsize = TextUtils.TruncateAt.END
                holder.textViewComment.text = getTrimmedText(
                    currentItem.comment,
                    maxLinesCollapsed,
                    holder.textViewComment
                )
                holder.textViewMore.text = "more"
            }
            isExpanded = !isExpanded
        }

        // Check if the description is in the middle, and adjust layout accordingly
        if (position == comments.size / 2) {
            // Set the description at the start
            holder.textViewComment.layoutParams =
                (holder.textViewComment.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = com.android.car.ui.R.id.parent
                    endToEnd = com.android.car.ui.R.id.parent
                }
        } else {
            // Reset the description layout if not in the middle
            holder.textViewComment.layoutParams =
                (holder.textViewComment.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = com.android.car.ui.R.id.parent
                    endToEnd = com.android.car.ui.R.id.parent // Adjust the end constraint as per your layout
                }
        }
    }

    private fun loadProfilePicture(userId: String, imageView: ImageView) {

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Get current user
            currentUser = FirebaseAuth.getInstance().currentUser!!

        val userDocRef = firebaseFirestore.collection("InvestIn")
            .document("profile")
            .collection(userId)
            .document(userId)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val imageUrl = documentSnapshot.getString("profilePicUrl")
                    imageUrl?.let { url ->
                        Glide.with(imageView.context)
                            .load(url)
                            .placeholder(R.drawable.profile_2)
                            .error(R.drawable.profile_2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView)
                    }
                } else {
                    imageView.setImageResource(R.drawable.profile_2)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CommentAdapter", "Failed to load profile picture: $e")
                imageView.setImageResource(R.drawable.profile_2)
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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
    override fun getItemCount() = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewComment: TextView = itemView.findViewById(R.id.tvComment)
        val textViewTime: TextView = itemView.findViewById(R.id.tvCommentTime)
        val textViewName: TextView = itemView.findViewById(R.id.tvCommentName)
        val textViewRoll: TextView = itemView.findViewById(R.id.tvCommentRoll)
        val textViewMore: TextView = itemView.findViewById(R.id.tvCommentMore)
        val imageViewPic: ImageView = itemView.findViewById(R.id.ivCommentProfile) // Corrected to ImageView
    }
}
