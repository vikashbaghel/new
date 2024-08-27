package com.app.rupyz.sales.staffactivitytrcker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class StaffActivityFragmentPagerAdapter(
        var context: FragmentActivity,
        var fragmentList: ArrayList<String>,
        var staffId: Int?
) :
        FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (fragmentList.size > 1) {
            if (position == 0) {
                TeamActivityFragment()
            } else {
                MyActivityFragment()
            }
        } else {
            MyActivityFragment()
        }
    }

}