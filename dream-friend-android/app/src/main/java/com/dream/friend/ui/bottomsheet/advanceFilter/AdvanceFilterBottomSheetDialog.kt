package com.dream.friend.ui.bottomsheet.advanceFilter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.dream.friend.R
import com.dream.friend.common.Constants
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openLoginPage
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.AdvanceFilterResponse
import com.dream.friend.data.model.CreateAdvanceFilterReq
import com.dream.friend.data.model.Lifestyle
import com.dream.friend.data.model.NotificationOn
import com.dream.friend.data.model.NotificationRequest
import com.dream.friend.data.model.user.Location
import com.dream.friend.data.network.Resource
import com.dream.friend.interfaces.AdvanceFilterListener
import com.dream.friend.interfaces.LocationListener
import com.dream.friend.interfaces.SubFilterListener
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.getCity
import com.dream.friend.util.Extensions.gone
import com.dream.friend.util.Extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.discovery_view_item.tvLocation


class AdvanceFilterBottomSheetDialog(var listener: AdvanceFilterListener) :
    BottomSheetDialogFragment(), View.OnClickListener {
    var verifiedProfilesOnly: Boolean = false
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: HomeScreenViewModel by viewModels()
    lateinit var loadingDialog: Dialog
    lateinit var views: View
    lateinit var switchVerified: SwitchCompat
    lateinit var heightSeekbar: SeekBar
    lateinit var tvSexualOrientation: TextView
    lateinit var tvEducation: TextView
    lateinit var tvReligion: TextView
    lateinit var tvSmoking: TextView
    lateinit var tvHeight: TextView
    lateinit var tvDrinking: TextView
    lateinit var tvInterest: TextView
    lateinit var tvClear: TextView
    lateinit var tvchange: TextView
    lateinit var tvWorkout: TextView
    lateinit var txtWhatsThis: TextView
    lateinit var btnApplyFilters: MaterialButton
    lateinit var btnUpgrade: MaterialButton
    lateinit var advanceFilterResponse: AdvanceFilterResponse
    lateinit var subFilterBottomSheetDialog: SubFilterBottomSheetDialog
    var selectedSexualOrientationId:ArrayList<Int>?= arrayListOf()
    var selectedEducationId:ArrayList<Int>? = arrayListOf()
    var selectedReligionId:ArrayList<Int>? = arrayListOf()

    //    var selectedWorkoutId= arrayListOf<Int>()
//    var selectedSmokingId= arrayListOf<Int>()
//    var selectedDrinkingId= arrayListOf<Int>()
    var selectedInterestsId:ArrayList<Int>? = arrayListOf()
    var drinking: String? = ""
    var smoking: String? = ""
    var workout: String? =""
    var isSexualOrientation:Boolean? = false
    var isEducation:Boolean? = false
    var isReligion:Boolean? = false
    var isInterest:Boolean? =false
    var isDrinking :Boolean?=false
    var isSmoking :Boolean?= false
    var isWorkout:Boolean? =false
   // var verifiedProfileOnly = true
    private var height:Int?=null;
    var lat:Double?=null
    var long:Double?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenBottomSheetDialog)
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view1 = inflater.inflate(R.layout.advance_filters_bottomsheet_layout, container, false)
        views = view1
        loadingDialog = requireActivity().dialogLoading()
        loadingDialog.dismiss()
        init()
        return view1
    }

    fun init() {
        views.findViewById<ImageView>(R.id.ivCross).setOnClickListener(this)
        switchVerified = views.findViewById(R.id.switchVerified)
        tvClear= views.findViewById(R.id.tvClear)
        tvClear.setOnClickListener(this)
        heightSeekbar = views.findViewById(R.id.heightSeekbar)
        tvSexualOrientation = views.findViewById(R.id.tvSexualOrientation)
        tvEducation = views.findViewById(R.id.tvEducation)
        tvReligion = views.findViewById(R.id.tvReligion)
        tvHeight = views.findViewById(R.id.tvHeight)
        tvWorkout = views.findViewById(R.id.tvWorkout)
        tvSmoking = views.findViewById(R.id.tvSmoking)
        tvchange = views.findViewById(R.id.tvChange)
        tvDrinking = views.findViewById(R.id.tvDrinking)
        tvInterest = views.findViewById(R.id.tvInterest)
        txtWhatsThis = views.findViewById(R.id.txtWhatsThis)
        btnApplyFilters = views.findViewById(R.id.btnApplyFilters)
        btnUpgrade = views.findViewById(R.id.btnUpgradeForAdvanceFilter)
        viewModelUserLogin.getUser()?.subscribption?.isPremiumSubscriber?.let {
            if(it){
                btnUpgrade.gone()
            }else{
                btnUpgrade.visible()
            }
        }
        txtWhatsThis.setOnClickListener {
            AskForVerificationBottomSheetDialog("Ask for Verification")
                .show(
                    requireActivity().supportFragmentManager,"verificationSheet"
                )
        }
        btnUpgrade.setOnClickListener {
            dismiss()
            listener.onUpgradeBtnClicked()
        }
        heightSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                height=progress
                tvHeight.text="${progress} CM"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {

            }
        })
        switchVerified.setOnCheckedChangeListener { buttonView, isChecked ->
            verifiedProfilesOnly=isChecked
        }
        viewModelUserLogin.getUser()?.userId?.let { viewModel.getAdvanceFilterData(it) }

        addObserver()
    }

    private fun addObserver() {
        viewModel.clearAdvanceFilterResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.status == 200) {
//                        requireActivity().showToast("cleared Advance Filter Successfully.")
                        dismiss()
                        listener.clearAllBtnClicked()
                    } else {
                        response.data?.message?.let { requireActivity().showToast(it) }
                    }

                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
