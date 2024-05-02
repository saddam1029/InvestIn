package com.example.investin.home

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.text.StaticLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyHomeAdapter(private val postList: List<PostModel>, private val context: Context) :
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
                // Handle report action
                bottomSheetDialog.dismiss()
            }

            // Show the bottom sheet dialog
            bottomSheetDialog.show()
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
        val ivDislike: ImageView = itemView.findViewById(R.id.ivDislike)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu)
    }
}
