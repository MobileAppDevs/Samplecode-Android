package com.dream.friend.interfaces

import com.dream.friend.data.model.BasicFilterRequest
import com.dream.friend.data.model.user.User

interface BasicFilterListener {
    fun onAdvanceFilterClicked()
    fun onBasicFilterApplyBtnClicked(basicFilterRequest: BasicFilterRequest)
}

interface SuccessBottomSheetListener {
    fun onGoHomeButtonClicked()
}

interface BlockDialog1Listener{
    fun onBlockClicked(user: User)
    fun onBlockAndReportClicked(user: User)
}

interface PhotoDeleteListener{
    fun onDeleteClicked(fileName:String)
}

interface SubscriptionDialogListener{

    fun onClick(month:Int)

}


interface BlockThisPersonListener{
    fun onBlockClicked( user: User)

    fun onBlockAndReportClicked(user: User)
}
interface BlockAndReportThisPersonListener{
    fun onItemClicked( user: User)

}
interface BlockBottomSheetListener{
    fun onBlockBtnClicked(user: User)
}

interface  LocationAdapterListener{
  fun  onItemClicked(lat:Double,long: Double, city:String)
}

interface AdvanceFilterListener {
    fun onApplyBtnClicked()

    fun onUpgradeBtnClicked()

    fun clearAllBtnClicked()
}

interface SliderTimerListener{
    fun onRun()
}
interface LocationListener {
    fun onLocationSelected(lat: Double?, long: Double?, cityFinal: String?)
}
interface SubFilterListener {
    fun onApplyFilter(selectedChips: ArrayList<String>, isSeeOtherPeopleToggleEnable: Boolean)
}

interface ChipGroupListener {
    fun onApplyBtnClicked(selectedChips: ArrayList<String>)
}
interface HeightListener {
    fun onApplyBtnClicked(height:Int)
}
interface NameListener {
    fun onApplyBtnClicked(name:String)
}

interface PhotoVerificationListener {
fun openCamera()
fun uploadPhoto()
}