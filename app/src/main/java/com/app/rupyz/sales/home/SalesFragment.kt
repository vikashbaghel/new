package com.app.rupyz.sales.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.BuildConfig
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.MySalesFragmentBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.custom.MyMarkerView
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.fromJson
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.getTimeDifference
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.isMyAppIsBatteryOptimizationMode
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.toHoursMinutesSeconds
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.isTimeLessThanTo8PM
import com.app.rupyz.model_kt.ActiveBeatRouteInfoAndDayListModel
import com.app.rupyz.model_kt.DeviceActivityListItem
import com.app.rupyz.model_kt.OrganizationWiseSalesDataItem
import com.app.rupyz.model_kt.SaveAttendanceModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import com.app.rupyz.model_kt.StaffTargetModel
import com.app.rupyz.model_kt.UserInfoData
import com.app.rupyz.model_kt.order.dashboard.DashboardData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.analytics.AnalyticsOverViewActivity
import com.app.rupyz.sales.beat.BeatListActivity
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.sales.beatplan.MyBeatPlanActivity
import com.app.rupyz.sales.customer.ListOfCustomerActivity
import com.app.rupyz.sales.lead.AllLeadListActivity
import com.app.rupyz.sales.login.LoginViewModel
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.app.rupyz.sales.orders.AllListOfOrdersActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.orders.OrderRejectedBottomSheetDialogFragment
import com.app.rupyz.sales.organization.OrganizationViewModel
import com.app.rupyz.sales.payment.ListOfPaymentActivity
import com.app.rupyz.sales.product.ProductListActivity
import com.app.rupyz.sales.staff.ListOfStaffActivity
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.sales.targets.TargetsListViewPagerAdapter
import com.app.rupyz.service.EndlessService
import com.app.rupyz.service.MarkEndDayJobService
import com.app.rupyz.service.ServiceState
import com.app.rupyz.service.StopLiveTrackingJobService
import com.app.rupyz.service.getServiceState
import com.app.rupyz.ui.more.MoreViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


