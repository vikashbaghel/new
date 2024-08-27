package com.app.rupyz.sales.staffactivitytrcker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTrackerFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref

class ActivityTrackerListFragment : BaseFragment() {
    private lateinit var binding: ActivityTrackerFragmentBinding
    private lateinit var staffActivityFragmentPagerAdapter: StaffActivityFragmentPagerAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = ActivityTrackerFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
    }

    private fun initLayout() {
        binding.tvMyActivity.setOnClickListener {
            changeMyActivityTab()
            binding.viewPager.currentItem = 1
        }

        binding.tvTeamActivity.setOnClickListener {
            changeTeamActivityTab()
            binding.viewPager.currentItem = 0
        }
    }

    private fun initTabLayout() {

        if (hasInternetConnection()) {
            binding.offlineLayout.clMain.visibility = View.GONE
            binding.clStaffActivity.visibility = View.VISIBLE
            binding.viewPager.visibility = View.VISIBLE
            val fragmentList: ArrayList<String>

            if (isStaffUser()) {
                if (SharedPref.getInstance().getBoolean(AppConstant.STAFF_HIERARCHY, false)) {
                    binding.clStaffActivity.visibility = View.VISIBLE
                    fragmentList = arrayListOf(
                            AppConstant.TEAM_INFO,
                            AppConstant.ALL,
                    )
                } else {
                    binding.clStaffActivity.visibility = View.GONE
                    fragmentList = arrayListOf(
                            AppConstant.ALL
                    )
                }
            } else {
                binding.clStaffActivity.visibility = View.VISIBLE
                fragmentList = arrayListOf(
                        AppConstant.TEAM_INFO,
                        AppConstant.ALL,
                )
            }


            staffActivityFragmentPagerAdapter = StaffActivityFragmentPagerAdapter(
                    requireActivity(), fragmentList, null
            )

            binding.viewPager.adapter = staffActivityFragmentPagerAdapter

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 0) {
                        changeTeamActivityTab()
                    } else {
                        changeMyActivityTab()
                    }
                }
            })
        } else {
            binding.offlineLayout.clMain.visibility = View.VISIBLE
            binding.clStaffActivity.visibility = View.GONE
            binding.viewPager.visibility = View.GONE

            binding.offlineLayout.btnRetry.setOnClickListener {
                if (hasInternetConnection()) {
                    initTabLayout()
                } else {
                    showToast(resources.getString(R.string.make_sure_have_internet_connection))
                }
            }
        }
    }

    private fun changeTeamActivityTab() {
        binding.tvMyActivity.setBackgroundColor(resources.getColor(R.color.white))
        binding.tvTeamActivity.setBackgroundColor(resources.getColor(R.color.theme_purple))

        binding.tvMyActivity.setTextColor(resources.getColor(R.color.expense_dark_gray))
        binding.tvTeamActivity.setTextColor(resources.getColor(R.color.white))

    }

    private fun changeMyActivityTab() {
        binding.tvMyActivity.setBackgroundColor(resources.getColor(R.color.theme_purple))
        binding.tvTeamActivity.setBackgroundColor(resources.getColor(R.color.white))

        binding.tvMyActivity.setTextColor(resources.getColor(R.color.white))
        binding.tvTeamActivity.setTextColor(resources.getColor(R.color.expense_dark_gray))
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            initTabLayout()
        }
    }
}