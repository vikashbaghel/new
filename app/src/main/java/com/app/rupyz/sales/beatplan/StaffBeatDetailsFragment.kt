package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentStaffBeatDetailsBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatRouteInfoModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity

class StaffBeatDetailsFragment : BaseFragment() {
    private lateinit var binding: FragmentStaffBeatDetailsBinding
    private lateinit var beatViewModel: BeatViewModel
    private var staffId: Int? = null
    private var beatRouteInfoModel: BeatRouteInfoModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffBeatDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        initObservers()

        arguments?.let {
            staffId = arguments?.getInt(AppConstant.STAFF_ID)
            beatViewModel.getCurrentlyActiveBeatPlan(staffId, null)
        }

        binding.hdTargetViewAll.setOnClickListener {
            startActivity(
                Intent(requireContext(), FragmentContainerActivity::class.java)
                    .putExtra(AppConstant.ALL_BEAT_PLAN, true)
                    .putExtra(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, true)
                    .putExtra(AppConstant.USER_ID, staffId)
                    .putExtra(AppConstant.STAFF_DETAILS, true)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        beatViewModel.currentlyActiveBeatLiveData.observe(requireActivity()) {
            if (it.error == false) {
                if (it.data?.beatRouteInfo != null) {
                    it.data.beatRouteInfo?.let { info ->
                        binding.clEmptyData.visibility = View.GONE
                        beatRouteInfoModel = info

                        if (info.name.isNullOrEmpty().not()) {
                            binding.tvBeat.text = info.name
                        }

                        if (info.startDate.isNullOrEmpty().not() && info.endDate.isNullOrEmpty()
                                .not()
                        ) {
                            binding.tvBeatPlanDate.text =
                                "${DateFormatHelper.convertStringToMonthFormat(info.startDate)} - ${
                                    DateFormatHelper.convertStringToMonthFormat(info.endDate)
                                }"
                        }
                    }

                    if (it.data.beatRouteDayPlan != null) {
                        it.data.beatRouteDayPlan?.let { day ->
                            val fragment = AllDailyBeatPlanListFragment()
                            val bundle = Bundle()
                            bundle.putInt(AppConstant.BEAT_ID, day.beatrouteplan!!)
                            bundle.putBoolean(AppConstant.TARGET_VISITS, true)
                            fragment.arguments = bundle
                            replaceFragment(R.id.frame_container, fragment)
                        }
                    }
                } else {
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            } else {
                showToast("${it.message}")
            }
        }
    }
}