class SalesFragment : BaseFragment(), OrderStatusActionListener, OnChartValueSelectedListener,
    OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    LocationPermissionUtils.ILocationPermissionListener,
    TargetsListViewPagerAdapter.ITargetActionListener, DeleteDialog.IOnClickListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: MySalesFragmentBinding
    private var sortingBY: String = AppConstant.CREATED_AT
    private var sortingOrder: String = AppConstant.SORTING_LEVEL_DESCENDING

    private lateinit var recentOrderAdapter: SalesRecentNewOrderAdapter
    private lateinit var targetsListViewPagerAdapter: TargetsListViewPagerAdapter

    private var recentOrderList = ArrayList<OrderData>()
    private var targetList = ArrayList<StaffTargetModel>()

    private var fullFilledList = ArrayList<Int?>()
    private var receivedOnList = ArrayList<String>()
    private var paymentOnList = ArrayList<String>()
    private var staffType = ArrayList<Int>()
    private var customerTypeList = ArrayList<Int>()
    private lateinit var staffViewModel: StaffViewModel
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var activityViewModel: StaffActivityViewModel
    private lateinit var organizationViewModel: OrganizationViewModel
    private lateinit var moreViewModel: MoreViewModel
    private lateinit var loginViewModel: LoginViewModel

    private var rejectedPosition = -1
    private var rejectedOrderModel: OrderData? = null

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private var location: Location? = null

    private var locationManager: LocationManager? = null
    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private var beatRoutePlanId: Int = 0
    private var beatId: Int = 0
    private var beatDate: String = ""

    private var currentlyActiveTargets: StaffCurrentlyActiveDataModel? = null

    private var isStartDayDialogShow = false
    private var isProfileDataAvailable = false
    private var isLocationPermissionDialogVisible = false

    private var locationPermissionDialog: Dialog? = null

    private var locationPermissionImageList = listOf(
        R.mipmap.app_settings,
        R.mipmap.settings_apps,
        R.mipmap.app_management,
        R.mipmap.rupyz_app,
        R.mipmap.permissions_section,
        R.mipmap.location_section,
        R.mipmap.allow_all_time
    )

    private val startTimeHandler = Handler(Looper.myLooper()!!)
    private var startTime: Long = 0

    companion object {
        private var updateMainDataListener: UpdateMainDataListener? = null
        fun getInstance(listener: UpdateMainDataListener?): SalesFragment {
            updateMainDataListener = listener
            return SalesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MySalesFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        activityViewModel = ViewModelProvider(this)[StaffActivityViewModel::class.java]
        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]
        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, requireActivity())
        locationManager =
            this.context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requireContext().registerReceiver(
            gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

        locationPermissionDialog = Dialog(requireActivity())

        initRecyclerView()
        initTargetViewPager()

        initLayout()

        initObservers()

        if (isStaffUser()) {
            addScheduleWorkerForEndDay()
            addScheduleWorkerForStopService()
        }

        SharedPref.getInstance().putBoolean(AppConstant.FAKE_LOCATION_UPDATE_SEND, false)
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun initLayout() {

        if (isStaffUser()) {
            staffViewModel.getCurrentlyActiveTargets(0)
            beatViewModel.getCurrentlyActiveBeatPlan(null, null)
            binding.groupStaffAttendance.visibility = View.VISIBLE
        } else {
            binding.groupStaffAttendance.visibility = View.GONE
        }

        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_ORDER_PERMISSION, false)) {
            binding.groupRecentOrderHd.visibility = View.VISIBLE
        } else {
            binding.groupRecentOrderHd.visibility = View.GONE
        }

        binding.clProduct.setOnClickListener {

            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.VIEW_PRODUCT_PERMISSION, false
                )
            ) {
                someActivityResultLauncher.launch(
                    Intent(
                        requireActivity(), ProductListActivity::class.java
                    )
                )
            } else {
                showToast(resources.getString(R.string.product_list_permission))
            }
        }

        binding.clOrders.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_ORDER_PERMISSION, false)) {
                getOrderListResultLauncher.launch(
                    Intent(
                        requireActivity(), AllListOfOrdersActivity::class.java
                    )
                )
            } else {
                showToast(resources.getString(R.string.order_permission))
            }
        }

        binding.clStaff.setOnClickListener {

            if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_STAFF_PERMISSION, false)) {
                someActivityResultLauncher.launch(
                    Intent(
                        requireActivity(), ListOfStaffActivity::class.java
                    )
                )
            } else {
                showToast(resources.getString(R.string.staff_permission))
            }
        }

        binding.clStaffLead.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_LEAD_PERMISSION, false)) {
                someActivityResultLauncher.launch(
                    Intent(
                        requireActivity(), AllLeadListActivity::class.java
                    )
                )
            } else {
                showToast(resources.getString(R.string.category_lead_permission))
            }
        }
        binding.clCustomer.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.GET_CUSTOMER_PERMISSION, false
                )
            ) {
                someActivityResultLauncher.launch(
                    Intent(
                        requireActivity(), ListOfCustomerActivity::class.java
                    )
                )
            } else {
                showToast(resources.getString(R.string.customer_permission))
            }
        }

        binding.clCustomerPayment.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.VIEW_PAYMENT_PERMISSION, false
                )
            ) {
                startActivity(Intent(requireActivity(), ListOfPaymentActivity::class.java))
            } else {
                showToast(resources.getString(R.string.payment_permission))
            }
        }

        binding.tvViewAllAttendance.setOnClickListener {
            startActivity(
                Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                    AppConstant.ATTENDANCE, true
                )
            )
        }

        binding.tvChartDetails.setOnClickListener {
            startActivity(Intent(requireContext(), AnalyticsOverViewActivity::class.java))
        }

        binding.clStartDay.setOnClickListener {
            if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false).not()) {
                if (Utils.isMockLocation(location).not()) {
                    if (isGpsOn().not()) {
                        locationPermissionUtils.showEnableGpsDialog()
                    } else if (geoLocationLat == 0.0 || geoLocationLong == 0.0) {
                        if (locationPermissionUtils.hasPermission()) {
                            val myLocation = MyLocation()
                            myLocation.getLocation(requireContext(), locationResultIfException)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    } else {
                        showStartDayDialog()
                    }
                } else {
                    openMockLocationDetectionDialog()
                }
            }
        }

        binding.clEndDay.setOnClickListener {
            if (SharedPref.getInstance()
                    .getBoolean(AppConstant.START_DAY, false) && SharedPref.getInstance()
                    .getLong(AppConstant.END_DAY_TIME) == 0L
            ) {
                if (Utils.isMockLocation(location).not()) {
                    showEndDayDialog()
                } else {
                    openMockLocationDetectionDialog()
                }
            }
        }

        getDashBoardData()

        binding.swipeToRefresh.setOnRefreshListener {
            getDashBoardData()
            organizationViewModel.getProfileInfo()
            moreViewModel.getPreferencesInfo()
            staffViewModel.getCurrentlyActiveTargets(0)
            beatViewModel.getCurrentlyActiveBeatPlan(null, null)
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.hdRecentOrdersViewAll.setOnClickListener {
            startActivity(Intent(requireContext(), AllListOfOrdersActivity::class.java))
        }

        binding.clBeatPlan.setOnClickListener {
            startActivity(
                Intent(requireContext(), MyBeatPlanActivity::class.java).putExtra(
                    AppConstant.BEAT_ID, beatId
                ).putExtra(AppConstant.BEAT_ROUTE_PLAN_ID, beatRoutePlanId)
                    .putExtra(AppConstant.DATE_FILTER, beatDate)
            )
        }

        binding.clBeat.setOnClickListener {
            if (hasInternetConnection()) {
                someActivityResultLauncher.launch(
                    Intent(
                        requireContext(), BeatListActivity::class.java
                    )
                )
            } else {
                showToast(AppConstant.THIS_FEATURE_DOES_NOT_SUPPORT_IN_OFFLINE_MODE)
            }
        }
    }

    private fun isGpsOn(): Boolean {
        return Utils.isGpsOn(locationManager)
    }

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val myLocation = MyLocation()
                myLocation.getLocation(requireContext(), locationResultIfException)
            } else {
                showEnablePermissionDialog(false)
            }
        }

    private fun showEnablePermissionDialog(isLiveLocation: Boolean) {
        val locationImageViewPager =
            LocationPermissionImageViewPagerAdapter(locationPermissionImageList)
        if (locationPermissionDialog != null && locationPermissionDialog!!.isShowing.not()) {
            isLocationPermissionDialogVisible = true
            locationPermissionDialog?.setContentView(R.layout.location_permission_dialog)
            locationPermissionDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            locationPermissionDialog?.setCancelable(false)

            locationPermissionDialog?.let { dialog ->
                val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
                val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
                val tvSubTitle = dialog.findViewById<TextView>(R.id.tv_sub_title)
                val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
                val ivActionImage = dialog.findViewById<ImageView>(R.id.iv_action_image)
                val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
                val tvGrantPermission = dialog.findViewById<TextView>(R.id.tv_delete)
                val viewPager = dialog.findViewById<ViewPager2>(R.id.viewPagerMain)
                val sliderDots = dialog.findViewById<LinearLayout>(R.id.slider_dots)
                val imagesGroup = dialog.findViewById<Group>(R.id.group_images)

                viewPager.adapter = locationImageViewPager

                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(MarginPageTransformer(30))

                val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
                val offsetPx = resources.getDimensionPixelOffset(R.dimen.view_pager_offset)

                viewPager.setPageTransformer { page, position ->
                    val offset = position * -(2 * offsetPx + pageMarginPx)
                    if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                        if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                            page.translationX = -offset
                        } else {
                            page.translationX = offset
                        }
                    } else {
                        page.translationY = offset
                    }
                }

                viewPager.setPageTransformer(compositePageTransformer)

                val dotCount = locationImageViewPager.itemCount
                val dots: ArrayList<ImageView> = ArrayList()

                for (i in 0 until dotCount) {
                    val image = ImageView(requireContext())
                    image.setImageDrawable(
                        getDrawable(
                            requireContext(), R.drawable.pager_non_active_dot
                        )
                    )
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(5, 0, 5, 0)
                    dots.add(image)
                    sliderDots.addView(dots[i], params)
                }

                dots[0].setImageDrawable(getDrawable(requireContext(), R.drawable.pager_active_dot))

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int, positionOffset: Float, positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        for (i in 0 until dotCount) {
                            dots[i].setImageDrawable(
                                getDrawable(
                                    requireContext(), R.drawable.pager_non_active_dot
                                )
                            )
                        }
                        dots[position].setImageDrawable(
                            getDrawable(
                                requireContext(), R.drawable.pager_active_dot
                            )
                        )
                    }

                    override fun onPageScrollStateChanged(state: Int) {}
                })

                tvHeading.text = resources.getString(R.string.location_permission_required)
                tvTitle.text = resources.getString(R.string.you_need_grant_location_permission)

                if (isLiveLocation && isStaffUser()) {
                    tvSubTitle.visibility = View.VISIBLE
                    ivClose.visibility = View.GONE
                    tvSubTitle.text = resources.getString(R.string.please_allow_all_the_time)
                    tvTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext().applicationContext, R.color.color_888
                        )
                    )
                    tvSubTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext().applicationContext, R.color.black
                        )
                    )
                    imagesGroup.visibility = View.VISIBLE
                }

                ivActionImage.visibility = View.VISIBLE
                ivActionImage.setImageResource(R.drawable.ic_location_blue)

                tvCancel.visibility = View.GONE
                ivClose.setOnClickListener {
                    dialog.dismiss()
                }

                tvCancel.setOnClickListener {
                    dialog.dismiss()
                }

                tvGrantPermission.text = resources.getString(R.string.grant_permission)

                tvGrantPermission.setOnClickListener {
                    requestLocationPermission()
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }


    private fun openMockLocationDetectionDialog() {
        val fragment = MockLocationDetectedDialogFragment.getInstance(this@SalesFragment)
        fragment.show(childFragmentManager, MockLocationDetectedDialogFragment::class.simpleName)
    }

    private fun showEndDayDialog() {
        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
            this, geoLocationLat, geoLocationLong
        )
        val bundle = Bundle()
        bundle.putBoolean(AppConstant.ATTENDANCE_END_DAY, true)
        fragment.arguments = bundle
        fragment.show(
            childFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name
        )
    }


    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        if (locationPermissionUtils.isGpsEnabled(requireActivity())) {
            if (locationPermissionUtils.hasPermission()) {
                val myLocation = MyLocation()
                myLocation.getLocation(requireContext(), locationResult)
            }
        } else {
            locationPermissionUtils.showEnableGpsDialog()
        }
    }

    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {
            if (isAdded) {
                location = myLocation
                if (Utils.isMockLocation(myLocation)) {
                    openMockLocationDetectionDialog()
                } else {
                    myLocation?.let {
                        geoLocationLat = it.latitude
                        geoLocationLong = it.longitude

                        if (hasInternetConnection()) {
                            if (isStaffUser() && isProfileDataAvailable && isStartDayDialogShow.not() && SharedPref.getInstance()
                                    .getBoolean(
                                        AppConstant.START_DAY, false
                                    ).not()
                            ) {
                                showStartDayDialog()
                            }
                        } else {
                            if (SharedPref.getInstance().getBoolean(
                                    AppConstant.START_DAY, false
                                ).not() && isStartDayDialogShow.not()
                            ) {
                                showStartDayDialog()
                            }
                        }

                        checkForLocationPermissionInDeviceLogs()
                    }
                }
            }
        }
    }

    private fun checkForLocationPermissionInDeviceLogs() {
        launch {
            if (SharedPref.getInstance()
                    .getBoolean(AppConstant.SHARE_LOCATION_PERMISSION_CHANGE_TO_API, false)
            ) {
                val internetTrueModel = DeviceActivityListItem()
                internetTrueModel.locationPermissionType =
                    AppConstant.LocationPermissionType.BACKGROUND
                sendDeviceLogs(internetTrueModel)

                SharedPref.getInstance().putBoolean(
                    AppConstant.SHARE_LOCATION_PERMISSION_CHANGE_TO_API, false
                )
            }
        }
    }

    private var locationResultIfException: MyLocation.LocationResult =
        object : MyLocation.LocationResult() {
            override fun gotLocation(myLocation: Location?) {
                if (isAdded) {
                    location = myLocation
                    myLocation?.let {
                        geoLocationLat = it.latitude
                        geoLocationLong = it.longitude
                        showStartDayDialog()
                    }
                }
            }
        }

    private fun getDashBoardData() {
        dashboardViewModel.getDashboardData(hasInternetConnection())

        dashboardViewModel.getOrderData(
            "",
            fullFilledList,
            "",
            1,
            null,
            "",
            "",
            null,
            null,
            "",
            null,
            false,
            "",
            customerTypeList,
            paymentOnList,
            receivedOnList,
            staffType,
            "",
            "",
            hasInternetConnection()
        )

        if (hasInternetConnection()) {
            if (isStaffUser()) {
                binding.clChart.visibility = View.GONE
            } else if (SharedPref.getInstance().getBoolean(
                    SharePrefConstant.ANALYTICS_CALCULATION_ENABLE, false
                )
            ) {
                binding.clChart.visibility = View.VISIBLE
                initLineChart()
            } else {
                binding.clChart.visibility = View.GONE
            }
        }
    }

    private fun initTargetViewPager() {
        binding.vpTargets.clipToPadding = false
        binding.vpTargets.clipChildren = false
        binding.vpTargets.offscreenPageLimit = 5
        binding.vpTargets.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))

        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.view_pager_offset)

        binding.vpTargets.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }

        binding.vpTargets.setPageTransformer(compositePageTransformer)
    }

    private fun initLineChart() {
        dashboardViewModel.getOrganizationWiseSalesList(
            Utility.getLastTwelveMonthDateFilterModel().filter_type!!,
            Utility.getLastTwelveMonthDateFilterModel().startDate!!,
            Utility.getLastTwelveMonthDateFilterModel().end_date!!,
            1
        )

        // background color
        binding.chart.setBackgroundColor(Color.TRANSPARENT)

        // disable description text
        binding.chart.description.isEnabled = false

        // enable touch gestures
        binding.chart.setTouchEnabled(true)

        // set listeners
        binding.chart.setOnChartValueSelectedListener(this)
        binding.chart.setDrawGridBackground(false)

        // create marker to display box when values are selected
        val mv = MyMarkerView(requireActivity(), R.layout.custom_marker_view)

        // Set the marker to the  binding.chart
        mv.chartView = binding.chart
        binding.chart.marker = mv

        // enable scaling and dragging
        binding.chart.isDragEnabled = false
        binding.chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        binding.chart.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis? = binding.chart.xAxis
        xAxis?.gridColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.chart_grid_color)
        xAxis?.textColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.chart_grid_color)

        // vertical grid lines
        xAxis?.enableGridDashedLine(10f, 0f, 0f)
        xAxis!!.position = XAxis.XAxisPosition.BOTTOM

        xAxis.textColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.theme_purple)
        xAxis.typeface = Typeface.DEFAULT_BOLD

        // // Y-Axis Style // //
        val yAxis: YAxis = binding.chart.axisLeft
        yAxis.gridColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.chart_grid_color)
        yAxis.axisLineColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.chart_grid_color)

        // disable dual axis (only use LEFT axis)
        binding.chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(0f, 0f, 0f)

        // axis range
        yAxis.axisMinimum = 0f
        yAxis.textColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.theme_purple)
        yAxis.typeface = Typeface.DEFAULT_BOLD

        // // Create Limit Lines // //
        val llXAxis = LimitLine(0f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.lineColor =
            ContextCompat.getColor(requireContext().applicationContext, R.color.chart_grid_color)

        llXAxis.enableDashedLine(0f, 10f, 0f)

        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(false)
        xAxis.setDrawLimitLinesBehindData(false)

        // draw points over time

        // draw points over time
        binding.chart.animateX(500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l = binding.chart.legend

        // draw legend entries as lines

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE
    }

    private fun setData(list: MutableList<OrganizationWiseSalesDataItem>) {
        val values = ArrayList<Entry>()
        val xAxis = binding.chart.xAxis
        val xAxisValues: ArrayList<String> = ArrayList()

        xAxis.labelCount = list.size - 1

        list.forEach {
            xAxisValues.add(it.month.toString())
        }

        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        list.forEachIndexed { index, model ->
            val value: Double? = model.totalAmountSales
            values.add(Entry((index).toFloat(), value?.toFloat()!!, null))
        }

        val yAxis: YAxis = binding.chart.axisLeft
        yAxis.axisMaximum = getMax(list).toFloat()

        binding.chart.invalidate()

        val set1: LineDataSet
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            set1 = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "Months")
            set1.setDrawIcons(false)

            // black lines and points
            set1.color = ContextCompat.getColor(
                requireContext().applicationContext, R.color.check_score_bg_first
            )
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 3f
            set1.circleRadius = 4f

            // draw points as solid circles
            set1.setDrawCircleHole(true)

            // text size of values
            set1.valueTextSize = 0f
            set1.valueTextColor =
                ContextCompat.getColor(requireContext().applicationContext, R.color.theme_purple)

            // set the filled area
            set1.setDrawFilled(false)

            set1.fillFormatter = IFillFormatter { _, _ -> binding.chart.axisLeft.axisMinimum }

            // set color of filled area

            // drawables only supported on api level 18 and above
            val drawable = getDrawable(requireActivity(), R.drawable.border_black)
            set1.fillDrawable = drawable

            val dataSets = java.util.ArrayList<ILineDataSet>()

            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            binding.chart.data = data
        }
    }

    private fun getMax(list: MutableList<OrganizationWiseSalesDataItem>): Int {
        var max = Int.MIN_VALUE
        for (i in 0 until list.size) {
            if (list[i].totalAmountSales?.roundToInt()!! > max) {
                max = list[i].totalAmountSales?.toInt()!!
            }
        }

        max += max / 2
        return max
    }

    private fun actionOnService(action: Actions) {
        if (isStaffUser().not()) {
            return
        }

        if (getServiceState(requireContext()) == ServiceState.STOPPED && action == Actions.STOP) {
            return
        }

        Intent(requireContext(), EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(requireContext(), it)
                return
            }
            log("Starting the service in < 26 Mode")
            requireContext().startService(it)
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {

        organizationViewModel.profileLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == false) {
                    it.data?.let { model ->
                        if (isStaffUser()) {
                            updatePermissions(model.permissions)
                            if (updateMainDataListener != null) {
                                updateMainDataListener?.updateCompanyLogo(model)
                            }

                            if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.VIEW_ORDER_PERMISSION, false
                                )
                            ) {
                                binding.groupRecentOrderHd.visibility = View.VISIBLE
                            } else {
                                binding.groupRecentOrderHd.visibility = View.GONE
                            }

                            SharedPref.getInstance()
                                .putBoolean(AppConstant.STAFF_HIERARCHY, model.hierarchy)

                            SharedPref.getInstance().putString(AppConstant.USER_NAME, model.name)

                            if (model.name != null) {
                                binding.tvAttendance.text =
                                    resources.getString(R.string.hello, model.name)
                            } else {
                                binding.tvAttendance.hideView()
                            }

                        } else {
                            if (model.fullName != null) {
                                binding.tvAttendance.text =
                                    resources.getString(R.string.hello, model.fullName)
                            } else {
                                binding.tvAttendance.hideView()
                            }
                        }

                    }
                }
            }
        }

        dashboardViewModel.getDashboardLiveData().observe(viewLifecycleOwner) { data ->
            if (isAdded) {
                if (data.error == false) {
                    data.data?.let { data ->
                        inflateDashBoardData(data)
                    }
                } else {
                    if (data.errorCode == 403) {
                        logout()
                    } else {
                        showToast("${data.message}")
                    }
                }
            }
        }

        dashboardViewModel.getOrderLiveData().observe(viewLifecycleOwner) { data ->
            if (isAdded) {
                data.data?.let {
                    recentOrderList.clear()
                    if (it.isNotEmpty()) {
                        if (it.size > 3) {
                            recentOrderList.addAll(it.subList(0, 3))
                        } else {
                            recentOrderList.addAll(it)
                        }
                        recentOrderAdapter.notifyDataSetChanged()

                    } else {
                        binding.groupRecentOrderHd.visibility = View.GONE
                    }
                }
            }
        }

        dashboardViewModel.updateOrderStatusLiveData.observe(viewLifecycleOwner) { data ->
            if (isAdded) {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
                data?.let {
                    if (data.error == false) {
                        dashboardViewModel.getOrderData(
                            "",
                            fullFilledList,
                            "",
                            1,
                            null,
                            "",
                            "",
                            null,
                            null,
                            "",
                            null,
                            false,
                            "",
                            customerTypeList,
                            paymentOnList,
                            receivedOnList,
                            staffType,
                            "",
                            "",
                            hasInternetConnection()
                        )

                    }
                }
                enableTouch()
            }
        }

        dashboardViewModel.organizationWiseSalesLiveData.observe(requireActivity()) { data ->
            if (isAdded) {
                data.data?.let {
                    if (it.isNotEmpty()) {
                        if (isAdded) {
                            setData(it.asReversed())
                        }
                    }
                }
            }
        }

        activityViewModel.getAttendanceLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == false) {
                    it.data?.let { model ->
                        if (model.attendanceType == AppConstant.ACTIVITY_TYPE_MARK_LEAVE) {
                            SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)
                            makeViewForLeave()
                        } else if (model.checkoutTime.isNullOrEmpty().not()) {
                            SharedPref.getInstance().putLong(
                                AppConstant.START_DAY_TIME,
                                DateFormatHelper.convertStringTimeToLong(model.checkinTime)
                            )
                            SharedPref.getInstance().putLong(
                                AppConstant.END_DAY_TIME,
                                DateFormatHelper.convertStringTimeToLong(model.checkoutTime)
                            )
                            SharedPref.getInstance()
                                .putBoolean(AppConstant.SHARE_LOCATION_DATA_FOR_FIRST_TIME, false)
                            makeViewForDayEnded()
                            SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)

                            actionOnService(Actions.STOP)
                            SharedPref.getInstance().putModelClass(
                                AppConstant.SAVE_ATTENDANCE_PREF, SaveAttendanceModel(
                                    date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                    checkIn = null,
                                    checkOut = true
                                )
                            )

                            SharedPref.getInstance()
                                .putBoolean(AppConstant.FAKE_LOCATION_UPDATE_SEND, false)

                        } else if (model.checkinTime.isNullOrEmpty().not()) {
                            SharedPref.getInstance().putLong(
                                AppConstant.START_DAY_TIME,
                                DateFormatHelper.convertStringTimeToLong(model.checkinTime)
                            )
                            makeViewForDayHasBeenStarted()
                            SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)

                            if (isStaffUser() && SharedPref.getInstance()
                                    .getBoolean(AppConstant.LIVE_LOCATION, false)
                            ) {
                                if (isTimeLessThanTo8PM() && isGpsOn() && locationPermissionUtils.hasPermission() && locationPermissionUtils.hasBackgroundLocationPermission()) {
                                    actionOnService(Actions.START)
                                } else {
                                    actionOnService(Actions.STOP)
                                }
                            }

                            SharedPref.getInstance().putModelClass(
                                AppConstant.SAVE_ATTENDANCE_PREF, SaveAttendanceModel(
                                    date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                    checkIn = true,
                                    checkOut = null
                                )
                            )
                        } else {
                            SharedPref.getInstance().putLong(AppConstant.START_DAY_TIME, 0)
                            SharedPref.getInstance().putLong(AppConstant.END_DAY_TIME, 0)
                            SharedPref.getInstance()
                                .putBoolean(AppConstant.SHARE_LOCATION_DATA_FOR_FIRST_TIME, false)
                            SharedPref.getInstance()
                                .putBoolean(AppConstant.FAKE_LOCATION_UPDATE_SEND, false)
                            makeViewForStartTheDay()
                            if (isStaffUser()) {
                                SharedPref.getInstance().putBoolean(AppConstant.START_DAY, false)
                                if (geoLocationLat != 0.0 && geoLocationLong != 0.0 && isStartDayDialogShow.not()) {
                                    showStartDayDialog()
                                }
                            } else {
                                SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)
                            }

                            actionOnService(Actions.STOP)

                            SharedPref.getInstance().putModelClass(
                                AppConstant.SAVE_ATTENDANCE_PREF, SaveAttendanceModel(
                                    date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                    checkIn = null,
                                    checkOut = null
                                )
                            )
                        }

                        isProfileDataAvailable = true
                    }
                } else {
                    showToast(it.message)
                }
            }
        }

        moreViewModel.preferenceLiveData.observe(requireActivity()) { response ->
            if (isAdded) {
                if (response.data != null) {

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.STAFF_AND_CUSTOMER_MAPPING,
                        response.data.staffCustomerMapping ?: true
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.GALLERY_UPLOAD_PIC_ENABLE,
                        response.data.disableGalleryPhoto ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.ANALYTICS_CALCULATION_ENABLE,
                        response.data.enableAnalyticsCalculation ?: true
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.ENABLE_CUSTOMER_CATEGORY_MAPPING,
                        response.data.enableCustomerCategoryMapping ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        AppConstant.CHECK_IN, response.data.activityCheckInRequired ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        AppConstant.TELEPHONIC_ORDER,
                        response.data.activityAllowTelephonicOrder ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        CUSTOMER_LEVEL_ORDER, response.data.enableCustomerLevelOrder ?: false
                    )


                    SharedPref.getInstance().putBoolean(
                        AppConstant.CHECK_IMAGE_REQUIRED,
                        response.data.activityCheckInImageRequired ?: false
                    )
                    SharedPref.getInstance().putBoolean(
                        AppConstant.CHECK_IMAGE_INPUT,
                        response.data.activityCheckInShowImageInput ?: false
                    )

                    if (isStaffUser()) {
                        SharedPref.getInstance().putBoolean(
                            AppConstant.GEO_FENCING_ENABLE,
                            response.data.activityGeoFencing ?: false
                        )

                    } else {
                        SharedPref.getInstance().putBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                    }

                    SharedPref.getInstance().putBoolean(
                        AppConstant.ENABLE_ORG_OFFLINE_MODE, response.data.allowOfflineMode ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.CUSTOMER_CATEGORY_MAPPING_ENABLE,
                        response.data.enableCustomerCategoryMapping ?: true
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.ENABLE_CUSTOMER_LEVEL_ORDER,
                        response.data.enableCustomerLevelOrder ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        SharePrefConstant.DISABLE_SCREENSHOT_ON_PRODUCTS,
                        response.data.blockScreenshotsInProducts ?: false
                    )

                    SharedPref.getInstance().putBoolean(
                        AppConstant.LIVE_LOCATION, response.data.liveLocationTracking ?: false
                    )

                    if (response.data.enableAnalyticsCalculation == true) {

                        if (isStaffUser()) {
                            binding.clChart.visibility = View.GONE
                        } else if (SharedPref.getInstance()
                                .getBoolean(SharePrefConstant.ANALYTICS_CALCULATION_ENABLE, false)
                        ) {
                            binding.clChart.visibility = View.VISIBLE
                            initLineChart()
                        } else {
                            binding.clChart.visibility = View.GONE
                        }
                    } else {
                        binding.clChart.visibility = View.GONE
                    }

                    response.data.customerLevelConfig?.let { config ->
                        SharedPref.getInstance().putString(
                            AppConstant.CUSTOMER_LEVEL_1,
                            config.LEVEL_1 ?: AppConstant.CUSTOMER_LEVEL_1
                        )
                        SharedPref.getInstance().putString(
                            AppConstant.CUSTOMER_LEVEL_2,
                            config.LEVEL_2 ?: AppConstant.CUSTOMER_LEVEL_2
                        )
                        SharedPref.getInstance().putString(
                            AppConstant.CUSTOMER_LEVEL_3,
                            config.LEVEL_3 ?: AppConstant.CUSTOMER_LEVEL_3
                        )
                    }
                } else {
                    if (response.errorCode == 403) {
                        logout()
                    } else {
                        showToast("${response.message}")
                    }
                }

                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (locationPermissionUtils.hasPermission()) {
                        if (isStaffUser() && response.data?.liveLocationTracking == true
                            && locationPermissionUtils.hasBackgroundLocationPermission().not()
                        ) {
                            showEnablePermissionDialog(true)

                            val deviceModel = DeviceActivityListItem()
                            deviceModel.locationPermissionType =
                                AppConstant.LocationPermissionType.FOREGROUND

                            sendDeviceLogs(deviceModel)
                            SharedPref.getInstance().putBoolean(
                                AppConstant.SHARE_LOCATION_PERMISSION_CHANGE_TO_API,
                                true
                            )
                        } else if (locationPermissionDialog != null && locationPermissionDialog!!.isShowing) {
                            locationPermissionDialog?.dismiss()
                        }
                    } else {
                        showEnablePermissionDialog(
                            response.data?.liveLocationTracking ?: false
                        )

                        val deviceModel = DeviceActivityListItem()
                        deviceModel.locationPermissionType =
                            AppConstant.LocationPermissionType.NOT_PROVIDED

                        sendDeviceLogs(deviceModel)
                        SharedPref.getInstance()
                            .putBoolean(AppConstant.SHARE_LOCATION_PERMISSION_CHANGE_TO_API, true)
                    }
                }
            }
        }

        staffViewModel.staffCurrentlyActiveTargetsLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == false) {
                    targetList.clear()
                    currentlyActiveTargets = it.data

                    if (it.data?.targetSalesAmount != null) {
                        binding.clTargets.visibility = View.VISIBLE

                        it.data.let { data ->

                            binding.tvTargetDate.text =
                                "${DateFormatHelper.convertStringToMonthFormat(data.startDate)} - ${
                                    DateFormatHelper.convertStringToMonthFormat(data.endDate)
                                }"
                            if (data.targetSalesAmount != null && data.targetSalesAmount != 0.0) {
                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_SALES,
                                        data.targetSalesAmount,
                                        data.currentSalesAmount,
                                        R.drawable.ic_target_sales
                                    )
                                )
                            }

                            if (data.targetPaymentCollection != null && data.targetPaymentCollection != 0.0) {
                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_COLLECTION,
                                        data.targetPaymentCollection,
                                        data.currentPaymentCollection,
                                        R.drawable.ic_target_collection
                                    )
                                )
                            }
                            if (data.targetNewLeads != null && data.targetNewLeads != 0) {
                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_LEADS,
                                        data.targetNewLeads.toDouble(),
                                        data.currentNewLeads?.toDouble(),
                                        R.drawable.ic_target_leads
                                    )
                                )
                            }
                            if (data.targetNewCustomers != null && data.targetNewCustomers != 0) {
                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_CUSTOMER,
                                        data.targetNewCustomers.toDouble(),
                                        data.currentNewCustomers?.toDouble(),
                                        R.drawable.ic_target_customer
                                    )
                                )
                            }
                            if (data.targetCustomerVisits != null && data.targetCustomerVisits != 0) {
                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_VISITS,
                                        data.targetCustomerVisits.toDouble(),
                                        data.currentCustomerVisits?.toDouble(),
                                        R.drawable.ic_target_visits
                                    )
                                )
                            }
                            if (data.productMetrics.isNullOrEmpty().not()) {
                                var percent = 0.0
                                var counter = 0
                                data.productMetrics?.forEach { metrics ->
                                    if (metrics.currentValue != 0.0) {
                                        val perProductPercent =
                                            ((metrics.currentValue!! / metrics.targetValue!!) * 100)
                                        if (perProductPercent >= 100) {
                                            counter++
                                        }
                                    }
                                }
                                if (counter > 0) {
                                    percent =
                                        (counter.toDouble() / data.productMetrics?.size!!.toDouble()) * 100
                                }

                                targetList.add(
                                    StaffTargetModel(
                                        AppConstant.TARGET_PRODUCTS,
                                        data.productMetrics?.size!!.toDouble(),
                                        percent,
                                        R.drawable.ic_target_products
                                    )
                                )
                            }

                            targetsListViewPagerAdapter =
                                TargetsListViewPagerAdapter(targetList, this)
                            binding.vpTargets.adapter = targetsListViewPagerAdapter
                        }
                    } else {
                        binding.clTargets.visibility = View.GONE
                    }
                }
            }
        }

        beatViewModel.currentlyActiveBeatLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == false) {
                    if (it.data?.beatRouteInfo != null) {
                        binding.clBeatPlanLayout.visibility = View.VISIBLE
                        initBeatPlan(it.data)
                    } else {
                        binding.clBeatPlanLayout.visibility = View.GONE
                    }
                } else {
                    binding.clBeatPlanLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun makeViewForLeave() {
        binding.clStartDay.visibility = View.GONE
        binding.clEndDay.visibility = View.GONE
        binding.clTimeDuration.visibility = View.GONE
        binding.tvOnALeave.visibility = View.VISIBLE
    }

    private fun inflateDashBoardData(it: DashboardData) {
        binding.tvProductCount.text = it.products.toString()
        binding.tvCustomerCount.text = it.customers.toString()
        binding.tvOrdersAmount.text = it.orders.toString()
        binding.tvStaffCount.text = it.staff.toString()
        binding.tvBeatCount.text = "${it.beat ?: 0}"
        binding.tvLeadCount.text = it.leadCount.toString()
        binding.tvPaymentCount.text = it.payment.toString()
    }

    private fun requestLocationPermission() {
        activityResultLauncherForLocation.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val activityResultLauncherForLocation =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isGranted = false

            permissions.entries.forEach {
                isGranted = it.value
            }

            if (isGranted) {
                if (isStaffUser() && SharedPref.getInstance().getBoolean(
                        AppConstant.LIVE_LOCATION, false
                    ) && locationPermissionUtils.hasBackgroundLocationPermission().not()
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        activityResultLauncherForBackgroundLocation.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            )
                        )
                    } else {
                        setUpdatedLocationListener()
                    }
                } else {
                    setUpdatedLocationListener()
                }
            }
        }

    private val activityResultLauncherForBackgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isGranted = false

            permissions.entries.forEach {
                isGranted = it.value
            }

            if (isGranted) {
                setUpdatedLocationListener()
            } else {
                if (locationPermissionDialog != null && locationPermissionDialog!!.isShowing.not()) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }

    private fun showStartDayDialog() {
        isStartDayDialogShow = true
        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
            this, geoLocationLat, geoLocationLong
        )
        fragment.show(
            childFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name
        )
    }

    private fun addScheduleWorkerForStopService() {
        val componentName = ComponentName(requireContext(), StopLiveTrackingJobService::class.java)
        val builder = JobInfo.Builder(0, componentName)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, AppConstant.LIVE_LOCATION_END_TIME)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val triggerTime = cal.timeInMillis

        builder.setPersisted(true)
        builder.setMinimumLatency(triggerTime - System.currentTimeMillis())
        builder.setOverrideDeadline(triggerTime - System.currentTimeMillis() + 1000) // 1 second window

        val jobScheduler = requireActivity().getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())

    }

    private fun addScheduleWorkerForEndDay() {
        val componentName = ComponentName(requireContext(), MarkEndDayJobService::class.java)
        val builder = JobInfo.Builder(0, componentName)

        val cal = Calendar.getInstance()

        //Need to logout 12 PM midnight if user didn't logout or end day
        cal.set(Calendar.HOUR_OF_DAY, AppConstant.MARK_END_DAY_TIME)
        cal.set(Calendar.MINUTE, 58) // 2 minutes buffer time to logout
        cal.set(Calendar.SECOND, 0)

        val triggerTime = cal.timeInMillis

        builder.setPersisted(true)
        builder.setMinimumLatency(triggerTime - System.currentTimeMillis())
        builder.setOverrideDeadline(triggerTime - System.currentTimeMillis() + 1000) // 1 second window

        val jobScheduler = requireActivity().getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }


    @SuppressLint("SetTextI18n")
    private fun initBeatPlan(model: ActiveBeatRouteInfoAndDayListModel?) {
        var comments: String? = null
        model?.beatRouteInfo?.let { info ->
            if (info.name.isNullOrEmpty().not()) {
                binding.beatLayout.tvTitle.text = info.name
            }

            if (info.startDate.isNullOrEmpty().not() && info.endDate.isNullOrEmpty().not()) {
                binding.tvBeatPlanDate.text =
                    "${DateFormatHelper.convertStringToMonthFormat(info.startDate)} - ${
                        DateFormatHelper.convertStringToMonthFormat(info.endDate)
                    }"
            }

            comments = info.comments
        }

        if (model?.beatRouteDayPlan != null) {

            model.beatRouteDayPlan?.let { day ->
                beatRoutePlanId = day.beatrouteplan!!
                beatDate = day.date!!


                binding.beatLayout.tvTitle.text = day.beatName
                binding.beatLayout.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                    day.date, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )

                binding.beatLayout.tvBeatPlanDurationDate.text = "${
                    DateFormatHelper.convertStringToCustomDateFormat(
                        model.beatRouteInfo?.startDate,
                        SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                    )
                } - ${
                    DateFormatHelper.convertStringToCustomDateFormat(
                        model.beatRouteInfo?.endDate, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                    )
                }"

                binding.beatLayout.tvBeatPlanName.text = resources.getString(
                    R.string.beat_plan_name_with_underline, model.beatRouteInfo?.name
                )

                binding.beatLayout.tvBeatPlanName.paintFlags =
                    binding.beatLayout.tvBeatPlanName.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                if (day.isCancelled == true) {
                    binding.beatLayout.groupCustomerCount.visibility = View.GONE
                    binding.beatLayout.groupHeading.visibility = View.GONE
                    binding.beatLayout.groupNightStay.visibility = View.GONE
                    binding.beatLayout.groupNewLeadCount.visibility = View.GONE

                    binding.beatLayout.tvCancel.visibility = View.VISIBLE
                    binding.beatLayout.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext().applicationContext,
                            R.color.sales_text_color_light_black
                        )
                    )
                    binding.beatLayout.tvTitle.paintFlags =
                        binding.beatLayout.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                } else if (day.moduleType.equals(AppConstant.HOLIDAY)) {
                    binding.beatLayout.groupCustomerCount.visibility = View.GONE
                    binding.beatLayout.groupHeading.visibility = View.GONE
                    binding.beatLayout.groupNightStay.visibility = View.GONE
                    binding.beatLayout.groupNewLeadCount.visibility = View.GONE
                    binding.beatLayout.tvTitle.text = resources.getString(R.string.leave)
                    binding.beatLayout.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext().applicationContext, R.color.holiday_beat_color
                        )
                    )

                    if (day.beatId != null) {
                        beatId = day.beatId!!
                    }

                } else {
                    var counter = 0
                    if (day.targetCustomersCount != null && day.targetCustomersCount != 0) {
                        counter++
                        binding.beatLayout.tvCustomerCountForBeat.text =
                            "${day.targetCustomersCount}"
                        binding.beatLayout.tvVisitedCount.text =
                            "${day.achievedCustomersCount ?: 0}"
                        binding.beatLayout.groupCustomerCount.visibility = View.VISIBLE
                    } else {
                        binding.beatLayout.groupCustomerCount.visibility = View.GONE
                    }

                    if (day.targetLeadsCount != null && day.targetLeadsCount != 0) {
                        counter++
                        binding.beatLayout.tvNewLeadCountForBeat.text = "${day.targetLeadsCount}"
                        binding.beatLayout.tvNewLeadVisitedCount.text =
                            "${day.achievedLeadsCount ?: 0}"
                        binding.beatLayout.groupNewLeadCount.visibility = View.VISIBLE
                    } else {
                        binding.beatLayout.groupNewLeadCount.visibility = View.GONE
                    }

                    if (counter > 0) {
                        binding.beatLayout.groupHeading.visibility = View.VISIBLE
                    }

                    if (comments.isNullOrEmpty().not()) {
                        binding.beatLayout.clNotes.visibility = View.VISIBLE

                        binding.beatLayout.tvNotes.text = comments
                        binding.beatLayout.tvNotes.maxLines = 2
                        binding.beatLayout.tvNotes.ellipsize = TextUtils.TruncateAt.END

                        if (SharedPref.getInstance()
                                .getBoolean(AppConstant.BEAT_PLAN_NOTES_READ, false)
                        ) {
                            binding.beatLayout.ivNotesNotification.visibility = View.GONE
                        } else {
                            binding.beatLayout.ivNotesNotification.visibility = View.VISIBLE
                        }

                        var rotationAngle = 0

                        binding.beatLayout.clNotes.setOnClickListener {
                            rotationAngle = if (rotationAngle == 0) 180 else 0
                            binding.beatLayout.ivDropDown.animate()
                                .rotation(rotationAngle.toFloat()).setDuration(300).start()

                            if (binding.beatLayout.tvNotes.isVisible) {
                                binding.beatLayout.tvNotes.visibility = View.GONE

                                binding.beatLayout.ivNotesNotification.visibility = View.GONE
                            } else {
                                SharedPref.getInstance()
                                    .putBoolean(AppConstant.BEAT_PLAN_NOTES_READ, true)
                                binding.beatLayout.tvNotes.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

    }

    private fun updatePermissions(permissions: List<String>?) {
        if (permissions.isNullOrEmpty().not()) {
            loginViewModel.savePermissions(permissions!!)
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentOrder.layoutManager = linearLayoutManager
        recentOrderAdapter = SalesRecentNewOrderAdapter(
            recentOrderList,
            requireContext(),
            this,
            hasInternetConnection(),
            requireActivity().supportFragmentManager
        )
        binding.rvRecentOrder.adapter = recentOrderAdapter
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
                                requireContext(), CreateShipmentOrderActivity::class.java
                            ).putExtra(AppConstant.ORDER_ID, model.id)
                        )
                    }

                    else -> {
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

    private fun openRejectOrderBottomSheet(model: OrderData) {
        val fragment = OrderRejectedBottomSheetDialogFragment(model, this)
        fragment.show(childFragmentManager, AppConstant.ORDER_REJECTED)
    }

    override fun onGetOrderInfo(model: OrderData, position: Int) {
        getOrderInfoActivityResult.launch(
            Intent(context, OrderDetailActivity::class.java).putExtra(
                AppConstant.ORDER_ID, model.id
            )
        )
    }

    override fun onDeleteOrder(model: OrderData, position: Int) {
        DeleteDialog.showDeleteDialog(
            requireActivity(),
            model.id,
            position,
            resources.getString(R.string.delete_order),
            resources.getString(R.string.delete_order_message),
            this
        )
    }

    override fun getStoreFrontInfo() {
        val fragment = InfoBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_order))
        bundle.putString(
            AppConstant.MESSAGE, resources.getString(R.string.storefront_order_message)
        )
        fragment.arguments = bundle
        fragment.show(childFragmentManager, AppConstant.ORDER_REJECTED)
    }


    @SuppressLint("SetTextI18n")
    var getOrderInfoActivityResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                dashboardViewModel.getOrderData(
                    "",
                    fullFilledList,
                    "",

                    1,
                    null,
                    "",
                    "",
                    null,
                    null,
                    "",
                    null,
                    false,
                    "",
                    customerTypeList,
                    paymentOnList,
                    receivedOnList,
                    staffType,
                    sortingBY,
                    sortingOrder,
                    hasInternetConnection()
                )

            }
        }

    var someActivityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                dashboardViewModel.getDashboardData(hasInternetConnection())
            }
        }

    private var getOrderListResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                dashboardViewModel.getDashboardData(hasInternetConnection())
                dashboardViewModel.getOrderData(
                    "",
                    fullFilledList,
                    "",
                    1,
                    null,
                    "",
                    "",
                    null,
                    null,
                    "",
                    null,
                    false,
                    "",
                    customerTypeList,
                    paymentOnList,
                    receivedOnList,
                    staffType,
                    "",
                    "",
                    hasInternetConnection()
                )
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


    override fun onValueSelected(e: Entry?, h: Highlight?) {}

    override fun onNothingSelected() {}

    private fun checkForAttendance() {
        val attendance: SaveAttendanceModel? = Gson().fromJson(
            SharedPref.getInstance().getString(AppConstant.SAVE_ATTENDANCE_PREF)
        )
        if (attendance != null) {
            if (DateFormatHelper.isDate1EqualThenDate2(
                    attendance.date, DateFormatHelper.convertDateToCustomDateFormat(
                        Calendar.getInstance().time, SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    )
                )
            ) {
                if (attendance.attendanceType == AppConstant.ACTIVITY_TYPE_MARK_LEAVE) {
                    makeViewForLeave()
                } else if (attendance.checkOut != null) {
                    makeViewForDayEnded()
                } else if (attendance.checkIn != null) {
                    makeViewForDayHasBeenStarted()
                } else {
                    makeViewForStartTheDay()
                }
            } else {
                SharedPref.getInstance().putBoolean(AppConstant.START_DAY, false)
                SharedPref.getInstance().putModelClass(
                    AppConstant.SAVE_ATTENDANCE_PREF, SaveAttendanceModel(
                        date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                        checkIn = null,
                        checkOut = null,
                        attendanceType = null
                    )
                )
                makeViewForStartTheDay()
            }
        } else {
            makeViewForStartTheDay()
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            setUpdatedLocationListener()
        }
    }

    private val pushNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            if (locationPermissionUtils.hasPermission()) {
                setUpdatedLocationListener()
            }
        }

    interface UpdateMainDataListener {
        fun updateCompanyLogo(model: UserInfoData)
    }

    override fun getTargetDetails() {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.TARGET_SALES, true
            )
        )
    }

    override fun getTargetProductDetails() {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.TARGET_PRODUCTS, true
            ).putExtra(AppConstant.TARGET_PRODUCTS_LIST, currentlyActiveTargets)
        )
    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                log("gps provide status change")
                if (isGpsOn()) {
                    if (MyApplication.instance.getActionPerformedForGpsInServiceValue().not()) {
                        organizationViewModel.getProfileInfo()
                        val deviceModel = DeviceActivityListItem()
                        deviceModel.locationPermission = true

                        sendDeviceLogs(deviceModel)

                        MyApplication.instance.setPerformedForGpsInServiceValue(true)
                    }
                }
            }
        }
    }

    private fun sendDeviceLogs(deviceModel: DeviceActivityListItem) {
        deviceModel.deviceInformation = requireContext().getDeviceInformation()
        deviceModel.batteryPercent = requireContext().getBatteryInformation().first
        deviceModel.isSystemPowerSaving = requireContext().isBatteryOptimizationEnabled()
        deviceModel.isAppPowerSaving = requireContext().isMyAppIsBatteryOptimizationMode()
        deviceModel.locationPermission = requireContext().isGpsEnabled()

        deviceModel.activityTimeStamp =
            DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)

        moreViewModel.sendDeviceLogs(
            deviceModel, hasInternetConnection(), isApiRequired = true
        )
    }

    override fun onSuccessfullyMarkAttendance() {
        if (hasInternetConnection()) {
            activityViewModel.getAttendance(hasInternetConnection())
        } else {
            checkForAttendance()
        }
    }

    private fun makeViewForStartTheDay() {
        binding.groupStatDayHeading.visibility = View.VISIBLE
        binding.groupStatDayInitialised.visibility = View.GONE
        binding.clStartDay.setBackgroundResource(R.drawable.gray_rectangle_white_stoke_bg)

        startTimeHandler.removeCallbacks(updateTimer)

        binding.tvTotalDurationTime.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext, R.color.leve_text_color
            )
        )
        binding.tvTotalDurationTime.text =
            resources.getString(R.string.empty_duration_for_total_time)

        binding.groupDayEnded.visibility = View.GONE
        binding.ivEndDay.visibility = View.GONE
        binding.tvEndDayHeading.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext, R.color.leve_text_color
            )
        )
        binding.clEndDay.setBackgroundResource(R.drawable.white_border_rectangle_gradient)
    }

    private fun makeViewForDayHasBeenStarted() {
        startTime = SharedPref.getInstance().getLong(AppConstant.START_DAY_TIME)

        binding.groupStatDayHeading.visibility = View.GONE
        binding.groupStatDayInitialised.visibility = View.VISIBLE
        binding.clStartDay.setBackgroundResource(R.drawable.white_border_rectangle_gradient)

        binding.tvDayStartedTime.text = DateFormatHelper.convertDateToCustomDateFormat(
            Date(startTime), SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        )

        startTimeHandler.postDelayed(
            updateTimer, 1000 - (Calendar.getInstance().timeInMillis % 1000)
        )

        binding.tvTotalDurationTime.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext, R.color.theme_purple
            )
        )
        binding.ivEndDay.visibility = View.VISIBLE
        binding.tvEndDayHeading.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext, R.color.theme_purple
            )
        )
        binding.clEndDay.setBackgroundResource(R.drawable.gray_rectangle_white_stoke_bg)
    }

    private fun makeViewForDayEnded() {
        val startTime = SharedPref.getInstance().getLong(AppConstant.START_DAY_TIME)
        val endTime = SharedPref.getInstance().getLong(AppConstant.END_DAY_TIME)

        binding.groupStatDayHeading.visibility = View.GONE
        binding.groupStatDayInitialised.visibility = View.VISIBLE
        binding.clStartDay.setBackgroundResource(R.drawable.white_border_rectangle_gradient)

        binding.tvDayStartedTime.text = DateFormatHelper.convertDateToCustomDateFormat(
            Date(startTime), SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        )

        binding.tvDayEndedTime.text = DateFormatHelper.convertDateToCustomDateFormat(
            Date(endTime), SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        )

        binding.tvTotalDurationTime.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext, R.color.achieve_green
            )
        )

        startTimeHandler.removeCallbacks(updateTimer)

        binding.tvTotalDurationTime.text = getTimeDifference(startTime, endTime)

        binding.ivEndDay.visibility = View.GONE
        binding.tvEndDayHeading.visibility = View.GONE

        binding.groupDayEnded.visibility = View.VISIBLE

        binding.clEndDay.setBackgroundResource(R.drawable.white_border_rectangle_gradient)
    }

    private val updateTimer = object : Runnable {
        override fun run() {
            val timeFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
            val utc = TimeZone.getTimeZone("Asia/Kolkata")
            timeFormat.timeZone = utc

            val elapsedTime = Calendar.getInstance().timeInMillis - startTime

            binding.tvTotalDurationTime.text = elapsedTime.toHoursMinutesSeconds()

            startTimeHandler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun requiredBackgroundLocationFromPopUp() {
        showEnablePermissionDialog(true)
    }

    override fun onResume() {
        super.onResume()
        if (hasInternetConnection()) {
            moreViewModel.getPreferencesInfo()
            organizationViewModel.getProfileInfo()
            activityViewModel.getAttendance(hasInternetConnection())
        } else {
            checkForAttendance()
        }

        checkLocationPermission()
    }

    override fun onDelete(model: Any, position: Any) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("is_archived", true)
        dashboardViewModel.deleteOrder(jsonObject, model as Int, hasInternetConnection())

    }

}