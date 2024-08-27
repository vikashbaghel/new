package com.app.rupyz.sales.staffactivitytrcker

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityDailySalesReportBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.helper.toCamelCaseWithSpaces
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.isTimeLessThanTo8PM
import com.app.rupyz.model_kt.ActivityMapPointsModel
import com.app.rupyz.model_kt.CategoryMetricsItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.DailySalesReportData
import com.app.rupyz.model_kt.ProductMetricsItem
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import com.app.rupyz.sales.attendance.StartDayEndDayDetailsActivity
import com.app.rupyz.sales.map.GoogleMapActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class DailySalesReportActivity : BaseActivity() {
    private lateinit var binding: ActivityDailySalesReportBinding
    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var productCategoryAdapter: DailySalesReportCategoryAdapter
    private lateinit var productSummeryAdapter: ProductSummeryDetailsAdapter

    private var productCategoryList = ArrayList<CategoryMetricsItem>()
    private var productSummeryList = ArrayList<ProductMetricsItem>()
    private var activityList = ArrayList<CustomerFollowUpDataItem>()
    private var mapPointsList: ArrayList<CustomerFollowUpDataItem> = ArrayList()
    private var liveLocationList: ArrayList<CustomerFollowUpDataItem> = ArrayList()
    private var lastLiveLocationPoints: CustomerFollowUpDataItem? = null

    private var userId: Int? = null

    private var rotationAngleCustomer = 0
    private var rotationAngleCs = 0
    private var rotationAnglePs = 0

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var filterDate: String = ""

    private var mapPointCount = 0

    private var currentPage = 1
    private var isApiLastPage = false
    private var staffName: String = ""

    private var staffModel: StaffTrackingActivityModules? = null

    private var isFakeLocationDetectedFromThisDate = false
    private var isUserCheckout = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailySalesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myCalendar[year, month] = 1

        if (intent.getStringExtra(AppConstant.DATE_FILTER).isNullOrEmpty().not()) {
            filterDate = intent.getStringExtra(AppConstant.DATE_FILTER)
                ?: DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(cal.time)
            myCalendar.time = DateFormatHelper.convertStringToDate(filterDate)
        } else {
            myCalendar.time = Calendar.getInstance().time
        }

        initObservers()
        initRecyclerView()

        binding.scrollView.visibility = View.GONE

        staffModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AppConstant.STAFF,
                StaffTrackingActivityModules::class.java
            )
        } else {
            intent.getParcelableExtra(AppConstant.STAFF)
        }

        if (staffModel != null) {
            userId = staffModel?.staffId ?: 0
            staffName = staffModel?.staffName ?: ""
            binding.tvToolbarTitle.text = staffName
            ImageUtils.loadImage(staffModel?.picUrl, binding.ivToolbarUser)
        }

        updateStartDate()

        initDate()
        initLayout()

        binding.imgClose.setOnClickListener {
            finish()
        }

        if (isStaffUser && SharedPref.getInstance().getBoolean(
                AppConstant.STAFF_HIERARCHY,
                false
            ).not()
        ) {
            binding.clDeviceLogs.hideView()
            binding.clLiveLocation.hideView()
        } else {
            binding.clDeviceLogs.showView()
            binding.clLiveLocation.showView()
        }
    }

    private fun getDailySalesReport() {
        activityViewModel.getDailySalesReport(userId, filterDate)
    }

    private fun initDate() {

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
            myCalendar.add(Calendar.DAY_OF_MONTH, -1)
            binding.tvDate.text =
                DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)
            updateStartDate()
        }

        binding.ivIncrementMonth.setOnClickListener {
            myCalendar.add(Calendar.DAY_OF_MONTH, 1)
            binding.tvDate.text =
                DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)
            updateStartDate()
        }
    }

    private fun updateStartDate() {
        filterDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)

        lastLiveLocationPoints = null
        mapPointsList = ArrayList()
        liveLocationList = ArrayList()

        startDialog()
        binding.clEmptyData.visibility = View.GONE

        mapPointCount = 0

        disableTouch()

        getDailySalesReport()

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

    private fun getTrackingActivity() {
        activityList.clear()
        activityViewModel.getStaffTrackingDetails(filterDate, userId, currentPage)
    }

    private fun openStartDateCalendar() {
        val dialog = DatePickerDialog(
            this,
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

    private fun initLayout() {

        binding.clNewCustomer.setOnClickListener {
            binding.clNewCustomerDetails.isVisible =
                binding.clNewCustomerDetails.isVisible.not()

            rotationAngleCustomer = if (rotationAngleCustomer == 0) 180 else 0

            binding.ivCustomerLevelDropDown.animate().rotation(rotationAngleCustomer.toFloat())
                .setDuration(300).start()
        }

        binding.clTotalCall.setOnClickListener {
            startActivity(
                Intent(this, StaffTcPcInfoActivity::class.java)
                    .putExtra(
                        AppConstant.ACTIVITY_TYPE,
                        AppConstant.STAFF_TC_INFO
                    ).putExtra(AppConstant.STAFF, staffModel)
                    .putExtra(AppConstant.DATE_RANGE, filterDate)
            )
        }

        binding.clProductiveCall.setOnClickListener {
            startActivity(
                Intent(this, StaffTcPcInfoActivity::class.java)
                    .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.STAFF_PC_INFO)
                    .putExtra(AppConstant.STAFF, staffModel)
                    .putExtra(AppConstant.DATE_RANGE, filterDate)
            )
        }

        binding.clCategorySummery.setOnClickListener {
            binding.clCategorySummeryDetails.isVisible =
                binding.clCategorySummeryDetails.isVisible.not()

            rotationAngleCs = if (rotationAngleCs == 0) 180 else 0

            binding.ivCategorySummeryDropDown.animate().rotation(rotationAngleCs.toFloat())
                .setDuration(300)
                .start()
        }

        binding.clProductSummary.setOnClickListener {
            binding.clProductSummeryDetails.isVisible =
                binding.clProductSummeryDetails.isVisible.not()

            rotationAnglePs = if (rotationAnglePs == 0) 180 else 0

            binding.ivProductSummaryDropDown.animate().rotation(rotationAnglePs.toFloat())
                .setDuration(300)
                .start()

            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding.clActivityPoints.setOnClickListener {
            startDialog(resources.getString(R.string.collecting_data))
            getTrackingActivity()
        }

        binding.clLiveLocation.setOnClickListener {
            startDialog(resources.getString(R.string.collecting_data))
            getLiveLocationData()
        }

        binding.clDeviceLogs.setOnClickListener {
            startActivity(
                Intent(this, DeviceLogsActivity::class.java)
                    .putExtra(AppConstant.STAFF, staffModel)
                    .putExtra(AppConstant.DATE, filterDate)
            )
        }
    }

    private fun getLiveLocationData() {
        activityViewModel.getLiveLocationData(filterDate, userId ?: 0)
    }

    private fun initRecyclerView() {
        binding.rvProductCategorySales.layoutManager = LinearLayoutManager(this)
        productCategoryAdapter =
            DailySalesReportCategoryAdapter(productCategoryList, AppConstant.PRODUCT)
        binding.rvProductCategorySales.adapter = productCategoryAdapter

        binding.rvProductSales.layoutManager = LinearLayoutManager(this)
        productSummeryAdapter = ProductSummeryDetailsAdapter(productSummeryList)
        binding.rvProductSales.adapter = productSummeryAdapter
    }

    private fun initObservers() {
        activityViewModel.dailySalesReportLiveData.observe(this) {
            stopDialog()
            enableTouch()
            if (it.error == false) {
                if (it.data != null) {
                    if (it.data.date != null) {
                        initData(it.data)
                    } else {
                        binding.scrollView.visibility = View.GONE
                        binding.icHoliday.setImageResource(R.drawable.holiday_beat_plan)
                        binding.tvErrorMessage.text =
                            resources.getString(R.string.no_activity_performed)
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                } else {
                    binding.scrollView.visibility = View.GONE
                    binding.icHoliday.setImageResource(R.drawable.no_data_available)
                    binding.tvErrorMessage.text =
                        resources.getString(R.string.no_data_for_this_date)
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            } else {
                showToast(it.message)
            }
        }

        activityViewModel.staffTrackingDetailsLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    if (data.activityList.isNullOrEmpty().not()) {
                        activityList.addAll(data.activityList!!)

                        if (data.activityList.size < 30) {
                            isApiLastPage = true
                        } else {
                            currentPage++
                            getTrackingActivity()
                            return@observe
                        }
                    } else {
                        if (currentPage == 1) {
                            isApiLastPage = true
                        }
                    }

                    if (isTimeLessThanTo8PM() && isUserCheckout.not()) {
                        if (data.activityModules?.lastLiveLocation != null) {
                            data.activityModules.lastLiveLocation.let { dataModel ->
                                if (dataModel.lat != 0.0 && dataModel.long != 0.0) {
                                    lastLiveLocationPoints = CustomerFollowUpDataItem(
                                        geoLocationLat = dataModel.lat,
                                        geoLocationLong = dataModel.long
                                    )
                                }
                            }
                        }
                    }

                    stopDialog()

                    mapPointsList = ArrayList()
                    activityList.asReversed().forEachIndexed { _, activity ->

                        if (activity.geoLocationLat != 0.0 && activity.geoLocationLong != 0.0) {
                            mapPointCount++
                            mapPointsList.add(activity)
                        }
                    }

                    if (mapPointCount == 0) {
                        showToast("No activity points found!!")
                    } else {
                        trackingInfoAction()
                    }
                }
            } else {
                stopDialog()
                if (it.errorCode == 403) {
                    logout()
                } else {
                    showToast("${it.message}")
                }
            }
        }

        activityViewModel.liveLocationData.observe(this) {
            if (it.error == false) {
                stopDialog()
                if (it.data.isNullOrEmpty().not()) {

                    it.data?.forEach { dataModel ->
                        if (dataModel.latitude != 0.0 && dataModel.longitude != 0.0) {
                            liveLocationList.add(
                                CustomerFollowUpDataItem(
                                    geoLocationLat = dataModel.latitude,
                                    geoLocationLong = dataModel.longitude,
                                    createdAt = dataModel.dateTime
                                )
                            )
                        }
                    }
                    startGoogleMapForLiveLocation()
                } else {
                    showToast(resources.getString(R.string.no_live_location_found))
                }
            }
        }
    }

    private fun trackingInfoAction() {
        val activityMapPointsModel = ActivityMapPointsModel(
            lastLiveLocationPoints = lastLiveLocationPoints,
            activityPoints = mapPointsList,
            liveLocationPoints = liveLocationList
        )

        startActivity(
            Intent(this, GoogleMapActivity::class.java)
                .putExtra(AppConstant.LOCATION, activityMapPointsModel)
                .putExtra(AppConstant.STAFF_NAME, staffName)
                .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.MY_ACTIVITY)
                .putExtra(AppConstant.DATE, filterDate)
                .putExtra(
                    AppConstant.FAKE_LOCATION_DETECTED,
                    isFakeLocationDetectedFromThisDate
                )
        )
    }

    private fun startGoogleMapForLiveLocation() {
        val activityMapPointsModel = ActivityMapPointsModel(
            lastLiveLocationPoints = lastLiveLocationPoints,
            activityPoints = mapPointsList,
            liveLocationPoints = liveLocationList
        )
        startActivity(
            Intent(this, GoogleMapActivity::class.java)
                .putExtra(AppConstant.LOCATION, activityMapPointsModel)
                .putExtra(AppConstant.STAFF_NAME, staffName)
                .putExtra(AppConstant.DATE, filterDate)
        )
    }

    private fun initData(model: DailySalesReportData) {
        binding.tvBeatPlanName.text = model.beatName
        binding.tvDistributorName.text = model.distributorName
        binding.tvNewLead.text = ""
        binding.tvTotalCall.text = ""

        if (model.beatList.isNullOrEmpty().not()) {
            binding.tvBeatPlanName.text = "${model.beatList!![0].name}"
        }

        binding.tvUserName.text = model.userName

        staffName = model.userName ?: ""

        ImageUtils.loadTeamImage(model.profilePicUrl, binding.ivUser)
        binding.tvDateForReport.text = DateFormatHelper.convertStringToCustomDateFormat(
            model.date, SimpleDateFormat(
                "dd MMM yyyy",
                Locale.ENGLISH
            )
        )

        val attendanceModel = CustomerFollowUpDataItem()
        attendanceModel.moduleId = model.attendanceId

        if (model.attendanceType == AppConstant.ACTIVITY_TYPE_MARK_LEAVE) {
            binding.clEndDay.visibility = View.GONE
            binding.clStartDay.visibility = View.GONE
            binding.clAttendance.visibility = View.VISIBLE
            binding.tvOnALeave.visibility = View.VISIBLE

            binding.tvOnALeave.setOnClickListener {
                startActivity(
                    Intent(this, StartDayEndDayDetailsActivity::class.java)
                        .putExtra(AppConstant.ATTENDANCE, attendanceModel)
                        .putExtra(AppConstant.ACTIVITY_TYPE_MARK_LEAVE, true)
                )
            }
        } else {
            binding.tvOnALeave.visibility = View.GONE
            binding.clAttendance.visibility = View.VISIBLE

            if (model.checkInTime.isNullOrEmpty().not()) {
                binding.clStartDay.visibility = View.VISIBLE
                binding.tvDayStarted.text =
                    DateFormatHelper.convertDateToTimeFormat(model.checkInTime)
                binding.tvDayStartedActivity.text = model.activityType?.toCamelCaseWithSpaces()

                binding.clStartDay.setOnClickListener {
                    startActivity(
                        Intent(this, StartDayEndDayDetailsActivity::class.java)
                            .putExtra(AppConstant.ATTENDANCE, attendanceModel)
                            .putExtra(AppConstant.IsStartDay, true)
                    )
                }
            } else {
                binding.tvDayStarted.text = ""
                binding.tvDayStartedActivity.text = ""
            }

            if (model.checkOutTime.isNullOrEmpty().not()) {
                binding.clEndDay.visibility = View.VISIBLE
                isUserCheckout = true
                binding.tvDayEnded.text =
                    DateFormatHelper.convertDateToTimeFormat(model.checkOutTime)
                binding.tvDayEndedActivity.text = model.attendanceType?.toCamelCaseWithSpaces()

                binding.clEndDay.setOnClickListener {
                    startActivity(
                        Intent(this, StartDayEndDayDetailsActivity::class.java)
                            .putExtra(AppConstant.ATTENDANCE, attendanceModel)
                            .putExtra(AppConstant.IsStartDay, false)
                    )
                }
            } else {
                binding.clEndDay.visibility = View.GONE
                isUserCheckout = false
                binding.tvDayEnded.text = ""
                binding.tvDayEndedActivity.text = ""
            }
        }

        if (model.isFakeLocationDetected == true) {
            isFakeLocationDetectedFromThisDate = true
            binding.tvFakeLocationDetected.visibility = View.VISIBLE
        } else {
            isFakeLocationDetectedFromThisDate = false
            binding.tvFakeLocationDetected.visibility = View.GONE
        }



        binding.tvTotalCall.text = "${model.totalActivityCount}"
        binding.tvNewLead.text = "${model.newLeadIds?.size ?: 0}"

        binding.tvProductiveCall.text = "${model.totalPcCount ?: 0}"

        var totalNewCustomerCount = 0
        if (model.newCustomerData != null) {
            if (model.newCustomerData.lEVEL1?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL1.count

                binding.hdLevel1.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                binding.tvLevelOneCount.text = "${model.newCustomerData.lEVEL1.count}"
            }

            if (model.newCustomerData.lEVEL2?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL2.count
                binding.hdLevel2.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                binding.tvLevelTwoCount.text = "${model.newCustomerData.lEVEL2.count}"
            }

            if (model.newCustomerData.lEVEL3?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL3.count

                binding.hdLevel3.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                binding.tvLevelThreeCount.text = "${model.newCustomerData.lEVEL3.count}"
            }
        }

        binding.tvNewCustomerCount.text = "$totalNewCustomerCount"

        if (model.categoryMetrics.isNullOrEmpty().not()) {
            productCategoryList.addAll(model.categoryMetrics!!)
            productCategoryAdapter.notifyDataSetChanged()
        }

        if (model.productMetrics.isNullOrEmpty().not()) {
            productSummeryList.addAll(model.productMetrics!!)
            productSummeryAdapter.notifyDataSetChanged()
        }

        binding.scrollView.visibility = View.VISIBLE
        binding.ivShare.visibility = View.VISIBLE

        binding.ivShare.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            binding.clNewCustomerDetails.visibility = View.VISIBLE
            binding.clCategorySummeryDetails.visibility = View.VISIBLE
            binding.clProductSummeryDetails.visibility = View.VISIBLE
            binding.clDeviceLogs.hideView()
            binding.clLiveLocation.hideView()
            binding.groupUser.visibility = View.VISIBLE

            Handler(Looper.myLooper()!!).postDelayed({
                launch {
                    val bitmap = loadBitmapFromView()
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE

                        if (isStaffUser.not()) {
                            binding.clDeviceLogs.showView()
                            binding.clLiveLocation.showView()
                        } else if (isStaffUser && SharedPref.getInstance().getBoolean(
                                AppConstant.STAFF_HIERARCHY,
                                false
                            )
                        ) {
                            binding.clDeviceLogs.showView()
                            binding.clLiveLocation.showView()
                        }

                        binding.groupUser.visibility = View.GONE

                        if (bitmap != null) {
                            shareResultAsImage(bitmap)
                        }
                    }
                }
            }, 1000)
        }

        binding.scrollView.visibility = View.VISIBLE
    }

    private fun loadBitmapFromView(): Bitmap? {

        val bitmap = Bitmap.createBitmap(
            binding.scrollView.getChildAt(0).width,
            binding.scrollView.getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawARGB(100, 49, 43, 129)
        binding.scrollView.getChildAt(0).draw(canvas)

        // Do whatever you want with your bitmap

        // Do whatever you want with your bitmap
        return bitmap
    }

    private fun shareResultAsImage(bitmap: Bitmap) {
        try {
            val pathOfBmp = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap, UUID.randomUUID().toString() + ".png", null
            )
            Logger.errorLogger("Bitmap Crash", pathOfBmp)
            if (pathOfBmp !== "") {
                val bmpUri = Uri.parse(pathOfBmp)
                val emailIntent1 = Intent(Intent.ACTION_SEND)
                emailIntent1.setType("image/png")
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri)
                startActivity(emailIntent1)
            } else {
                Logger.errorLogger("Bitmap Crash", "null path ")
            }
        } catch (ex: Exception) {
            Logger.errorLogger("Bitmap Crash", ex.message)
            Logger.errorLogger("Bitmap Crash", ex.localizedMessage)
        }
    }
}