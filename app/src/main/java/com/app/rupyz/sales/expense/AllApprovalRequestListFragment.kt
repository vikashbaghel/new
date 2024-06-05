package com.app.rupyz.sales.expense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentMyAttendaceBinding
import com.app.rupyz.databinding.LeadListLayoutBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.RecyclerTouchListener
import com.app.rupyz.model_kt.ExpenseTrackerDataItem


class AllApprovalRequestListFragment : Fragment(), RecyclerTouchListener.ClickListener {
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ApprovalRequestsListAdapter
    private lateinit var binding: LeadListLayoutBinding
    private var expenseList = ArrayList<ExpenseTrackerDataItem>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private lateinit var status: String

  
 
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = LeadListLayoutBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        status = arguments?.getString(AppConstant.TAB_NAME)!!

        if (status == AppConstant.PENDING_EXPENSE){
            status = AppConstant.SUBMITTED
        }

        initRecyclerView()

        initObservers()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            loadNextPage()
            binding.swipeToRefresh.isRefreshing = false
        }

    }

    private fun initObservers() {
        viewModel.approvalRequestLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            binding.paginationProgressBar.visibility = View.GONE

            isPageLoading = false
            binding.clEmptyData.visibility = View.VISIBLE
            if (it.error == false) {
                it.data?.let { list ->
                    if (currentPage == 1) {
                        expenseList.clear()
                    }
                    expenseList.addAll(list)
                    adapter.notifyDataSetChanged()

                    if (list.size < 30) {
                        isApiLastPage = true
                    }

                    if (currentPage == 1 && list.isEmpty()) {
                        binding.clEmptyData.visibility = View.VISIBLE
                    } else {
                        binding.clEmptyData.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        adapter =
            ApprovalRequestsListAdapter(
                expenseList
            )

        binding.rvList.adapter = adapter

        binding.rvList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvList.addOnItemTouchListener(RecyclerTouchListener(requireContext(), binding.rvList, this))
    }

    private fun loadNextPage() {
        viewModel.getApprovalRequestList(status)
        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            currentPage = 1
            loadNextPage()
        }
    }

    override fun onClick(view: View?, position: Int) {
        someActivityResultLauncher.launch(
            Intent(
                requireContext(),
                TotalExpenseDetailsActivity::class.java
            ).putExtra(AppConstant.TOTAL_EXPENSE_DETAILS, expenseList[position])
                .putExtra(AppConstant.STAFF_ROLE, AppConstant.ADMIN)
        )
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            loadNextPage()
        }
    }

}