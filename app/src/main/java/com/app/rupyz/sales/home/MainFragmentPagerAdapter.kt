package com.app.rupyz.sales.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.sales.beatplan.BeatRouteTabFragment
import com.app.rupyz.sales.expense.ExpenseTrackerListFragment
import com.app.rupyz.sales.staffactivitytrcker.StaffStatusTrackerFragment

class MainFragmentPagerAdapter(
    context: FragmentActivity,
    var fragmentList: ArrayList<String>,
    var listener: SalesFragment.UpdateMainDataListener
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return SalesFragment.getInstance(listener)
            1 -> return StaffStatusTrackerFragment()
            2 -> return ExpenseTrackerListFragment()
            3 -> return BeatRouteTabFragment()
        }
        return SalesFragment.getInstance(listener)
    }
}