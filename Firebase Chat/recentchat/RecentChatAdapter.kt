package com.example.firebasechatapp.example.chatmodule.recentchat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.chatmodule.chat.ChatActivity
import com.example.firebasechatapp.example.utils.AppConstants
import kotlinx.android.synthetic.main.recent_chat_view.view.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class RecentChatAdapter(val context: Context) :
    RecyclerView.Adapter<RecentChatAdapter.RecentChatHolder>() {

    var recentChatList = mutableListOf<RecentChatModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentChatHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recent_chat_view, parent, false)
        return RecentChatHolder(view)
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {

        val singleRecentChat = recentChatList[position]
        holder.recentChatName?.text = singleRecentChat.name
        val dateString: String

        if (singleRecentChat.last_message.messageKey != null) {
            if (singleRecentChat.last_message.delete) {
                holder.lastMsg?.text = "message is deleted"
            } else{
                when(singleRecentChat.last_message.type){
                    "image" -> holder.lastMsg?.text ="image"
                    "video" -> holder.lastMsg?.text ="video"
                    else ->{
                        holder.lastMsg?.text = singleRecentChat.last_message.message
                    }
                }
            }
            dateString = SimpleDateFormat("h:mm a").format(
                Date(
                    Timestamp(singleRecentChat.last_message.timestamp).time
                )
            )
        } else {
            holder.lastMsg?.text = "new group created"
            dateString =
                SimpleDateFormat("h:mm a").format(
                    Date(
                        Timestamp(singleRecentChat.timestamp).time
                    )
                )
        }
        holder.lastMsgTime?.text = dateString

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(AppConstants.ID, singleRecentChat.id)
            intent.putExtra(AppConstants.NAME, singleRecentChat.name)
            if (singleRecentChat.getIsGroup())
                intent.putExtra(AppConstants.isGroup, singleRecentChat.getIsGroup())
            else
                intent.putExtra(AppConstants.isGroup, false)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return if (recentChatList.size == 0) 0 else recentChatList.size
    }

    inner class RecentChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recentChatName: TextView? = null
        var lastMsg: TextView? = null
        var lastMsgTime: TextView? = null
        init {
            recentChatName = itemView.chated_username
            lastMsg = itemView.lastmessage
            lastMsgTime = itemView.last_message_time
        }
    }

}