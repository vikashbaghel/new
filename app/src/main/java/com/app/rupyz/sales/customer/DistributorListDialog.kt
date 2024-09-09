package com.app.rupyz.sales.customer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.ItemViewDistributorListDialogBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.divideHeadersIntoQueryParams
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.adapters.DistributorRadioListAdapter


class DistributorListDialog : DialogFragment() {

    private lateinit var binding: ItemViewDistributorListDialogBinding
    private var isApiLastPage: Boolean = false
    private var isPageLoading: Boolean = false
    private var assignCustomerHeaders: String? = "selected=true&page_no=1"
    private var distributorListAdapter: DistributorRadioListAdapter =
        DistributorRadioListAdapter(false)
    private val customerViewModel: CustomerViewModel by viewModels()
    private var defaultHeader = "selected=true&page_no=1"


    companion object {
        var customerId: Int? = null
        var customerLevel: CustomerLevel? = null
        var onDistributorSelected: ((CustomerData) -> Unit)? = null
        fun getInstance(
            customerId: Int,
            customerLevel: CustomerLevel,
            onDistributorSelected: (CustomerData) -> Unit
        ): DistributorListDialog {
            this.customerLevel = customerLevel
            this.customerId = customerId
            this.onDistributorSelected = onDistributorSelected
            return DistributorListDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ItemViewDistributorListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        binding.tvChooseActivity.text = when (customerLevel) {
            CustomerLevel.LEVEL_ONE -> ""
            CustomerLevel.LEVEL_TWO -> SharedPref.getInstance()
                .getString(AppConstant.CUSTOMER_LEVEL_1)
                ?: binding.root.context.resources.getString(R.string.primary_customer)

            CustomerLevel.LEVEL_THREE -> SharedPref.getInstance()
                .getString(AppConstant.CUSTOMER_LEVEL_2)
                ?: binding.root.context.resources.getString(R.string.distributor)

            null -> ""
        }
        val linearLayoutManager: LinearLayoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvDistributorList.layoutManager = linearLayoutManager
        binding.rvDistributorList.adapter = distributorListAdapter
        binding.rvDistributorList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                customerLevel?.let { loadCustomerLevel(it, assignCustomerHeaders) }
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        customerLevel?.let { customerLevel ->
            loadCustomerLevel(
                customerLevel = customerLevel,
                currentPage = assignCustomerHeaders
            )
        }

        distributorListAdapter.setOnItemSelectListener {
            if (onDistributorSelected != null) {
                onDistributorSelected?.invoke(it)
                dismiss()
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }


    private fun setSpinnerItems(clearList: Boolean, customerList: MutableList<CustomerData>) {
        if (clearList) {
            distributorListAdapter.setCustomerList(customerList)
        } else {
            distributorListAdapter.addCustomer(customerList)
        }
    }

    private fun initObserver() {
        customerViewModel.getCustomerListData().observe(this) { response ->
            binding.progressbar.hideView()
            isPageLoading = false
            response.data?.let { customerDataList ->
                if (customerDataList.isNotEmpty()) {
                    customerLevel?.let {
                        setSpinnerItems(
                            defaultHeader == assignCustomerHeaders, customerDataList.toMutableList()
                        )
                    }
                }
            }
            response.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty()
                        .not() && headers.nextParams.divideHeadersIntoQueryParams().first
                ) {
                    assignCustomerHeaders = headers.nextParams
                    if (response.data.isNullOrEmpty()) {
                        customerLevel?.let { selectedStep ->
                            loadCustomerLevel(
                                selectedStep, assignCustomerHeaders
                            )
                        }
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }

    }

    private fun loadCustomerLevel(customerLevel: CustomerLevel, currentPage: String?) {
        if (customerLevel != CustomerLevel.LEVEL_ONE) {
            this.assignCustomerHeaders = currentPage
            customerViewModel.getCustomerListMapped(
                customerId = customerId,
                name = "",
                filterCustomerLevel = when (customerLevel) {
                    CustomerLevel.LEVEL_ONE -> AppConstant.CUSTOMER_LEVEL_1
                    CustomerLevel.LEVEL_TWO -> AppConstant.CUSTOMER_LEVEL_2
                    CustomerLevel.LEVEL_THREE -> AppConstant.CUSTOMER_LEVEL_3
                },
                filterCustomerType = ArrayList(),
                ignoreMapping = true,
                sortByOrder = AppConstant.SORTING_LEVEL_ASCENDING,
                header = assignCustomerHeaders ?: defaultHeader,
                hasInternetConnection = hasInternetConnection(binding.root.context)
            )
            if (defaultHeader == assignCustomerHeaders) {
                binding.progressbar.showView()
            }
        }
    }

}