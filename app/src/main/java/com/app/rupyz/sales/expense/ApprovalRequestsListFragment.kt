package com.app.rupyz.sales.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.app.rupyz.R
import com.app.rupyz.databinding.ExpenseTrackerFragmentBinding
import com.app.rupyz.databinding.LeadListLayoutBinding
import com.app.rupyz.generic.utils.AppConstant

import com.google.android.material.tabs.TabLayout


class ApprovalRequestsListFragment : Fragment() {

    private lateinit var myExpenseFragmentPagerAdapter: ApprovalRequestFragmentPagerAdapter

    private lateinit var binding: ExpenseTrackerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ExpenseTrackerFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
 
 
 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()

        binding.tvAddNewExpense.visibility = View.GONE
    }

    private fun initTabLayout() {
         binding.tabLayout.addTab( binding.tabLayout.newTab().setText(AppConstant.ALL))
         binding.tabLayout.addTab( binding.tabLayout.newTab().setText(AppConstant.PENDING_EXPENSE))
         binding.tabLayout.addTab( binding.tabLayout.newTab().setText(AppConstant.APPROVED))
         binding.tabLayout.addTab( binding.tabLayout.newTab().setText(AppConstant.PAID))
         binding.tabLayout.addTab( binding.tabLayout.newTab().setText(AppConstant.REJECTED))

        val fragmentList = arrayListOf(
            AppConstant.ALL,
            AppConstant.PENDING_EXPENSE,
            AppConstant.APPROVED,
            AppConstant.PAID,
            AppConstant.REJECTED
        )

        myExpenseFragmentPagerAdapter = ApprovalRequestFragmentPagerAdapter(
            requireActivity(), fragmentList
        )

         binding.viewPager.adapter = myExpenseFragmentPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })



         binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                 binding.tabLayout.selectTab( binding.tabLayout.getTabAt(position))
            }
        })
    }
}