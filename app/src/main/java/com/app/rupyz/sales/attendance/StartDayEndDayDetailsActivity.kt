package com.app.rupyz.sales.attendance

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityStartDayEndDayDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_DISTRIBUTOR_VISIT
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_FULL_DAY
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_HALF_DAY
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_JOINT_ACTIVITY
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_MARK_LEAVE
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_OFFICE_VISIT
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_OTHERS
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_REGULAR_BEAT
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.JointStaffInfoModel
import com.app.rupyz.sales.customer.StaffInfoListAdapter
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class StartDayEndDayDetailsActivity : BaseActivity(),
    ProductImageViewPagerAdapter.ProductImageClickListener {

    private lateinit var binding: ActivityStartDayEndDayDetailsBinding
    private lateinit var viewModel: AttendanceViewModel
    private val staffListForJointActivity: ArrayList<JointStaffInfoModel> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private lateinit var staffInfoListAdapter: StaffInfoListAdapter
    private var attendanceId: Int = 0
    private var isStartDay = true
    private var isOnLeave = false
    private var attendanceModel: CustomerFollowUpDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartDayEndDayDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]

        initObservers()
        initAdapter()
        initData()
        setListener()

    }

    private fun setListener() {
        binding.apply {
            ivClose.setOnClickListener {
                finish()
            }
        }
    }

    private fun initData() {
        var mapLabel = ""
        if (intent.hasExtra(AppConstant.ATTENDANCE)) {
            attendanceModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    AppConstant.ATTENDANCE,
                    CustomerFollowUpDataItem::class.java
                )
            } else {
                intent.getParcelableExtra(AppConstant.ATTENDANCE)
            }

            isStartDay = intent.getBooleanExtra(AppConstant.IsStartDay, true)
            isOnLeave = intent.getBooleanExtra(ACTIVITY_TYPE_MARK_LEAVE, false)

            if (isOnLeave) {
                mapLabel = resources.getString(R.string.leave_details)
                binding.tvToolbarTitle.text = mapLabel
            } else if (isStartDay) {
                mapLabel = resources.getString(R.string.day_started)
                binding.tvToolbarTitle.text = mapLabel
            } else {
                mapLabel = resources.getString(R.string.day_ended)
                binding.tvToolbarTitle.text = mapLabel
            }

            if (attendanceModel?.moduleId != null) {
                attendanceId = attendanceModel?.moduleId!!

                binding.mainContent.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                viewModel.getAttendanceDetails(attendanceId)
            }

            if (attendanceModel?.geoLocationLat != null
                && attendanceModel?.geoLocationLat != 0.0
                && attendanceModel?.geoLocationLong != null
                && attendanceModel?.geoLocationLong != 0.0
            ) {
                binding.groupLocation.visibility = View.VISIBLE
                if (attendanceModel?.geoAddress.isNullOrBlank().not()) {
                    binding.tvLocations.text = attendanceModel?.geoAddress
                } else {
                    GeoLocationUtils.getAddress(
                        this,
                        attendanceModel?.geoLocationLat!!,
                        attendanceModel?.geoLocationLong!!
                    ) { address ->
                        binding.tvLocations.text = address
                    }
                }

                binding.tvLocations.setOnClickListener {
                    Utils.openMap(
                        this, attendanceModel?.geoLocationLat!!,
                        attendanceModel?.geoLocationLong!!,
                        mapLabel
                    )
                }

                binding.ivLocationsIcon.setOnClickListener {
                    Utils.openMap(
                        this, attendanceModel?.geoLocationLat!!,
                        attendanceModel?.geoLocationLong!!, mapLabel
                    )
                }
            }
        }
    }

    private fun initAdapter() {
        staffInfoListAdapter = StaffInfoListAdapter(staffListForJointActivity)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)

        binding.rvSelfie.layoutManager = GridLayoutManager(this, 6)
        binding.rvSelfie.adapter = addPhotoListAdapter
        binding.rvJoinActivity.adapter = staffInfoListAdapter
    }

    override fun onPdfClick(position: Int, url: String) {
        super.onPdfClick(position, url)
        FileUtils.openPdf(url, this)
    }

    private fun initObservers() {
        viewModel.attendanceDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { data ->
                    binding.apply {

                        mainContent.visibility = View.VISIBLE

                        tvDayStartEndTime.text = if (isStartDay) {
                            DateFormatHelper.convertIsoToDateAndTimeFormat(data.timeIn)
                        } else {
                            DateFormatHelper.convertIsoToDateAndTimeFormat(data.timeOut)
                        }

                        if (data.createdByName != null) {
                            binding.groupStaff.visibility = View.VISIBLE
                            binding.tvStaffName.text = data.createdByName
                        }

                        when (data.activityType) {

                            ACTIVITY_TYPE_FULL_DAY -> {
                                tvDayStatus.text = resources.getString(R.string.full_day)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_full_day)
                            }

                            ACTIVITY_TYPE_HALF_DAY -> {
                                tvDayStatus.text = resources.getString(R.string.half_day)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_half_day)
                            }

                            ACTIVITY_TYPE_REGULAR_BEAT -> {
                                tvDayStatus.text = resources.getString(R.string.regular_beat)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_beat_plan_tab)
                            }

                            ACTIVITY_TYPE_JOINT_ACTIVITY -> {
                                tvDayStatus.text = resources.getString(R.string.joint_activity)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_record_activity)
                            }

                            ACTIVITY_TYPE_MARK_LEAVE -> {
                                tvDayStatus.text = resources.getString(R.string.mark_leave)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_mark_leave)
                            }

                            ACTIVITY_TYPE_OFFICE_VISIT -> {
                                tvDayStatus.text = resources.getString(R.string.ho_visit)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_head_office_visit)
                            }

                            ACTIVITY_TYPE_DISTRIBUTOR_VISIT -> {
                                tvDayStatus.text = resources.getString(
                                    R.string.distributor_visit,
                                    SharedPref.getInstance()
                                        .getString(AppConstant.CUSTOMER_LEVEL_1),
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                )

                                ivDayStatusIcon.setImageResource(R.drawable.ic_distributor_icon)
                            }

                            ACTIVITY_TYPE_OTHERS -> {
                                tvDayStatus.text = resources.getString(R.string.others)
                                ivDayStatusIcon.setImageResource(R.drawable.ic_order_tab)
                            }

                            else -> {
                                tvDayStatus.text = data.attendanceType?.replace("_", "")
                                ivDayStatusIcon.setImageResource(R.drawable.ic_beat_plan_tab)
                            }
                        }

                        if (data.jointStaffIdsInfo.isNullOrEmpty().not()) {
                            binding.groupJoinActivity.visibility = View.VISIBLE
                            staffListForJointActivity.addAll(data.jointStaffIdsInfo!!)
                            staffInfoListAdapter.notifyDataSetChanged()
                        } else {
                            binding.groupJoinActivity.visibility = View.GONE
                        }

                        if (isStartDay) {
                            when (data.activityType) {
                                ACTIVITY_TYPE_REGULAR_BEAT -> {
                                    tvDayStatus.text = resources.getString(R.string.regular_beat)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_beat_plan_tab)
                                }

                                ACTIVITY_TYPE_JOINT_ACTIVITY -> {
                                    tvDayStatus.text = resources.getString(R.string.joint_activity)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_record_activity)
                                }

                                ACTIVITY_TYPE_MARK_LEAVE -> {
                                    tvDayStatus.text = resources.getString(R.string.mark_leave)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_mark_leave)
                                }

                                ACTIVITY_TYPE_OFFICE_VISIT -> {
                                    tvDayStatus.text = resources.getString(R.string.ho_visit)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_head_office_visit)
                                }

                                ACTIVITY_TYPE_DISTRIBUTOR_VISIT -> {
                                    tvDayStatus.text = resources.getString(
                                        R.string.distributor_visit,
                                        SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_1),
                                        SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_2)
                                    )

                                    ivDayStatusIcon.setImageResource(R.drawable.ic_distributor_icon)
                                }

                                ACTIVITY_TYPE_OTHERS -> {
                                    tvDayStatus.text = resources.getString(R.string.others)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_order_tab)
                                }

                                else -> {
                                    tvDayStatus.text = data.attendanceType?.replace("_", "")
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_beat_plan_tab)
                                }
                            }

                            if (data.startDayComments.isNullOrBlank().not()) {
                                binding.groupComments.visibility = View.VISIBLE
                                binding.tvComment.text = data.startDayComments
                            } else {
                                binding.groupComments.visibility = View.GONE
                            }

                            if (data.startDayImagesInfo.isNullOrEmpty().not()) {
                                binding.groupSelfie.visibility = View.VISIBLE
                                pics.clear()
                                pics.addAll(data.startDayImagesInfo!!)
                                addPhotoListAdapter.notifyDataSetChanged()
                            } else {
                                binding.groupSelfie.visibility = View.GONE
                            }

                            if (data.startDayGeoLocationLat != null
                                && data.startDayGeoLocationLat != 0.0
                                && data.startDayGeoLocationLong != null
                                && data.startDayGeoLocationLong != 0.0
                            ) {
                                binding.groupLocation.visibility = View.VISIBLE
                                if (data.startDayGeoAddress.isNullOrBlank().not()) {
                                    binding.tvLocations.text = data.startDayGeoAddress
                                } else {
                                    GeoLocationUtils.getAddress(
                                        this@StartDayEndDayDetailsActivity,
                                        data.startDayGeoLocationLat!!,
                                        data.startDayGeoLocationLong!!
                                    ) { address ->
                                        binding.tvLocations.text = address
                                    }
                                }

                                binding.tvLocations.setOnClickListener {
                                    Utils.openMap(
                                        this@StartDayEndDayDetailsActivity,
                                        data.startDayGeoLocationLat!!,
                                        data.startDayGeoLocationLong!!,
                                        AppConstant.ATTENDANCE_CHECK_IN
                                    )
                                }

                                binding.ivLocationsIcon.setOnClickListener {
                                    Utils.openMap(
                                        this@StartDayEndDayDetailsActivity,
                                        data.startDayGeoLocationLat!!,
                                        data.startDayGeoLocationLong!!,
                                        AppConstant.ATTENDANCE_CHECK_IN
                                    )
                                }
                            }

                        } else {
                            when (data.attendanceType) {

                                ACTIVITY_TYPE_FULL_DAY -> {
                                    tvDayStatus.text = resources.getString(R.string.full_day)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_full_day)
                                }

                                ACTIVITY_TYPE_HALF_DAY -> {
                                    tvDayStatus.text = resources.getString(R.string.half_day)
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_half_day)
                                }

                                else -> {
                                    tvDayStatus.text = data.attendanceType?.replace("_", "")
                                    ivDayStatusIcon.setImageResource(R.drawable.ic_beat_plan_tab)
                                }
                            }

                            if (data.endDayComments.isNullOrBlank().not()) {
                                binding.groupComments.visibility = View.VISIBLE
                                binding.tvComment.text = data.endDayComments
                            } else {
                                binding.groupComments.visibility = View.GONE
                            }

                            if (data.endDayImagesInfo.isNullOrEmpty().not()) {
                                binding.groupSelfie.visibility = View.VISIBLE
                                pics.clear()
                                pics.addAll(data.endDayImagesInfo!!)
                                addPhotoListAdapter.notifyDataSetChanged()
                            } else {
                                binding.groupSelfie.visibility = View.GONE
                            }

                            if (data.endDayGeoLocationLat != null
                                && data.endDayGeoLocationLat != 0.0
                                && data.endDayGeoLocationLong != null
                                && data.endDayGeoLocationLong != 0.0
                            ) {
                                binding.groupLocation.visibility = View.VISIBLE
                                if (data.endDayGeoAddress.isNullOrBlank().not()) {
                                    binding.tvLocations.text = data.endDayGeoAddress
                                } else {
                                    GeoLocationUtils.getAddress(
                                        this@StartDayEndDayDetailsActivity,
                                        data.endDayGeoLocationLat!!,
                                        data.endDayGeoLocationLong!!
                                    ) { address ->
                                        binding.tvLocations.text = address
                                    }
                                }

                                binding.tvLocations.setOnClickListener {
                                    Utils.openMap(
                                        this@StartDayEndDayDetailsActivity,
                                        data.endDayGeoLocationLat!!,
                                        data.endDayGeoLocationLong!!,
                                        AppConstant.ATTENDANCE_CHECK_OUT
                                    )
                                }

                                binding.ivLocationsIcon.setOnClickListener {
                                    Utils.openMap(
                                        this@StartDayEndDayDetailsActivity,
                                        data.endDayGeoLocationLat!!,
                                        data.endDayGeoLocationLong!!,
                                        AppConstant.ATTENDANCE_CHECK_OUT
                                    )
                                }
                            }
                        }

                        if (isStartDay.not() && data.isAutoTimeOut != null && data.isAutoTimeOut == true) {
                            binding.tvToolbarTitle.text =
                                resources.getString(R.string.day_end_automatically)
                        }

                    }


                }
            } else {
                showToast("${it.message}")
            }
        }
    }

    override fun onImageClick(position: Int) {

    }

}