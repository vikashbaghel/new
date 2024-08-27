package com.app.rupyz.sales.analytics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerWiseSalesBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomerWiseSalesDataItem
import com.app.rupyz.model_kt.DateFilterModel
import com.app.rupyz.model_kt.StaffWiseSalesDataItem
import com.app.rupyz.sales.customer.StaffWiseSalesAdapter
import com.app.rupyz.sales.filter.AnalyticsFilterBottomSheetDialogFragment
import com.app.rupyz.sales.filter.IAnalyticsFilterListener

class CustomerWiseSalesActivity : AppCompatActivity(), IAnalyticsFilterListener {
    private lateinit var binding: ActivityCustomerWiseSalesBinding

    private lateinit var customerViewModel: AnalyticsViewModel
    private lateinit var customerWiseSalesAdapter: CustomerWiseSalesAdapter
    private lateinit var staffWiseSalesAdapter: StaffWiseSalesAdapter

    private var list: ArrayList<CustomerWiseSalesDataItem> = ArrayList()
    private var staffList: ArrayList<StaffWiseSalesDataItem> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private lateinit var dateFilterModel: DateFilterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerWiseSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        customerViewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]

        initObservers()

        if (intent.hasExtra(AppConstant.DATE_FILTER)) {
            dateFilterModel = intent.getParcelableExtra(AppConstant.DATE_FILTER)!!
            binding.tvFilter.text = dateFilterModel.title
        }

        if (intent.hasExtra(AppConstant.CUSTOMER_TYPE)) {
            if (intent.getStringExtra(AppConstant.CUSTOMER_TYPE).equals(AppConstant.CUSTOMER_ID)) {
                binding.tvToolbarTitle.text = getString(R.string.customer_wise_sales)
                binding.hdCustomer.text = getString(R.string.customer)
                initCustomerRecyclerView()
                loadNextCustomerPage(dateFilterModel)
            } else {
                binding.tvToolbarTitle.text = getString(R.string.staff_wise_sales)
                binding.hdCustomer.text = getString(R.string.staff)
                initStaffRecyclerView()
                loadNextStaffPage(dateFilterModel)
            }
        }

        binding.clFilter.setOnClickListener {
            val fragment = AnalyticsFilterBottomSheetDialogFragment(this, dateFilterModel!!)
            fragment.show(supportFragmentManager, "tag")
        }

        binding.ivBack.setOnClickListener { finish() }

    }


    private fun initCustomerRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerWiseSales.layoutManager = linearLayoutManager
        customerWiseSalesAdapter = CustomerWiseSalesAdapter(list)
        binding.rvCustomerWiseSales.adapter = customerWiseSalesAdapter

        binding.rvCustomerWiseSales.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadNextCustomerPage(dateFilterModel)
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun initStaffRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerWiseSales.layoutManager = linearLayoutManager
        staffWiseSalesAdapter = StaffWiseSalesAdapter(staffList)
        binding.rvCustomerWiseSales.adapter = staffWiseSalesAdapter

        binding.rvCustomerWiseSales.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadNextStaffPage(dateFilterModel)
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }


    private fun loadNextCustomerPage(dateFilterModel: DateFilterModel) {
        customerViewModel.getCustomerWiseSalesList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            currentPage
        )
        if (currentPage == 1) {
            binding.rvCustomerWiseSales.visibility = View.GONE
            binding.shimmerCustomerWiseSales.visibility = View.VISIBLE
        }
    }

    private fun loadNextStaffPage(dateFilterModel: DateFilterModel) {
        customerViewModel.getStaffWiseSalesList(
            dateFilterModel.filter_type!!, dateFilterModel.startDate!!,
            dateFilterModel.end_date!!, currentPage
        )
        if (currentPage == 1) {
            binding.rvCustomerWiseSales.visibility = View.GONE
            binding.shimmerCustomerWiseSales.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.customerWiseSalesLiveData.observe(this) { data ->
            data.data?.let { it ->
                isPageLoading = false
                if (it.isNotEmpty()) {
                    binding.shimmerCustomerWiseSales.visibility = View.GONE
                    if (currentPage == 1) {
                        list.clear()
                    }
                    list.addAll(it)
                    binding.rvCustomerWiseSales.visibility = View.VISIBLE
                    customerWiseSalesAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }

        customerViewModel.staffWiseSalesLiveData.observe(this) { data ->
            data.data?.let { it ->
                isPageLoading = false
                if (it.isNotEmpty()) {
                    binding.shimmerCustomerWiseSales.visibility = View.GONE
                    if (currentPage == 1) {
                        list.clear()
                    }
                    staffList.addAll(it)
                    binding.rvCustomerWiseSales.visibility = View.VISIBLE
                    staffWiseSalesAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }
    }

    override fun onFilterDate(model: DateFilterModel) {
        currentPage = 1
        dateFilterModel = model
        binding.tvFilter.text = model.title

        if (intent.hasExtra(AppConstant.CUSTOMER_TYPE)) {
            if (intent.getStringExtra(AppConstant.CUSTOMER_TYPE).equals(AppConstant.CUSTOMER_ID)) {
                loadNextCustomerPage(dateFilterModel)
            } else {
                loadNextStaffPage(dateFilterModel)
            }
        }
    }

}