package com.enkefalostechnologies.calendarpro.util.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.KnowYourDayDialogBinding
import com.enkefalostechnologies.calendarpro.ui.adapter.WaterInTakeDateAdapter
import com.enkefalostechnologies.calendarpro.util.AppUtil
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.Extension.disableAngryEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.disableHappyEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.disableHeart
import com.enkefalostechnologies.calendarpro.util.Extension.disableSadEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.disableStar
import com.enkefalostechnologies.calendarpro.util.Extension.disableText
import com.enkefalostechnologies.calendarpro.util.Extension.enableAngryEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.enableHappyEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.enableHeart
import com.enkefalostechnologies.calendarpro.util.Extension.enableSadEmoji
import com.enkefalostechnologies.calendarpro.util.Extension.enableStar
import com.enkefalostechnologies.calendarpro.util.Extension.enableText
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.invisible
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.KnowYourDialogListener
import com.enkefalostechnologies.calendarpro.util.WaterIntakeDateAdapterListener
import java.util.Date

fun Activity.knowYourDialog(
    isLoggedIn:Boolean,
    email: String,
    deviceId:String,
    binding: KnowYourDayDialogBinding,
    listener: KnowYourDialogListener
): Dialog =
    Dialog(this).apply {
        var selectedDate = Date().dateToTemporalDate()
        var healthCount = -1
        var productivityCount = -1
        var moodCount = -1
        var heart1 = false
        var heart2 = false
        var heart3 = false
        var star1 = false
        var star2 = false
        var star3 = false
        var emoji1 = false
        var emoji2 = false
        var emoji3 = false
        val ivHeart1 = binding.heart1
        val ivHeart2 = binding.heart2
        val ivHeart3 = binding.heart3
        val ivStar1 = binding.ivStar1
        val ivStar2 = binding.ivStar2
        val ivStar3 = binding.ivStar3
        val llAngry = binding.llAngry
        val llSad = binding.llSad
        val llHappy = binding.llHappy
        val ivHappy = binding.ivHappy
        val ivSad = binding.ivSad
        val ivAngry = binding.ivAngry
        val tvHappy = binding.tvHappy
        val tvSad = binding.tvSad
        val tvAngry = binding.tvAngry
        val tvDescription = binding.tvDescription
        val tvDescription2 = binding.tvDescription2
        val query=if(isLoggedIn){
            Where.matches(KnowYourDay.EMAIL.eq(email).and(KnowYourDay.DATE.eq(selectedDate)))
        }else{
            Where.matches(KnowYourDay.DEVICE_ID.eq(deviceId).and(KnowYourDay.DATE.eq(selectedDate)))
        }
            Amplify.DataStore.query(
                KnowYourDay::class.java, query, { data ->
                    while (data.hasNext()) {
                        val item = data.next()
                        healthCount = item.healthCount
                        productivityCount = item.productivityCount
                        moodCount = item.moodCount
                        runOnUiThread {
                            when (healthCount) {
                                0 -> {
                                    heart1 = true
                                    heart2 = false
                                    heart3 = false
                                    ivHeart1.enableHeart()
                                    ivHeart2.disableHeart()
                                    ivHeart3.disableHeart()
                                    tvDescription.visible()
                                    tvDescription.text = "Poor"
                                    binding.btnNext1.isEnabled =
                                        heart1 || heart2 || heart3
                                }

                                1 -> {

                                    ivHeart1.enableHeart()
                                    ivHeart2.enableHeart()
                                    ivHeart3.disableHeart()
                                    heart1 = true
                                    heart2 = true
                                    heart3 = false
                                    tvDescription.visible()
                                    tvDescription.text = "Good"
                                    binding.btnNext1.isEnabled =
                                        heart1 || heart2 || heart3
                                }

                                2 -> {
                                    ivHeart1.enableHeart()
                                    ivHeart2.enableHeart()
                                    ivHeart3.enableHeart()
                                    tvDescription.visible()
                                    heart1 = true
                                    heart2 = true
                                    heart3 = true
                                    tvDescription.text = "Excellent"
                                    binding.btnNext1.isEnabled =
                                        heart1 || heart2 || heart3
                                }
                            }
                            tvDescription2.visible()
                            when (productivityCount) {
                                0 -> {
                                    star1 = true
                                    star2 = false
                                    star3 = false
                                    ivStar1.enableStar()
                                    ivStar2.disableStar()
                                    ivStar3.disableStar()
                                    tvDescription2.visible()
                                    tvDescription2.text = "Poor"
                                    binding.btnNext2.isEnabled =
                                        star1 || star2|| star3
                                }

                                1 -> {
                                    star1 = true
                                    star2 = true
                                    star3 = false
                                    ivStar1.enableStar()
                                    ivStar2.enableStar()
                                    ivStar3.disableStar()
                                    tvDescription2.visible()
                                    tvDescription2.text = "Good"
                                    binding.btnNext2.isEnabled =
                                        star1 || star2|| star3
                                }

                                2 -> {
                                    star1 = true
                                    star2 = true
                                    star3 = false
                                    ivStar1.enableStar()
                                    ivStar2.enableStar()
                                    ivStar3.enableStar()
                                    tvDescription2.visible()
                                    tvDescription2.text = "Excellent"
                                    binding.btnNext2.isEnabled =
                                        star1 || star2|| star3
                                }
                            }
                            when (moodCount) {
                                0 -> {
                                    ivHappy.disableHappyEmoji()
                                    tvHappy.disableText()
                                    ivSad.disableSadEmoji()
                                    tvSad.disableText()
                                    ivAngry.enableAngryEmoji()
                                    tvAngry.enableText()
                                    emoji1 = true
                                    emoji2 = false
                                    emoji3 = false
                                    binding.btnDone.isEnabled =
                                        emoji1 || emoji2|| emoji3
                                }

                                1 -> {
                                    ivHappy.disableHappyEmoji()
                                    tvHappy.disableText()
                                    ivSad.enableSadEmoji()
                                    tvSad.enableText()
                                    ivAngry.disableAngryEmoji()
                                    tvAngry.disableText()
                                    emoji1 = false
                                    emoji2 = true
                                    emoji3 = false
                                    binding.btnDone.isEnabled =
                                        emoji1 || emoji2|| emoji3
                                }

                                2 -> {
                                    ivHappy.enableHappyEmoji()
                                    tvHappy.enableText()
                                    ivSad.disableSadEmoji()
                                    tvSad.disableText()
                                    ivAngry.disableAngryEmoji()
                                    tvAngry.disableText()
                                    emoji1 = false
                                    emoji2 = false
                                    emoji3 = true
                                    binding.btnDone.isEnabled =
                                        emoji1 || emoji2|| emoji3
                                }
                            }
                        }
                    }
                }, {})
        val rvDate = binding.rvDate
        val adapter =
            WaterInTakeDateAdapter(selectedDate, object : WaterIntakeDateAdapterListener {
                override fun onItemClicked(date: Temporal.Date) {
                    if (binding.btnNext1.isVisible) {
                        selectedDate = date
                        rvDate.adapter?.notifyDataSetChanged()
                        if(isLoggedIn){
                            Amplify.DataStore.query(
                                KnowYourDay::class.java, Where.matches(
                                    KnowYourDay.EMAIL.eq(email)
                                        .and(KnowYourDay.DATE.eq(selectedDate))
                                ), { data ->
                                    if (data.hasNext()) {
                                        val item = data.next()
                                        healthCount = item.healthCount
                                        productivityCount = item.productivityCount
                                        moodCount = item.moodCount
                                    } else {
                                        healthCount = -1
                                        productivityCount = -1
                                        moodCount = -1
                                    }
                                    runOnUiThread {
                                        when (healthCount) {
                                            0 -> {
                                                heart1 = true
                                                heart2 = false
                                                heart3 = false
                                                ivHeart1.enableHeart()
                                                ivHeart2.disableHeart()
                                                ivHeart3.disableHeart()
                                                tvDescription.visible()
                                                tvDescription.text = "Poor"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }

                                            1 -> {

                                                ivHeart1.enableHeart()
                                                ivHeart2.enableHeart()
                                                ivHeart3.disableHeart()
                                                heart1 = true
                                                heart2 = true
                                                heart3 = false
                                                tvDescription.visible()
                                                tvDescription.text = "Good"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }

                                            2 -> {
                                                ivHeart1.enableHeart()
                                                ivHeart2.enableHeart()
                                                ivHeart3.enableHeart()
                                                tvDescription.visible()
                                                heart1 = true
                                                heart2 = true
                                                heart3 = true
                                                tvDescription.text = "Excellent"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }
                                            else->{
                                                ivHeart1.disableHeart()
                                                ivHeart2.disableHeart()
                                                ivHeart3.disableHeart()
                                                tvDescription.invisible()
                                                heart1 = false
                                                heart2 = false
                                                heart3 = false
                                                tvDescription.text = "Excellent"
                                                tvDescription.invisible()
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }
                                        }
                                        when (productivityCount) {
                                            0 -> {
                                                star1 = true
                                                star2 = false
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.disableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Poor"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }

                                            1 -> {
                                                star1 = true
                                                star2 = true
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.enableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Good"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }

                                            2 -> {
                                                star1 = true
                                                star2 = true
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.enableStar()
                                                ivStar3.enableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Excellent"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }
                                            else->{
                                                star1 = false
                                                star2 = false
                                                star3 = false
                                                ivStar1.disableStar()
                                                ivStar2.disableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.invisible()
                                                tvDescription2.text = "Excellent"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }
                                        }
                                        when (moodCount) {
                                            0 -> {
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.enableAngryEmoji()
                                                tvAngry.enableText()
                                                emoji1 = true
                                                emoji2 = false
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }

                                            1 -> {
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.enableSadEmoji()
                                                tvSad.enableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = true
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }

                                            2 -> {
                                                ivHappy.enableHappyEmoji()
                                                tvHappy.enableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = false
                                                emoji3 = true
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }
                                            else->{
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = false
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }
                                        }
                                    }
                                }, {})
                        }else{
                            Amplify.DataStore.query(
                                KnowYourDay::class.java, Where.matches(
                                    KnowYourDay.DEVICE_ID.eq(deviceId)
                                        .and(KnowYourDay.DATE.eq(selectedDate))
                                ), { data ->
                                    if (data.hasNext()) {
                                        val item = data.next()
                                        healthCount = item.healthCount
                                        productivityCount = item.productivityCount
                                        moodCount = item.moodCount
                                    } else {
                                        healthCount = -1
                                        productivityCount = -1
                                        moodCount = -1
                                    }
                                    runOnUiThread {
                                        when (healthCount) {
                                            0 -> {
                                                heart1 = true
                                                heart2 = false
                                                heart3 = false
                                                ivHeart1.enableHeart()
                                                ivHeart2.disableHeart()
                                                ivHeart3.disableHeart()
                                                tvDescription.visible()
                                                tvDescription.text = "Poor"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }

                                            1 -> {

                                                ivHeart1.enableHeart()
                                                ivHeart2.enableHeart()
                                                ivHeart3.disableHeart()
                                                heart1 = true
                                                heart2 = true
                                                heart3 = false
                                                tvDescription.visible()
                                                tvDescription.text = "Good"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }

                                            2 -> {
                                                ivHeart1.enableHeart()
                                                ivHeart2.enableHeart()
                                                ivHeart3.enableHeart()
                                                tvDescription.visible()
                                                heart1 = true
                                                heart2 = true
                                                heart3 = true
                                                tvDescription.text = "Excellent"
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }
                                            else->{
                                                ivHeart1.disableHeart()
                                                ivHeart2.disableHeart()
                                                ivHeart3.disableHeart()
                                                tvDescription.invisible()
                                                heart1 = false
                                                heart2 = false
                                                heart3 = false
                                                tvDescription.text = "Excellent"
                                                tvDescription.invisible()
                                                binding.btnNext1.isEnabled =
                                                    heart1 || heart2 || heart3
                                            }
                                        }
                                        when (productivityCount) {
                                            0 -> {
                                                star1 = true
                                                star2 = false
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.disableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Poor"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }

                                            1 -> {
                                                star1 = true
                                                star2 = true
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.enableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Good"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }

                                            2 -> {
                                                star1 = true
                                                star2 = true
                                                star3 = false
                                                ivStar1.enableStar()
                                                ivStar2.enableStar()
                                                ivStar3.enableStar()
                                                tvDescription2.visible()
                                                tvDescription2.text = "Excellent"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }
                                            else->{
                                                star1 = false
                                                star2 = false
                                                star3 = false
                                                ivStar1.disableStar()
                                                ivStar2.disableStar()
                                                ivStar3.disableStar()
                                                tvDescription2.invisible()
                                                tvDescription2.text = "Excellent"
                                                binding.btnNext2.isEnabled =
                                                    star1 || star2|| star3
                                            }
                                        }
                                        when (moodCount) {
                                            0 -> {
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.enableAngryEmoji()
                                                tvAngry.enableText()
                                                emoji1 = true
                                                emoji2 = false
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }

                                            1 -> {
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.enableSadEmoji()
                                                tvSad.enableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = true
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }

                                            2 -> {
                                                ivHappy.enableHappyEmoji()
                                                tvHappy.enableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = false
                                                emoji3 = true
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }
                                            else->{
                                                ivHappy.disableHappyEmoji()
                                                tvHappy.disableText()
                                                ivSad.disableSadEmoji()
                                                tvSad.disableText()
                                                ivAngry.disableAngryEmoji()
                                                tvAngry.disableText()
                                                emoji1 = false
                                                emoji2 = false
                                                emoji3 = false
                                                binding.btnDone.isEnabled =
                                                    emoji1 || emoji2|| emoji3
                                            }
                                        }
                                    }
                                }, {})
                        }

                    }
                }
            })
        rvDate.adapter = adapter
        adapter.setItems(AppUtil.getTemporalDateOfCurrentWeek())
        setContentView(binding.root)
        create()
        show()
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.llHealth.visible()
        binding.llProductivity.gone()
        binding.llMood.gone()
        binding.iVBack.setOnClickListener { dismiss() }
        binding.tvTitle.text = "Enhancing Health"
        binding.tvDescription.invisible()
        binding.btnNext1.isEnabled = false
        ivHeart1.setOnClickListener { iv ->
            if (heart1) {
                if(heart2) {
                    heart1 = true
                    heart2 = false
                    heart3 = false
                    ivHeart1.enableHeart()
                    ivHeart2.disableHeart()
                    ivHeart3.disableHeart()
                    tvDescription.visible()
                    tvDescription.text = "Poor"
                }else{
                    heart1 = false
                    heart2 = false
                    heart3 = false
                    ivHeart1.disableHeart()
                    ivHeart2.disableHeart()
                    ivHeart3.disableHeart()
                    tvDescription.invisible()
                }
            } else {
                heart1 = true
                heart2 = false
                heart3 = false
                ivHeart1.enableHeart()
                ivHeart2.disableHeart()
                ivHeart3.disableHeart()
                tvDescription.visible()
            }
            tvDescription.text = "Poor"
            healthCount = if (heart3) {
                2
            } else if (heart2) {
                1
            } else if (heart1) {
                0
            } else -1
            binding.btnNext1.isEnabled =
                heart1 || heart2 || heart3
        }
        ivHeart2.setOnClickListener { iv ->
            if (heart2) {
                if(heart3) {
                    ivHeart1.enableHeart()
                    ivHeart2.enableHeart()
                    ivHeart3.disableHeart()
                    heart1 = true
                    heart2 = true
                    heart3 = false
                    tvDescription.visible()
                    tvDescription.text = "Good"
                }else{
                    ivHeart1.enableHeart()
                    ivHeart2.disableHeart()
                    ivHeart3.disableHeart()
                    heart1 = true
                    heart2 = false
                    heart3 = false
                    tvDescription.visible()
                    tvDescription.text = "Poor"
                }
            } else {
                ivHeart1.enableHeart()
                ivHeart2.enableHeart()
                ivHeart3.disableHeart()
                heart1 = true
                heart2 = true
                heart3=false
                tvDescription.visible()
                tvDescription.text = "Good"
            }
            healthCount = if (heart3) {
                2
            } else if (heart2) {
                1
            } else if (heart1) {
                0
            } else -1
            binding.btnNext1.isEnabled =
                heart1 || heart2 || heart3
        }
        ivHeart3.setOnClickListener { iv ->
            if (heart3) {
                ivHeart1.enableHeart()
                ivHeart2.enableHeart()
                ivHeart3.disableHeart()
                heart1 = true
                heart2 = true
                heart3 = false
                tvDescription.visible()
                tvDescription.text = "Good"
            } else {
                ivHeart1.enableHeart()
                ivHeart2.enableHeart()
                ivHeart3.enableHeart()
                heart1 = true
                heart2 = true
                heart3 = true
                tvDescription.visible()
                tvDescription.text = "Excellent"
            }

            healthCount = if (heart3) {
                2
            } else if (heart2) {
                1
            } else if (heart1) {
                0
            } else -1
            binding.btnNext1.isEnabled =
                heart1 || heart2 || heart3
        }

        binding.btnNext1.setOnClickListener {
            binding.tvTitle.text = "Empowering Productivity"
            binding.btnNext1.gone()
            binding.llHealth.gone()
            binding.llProductivity.visible()
            binding.llMood.gone()
            binding.tvDescription2.visible()
            binding.tvDescription.invisible()
        }

        binding.btnNext2.isEnabled = false


        tvDescription2.visible()
        ivStar1.setOnClickListener {
            if(star1) {
                if(star2){
                    ivStar1.enableStar()
                    ivStar2.disableStar()
                    ivStar3.disableStar()
                    star1 = true
                    star2 = false
                    star3 = false
                    tvDescription2.visible()
                    tvDescription2.text = "Poor"
                }else{
                    ivStar1.disableStar()
                    ivStar2.disableStar()
                    ivStar3.disableStar()
                    star1 = false
                    star2 = false
                    star3 = false
                    tvDescription2.invisible()
                }
            }else{
                ivStar1.enableStar()
                ivStar2.disableStar()
                ivStar3.disableStar()
                star1 = true
                star2 = false
                star3 = false
                tvDescription2.visible()
            }
            tvDescription2.text = "Poor"
            productivityCount = if (star3) {
                2
            } else if (star2) {
                1
            } else if (star1) {
                0
            } else -1
            binding.btnNext2.isEnabled =
                star1 || star2 || star3
        }
        ivStar2.setOnClickListener {
            tvDescription2.visible()
            if(star2) {
                if(star3){
                    ivStar1.enableStar()
                    ivStar2.enableStar()
                    ivStar3.disableStar()
                    star1 = true
                    star2 = true
                    star3 = false
                    tvDescription2.text = "Good"
                }else {
                    ivStar1.enableStar()
                    ivStar2.disableStar()
                    ivStar3.disableStar()
                    star1 = true
                    star2 = false
                    star3 = false
                    tvDescription2.text = "Poor"
                }
            }else{
                ivStar1.enableStar()
                ivStar2.enableStar()
                ivStar3.disableStar()
                star1 = true
                star2 = true
                star3 = false
                tvDescription2.text = "Good"
            }
            productivityCount = if (star3) {
                2
            } else if (star2) {
                1
            } else if (star1) {
                0
            } else -1
            binding.btnNext2.isEnabled =
                star1 || star2 || star3
        }
        ivStar3.setOnClickListener {
            tvDescription2.visible()
            if(star3) {
                ivStar1.enableStar()
                ivStar2.enableStar()
                ivStar3.disableStar()
                star1 = true
                star2 = true
                star3 = false
                tvDescription2.text = "Good"
            }else{
                ivStar1.enableStar()
                ivStar2.enableStar()
                ivStar3.enableStar()
                star1 = true
                star2 = true
                star3 = true
                tvDescription2.text = "Excellent"
            }
            productivityCount = if (star3) {
                2
            } else if (star2) {
                1
            } else if (star1) {
                0
            } else -1
            binding.btnNext2.isEnabled =
                star1 || star2 || star3
        }

        binding.btnNext2.setOnClickListener {
            binding.tvTitle.text = "Uplifting Moods"
            binding.llHealth.gone()
            binding.llProductivity.gone()
            binding.llMood.visible()
        }

        binding.btnDone.isEnabled = false
        llAngry.setOnClickListener {
            if(emoji1) {
                ivHappy.disableHappyEmoji()
                tvHappy.disableText()
                ivSad.disableSadEmoji()
                tvSad.disableText()
                ivAngry.disableAngryEmoji()
                tvAngry.disableText()
                emoji1 = false
                emoji2 = false
                emoji3 = false
            }else{
                ivHappy.disableHappyEmoji()
                tvHappy.disableText()
                ivSad.disableSadEmoji()
                tvSad.disableText()
                ivAngry.enableAngryEmoji()
                tvAngry.enableText()
                emoji1 = true
                emoji2 = false
                emoji3 = false
            }
            moodCount = if (emoji3) {
                2
            } else if (emoji2) {
                1
            } else if (emoji1) {
                0
            } else -1
            binding.btnDone.isEnabled =
                emoji1 || emoji2 || emoji3
        }
        llSad.setOnClickListener {
            if(emoji2) {
                ivHappy.disableHappyEmoji()
                tvHappy.disableText()
                ivSad.disableSadEmoji()
                tvSad.disableText()
                ivAngry.disableAngryEmoji()
                tvAngry.disableText()
                emoji1 = false
                emoji2 = false
                emoji3 = false
            }else{
                ivHappy.disableHappyEmoji()
                tvHappy.disableText()
                ivSad.enableSadEmoji()
                tvSad.enableText()
                ivAngry.disableAngryEmoji()
                tvAngry.disableText()
                emoji1 = false
                emoji2 = true
                emoji3 = false
            }
            moodCount = if (emoji3) {
                2
            } else if (emoji2) {
                1
            } else if (emoji1) {
                0
            } else -1
            binding.btnDone.isEnabled =
                emoji1 || emoji2 || emoji3

        }
        llHappy.setOnClickListener {
            if(emoji3) {
                ivHappy.disableHappyEmoji()
                tvHappy.disableText()
                ivSad.disableSadEmoji()
                tvSad.disableText()
                ivAngry.disableAngryEmoji()
                tvAngry.disableText()
                emoji1 = false
                emoji2 = false
                emoji3 = false
            }else{
                ivHappy.enableHappyEmoji()
                tvHappy.enableText()
                ivSad.disableSadEmoji()
                tvSad.disableText()
                ivAngry.disableAngryEmoji()
                tvAngry.disableText()
                emoji1 = false
                emoji2 = false
                emoji3 = true
            }
            moodCount = if (emoji3) {
                2
            } else if (emoji2) {
                1
            } else if (emoji1) {
                0
            } else -1
            binding.btnDone.isEnabled =
                emoji1 || emoji2 || emoji3
        }
        binding.btnDone.setOnClickListener {
            listener.onDone(
                this,
                selectedDate,
                healthCount = healthCount,
                productivityCount = productivityCount,
                moodCount = moodCount
            )
        }

    }