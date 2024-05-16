package com.enkefalostechnologies.calendarpro.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.constant.StorageConstant.SUBSCRIPTION_STATUS
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.getAndroidId
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dialogLoading
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler

abstract class BaseFragment<Binding : ViewBinding>(
    layoutID: Int
) : Fragment(layoutID) {

    val binding: Binding by fragmentBindings(layoutID)
    lateinit var preferenceManager: PreferenceHandler
    lateinit var amplifyDataModelUtil: AmplifyDataModelUtil
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            dialog=requireActivity().dialogLoading()
            dialog.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("lifecycle","onViewCreated")
        preferenceManager=PreferenceHandler(requireActivity())
        amplifyDataModelUtil=AmplifyDataModelUtil(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onResume() {
        setupViews()
        setupListeners()
        setupObservers()
        fetchInitialData()
        super.onResume()
        Log.i("Lifecycle", "BaseFragment -> onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.i("Lifecycle", "BaseFragment -> onPause()")
    }

    override fun onDetach() {
        super.onDetach()

        removeObserver()
        dialog.close()
        Log.i("Lifecycle", "BaseFragment -> onDetach()")
    }

    override fun onDestroy() {
        Log.i("Lifecycle", "BaseFragment -> onDestroy()")
        removeObserver()
        dialog.close()
        super.onDestroy()
    }
    abstract fun setupViews()

    abstract fun setupListeners()
    abstract fun fetchInitialData()

    abstract fun setupObservers()
    abstract  fun removeObserver()

    fun isUserLoggedIn()=preferenceManager.readBoolean(StorageConstant.IS_USER_LOGGED_IN,false)
    fun setUserLoggedIn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_USER_LOGGED_IN,value)

    fun deviceId(): String =if(isUserLoggedIn()) "" else requireActivity().getAndroidId()

//    fun getUserId():String =if(isUserLoggedIn()) preferenceManager.readString(StorageConstant.USER_ID,"") else ""
//    fun setUserId(userId:String)=preferenceManager.writeString(StorageConstant.USER_ID,userId)

    fun getUserName():String =preferenceManager.readString(StorageConstant.USER_NAME,"User")
    fun setUserName(name:String)=preferenceManager.writeString(StorageConstant.USER_NAME,name)

    fun getUserPicUrl():String =preferenceManager.readString(StorageConstant.USER_PIC_URL,"")
    fun setUserPicUrl(url:String)=preferenceManager.writeString(StorageConstant.USER_PIC_URL,url)

    fun getUserEmail():String =if(isUserLoggedIn()) preferenceManager.readString(StorageConstant.USER_EMAIL,"") else ""
    fun setUserEmail(url:String)=preferenceManager.writeString(StorageConstant.USER_EMAIL,url)

    fun getCountries():String =preferenceManager.readString(StorageConstant.COUNTRIES,"ADD")
    fun setCountries(countries:String)=preferenceManager.writeString(StorageConstant.COUNTRIES,countries)

//    fun isSubscribedUser():Boolean =preferenceManager.readBoolean(StorageConstant.IS_SUBSCRIBED_USER,false)
//    fun setIsSubscribedUser(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_SUBSCRIBED_USER,value)
    fun setSubscriptionStatus(value:String)=preferenceManager.writeString(SUBSCRIPTION_STATUS,value)

    fun isUserEmailVerified():Boolean =preferenceManager.readBoolean(StorageConstant.IS_USER_EMAIL_VERIFIED,false)
    fun setIsUserEmailVerified(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_USER_EMAIL_VERIFIED,value)
    fun saveSubscribedPlan(value: String)=preferenceManager.writeString(StorageConstant.SUBSCRIBED_PLAN,value)

    fun setSubscriptionStatus(value: SubscriptionStatus)=preferenceManager.writeString(StorageConstant.SUBSCRIPTION_STATUS,when(value){
        SubscriptionStatus.ACTIVE-> SubscriptionStatus.ACTIVE.name
        SubscriptionStatus.EXPIRED-> SubscriptionStatus.EXPIRED.name
        SubscriptionStatus.CANCELED-> SubscriptionStatus.CANCELED.name
        SubscriptionStatus.NONE-> SubscriptionStatus.NONE.name
    })

    fun getSubscribedPlan()=when(preferenceManager.readString(StorageConstant.SUBSCRIBED_PLAN,"")){
        SubscriptionType.YEARLY.name-> SubscriptionType.YEARLY
        SubscriptionType.MONTHLY.name-> SubscriptionType.MONTHLY
        SubscriptionType.NONE.name-> SubscriptionType.NONE
        else-> SubscriptionType.NONE
    }
    fun saveSubscribedPlan(value: SubscriptionType)=preferenceManager.writeString(
        StorageConstant.SUBSCRIBED_PLAN,when(value){
        SubscriptionType.YEARLY-> SubscriptionType.YEARLY.name
        SubscriptionType.MONTHLY-> SubscriptionType.MONTHLY.name
        SubscriptionType.NONE-> SubscriptionType.NONE.name
    })

    fun getSubscriptionStatus()=when(preferenceManager.readString(StorageConstant.SUBSCRIPTION_STATUS,"")){
        SubscriptionStatus.ACTIVE.name-> SubscriptionStatus.ACTIVE
        SubscriptionStatus.EXPIRED.name-> SubscriptionStatus.EXPIRED
        SubscriptionStatus.CANCELED.name-> SubscriptionStatus.CANCELED
        SubscriptionStatus.NONE.name-> SubscriptionStatus.NONE
        else-> SubscriptionStatus.NONE
    }
    fun isReminderOn():Boolean =preferenceManager.readBoolean(StorageConstant.IS_REMINDER_ON,true)
    fun setIsReminderOn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_REMINDER_ON,value)
    fun isNotificationOn():Boolean =preferenceManager.readBoolean(StorageConstant.IS_NOTIFICATION_ON,true)
    fun setIsNotificationOn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_NOTIFICATION_ON,value)
    fun clearLocalStorage(context: Context)=preferenceManager.clear(context)

}