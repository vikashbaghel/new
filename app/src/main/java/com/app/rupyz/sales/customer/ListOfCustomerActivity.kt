package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityListOfCustomerBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.dialog.DeleteDialogFragment.IDeleteDialogListener
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.sales.beatplan.SelectCustomerForBeatPlanFragment
import com.app.rupyz.sales.beatplan.SortByBottomSheetDialogFragment
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.more.MoreViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity

class ListOfCustomerActivity : BaseActivity(), CustomerActionListener, SortByBottomSheetDialogFragment.ISortByCustomerListener, CustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {
    private lateinit var binding: ActivityListOfCustomerBinding
    private lateinit var customerAdapter: ListOfAllCustomerAdapter

    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var moreViewModel: MoreViewModel

    private var customerList = ArrayList<CustomerData>()
    private var isDataChange = false

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    private var customerInActivePosition = -1
    private var customerInActiveModel: CustomerData? = null

    private var customerLevel = ""
    private var assignedStaffFilterApply = false

    private var isDeleteCustomerDialogShow = false

    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterCustomerLevel = ""
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""

    private var filterCount = 0
    private var levelFilterApply = false
    private var customerTypeFilterApply = false

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        initLayout()
        initRecyclerView()
        initObservers()


        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(supportFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.tvFilter.setOnClickListener {
            val fragment = CustomerFilterBottomSheetDialogFragment.newInstance(this, filterCustomerLevel, filterAssignedStaff, filterCustomerType)
            fragment.show(supportFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            Utils.hideKeyboard(this)
            loadCustomerList()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                customerList.clear()
                customerAdapter.notifyDataSetChanged()
                loadCustomerList()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
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

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            binding.clEmptyData.visibility = View.GONE

            customerList.clear()
            customerAdapter.notifyDataSetChanged()
        }

        binding.ivAddCustomer.isVisible = PermissionModel.INSTANCE
                .getPermission(AppConstant.CREATE_CUSTOMER_PERMISSION, false)
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            binding.rvCustomerList.visibility = View.GONE
            loadCustomerList()
        }
    }

    private fun initLayout() {
        binding.ivAddCustomer.setOnClickListener {
            someActivityResultLauncher.launch(Intent(this@ListOfCustomerActivity, AddCustomerActivity::class.java).putExtra(AppConstant.CUSTOMER_TYPE, customerLevel))
        }

        moreViewModel.getPreferencesInfo()

        customerViewModel.checkOfflineCustomerWithErrorList()

        SharedPref.getInstance().clearCart()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadCustomerList() {
        if (currentPage == 1) {
            binding.shimmerCustomer.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        isApiLastPage = false
        binding.clEmptyData.visibility = View.GONE

        customerViewModel.getCustomerList(null, binding.etSearch.text.toString(), filterCustomerLevel, filterCustomerType, sortByOrder, currentPage, hasInternetConnection())
    }

    var someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            currentPage = 1
            customerList.clear()
            loadCustomerList()
        }
    }

