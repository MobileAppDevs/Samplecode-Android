package com.enkefalostechnologies.calendarpro.ui.base

import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.constant.StorageConstant.SUBSCRIBED_PLAN
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.AppUtil.changeStatusBarColor
import com.enkefalostechnologies.calendarpro.util.AppUtil.getAndroidId
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dialogLoading
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.PreferenceHandler
import com.google.firebase.analytics.FirebaseAnalytics

abstract class BaseActivity<B : ViewBinding>(@LayoutRes layoutResId: Int) : AppCompatActivity(),
    View.OnClickListener {

     val binding: B by activityBindings(layoutResId)
    lateinit var preferenceManager:PreferenceHandler
    lateinit var amplifyDataModelUtil: AmplifyDataModelUtil
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        dialog=dialogLoading()
        dialog.dismiss()
        firebaseAnalytics=FirebaseAnalytics.getInstance(this)
        changeStatusBarColor(R.color.color_bg)
        Log.i("Lifecycle", "BaseActivity -> onCreate()")
    }

    override fun onResume() {
        preferenceManager=PreferenceHandler(this)
        amplifyDataModelUtil=AmplifyDataModelUtil(this)
        onViewBindingCreated()
        addObserver()
        super.onResume()
        Log.i("Lifecycle", "BaseActivity -> onResume()")
    }

    override fun onStop() {
        super.onStop()
        dialog.close()
        Log.i("Lifecycle", "BaseActivity -> onStop()")
    }

    override fun onDestroy() {
        removeObserver()
        super.onDestroy()
        Log.i("Lifecycle", "BaseActivity -> onDestroy()")
    }

    abstract fun onViewBindingCreated()
    abstract fun addObserver()
    abstract fun removeObserver()


//    fun getUserId()=preferenceManager.readString(StorageConstant.USER_ID,"")
    fun isUserLoggedIn()=preferenceManager.readBoolean(StorageConstant.IS_USER_LOGGED_IN,false)
    fun setUserLoggedIn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_USER_LOGGED_IN,value)
    fun setIsSocialLoggedIn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_SOCIAL_LOGGED_IN,value)
    fun isSocialLoggedIn()=preferenceManager.readBoolean(StorageConstant.IS_SOCIAL_LOGGED_IN,false)

    fun deviceId(): String = if(isUserLoggedIn()) "" else getAndroidId()


//    fun getUserId():String =if(isUserLoggedIn())preferenceManager.readString(StorageConstant.USER_ID,"") else ""
//    fun setUserId(userId:String)=preferenceManager.writeString(StorageConstant.USER_ID,userId)

    fun getUserName():String =preferenceManager.readString(StorageConstant.USER_NAME,"User")
    fun setUserName(name:String)=preferenceManager.writeString(StorageConstant.USER_NAME,name)
    fun getCountries():String =preferenceManager.readString(StorageConstant.COUNTRIES,"ADD")
    fun setCountries(countries:String)=preferenceManager.writeString(StorageConstant.COUNTRIES,countries)
    fun getUserPicUrl():String =preferenceManager.readString(StorageConstant.USER_PIC_URL,"")
    fun setUserPicUrl(url:String)=preferenceManager.writeString(StorageConstant.USER_PIC_URL,url)

    fun getUserEmail():String =if(isUserLoggedIn()) preferenceManager.readString(StorageConstant.USER_EMAIL,"") else ""
    fun setUserEmail(url:String)=preferenceManager.writeString(StorageConstant.USER_EMAIL,url)

//    fun isSubscribedUser():Boolean =preferenceManager.readBoolean(StorageConstant.IS_SUBSCRIBED_USER,false)
//    fun setIsSubscribedUser(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_SUBSCRIBED_USER,value)
    fun setSubscriptionStatus(value:SubscriptionStatus)=preferenceManager.writeString(StorageConstant.SUBSCRIPTION_STATUS,when(value){
        SubscriptionStatus.ACTIVE->SubscriptionStatus.ACTIVE.name
        SubscriptionStatus.EXPIRED->SubscriptionStatus.EXPIRED.name
        SubscriptionStatus.CANCELED->SubscriptionStatus.CANCELED.name
        SubscriptionStatus.NONE->SubscriptionStatus.NONE.name
    })

    fun getSubscribedPlan()=when(preferenceManager.readString(SUBSCRIBED_PLAN,"")){
        SubscriptionType.YEARLY.name->SubscriptionType.YEARLY
        SubscriptionType.MONTHLY.name->SubscriptionType.MONTHLY
        SubscriptionType.NONE.name->SubscriptionType.NONE
        else->SubscriptionType.NONE
    }
    fun saveSubscribedPlan(value: SubscriptionType)=preferenceManager.writeString(SUBSCRIBED_PLAN,when(value){
        SubscriptionType.YEARLY->SubscriptionType.YEARLY.name
        SubscriptionType.MONTHLY->SubscriptionType.MONTHLY.name
        SubscriptionType.NONE->SubscriptionType.NONE.name
    })

    fun getSubscriptionStatus()=when(preferenceManager.readString(StorageConstant.SUBSCRIPTION_STATUS,"")){
        SubscriptionStatus.ACTIVE.name->SubscriptionStatus.ACTIVE
        SubscriptionStatus.EXPIRED.name->SubscriptionStatus.EXPIRED
        SubscriptionStatus.CANCELED.name->SubscriptionStatus.CANCELED
        SubscriptionStatus.NONE.name->SubscriptionStatus.NONE
        else->SubscriptionStatus.NONE
    }
    fun isUserEmailVerified():Boolean =preferenceManager.readBoolean(StorageConstant.IS_USER_EMAIL_VERIFIED,false)
    fun setIsUserEmailVerified(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_USER_EMAIL_VERIFIED,value)

    fun isReminderOn():Boolean =preferenceManager.readBoolean(StorageConstant.IS_REMINDER_ON,true)
    fun setIsReminderOn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_REMINDER_ON,value)
    fun isNotificationOn():Boolean =preferenceManager.readBoolean(StorageConstant.IS_NOTIFICATION_ON,true)
    fun setIsNotificationOn(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_NOTIFICATION_ON,value)
    fun clearLocalStorage(context: Context)=preferenceManager.clear(context)

    fun setNewInstall(value:Boolean)=preferenceManager.writeBoolean(StorageConstant.IS_NEW_INSTALL,value)
    fun isNewInstall():Boolean =preferenceManager.readBoolean(StorageConstant.IS_NEW_INSTALL,true)
}