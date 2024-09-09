package com.app.rupyz.sales.staffactivitytrcker

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.ActivityDeviceLogsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.DeviceActivityListItem
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import java.util.Calendar

class DeviceLogsActivity : BaseActivity() {
    private lateinit var binding: ActivityDeviceLogsBinding

    private val activityViewModel: StaffActivityViewModel by viewModels()
    private lateinit var adapter: DeviceInfoLogsAdapter

    private var logList: ArrayList<DeviceActivityListItem> = ArrayList()

    private var filterDate: String = ""

    private var staffModel: StaffTrackingActivityModules? = null

    private var staffName: String = ""
    private var staffId: Int = 0
    private var isPageLoading = true
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initObserver()

        filterDate = intent.getStringExtra(AppConstant.DATE)
            ?: DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(Calendar.getInstance().time)

        binding.tvToolbarSubTitle.text = DateFormatHelper.getMonthDate(filterDate)

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
            ImageUtils.loadImage(staffModel?.picUrl, binding.ivToolbarUser)
        }

        binding.imgClose.setOnClickListener {
            finish()
        }

        getDeviceLogs()
    }

    private fun getDeviceLogs() {
        activityViewModel.getDeviceLogs(staffId, filterDate, currentPage)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStaffInfo.layoutManager = linearLayoutManager
        adapter = DeviceInfoLogsAdapter(logList)
        binding.rvStaffInfo.adapter = adapter

        binding.rvStaffInfo.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getDeviceLogs()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun initObserver() {
        activityViewModel.deviceActivityLogsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isPageLoading = false
                binding.clEmptyData.visibility = View.GONE

                if (currentPage == 1) {
                    logList.clear()
                    adapter.notifyDataSetChanged()
                }

                if (it.data.isNullOrEmpty().not()) {
                    logList.addAll(it.data!!)
                    adapter.notifyDataSetChanged()

                    if (it.data!!.size < 30) {
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

}