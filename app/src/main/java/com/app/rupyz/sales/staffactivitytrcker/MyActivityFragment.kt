package com.app.rupyz.sales.staffactivitytrcker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentMyActivityBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.ActivityMapPointsModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import com.app.rupyz.model_kt.WayPointsModel
import com.app.rupyz.sales.attendance.StartDayEndDayDetailsActivity
import com.app.rupyz.sales.customer.CustomerActivityAdapter
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.map.GoogleMapActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import com.google.android.gms.maps.model.LatLng
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.roundToInt

class MyActivityFragment : BaseFragment(),
        CustomerActivityAdapter.ICustomerFeedbackActionListener {
    private lateinit var binding: FragmentMyActivityBinding
    private lateinit var activityViewModel: StaffActivityViewModel
    private lateinit var customerActivityAdapter: CustomerActivityAdapter
    private var activityList = ArrayList<CustomerFollowUpDataItem>()

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var mapPointCount = 0
    private var mapPointCountForLive = 0
    private var stringBuilder: StringBuilder? = null
    private var mapPointsList: ArrayList<WayPointsModel> = ArrayList()
    private var liveLocationPoints: ArrayList<WayPointsModel> = ArrayList()

    private var liveLocationStringBuilder: StringBuilder? = null
    private var staffPolygonMapDots: String = ""
    private var liveLocationPolygonMapDots: String = ""
    private var filterDate: String = ""
    private var geoActivityType: String = ""

    private var staffId: Int = 0

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel = ViewModelProvider(this)[StaffActivityViewModel::class.java]

        arguments?.let {
            if (arguments?.get(AppConstant.DATE_FILTER) != null) {
                filterDate = arguments?.getString(AppConstant.DATE_FILTER)!!
            }

            if (arguments?.getInt(AppConstant.STAFF_ID) != null) {
                staffId = arguments?.getInt(AppConstant.STAFF_ID) ?: 0
            }
        }

        geoActivityType = AppConstant.GEO_ACTIVITY_TYPE_ACTIVITY_MAP

        myCalendar[year, month] = 1
        myCalendar.time = Calendar.getInstance().time

        initRecyclerView()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE

        mapPointCount = 0
        activityList.clear()
        customerActivityAdapter.notifyDataSetChanged()

        getTrackingActivity()

        if (filterDate.isNotEmpty()) {
            myCalendar.time = DateFormatHelper.convertStringToDate(filterDate)
            binding.tvDate.text = DateFormatHelper.getMonthDate(filterDate)

            if (DateFormatHelper.isDate1EqualThenDate2(
                            DateFormatHelper.convertDateToIsoFormat(
                                    myCalendar.time
                            ), DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time)
                    )
            ) {
                binding.ivIncrementMonth.visibility = View.GONE
            } else {
                binding.ivIncrementMonth.visibility = View.VISIBLE
            }

        } else {
            binding.clTrackingInfo.tvStaffName.visibility = View.INVISIBLE
            binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(cal.time)
        }

        mStartDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateStartDate()
        }

        binding.tvDate.setOnClickListener {
            openStartDateCalendar()
        }

        binding.ivDecrementMonth.setOnClickListener {
            currentPage = 1
            myCalendar.add(Calendar.DAY_OF_MONTH, -1)
            binding.tvDate.text =
                    DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)
            updateStartDate()
        }

        binding.ivIncrementMonth.setOnClickListener {
            currentPage = 1
            myCalendar.add(Calendar.DAY_OF_MONTH, 1)
            binding.tvDate.text =
                    DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)
            updateStartDate()
        }

        if (isStaffUser().not() || staffId == 0) {
            binding.clTrackingInfo.ivDropArrow.rotation = 0F
        }

        binding.clTrackingInfo.clMapView.setOnClickListener {
            trackingInfoAction()
        }

        binding.clTrackingInfo.clDailySalesReport.setOnClickListener {
            startActivity(
                    Intent(requireContext(), DailySalesReportActivity::class.java).putExtra(
                            AppConstant.DATE, filterDate
                    ).putExtra(AppConstant.STAFF_ID, staffId)
            )
        }

        binding.clTrackingInfo.ivDropArrow.setOnClickListener {
            if (isStaffUser() || staffId != 0) {
                val popup = PopupMenu(requireContext(), binding.clTrackingInfo.ivDropArrow)

                try {
                    val fields: Array<Field> = popup.javaClass.declaredFields
                    for (field in fields) {
                        if ("mPopup" == field.name) {
                            field.isAccessible = true
                            val menuPopupHelper: Any = field.get(popup)
                            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                            val setForceIcons: Method = classPopupHelper.getMethod(
                                    "setForceShowIcon",
                                    Boolean::class.javaPrimitiveType
                            )
                            setForceIcons.invoke(menuPopupHelper, true)
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                popup.inflate(R.menu.menu_live_location)

                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.menu_activity_map -> {
                            geoActivityType = AppConstant.GEO_ACTIVITY_TYPE_ACTIVITY_MAP
                            binding.clTrackingInfo.tvMap.text =
                                    resources.getString(R.string.activity_map)
                            binding.clTrackingInfo.ivMap.setImageResource(R.drawable.ic_location_blue)
                            trackingInfoAction()
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_live_location -> {
                            geoActivityType = AppConstant.GEO_ACTIVITY_TYPE_LIVE_LOCATION
                            binding.clTrackingInfo.tvMap.text =
                                    resources.getString(R.string.live_location)
                            binding.clTrackingInfo.ivMap.setImageResource(R.drawable.ic_live_location)

                            startDialog("collecting data..")
                            getLiveLocationData()

                            return@setOnMenuItemClickListener false
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popup.show()
            } else {
                trackingInfoAction()
            }
        }

        binding.clTrackingInfo.clDistanceTravelled.setOnClickListener {
            val fragment = InfoBottomSheetDialogFragment()
            val bundle = Bundle()
            bundle.putString(AppConstant.HEADING, resources.getString(R.string.distance_calculation))
            bundle.putString(AppConstant.MESSAGE, resources.getString(R.string.distance_calculation_message))
            fragment.arguments = bundle
            fragment.show(childFragmentManager, InfoBottomSheetDialogFragment::class.java.name)
        }
    }

    private fun trackingInfoAction() {
        if (geoActivityType == AppConstant.GEO_ACTIVITY_TYPE_ACTIVITY_MAP) {
            val activityMapPointsModel = ActivityMapPointsModel(mapPointsList)
            startActivity(
                    Intent(requireContext(), GoogleMapActivity::class.java).putExtra(
                            AppConstant.LOCATION,
                            activityMapPointsModel
                    ).putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.MY_ACTIVITY)
            )
        } else {
            startDialog("collecting data..")
            getLiveLocationData()
        }
    }

    private fun getTrackingActivity() {
        activityViewModel.getStaffTrackingDetails(filterDate, staffId, currentPage)
    }

    private fun openStartDateCalendar() {
        val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.ThemeOverlay_Material_Dialog,
                mStartDateSetListener,
                year,
                month,
                day
        )
        dialog.updateDate(year, month, day)
        dialog.datePicker.maxDate = cal.time.time
        dialog.show()
    }

    private fun updateStartDate() {
        filterDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)

        staffPolygonMapDots = ""
        liveLocationPolygonMapDots = ""

        geoActivityType = AppConstant.GEO_ACTIVITY_TYPE_ACTIVITY_MAP

        binding.clTrackingInfo.clMapView.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.clEmptyData.visibility = View.GONE

        mapPointCount = 0
        activityList.clear()
        customerActivityAdapter.notifyDataSetChanged()

        // reset the api last page for change date and refreshing the api
        isApiLastPage = false

        getTrackingActivity()

        disableTouch()

        if (DateFormatHelper.isDate1EqualThenDate2(
                        DateFormatHelper.convertDateToIsoFormat(
                                myCalendar.time
                        ), DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time)
                )
        ) {
            binding.ivIncrementMonth.visibility = View.GONE
        } else {
            binding.ivIncrementMonth.visibility = View.VISIBLE
        }
    }

    private fun initObservers() {
        activityViewModel.staffTrackingDetailsLiveData.observe(requireActivity()) {
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                enableTouch()
                if (it.error == false) {
                    it.data?.let { data ->
                        isPageLoading = false
                        binding.clEmptyData.visibility = View.GONE

                        initDetailData(data.activityModules)

                        if (data.activityList.isNullOrEmpty().not()) {
                            binding.clTrackingInfo.clMapView.visibility = View.VISIBLE
                            activityList.addAll(data.activityList!!)

                            customerActivityAdapter.notifyDataSetChanged()

                            if (data.activityList.size < 30) {
                                isApiLastPage = true
                            }
                        } else {
                            binding.clTrackingInfo.clMapView.visibility = View.INVISIBLE
                            if (currentPage == 1) {
                                isApiLastPage = true
                                initiateEmptyData()
                            }
                        }

                        mapPointsList = ArrayList()
                        activityList.asReversed().forEachIndexed { _, activity ->

                            if (activity.geoLocationLat != 0.0 && activity.geoLocationLong != 0.0) {
                                mapPointCount++
                                val latLng = LatLng(
                                        activity.geoLocationLat ?: 0.0, activity.geoLocationLong
                                        ?: 0.00
                                )
                                mapPointsList.add(
                                        WayPointsModel(
                                                getActivityLabelForMarker(activity) ?: "", latLng
                                        )
                                )
                            }
                        }

                        if (mapPointCount == 0) {
                            binding.clTrackingInfo.clMapView.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    if (it.errorCode == 403) {
                        logout()
                    } else {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        activityViewModel.liveLocationData.observe(viewLifecycleOwner) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    liveLocationPoints = ArrayList()

                    it.data?.forEach { dataModel ->
                        if (dataModel.latitude != 0.0 && dataModel.longitude != 0.0) {
                            val latLng = LatLng(
                                    dataModel.latitude ?: 0.0,
                                    dataModel.longitude ?: 0.00
                            )
                            liveLocationPoints.add(WayPointsModel("$mapPointCount", latLng))
                        }
                    }

                    stopDialog()

                    startGoogleMapForLiveLocation()

                } else {
                    stopDialog()
                    showToast("No Live Activity Found For This Date")
                }
            } else {
                stopDialog()
                showToast("No Live Activity Found For This Date")
            }
        }
    }

    private fun getActivityLabelForMarker(model: CustomerFollowUpDataItem): String {
        var label = ""
        if (model.moduleType == AppConstant.ATTENDANCE) {

            if (model.action == AppConstant.ATTENDANCE_CHECK_IN) {
                label = resources.getString(R.string.start_day)
            } else if (model.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                label = resources.getString(R.string.end_day)
            }
        } else {

            var customerAndBusinessName = ""
            if (model.businessName.isNullOrEmpty().not()) {
                customerAndBusinessName = model.businessName!!
                label = "${model.businessName} - ${model.moduleType}"
            } else if (model.customerName.isNullOrEmpty().not()) {
                customerAndBusinessName = model.customerName!!
                label = "${model.customerName} - ${model.moduleType}"
            }

            when (model.moduleType) {
                AppConstant.CUSTOMER_FEEDBACK -> {
                    label = "${model.feedbackType}, $customerAndBusinessName"
                }

                AppConstant.LEAD_FEEDBACK -> {
                    label = "${model.feedbackType}, $customerAndBusinessName"
                }

                AppConstant.ORDER_DISPATCH -> {
                    label = "${model.moduleType}, $customerAndBusinessName"
                }

                AppConstant.PAYMENT -> {
                    label =
                            "${resources.getString(R.string.payment_collected)}, $customerAndBusinessName"
                }

                else -> {
                    label =
                            "${
                                model.action?.lowercase()?.replaceFirstChar(Char::titlecase)
                            } ${model.moduleType}, $customerAndBusinessName"
                }
            }
        }

        return label
    }

    private fun startGoogleMapForLiveLocation() {
        val activityMapPointsModel = ActivityMapPointsModel(liveLocationPoints)
        startActivity(
                Intent(requireContext(), GoogleMapActivity::class.java).putExtra(
                        AppConstant.LOCATION,
                        activityMapPointsModel
                )
        )
    }

    private fun initiateEmptyData() {
        binding.clTrackingInfo.clMapView.visibility = View.INVISIBLE

        binding.clTrackingInfo.tvStaffMeeting.text = "0"
        binding.clTrackingInfo.tvDistanceTravelled.text = "0"
        binding.clTrackingInfo.tvOrderCount.text = "0"

        val totalOrderAmount = CalculatorHelper().convertCommaSeparatedAmount(0.00, AppConstant.TWO_DECIMAL_POINTS)
        binding.clTrackingInfo.tvOrdersAmount.text = totalOrderAmount
        binding.clTrackingInfo.tvLeadCount.text = "0"

        binding.clTrackingInfo.ivDistanceInfo.visibility = View.GONE
        binding.clEmptyData.visibility = View.VISIBLE
    }

    private fun initDetailData(model: StaffTrackingActivityModules?) {
        val df = DecimalFormat("0.000")
        binding.clTrackingInfo.tvStaffMeeting.text = "${model?.meetings ?: 0}"

        if (model?.distanceTravelled != null && model.distanceTravelled != 0.0) {
            binding.clTrackingInfo.tvDistanceTravelled.text = df.format(model.distanceTravelled)
            binding.clTrackingInfo.ivDistanceInfo.visibility = View.VISIBLE
        } else {
            binding.clTrackingInfo.tvDistanceTravelled.text = "0"
            binding.clTrackingInfo.ivDistanceInfo.visibility = View.GONE
        }

        binding.clTrackingInfo.tvOrderCount.text = "${model?.orderCount ?: 0}"

        val totalOrderAmount =
                CalculatorHelper().convertCommaSeparatedAmount(model?.orderAmount
                        ?: 0.0, AppConstant.TWO_DECIMAL_POINTS)
        binding.clTrackingInfo.tvOrdersAmount.text = totalOrderAmount

        if (model?.staffName.isNullOrEmpty().not()) {
            if (staffId != 0) {
                binding.clTrackingInfo.tvStaffName.text = model?.staffName
                binding.clTrackingInfo.tvStaffName.visibility = View.VISIBLE
            }
        }

        binding.clTrackingInfo.tvLeadCount.text = "${model?.leadCount ?: 0}"

        binding.clTrackingInfo.tvMap.text =
                resources.getString(R.string.activity_map)
        binding.clTrackingInfo.ivMap.setImageResource(R.drawable.ic_location_blue)
    }

    private fun getLiveLocationData() {
        activityViewModel.getLiveLocationData(filterDate, staffId)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvStaffTrackingList.layoutManager = linearLayoutManager

        customerActivityAdapter = CustomerActivityAdapter(
                activityList, this, AppConstant.STAFF
        )

        binding.rvStaffTrackingList.adapter = customerActivityAdapter

        binding.rvStaffTrackingList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getTrackingActivity()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }


    override fun getFeedbackDetails(model: CustomerFollowUpDataItem) {
        when (model.moduleType) {
            AppConstant.ORDER -> {
                startActivity(
                        Intent(
                                requireContext(), OrderDetailActivity::class.java
                        ).putExtra(AppConstant.ORDER_ID, model.moduleId)
                )
            }

            AppConstant.LEAD -> {
                startActivity(
                        Intent(
                                requireContext(), LeadDetailsActivity::class.java
                        ).putExtra(AppConstant.LEAD_ID, model.moduleId)
                )
            }

            AppConstant.CUSTOMER -> {
                startActivity(
                        Intent(
                                requireContext(), CustomerDetailActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER_ID, model.moduleId)
                )
            }

            AppConstant.PAYMENT -> {
                startActivity(
                        Intent(
                                requireContext(), PaymentDetailsActivity::class.java
                        ).putExtra(AppConstant.PAYMENT_ID, model.moduleId)
                )
            }

            AppConstant.ORDER_DISPATCH -> {
                startActivity(
                        Intent(
                                requireContext(), OrderDetailActivity::class.java
                        ).putExtra(AppConstant.ORDER_ID, model.subModuleId)
                )
            }

            AppConstant.DAY_START_END -> {
                startActivity(
                        Intent(requireContext(), StartDayEndDayDetailsActivity::class.java)
                                .putExtra(AppConstant.ATTENDANCE, model)
                                .putExtra(AppConstant.IsStartDay,
                                        model.action?.equals(AppConstant.ATTENDANCE_CHECK_IN, true))
                )
            }

            else -> {
                startActivity(
                        Intent(
                                requireContext(), CustomerFeedbackDetailActivity::class.java
                        ).putExtra(AppConstant.ACTIVITY_ID, model.id)
                                .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.MY_ACTIVITY)
                )
            }
        }
    }

    override fun getMapLocation(model: CustomerFollowUpDataItem) {
        if (model.geoLocationLat != null && model.geoLocationLat?.roundToInt() != 0) {
            Utils.openMap(
                    requireContext(), model.geoLocationLat, model.geoLocationLong, model.label
            )
        }
    }
}