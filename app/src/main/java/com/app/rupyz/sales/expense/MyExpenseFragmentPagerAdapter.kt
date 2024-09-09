package com.app.rupyz.sales.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.helper.FragmentObserver
import com.app.rupyz.generic.utils.AppConstant
import java.util.*

class MyExpenseFragmentPagerAdapter(
    context: FragmentActivity,
    var fragmentList: ArrayList<String>
) :
    FragmentStateAdapter(context) {
    private val mObservers: Observable = FragmentObserver()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        mObservers.deleteObservers();

        val fragment = AllExpenseListFragment()
        val args = Bundle()
        args.putString(AppConstant.TAB_NAME, fragmentList[position])
        fragment.arguments = args

        mObservers.addObserver(fragment as Observer)

        return fragment
    }

    fun updateFragments() {
        mObservers.notifyObservers()
    }

}