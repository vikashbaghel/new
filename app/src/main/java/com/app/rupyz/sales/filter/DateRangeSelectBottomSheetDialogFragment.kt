package com.app.rupyz.sales.filter

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetDateRangeSelectBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateRangeSelectBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: BottomSheetDateRangeSelectBinding

    companion object {
        private lateinit var listener: IDateRangeFilterListener
        private var selectItem: Int? = null

        @JvmStatic
        fun newInstance(
            listener1: IDateRangeFilterListener,
            selectItem: Int,
        ): DateRangeSelectBottomSheetDialogFragment {
            val fragment = DateRangeSelectBottomSheetDialogFragment()
            this.selectItem = selectItem
            listener = listener1
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetDateRangeSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (selectItem) {
            0 -> {
                binding.tvToday.background = ContextCompat.getDrawable(
                    requireActivity(), R.drawable.blue_stroke_1_dp_empty_bg
                )
                binding.tvToday.setTextColor(
                    ContextCompat.getColor(
                        MyApplication.instance, R.color.theme_color
                    )
                )

            }

            1 -> {
                binding.tvThisWeek.background = ContextCompat.getDrawable(
                    requireActivity(), R.drawable.blue_stroke_1_dp_empty_bg
                )
                binding.tvThisWeek.setTextColor(
                    ContextCompat.getColor(
                        MyApplication.instance, R.color.theme_color
                    )
                )

            }

            2 -> {
                binding.tvThisMonth.background = ContextCompat.getDrawable(
                    requireActivity(), R.drawable.blue_stroke_1_dp_empty_bg
                )
                binding.tvThisMonth.setTextColor(
                    ContextCompat.getColor(
                        MyApplication.instance, R.color.theme_color
                    )
                )

            }

            3 -> {
                binding.tvCustomRange.background = ContextCompat.getDrawable(
                    requireActivity(), R.drawable.blue_stroke_1_dp_empty_bg
                )
                binding.tvCustomRange.setTextColor(
                    ContextCompat.getColor(
                        MyApplication.instance, R.color.theme_color
                    )
                )

            }
        }
        binding.tvToday.setOnClickListener(this)
        binding.tvThisWeek.setOnClickListener(this)
        binding.tvThisMonth.setOnClickListener(this)
        binding.tvCustomRange.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        // Create CalendarConstraints to set max date to today .setEnd(today)
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.now()
        )


        // Creating a MaterialDatePicker builder for selecting a date range
        val builder: Builder<Pair<Long, Long>> = Builder.dateRangePicker()
        builder.setTitleText(AppConstant.SELECT_DATE)
        builder.setCalendarConstraints(constraintsBuilder.build())

        // Building the date picker dialog
        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->

            selection?.let {
                // Retrieve the selected start and end dates
                val startDate = Date(it.first)
                val endDate = Date(it.second)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // Perform date formatting using coroutines
                CoroutineScope(Dispatchers.Main).launch {
                    val startDateRange = formatDatesAsync(startDate, sdf)
                    val endDateRange = formatDatesAsync(endDate, sdf)

                    if (DateFormatHelper.isDateBetweenOneMonth(startDateRange, endDateRange)) {
                        Log.e("DEBUG", "date range is one month")
                        listener.dateRangeFilterWithCustomDate(
                            startDateRange,
                            endDateRange,
                            AppConstant.CUSTOM_RANGE
                        )
                        dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Date range can not be more then 1 month",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Showing the date picker dialog
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    // Utility function to format dates asynchronously using coroutines
    private suspend fun formatDatesAsync(startDate: Date, sdf: SimpleDateFormat): String {
        return withContext(Dispatchers.IO) {
            val startDateString = sdf.format(startDate)
            //val endDateString = sdf.format(endDate)
            startDateString
        }
    }

    interface IDateRangeFilterListener {
        fun changeDateRangeFilter(range: String, flag: Int)
        fun dateRangeFilterWithCustomDate(startDate: String, endDate: String, dateRange: String)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(p0: View?) {
        when (p0) {
            binding.tvToday -> {
                selectItem = 0
                listener.changeDateRangeFilter(AppConstant.TODAY, selectItem!!)
                dismiss()

            }

            binding.tvThisWeek -> {
                selectItem = 1
                listener.changeDateRangeFilter(AppConstant.THIS_WEEK, selectItem!!)
                dismiss()

            }

            binding.tvThisMonth -> {
                selectItem = 2
                listener.changeDateRangeFilter(AppConstant.THIS_MONTH, selectItem!!)
                dismiss()
            }

            binding.tvCustomRange -> {
                selectItem = 3
                showDatePickerDialog()
            }

            binding.ivBack -> {
                dismiss()
            }
        }
    }

}