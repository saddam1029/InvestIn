package com.example.investin.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Layout
import android.text.StaticLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyHomeAdapter(private val postList: List<PostModel>, private val context: Context) :
    RecyclerView.Adapter<MyHomeAdapter.ViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()


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

            intent.putExtra("postId", currentItem.postId)
            intent.putExtra("title", currentItem.title)
            intent.putExtra("descriptor", currentItem.descriptor)
            intent.putExtra("location", currentItem.location)
            intent.putExtra("budget", currentItem.budget)
            intent.putExtra("userId", currentItem.userId)
            intent.putExtra("time", formatTimestamp(currentItem.timestamp))
            intent.putStringArrayListExtra("skills", ArrayList(currentItem.skills))
            // Add other fields as needed
            context.startActivity(intent)
        }


        holder.ivMenu.setOnClickListener { view ->
            // Use the context associated with the item view
            val context = holder.itemView.context

            // Inflate the bottom sheet menu layout
            val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_dialog_home_layout, null)

            // Create a BottomSheetDialog and set the view
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.setContentView(bottomSheetView)

            // Set animations
            bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

            // Handle click events for menu items
            bottomSheetView.findViewById<View>(R.id.layoutAddToFav).setOnClickListener {
                val post = postList[position] // Get the post data
                addToFavorites(post) // Add post to favorites
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<View>(R.id.layoutReport).setOnClickListener {

                showReportConfirmationDialog(currentItem.postId, holder.tvReport)


                bottomSheetDialog.dismiss()
            }

            // Show the bottom sheet dialog
            bottomSheetDialog.show()
        }

        // Check if the post is liked by the current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val reactionRef = FirebaseFirestore.getInstance().collection("InvestIn")
                .document("post_reaction").collection(currentItem.postId)
            reactionRef.document(userId).get()
                .addOnSuccessListener { userReactionSnapshot ->
                    val hasReacted = userReactionSnapshot.exists()
                    if (hasReacted) {
                        // User has already liked the post, set the like image to red
                        holder.ivLike.setImageResource(R.drawable.like_red)
                    } else {
                        // User hasn't liked the post, set the like image to default
                        holder.ivLike.setImageResource(R.drawable.like)
                    }
                }
        }

        fetchLikeCount(currentItem.postId, holder.tvLike)

        // Set up like button click listener
        holder.ivLike.setOnClickListener {
            updateReaction(true, currentItem.postId, holder)
        }

        fetchTotalReportCount(currentItem.postId, holder)


    }

    private fun reportPost(postId: String, tvReport: TextView) {
        // Reference to the collection of reports for the specific post
        val reportRef = firestore.collection("InvestIn").document("report").collection(postId)

        // Reference to the document that holds the total report count for the post
        val reportCountRef = firestore.collection("InvestIn").document("report").collection(postId).document("count")

        // Get the current user's ID
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Check if the user has already reported this post
        reportRef.document(currentUserID).get().addOnSuccessListener { userSnapshot ->
            if (!userSnapshot.exists()) { // User has not reported this post yet
                // Increment the total report count for the post
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(reportCountRef)
                    val currentCount = snapshot.getLong("count") ?: 0
                    val newCount = currentCount + 1

                    transaction.set(reportCountRef, mapOf("count" to newCount))

                    // Set a flag to mark that the user has reported this post
                    transaction.set(reportRef.document(currentUserID), mapOf("reported" to true))
                }.addOnSuccessListener {
                    // Update the report count TextView with the total count
                    Toast.makeText(context, "Reported successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to report", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "You have already reported this post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReportConfirmationDialog(postId: String, tvReport: TextView) {
        AlertDialog.Builder(context)
            .setTitle("Report Post")
            .setMessage("Are you sure you want to report this post?")
            .setPositiveButton("Yes") { dialog, which ->
                reportPost(postId, tvReport)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun fetchTotalReportCount(postId: String, holder: ViewHolder) {
        val reportCountRef = firestore.collection("InvestIn")
            .document("report")
            .collection(postId)
            .document("count")

        reportCountRef.get()
            .addOnSuccessListener { reportSnapshot ->
                val reportCount = reportSnapshot.getLong("count") ?: 0
                holder.tvReport.text = reportCount.toString()
            }
            .addOnFailureListener { e ->
                // Handle failure to fetch report count
            }
    }
    private fun fetchLikeCount(postId: String, tvLike: TextView) {
        val reactionRef = firestore.collection("InvestIn")
            .document("post_reaction")
            .collection(postId)
            .document("like")

        reactionRef.get()
            .addOnSuccessListener { likeSnapshot ->
                val likeCount = likeSnapshot.getLong("count") ?: 0
                tvLike.text = likeCount.toString()
            }
            .addOnFailureListener { e ->
            }
    }

    private fun updateReaction(isLike: Boolean, postId: String, holder: ViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val post = postList[position]
            val reactionRef = FirebaseFirestore.getInstance().collection("InvestIn")
                .document("post_reaction").collection(postId)

            // Check if the user has already reacted to the post
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val userId = user.uid
                reactionRef.document(userId).get()
                    .addOnSuccessListener { userReactionSnapshot ->
                        val hasReacted = userReactionSnapshot.exists()
                        if (hasReacted) {
                            // User has already reacted, decrement the count and update the database
                            removeLike(reactionRef, userId, holder)
                            holder.ivLike.setImageResource(R.drawable.like) // Change back to default image
                        } else {
                            // User hasn't reacted yet, increment the count and update the database
                            addLike(isLike, reactionRef, userId, holder)
                            holder.ivLike.setImageResource(R.drawable.like_red) // Change to liked image
                        }
                    }
            }
        }
    }

    private fun addLike(isLike: Boolean, reactionRef: CollectionReference, userId: String, holder: ViewHolder) {
        // Update like count in the UI
        holder.tvLike.text = (holder.tvLike.text.toString().toInt() + 1).toString()

        // Play the like animation
        val scaleAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.like_scale)
        holder.ivLike.startAnimation(scaleAnimation)

        // Update the like count in the database
        val likeData = hashMapOf(
            "isLike" to true // Set the like status
        )
        reactionRef.document(userId).set(likeData)

        // Increment like count in the database
        reactionRef.document("like").get()
            .addOnSuccessListener { likeSnapshot ->
                var likeCount = likeSnapshot.getLong("count") ?: 0
                likeCount++
                reactionRef.document("like").set(mapOf("count" to likeCount))
            }
    }

    private fun removeLike(reactionRef: CollectionReference, userId: String, holder: ViewHolder) {
        // Update like count in the UI
        holder.tvLike.text = (holder.tvLike.text.toString().toInt() - 1).toString()

        // Remove the like status from the database
        reactionRef.document(userId).delete()

        // Decrement like count in the database
        reactionRef.document("like").get()
            .addOnSuccessListener { likeSnapshot ->
                var likeCount = likeSnapshot.getLong("count") ?: 0
                likeCount--
                reactionRef.document("like").set(mapOf("count" to likeCount))
            }
    }


    private fun addToFavorites(post: PostModel) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val currentUserId = user.uid

            if (currentUserId.isNotEmpty()) {
                val favoritesCollectionRef = firebaseFirestore.collection("InvestIn")
                    .document("favorites")
                    .collection(currentUserId)

                // Check if the post is already saved
                favoritesCollectionRef.document(post.postId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Post already saved
                            Toast.makeText(context, "Post is already saved", Toast.LENGTH_SHORT).show()
                        } else {
                            // Post not saved, add it to favorites
                            favoritesCollectionRef.document(post.postId)
                                .set(post)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Post added to favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "User ID is empty", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
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
        val now = Calendar.getInstance()
        val time = Calendar.getInstance().apply { timeInMillis = timestamp }

        val diff = now.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR)

        return when (diff) {
            0 -> { // Present day
                val hours = now.get(Calendar.HOUR_OF_DAY)
                if (hours == 0) "12 AM" else if (hours < 12) "$hours AM" else if (hours == 12) "12 PM" else "${hours - 12} PM"
            }
            1 -> "Yesterday" // Yesterday
            in 2..6 -> "This week" // Within a week
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(time.time) // Before this month
        }
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

        val tvLike: TextView = itemView.findViewById(R.id.tvLike)

        val tvReport: TextView = itemView.findViewById(R.id.tvReport)

        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)

        val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu)
    }
}
