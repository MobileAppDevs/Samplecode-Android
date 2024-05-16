package com.dream.friend.ui.bottomsheet.plan

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.ViewPager
import com.dream.friend.R
import com.dream.friend.common.invisible
import com.dream.friend.data.model.PlanFeature
import com.dream.friend.data.model.SubscriptionPlan
import com.dream.friend.interfaces.SubscriptionDialogListener
import com.dream.friend.util.Extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.NonCancellable.cancel


class AddOnBottomSheetDialog(
    var subscriptionPlan: SubscriptionPlan, @DrawableRes var bgResource:Int,
    var forBoost: Boolean =true, var subscriptionListener:SubscriptionDialogListener) :
    BottomSheetDialogFragment() {
    var distance: Int? = null
    lateinit var viewPager: ViewPager
    private var myCountDownTimer: AddOnBottomSheetDialog.MyCountDownTimer? = null
    var superLikeCount=if(forBoost)subscriptionPlan.planSchedule?.get(1)?.boostCount.toString().toInt() else subscriptionPlan.planSchedule?.get(1)?.superLikeCount.toString().toInt()
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.super_likes_bottomsheet_layout, container, false)
        myCountDownTimer = MyCountDownTimer(30000, 3000)
        view.findViewById<LinearLayoutCompat>(R.id.sll).setBackgroundResource(R.drawable.bottomsheet_bg)
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
            dismiss()
        }
        view.findViewById<LinearLayoutCompat>(R.id.ll).setBackgroundResource(bgResource)
        viewPager = view.findViewById<ViewPager>(R.id.view_pager)
//        val dot1 = view.findViewById<DotsIndicator>(R.id.dot1)

        val adapter = PlanPagerAdapter(requireActivity(),
            subscriptionPlan.planFeatures as ArrayList<PlanFeature>
        );
        viewPager.adapter = adapter
