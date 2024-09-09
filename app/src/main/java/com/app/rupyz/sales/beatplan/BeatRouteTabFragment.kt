package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentBeatRouteTabBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref

class BeatRouteTabFragment : BaseFragment() {
    private lateinit var binding: FragmentBeatRouteTabBinding
    private val myBeatFragment = AllBeatPlanListFragment()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeatRouteTabBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        bundle.putBoolean(AppConstant.MY_BEAT_PLAN, true)
        myBeatFragment.arguments = bundle

        if (hasInternetConnection()) {
            replaceFragment(R.id.frame_container, myBeatFragment)
        }


        if (arguments?.getBoolean(AppConstant.VIEW_BEAT_PLAN, false) == true) {
            initLayout()
        }

        binding.tvMyBeatPlan.setOnClickListener {
            binding.tvMyBeatPlan.setBackgroundColor(resources.getColor(R.color.theme_purple))
            binding.tvPendingBeatPlan.setBackgroundColor(resources.getColor(R.color.white))
            binding.tvMyBeatPlan.setTextColor(resources.getColor(R.color.white))
            binding.tvPendingBeatPlan.setTextColor(resources.getColor(R.color.expense_dark_gray))

            replaceFragment(R.id.frame_container, myBeatFragment)
        }

        binding.tvPendingBeatPlan.setOnClickListener {
            binding.tvMyBeatPlan.setBackgroundColor(resources.getColor(R.color.white))
            binding.tvPendingBeatPlan.setBackgroundColor(resources.getColor(R.color.theme_purple))

            binding.tvMyBeatPlan.setTextColor(resources.getColor(R.color.expense_dark_gray))
            binding.tvPendingBeatPlan.setTextColor(resources.getColor(R.color.white))

            replaceFragment(R.id.frame_container, PendingBeatPlanListFragment.getInstance())
        }
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        /* Setting the fragment on SetMenuVisibility because if "Hierarchy" permission is changes
            dynamically from the backend wee need to replace fragment accordingly
            */

        if (menuVisible) {
            initLayout()
        }
    }

    private fun initLayout() {
        if (hasInternetConnection()) {
            binding.offlineLayout.clMain.visibility = View.GONE
            if (isStaffUser()) {
                if (SharedPref.getInstance().getBoolean(AppConstant.STAFF_HIERARCHY, false)) {
                    binding.clExpensesTab.visibility = View.VISIBLE
                } else {
                    binding.clExpensesTab.visibility = View.GONE
                }
            } else {
                binding.clExpensesTab.visibility = View.GONE
                replaceFragment(R.id.frame_container, PendingBeatPlanListFragment.getInstance())
            }
        } else {
            binding.offlineLayout.clMain.visibility = View.VISIBLE

            binding.offlineLayout.btnRetry.setOnClickListener {
                if (hasInternetConnection()) {
                    initLayout()
                    replaceFragment(R.id.frame_container, myBeatFragment)
                } else {
                    showToast(resources.getString(R.string.make_sure_have_internet_connection))
                }
            }
        }
    }
}