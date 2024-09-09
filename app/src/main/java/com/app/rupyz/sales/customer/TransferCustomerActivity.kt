package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTransferCustomerBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel

class TransferCustomerActivity : BaseActivity(), CustomerNameRvAdapter.ICustomerSelectListener {
    private lateinit var binding: ActivityTransferCustomerBinding

    private val customerViewModel: CustomerViewModel by viewModels()

    private lateinit var parentCustomerAdapter: CustomerNameRvAdapter
    private lateinit var retailerAdapter: MappedCustomerInfoForTransferAdapter

    private var parentCustomerList = ArrayList<CustomerData>()
    private var retailerList = ArrayList<CustomerData>()

    private var isRetailerPageLoading = false
    private var isParentPageLoading = false

    private var isApiLastPageForRetailer = false
    private var isApiLastPageForParent = false

    private var parentCurrentPage = 1
    private var retailerCurrentPage = 1

    private var transferCustomerModel: CustomerData? = null
    private var customerId: Int = 0
    private var parentCustomerId: Int? = null

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""

    private var parentCustomerLevel = ""
    private var retailerCustomerLevel = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transferCustomerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
        } else {
            intent.getParcelableExtra(AppConstant.CUSTOMER)
        }

        if (transferCustomerModel?.id != null) {
            customerId = transferCustomerModel?.id!!

            parentCustomerLevel = SharedPref.getInstance().getString(transferCustomerModel?.customerLevel)

            if (transferCustomerModel?.customerLevel == AppConstant.CUSTOMER_LEVEL_1) {
                retailerCustomerLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
            } else if (transferCustomerModel?.customerLevel == AppConstant.CUSTOMER_LEVEL_2) {
                retailerCustomerLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
            }

            binding.tvToolbarTitle.text = resources.getString(R.string.transfer_retailer, retailerCustomerLevel)
            binding.tvToolbarSubTitle.text = resources.getString(R.string.transfer_retailer_for_parent, transferCustomerModel?.name)

            binding.tvTransferCustomerHeading.text = resources.getString(R.string.select_customer_to_transfer_heading, parentCustomerLevel)
            binding.etSearchCustomer.hint = resources.getString(R.string.select_customer_level_list, parentCustomerLevel)
            binding.tvMapCustomerListHeading.text = resources.getString(R.string.mapped_placeHolder_list, retailerCustomerLevel)
        }

        initRecyclerView()
        initObservers()

        loadRetailerList()
        loadCustomerList()

        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                parentCurrentPage = 1
                parentCustomerList.clear()
                parentCustomerAdapter.notifyDataSetChanged()
                parentCustomerList.clear()
                loadCustomerList()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    loadCustomerList()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }
            }
        })

        binding.etSearchCustomer.onFocusChangeListener = OnFocusChangeListener { p0, isFocus ->
            if (isFocus) {
                binding.clParentCustomerList.visibility = View.VISIBLE
            }
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchCustomer.setText("")
            parentCustomerList.clear()
            parentCustomerAdapter.notifyDataSetChanged()
            loadCustomerList()
        }

        binding.tvRemoveSelectedCustomer.setOnClickListener {
            parentCustomerId = null

            binding.mainContent.visibility = View.GONE
            binding.groupSearch.visibility = View.VISIBLE
            binding.tvTransferCustomerHeading.text =
                    resources.getString(R.string.select_customer_to_transfer_heading,
                            parentCustomerLevel)

            binding.tvTransfer.visibility = View.GONE
        }

        binding.main.setOnClickListener {
            binding.clParentCustomerList.visibility = View.GONE
            hideKeyboard()
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvTransfer.setOnClickListener {
            if (parentCustomerId != null) {
                binding.tvTransfer.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                val deleteModel = CustomerDeleteOptionModel()
                deleteModel.isCustomerDelete = true
                deleteModel.customerParentId = parentCustomerId

                customerViewModel.inactiveCustomer(customerId, deleteModel, true)
            } else {
                showToast("Please select $parentCustomerLevel to transfer")
            }
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            parentCurrentPage = 1
            parentCustomerList.clear()
            parentCustomerAdapter.notifyDataSetChanged()
            loadCustomerList()
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvMappedRetailerList.layoutManager = linearLayoutManager
        retailerAdapter =
                MappedCustomerInfoForTransferAdapter(retailerList)
        binding.rvMappedRetailerList.adapter = retailerAdapter

        binding.rvMappedRetailerList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isRetailerPageLoading = true
                retailerCurrentPage += 1
                loadRetailerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForRetailer
            }

            override fun isLoading(): Boolean {
                return isRetailerPageLoading
            }
        })

        val llM2 = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = llM2
        parentCustomerAdapter = CustomerNameRvAdapter(parentCustomerList, this)
        binding.rvCustomerList.adapter = parentCustomerAdapter

        binding.rvCustomerList.addOnScrollListener(object : PaginationScrollListener(llM2) {
            override fun loadMoreItems() {
                isParentPageLoading = true
                parentCurrentPage += 1
                loadCustomerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForParent
            }

            override fun isLoading(): Boolean {
                return isParentPageLoading
            }
        })
    }

    private fun loadCustomerList() {
        binding.parenCustomerProgressBar.visibility = View.VISIBLE
        binding.tvNoCustomerFound.visibility = View.GONE
        customerViewModel.getParentCustomerList(
                binding.etSearchCustomer.text.toString(),
                transferCustomerModel?.customerLevel ?: "",
                filterCustomerType,
                sortByOrder,
                parentCurrentPage
        )
    }

    private fun loadRetailerList() {
        if (retailerCurrentPage == 1) {
            binding.shimmerCustomer.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        customerViewModel.getCustomerList(
                customerId,
                binding.etSearchCustomer.text.toString(),
                "",
                filterCustomerType,
                sortByOrder,
                parentCurrentPage,
                hasInternetConnection()
        )
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.parentCustomerListLiveData.observe(this) { data ->
            binding.parenCustomerProgressBar.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    data.data?.let { it ->
                        isParentPageLoading = false

                        if (parentCurrentPage == 1) {
                            parentCustomerList.clear()
                        }

                        it.forEach { customerData ->
                            if (customerData.id != transferCustomerModel?.id) {
                                parentCustomerList.add(customerData)
                            }
                        }

                        parentCustomerAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPageForParent = true
                        }
                    }
                } else {
                    binding.tvNoCustomerFound.visibility = View.VISIBLE
                    if (parentCurrentPage == 1) {
                        isApiLastPageForParent = true
                        parentCustomerList.clear()
                        parentCustomerAdapter.notifyDataSetChanged()
                    }
                }

            } else {
                showToast(data.message)
            }
        }

        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {

                if (data.data.isNullOrEmpty().not()) {
                    data.data?.let { it ->
                        isRetailerPageLoading = false

                        if (retailerCurrentPage == 1) {
                            retailerList.clear()
                        }

                        it.forEach { customerData ->
                            retailerList.add(customerData)
                        }

                        binding.tvMapCustomerListCount.text =
                                resources.getString(R.string.placeHolder_placeHolder_string,
                                        "${retailerList.size}", retailerCustomerLevel)

                        retailerAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPageForRetailer = true
                        }
                    }
                } else {
                    if (retailerCurrentPage == 1) {
                        isApiLastPageForRetailer = true
                        retailerList.clear()
                        retailerAdapter.notifyDataSetChanged()
                    }
                }

            } else {
                showToast(data.message)
            }

            customerViewModel.customerDeleteLiveData.observe(this) {
                binding.progressBar.visibility = View.GONE
                binding.tvTransfer.isEnabled = true
                showToast(it.message)
                if (it.error == false) {
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

    }

    override fun onCustomerSelect(model: CustomerData, position: Int) {
        parentCustomerId = model.id

        binding.mainContent.visibility = View.VISIBLE
        binding.ivClearSearch.visibility = View.GONE
        binding.groupSearch.visibility = View.GONE
        binding.clParentCustomerList.visibility = View.GONE
        binding.tvTransfer.visibility = View.VISIBLE

        binding.tvTransferCustomerHeading.text =
                resources.getString(R.string.transfer_to)

        hideKeyboard()

        initSelectedParentCustomerData(model)
    }

    private fun initSelectedParentCustomerData(model: CustomerData) {
        binding.tvCustomerName.text = model.name

        if (model.logoImageUrl.isNullOrEmpty().not()) {
            ImageUtils.loadImage(model.logoImageUrl, binding.ivCustomer)
        } else {
            binding.ivCustomer.setImageResource(R.mipmap.no_photo_available)
        }

        binding.tvLocation.text = model.city

        binding.tvAuthorizePersonName.text = model.contactPersonName

        if (model.customerLevel.isNullOrEmpty().not()) {
            binding.tvCustomerLevel.visibility = View.VISIBLE
            binding.tvCustomerLevel.text =
                    SharedPref.getInstance().getString(model.customerLevel)

            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    resources.getColor(R.color.customer_level_one_background)
                            )
                    binding.tvCustomerLevel.setTextColor(resources.getColor(R.color.customer_level_one_text_color))
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.customer_level_two_background))
                    binding.tvCustomerLevel.setTextColor(resources.getColor(R.color.customer_level_two_text_color))
                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    resources.getColor(R.color.customer_level_three_background)
                            )

                    binding.tvCustomerLevel.setTextColor(resources.getColor(R.color.customer_level_three_text_color))
                }
            }
        } else {
            binding.tvCustomerLevel.visibility = View.GONE
        }
    }
}