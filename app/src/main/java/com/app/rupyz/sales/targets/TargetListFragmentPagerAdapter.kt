package com.app.rupyz.sales.targets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.utils.AppConstant

class TargetListFragmentPagerAdapter(
    var context: FragmentActivity,
    var fragmentList: ArrayList<String>,
    var staffId: Int?
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment
        if (position == 0) {
            fragment = TargetDetailsFragment()
            val args = Bundle()
            args.putString(AppConstant.TAB_NAME, fragmentList[position])
            args.putInt(AppConstant.STAFF_ID, staffId ?: 0)
            fragment.arguments = args
        } else {
            fragment = UpcomingAndClosedTargetDetailsFragment()
            val args = Bundle()
            args.putString(AppConstant.TAB_NAME, fragmentList[position])
            args.putInt(AppConstant.STAFF_ID, staffId ?: 0)
            fragment.arguments = args
        }
        return fragment
    }

}