package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerBeatPlanBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.ActiveBeatRouteInfoAndDayListModel
import com.app.rupyz.model_kt.BeatRouteInfoModel
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class MyBeatPlanActivity : BaseActivity() {
    private lateinit var binding: ActivityCustomerBeatPlanBinding
    private var beatRoutePlanId: Int = 0
    private var beatId: Int = 0
    private var beatDate: String = ""
    private lateinit var beatFragmentPagerAdapter: BeatFragmentPagerAdapter

    private lateinit var beatViewModel: BeatViewModel

    private var beatRouteInfoModel: BeatRouteInfoModel? = null
    private var customerLevel: String = AppConstant.TARGET_CUSTOMERS_FILTER_FOR_BEAT

    val fragmentList = arrayListOf(AppConstant.CUSTOMER, AppConstant.LEAD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        beatRoutePlanId = intent.getIntExtra(AppConstant.BEAT_ROUTE_PLAN_ID, 0)
        beatId = intent.getIntExtra(AppConstant.BEAT_ID, 0)
        beatDate = intent.getStringExtra(AppConstant.DATE_FILTER)!!

        binding.progressBar.visibility = View.VISIBLE
        initObservers()
        initLayout()
        initTabLayout()

        binding.beatLayout.tvBeatPlanName.setOnClickListener {
            if (intent.hasExtra(AppConstant.STAFF_DETAILS)){
                finish()
            } else {
                someActivityResultLauncher.launch(
                    Intent(
                        this,
                        MyDailyBeatPlanListActivity::class.java
                    ).putExtra(AppConstant.BEAT_ROUTE_PLAN_ID, beatRoutePlanId)
                        .putExtra(AppConstant.BEAT_ROUTE, beatRouteInfoModel)
                )
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                beatRoutePlanId = intent.getIntExtra(AppConstant.BEAT_ROUTE_PLAN_ID, 0)
                beatId = intent.getIntExtra(AppConstant.BEAT_ID, 0)
                beatDate = intent.getStringExtra(AppConstant.DATE_FILTER)!!

                binding.clBeatPlan.visibility = View.GONE
                binding.clCustomerDetails.visibility = View.GONE
                binding.clHoliday.visibility = View.GONE
                binding.tvStatus.visibility = View.GONE

                initLayout()
            }
        }
    }

    private fun initLayout() {

        if (beatDate.isEmpty().not()) {
            getBeatPlan(beatDate)
        }

        val arrayAdapter = ArrayAdapter(
            this, R.layout.single_text_view_spinner_12dp_text,
            resources.getStringArray(R.array.beat_customer_level)
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCustomerLevel.adapter = arrayAdapter

        binding.spinnerCustomerLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            customerLevel = AppConstant.TARGET_CUSTOMERS_FILTER_FOR_BEAT
                            refreshTab()
                        }
                        1 -> {
                            customerLevel = AppConstant.VISITED_CUSTOMERS_FILTER_FOR_BEAT
                            refreshTab()
                        }
                        2 -> {
                            customerLevel = AppConstant.UNEXPECTED_CUSTOMERS_FILTER_FOR_BEAT
                            refreshTab()
                        }
                    }
                }
            }
    }

    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.CUSTOMER))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.LEAD))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                if (tab.position == 0){
                    binding.spinnerCustomerLevel.visibility = View.VISIBLE
                } else {
                    binding.spinnerCustomerLevel.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                if (position == 0){
                    binding.spinnerCustomerLevel.visibility = View.VISIBLE
                } else {
                    binding.spinnerCustomerLevel.visibility = View.GONE
                }
            }
        })
    }

    private fun refreshTab() {
        beatFragmentPagerAdapter = BeatFragmentPagerAdapter(
            this, fragmentList, beatRoutePlanId, beatDate, customerLevel
        )
        binding.viewPager.adapter = beatFragmentPagerAdapter
    }

    private fun getBeatPlan(date: String) {
        if (intent.hasExtra(AppConstant.STAFF_DETAILS)){
            beatViewModel.getStaffBeatPlanInfoList(beatRoutePlanId, date)
        }else {
            beatViewModel.getCurrentlyActiveBeatPlan(null, date)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        beatViewModel.currentlyActiveBeatLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                if (it.data?.beatRouteInfo != null) {
                    it.data.beatRouteInfo?.let { info ->
                        beatRouteInfoModel = info

                        setStatus(info.status)

                        if (intent.hasExtra(AppConstant.STAFF_DETAILS)){
                            binding.tvToolbarTitle.text = info.name

                        } else {
                            binding.tvToolbarTitle.text = resources.getString(R.string.my_beat_plan)
                        }

                        binding.tvToolbarSubTitle.text = "${
                            DateFormatHelper.convertStringToCustomDateFormat(
                                info.startDate,
                                SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                            )
                        } - ${
                            DateFormatHelper.convertStringToCustomDateFormat(
                                info.endDate, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                            )
                        }"

                        binding.beatLayout.tvBeatPlanDurationDate.text = "${
                            DateFormatHelper.convertStringToCustomDateFormat(
                                info.startDate,
                                SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                            )
                        } - ${
                            DateFormatHelper.convertStringToCustomDateFormat(
                                info.endDate, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                            )
                        }"

                        if (info.comments.isNullOrEmpty().not()) {
                            binding.beatLayout.clNotes.visibility = View.VISIBLE

                            binding.beatLayout.tvNotes.text = info.comments

                            if (SharedPref.getInstance()
                                    .getBoolean(AppConstant.BEAT_PLAN_NOTES_READ, false)
                            ) {
                                binding.beatLayout.ivNotesNotification.visibility = View.GONE
                            } else {
                                binding.beatLayout.ivNotesNotification.visibility = View.GONE
                            }
                            SharedPref.getInstance()
                                .putBoolean(AppConstant.BEAT_PLAN_NOTES_READ, true)

                            binding.beatLayout.tvNotes.visibility = View.VISIBLE
                            binding.beatLayout.ivDropDown.visibility = View.GONE
                        }
                    }

                }

                if (it.data?.beatRouteDayPlan != null) {
                    initBeatPlan(it.data)
                }
            } else {
                showToast("${it.message}")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initBeatPlan(model: ActiveBeatRouteInfoAndDayListModel?) {
        if (model?.beatRouteDayPlan != null) {

            model.beatRouteDayPlan?.let { day ->
                beatRoutePlanId = day.beatrouteplan!!

                if (day.beatId != null) {
                    beatId = day.beatId!!
                    binding.clCustomerDetails.visibility = View.VISIBLE
                } else {
                    binding.clCustomerDetails.visibility = View.GONE
                }

                if (day.isActive == true){
                    binding.tvStatusSubTitle.visibility = View.VISIBLE
                }

                binding.tvStatus.visibility = View.VISIBLE

                var rotationAngle = 0

                binding.beatLayout.clMain.setOnClickListener {
                    rotationAngle = if (rotationAngle == 0) 180 else 0
                    binding.beatLayout.ivDropArrowForInfo.animate()
                        .rotation(rotationAngle.toFloat()).setDuration(300)
                        .start()
                    binding.beatLayout.clBeatPlanInfo.isVisible = binding.beatLayout.clBeatPlanInfo.isVisible.not()
                }

                binding.clBeatPlan.visibility = View.VISIBLE

                binding.beatLayout.tvTitle.paintFlags =
                    binding.beatLayout.tvTitle.paintFlags or Paint.ANTI_ALIAS_FLAG
                binding.beatLayout.tvCancel.visibility = View.GONE

                binding.beatLayout.tvTitle.text = day.beatName

                if (day.isCancelled == true) {
                    binding.beatLayout.ivDropArrowForInfo.visibility = View.GONE
                    binding.beatLayout.groupCustomerCount.visibility = View.GONE
                    binding.beatLayout.groupHeading.visibility = View.GONE
                    binding.beatLayout.groupNightStay.visibility = View.GONE
                    binding.beatLayout.groupNewLeadCount.visibility = View.GONE

                    binding.beatLayout.tvCancel.visibility = View.VISIBLE
                    binding.beatLayout.tvTitle.setTextColor(resources.getColor(R.color.sales_text_color_light_black))
                    binding.beatLayout.tvTitle.paintFlags =
                        binding.beatLayout.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    binding.clCustomerDetails.visibility = View.GONE
                    binding.clHoliday.visibility = View.VISIBLE
                    binding.icHoliday.setImageResource(R.drawable.cancel_beat_calendar_image)
                } else if (day.moduleType.equals(AppConstant.HOLIDAY)) {
                    binding.beatLayout.ivDropArrowForInfo.visibility = View.GONE
                    binding.beatLayout.groupCustomerCount.visibility = View.GONE
                    binding.beatLayout.groupHeading.visibility = View.GONE
                    binding.beatLayout.groupNightStay.visibility = View.GONE
                    binding.beatLayout.groupNewLeadCount.visibility = View.GONE
                    binding.beatLayout.tvTitle.text = resources.getString(R.string.leave)
                    binding.beatLayout.tvTitle.setTextColor(resources.getColor(R.color.holiday_beat_color))
                    binding.clHoliday.visibility = View.VISIBLE
                    binding.icHoliday.setImageResource(R.drawable.holiday_beat_plan)
                } else {
                    var counter = 0
                    binding.beatLayout.ivDropArrowForInfo.visibility = View.VISIBLE
                    binding.beatLayout.tvTitle.setTextColor(resources.getColor(R.color.black))
                    binding.clHoliday.visibility = View.GONE

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
                        binding.beatLayout.tvNewLeadVisitedCount.text
                            "${day.achievedLeadsCount ?: 0}"
                        binding.beatLayout.groupNewLeadCount.visibility = View.VISIBLE
                    } else {
                        binding.beatLayout.groupNewLeadCount.visibility = View.GONE
                    }

                    if (day.purpose.isNullOrEmpty().not() || day.nightStay.isNullOrEmpty().not()) {
                         binding.beatLayout.view1.visibility = View.VISIBLE
                    } else {
                         binding.beatLayout.view1.visibility = View.GONE
                    }

                    if (counter > 0) {
                        binding.beatLayout.groupHeading.visibility = View.VISIBLE
                    }

                    if (day.purpose.isNullOrEmpty().not()) {
                         binding.beatLayout.tvPurpose.text = day.purpose
                         binding.beatLayout.groupPurpose.visibility = View.VISIBLE
                    } else {
                         binding.beatLayout.groupPurpose.visibility = View.GONE
                    }

                    if (day.nightStay.isNullOrEmpty().not()) {
                         binding.beatLayout.tvNightStay.text = day.nightStay
                         binding.beatLayout.groupNightStay.visibility = View.VISIBLE
                    } else {
                         binding.beatLayout.groupNightStay.visibility = View.GONE
                    }

                    refreshTab()
                }

                binding.beatLayout.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                    day.date, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
                binding.beatLayout.tvBeatPlanName.text = resources.getString(
                    R.string.beat_plan_name_with_underline, model.beatRouteInfo?.name
                )
                binding.beatLayout.tvBeatPlanName.paintFlags =
                    binding.beatLayout.tvBeatPlanName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
        }
    }

    private fun setStatus(status: String?) {
        binding.tvStatus.text = status
        binding.tvStatus.visibility = View.VISIBLE

        when (status) {
            AppConstant.APPROVED.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.payment_approved_text_color))
            }

            AppConstant.PENDING.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.status_pending_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.pending_text_color))
            }
            AppConstant.REJECTED.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.payment_rejected_text_color))
            }
            AppConstant.COMPLETED.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.status_closed_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.closed_text_color))
                binding.tvStatus.text = resources.getString(R.string.closed).uppercase()
            }
        }
    }
}