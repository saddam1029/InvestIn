package com.example.investin.chat


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.investin.R
import java.util.Locale


class MyChatAdapter(private val itemList: List<ChatItem>) :
    RecyclerView.Adapter<MyChatAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<ChatItem> = itemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filteredList[position]

        holder.profile?.setImageResource(currentItem.imageResourceId)
        holder.userName?.text = currentItem.userName
        holder.userLastMessage?.text = currentItem.lastMessage

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Message::class.java)
            intent.putExtra("userName", currentItem.userName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (charSequence == null || charSequence.isEmpty()) {
                    filterResults.values = itemList
                } else {
                    val query = charSequence.toString().toLowerCase(Locale.ROOT).trim()
                    val filtered = itemList.filter {
                        it.userName.toLowerCase(Locale.ROOT).contains(query)
                    }
                    filterResults.values = filtered
                }
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults?
            ) {
                filteredList = filterResults?.values as List<ChatItem>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: ImageView? = itemView.findViewById(R.id.ivChatProfile)
        val userName: TextView? = itemView.findViewById(R.id.tvUserName)
        val userLastMessage: TextView? = itemView.findViewById(R.id.tvUserLastMessage)
        val lastTime: TextView? = itemView.findViewById(R.id.tvLastTime)
    }
}



