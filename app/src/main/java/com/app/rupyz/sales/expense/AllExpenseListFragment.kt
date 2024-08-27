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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.LeadListLayoutBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.RecyclerTouchListener
import com.app.rupyz.model_kt.ExpenseTrackerDataItem

import java.util.*

class AllExpenseListFragment : BaseFragment(), RecyclerTouchListener.ClickListener, Observer {
    private val viewModel: ExpenseViewModel  by viewModels<ExpenseViewModel>()
    private lateinit var adapter: ExpensesTrackerListAdapter

    private var expenseList = ArrayList<ExpenseTrackerDataItem>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private lateinit var status: String

    private lateinit var binding: LeadListLayoutBinding

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

        status = arguments?.getString(AppConstant.TAB_NAME)!!

        initRecyclerView()

        initObservers()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            loadNextPage()
            binding.swipeToRefresh.isRefreshing = false
        }

    }

    private fun initObservers() {
        viewModel.expenseTrackerLiveData.observe(viewLifecycleOwner) {
            isPageLoading = false
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                binding.paginationProgressBar.visibility = View.GONE
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
                    if (it.errorCode != null && it.errorCode == 403) {
                        logout()
                    } else {
                        showToast(it.message)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        adapter =
            ExpensesTrackerListAdapter(
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

        binding.rvList.addOnItemTouchListener(
            RecyclerTouchListener(
                requireContext(),
                binding.rvList,
                this
            )
        )
    }

    private fun loadNextPage() {
        viewModel.getTotalExpenseList(status, currentPage, hasInternetConnection())
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
                .putExtra(AppConstant.STAFF_ROLE, AppConstant.STAFF)
        )
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            loadNextPage()
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
        currentPage = 1
        loadNextPage()
    }

}