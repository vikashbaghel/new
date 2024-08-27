package com.app.rupyz.sales.staffactivitytrcker

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator
import com.app.rupyz.databinding.ActivityStaffActiveStatusInfoBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.StaffActiveInactiveInfoModel
import com.app.rupyz.model_kt.StaffTrackingActivityModules

class StaffActiveInfoActivity : BaseActivity(),
    ActiveInActiveStaffInfoAdapter.IOnStaffAttendanceInfoListener {
    private lateinit var binding: ActivityStaffActiveStatusInfoBinding

    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var adapter: ActiveInActiveStaffInfoAdapter
    private var staffList = ArrayList<StaffActiveInactiveInfoModel>()

    private var activityType: String = ""

    private var filterDate: String = ""

    private var isPageLoading = true
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffActiveStatusInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityType = intent.getStringExtra(AppConstant.ACTIVITY_TYPE) ?: AppConstant.ACTIVE

        filterDate = intent.getStringExtra(AppConstant.DATE) ?: ""

        if (filterDate.isNotEmpty()) {
            binding.tvToolbarSubTitle.text = DateFormatHelper.getMonthDate(filterDate)
        }

        handleIntentAction(activityType)

        initRecyclerView()
        initObserver()

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    private fun initObserver() {
        activityViewModel.activeInActiveLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isPageLoading = false
                if (it.data.isNullOrEmpty().not()) {
                    staffList.addAll(it.data!!)
                    adapter.notifyDataSetChanged()

                    binding.groupStaffInfo.visibility = View.VISIBLE
                    binding.clEmptyData.visibility = View.GONE

                    if (activityType == AppConstant.ACTIVE
                        || activityType == AppConstant.LEAVE
                    ) {
                        binding.tvDayStartedHeading.visibility = View.VISIBLE

                        if(activityType == AppConstant.LEAVE){
                            binding.tvDayStartedHeading.text = resources.getString(R.string.marked_leave)
                        }
                    } else {
                        binding.tvDayStartedHeading.visibility = View.GONE
                    }

                    if (it.data.size < 30) {
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
        val itemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.item_divider_gray
                )
            )
        binding.rvStaffInfo.addItemDecoration(itemDecoration)
        adapter = ActiveInActiveStaffInfoAdapter(staffList, activityType, this)
        binding.rvStaffInfo.adapter = adapter

        binding.rvStaffInfo.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getStaffList()
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
        getStaffList()

        when (activityType) {
            AppConstant.ACTIVE -> {
                binding.tvToolbarTitle.text = resources.getString(R.string.active_staff)
            }

            AppConstant.INACTIVE -> {
                binding.tvToolbarTitle.text = resources.getString(R.string.inactive_staff)
                binding.ivActive.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(R.color.color_727176)
                    )
            }

            AppConstant.LEAVE -> {
                binding.tvToolbarTitle.text = resources.getString(R.string.staff_on_a_leave)
                binding.ivActive.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(R.color.out_of_stock_red)
                    )
            }
        }
    }

    private fun getStaffList() {
        binding.progressBar.visibility = View.VISIBLE
        activityViewModel.getActiveInActiveStaffList(filterDate, activityType, currentPage)
    }

    override fun staffAttendanceInfo(model: StaffActiveInactiveInfoModel) {
        val staffModel = StaffTrackingActivityModules()
        staffModel.staffId = model.staffId
        staffModel.picUrl = model.picUrl
        staffModel.staffName = model.staffName

        if (activityType == AppConstant.LEAVE) {
            startActivity(
                Intent(this, DailySalesReportActivity::class.java)
                    .putExtra(AppConstant.STAFF, staffModel)
                    .putExtra(AppConstant.DATE_FILTER, filterDate)
            )
        }
    }
}