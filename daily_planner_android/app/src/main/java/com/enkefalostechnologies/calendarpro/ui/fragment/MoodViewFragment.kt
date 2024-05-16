package com.enkefalostechnologies.calendarpro.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.FragmentHealthViewBinding
import com.enkefalostechnologies.calendarpro.databinding.FragmentMoodViewBinding
import com.enkefalostechnologies.calendarpro.ui.adapter.BarData
import com.enkefalostechnologies.calendarpro.ui.adapter.BarGraphAdapter
import com.enkefalostechnologies.calendarpro.ui.adapter.BarGraphAdapter2
import com.enkefalostechnologies.calendarpro.ui.base.BaseFragment


class MoodViewFragment()
    : BaseFragment<FragmentMoodViewBinding>(R.layout.fragment_mood_view) {
    var data: List<BarData> = arrayListOf(
        BarData("Mon", 0.toFloat()),
        BarData("Tue", 0.toFloat()),
        BarData("Wed", 0.toFloat()),
        BarData("Thus", 0.toFloat()),
        BarData("Fri", 0.toFloat()),
        BarData("Sat", 0.toFloat()),
        BarData("Sun", 0.toFloat())
    )
    private var healthGraphAdapter:BarGraphAdapter2?=null
    override fun setupViews() {}

    override fun onResume() {
        super.onResume()
        healthGraphAdapter = BarGraphAdapter2()
        healthGraphAdapter?.setItems(data)
        binding.rvBarGraph.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvBarGraph.adapter = healthGraphAdapter
    }

    override fun setupListeners() {

    }

    override fun fetchInitialData() {

    }

    override fun setupObservers() {

    }

    override fun removeObserver() {

    }

    fun setOrUpdateData(data: List<com.enkefalostechnologies.calendarpro.ui.adapter.BarData>){
         healthGraphAdapter?.let {
             it.setItems(data)
             it.notifyDataSetChanged()
         }
     }

}