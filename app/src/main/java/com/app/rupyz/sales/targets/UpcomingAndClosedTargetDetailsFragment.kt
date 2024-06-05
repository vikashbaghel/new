package com.app.rupyz.sales.targets

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.UpcomingCloedTargetsDetailsFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity

class UpcomingAndClosedTargetDetailsFragment : BaseFragment(),
    UpcomingAndClosedTargetsListRvAdapter.ITargetProductActionListener{
    private lateinit var binding: UpcomingCloedTargetsDetailsFragmentBinding
    private lateinit var staffViewModel: StaffViewModel
    private lateinit var upcomingAndClosedTargetsListRvAdapter: UpcomingAndClosedTargetsListRvAdapter

    private var upcomingClosedList = ArrayList<StaffCurrentlyActiveDataModel>()
    private var tab = ""
    private var staffId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UpcomingCloedTargetsDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        arguments?.let {
            tab = arguments?.getString(AppConstant.TAB_NAME)!!
            staffId = arguments?.getInt(AppConstant.STAFF_ID)
        }

        initUpcomingAndClosedRecyclerView()
        initObservers()

        when (tab) {
            AppConstant.UPCOMING_TARGET -> {
                staffViewModel.getUpcomingAndClosedTargets(upcoming = true, closed = false, staffId)
            }
            else -> {
                staffViewModel.getUpcomingAndClosedTargets(
                    upcoming = false,
                    closed = true,
                    staffId = staffId
                )
            }
        }
    }


    private fun initUpcomingAndClosedRecyclerView() {
        binding.rvTarget.layoutManager = LinearLayoutManager(requireContext())
        upcomingAndClosedTargetsListRvAdapter =
            UpcomingAndClosedTargetsListRvAdapter(upcomingClosedList, this)
        binding.rvTarget.adapter = upcomingAndClosedTargetsListRvAdapter
    }


    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        staffViewModel.staffUpcomingClosedTargetsLiveData.observe(requireActivity()) {
            if (it.error == false) {
                if (!it.data.isNullOrEmpty()) {
                    upcomingClosedList.addAll(it.data)
                    upcomingAndClosedTargetsListRvAdapter.notifyDataSetChanged()
                } else {
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getTargetProductDetails(model: StaffCurrentlyActiveDataModel) {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.TARGET_PRODUCTS,
                true
            ).putExtra(AppConstant.TARGET_PRODUCTS_LIST, model)
        )
    }
}