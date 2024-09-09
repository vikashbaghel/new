package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAllListOfOrdersBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.hideWithRightToLeftAnimation
import com.app.rupyz.generic.helper.showWithRightToLeftAnimation
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.SORTING_LEVEL_DESCENDING
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.filter.GallerySortByBottomSheetDialogFragment
import com.app.rupyz.sales.filter.OrderLevelBottomSheetDialogFragment
import com.app.rupyz.sales.filter.OrderListSortByBottomSheetDialogFragment
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.home.SalesRecentNewOrderAdapter
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class AllListOfOrdersActivity : BaseActivity(),
    OrdersFilterActivity.IOrderFilterListener, OrdersFilterActivity.OnItemStateListener,
    OrderLevelBottomSheetDialogFragment.IOrderFilterListener,
    StatusFilterAdapter.StatusSelectListener, OrderStatusActionListener,
    OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    OrderListSortByBottomSheetDialogFragment.ISortingOrderListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener, View.OnClickListener {

    private lateinit var binding: ActivityAllListOfOrdersBinding
    private var selected: Boolean? = false
    private var position: Int? = -1

    private lateinit var dashboardViewModel: DashboardViewModel
    private var sortingBY: String = AppConstant.CREATED_AT
    private var sortingOrder: String = SORTING_LEVEL_DESCENDING

    private lateinit var statusFilterAdapter: StatusFilterAdapter
    private lateinit var recentOrderAdapter: SalesRecentNewOrderAdapter

    private var statusList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var recentOrderList = java.util.ArrayList<OrderData>()

    private var orderStatus: String = ""
    var handler: Handler = Handler(Looper.myLooper()!!)
    private var selectedCustomerLevel: String = ""
    private var selectedCustomerModel: CustomerData? = null

    private var isGetInfoClick: Boolean = false
    private var isPageLoading = false
    private var startDateRange: String = ""
    private var endDateRange: String = ""

    private var isApiLastPage = false
    private var currentPage = 1
    private var filterCount = 0
    var delay: Long = 500
    var lastTextEdit: Long = 0
    private var customerTypeFilterApply = false
    private var staffTypeFilterApply = false
    private var fullFilledFilterApply = false
    private var receivedFilterApply = false
    private var paymentFilterApply = false
    private var orderAdminApply = false

    private var rejectedPosition = -1

    private var rejectedOrderModel: OrderData? = null

    private lateinit var horizontalLayoutManager: LinearLayoutManager

    private var filterChange = false

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var geoAddress: String? = ""

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null
    private var checkboxFullFilledMap = HashMap<Int, Boolean>()
    private var checkboxCustomerMap = HashMap<Int, Boolean>()
    private var checkboxStaffMap = HashMap<Int, Boolean>()
    private var checkboxReceivedOnMap = HashMap<Int, Boolean>()
    private var checkboxPaymentMap = HashMap<Int, Boolean>()
    private var checkboxAdminMap = HashMap<Int, Boolean>()
    private var fullFilledList = ArrayList<Int?>()
    private var receivedOnList = ArrayList<String>()
    private var paymentOnList = ArrayList<String>()
    private var staffType = ArrayList<Int>()
    private var orderTakenByList = ArrayList<Int>()
    private var orderTypeList = ArrayList<Int>()
    private var customerTypeList = ArrayList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllListOfOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        getUserCurrentLocation()
        initRecyclerView()
        setStatusList()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE
        getOrderList()
        binding.tvAllRange.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.tvFilter.setOnClickListener(this)
        binding.etSearch.setOnClickListener(this)
        binding.tvFilter.setOnClickListener(this)
        binding.tvSortBy.setOnClickListener(this)
        binding.ivClearSearch.setOnClickListener(this)
        binding.tvDate.setOnClickListener(this)
        binding.dateClear.setOnClickListener(this)

        binding.swipeToRefresh.setOnRefreshListener {
            getOrderList()
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.etSearch1.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                recentOrderList.clear()
                binding.progressBar.visibility = View.VISIBLE
                getOrderList()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)
                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE


                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }
        })


    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            recentOrderList.clear()
            binding.progressBar.visibility = View.VISIBLE
            getOrderList()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(this)) {
                setUpdatedLocationListener()
            } else {
                locationPermissionUtils.showEnableGpsDialog()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val myLocation = MyLocation()
        myLocation.getLocation(this, locationResult)
    }


    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {

            if (Utils.isMockLocation(myLocation)) {
                if (isFinishing.not() && supportFragmentManager.isStateSaved.not()) {
                    val fragment =
                        MockLocationDetectedDialogFragment.getInstance(this@AllListOfOrdersActivity)
                    fragment.isCancelable = false
                    fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                    )
                }
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    GeoLocationUtils.getAddress(
                        this@AllListOfOrdersActivity,
                        longitude = geoLocationLong,
                        latitude = geoLocationLat
                    ) { address ->
                        geoAddress = address
                    }
                }
            }
        }
    }

    private fun validateSearch() {
        binding.progressBar.visibility = View.VISIBLE
        dashboardViewModel.getSearchResultForOrderData(
            "", binding.etSearch1.text.toString(),
            currentPage
        )
    }

    private fun getOrderList() {
        currentPage = 1
        isApiLastPage = false
        isPageLoading = true
        dashboardViewModel.getOrderData(
            orderStatus,
            fullFilledList,
            selectedCustomerLevel,
            currentPage,
            null,
            startDateRange,
            endDateRange,
            null,
            null,
            "",
            selectedCustomerModel?.id,
            isArchived = true,
            binding.etSearch1.text.toString(),
            customerTypeList,
            paymentOnList,
            receivedOnList,
            orderTakenByList,
            sortBy = sortingBY,
            sortingOrder,

            hasInternetConnection()
        )
    }


    private fun initObservers() {
        dashboardViewModel.getOrderLiveData().observe(this) { data ->
            binding.paginationProgressBar.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            isPageLoading = false
            enableTouch()
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    if (currentPage == 1) {
                        recentOrderList.clear()
                    }
                    recentOrderList.addAll(data.data!!)

                    if (data.data!!.size < 30) {
                        isApiLastPage = true
                    }

                    recentOrderAdapter.notifyDataSetChanged()

                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        binding.clEmptyData.visibility = View.VISIBLE
                        recentOrderList.clear()
                        recentOrderAdapter.notifyDataSetChanged()
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

        dashboardViewModel.updateOrderStatusLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            data?.let {
                if (data.error == false) {
                    Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
                    getOrderList()
                } else if (data.message != null) {
                    Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
                }
            }

            enableTouch()
        }
    }

    private fun setStatusList() {
        val list = resources.getStringArray(R.array.order_status_for_filter)

        list.forEachIndexed { index, value ->
            val model = AllCategoryResponseModel()
            if (index == 0) {
                model.isSelected = true
            }
            model.name = value
            statusList.add(model)
        }
        initTabLayout()
    }

    private fun initTabLayout() {
        binding.rvStatus.setHasFixedSize(true)
        horizontalLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvStatus.layoutManager = horizontalLayoutManager

        statusFilterAdapter = StatusFilterAdapter(statusList, this)
        binding.rvStatus.adapter = statusFilterAdapter
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvOrderList.layoutManager = linearLayoutManager
        recentOrderAdapter = SalesRecentNewOrderAdapter(
            recentOrderList,
            this,
            this,
            hasInternetConnection(),
            supportFragmentManager
        )
        binding.rvOrderList.adapter = recentOrderAdapter

        binding.rvOrderList.addOnScrollListener(object :
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
    }

    private fun loadNextPage() {
        dashboardViewModel.getOrderData(
            orderStatus,
            fullFilledList,
            selectedCustomerLevel,
            currentPage,
            null,
            "",
            "",
            null,
            orderId = null,
            "",
            selectedCustomerModel?.id,
            isArchived = false,
            binding.etSearch1.text.toString(),
            customerTypeList,
            paymentOnList,
            receivedOnList,
            staffType,
            sortBy = sortingBY,
            sortingOrder,
            hasInternetConnection()
        )
        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
    }

    override fun onStatusChange(model: OrderData, position: Int, status: String) {
        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
            if (model.deliveryStatus != status) {
                val orderData = OrderData()

                when (AppConstant.getOrderStatusForUpdate(status)) {
                    AppConstant.ORDER_REJECTED -> {
                        rejectedPosition = position
                        openRejectOrderBottomSheet(model)
                    }

                    AppConstant.SHIPPED_ORDER -> {
                        dispatchActivityResultLauncher.launch(
                            Intent(this, CreateShipmentOrderActivity::class.java).putExtra(
                                AppConstant.ORDER_ID,
                                model.id
                            )
                        )
                    }

                    else -> {
                        binding.progressBar.visibility = View.VISIBLE
                        disableTouch()
                        orderData.deliveryStatus = AppConstant.getOrderStatusForUpdate(status)
                        dashboardViewModel.updateOrderStatus(orderData, model.id ?: 0)
                    }
                }
            }
        } else {
            showStartDayDialog()
        }
    }

    private fun showStartDayDialog() {

        if (supportFragmentManager.fragments.firstOrNull {
                it.tag?.equals(
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                ) == true
            } == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
                this,
                geoLocationLat,
                geoLocationLong
            )
            fragment?.show(
                supportFragmentManager,
                MarkAttendanceBottomSheetDialogFragment::class.java.name
            )
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(
                    supportFragmentManager,
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                )
            }
        }

    }

    private fun openRejectOrderBottomSheet(model: OrderData) {
        val fragment = OrderRejectedBottomSheetDialogFragment(model, this)
        fragment.show(supportFragmentManager, AppConstant.ORDER_REJECTED)
    }


    override fun commentOfOrderRejected(model: OrderData) {
        rejectedOrderModel = model
        rejectedOrderModel!!.deliveryStatus = AppConstant.ORDER_REJECTED
        dashboardViewModel.updateOrderStatus(model, model.id ?: 0)
    }

    override fun onDismissDialog(model: OrderData) {
        if (rejectedPosition != -1 && rejectedPosition < recentOrderList.size) {
            recentOrderList[rejectedPosition] = model
            recentOrderAdapter.notifyItemChanged(rejectedPosition)
        }
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
    }

    override fun onGetOrderInfo(model: OrderData, position: Int) {
        isGetInfoClick = true
        someActivityResultLauncher.launch(
            Intent(
                this,
                OrderDetailActivity::class.java
            ).putExtra(AppConstant.ORDER_ID, model.id)
        )
    }

    override fun onDeleteOrder(model: OrderData, position: Int) {
        showDeleteDialog(model)
    }

    override fun getStoreFrontInfo() {
        val fragment = InfoBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_order))
        bundle.putString(
            AppConstant.MESSAGE,
            resources.getString(R.string.storefront_order_message)
        )
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, AppConstant.STORE_FRONT)
    }

    private fun showDeleteDialog(model: OrderData) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_order)
        tvTitle.text = resources.getString(R.string.delete_order_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("is_archived", true)
            dashboardViewModel.deleteOrder(jsonObject, model.id!!, hasInternetConnection())

            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            currentPage = 1
            recentOrderList.clear()
            recentOrderAdapter.notifyDataSetChanged()
            getOrderList()
        }
    }

    @SuppressLint("SetTextI18n")
    var dispatchActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            binding.progressBar.visibility = View.VISIBLE
            recentOrderList.clear()
            recentOrderAdapter.notifyDataSetChanged()
            getOrderList()
        }
    }

    private var filterActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            binding.progressBar.visibility = View.VISIBLE
            recentOrderList.clear()
            recentOrderAdapter.notifyDataSetChanged()
            getOrderList()
        }
    }

    override fun changeOrderFilter(
        status: String,
        customerLevel: String,
        customerSelect: CustomerData?,
        selectedValue: String
    ) {

        binding.tvAllRange.text = selectedValue
        binding.clEmptyData.visibility = View.GONE
        filterChange = true

        selectedCustomerLevel = customerLevel
        selectedCustomerModel = customerSelect

        recentOrderList.clear()
        recentOrderAdapter.notifyDataSetChanged()

        orderStatus = status

        for (i in statusList.indices) {
            statusList[i].isSelected = false
        }

        binding.progressBar.visibility = View.VISIBLE
        getOrderList()
    }

    override fun onStatusSelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected.not()) {
            for (i in statusList.indices) {
                statusList[i].isSelected = false
            }

            orderStatus = if (position != 0) {
                statusList[position].name.toString()
            } else {
                ""
            }

            statusList[position].isSelected = true
            statusFilterAdapter.notifyDataSetChanged()

            recentOrderList.clear()
            recentOrderAdapter.notifyDataSetChanged()

            binding.progressBar.visibility = View.VISIBLE
            getOrderList()
        }
    }


    override fun onPermissionsGiven() {
        super.onPermissionsGiven()
        getUserCurrentLocation()
    }

    override fun onPermissionsDenied() {
        super.onPermissionsDenied()
        getUserCurrentLocation()
    }

    override fun onGpsEnabled() {
        super.onGpsEnabled()
        Handler(Looper.myLooper()!!).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onDismissDialogForStartDay() {
        super.onDismissDialogForStartDay()
        recentOrderAdapter.notifyDataSetChanged()
    }

    override fun onSuccessfullyMarkAttendance() {
        super.onSuccessfullyMarkAttendance()
        recentOrderAdapter.notifyDataSetChanged()
    }


    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        if (intent.hasExtra(AppConstant.ORDER_ID)) {
            startActivity(Intent(this, SalesMainActivity::class.java))
        } else {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun applySorting(
        sortBy: String,
        sortOrder: String,
        mSelected: Boolean,
        mPosition: Int
    ) {
        sortingOrder = sortOrder
        sortingBY = sortBy
        currentPage = 1
        selected = mSelected
        position = mPosition
        binding.progressBar.visibility = View.VISIBLE
        recentOrderList.clear()
        recentOrderAdapter.notifyDataSetChanged()
        getOrderList()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
                recentOrderList.clear()
            }

            binding.tvFilter -> {
                filterActivityResultLauncher.launch(
                    Intent(this, OrdersFilterActivity.newInstance(this, this)::class.java).putExtra(
                        AppConstant.CUSTOMER_MAP,
                        checkboxCustomerMap
                    ).putExtra(AppConstant.STAFF_MAP, checkboxStaffMap)
                        .putExtra(AppConstant.FULLFILLED_MAP, checkboxFullFilledMap)
                        .putExtra(AppConstant.RECEIVED_MAP, checkboxReceivedOnMap)
                        .putExtra(AppConstant.PAYMENT_MAP, checkboxPaymentMap)
                        .putExtra(AppConstant.ORDER_MAP, checkboxAdminMap)
                        .putExtra(AppConstant.RECEIVED, receivedOnList)
                        .putExtra(AppConstant.FULLFILLED, fullFilledList)
                        .putExtra(AppConstant.CUSTOMER, customerTypeList)
                        .putExtra(AppConstant.STAFF, staffType)
                        .putExtra(AppConstant.PAYMENT, paymentOnList)
                        .putExtra(AppConstant.ORDER, orderTypeList)
                )

            }

            binding.dateClear -> {
                binding.tvHdDelivery.visibility = View.GONE
                startDateRange = ""
                endDateRange = ""
                recentOrderList.clear()
                currentPage = 1
                recentOrderAdapter.notifyDataSetChanged()
                getOrderList()

            }

            binding.tvSortBy -> {

                val fragment = OrderListSortByBottomSheetDialogFragment.newInstance(
                    this, selected!!, sortingOrder, position!!
                )
                fragment.show(
                    supportFragmentManager,
                    GallerySortByBottomSheetDialogFragment::class.java.name
                )
            }

            binding.etSearch -> {
                binding.etSearch.visibility = View.GONE
                binding.tvSortBy.visibility = View.GONE
                binding.tvToolbarTitle.visibility = View.GONE
                binding.etSearch1.showWithRightToLeftAnimation()
                binding.ivClearSearch.visibility = View.VISIBLE

            }

            binding.tvDate -> {
                showDatePickerDialog()

            }

            binding.tvAllRange -> {
                val fragment = OrderLevelBottomSheetDialogFragment.newInstance(
                    this,
                    orderStatus,
                    selectedCustomerLevel,
                    selectedCustomerModel
                )
                fragment.show(
                    supportFragmentManager,
                    OrderLevelBottomSheetDialogFragment::class.java.name
                )
            }

            binding.ivClearSearch -> {
                binding.etSearch1.setText("")
                binding.tvSortBy.visibility = View.VISIBLE
                binding.ivClearSearch.visibility = View.GONE
                binding.etSearch.visibility = View.VISIBLE
                binding.tvToolbarTitle.visibility = View.VISIBLE
                binding.etSearch1.hideWithRightToLeftAnimation()
                binding.tvErrorMessage.visibility = View.GONE
                recentOrderList.clear()
                recentOrderAdapter.notifyDataSetChanged()
                getOrderList()
            }
        }
    }

    override fun applyFilter(
        customerType: ArrayList<Int>,
        staffType: ArrayList<Int>,
        receivedType: ArrayList<String>,
        fullFilledType: ArrayList<Int?>,
        orderAdminType: ArrayList<Int>,
        currentSelectedPaymentOnItems: ArrayList<String>
    ) {
        this.customerTypeList = customerType
        this.staffType = staffType
        this.fullFilledList = fullFilledType
        this.receivedOnList = receivedType
        this.paymentOnList = currentSelectedPaymentOnItems
        this.orderTypeList = orderAdminType




        binding.progressBar.visibility = View.VISIBLE


        if (customerTypeList.isNotEmpty() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }

        if (staffType.isNotEmpty() && staffTypeFilterApply.not()) {
            ++filterCount
            staffTypeFilterApply = true

        }
        if (fullFilledList.isNotEmpty() && fullFilledFilterApply.not()) {
            ++filterCount
            fullFilledFilterApply = true

        }
        if (receivedType.isNotEmpty() && receivedFilterApply.not()) {
            ++filterCount
            receivedFilterApply = true

        }
        if (paymentOnList.isNotEmpty() && paymentFilterApply.not()) {
            ++filterCount
            paymentFilterApply = true
        }

        if (!isStaffUserActivity()) {

            if (orderTypeList.isNotEmpty() && orderAdminApply.not()) {
                ++filterCount
                orderAdminApply = true
                Log.e("tagList", "$orderTypeList")
            }
            if (orderTypeList.isEmpty() && orderAdminApply) {
                --filterCount
                orderAdminApply = false
                Log.e("tagList", "$orderTypeList")
            }
        }

        if (customerTypeList.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        if (staffType.isEmpty() && staffTypeFilterApply) {
            --filterCount
            staffTypeFilterApply = false

        }
        if (fullFilledList.isEmpty() && fullFilledFilterApply) {
            --filterCount
            fullFilledFilterApply = false

        }
        if (receivedOnList.isEmpty() && receivedFilterApply) {
            --filterCount
            receivedFilterApply = false

        }
        if (paymentOnList.isEmpty() && paymentFilterApply) {
            --filterCount
            paymentFilterApply = false
        }
        orderTakenByList.clear()
        if (!isStaffUserActivity()) {

            orderTakenByList.addAll(orderTypeList)
            orderTakenByList.addAll(staffType)
        } else {
            orderTakenByList.addAll(staffType)
        }


        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
            customerTypeList.clear()
            staffType.clear()
            fullFilledList.clear()
            recentOrderList.clear()
            orderTypeList.clear()
            orderTakenByList.clear()

        }
        recentOrderList.clear()
        currentPage = 1
        recentOrderAdapter.notifyDataSetChanged()
        getOrderList()
    }

    override fun onItemStateHolder(
        checkboxReceivedMap: HashMap<Int, Boolean>,
        checkboxCustomerMap: HashMap<Int, Boolean>,
        checkboxStaffMap: HashMap<Int, Boolean>,
        checkboxFullFillMap: HashMap<Int, Boolean>,
        checkboxPaymentMap: HashMap<Int, Boolean>,
        checkboxAdminMap: HashMap<Int, Boolean>
    ) {
        this.checkboxReceivedOnMap = checkboxReceivedMap
        this.checkboxCustomerMap = checkboxCustomerMap
        this.checkboxStaffMap = checkboxStaffMap
        this.checkboxFullFilledMap = checkboxFullFillMap
        this.checkboxPaymentMap = checkboxPaymentMap
        this.checkboxAdminMap = checkboxAdminMap
        Log.e("tag", "" + checkboxAdminMap)
    }

    private fun showDatePickerDialog() {

        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.now()
        )


        // Creating a MaterialDatePicker builder for selecting a date range
        val builder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText(AppConstant.SELECT_DATE)
        builder.setCalendarConstraints(constraintsBuilder.build())

        // Building the date picker dialog
        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->

            selection?.let {
                // Retrieve the selected start and end dates
                val startDate = Date(it.first)
                val endDate = Date(it.second)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // Perform date formatting using coroutines
                CoroutineScope(Dispatchers.Main).launch {
                    val startDateRangeValue = formatDatesAsync(startDate, sdf)
                    val endDateRangeValue = formatDatesAsync(endDate, sdf)
                    binding.tvHdDelivery.visibility = View.VISIBLE

                    binding.tvDateValue.text = buildString {
                        append(DateFormatHelper.convertSanctionDateOrder(startDateRangeValue))
                        append("-")
                        append(DateFormatHelper.convertSanctionDateOrder(endDateRangeValue))
                    }
                    startDateRange = startDateRangeValue
                    endDateRange = endDateRangeValue
                    recentOrderList.clear()
                    currentPage = 1
                    recentOrderAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.VISIBLE
                    getOrderList()
                    //DateRangeOrdersBottomSheetDialogFragment.listener.dateRangeFilter(startDateRange, endDateRange, AppConstant.CUSTOM_RANGE)

                }
            }
        }

        // Showing the date picker dialog
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private suspend fun formatDatesAsync(startDate: Date, sdf: SimpleDateFormat): String {
        return withContext(Dispatchers.IO) {
            val startDateString = sdf.format(startDate)
            //val endDateString = sdf.format(endDate)
            startDateString
        }
    }


}