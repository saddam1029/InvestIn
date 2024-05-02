package com.example.investin.home.drawer

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import com.example.investin.home.PostDetail
import com.example.investin.home.PostModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyFavoriteAdapter(
    private val favoritePostsList: MutableList<PostModel>,
    private val context: Context
) : RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = favoritePostsList[position]
        holder.tvTitle.text = currentItem.title
        holder.tvLocation.text = currentItem.location
        holder.tvTime.text = formatTimestamp(currentItem.timestamp)

        val context = holder.itemView.context

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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PostDetail::class.java)
            intent.putExtra("postId", currentItem.postId)
            intent.putExtra("title", currentItem.title)
            intent.putExtra("descriptor", currentItem.descriptor)
            intent.putExtra("location", currentItem.location)
            intent.putExtra("budget", currentItem.budget)
            intent.putExtra("userId", currentItem.userId)
            intent.putExtra("time", formatTimestamp(currentItem.timestamp))
            intent.putStringArrayListExtra("skills", ArrayList(currentItem.skills))
            intent.putExtra("fromProfile", true)
            context.startActivity(intent)
        }

        holder.ivMenu.setOnClickListener {
            val bottomSheetView =
                LayoutInflater.from(context).inflate(R.layout.bottom_dialog_favorite_layout, null)
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

            bottomSheetView.findViewById<View>(R.id.layoutRemoveFromFav).setOnClickListener {
                // Remove the post from favorites
                removeFromFavorites(currentItem)
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<View>(R.id.layoutReport).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    // Inside MyFavoriteAdapter

    fun removeFromFavorites(post: PostModel) {
        // Implementation of removeFromFavorites() function
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val currentUserId = user.uid

            if (currentUserId.isNotEmpty()) {
                val favoritesCollectionRef = firebaseFirestore.collection("InvestIn")
                    .document("favorites")
                    .collection(currentUserId)

                // Remove the post from favorites collection
                favoritesCollectionRef.document(post.postId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Post removed from favorites", Toast.LENGTH_SHORT).show()
                        // Remove the post from the list and notify the adapter
                        favoritePostsList.remove(post)
                        notifyDataSetChanged()
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
        return favoritePostsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvMore: TextView = itemView.findViewById(R.id.tvMore)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu)
    }
}
