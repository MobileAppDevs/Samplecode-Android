package com.dream.friend.ui.bottomsheet.advanceFilter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.dream.friend.R
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.fontFamily
import com.dream.friend.data.model.NotificationOn
import com.dream.friend.data.model.NotificationRequest
import com.dream.friend.interfaces.SubFilterListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SubFilterBottomSheetDialog(var title:String, var list:ArrayList<String>, var alreadySelectedList:ArrayList<String>, var isToggleOn:Boolean, var isMultiSelectEnabled:Boolean?=false, var listener: SubFilterListener) : BottomSheetDialogFragment(){

    var selectedChips= arrayListOf<String>()
    var preCheckIds= listOf<Int>()
    var isToggleOnFinal = false
    var count = 0

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_sub_filter, container, false)
        view.setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<TextView>(R.id.tvTitle).text=title
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener { dismiss() }
        val toggle=view.findViewById<SwitchCompat>(R.id.toggle)
        toggle.isChecked=isToggleOn
        isToggleOnFinal=isToggleOn
        val chipGroup=view.findViewById<ChipGroup>(R.id.chipGroup)
            chipGroup.isSingleSelection=isMultiSelectEnabled!=true
        list.mapIndexed { index, name ->
            val chip = layoutInflater.inflate(
                R.layout.chip_layout,
                chipGroup,
                false
            ) as Chip
            chip.isCheckedIconVisible = false
            if(alreadySelectedList.contains(name)){
                chip.isChecked=true
                selectedChips.add(name)
            }
            chip.fontFamily(R.font.merriweather_sans_regular)
            chip.id = index
            chip.text = name
            chipGroup.addView(chip)
        }
        toggle.setOnCheckedChangeListener { buttonView, isChecked ->
                isToggleOnFinal=isChecked
        }
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            count = checkedIds.size
            if(count<6) {
                selectedChips.clear()
                checkedIds.map { checkedIndex ->
                    list[checkedIndex].let {
                        selectedChips.add(it)
                    }
                    view.findViewById<MaterialButton>(R.id.btnApplyFilters).text=if(title.lowercase() != "interests"){"Apply Filters"}else "Apply Filters $count/${if(isMultiSelectEnabled == true) 5 else 1}"
                }
                preCheckIds=checkedIds

            }else{
                chipGroup.removeAllViews()
                list.mapIndexed { index, name ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        chipGroup,
                        false
                    ) as Chip
                    chip.isCheckedIconVisible = false
                    chip.isChecked = preCheckIds.contains(index)
                    chip.fontFamily(R.font.merriweather_sans_regular)
                    chip.id = index
                    chip.text = name
                    chipGroup.addView(chip)
                }
                context?.showToast("only 5 option is allowed.")
            }
        }
        view.findViewById<MaterialButton>(R.id.btnApplyFilters).setOnClickListener {
            listener.onApplyFilter(selectedChips,isToggleOnFinal)
            dismiss()
        }




        return view
    }

}