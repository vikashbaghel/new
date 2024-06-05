package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAllListOfOrdersBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharePrefConstant.ENABLE_CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.home.SalesRecentOrderAdapter
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.google.gson.JsonObject

class AllListOfOrdersActivity : BaseActivity(),
    OrderFilterBottomSheetDialogFragment.IOrderFilterListener,
    StatusFilterAdapter.StatusSelectListener, OrderStatusActionListener,
    OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityAllListOfOrdersBinding

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var statusFilterAdapter: StatusFilterAdapter
    private lateinit var recentOrderAdapter: SalesRecentOrderAdapter

    private var statusList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var recentOrderList = java.util.ArrayList<OrderData>()

    private var orderStatus: String = ""
    private var selectedCustomerLevel: String = ""
    private var selectedCustomerModel: CustomerData? = null

    private var isGetInfoClick: Boolean = false
    private var isPageLoading = false

    private var isApiLastPage = false
    private var currentPage = 1
    private var filterCount = 0
    private var levelFilterApply = false
    private var customerModelFilterApply = false

    private var rejectedPosition = -1

    private var rejectedOrderModel: OrderData? = null

    private lateinit var horizontalLayoutManager: LinearLayoutManager

    private var filterChange = false

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var geoAddress: String? = ""

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private  var  fragment : MarkAttendanceBottomSheetDialogFragment? = null

    
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

        if (SharedPref.getInstance().getBoolean(ENABLE_CUSTOMER_LEVEL_ORDER, false)) {
            binding.ivFilter.visibility = View.VISIBLE
        } else {
            binding.tvFilterCount.visibility = View.GONE
            binding.ivFilter.visibility = View.GONE
        }

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.etSearch.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchOrderActivity::class.java
                )
            )
        }

        binding.swipeToRefresh.setOnRefreshListener {
            getOrderList()
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.ivFilter.setOnClickListener {
            val fragment = OrderFilterBottomSheetDialogFragment.newInstance(
                this,
                orderStatus,
                selectedCustomerLevel,
                selectedCustomerModel
            )
            fragment.show(supportFragmentManager, AppConstant.FILTER_BADGE)
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
                val fragment =
                    MockLocationDetectedDialogFragment.getInstance(null)
                fragment.isCancelable = false
                fragment.show(
                    supportFragmentManager,
                    MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    geoAddress = GeoLocationUtils.getAddress(this@AllListOfOrdersActivity, longitude =  geoLocationLong, latitude =  geoLocationLat)
                }
            }
        }
    }

    private fun getOrderList() {
        currentPage = 1
        isApiLastPage = false
        isPageLoading = true
        dashboardViewModel.getOrderData(
            orderStatus,
            selectedCustomerModel?.id,
            selectedCustomerLevel,
            currentPage,
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
        recentOrderAdapter = SalesRecentOrderAdapter(recentOrderList, this, this, hasInternetConnection())
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
            selectedCustomerModel?.id,
            selectedCustomerLevel,
            currentPage,
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
        
        if (supportFragmentManager.fragments.firstOrNull { it.tag?.equals(MarkAttendanceBottomSheetDialogFragment::class.java.name) == true } == null){
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
            fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
        }else{
            if (fragment?.isVisible == false && fragment?.isAdded == false){
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
            }
        }
//        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
//        fragment.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
    
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
        recentOrderList[rejectedPosition] = model
        recentOrderAdapter.notifyItemChanged(rejectedPosition)
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

    override fun changeOrderFilter(
        status: String,
        customerLevel: String,
        customerSelect: CustomerData?,
        orderSelectedIndex: Int
    ) {
        binding.clEmptyData.visibility = View.GONE
        filterChange = true

        selectedCustomerLevel = customerLevel
        selectedCustomerModel = customerSelect

        if (selectedCustomerLevel.isEmpty().not() && levelFilterApply.not()) {
            ++filterCount
            levelFilterApply = true
        }
        if (selectedCustomerModel?.id != null && customerModelFilterApply.not()) {
            ++filterCount
            customerModelFilterApply = true
        }
        if (selectedCustomerLevel.isEmpty() && levelFilterApply) {
            --filterCount
            levelFilterApply = false
        }
        if (selectedCustomerModel == null && customerModelFilterApply) {
            --filterCount
            customerModelFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        recentOrderList.clear()
        recentOrderAdapter.notifyDataSetChanged()

        orderStatus = status

        for (i in statusList.indices) {
            statusList[i].isSelected = false
        }

        statusList[orderSelectedIndex].isSelected = true
        statusFilterAdapter.notifyDataSetChanged()

        if (orderSelectedIndex > 0) {
            binding.rvStatus.layoutManager?.scrollToPosition(orderSelectedIndex - 1)
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


    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.ORDER_ID)) {
            startActivity(Intent(this, SalesMainActivity::class.java))
        } else {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}