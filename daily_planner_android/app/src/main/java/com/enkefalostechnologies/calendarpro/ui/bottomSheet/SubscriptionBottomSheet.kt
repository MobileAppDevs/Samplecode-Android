package com.enkefalostechnologies.calendarpro.ui.bottomSheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.util.AmplifyDataModelUtil
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.openPlayStoreSubscriptionScreen
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.InAppSubscriptionListener
import com.enkefalostechnologies.calendarpro.util.SubscriptionBottomSheetListener
import com.enkefalostechnologies.calendarpro.util.SubscriptionHelper.getNextRenewingDate
import com.enkefalostechnologies.calendarpro.util.SubscriptionUtil


class SubscriptionBottomSheet(
    var email: String,
    var amplifyDataModelUtil: AmplifyDataModelUtil,
    var listener: SubscriptionBottomSheetListener
) :
    BottomSheetDialogFragment() {
    var subscriptionType: SubscriptionType = SubscriptionType.MONTHLY


    lateinit var mView: View


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.bottomsheet_subscription, container, false)
        mView.findViewById<CircularProgressButton>(R.id.btnSubscribeNow).isEnabled = false
        fetchPlans()
        mView.findViewById<ImageView>(R.id.ivCancel).setOnClickListener {
            dismiss()
            listener.onClosed()
        }
        mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).gone()
        mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).gone()

        mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).setOnClickListener {
            mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.white)
            mView.findViewById<TextView>(R.id.tvMonth)
                .setTextColor(requireActivity().resources.getColor(R.color.white))
            mView.findViewById<TextView>(R.id.tvYear)
                .setTextColor(requireActivity().resources.getColor(R.color.color_25282B))
            mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.color_25282B)
            mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).elevation = 10f
            mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).elevation = 1f
            subscriptionType = SubscriptionType.MONTHLY
            mView.findViewById<CircularProgressButton>(R.id.btnSubscribeNow).isEnabled = true
        }
        mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).setOnClickListener {
            mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.color_25282B)
            mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.white)
            mView.findViewById<TextView>(R.id.tvMonth)
                .setTextColor(requireActivity().resources.getColor(R.color.color_25282B))
            mView.findViewById<TextView>(R.id.tvYear)
                .setTextColor(requireActivity().resources.getColor(R.color.white))
            mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).elevation = 1f
            mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).elevation = 10f
            subscriptionType = SubscriptionType.YEARLY
            mView.findViewById<CircularProgressButton>(R.id.btnSubscribeNow).isEnabled = true
        }
        mView.findViewById<CircularProgressButton>(R.id.btnSubscribeNow).setOnClickListener {
            initiateSubscription(subscriptionType)
        }

        val ss = SpannableStringBuilder(mView.findViewById<TextView>(R.id.tvBottom).text.toString())

        val text1 = "from your"
        ss.setSpan(
            googlePlayConsole, ss.toString().indexOf(text1) + 10,
            ss.toString().indexOf(text1) + text1.length + 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_1E7FDF)),
            ss.toString().indexOf(text1) + 10,
            ss.toString().indexOf(text1) + text1.length + 20,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )

        mView.findViewById<TextView>(R.id.tvBottom).text = ss
        mView.findViewById<TextView>(R.id.tvBottom).movementMethod =
            LinkMovementMethod.getInstance()

        return mView
    }

    private var googlePlayConsole: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            requireActivity().openPlayStoreSubscriptionScreen()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
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


    private fun initiateSubscription(subscriptionType: SubscriptionType) =
        SubscriptionUtil(
            requireActivity()
        ).purchase(
            getProductId(subscriptionType),
            getPlanId(subscriptionType),
            object : InAppSubscriptionListener {
                override fun onSuccess(purchases: Purchase) {
                    val validUpTo = purchases.purchaseTime.getNextRenewingDate(subscriptionType)
                    amplifyDataModelUtil.setSubscription(email, validUpTo, subscriptionType, {
                        requireActivity().runOnUiThread {
                            dismiss()
                            listener.onPurchaseSuccess(purchases, validUpTo, subscriptionType)
                        }
                    }, {
                        requireActivity().runOnUiThread {
                            dismiss()
                            listener.onError(it.localizedMessage)
                        }
                    })
                }

                override fun onError(msg: String) {
                    listener.onError(msg)
                }
            })


    fun getProductId(subscriptionType: SubscriptionType) = when (subscriptionType) {
        SubscriptionType.MONTHLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.MONTHLY.productId
        SubscriptionType.YEARLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.YEARLY.productId
        SubscriptionType.NONE -> ""
    }

    fun getPlanId(subscriptionType: SubscriptionType) = when (subscriptionType) {
        SubscriptionType.MONTHLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.MONTHLY.planId
        SubscriptionType.YEARLY -> com.enkefalostechnologies.calendarpro.SubscriptionType.YEARLY.planId
        SubscriptionType.NONE -> ""
    }

    fun fetchPlans() {
        SubscriptionUtil(requireActivity()).fetchProductDetails(
            getProductId(SubscriptionType.MONTHLY),
            object : InAppSubscriptionListener {
                override fun onProductDetailsFetched(
                    productId: String,
                    productDetails: MutableList<ProductDetails>
                ) {
                    super.onProductDetailsFetched(productId, productDetails)
                    requireActivity().runOnUiThread {
                        val price =
                            productDetails[0].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                0
                            )?.formattedPrice

                        mView.findViewById<LinearLayoutCompat>(R.id.llMonthlySubscription).visible()
                        mView.findViewById<TextView>(R.id.tvPriceMonthly).text = "$price/month"
                    }
                }

                override fun onError(msg: String) {
                    super.onError(msg)
                    listener.onError(msg)

                }
            }
        )
        SubscriptionUtil(requireActivity()).fetchProductDetails(
            getProductId(SubscriptionType.YEARLY),
            object : InAppSubscriptionListener {
                override fun onProductDetailsFetched(
                    productId: String,
                    productDetails: MutableList<ProductDetails>
                ) {
                    super.onProductDetailsFetched(productId, productDetails)
                    requireActivity().runOnUiThread {
                        val price =
                            productDetails[0].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                0
                            )?.formattedPrice

                        mView.findViewById<LinearLayoutCompat>(R.id.llYearlySubscription).visible()
                        mView.findViewById<TextView>(R.id.tvPriceYearly).text = "$price/year"
                    }
                }

                override fun onError(msg: String) {
                    super.onError(msg)
                    listener.onError(msg)

                }
            }
        )
    }
}