package com.ongraph.swipe.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private var totalNumberOfScreens:ArrayList<Fragment> = ArrayList()

    override fun createFragment(position: Int): Fragment {
        return totalNumberOfScreens[position]
    }

    override fun getItemCount(): Int {
        return totalNumberOfScreens.size
    }

    fun addScreenFragment(fragment: Fragment) {
        totalNumberOfScreens.add(fragment)
    }

}