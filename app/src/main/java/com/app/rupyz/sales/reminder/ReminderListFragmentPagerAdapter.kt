package com.app.rupyz.sales.reminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.helper.FragmentObserver
import com.app.rupyz.generic.utils.AppConstant
import java.util.*

class ReminderListFragmentPagerAdapter(
    context: FragmentActivity,
    var fragmentList: ArrayList<String>
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {

        if (position == 0 || position == 1) {
            val fragment = TodayTomorrowReminderFragment()
            val args = Bundle()
            args.putString(AppConstant.TAB_NAME, fragmentList[position])
            fragment.arguments = args
            return fragment
        } else {
            val fragment = WeekMonthReminderFragment()
            val args = Bundle()
            args.putString(AppConstant.TAB_NAME, fragmentList[position])
            fragment.arguments = args
            return fragment
        }
    }
}