    var transferCustomerActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                }
            }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerAdapter = ListOfAllCustomerAdapter(customerList, this, true, hasInternetConnection())
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.offlineCustomerListWithErrorLiveData.observe(this) {
            if (it.first and it.second.data.isNullOrEmpty().not()) {
                customerList.addAll(it.second.data!!)
                customerAdapter.notifyDataSetChanged()

                loadCustomerList()
            } else {
                loadCustomerList()
            }
        }
        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    binding.rvCustomerList.visibility = View.VISIBLE
                    data.data?.let { it ->
                        isPageLoading = false

                        if (currentPage == 1) {
                            customerList.clear()
                        }
                        customerList.addAll(it)
                        customerAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }

        customerViewModel.customerDeleteLiveData.observe(this) {
            binding.mainProgressBar.visibility = View.GONE
            if (it.error == false) {
                isDataChange = true
                if (it.data != null) {
                    it.data.let { data ->
                        if (data.isUsed == true && data.childCount != null && data.childCount!! > 0) {
                            showDeleteDialog(AppConstant.TRANSFER_CUSTOMER, data.childCount)
                        } else {
                            if (isDeleteCustomerDialogShow.not()) {
                                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                            } else {
                                isDeleteCustomerDialogShow = false
                                if (customerInActivePosition != -1) {
                                    customerList.removeAt(customerInActivePosition)
                                    customerAdapter.notifyItemRemoved(customerInActivePosition)
                                    customerAdapter.notifyItemRangeChanged(customerInActivePosition, customerList.size)
                                    customerInActivePosition = -1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCall(model: CustomerData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: CustomerData, position: Int) {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile}&text=Hi, ${model.name}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onNewOrder(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
            SharedPref.getInstance().clearCart()
            startActivity(Intent(this, CreateNewOrderForCustomerActivity::class.java).putExtra(AppConstant.CUSTOMER_NAME, model.name).putExtra(AppConstant.CUSTOMER_ID, model.id).putExtra(AppConstant.CUSTOMER, model).putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm))
        } else {
            showToast(resources.getString(R.string.create_order_permission))
        }
    }

    override fun onRecordPayment(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
            getRecordPaymentResultLauncher.launch(Intent(this, AddRecordPaymentActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER, model)
                    .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                    .putExtra(AppConstant.CUSTOMER_ID, model.id))
        } else {
            showToast(resources.getString(R.string.payment_permission))
        }
    }

    private var getRecordPaymentResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataChange = true
        }
    }

    override fun onEdit(model: CustomerData, position: Int) {
        val intent = Intent(this, AddCustomerActivity::class.java)
        intent.putExtra(AppConstant.CUSTOMER_ID, model.id)
        intent.putExtra(AppConstant.CUSTOMER_TYPE, model.customerLevel)

        if (model.errorMessage != null) {
            intent.putExtra(AppConstant.ANDROID_OFFLINE_TAG, true)
        }

        someActivityResultLauncher.launch(intent)
    }

    override fun onInActiveCustomer(model: CustomerData, position: Int) {
        customerInActivePosition = position
        customerInActiveModel = model
        val customer = CustomerDeleteOptionModel()
        customer.checkChildren = true
        binding.mainProgressBar.visibility = View.VISIBLE
        customerViewModel.inactiveCustomer(model.id!!, customer, hasInternetConnection())
    }

    private fun showDeleteDialog(action: String, count: Int?) {
        val fragment = DeleteDialogFragment.getInstance(action, object : IDeleteDialogListener {
            override fun onDeleteButtonClick() {
                super.onDeleteButtonClick()
                if (action == AppConstant.TRANSFER_CUSTOMER) {
                    transferCustomerActivityResultLauncher.launch(Intent(this@ListOfCustomerActivity,
                            TransferCustomerActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER,
                                    customerInActiveModel))
                } else {
                    isDeleteCustomerDialogShow = true
                    val customer = CustomerDeleteOptionModel()
                    customer.checkChildren = false
                    customer.isCustomerDelete = true
                    binding.mainProgressBar.visibility = View.VISIBLE
                    customerViewModel.inactiveCustomer(customerInActiveModel?.id!!, customer, hasInternetConnection())
                }
            }
        })

        if (action == AppConstant.TRANSFER_CUSTOMER) {
            val bundle = Bundle()
            bundle.putInt(AppConstant.ADD_COUNT, count ?: 0)
            bundle.putString(AppConstant.CUSTOMER_LEVEL, customerInActiveModel?.customerLevel)
            bundle.putString(AppConstant.CUSTOMER_NAME, customerInActiveModel?.name)
            fragment.arguments = bundle
        }

        fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
    }


    override fun onGetCustomerInfo(model: CustomerData) {
        getCustomerInfoResultLauncher.launch(Intent(this, CustomerDetailActivity::class.java).putExtra(AppConstant.CUSTOMER_ID, model.id).putExtra(AppConstant.CUSTOMER_TYPE, customerLevel))
    }

    override fun recordCustomerActivity(model: CustomerData) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            someActivityResultLauncher.launch(Intent(this,
                    CustomFormActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER_ID, model.id)
                    .putExtra(AppConstant.CUSTOMER, model)
                    .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK))
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    override fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(Intent(this, OrgPhotosViewActivity::class.java).putExtra(AppConstant.PRODUCT_INFO, imageListModel).putExtra(AppConstant.IMAGE_POSITION, 0))
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun getCustomerParentDetails(model: CustomerData, position: Int) {
        if (model.customerParent != null) {
            startActivity(Intent(this, CustomerDetailActivity::class.java).putExtra(AppConstant.CUSTOMER_ID, model.customerParent))
        }
    }

    override fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }

    }

    private var getCustomerInfoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerList()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun applyFilter(customerLevel: String, customerType: ArrayList<CustomerTypeDataItem>, assignedStaff: Pair<Int, String>) {
        filterCustomerType = customerType
        filterCustomerLevel = customerLevel
        filterAssignedStaff = assignedStaff

        if (filterAssignedStaff.first != 0 && assignedStaffFilterApply.not()) {
            ++filterCount
            assignedStaffFilterApply = true
        }

        if (filterCustomerLevel.isEmpty().not() && levelFilterApply.not()) {
            ++filterCount
            levelFilterApply = true
        }

        if (filterCustomerType.isEmpty().not() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }

        if (filterAssignedStaff.first == 0 && assignedStaffFilterApply) {
            --filterCount
            assignedStaffFilterApply = false
        }

        if (filterCustomerLevel.isEmpty() && levelFilterApply) {
            --filterCount
            levelFilterApply = false
        }

        if (filterCustomerType.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        loadCustomerList()
    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        loadCustomerList()
    }
}