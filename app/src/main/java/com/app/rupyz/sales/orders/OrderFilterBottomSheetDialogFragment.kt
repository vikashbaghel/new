package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.organization.profile.CategoryListAdapter
import com.app.rupyz.adapter.organization.profile.CategoryListener
import com.app.rupyz.databinding.BottomSheetOrderFilterBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomerNameRvAdapter
import com.app.rupyz.sales.customer.CustomerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderFilterBottomSheetDialogFragment : BottomSheetDialogFragment(), CategoryListener,
    CustomerNameRvAdapter.ICustomerSelectListener,
    StatusFilterAdapter.StatusSelectListener {
    private lateinit var binding: BottomSheetOrderFilterBinding
    private lateinit var customerViewModel: CustomerViewModel

    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var statusFilterAdapter: StatusFilterAdapter
    private lateinit var customerAdapter: CustomerNameRvAdapter

    private var customerTypeList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var statusList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var customerList = ArrayList<CustomerData>()

    private var isPageLoading = false
    private var isApiLastPage = false

    private var customerCurrentPage = 1
    private var orderSelectedIndex = 0

    private var customerParentId: Int? = null

    private var delay: Long = 500 // 1 seconds after user stops typing
    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    companion object {
        private lateinit var listener: IOrderFilterListener
        private var customerLevel = ""
        private var orderStatus = ""
        private var customerSelect: CustomerData? = null

        @JvmStatic
        fun newInstance(
            listener1: IOrderFilterListener,
            status: String,
            level: String,
            customer: CustomerData?

        ): OrderFilterBottomSheetDialogFragment {
            val fragment = OrderFilterBottomSheetDialogFragment()
            listener = listener1
            orderStatus = status
            customerLevel = level
            customerSelect = customer

            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetOrderFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]


        setStatusList()
        initRecyclerView()
        initObservers()

        loadCustomerLevel()

        loadCustomerPage()

        binding.tvParentCustomerName.setOnClickListener {
            binding.clCustomerList.isVisible = !binding.clCustomerList.isVisible
        }

        customerSelect?.let {
            binding.tvParentCustomerName.text = customerSelect?.name
            binding.ivClearCustomer.visibility = View.VISIBLE
        }


        binding.ivClearCustomer.setOnClickListener {
            binding.tvParentCustomerName.text =
                requireContext().getString(R.string.select_customer_name)
            customerSelect = null
            binding.ivClearCustomer.visibility = View.GONE
            binding.clCustomerList.visibility = View.GONE
        }

        binding.clFilter.setOnClickListener {
            binding.clCustomerList.visibility = View.GONE
        }
        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etSearchCustomer.text.toString().isNotEmpty()) {
                    customerCurrentPage = 1
                    customerList.clear()
                    loadCustomerPage()
                    Utils.hideKeyboard(requireActivity())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter some value!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchCustomer.setText("")
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerPage()
        }

        binding.buttonCancel.setOnClickListener {
            clearFilter()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            listener.changeOrderFilter(
                orderStatus,
                customerLevel,
                customerSelect,
                orderSelectedIndex
            )
            dismiss()
        }
    }

    private fun loadCustomerLevel() {

        val model = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                .not()
        ) {
            model.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
        }
        if (customerLevel.isEmpty()) {
            model.isSelected = true
        }
        customerTypeList.add(model)

        val model2 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                .not()
        ) {
            model2.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
        }

        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_1
        ) {
            model2.isSelected = true
        }
        customerTypeList.add(model2)


        val model3 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                .not()
        ) {
            model3.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
        }
        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_2
        ) {
            model3.isSelected = true
        }
        customerTypeList.add(model3)

        categoryListAdapter.notifyDataSetChanged()
    }

    private fun clearFilter() {
        orderStatus = ""
        customerLevel = ""
        customerSelect = null
        orderSelectedIndex = 0
        listener.changeOrderFilter(orderStatus, customerLevel, customerSelect, orderSelectedIndex)
        dismiss()
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            customerCurrentPage = 1
            customerList.clear()
            loadCustomerPage()
            customerAdapter.notifyDataSetChanged()
        }
    }

    private fun setStatusList() {
        val list = requireContext().resources.getStringArray(R.array.order_status_for_filter)

        list.forEachIndexed { index, value ->
            val model = AllCategoryResponseModel()
            if (value.equals(orderStatus)) {
                model.isSelected = true
                orderSelectedIndex = index
            } else if (orderStatus.isEmpty()) {
                if (index == 0) {
                    model.isSelected = true
                    orderSelectedIndex = 0
                }

            }
            model.name = value
            statusList.add(model)
        }
    }

    private fun initRecyclerView() {
        binding.rvCustomerType.setHasFixedSize(true)
        binding.rvCustomerType.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryListAdapter = CategoryListAdapter(customerTypeList, this)
        binding.rvCustomerType.adapter = categoryListAdapter

        binding.rvStatus.setHasFixedSize(true)
        binding.rvStatus.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        statusFilterAdapter = StatusFilterAdapter(statusList, this)
        binding.rvStatus.adapter = statusFilterAdapter

        if (orderSelectedIndex > 0) {
            binding.rvStatus.scrollToPosition(orderSelectedIndex - 1)
        }

        val llM2 = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = llM2
        customerAdapter = CustomerNameRvAdapter(customerList, this)
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object : PaginationScrollListener(llM2) {
            override fun loadMoreItems() {
                isPageLoading = true
                customerCurrentPage += 1
                loadCustomerPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadCustomerPage() {
        customerViewModel.getCustomerList(
            null,
            binding.etSearchCustomer.text.toString(),
            "",
            ArrayList(),
            AppConstant.SORTING_LEVEL_ASCENDING,
            customerCurrentPage,
            hasInternetConnection
        )
    }

    val hasInternetConnection: Boolean
        get() {
            return Connectivity.hasInternetConnection(requireContext())
        }

    private fun initObservers() {
        customerViewModel.getCustomerListData().observe(this) { data ->
            data.data?.let { it ->
                isPageLoading = false
                if (it.isNotEmpty()) {
                    if (customerCurrentPage == 1) {
                        customerList.clear()
                    }
                    customerList.addAll(it)
                    customerAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }
    }

    override fun onCategorySelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected == false) {
            for (i in customerTypeList.indices) {
                customerTypeList[i].isSelected = false
            }

            customerLevel = customerTypeList[position].name.toString()
            customerTypeList[position].isSelected = true
            categoryListAdapter.notifyDataSetChanged()

            when (position) {
                0 -> customerLevel = ""
                1 -> customerLevel = AppConstant.CUSTOMER_LEVEL_1
                2 -> customerLevel = AppConstant.CUSTOMER_LEVEL_2
                3 -> customerLevel = AppConstant.CUSTOMER_LEVEL_3
            }

        }
    }

    override fun onCustomerSelect(model: CustomerData, position: Int) {
        binding.tvParentCustomerName.text = model.name
        customerParentId = model.id
        customerSelect = model
        binding.clCustomerList.visibility = View.GONE
        binding.ivClearCustomer.visibility = View.VISIBLE
    }

    override fun onStatusSelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected == false) {
            orderSelectedIndex = position
            for (i in statusList.indices) {
                statusList[i].isSelected = false
            }


            orderStatus = if (position == 0) {
                ""
            } else {
                statusList[position].name.toString()
            }
            statusList[position].isSelected = true
            statusFilterAdapter.notifyDataSetChanged()
        }
    }

    interface IOrderFilterListener {
        fun changeOrderFilter(
            orderStatus: String,
            customerLevel: String,
            customerSelect: CustomerData?,
            orderSelectedIndex: Int
        )
    }

}