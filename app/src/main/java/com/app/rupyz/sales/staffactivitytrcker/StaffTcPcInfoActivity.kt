package com.app.rupyz.sales.staffactivitytrcker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityStaffTcPcInfoBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.toCamelCaseWithSpaces
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.StaffTcPcInfoModelItem
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import java.util.Calendar

class StaffTcPcInfoActivity : BaseActivity(), StaffTcPcInfoAdapter.StaffTcPcInfoListener {
    private lateinit var binding: ActivityStaffTcPcInfoBinding

    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var adapter: StaffTcPcInfoAdapter
    private var staffList = ArrayList<StaffTcPcInfoModelItem>()

    private var activityType: String = ""

    private var filterDate: String = ""
    private var staffName: String = ""
    private var staffId: Int = 0
    private var isPc = false

    private var isPageLoading = true
    private var isApiLastPage = false
    private var currentPage = 1

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var staffModel: StaffTrackingActivityModules? = null

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffTcPcInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myCalendar[year, month] = 1
        myCalendar.time = Calendar.getInstance().time

        filterDate = intent.getStringExtra(AppConstant.DATE_RANGE)
            ?: DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(cal.time)

        myCalendar.time = DateFormatHelper.convertStringToDate(filterDate)

        activityType = intent.getStringExtra(AppConstant.ACTIVITY_TYPE) ?: AppConstant.STAFF_TC_INFO

        staffModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AppConstant.STAFF,
                StaffTrackingActivityModules::class.java
            )
        } else {
            intent.getParcelableExtra(AppConstant.STAFF)
        }

        if (staffModel != null) {
            staffId = staffModel?.staffId ?: 0
            staffName = staffModel?.staffName ?: ""

            binding.tvToolbarTitle.text = staffName
        }

        initRecyclerView()
        initObserver()

        updateStartDate()

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

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    private fun updateStartDate() {
        filterDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        binding.tvToolbarSubTitle.text = DateFormatHelper.getMonthDate(filterDate)

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

        handleIntentAction(activityType)
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

    private fun initObserver() {
        activityViewModel.staffTcPcInfoLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isPageLoading = false
                binding.clEmptyData.visibility = View.GONE

                if (currentPage == 1) {
                    staffList.clear()
                    adapter.notifyDataSetChanged()
                }

                if (it.data?.records.isNullOrEmpty().not()) {
                    staffList.addAll(it.data?.records!!)
                    adapter.notifyDataSetChanged()

                    binding.clEmptyData.visibility = View.GONE

                    if (it.data.records.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    binding.clEmptyData.visibility = View.VISIBLE
                    if (currentPage == 1) {
                        isApiLastPage = true
                    }
                }
            } else {
                showToast("${it.message}")
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStaffInfo.layoutManager = linearLayoutManager
        adapter = StaffTcPcInfoAdapter(staffList, activityType, this)
        binding.rvStaffInfo.adapter = adapter

        binding.rvStaffInfo.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getStaffTcPcList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun handleIntentAction(activityType: String) {
        when (activityType) {
            AppConstant.STAFF_TC_INFO -> {
                isPc = false
                binding.tvActivityType.text = resources.getString(R.string.tc)
            }

            AppConstant.STAFF_PC_INFO -> {
                isPc = true
                binding.tvActivityType.text = resources.getString(R.string.pc)

            }
        }

        getStaffTcPcList()
    }

    private fun getStaffTcPcList() {
        binding.progressBar.visibility = View.VISIBLE
        activityViewModel.getStaffTcPcList(staffId, filterDate, isPc, currentPage)
    }

    override fun getLocationInfo(model: StaffTcPcInfoModelItem) {
        Utils.openMap(
            this,
            model.geoLocationLat,
            model.geoLocationLong,
            model.activityType?.toCamelCaseWithSpaces()
        )
    }

    override fun getActivityDetails(model: StaffTcPcInfoModelItem) {
        when (model.moduleType?.uppercase()) {
            AppConstant.ORDER.uppercase() -> {
                startActivity(
                    Intent(
                        this, OrderDetailActivity::class.java
                    ).putExtra(AppConstant.ORDER_ID, model.moduleId)

                )
            }

            AppConstant.LEAD.uppercase() -> {
                startActivity(
                    Intent(
                        this, LeadDetailsActivity::class.java
                    ).putExtra(AppConstant.LEAD_ID, model.moduleId)
                )
            }

            AppConstant.CUSTOMER.uppercase() -> {
                startActivity(
                    Intent(
                        this, CustomerDetailActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, model.moduleId)
                )
            }

            AppConstant.PAYMENT.uppercase() -> {
                startActivity(
                    Intent(
                        this, PaymentDetailsActivity::class.java
                    ).putExtra(AppConstant.PAYMENT_ID, model.moduleId)
                )
            }

            AppConstant.ORDER_DISPATCH.uppercase() -> {
                startActivity(
                    Intent(
                        this, OrderDetailActivity::class.java
                    ).putExtra(AppConstant.ORDER_ID, model.moduleId)
                )
            }

            else -> {
                startActivity(
                    Intent(
                        this, CustomerFeedbackDetailActivity::class.java
                    ).putExtra(AppConstant.ACTIVITY_ID, model.id)
                        .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.MY_ACTIVITY)
                        .putExtra(AppConstant.ACTIVITY_ID, model.moduleId)
                        .putExtra(AppConstant.CUSTOMER_TYPE, model.moduleType)
                )
            }
        }
    }
}