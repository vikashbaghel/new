package com.app.rupyz.sales.beatplan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BeatFragmentPagerAdapter(
    var context: FragmentActivity,
    var fragmentList: ArrayList<String>,
    var beatID: Int,
    var beatDate: String,
    var customerLevel: String
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            AssignedCustomerBeatFragment.getInstance(beatID, beatDate, customerLevel)
        } else {
            AssignedLeadsBeatFragment.getInstance(beatID, beatDate)
        }
    }

}