package com.app.rupyz.sales.beat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentStaffAssignToBeatListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.StaffModelForBeatData
import com.app.rupyz.sales.beatplan.BeatViewModel

class StaffAssignedToBeatListFragment : BaseFragment() {
    private lateinit var binding: FragmentStaffAssignToBeatListBinding

    private val beatViewModel: BeatViewModel by viewModels()

    private var staffList: ArrayList<StaffModelForBeatData> = ArrayList()

    private lateinit var staffListForAssignAdapter: StaffAssignForBeatAdapter

    private var staffCurrentPage = 1
    private var beatId: Int? = 0

    private var isPageLoading = false
    private var isApiLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignToBeatListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beatId = arguments?.getInt(AppConstant.BEAT_ID_FOR_ASSIGN_STAFF, 0)

        initRecyclerView()

        initObservers()
        loadStaff()
    }

    private fun initObservers() {
        beatViewModel.staffListWithCustomerBeatLiveDataWithInfo.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            isPageLoading = false
            if (!it.data.isNullOrEmpty()) {
                isPageLoading = false
                if (staffCurrentPage == 1) {
                    staffList.clear()
                }

                staffList.addAll(it.data)

                staffListForAssignAdapter.notifyDataSetChanged()

                if (it.data.size < 30) {
                    isApiLastPage = true
                }
            } else {
                isApiLastPage = true
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvStaffList.layoutManager = linearLayoutManager
        staffListForAssignAdapter = StaffAssignForBeatAdapter(staffList)
        binding.rvStaffList.adapter = staffListForAssignAdapter


        binding.rvStaffList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                staffCurrentPage += 1
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadStaff() {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getStaffListWithBeatMappingWithData(
            beatId ?: 0,
            "",
            true,
            staffCurrentPage
        )
    }
}