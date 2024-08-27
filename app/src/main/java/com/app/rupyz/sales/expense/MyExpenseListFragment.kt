package com.app.rupyz.sales.expense

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.app.rupyz.databinding.ExpenseTrackerFragmentBinding
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.tabs.TabLayout

class MyExpenseListFragment : Fragment() {
    private lateinit var binding: ExpenseTrackerFragmentBinding
    private lateinit var myExpenseFragmentPagerAdapter: MyExpenseFragmentPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExpenseTrackerFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()

        binding.tvAddNewExpense.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    requireContext(),
                    AddExpenseHeadActivity::class.java
                )
            )
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            myExpenseFragmentPagerAdapter.updateFragments()
        }
    }

    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ALL))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ACTIVE))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.SUBMITTED))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.APPROVED))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.REJECTED))

        val fragmentList = arrayListOf(
            AppConstant.ALL,
            AppConstant.ACTIVE,
            AppConstant.SUBMITTED,
            AppConstant.APPROVED,
            AppConstant.REJECTED
        )

        myExpenseFragmentPagerAdapter = MyExpenseFragmentPagerAdapter(
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
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }
}