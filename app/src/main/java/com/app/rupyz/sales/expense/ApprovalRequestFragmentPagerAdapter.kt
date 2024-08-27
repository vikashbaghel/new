package com.app.rupyz.sales.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.utils.AppConstant

class ApprovalRequestFragmentPagerAdapter(context: FragmentActivity, var fragmentList: ArrayList<String>) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = AllApprovalRequestListFragment()
        val args = Bundle()
        args.putString(AppConstant.TAB_NAME, fragmentList[position])
        fragment.arguments = args
        return fragment
    }
}