//        dot1.setViewPager(viewPager)
        if(!requireActivity().isDestroyed) {
            myCountDownTimer?.start()
        }
        view.findViewById<TextView>(R.id.tvTitle).text = subscriptionPlan.planName
       val p1= view.findViewById<View>(R.id.p1)
       val p2= view.findViewById<View>(R.id.p2)
       val p3= view.findViewById<View>(R.id.p3)
        p1.findViewById<TextView>(R.id.tvProduct).text=if(forBoost)"Boosts" else "Super Likes"
        p2.findViewById<TextView>(R.id.tvProduct).text=if(forBoost)"Boosts" else "Super Likes"
        p3.findViewById<TextView>(R.id.tvProduct).text=if(forBoost)"Boosts" else "Super Likes"

        p1.findViewById<TextView>(R.id.tvMonth).text= if(forBoost)subscriptionPlan.planSchedule?.get(2)?.boostCount.toString() else subscriptionPlan.planSchedule?.get(2)?.superLikeCount.toString()
        p2.findViewById<TextView>(R.id.tvMonth).text= if(forBoost)subscriptionPlan.planSchedule?.get(1)?.boostCount.toString() else subscriptionPlan.planSchedule?.get(1)?.superLikeCount.toString()
        p3.findViewById<TextView>(R.id.tvMonth).text=if(forBoost)subscriptionPlan.planSchedule?.get(0)?.boostCount.toString() else subscriptionPlan.planSchedule?.get(0)?.superLikeCount.toString()


        p1.findViewById<TextView>(R.id.tvDiscount).text=  "Save ${subscriptionPlan.planSchedule?.get(2)?.discount.toString()}%"
        p2.findViewById<TextView>(R.id.tvDiscount).text= "Save ${subscriptionPlan.planSchedule?.get(1)?.discount.toString()}%"
        p3.findViewById<TextView>(R.id.tvDiscount).text= "Save ${subscriptionPlan.planSchedule?.get(0)?.discount.toString()}%"

        p1.findViewById<TextView>(R.id.tvPrice).text=  "₹${subscriptionPlan.planSchedule?.get(2)?.planPrice.toString()}"
        p2.findViewById<TextView>(R.id.tvPrice).text= "₹${subscriptionPlan.planSchedule?.get(1)?.planPrice.toString()}"
        p3.findViewById<TextView>(R.id.tvPrice).text= "₹${subscriptionPlan.planSchedule?.get(0)?.planPrice.toString()}"

        p1.findViewById<TextView>(R.id.tvPerMonthPrice).text=  "or ₹${if(forBoost)subscriptionPlan.planSchedule?.get(2)?.perBoostPrice.toString() else subscriptionPlan.planSchedule?.get(2)?.perSuperLikePrice.toString()} each"
        p2.findViewById<TextView>(R.id.tvPerMonthPrice).text= "or ₹${if(forBoost)subscriptionPlan.planSchedule?.get(1)?.perBoostPrice.toString() else subscriptionPlan.planSchedule?.get(1)?.perSuperLikePrice.toString()} each"
        p3.findViewById<TextView>(R.id.tvPerMonthPrice).text= "or ₹${if(forBoost)subscriptionPlan.planSchedule?.get(0)?.perBoostPrice.toString() else subscriptionPlan.planSchedule?.get(0)?.perSuperLikePrice.toString()} each"
        p3.findViewById<TextView>(R.id.tvDiscount).invisible()
        p1.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));

        p1.setOnClickListener {
            p1.findViewById<TextView>(R.id.tvPopular).invisible()
            p1.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_selected_bg_rounded)
            if(subscriptionPlan.planSchedule?.get(1)?.discount!=0) {
                p1.findViewById<TextView>(R.id.tvDiscount).visible()
                p1.findViewById<TextView>(R.id.tvDiscount).background.setTint(
                    requireActivity().getColor(
                        R.color.color_FFC629
                    )
                );
            }else{
                p1.findViewById<TextView>(R.id.tvDiscount).invisible()
                p1.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            }

            p2.findViewById<TextView>(R.id.tvPopular).visible()
            p2.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p2.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p2.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)

            p3.findViewById<TextView>(R.id.tvPopular).invisible()
            p3.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_161616));
            p3.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p3.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)

            superLikeCount=if(forBoost) subscriptionPlan.planSchedule?.get(2)?.boostCount.toString().toInt() else subscriptionPlan.planSchedule?.get(2)?.superLikeCount.toString().toInt()
        }
        p2.setOnClickListener {
            p2.findViewById<TextView>(R.id.tvPopular).visible()
            p2.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_161616));
            if(subscriptionPlan.planSchedule?.get(1)?.discount!=0) {
                p2.findViewById<TextView>(R.id.tvDiscount).visible()
                p2.findViewById<TextView>(R.id.tvDiscount).background.setTint(
                    requireActivity().getColor(
                        R.color.color_FFC629
                    )
                );
            }else{
                p2.findViewById<TextView>(R.id.tvDiscount).invisible()
                p2.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            }
            p2.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_selected_bg_rounded)

            p1.findViewById<TextView>(R.id.tvPopular).invisible()
            p1.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)
            p1.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));

            p3.findViewById<TextView>(R.id.tvPopular).invisible()
            p3.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_161616));
            p3.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p3.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)
            superLikeCount=if(forBoost) subscriptionPlan.planSchedule?.get(1)?.boostCount.toString().toInt() else subscriptionPlan.planSchedule?.get(1)?.superLikeCount.toString().toInt()
        }

        p3.setOnClickListener {
            p1.findViewById<TextView>(R.id.tvPopular).invisible()
            p1.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)
            p1.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));

            p2.findViewById<TextView>(R.id.tvPopular).visible()
            p2.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p2.findViewById<TextView>(R.id.tvDiscount).background.setTint(requireActivity().getColor(R.color.color_E4E4E4));
            p2.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_unselected_bg_rounded)


            p3.findViewById<TextView>(R.id.tvPopular).invisible()
            p3.findViewById<TextView>(R.id.tvPopular).background.setTint(requireActivity().getColor(R.color.color_161616))
            p3.findViewById<LinearLayoutCompat>(R.id.llPrice).setBackgroundResource(R.drawable.plan_selected_bg_rounded)
            superLikeCount=if(forBoost) subscriptionPlan.planSchedule?.get(0)?.boostCount.toString().toInt() else subscriptionPlan.planSchedule?.get(0)?.superLikeCount.toString().toInt()
        }

        p2.performClick()

        view.findViewById<MaterialButton>(R.id.btnContinue).setOnClickListener{subscriptionListener?.onClick(superLikeCount)}



        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable=false
        }
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
//            requireActivity().runOnUiThread {
                if (viewPager.currentItem < (subscriptionPlan.planFeatures?.size?.minus(1) ?: 0)) {
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                } else {
                    viewPager.setCurrentItem(0, true)
                }
//            }
        }

        override fun onFinish() {
            myCountDownTimer?.let {
                cancel()
                start()
            }
        }
    }

    override fun onDestroy() {
        myCountDownTimer?.let {
            cancel()
        }
        super.onDestroy()
    }
}