//                    loadingDialog.show()
//                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
//                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.createAdvanceFilterResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.status == 200) {
                        //requireActivity().showToast("Applied Filter successfully.")
                        dismiss()
                        listener.onApplyBtnClicked()
                    } else {
                        response.data?.message?.let { requireActivity().showToast(it) }
                    }

                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
//                    loadingDialog.show()
//                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
//                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.advanceFilterResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    response.data?.let {
                        advanceFilterResponse = it
                    }
                    if (response.data?.data?.isNotEmpty() == true) {
                        response.data.data[0].location.let {
                                   if(it.coordinates.size>0 && it.coordinates[0]!=null && it.coordinates[1]!=null) {
                                       lat=it.coordinates[1]
                                       long=it.coordinates[0]
                                       views.findViewById<TextView>(R.id.tvLocation).text =
                                           requireActivity().getCity(it.coordinates[1], it.coordinates[0])
                                   }
                        }
                        response.data.data[0].verifiedProfileOnly?.let {
                            verifiedProfilesOnly=it
                            switchVerified.isChecked = it
                        }
                        response.data.data[0].height.let {
                            height=it
                            heightSeekbar.progress = it
                            tvHeight.text="${it} CM"
                        }
                        response.data.data[0].sexualOrientation.let { list ->
                            var txt = if(list.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            list.map {
                                txt += "$it,"
                            }
                            val sb = StringBuffer(txt)
                            if(list.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvSexualOrientation.text = sb
                        }
                        response.data.data[0].education?.let { list ->
                            var txt = if(list.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            list.map {
                                txt += "$it,"
                            }
                            val sb = StringBuffer(txt)
                            if(list.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvEducation.text = sb
                        }
                        response.data.data[0].religion?.let { list ->
                            var txt = if(list.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            list.map {
                                txt += "$it,"
                            }
                            val sb = StringBuffer(txt)
                            if(list.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvReligion.text = sb
                        }
                        response.data.data?.get(0)?.lifestyle?.workout?.let { name ->
                            tvWorkout.text =  if(name.trim() != "")"You're Seeing:${name}" else "+ Add this Filter"
                            workout=name
                        }
                        response.data.data?.get(0)?.lifestyle?.smoking?.let { name ->
                            tvSmoking.text =  if(name.trim() != "")"You're Seeing:${name}" else "+ Add this Filter"
                            smoking=name
                        }
                        response.data.data?.get(0)?.lifestyle?.drinking?.let { name ->
                            tvDrinking.text = if(name.trim() != "")"You're Seeing:${name}" else "+ Add this Filter"
                            drinking=name
                        }
                        response.data.data[0].interest.let { list ->
                            var txt =if(list.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            list.map {
                                txt += "$it,"
                            }
                            val sb = StringBuffer(txt)
                            if(list.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvInterest.text = sb
                        }

                        isSexualOrientation = response.data.data[0].isSexualOrientation
                        isEducation = response.data.data[0].isEducation
                        isReligion = response.data.data[0].isReligion
                        isWorkout = response.data.data[0].isWorkout
                        isSmoking = response.data.data[0].isSmoking
                        isDrinking = response.data.data[0].isDrinking
                        isInterest = response.data.data[0].isInterest
                        verifiedProfilesOnly = response.data.data[0].verifiedProfileOnly
                    }
                    tvSexualOrientation.setOnClickListener(this)
                    tvEducation.setOnClickListener(this)
                    tvReligion.setOnClickListener(this)
                    tvWorkout.setOnClickListener(this)
                    tvSmoking.setOnClickListener(this)
                    tvDrinking.setOnClickListener(this)
                    tvchange.setOnClickListener(this)
                    tvInterest.setOnClickListener(this)
                    btnApplyFilters.setOnClickListener(this)
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.educationResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.education?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.education.map {
                            list.add(it.name)
                        }

                        if (advanceFilterResponse.data.isNotEmpty()) {
                            advanceFilterResponse.data?.get(0)?.education?.map {
                                selectedList.add(it)
                            }
                        }
                        SubFilterBottomSheetDialog("Education",
                            list = list, isToggleOn = isEducation?:true,
                            alreadySelectedList = selectedList,
                            listener = object : SubFilterListener {
                                override fun onApplyFilter(
                                    selectedChips: ArrayList<String>,
                                    isSeeOtherPeopleToggleEnable: Boolean
                                ) {
                                    isEducation = isSeeOtherPeopleToggleEnable
                                    selectedEducationId?.clear()
                                    var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                                    selectedChips.map { edu ->
                                        if (list.contains(edu)) {
                                            txt += "$edu,"
                                            val e =
                                                response.data.education.filter { it.name == edu }
                                            if (e.isNotEmpty()) {
                                                selectedEducationId?.add(e[0].educationId)
                                            }
                                        }
                                    }

                                    val sb = StringBuffer(txt)
                                    if(selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    tvEducation.text = sb
                                }

                            }).show(requireActivity().supportFragmentManager, "ModalBottomSheet")
                    } else {
                        response.message?.let { requireActivity().showToast(it) }
                    }
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.sexualOrientationResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.sexualOrientations?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.sexualOrientations.map {
                            list.add(it.name)
                        }
                        if (advanceFilterResponse.data.isNotEmpty()) {
                            advanceFilterResponse.data?.get(0)?.sexualOrientation?.map {
                                selectedList.add(it)
                            }
                        }
                        SubFilterBottomSheetDialog("Sexual Orientation",
                            list = list, isToggleOn = isSexualOrientation?:true,
                            isMultiSelectEnabled = false,
                            alreadySelectedList = selectedList,
                            listener = object : SubFilterListener {
                                override fun onApplyFilter(
                                    selectedChips: ArrayList<String>,
                                    isSeeOtherPeopleToggleEnable: Boolean
                                ) {
                                    isSexualOrientation = isSeeOtherPeopleToggleEnable
                                    selectedSexualOrientationId?.clear()
                                    var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e =
                                                response.data.sexualOrientations.filter { it.name.equals(so, true) }
                                            if (e.isNotEmpty()) {
                                                selectedSexualOrientationId?.add(e[0].orientationId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if(selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    tvSexualOrientation.text = sb
                                }

                            }).show(
                            requireActivity().supportFragmentManager,
                            "SexualOrientationSheet"
                        )
                    } else {
                        response.message?.let { requireActivity().showToast(it) }
                    }
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.interestsResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.hobbies?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.hobbies.map {
                            list.add(it.name)
                        }
                        if (advanceFilterResponse.data.isNotEmpty()) {
                            advanceFilterResponse.data?.get(0)?.interest?.map {
                                selectedList.add(it)
                            }
                        }
                        SubFilterBottomSheetDialog("Interests",
                            list = list,
                            isToggleOn = isInterest?:true,
                            isMultiSelectEnabled = true,
                            alreadySelectedList = selectedList,
                            listener = object : SubFilterListener {
                                override fun onApplyFilter(
                                    selectedChips: ArrayList<String>,
                                    isSeeOtherPeopleToggleEnable: Boolean
                                ) {
                                    //requireActivity().showToast(selectedChips.toString() + isSeeOtherPeopleToggleEnable)
                                    isInterest = isSeeOtherPeopleToggleEnable
                                    selectedInterestsId?.clear()
                                    var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e = response.data.hobbies.filter { it.name == so }
                                            if (e.isNotEmpty()) {
                                                selectedInterestsId?.add(e[0].hobbiesId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if(selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    tvInterest.text = sb
                                }

                            }).show(
                            requireActivity().supportFragmentManager,
                            "InterestsBottomSheet"
                        )
                    } else {
                        response.message?.let { requireActivity().showToast(it) }
                    }
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }
        viewModel.religionResponse.observe(requireActivity()) { response ->
            when (response) {
                is Resource.Success -> {
                    loadingDialog.dismiss()
                    if (response.data?.religion?.isNotEmpty() == true) {
                        val list = arrayListOf<String>()
                        val selectedList = arrayListOf<String>()
                        response.data.religion.map {
                            list.add(it.name)
                        }
                        if (advanceFilterResponse.data.isNotEmpty()) {
                            advanceFilterResponse.data?.get(0)?.religion?.map {
                                selectedList.add(it)
                            }
                        }
                        SubFilterBottomSheetDialog("Religion",
                            list = list, isToggleOn = isReligion?:true,
                            alreadySelectedList = selectedList,
                            listener = object : SubFilterListener {
                                override fun onApplyFilter(
                                    selectedChips: ArrayList<String>,
                                    isSeeOtherPeopleToggleEnable: Boolean
                                ) {
                                    //requireActivity().showToast(selectedChips.toString() + isSeeOtherPeopleToggleEnable)
                                    isReligion = isSeeOtherPeopleToggleEnable
                                    selectedReligionId?.clear()
                                    var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                                    selectedChips.map { so ->
                                        if (list.contains(so)) {
                                            txt += "$so,"
                                            val e = response.data.religion.filter { it.name == so }
                                            if (e.isNotEmpty()) {
                                                selectedReligionId?.add(e[0].religionId)
                                            }
                                        }
                                    }
                                    val sb = StringBuffer(txt)
                                    if(selectedChips.isNotEmpty()) {
                                        sb.deleteCharAt(sb.length - 1)
                                    }
                                    tvReligion.text = sb
                                }

                            }).show(
                            requireActivity().supportFragmentManager,
                            "InterestsBottomSheet"
                        )
                    } else {
                        response.message?.let { requireActivity().showToast(it) }
                    }
                }

                is Resource.Error -> {
                    loadingDialog.dismiss()
                    response.message?.let { requireActivity().showToast(it) }
                }

                is Resource.Loading -> {
                    loadingDialog.show()
                    loadingDialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    loadingDialog.dismiss()
                    if (response.isTokenExpire == true) {
                        requireActivity().openLoginPage()
                    }
                }
            }
        }

    }

    private fun removeObserver() {
        viewModel.clearAdvanceFilterResponse.removeObservers(this)
        viewModel.createAdvanceFilterResponse.removeObservers(this)
        viewModel.advanceFilterResponse.removeObservers(this)
        viewModel.educationResponse.removeObservers(this)
        viewModel.sexualOrientationResponse.removeObservers(this)
        viewModel.interestsResponse.removeObservers(this)
        viewModel.religionResponse.removeObservers(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivCross -> {
                dismiss()
            }

            R.id.tvChange->{
                LocationBottomSheetDialog(object :LocationListener{

                    override fun onLocationSelected(
                        latt: Double?,
                        longg: Double?,
                        cityFinal: String?
                    ) {
                        views.findViewById<TextView>(R.id.tvLocation).text=cityFinal
                        lat=latt
                        long=longg
                    }

                }).show(requireActivity().supportFragmentManager,"locationDialog")
            }

            R.id.tvSexualOrientation -> {
                viewModel.getSexualOrientations(Constants.BASE_URL + Constants.UserSexualOrientations)
                loadingDialog.show()
            }

            R.id.tvEducation -> {
                viewModel.getEducations()
                loadingDialog.show()
            }

            R.id.tvReligion -> {
                viewModel.getReligions()
                loadingDialog.show()
            }

            R.id.tvWorkout -> {

                val selectedList = arrayListOf<String>()
                if (advanceFilterResponse.data.isNotEmpty()) {
                    advanceFilterResponse.data?.get(0)?.lifestyle?.workout?.let {
                        selectedList.add(
                            it
                        )
                    }
                }

                val list = arrayListOf(
                    getString(R.string.chip_everyday),
                    getString(R.string.chip_often),
                    getString(R.string.chip_sometimes)
                )
                SubFilterBottomSheetDialog("Workout",
                    list = list, isToggleOn = isWorkout?:true,
                    alreadySelectedList = selectedList,
                    listener = object : SubFilterListener {
                        override fun onApplyFilter(
                            selectedChips: ArrayList<String>,
                            isSeeOtherPeopleToggleEnable: Boolean
                        ) {
                            isWorkout = isSeeOtherPeopleToggleEnable
                            workout = ""
                            var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    workout = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if(selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvWorkout.text = sb
                        }

                    }).show(
                    requireActivity().supportFragmentManager,
                    "WorkoutSheet"
                )
            }

            R.id.tvSmoking -> {

                val selectedList = arrayListOf<String>()
                if (advanceFilterResponse.data.isNotEmpty()) {
                    advanceFilterResponse.data?.get(0)?.lifestyle?.smoking?.let {
                        selectedList.add(
                            it
                        )
                    }
                }
                val list = arrayListOf(
                    getString(R.string.chip_socially),
                    getString(R.string.chip_never),
                    getString(R.string.chip_regularly)
                )
                SubFilterBottomSheetDialog("Smoking",
                    list = list, isToggleOn = isSmoking?:true,
                    alreadySelectedList = selectedList,
                    listener = object : SubFilterListener {
                        override fun onApplyFilter(
                            selectedChips: ArrayList<String>,
                            isSeeOtherPeopleToggleEnable: Boolean
                        ) {
                            isSmoking = isSeeOtherPeopleToggleEnable
                            smoking =""
                            var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    smoking = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if(selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvSmoking.text = sb
                        }

                    }).show(
                    requireActivity().supportFragmentManager,
                    "SmokingSheet"
                )
            }

            R.id.tvDrinking -> {
                val selectedList = arrayListOf<String>()
                if (advanceFilterResponse.data.isNotEmpty()) {
                    advanceFilterResponse.data?.get(0)?.lifestyle?.drinking?.let {
                        selectedList.add(
                            it
                        )
                    }
                }
                val list = arrayListOf(
                    getString(R.string.chip_socially),
                    getString(R.string.chip_never)
                )
                SubFilterBottomSheetDialog("Drinking",
                    list = list, isToggleOn = isDrinking?:true,
                    alreadySelectedList = selectedList,
                    listener = object : SubFilterListener {
                        override fun onApplyFilter(
                            selectedChips: ArrayList<String>,
                            isSeeOtherPeopleToggleEnable: Boolean
                        ) {
                            isDrinking = isSeeOtherPeopleToggleEnable
                            drinking = ""
                            var txt = if(selectedChips.isNotEmpty())"You're Seeing:" else "+ Add this Filter"
                            selectedChips.map {
                                if (list.contains(it)) {
                                    txt += "$it,"
                                    drinking = it
                                }
                            }
                            val sb = StringBuffer(txt)
                            if(selectedChips.isNotEmpty()) {
                                sb.deleteCharAt(sb.length - 1)
                            }
                            tvDrinking.text = sb
                        }

                    }).show(
                    requireActivity().supportFragmentManager,
                    "DrinkingSheet"
                )
            }

            R.id.tvInterest -> {
                viewModel.getInterests()
                loadingDialog.show()
            }

            R.id.btnApplyFilters -> {
                viewModelUserLogin.getUser()?.subscribption?.isPremiumSubscriber?.let {
                    if(it){
                        viewModelUserLogin.getUser()?.userId?.let { uid ->
                            viewModel.createAdvanceFilter(
                                uid,
                                CreateAdvanceFilterReq(
                                    education = if(selectedEducationId?.isNotEmpty()==true) selectedEducationId else null,
                                    height = if(height!=null)height else null,
                                    interest =if(selectedInterestsId?.isNotEmpty()==true) selectedInterestsId else null,
                                    isDrinking = isDrinking,
                                    isEducation = isEducation,
                                    isInterest = isInterest,
                                    isReligion = isReligion,
                                    isSexualOrientation = isSexualOrientation,
                                    isSmoking = isSmoking,
                                    isWorkout = isWorkout,
                                    lifestyle = Lifestyle(
                                        drinking = if(drinking?.isNotEmpty()==true) drinking else null,
                                        workout = if(workout?.isNotEmpty()==true) workout else null,
                                        smoking = if(smoking?.isNotEmpty()==true) smoking else null
                                    ),
                                    location =if(lat!=null && long!==null) Location(
                                        lat = if(lat!=null) lat else null,
                                        long = if(long!=null) long else null
                                    )else null,
                                    religion =if(selectedReligionId?.isNotEmpty()==true) selectedReligionId else null ,
                                    sexualOrientation =if(selectedSexualOrientationId?.isNotEmpty()==true) selectedSexualOrientationId else null ,
                                    verifiedProfileOnly = verifiedProfilesOnly

                                )
                            )
                        }
                    }else{
                        listener.onUpgradeBtnClicked()
                       // requireActivity().showToast("Upgrade To Dream Friend Premium plan for using Advance filter feature.")
                    }
                }

            }
            R.id.tvClear->{
                viewModelUserLogin.getUser()?.userId?.let { viewModel.clearAdvanceFilter(it) }
                loadingDialog.show()
            }
        }
    }

}