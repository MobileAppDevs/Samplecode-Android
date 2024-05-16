package com.enkefalostechnologies.calendarpro.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.enkefalostechnologies.calendarpro.ui.fragment.HealthViewFragment

class PageAdapter(fm: FragmentManager,var list:List<Fragment>) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return list.size;
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Health"
            }
            1 -> {
                return "Productivity"
            }
            2 -> {
                return "Mood"
            }
        }
        return super.getPageTitle(position)
    }

}