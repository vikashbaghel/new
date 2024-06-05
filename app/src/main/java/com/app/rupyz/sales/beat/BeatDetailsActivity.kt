package com.app.rupyz.sales.beat

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityBeatDetailsBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.BeatListDataItem
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.sales.beatplan.SelectCustomerForBeatPlanFragment
import com.app.rupyz.sales.beatplan.SortByBottomSheetDialogFragment
import com.app.rupyz.sales.customer.AddCustomerActivity
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.customer.CustomerActionListener
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerFilterBottomSheetDialogFragment
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.customer.ListOfAllCustomerAdapter
import com.app.rupyz.sales.customer.TransferCustomerActivity
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.google.gson.JsonObject

class BeatDetailsActivity : BaseActivity(), CustomerActionListener,
        SortByBottomSheetDialogFragment.ISortByCustomerListener,
        CustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {
    private lateinit var binding: ActivityBeatDetailsBinding

    private val beatViewModel: BeatViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()

    private var beatModel: BeatListDataItem? = null

    private var customerList = ArrayList<CustomerData>()
    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()

    private lateinit var customerAdapter: ListOfAllCustomerAdapter

    private var sortByOrder: String = ""
    private var filterCustomerLevel = ""
    private var customerLevel = ""

    private var currentPage = 1
    private var filterCount = 0
    private var customerInActivePosition = -1
    private var customerInActiveModel: CustomerData? = null

    private var levelFilterApply = false
    private var assignedStaffFilterApply = false
    private var customerTypeFilterApply = false
    private var isPageLoading = false
    private var isApiLastPage = false
    private var isDeleteCustomerDialogShow = false
    private var isDataChange = false

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0

    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        initRecyclerView()
        initObservers()

        binding.shimmerCustomer.visibility = View.VISIBLE
        loadBeat()

        binding.imgClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initLayout() {
        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(
                    supportFragmentManager,
                    SelectCustomerForBeatPlanFragment::class.java.name
            )
        }

        binding.tvFilter.setOnClickListener {
            val fragment = CustomerFilterBottomSheetDialogFragment.newInstance(
                    this,
                    filterCustomerLevel,
                    filterAssignedStaff,
                    filterCustomerType
            )

            val bundle = Bundle()
            bundle.putBoolean(AppConstant.STAFF_DETAILS, true)
            fragment.arguments = bundle
            fragment.show(
                    supportFragmentManager,
                    SelectCustomerForBeatPlanFragment::class.java.name
            )
        }

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            Utils.hideKeyboard(this)
            loadCustomerPage()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                customerList.clear()
                customerAdapter.notifyDataSetChanged()
                loadCustomerPage()
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
                    loadCustomerPage()
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

        binding.ivMore.setOnClickListener { v ->
            val popup = PopupMenu(v.context, binding.ivMore)
            popup.inflate(R.menu.menu_edit_and_delete)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.delete_product -> {
                        showDeleteBeatDialog(beatModel?.id, false, "")
                        return@setOnMenuItemClickListener true
                    }

                    R.id.edit_product -> {
                        updateBeatResultLauncher.launch(
                                Intent(this, AddNewBeatActivity::class.java).putExtra(
                                        AppConstant.BEAT_ID,
                                        beatModel?.id
                                )
                        )
                        return@setOnMenuItemClickListener false
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }
    }

    private fun showDeleteBeatDialog(id: Int?, isForced: Boolean, message: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_beat)

        if (isForced && message.isNullOrEmpty().not()) {
            tvTitle.text = message
            tvDelete.text = resources.getString(R.string.confirm)
        } else {
            tvTitle.text = resources.getString(R.string.delete_beat_message)
        }

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val jsonObject = JsonObject()
            jsonObject.addProperty("is_forced", isForced)
            beatViewModel.deleteBeat(id!!, jsonObject)
            dialog.dismiss()
        }

        dialog.show()
    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            binding.rvCustomerList.visibility = View.GONE
            loadCustomerPage()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerAdapter = ListOfAllCustomerAdapter(customerList, this, true, hasInternetConnection())
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
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
        binding.clEmptyData.visibility = View.GONE
        beatViewModel.getListOfCustomerForBeat(
                beatModel?.id,
                binding.etSearch.text.toString(),
                null,
                0,
                AppConstant.ASSIGNED.uppercase(),
                false,
                filterCustomerLevel,
                0,
                filterCustomerType,
                sortByOrder,
                currentPage
        )
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        beatViewModel.beatDetailsLiveData.observe(this) {
            if (it.error == false) {
                initData(it.data)
            } else {
                showToast(it.message)
            }
        }
        beatViewModel.customerListForBeatLiveData.observe(this) { data ->
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
                showToast(data.message)
            }
        }

        customerViewModel.customerDeleteLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
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

        beatViewModel.deleteBeatLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isDataChange = true
                onBackPressed()
            } else {
                if (it.data?.isUsed == true) {
                    showDeleteBeatDialog(beatModel?.id, true, it.message)
                } else {
                    showToast(it.message)
                }
            }
        }
    }

    private fun initData(model: BeatListDataItem?) {
        beatModel = model

        currentPage = 1
        isApiLastPage = false
        customerList.clear()
        loadCustomerPage()

        if (beatModel != null) {
            binding.tvToolbarTitle.text = beatModel?.name

            if (beatModel?.locality.isNullOrEmpty().not()) {
                binding.tvToolbarSubTitle.text = beatModel?.locality
            } else {
                binding.tvToolbarSubTitle.visibility = View.GONE
            }

            if (beatModel?.parentCustomerName.isNullOrEmpty().not()) {
                binding.tvDistributorName.text = beatModel?.parentCustomerName
                binding.hdDistributor.text =
                        SharedPref.getInstance().getString(beatModel?.parentCustomerLevel)

                binding.tvDistributorName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_right,
                        0
                )
                binding.tvDistributorName.setOnClickListener {
                    updateBeatResultLauncher.launch(
                            Intent(this, CustomerDetailActivity::class.java).putExtra(
                                    AppConstant.CUSTOMER_ID,
                                    beatModel?.parentCustomer
                            )
                    )
                }
            } else {
                binding.tvDistributorName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_add_emi,
                        0
                )
                binding.tvDistributorName.text = resources.getString(R.string.assign_distributor)

                binding.tvDistributorName.setOnClickListener {
                    updateBeatResultLauncher.launch(
                            Intent(this, AddNewBeatActivity::class.java)
                                    .putExtra(
                                            AppConstant.BEAT_ID, beatModel?.id
                                    ).putExtra(AppConstant.STAFF_DETAILS_FOR_BEAT, true)
                                    .putExtra(AppConstant.LOCATION, beatModel?.locality)
                    )
                }
            }

            if (beatModel?.staffCount != null && beatModel?.staffCount != 0) {
                binding.tvStaffAssignName.text = "${beatModel?.staffCount}"
                binding.tvStaffAssignName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_right,
                        0
                )

                binding.tvStaffAssignName.setOnClickListener {
                    startActivity(
                            Intent(this, FragmentContainerActivity::class.java).putExtra(
                                    AppConstant.BEAT_ID_FOR_ASSIGN_STAFF, beatModel?.id
                            ).putExtra(AppConstant.STAFF_DETAILS_FOR_BEAT, true)
                                    .putExtra(AppConstant.LOCATION, beatModel?.locality)
                    )
                }
            } else {
                binding.tvStaffAssignName.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_add_emi,
                        0
                );
                binding.tvStaffAssignName.text = resources.getString(R.string.assign_staff)

                binding.tvStaffAssignName.setOnClickListener {
                    updateBeatResultLauncher.launch(
                            Intent(this, AddNewBeatActivity::class.java)
                                    .putExtra(
                                            AppConstant.BEAT_ID, beatModel?.id
                                    ).putExtra(AppConstant.STAFF_DETAILS_FOR_BEAT, true)
                                    .putExtra(AppConstant.LOCATION, beatModel?.locality)
                    )
                }
            }

            binding.tvDistributorName.visibility = View.VISIBLE
            binding.tvStaffAssignName.visibility = View.VISIBLE
        }

    }

    override fun onCall(model: CustomerData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: CustomerData, position: Int) {
        val uri =
                Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile}&text=Hi, ${model.name}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onNewOrder(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
            SharedPref.getInstance().clearCart()
            startActivity(
                    Intent(this, CreateNewOrderForCustomerActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
            )
        } else {
            showToast(resources.getString(R.string.create_order_permission))
        }
    }

    override fun onRecordPayment(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
            getRecordPaymentResultLauncher.launch(
                    Intent(
                            this,
                            AddRecordPaymentActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER, model)
                .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
            )
        } else {
            showToast(resources.getString(R.string.payment_permission))
        }
    }

    private var getRecordPaymentResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataChange = true
        }
    }


    private var updateBeatResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            loadBeat()
        }
    }

    private fun loadBeat() {
        beatViewModel.getBeatDetails(intent.getIntExtra(AppConstant.BEAT_ID, 0))
    }


    override fun onEdit(model: CustomerData, position: Int) {
        updateBeatResultLauncher.launch(
                Intent(this, AddCustomerActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, model.id)
                        .putExtra(AppConstant.CUSTOMER_TYPE, customerLevel)
        )
    }

    override fun onInActiveCustomer(model: CustomerData, position: Int) {
        customerInActivePosition = position
        customerInActiveModel = model
        val customer = CustomerDeleteOptionModel()
        customer.checkChildren = true
        binding.progressBar.visibility = View.VISIBLE
        customerViewModel.inactiveCustomer(model.id!!, customer, hasInternetConnection())
    }

    private fun showDeleteDialog(action: String, count: Int?) {
        val fragment = DeleteDialogFragment.getInstance(action, object : DeleteDialogFragment.IDeleteDialogListener {
            override fun onDeleteButtonClick() {
                super.onDeleteButtonClick()
                if (action == AppConstant.TRANSFER_CUSTOMER) {
                    transferCustomerActivityResultLauncher.launch(Intent(this@BeatDetailsActivity,
                            TransferCustomerActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER,
                                    customerInActiveModel))
                } else {
                    isDeleteCustomerDialogShow = true
                    val customer = CustomerDeleteOptionModel()
                    customer.checkChildren = false
                    customer.isCustomerDelete = true
                    binding.progressBar.visibility = View.VISIBLE
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

    var transferCustomerActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                }
            }

    override fun onGetCustomerInfo(model: CustomerData) {
        getCustomerInfoResultLauncher.launch(
                Intent(this, CustomerDetailActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, model.id)
                        .putExtra(AppConstant.CUSTOMER_TYPE, customerLevel)
        )
    }

    override fun recordCustomerActivity(model: CustomerData) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            updateBeatResultLauncher.launch(
                    Intent(this, CustomFormActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            )
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
            startActivity(
                    Intent(this, OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun getCustomerParentDetails(model: CustomerData, position: Int) {
        if (model.customerParent != null) {
            startActivity(
                    Intent(this, CustomerDetailActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, model.customerParent)
            )
        }
    }

    override fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }

    }

    private var getCustomerInfoResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerPage()
        }
    }

    override fun applyFilter(
            customerLevel: String,
            customerType: ArrayList<CustomerTypeDataItem>,
            assignedStaff: Pair<Int, String>
    ) {
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

        if (filterCustomerType.isNotEmpty() && customerTypeFilterApply.not()) {
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

        loadCustomerPage()
    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        loadCustomerPage()
    }

    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}