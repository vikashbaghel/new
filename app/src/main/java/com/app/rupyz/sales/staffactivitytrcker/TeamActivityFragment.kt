package com.app.rupyz.sales.staffactivitytrcker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentTeamActivityBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import java.util.*

class TeamActivityFragment : BaseFragment(), TeamTrackingListAdapter.ITeamTrackingListener {
    private lateinit var binding: FragmentTeamActivityBinding

    private lateinit var activityViewModel: StaffActivityViewModel
    private lateinit var teamTrackingListAdapter: TeamTrackingListAdapter
    private var activityList = ArrayList<StaffTrackingActivityModules>()
    private var filterDate: String = ""

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeamActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel = ViewModelProvider(this)[StaffActivityViewModel::class.java]

        myCalendar[year, month] = 1
        myCalendar.time = Calendar.getInstance().time

        initRecyclerview()
        initObserver()

        activityList.clear()
        teamTrackingListAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.VISIBLE
        getTrackingActivity()

        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(cal.time)

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
        binding.clEmptyData.visibility = View.GONE
        filterDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)

        activityList.clear()
        teamTrackingListAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.VISIBLE

        currentPage = 1
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

    private fun getTrackingActivity() {
        activityViewModel.getTeamTrackingActivity(filterDate, currentPage)
    }

    private fun initRecyclerview() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvTeamActivity.layoutManager = linearLayoutManager
        teamTrackingListAdapter = TeamTrackingListAdapter(activityList, this)
        binding.rvTeamActivity.adapter = teamTrackingListAdapter

        binding.rvTeamActivity.addOnScrollListener(object :
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


    private fun initObserver() {

        activityViewModel.teamTrackingDetailsLiveData.observe(requireActivity()) {
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                enableTouch()
                if (it.error == false) {
                    isPageLoading = false
                    if (!it.data.isNullOrEmpty()) {
                        activityList.addAll(it.data)
                        teamTrackingListAdapter.notifyDataSetChanged()

                        if (it.data.size < 30) {
                            isApiLastPage = true
                        }
                    } else {
                        if (currentPage == 1) {
                            isApiLastPage = true
                            binding.clEmptyData.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun getStaffInfo(model: StaffTrackingActivityModules) {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.STAFF_ID, model.staffId
            ).putExtra(AppConstant.DATE_FILTER, filterDate)
        )
    }


}