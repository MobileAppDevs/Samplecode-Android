package com.enkefalostechnologies.calendarpro.ui

import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.databinding.ActivityGetStartedBinding
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.permission.PermissionActivity
import com.enkefalostechnologies.calendarpro.util.AppUtil.hideStatusBar
import com.enkefalostechnologies.calendarpro.util.AppUtil.openUrl


class GetStartedActivity : BaseActivity<ActivityGetStartedBinding>(R.layout.activity_get_started){


    private var termsListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
             openUrl(Constants.TERMS_AND_CONDITION_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    private var policyListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            openUrl(Constants.PRIVACY_POLICY_URL)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }
    override fun onViewBindingCreated() {
        hideStatusBar()
        binding.slidingButton.setOnClickListener {
                startActivity(Intent(this,PermissionActivity::class.java))
                finish()

        }
        binding.ib.setOnClickListener {
            startActivity(Intent(this,PermissionActivity::class.java))
            finish()

        }

        setSpannable()
    }

    override fun addObserver() {

    }

    override fun removeObserver() {

    }

    override fun onClick(p0: View?) {

    }

    private fun setSpannable() {
        val ss = SpannableStringBuilder(binding.txt.text.toString())

        val text1 = "Privacy policy"
        ss.setSpan(
            policyListener, ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_1E7FDF)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )


        val text2 = "End User License Agreement."
        ss.setSpan(
            termsListener, ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_1E7FDF)),
            ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )


        binding.txt.text = ss
        binding.txt.movementMethod = LinkMovementMethod.getInstance()
    }

}