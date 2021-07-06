package com.example.firebasechatapp.example.chatmodule.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.firebasechatapp.R
import com.example.firebasechatapp.example.base.BaseActivity
import com.example.firebasechatapp.example.utils.AppConstants
import com.example.firebasechatapp.example.utils.checkDirectory
import com.example.firebasechatapp.example.utils.createDirectory
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.io.FileOutputStream

class ChatActivity : BaseActivity(), ChatView, ItemLongClickListener {

    lateinit var chatPresenter: ChatPresenter<ChatView>
    lateinit var mChatUserId: String
    var mCurrentUserId: String? = null
    var isGroup: Boolean? = null
    lateinit var mChatUserName: String
    lateinit var chatAdapter: ChatAdapter
    var messageses = mutableListOf<Message>()
    lateinit var outputStream: FileOutputStream

    //image related
    private val PICK_IMAGE_REQUEST: Int by lazy { 4 }
    private val REQUEST_TAKE_GALLERY_VIDEO: Int by lazy { 5 }
    private var imageUri: Uri? = null
    private var imageArrayList: ArrayList<Uri> = ArrayList()
    private val READ_WRITE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatPresenter = ChatPresenter()

        //if the user clicks of group mchatUserId will be equal to group id
        // if user clicks on a single user the mChatUserId will be equal to the that user id
        isGroup = intent.getBooleanExtra(AppConstants.isGroup, false)
        mChatUserId = intent.getStringExtra(AppConstants.ID).toString()
        mChatUserName = intent.getStringExtra(AppConstants.NAME).toString()

        mCurrentUserId = firebaseAuth.currentUser!!.uid

        chatPresenter.onAttach(this)

        setUpMessageRecyclerView()

        send_button.setOnClickListener {
            val message = tv_message.text.toString().trim()
            if (isInternetAvailable() && message.isNotEmpty()) {
                chatPresenter.sendMessage(
                    message,
                    mChatUserId,
                    mChatUserName,
                    isGroup!!,
                    "text",
                    System.currentTimeMillis()
                )
                tv_message.setText("")
            } else
                showToast("check Internet connection")
        }

