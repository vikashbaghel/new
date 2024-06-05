package com.app.rupyz.sales.filter

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetAnalyticsFilterBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.DateFilterModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsFilterBottomSheetDialogFragment(
    private val analyticsFilter: IAnalyticsFilterListener,
    private var dateFilterModel: DateFilterModel,
) : BottomSheetDialogFragment(), DateFilterRvAdapter.IDateFilterSelectedListener {
    private lateinit var binding: BottomSheetAnalyticsFilterBinding

    private lateinit var dateFilterRvAdapter: DateFilterRvAdapter

    private var weekFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    private var monthFormat: DateFormat = SimpleDateFormat("MM-yyyy", Locale.ENGLISH)
    var currentYearFormat: DateFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)

    var monthDigitFormat: DateFormat = SimpleDateFormat("MM", Locale.ENGLISH)

    private var filter_list: ArrayList<DateFilterModel> = ArrayList()

    private var onStartDateSetListener: OnDateSetListener? = null
    private var onEndDateSetListener: OnDateSetListener? = null

    private var dateSelectedModel = DateFilterModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetAnalyticsFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFilterList()
        openDataPicker()

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            analyticsFilter.onFilterDate(dateSelectedModel)
            dismiss()
        }
    }

    private fun setFilterList(){
        val calendar: Calendar = Calendar.getInstance()

        val monthCalendar: Calendar = Calendar.getInstance()

        val currentMonth = monthFormat.format(calendar.time) ?: ""
        val currentMonthDigit = monthDigitFormat.format(calendar.time) ?: ""
        val currentYearDigit = currentYearFormat.format(calendar.time) ?: ""

        val newMonth = monthCalendar.time
        val lastMonth: String = monthFormat.format(newMonth)

        val monthModel = DateFilterModel()
        monthModel.title = AppConstant.CURRENT_MONTH
        monthModel.startDate = lastMonth
        monthModel.end_date = currentMonth
        monthModel.filter_type = AppConstant.MONTHLY

        filter_list.add(monthModel)

        monthCalendar.add(Calendar.MONTH, -2)
        val lastThreeMonth: String = monthFormat.format(monthCalendar.time) ?: ""

        val threeMonthModel = DateFilterModel()
        threeMonthModel.title = AppConstant.LAST_THREE_MONTH

        threeMonthModel.startDate = lastThreeMonth
        threeMonthModel.end_date = currentMonth
        threeMonthModel.filter_type = AppConstant.MONTHLY

        filter_list.add(threeMonthModel)

        monthCalendar.add(Calendar.MONTH, -3)
        val lastSixMonth: String = monthFormat.format(monthCalendar.time) ?: ""

        val sixMonthModel = DateFilterModel()
        sixMonthModel.title = AppConstant.LAST_SIX_MONTH
        sixMonthModel.startDate = lastSixMonth
        sixMonthModel.end_date = currentMonth
        sixMonthModel.filter_type = AppConstant.MONTHLY

        filter_list.add(sixMonthModel)

        monthCalendar.add(Calendar.MONTH, -6)
        val lastTwelveMonth: String = monthFormat.format(monthCalendar.time) ?: ""

        val twelveMonthModel = DateFilterModel()
        twelveMonthModel.title = AppConstant.LAST_TWELVE_MONTH
        twelveMonthModel.startDate = lastTwelveMonth
        twelveMonthModel.end_date = currentMonth
        twelveMonthModel.filter_type = AppConstant.MONTHLY

        filter_list.add(twelveMonthModel)

        var financialYearStartDate = ""
        var financialYearEndDate = ""
        if (currentMonthDigit.toInt() > 4) {
            val nextYear = currentYearDigit.toInt() + 1
            financialYearStartDate = "04-$currentYearDigit"
            financialYearEndDate = "03-$nextYear"
        } else {
            val startYear = currentYearDigit.toInt() - 1
            financialYearStartDate = "04-$startYear"
            financialYearEndDate = "03-$currentYearDigit"
        }

        val financialYearModel = DateFilterModel()
        financialYearModel.title = AppConstant.CURRENT_FY
        financialYearModel.startDate = financialYearStartDate
        financialYearModel.end_date = financialYearEndDate
        financialYearModel.filter_type = AppConstant.MONTHLY

        filter_list.add(financialYearModel)

        filter_list.forEachIndexed { _, fModel ->
            if (dateFilterModel.title.equals(fModel.title)){
                fModel.isSelected  = true
            }
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFilterRow.layoutManager = linearLayoutManager
        dateFilterRvAdapter = DateFilterRvAdapter(filter_list, this)
        binding.rvFilterRow.adapter = dateFilterRvAdapter
    }

    private fun openDataPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        binding.tvStartDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                onStartDateSetListener,
                year,
                month,
                day
            )
            datePickerDialog.datePicker.maxDate = calendar.time.time
            datePickerDialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            datePickerDialog.show()
        }

        binding.tvEndDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                onEndDateSetListener,
                year,
                month,
                day
            )
            datePickerDialog.datePicker.maxDate = calendar.time.time
            datePickerDialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }

        dateSelectedModel.title = AppConstant.CUSTOM
        dateSelectedModel.filter_type = AppConstant.CUSTOMER_DATE_FILTER
        onStartDateSetListener =
            OnDateSetListener { _: DatePicker?, year1: Int, month1: Int, _: Int ->
                val date = "$day-$month1-$year1"
                binding.tvStartDate.text = date
                dateSelectedModel.startDate = date

            }

        onEndDateSetListener =
            OnDateSetListener { _: DatePicker?, year1: Int, month1: Int, dayofmonth: Int ->
                val date = "$day-$month1-$year1"
                binding.tvEndDate.text = date
                dateSelectedModel.end_date = date
            }

    }

    override fun onDateSelected(model: DateFilterModel, position: Int) {
        dateSelectedModel = model
        filter_list.forEach { it.isSelected = false }
        filter_list[position].isSelected = true
        dateFilterRvAdapter.notifyDataSetChanged()
    }

}