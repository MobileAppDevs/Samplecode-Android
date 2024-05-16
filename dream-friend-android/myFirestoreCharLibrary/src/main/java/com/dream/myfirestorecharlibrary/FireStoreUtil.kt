package com.dream.myfirestorecharlibrary

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.dream.myfirestorecharlibrary.models.Message
import java.io.File

class FireStoreUtil {
    private val fireStore = Firebase.firestore
    val lastUnseenMsg = MutableLiveData<Resource<HashMap<String, String>>>()
    val getMessageResponse = MutableLiveData<Resource<ArrayList<Message>>>()
    val getImageURLResponse = MutableLiveData<Resource<String>>()
    private var countMSG = 0

    fun sendImageToFirebase(filePath: String) {
        getImageURLResponse.postValue(Resource.Loading())
        val ref = FirebaseStorage.getInstance().reference
        val imageRoomID = System.currentTimeMillis().toString()
        ref.child(imageRoomID).putFile(Uri.fromFile(File(filePath)))
            .addOnSuccessListener {
                ref.child(imageRoomID).downloadUrl
                    .addOnSuccessListener {
                        it?.let {
                            getImageURLResponse.postValue(Resource.Success(it.toString()))
                        }
                    }
                    .addOnFailureListener {
                        getImageURLResponse.postValue(Resource.Error(it.message.toString()))
                    }
            }
            .addOnFailureListener {
                getImageURLResponse.postValue(Resource.Error(it.message.toString()))
            }
    }

    fun retrieveMsg(user1ID: String, user2ID: String) {
        val messages = arrayListOf<Message>()
        fireStore.collection("$user1ID-$user2ID")
            .addSnapshotListener { value, _ ->
                value?.documents?.size?.let {
                    if (it > 0) {
                        messages.clear()
                        value.documents.forEach { map ->
                            messages.add(
                                Message(
                                    map["message"].toString(),
                                    map["messageBy"].toString(),
                                    map["time"].toString(),
                                    map["date"].toString(),
                                    map["messageType"].toString(),
                                    map["path"].toString(),
                                )
                            )
                        }.let {
                            getMessageResponse.postValue(Resource.Success(messages))
                        }
                    }
                }
            }
        fireStore.collection("$user2ID-$user1ID")
            .addSnapshotListener { value, _ ->
                value?.documents?.size?.let {
                    if (it > 0) {
                        messages.clear()
                        value.documents.forEach { map ->
                            messages.add(
                                Message(
                                    map["message"].toString(),
                                    map["messageBy"].toString(),
                                    map["time"].toString(),
                                    map["date"].toString(),
                                    map["messageType"].toString(),
                                    map["path"].toString(),
                                )
                            )
                        }.let {
                            getMessageResponse.postValue(Resource.Success(messages))
                        }
                    }
                }
            }
    }

    val map = hashMapOf<String, String>()

    fun countUnreadMsg(user1ID: String, user2ID: String) {
        fireStore.collection("$user1ID-$user2ID")
            .addSnapshotListener { value, _ ->
                value?.documentChanges?.size?.let {
                    if (it > 0) {
                        val messageBy =
                            value.documentChanges[value.documentChanges.size - 1].document.data["messageBy"].toString()
                        val message =
                            value.documentChanges[value.documentChanges.size - 1].document.data["message"].toString()
                        val date =
                            value.documentChanges[value.documentChanges.size - 1].document.data["date"].toString()
                        val time =
                            value.documentChanges[value.documentChanges.size - 1].document.data["time"].toString()
                        val map = hashMapOf<String, String>()
                        if (message.isNotBlank()) {
                            map["$user2ID-msg-by"] = messageBy
                            map["$user2ID-msg"] = message
                            map["$user2ID-date"] = date
                            map["$user2ID-time"] = time
                            lastUnseenMsg.postValue(Resource.Success(map))
                        }
                    }
                }
            }
        fireStore.collection("$user2ID-$user1ID")
            .addSnapshotListener { value, _ ->
                value?.documentChanges?.size?.let {
                    if (it > 0) {
                        val messageBy =
                            value.documentChanges[value.documentChanges.size - 1].document.data["messageBy"].toString()
                        val message =
                            value.documentChanges[value.documentChanges.size - 1].document.data["message"].toString()
                        val date =
                            value.documentChanges[value.documentChanges.size - 1].document.data["date"].toString()
                        val time =
                            value.documentChanges[value.documentChanges.size - 1].document.data["time"].toString()
                        if (message.isNotBlank()) {
                            map["$user2ID-msg-by"] = messageBy
                            map["$user2ID-msg"] = message
                            map["$user2ID-date"] = date
                            map["$user2ID-time"] = time
                            lastUnseenMsg.postValue(Resource.Success(map))
                        }
                    }
                }
            }
    }

    fun sendMsgToFirebase(user1ID: String, user2ID: String, message: Message) {
        countMSG++
        fireStore.collection("$user1ID-$user2ID")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.size > 0) {
                    countMSG = 0
                    sendMessage(user1ID, user2ID, message)
                } else if (snapshot.documents.size == 0 && countMSG == 1)
                    sendMsgToFirebase(user2ID, user1ID, message)
                else if (snapshot.documents.size == 0 && countMSG == 2) {
                    countMSG = 0
                    sendMessage(user1ID, user2ID, message)
                }
            }
    }

    private fun sendMessage(user1ID: String, user2ID: String, message: Message) {
        fireStore.collection("$user1ID-$user2ID")
            .document(System.currentTimeMillis().toString())
            .set(message)
    }
}