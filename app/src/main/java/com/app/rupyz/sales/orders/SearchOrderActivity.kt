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
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySearchOrderBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.home.SalesRecentOrderAdapter
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.google.gson.JsonObject

class SearchOrderActivity : BaseActivity(), OrderStatusActionListener,
        OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
        LocationPermissionUtils.ILocationPermissionListener,
        MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivitySearchOrderBinding
    private lateinit var recentOrderAdapter: SalesRecentOrderAdapter
    private var recentOrderList = ArrayList<OrderData>()
    private lateinit var dashboardViewModel: DashboardViewModel

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var rejectedPosition = -1
    private var rejectedOrderModel: OrderData? = null

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private  var  fragment : MarkAttendanceBottomSheetDialogFragment? = null

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        binding.etSearch.requestFocus()

        getUserCurrentLocation()
        initRecyclerView()
        initObservers()

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            recentOrderList.clear()
            Utils.hideKeyboard(this)
            validateSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                recentOrderList.clear()
                validateSearch()
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
            binding.tvErrorMessage.visibility = View.GONE

            recentOrderList.clear()
            recentOrderAdapter.notifyDataSetChanged()
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            recentOrderList.clear()
            validateSearch()
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
                }
            }
        }
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
                validateSearch()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun initObservers() {
        dashboardViewModel.getOrderLiveData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            data.data?.let { it ->
                isPageLoading = false
                if (!it.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvErrorMessage.visibility = View.GONE
                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                    if (currentPage == 1) {
                        recentOrderList.clear()
                    }
                    recentOrderList.addAll(it)
                    recentOrderAdapter.notifyDataSetChanged()
                } else {
                    isApiLastPage = true
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }
            }
        }


        dashboardViewModel.updateOrderStatusLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            data?.let {
                if (data.error == false) {
                    Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
                    currentPage = 1
                    validateSearch()
                } else if (data.message != null) {
                    Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
                }
            }

            enableTouch()
        }
    }

    private fun validateSearch() {
        binding.progressBar.visibility = View.VISIBLE

        dashboardViewModel.getSearchResultForOrderData(
                "", binding.etSearch.text.toString(),
                currentPage
        )
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
                        startActivity(
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

    override fun onGetOrderInfo(model: OrderData, position: Int) {
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

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            validateSearch()
        }
    }

    override fun commentOfOrderRejected(model: OrderData) {
        rejectedOrderModel = model
        rejectedOrderModel!!.deliveryStatus = AppConstant.ORDER_REJECTED
        disableTouch()
        dashboardViewModel.updateOrderStatus(model, model.id ?: 0)
    }

    override fun onDismissDialog(model: OrderData) {
        recentOrderList[rejectedPosition] = model
        recentOrderAdapter.notifyItemChanged(rejectedPosition)
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

    override fun onResume() {
        super.onResume()
        if (binding.etSearch.text.toString().isNotEmpty()) {
            validateSearch()
        }
    }
}