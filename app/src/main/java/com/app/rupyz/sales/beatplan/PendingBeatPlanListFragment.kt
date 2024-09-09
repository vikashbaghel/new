package com.app.rupyz.sales.beatplan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentBeatPlanListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.RecyclerTouchListener
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.google.android.material.tabs.TabLayout

class PendingBeatPlanListFragment : BaseFragment(),
    RecyclerTouchListener.ClickListener {
    private lateinit var binding: FragmentBeatPlanListBinding
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var beatPlanInfoAdapter: BeatPlanInfoAdapter
    private var beatList: ArrayList<BeatPlanModel> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    private var status: String = ""

    companion object {
        fun getInstance(): PendingBeatPlanListFragment {
            return PendingBeatPlanListFragment()
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

        initTabLayout()

        initRecyclerView()
        initObservers()

//        binding.swipeToRefresh.setOnRefreshListener {
//            binding.swipeToRefresh.isRefreshing = false
//            currentPage = 1
//            isPageLoading = true
//            loadBeatList()
//        }
    }

    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ALL))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.APPROVAL))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ACTIVE))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.UPCOMING_TARGET))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.CLOSED))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                status = when (tab.text) {
                    AppConstant.ALL -> ""
                    AppConstant.APPROVAL -> AppConstant.PENDING
                    AppConstant.CLOSED -> "${AppConstant.COMPLETED}, ${AppConstant.REJECTED}"
                    else -> tab.text.toString()
                }

                currentPage = 1

                loadBeatList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        beatPlanInfoAdapter = BeatPlanInfoAdapter(
            beatList,
            staffDetailsView = true,
            approval = true
        )

        binding.rvList.adapter = beatPlanInfoAdapter

        binding.rvList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadBeatList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvList.addOnItemTouchListener(
            RecyclerTouchListener(
                requireContext(),
                binding.rvList,
                this
            )
        )
    }

    private fun loadBeatList() {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getPendingBeatPlanList(currentPage, status)
    }

    private fun initObservers() {
        beatViewModel.beatPlanListLiveData.observe(requireActivity()) {
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
                            beatPlanInfoAdapter.notifyDataSetChanged()
                            if (list.size < 30) {
                                isApiLastPage = true
                            }
                        }
                    } else {
                        if (currentPage == 1) {
                            binding.clEmptyData.visibility = View.VISIBLE
                            isApiLastPage = true
                            beatList.clear()
                            beatPlanInfoAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    if (it.errorCode != null && it.errorCode == 403) {
                        logout()
                    } else {
                        showToast(it.message)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.clEmptyData.visibility = View.GONE
        beatList.clear()
        beatPlanInfoAdapter.notifyDataSetChanged()
        loadBeatList()
    }

    override fun onClick(view: View?, position: Int) {
        if (binding.rvList.findViewHolderForAdapterPosition(position)?.itemView != null) {
            val popup =
                PopupMenu(
                    requireContext(),
                    binding.rvList.findViewHolderForAdapterPosition(position)?.itemView?.findViewById(
                        R.id.iv_more_info
                    )
                )

            popup.inflate(R.menu.menu_beat_plan_view)
            //adding click listener

            popup.menu.getItem(1).isVisible = false
            popup.menu.getItem(3).isVisible = false
            popup.menu.getItem(4).isVisible = false

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_view -> {
                        startAllBeatActivity(position)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_history -> {
                        showBeatPlanHistory(position)
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

    }

    private fun showBeatPlanHistory(position: Int) {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java)
                .putExtra(
                    AppConstant.BEAT_ID,
                    beatList[position].id
                ).putExtra(
                    AppConstant.BEAT_PLAN_HISTORY,
                    true
                )
        )
    }

    private fun startAllBeatActivity(position: Int) {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java)
                .putExtra(AppConstant.BEAT_ID, beatList[position].id)
                .putExtra(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, true)
        )
    }
}