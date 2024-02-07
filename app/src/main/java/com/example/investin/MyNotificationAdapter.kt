import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.NotificationItem
import com.example.investin.R

class MyNotificationAdapter(private val notificationList: List<NotificationItem>) :
    RecyclerView.Adapter<MyNotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]

        // Set data to views in the ViewHolder
        holder.iconImageView.setImageResource(currentItem.iconResId)
        holder.titleTextView.text = currentItem.title
        holder.messageTextView.text = currentItem.message
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    // Update the inner ViewHolder class name
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.ivChatProfile)
        val titleTextView: TextView = itemView.findViewById(R.id.tvUserName)
        val messageTextView: TextView = itemView.findViewById(R.id.tvUserLastMessage)
        val lastTime: TextView = itemView.findViewById(R.id.tvLastTime)
    }
}

