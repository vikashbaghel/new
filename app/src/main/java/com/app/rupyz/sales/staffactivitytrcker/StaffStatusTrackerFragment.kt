package com.app.rupyz.sales.staffactivitytrcker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentStaffStatusTrackerBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.hideWithRightToLeftAnimation
import com.app.rupyz.generic.helper.showWithRightToLeftAnimation
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.StaffActivityFilterModel
import com.app.rupyz.model_kt.StaffActivitySortingModel
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import com.app.rupyz.sales.filter.DateRangeSelectBottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class StaffStatusTrackerFragment : BaseFragment(),
    TeamTrackingListAdapter.ITeamTrackingListener,
    StaffActivitySortByBottomSheetDialogFragment.ISortByStaffActivityListener,
    DateRangeSelectBottomSheetDialogFragment.IDateRangeFilterListener {
    private lateinit var binding: FragmentStaffStatusTrackerBinding

    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var teamTrackingListAdapter: TeamTrackingListAdapter

    private var activityList = ArrayList<StaffTrackingActivityModules>()
    private var filteredRoleList: ArrayList<String?> = ArrayList()
    private var filterReportingManager: Int? = null

    private var filterDate: String = ""

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var isPageLoading = true
    private var isApiLastPage = false
    private var currentPage = 1

    private var dateRange: String = AppConstant.TODAY
    private var rangeCount = 0

    private var startDateRange: String = ""
    private var endDateRange: String = ""

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var staffActivityFilterModel: StaffActivityFilterModel? = null
    private var staffActivitySortingModel: StaffActivitySortingModel? = null

    private var isHierarchyExist = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffStatusTrackerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myCalendar[year, month] = 1
        myCalendar.time = Calendar.getInstance().time

        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(cal.time)
        filterDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(cal.time)

        if (isStaffUser() && SharedPref.getInstance().getBoolean(
                AppConstant.STAFF_HIERARCHY,
                false
            ).not()
        ) {
            isHierarchyExist = false
        }

        initRecyclerview()
        initObserver()

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

        binding.tvFilterRange.setOnClickListener {
            val fragment = DateRangeSelectBottomSheetDialogFragment.newInstance(
                this, rangeCount
            )
            fragment.show(
                childFragmentManager,
                DateRangeSelectBottomSheetDialogFragment::class.java.name
            )

        }

        binding.clActiveStaff.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaffActiveInfoActivity::class.java).putExtra(
                    AppConstant.DATE,
                    filterDate
                ).putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.ACTIVE)
            )
        }


        binding.clInActiveStaff.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaffActiveInfoActivity::class.java).putExtra(
                    AppConstant.DATE,
                    filterDate
                ).putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.INACTIVE)
            )
        }

        binding.clOnLeaveStaffStaff.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaffActiveInfoActivity::class.java).putExtra(
                    AppConstant.DATE,
                    filterDate
                ).putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.LEAVE)
            )
        }

        binding.tvFilter.setOnClickListener {
            filterActivityResultLauncher.launch(
                Intent(
                    requireContext(),
                    StaffActivityFilterActivity::class.java
                ).putExtra(AppConstant.ACTIVITY_FILTER, staffActivityFilterModel)
            )
        }

        binding.tvSearch.setOnClickListener {
            binding.etSearch.showWithRightToLeftAnimation()
            binding.ivClearSearch.showWithRightToLeftAnimation()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                activityList.clear()
                teamTrackingListAdapter.notifyDataSetChanged()
                getTeamTrackingActivity()
                Utils.hideKeyboard(requireActivity())
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
                    getTeamTrackingActivity()
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
            binding.etSearch.hideWithRightToLeftAnimation()
            binding.ivClearSearch.hideWithRightToLeftAnimation()
            binding.etSearch.setText("")

            activityList.clear()
            teamTrackingListAdapter.notifyDataSetChanged()
        }

        binding.tvSortBy.setOnClickListener {
            val fragment = StaffActivitySortByBottomSheetDialogFragment.newInstance(
                this,
                staffActivitySortingModel
            )
            fragment.show(
                childFragmentManager,
                StaffActivitySortByBottomSheetDialogFragment::class.java.name
            )
        }

    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            activityList.clear()
            teamTrackingListAdapter.notifyDataSetChanged()

            getTeamTrackingActivity()
        }
    }

    private var filterActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            staffActivityFilterModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data!!.getParcelableExtra(
                    AppConstant.ACTIVITY_FILTER,
                    StaffActivityFilterModel::class.java
                )
            } else {
                result.data!!.getParcelableExtra(
                    AppConstant.ACTIVITY_FILTER
                )
            }

            var counter = 0
            if (staffActivityFilterModel != null) {
                if (staffActivityFilterModel?.filterRoleList.isNullOrEmpty().not()) {
                    filteredRoleList.clear()
                    filteredRoleList.addAll(staffActivityFilterModel?.filterRoleList!!)
                    counter++
                } else {
                    filteredRoleList.clear()
                }

                if (staffActivityFilterModel?.filterStaffId != null) {
                    filterReportingManager = staffActivityFilterModel?.filterStaffId
                    counter++
                } else {
                    filterReportingManager = null
                }

                if (counter > 0) {
                    binding.tvFilterCount.visibility = View.VISIBLE
                    binding.tvFilterCount.text = "$counter"
                } else {
                    binding.tvFilterCount.visibility = View.GONE
                }
            } else {
                binding.tvFilterCount.visibility = View.GONE
            }

            currentPage = 1
            getTeamTrackingActivity()
        }
    }

    private fun getTeamAggregatedInfo() {
        binding.slStatus.visibility = View.VISIBLE
        activityViewModel.getTeamAggregatedInfo(filterDate)
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

        getTeamAggregatedInfo()
    }

    private fun initRecyclerview() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvTeamActivity.layoutManager = linearLayoutManager
        teamTrackingListAdapter =
            TeamTrackingListAdapter(activityList, this, isHierarchyExist)
        binding.rvTeamActivity.adapter = teamTrackingListAdapter

        binding.rvTeamActivity.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getTeamTrackingActivity()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun getTeamTrackingActivity() {
        activityViewModel.getTeamTrackingActivity(
            dateRange,
            startDateRange, endDateRange,
            currentPage,
            binding.etSearch.text.toString(),
            filteredRoleList,
            filterReportingManager,
            staffActivitySortingModel
        )
    }

    private fun initObserver() {
        activityViewModel.teamTrackingDetailsLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == false) {
                    isPageLoading = false

                    it.data?.let { records ->

                        if (currentPage == 1) {
                            activityList.clear()
                        }

                        teamTrackingListAdapter.setHeaderText(
                            Triple(
                                records.tcMeetingCount ?: 0,
                                records.pcOrderCount ?: 0,
                                records.totalOrderAmount ?: 0.0
                            )
                        )
                        if (records.records.isNullOrEmpty().not()) {
                            activityList.addAll(records.records!!)
                            teamTrackingListAdapter.notifyDataSetChanged()

                            if (records.records.size < 30) {
                                isApiLastPage = true
                            }
                        } else {
                            if (currentPage == 1) {
                                isApiLastPage = true
                                activityList.clear()
                                teamTrackingListAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

        activityViewModel.getTeamAggregatedLiveData.observe(viewLifecycleOwner) {
            binding.slStatus.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { data ->
                    binding.tvActiveStaffCount.text = "${data.activeStaffCount ?: 0}"
                    binding.tvInActiveStaffCount.text = "${data.inactiveStaffCount ?: 0}"
                    binding.tvOnLeaveStaffStaffCount.text = "${data.leaveStaffCount ?: 0}"
                }
            } else {
                showToast("${it.message}")
            }
        }
    }

    override fun getStaffInfo(model: StaffTrackingActivityModules) {
        startActivity(
            Intent(requireContext(), DailySalesReportActivity::class.java)
                .putExtra(AppConstant.STAFF, model)
                .putExtra(AppConstant.DATE_FILTER, endDateRange)
        )
    }

    override fun changeDateRangeFilter(range: String, flag: Int) {
        dateRange = range
        rangeCount = flag

        startDateRange = ""
        endDateRange = ""

        activityList.clear()
        teamTrackingListAdapter.notifyDataSetChanged()

        teamTrackingListAdapter.changeDateRange(dateRange)

        getTeamTrackingActivity()

        binding.tvFilterRange.text = buildString {
            val formattedString =
                dateRange.substring(0, 1).uppercase(Locale.ROOT) + dateRange.substring(1)
                    .lowercase(Locale.ROOT)
            if (flag == 1 || flag == 2) {
                append(AppConstant.RANGE)
                append(AppConstant.THIS)
                append(formattedString)
            } else {
                append(AppConstant.RANGE)
                append(formattedString)
            }
        }
    }

    override fun dateRangeFilterWithCustomDate(
        startDate: String,
        endDate: String,
        dateRange: String
    ) {
        binding.tvFilterRange.text = buildString {
            append(AppConstant.RANGE)
            append(DateFormatHelper.convertStringToMonthFormat(startDate))
            append(AppConstant.RANGE_DATE)
            append(DateFormatHelper.convertStringToMonthFormat(endDate))
        }

        this.dateRange = dateRange
        rangeCount = 3
        startDateRange = startDate
        endDateRange = endDate

        teamTrackingListAdapter.changeDateRange(dateRange)

        activityList.clear()
        teamTrackingListAdapter.notifyDataSetChanged()
        currentPage = 1

        getTeamTrackingActivity()
    }

    override fun applySorting(sorting: StaffActivitySortingModel?) {
        staffActivitySortingModel = sorting
        getTeamTrackingActivity()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if (menuVisible) {
            staffActivityFilterModel = null
            binding.tvFilterCount.visibility = View.GONE
            filteredRoleList = ArrayList()
            filterReportingManager = null
            dateRange = AppConstant.TODAY
            rangeCount = 0
            binding.tvFilterRange.text = resources.getString(R.string.range_today)
            updateStartDate()
            getTeamTrackingActivity()
        }
    }
}