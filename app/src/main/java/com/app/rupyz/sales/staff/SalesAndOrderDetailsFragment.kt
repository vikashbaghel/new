package com.app.rupyz.sales.staff

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffsSalesAndOrderDetailsFragmentBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.home.CustomerOrderAdapter
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.orders.OrderRejectedBottomSheetDialogFragment
import com.app.rupyz.sales.home.SalesRecentNewOrderAdapter
import com.google.gson.JsonObject

@SuppressLint("NotifyDataSetChanged")

class SalesAndOrderDetailsFragment : BaseFragment(), OrderStatusActionListener,
    DeleteDialog.IOnClickListener,
    OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private var customerId: Int? = 0
    private lateinit var binding: StaffsSalesAndOrderDetailsFragmentBinding

    private lateinit var recentOrderAdapter: CustomerOrderAdapter
    private val salesAnOrderDetailsViewModel: SalesAnOrderDetailsViewModel by viewModels()

    private var recentOrderList: ArrayList<OrderData> = ArrayList()

    private var rejectedPosition = -1
    private var rejectedOrderModel: OrderData? = null

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StaffsSalesAndOrderDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getInt(AppConstant.CUSTOMER_ID)
        locationPermissionUtils = LocationPermissionUtils(this, requireActivity())

        initLayout()
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {
        getUserCurrentLocation()
        initRecyclerView()
        initObservers()
        loadOrderList()
    }


    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(requireActivity())) {
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
        myLocation.getLocation(requireContext(), locationResult)
    }


    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {

            if (Utils.isMockLocation(myLocation)) {
                val fragment =
                    MockLocationDetectedDialogFragment.getInstance(null)
                fragment.isCancelable = false
                fragment.show(
                    childFragmentManager,
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

    private fun loadOrderList() {

        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_ORDER_PERMISSION, false)) {
            binding.progressBar.visibility = View.VISIBLE
            salesAnOrderDetailsViewModel.getRecentOrderListById(
                customerId ?: 0, currentPage, hasInternetConnection()
            )
        } else {
            binding.clEmptyData.visibility = View.VISIBLE
            binding.tvErrorMessage.text = resources.getString(R.string.order_permission)
        }
    }

    fun initObservers() {
        if (activity != null){
            salesAnOrderDetailsViewModel.recentOrderLiveData.observe(requireActivity()) {
                binding.progressBar.visibility = View.GONE
                
                if (it.error == false) {
                    isPageLoading = false
                    if (it.data.isNullOrEmpty().not()) {
                        binding.clEmptyData.visibility = View.GONE
                        if (currentPage == 1) {
                            recentOrderList.clear()
                        }
                        recentOrderList.addAll(it.data!!)
                        
                        if (it.data!!.size < 30) {
                            isApiLastPage = true
                        }
                        recentOrderAdapter.notifyDataSetChanged()
                    } else {
                        if (currentPage == 1) {
                            isApiLastPage = true
                            binding.clEmptyData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                
                enableTouch()
            }
            
            dashboardViewModel.updateOrderStatusLiveData.observe(viewLifecycleOwner) { data ->
                data?.let {
                    if (data.error == false) {
                        Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
                        loadOrderList()
                    } else {
                        Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
                        enableTouch()
                    }
                }
            }
        }
     
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentOrder.layoutManager = linearLayoutManager
        recentOrderAdapter = CustomerOrderAdapter(
            recentOrderList,
            requireContext(),
            this,
            hasInternetConnection(),
            requireActivity().supportFragmentManager
        )
        binding.rvRecentOrder.adapter = recentOrderAdapter

        binding.rvRecentOrder.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadOrderList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
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
                            Intent(
                                requireContext(),
                                CreateShipmentOrderActivity::class.java
                            ).putExtra(
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
//            else {
//                showStartDayDialog()
//            }
        } else {
            showStartDayDialog()
        }
    }


    private fun showStartDayDialog() {
        if (childFragmentManager.fragments.firstOrNull {
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
                childFragmentManager,
                MarkAttendanceBottomSheetDialogFragment::class.java.name
            )
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                childFragmentManager.fragments.remove(fragment)
                fragment?.show(
                    childFragmentManager,
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                )
            }
        }
//        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
//        fragment.show(childFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
    }

    private fun openRejectOrderBottomSheet(model: OrderData) {
        val fragment = OrderRejectedBottomSheetDialogFragment(model, this)
        fragment.show(childFragmentManager, AppConstant.ORDER_REJECTED)
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
        someActivityResultLauncher.launch(
            Intent(
                context,
                OrderDetailActivity::class.java
            ).putExtra(AppConstant.ORDER_ID, model.id)
        )
    }

    override fun onDeleteOrder(model: OrderData, position: Int) {
        DeleteDialog.showDeleteDialog(
            requireActivity(), model.id, position, resources.getString(R.string.delete_order),
            resources.getString(R.string.delete_order_message), this
        )
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
        fragment.show(childFragmentManager, AppConstant.STORE_FRONT)
    }


    @SuppressLint("SetTextI18n")
    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            loadOrderList()
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

    override fun onDelete(model: Any, position: Any) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("is_archived", true)
        dashboardViewModel.deleteOrder(jsonObject, model as Int, hasInternetConnection())

    }

}