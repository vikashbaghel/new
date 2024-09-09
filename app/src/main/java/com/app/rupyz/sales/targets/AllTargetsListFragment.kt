package com.app.rupyz.sales.targets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.databinding.AllTargetsListFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.tabs.TabLayout

class AllTargetsListFragment: BaseFragment() {
    private lateinit var binding: AllTargetsListFragmentBinding
    private lateinit var targetListFragmentPagerAdapter: TargetListFragmentPagerAdapter
    private var staffId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllTargetsListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            staffId = arguments?.getInt(AppConstant.STAFF_ID)
        }

        initTabLayout()
    }


    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ACTIVE_TARGET))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.UPCOMING_TARGET))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.CLOSED_TARGET))

        val fragmentList = arrayListOf(
            AppConstant.ACTIVE_TARGET,
            AppConstant.UPCOMING_TARGET,
            AppConstant.CLOSED_TARGET
        )

        targetListFragmentPagerAdapter = TargetListFragmentPagerAdapter(
            requireActivity(), fragmentList, staffId
        )

        binding.viewPager.adapter = targetListFragmentPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }
}