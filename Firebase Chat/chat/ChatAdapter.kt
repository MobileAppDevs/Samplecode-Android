package com.example.firebasechatapp.example.chatmodule.chat

import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.utils.GlideApp
import com.example.firebasechatapp.example.utils.checkDirectory
import com.example.firebasechatapp.example.utils.createDirectory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.receive_message_view.view.*
import kotlinx.android.synthetic.main.received_message_image_view.view.*
import kotlinx.android.synthetic.main.received_message_video_view.view.*
import kotlinx.android.synthetic.main.sent_message_image_view.view.*
import kotlinx.android.synthetic.main.sent_message_image_view.view.sent_message_image_time
import kotlinx.android.synthetic.main.sent_message_video_view.view.*
import kotlinx.android.synthetic.main.sent_message_view.view.*
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(
    val context: Context,
    private val itemLongClickListener: ItemLongClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messageList = mutableListOf<Message>()
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int): Int {
        val currentUser = mAuth.currentUser!!.uid
        if (messageList[position].getFrom().equals(currentUser)) {
            return when (messageList[position].getType()) {
                "image" -> 2
                "video" -> 4
                else -> 1
            }
            /*  if (messageList[position].getType().equals("image")) {
                  return 2
              }else {
                  return 1
              }
              if(messageList[position].getType().equals("video")){

              }*/

        } else {
            return when (messageList[position].getType()) {
                "image" -> 3
                "video" -> 5
                else -> 0
            }
            /*  if (messageList[position].getType().equals("image"))
                  return 3
              return 0*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == 1) {
            val view = layoutInflater.inflate(R.layout.sent_message_view, parent, false)
            return SentMessageHolder(view)
        }
        if (viewType == 2) {
            val view = layoutInflater.inflate(R.layout.sent_message_image_view, parent, false)
            return SentMessageImageHolder(view)
        }
        if (viewType == 3) {
            val view = layoutInflater.inflate(R.layout.received_message_image_view, parent, false)
            return ReceivedMessageImageHolder(view)
        }
        if (viewType == 4) {
            val view = layoutInflater.inflate(R.layout.sent_message_video_view, parent, false)
            return SentMessageVideoHolder(view)
        }
        if (viewType == 5) {
            val view = layoutInflater.inflate(R.layout.received_message_video_view, parent, false)
            return ReceivedMessageVideoHolder(view)
        }
        val view = layoutInflater.inflate(R.layout.receive_message_view, parent, false)
        return ReceivedMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            1 -> (holder as SentMessageHolder).bind(
                position,
                message,
                itemLongClickListener
            )
            0 -> (holder as ReceivedMessageHolder).bind(
                position,
                message,
                itemLongClickListener
            )
            2 -> (holder as SentMessageImageHolder).bindSentImage(
                position,
                message,
                itemLongClickListener
            )
            3 -> (holder as ReceivedMessageImageHolder).bindReceivedImage(
                position,
                message,
                itemLongClickListener
            )
            4 -> (holder as SentMessageVideoHolder).bindSentVideo(position, message)
            5 -> (holder as ReceivedMessageVideoHolder).bindReceivedVideo(position, message)
        }
    }

    override fun getItemCount(): Int {
        return if (messageList.size == 0) 0 else messageList.size
    }

    //sent message work
    inner class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, message: Message, itemLongClickListener: ItemLongClickListener) {
            val dateString =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))

            itemView.sent_message_time?.text = dateString

            if (message.delete)
                itemView.sent_message?.text = "this message is deleted"
            else
                itemView.sent_message?.text = message.message

            if (message.getSelectedPosition() >= 0) {
                itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.selected_view)
            } else
                itemView.setBackgroundColor(context.resources.getColor(R.color.white))

            itemView.setOnLongClickListener {
                if (!messageList[adapterPosition].getDelete()) {
                    messageList[adapterPosition].setSelectedPosition(adapterPosition)
                    notifyItemChanged(adapterPosition)
                    itemLongClickListener.onclick(position)
                }
                return@setOnLongClickListener false
            }
        }
    }

    inner class SentMessageImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSentImage(
            position: Int,
            message: Message,
            itemLongClickListener: ItemLongClickListener
        ) {
            val dateString =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))
            itemView.sent_message_image_time.text = dateString

            if (checkFileIsExitOrNot(message.messageKey)) {
                val file: String = getFile(message.messageKey)
                showSentImage(imageUri = file)
                itemView.sent_image_progress.visibility = View.GONE
            } else {
                if (message.messageKey != null) {
                    if (checkDirectory()) {
                        downloadImage(message)
                    } else {
                        if (createDirectory()) {
                            downloadImage(message)
                        } else {
                            Toast.makeText(context, "Unable to create folder", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    showSentImageAfterDownload(message.messageKey)
                } else {
                    showSentImage(message.message)
                }
            }
        }

        private fun showSentImage(imageUri: String) {
            GlideApp.with(context).load(imageUri).placeholder(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.placeholder
                )
            ).into(itemView.sent_image)
        }

        private fun showSentImageAfterDownload(imageUri: String) {
            GlideApp.with(context).load(imageUri).placeholder(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.placeholder
                )
            ).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemView.sent_image_progress.visibility = View.GONE
                    return false
                }

            }).into(itemView.sent_image)
        }
    }

    inner class SentMessageVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSentVideo(position: Int, message: Message) {
            val dateString =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))
            itemView.sent_message_video_time.text = dateString

            if (checkFileIsExitOrNot(message.messageKey)) {
                val file: String = getFile(message.messageKey)
                startVideo(file.toUri())
                itemView.sent_image_progress.visibility = View.GONE
            } else {
                if (message.messageKey != null) {
                    if (checkDirectory()) {
                        downloadImage(message)
                    } else {
                        if (createDirectory()) {
                            downloadImage(message)
                        } else {
                            Toast.makeText(context, "Unable to create folder", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    startVideoAfterDownload(message.messageKey)
                } else {
                    startVideo(message.message.toUri())
                }
            }
        }

        private fun startVideoAfterDownload(messageKey: String?) {
            itemView.sent_video.setVideoURI(messageKey?.toUri())
            itemView.sent_video.setOnPreparedListener {
                it.isLooping = true
                itemView.sent_video_progress.visibility = View.GONE
                itemView.sent_video.start()
            }
        }

        private fun startVideo(videoUri: Uri) {
            itemView.sent_video.setVideoURI(videoUri)
            itemView.sent_video.setOnPreparedListener {
                it.isLooping = true
                itemView.sent_video.start()
            }
        }

    }

    inner class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            position: Int,
            message: Message,
            itemLongClickListener: ItemLongClickListener
        ) {
            val receivedMessageDateTime =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))
            itemView.received_message_time?.text = receivedMessageDateTime

            if (message.delete)
                itemView.received_message_view?.text = "this message is deleted"
            else
                itemView.received_message_view?.text = message.message

            if (message.getSelectedPosition() >= 0)
                itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.selected_view)
            else
                itemView.setBackgroundColor(context.resources.getColor(R.color.white))

            itemView.setOnLongClickListener {
                if (!messageList[adapterPosition].getDelete()) {
                    messageList[adapterPosition].setSelectedPosition(adapterPosition)
                    notifyDataSetChanged()
                    itemLongClickListener.onclick(position)
                }
                return@setOnLongClickListener false
            }

        }
    }

    inner class ReceivedMessageImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindReceivedImage(
            position: Int,
            message: Message,
            itemLongClickListener: ItemLongClickListener
        ) {
            val receivedImageDateAndTime =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))
            itemView.received_message_image_time.text = receivedImageDateAndTime

            if (checkFileIsExitOrNot(message.messageKey)) {
                itemView.received_image_progress.visibility = View.GONE
                val file: String = getFile(message.messageKey)
                showReceivedImage(imageUri = file)
            } else {
                if (checkDirectory()) {
                    downloadImage(message)
                } else {
                    if (createDirectory()) {
                        downloadImage(message)
                    } else {
                        Toast.makeText(context, "unable to create folder", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                showReceivedImageAfterDownload(imageUri = message.message)
            }
        }

        private fun showReceivedImage(imageUri: String) {
            GlideApp.with(context).load(imageUri).placeholder(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.placeholder
                )
            ).into(itemView.received_image)
        }

        private fun showReceivedImageAfterDownload(imageUri: String) {
            GlideApp.with(context).load(imageUri).placeholder(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.placeholder
                )
            ).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemView.received_image_progress.visibility = View.GONE
                    return false
                }
            }).into(itemView.received_image)
        }
    }

    inner class ReceivedMessageVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindReceivedVideo(position: Int, message: Message) {
            val receivedMessageDateTime =
                SimpleDateFormat("h:mm a").format(Date(Timestamp(message.timestamp).time))
            itemView.received_message_video_time.text = receivedMessageDateTime
            if (checkFileIsExitOrNot(message.messageKey)) {
                itemView.received_image_progress.visibility = View.GONE
                val file: String = getFile(message.messageKey)
                startVideo(file.toUri())
            }
            else{
                if (checkDirectory()) {
                    downloadImage(message)
                } else {
                    if (createDirectory()) {
                        downloadImage(message)
                    } else {
                        Toast.makeText(context, "unable to create folder", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                showReceivedImageAfterDownload(imageUri = message.message)
            }
            itemView.received_video.setVideoURI(message.message.toUri())
            itemView.received_video.start()
        }

        private fun startVideo(videoUri: Uri) {
            itemView.sent_video.setVideoURI(videoUri)
            itemView.sent_video.setOnPreparedListener {
                it.isLooping = true
                itemView.sent_video.start()
            }
        }

    }

    private fun downloadImage(message: Message) {
        val uri = Uri.parse(message.message)
        val downloadManger =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManger.enqueue(DownloadManager.Request(uri).apply {
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or
                        DownloadManager.Request.NETWORK_WIFI
            )
            setMimeType("*/*")
            if (message.type.equals("image")) {
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "ChatApp/" + message.messageKey + ".jpg"
                )
            } else {
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "ChatApp/" + message.messageKey + ".mp4"
                )
            }
        }
        )
    }

    private fun getFile(messageKey: String?): String {
        return Uri.parse(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "/ChatApp/$messageKey.jpg"
            ).absolutePath.toString()
        ).toString()
    }

    private fun checkFileIsExitOrNot(messageKey: String?): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/ChatApp/$messageKey.jpg"
        )
        return file.exists()
    }

}


