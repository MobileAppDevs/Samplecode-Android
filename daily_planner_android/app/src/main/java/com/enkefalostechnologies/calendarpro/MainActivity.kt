package com.enkefalostechnologies.calendarpro

import android.view.View
import com.enkefalostechnologies.calendarpro.databinding.ActivityMainBinding
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onViewBindingCreated() {
//        val adapter=CalenderLinearViewAdapter(Date(),listener= object : CalenderListener {
//            override fun onDateSelected(date: Date) {
//
//            }
//        })
//        binding.rvCalenderView.layoutManager=
//            GridLayoutManager(this, 7, GridLayoutManager.VERTICAL, false)
//        val list=CalenderUtil.getDatesForCalenderView(2023,11)
//        adapter.setItems(list, CalenderUtil.getDatesForCurrentMonth(2023,11))
//       binding.rvCalenderView.adapter=adapter
    }

    override fun addObserver() {

    }

    override fun removeObserver() {

    }

    override fun onClick(p0: View?) {

    }



}