        gallary_open.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

            ) {
                selectImage()
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), READ_WRITE
                )
            }

        }

        videoOpen.setOnClickListener {
            if (isInternetAvailable()) {
                val intent = Intent()
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Video"),
                    REQUEST_TAKE_GALLERY_VIDEO
                )
            }
            else{
                showInternetErrorMessage()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_WRITE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null
        ) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                var singleImage = 0
                while (singleImage < count) {
                    val imageUri = data.clipData!!.getItemAt(singleImage).uri
                    imageArrayList.add(imageUri)
                    singleImage++
                }
                if (isInternetAvailable()) {
                    for (singleImageUri in imageArrayList) {
                        val messageData = Message().apply {
                            message = singleImageUri.toString()
                            type = "image"
                            from = firebaseAuth.currentUser!!.uid
                            timestamp = System.currentTimeMillis()
                            messageKey = null
                            delete = false
                        }
                        chatAdapter.messageList.add(messageData)
                        chatAdapter.notifyDataSetChanged()
                        message_recyclerview.scrollToPosition(chatAdapter.messageList.size - 1)
                        chatPresenter.uploadImage(messageData)
                    }
                } else {
                    showInternetErrorMessage()
                }
            } else {
                if (isInternetAvailable()) {
                    val imageUri = data.data
                    val messageData = Message().apply {
                        message = imageUri.toString()
                        type = "image"
                        from = firebaseAuth.currentUser!!.uid
                        timestamp = System.currentTimeMillis()
                        messageKey = null
                        delete = false
                    }
                    chatAdapter.messageList.add(messageData)
                    chatAdapter.notifyDataSetChanged()
                    message_recyclerview.scrollToPosition(chatAdapter.messageList.size - 1)
                    chatPresenter.uploadImage(messageData)
                } else {
                    showInternetErrorMessage()
                }
            }
        }

        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK && data != null) {
            if (isInternetAvailable()) {
                val imageUri = data.data
                val messageData = Message().apply {
                    message = imageUri.toString()
                    type = "video"
                    from = firebaseAuth.currentUser!!.uid
                    timestamp = System.currentTimeMillis()
                    messageKey = null
                    delete = false
                }
                chatAdapter.messageList.add(messageData)
                chatAdapter.notifyDataSetChanged()
                message_recyclerview.scrollToPosition(chatAdapter.messageList.size - 1)
                chatPresenter.uploadVideo(messageData)
            } else {
                showInternetErrorMessage()
            }
        }
    }

    override fun loadScreenView() {
        chat_username.text = mChatUserName
        chatPresenter.retrieveMessagesFromDb(mChatUserId, isGroup!!)
    }

    override fun setAnotherUserOnlineStatus(onlineStatus: Boolean) {
        if (onlineStatus) {
            onlinetstaus.text = "online"
            onlinetstaus.visibility = View.VISIBLE
        } else {
            onlinetstaus.visibility = View.GONE
        }
    }

    override fun loadUsersConversation(user_messages: Message) {
        val list = chatAdapter.messageList.filter { it.timestamp == user_messages.timestamp }
        if (list.isNotEmpty()) {
            copyImageFlow(list[0].message, list[0].messageKey)
            list[0].message = user_messages.message
            list[0].messageKey = user_messages.messageKey
        } else {
            chatAdapter.messageList.add(user_messages)
        }
        chatAdapter.notifyDataSetChanged()
        message_recyclerview.scrollToPosition(chatAdapter.messageList.size - 1)
    }

    private fun copyImageFlow(path: String?, messageKey: String?) {
        val destination = File(
            Environment.getExternalStorageDirectory(),
            "Download/ChatApp",
        )
        if (checkDirectory()) {
            saveImage(path?.toUri(), destination, messageKey)
        } else {
            val check = createDirectory()
            if (check)
                saveImage(path?.toUri(), destination, messageKey)
            else
                showToast("unable to create a directory")
        }

    }

    private fun saveImage(sentImageUri: Uri?, destination: File, messageKey: String?) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, sentImageUri)
        val file = File(destination, "$messageKey.jpg")

        outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    override fun messageDeleteSuccess(position: Int) {
        delete.visibility = View.GONE
        copy.visibility = View.GONE
        back_button.visibility = View.GONE
        chat_username.visibility = View.VISIBLE
        chatAdapter.messageList[position].delete = true
        chatAdapter.messageList[position].setSelectedPosition(-1)
        chatAdapter.notifyItemChanged(position)
    }

    override fun messageDeleteFailure(position: Int) {
        chatAdapter.messageList[position].delete = true
        chatAdapter.messageList[position].setSelectedPosition(-1)
        chatAdapter.notifyItemChanged(position)
    }

    override fun uploadImageSuccess(uploadedimageurl: String, timestamp: Long) {
        chatPresenter.sendMessage(
            uploadedimageurl,
            mChatUserId,
            mChatUserName,
            isGroup!!,
            "image",
            timestamp
        )
    }

    override fun uploadImageFailure(exception: Exception) {
        showToast("error$exception")
    }

    override fun uploadVideoSuccess(uploadVideoUrl: String, timestamp: Long) {
        chatPresenter.sendMessage(
            uploadVideoUrl,
            mChatUserId,
            mChatUserName,
            isGroup!!,
            "video",
            timestamp
        )
    }

    override fun uploadVideoFailure(exception: java.lang.Exception) {
        showToast("error$exception")
    }

    private fun setUpMessageRecyclerView() {
        chatAdapter = ChatAdapter(this, this)
        message_recyclerview.adapter = chatAdapter
        chatAdapter.messageList = messageses
        chatAdapter.notifyDataSetChanged()
    }

    override fun onclick(position: Int) {
        val checkIsDeleteOrNot = chatAdapter.messageList[position]
        delete.visibility = View.VISIBLE
        copy.visibility = View.VISIBLE
        back_button.visibility = View.VISIBLE
        chat_username.visibility = View.GONE

        delete.setOnClickListener {
            chatPresenter.setDeleteOnMessageFlag(
                checkIsDeleteOrNot,
                mChatUserId,
                isGroup!!,
                position
            )
        }

        back_button.setOnClickListener {
            delete.visibility = View.GONE
            copy.visibility = View.GONE
            back_button.visibility = View.GONE
            chat_username.visibility = View.VISIBLE
            unSelect(position)
        }

        copy.setOnClickListener {
            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val myClip = ClipData.newPlainText("text", checkIsDeleteOrNot.message)
            myClipboard.setPrimaryClip(myClip)
            delete.visibility = View.GONE
            copy.visibility = View.GONE
            chat_username.visibility = View.VISIBLE
            unSelect(position)
            showToast("message copied")
        }
    }

    private fun unSelect(position: Int) {
        chatAdapter.messageList[position].setSelectedPosition(-1)
        chatAdapter.notifyItemChanged(position)
    }

    override fun onDestroy() {
        if (!isGroup!!)
            chatPresenter.setCurrentUserOfflineStatus()
        chatPresenter.onDetach()
        super.onDestroy()
    }

    override fun onStart() {
        chatPresenter.setCurrentUserOnlineStatus()
        if (!isGroup!!)
            chatPresenter.getAnotherOnlineStatus(mChatUserId)
        super.onStart()
    }
}