package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentBeatPlanListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomerFollowUpDataItem

class BeatPlanHistoryFragment : BaseFragment() {
    private lateinit var binding: FragmentBeatPlanListBinding
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var beatPlanHistoryAdapter: BeatHistoryAdapter
    private var beatList: ArrayList<CustomerFollowUpDataItem> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var moduleId: Int? = 0

    companion object {
        fun getInstance(): BeatPlanHistoryFragment {
            return BeatPlanHistoryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeatPlanListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(requireActivity())[BeatViewModel::class.java]

        moduleId = arguments?.getInt(AppConstant.BEAT_ID, 0)

        binding.tabLayout.visibility = View.GONE

        initRecyclerView()
        initObservers()

        loadBeatHistory()
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        beatPlanHistoryAdapter = BeatHistoryAdapter(
            beatList
        )

        binding.rvList.adapter = beatPlanHistoryAdapter

        binding.rvList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadBeatHistory()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadBeatHistory() {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getBeatPlanHistory(moduleId, currentPage)
    }

    private fun initObservers() {
        beatViewModel.beatPlanHistoryLiveData.observe(requireActivity()) {
            isPageLoading = false
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                if (it.error == false) {
                    if (it.data.isNullOrEmpty().not()) {
                        binding.clEmptyData.visibility = View.GONE
                        it.data?.let { list ->
                            if (currentPage == 1) {
                                beatList.clear()
                            }

                            beatList.addAll(list)
                            beatPlanHistoryAdapter.notifyDataSetChanged()
                            if (list.size < 30) {
                                isApiLastPage = true
                            }
                        }
                    } else if (currentPage == 1) {
                        binding.clEmptyData.visibility = View.VISIBLE
                        isApiLastPage = true
                        beatList.clear()
                        beatPlanHistoryAdapter.notifyDataSetChanged()
                    } else {
                        isApiLastPage = true
                    }
                } else {
                    showToast(it.message)
                }
            }
        }
    }
}