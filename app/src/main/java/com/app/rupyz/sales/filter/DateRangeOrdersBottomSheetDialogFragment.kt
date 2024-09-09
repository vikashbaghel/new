package com.app.rupyz.sales.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetDateRangeOrdersBinding
import com.app.rupyz.databinding.BottomSheetDateRangeSelectBinding
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateRangeOrdersBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: BottomSheetDateRangeOrdersBinding

    companion object {
        private lateinit var listener: IDateRangeFilterListener

        private var dateRange: String = ""
        private var selectItem: Int? = null

        @JvmStatic
        fun newInstance(
            listener1: IDateRangeFilterListener,
            dateRange: String,
            selectItem: Int,
        ): DateRangeOrdersBottomSheetDialogFragment {
            val fragment = DateRangeOrdersBottomSheetDialogFragment()
            this.dateRange = dateRange
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
        binding = BottomSheetDateRangeOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCustomRange.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)


    }


    private fun showDatePickerDialog() {

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
                    listener.dateRangeFilter(startDateRange, endDateRange, AppConstant.CUSTOM_RANGE)
                    dismiss()

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

        fun dateRangeFilter(startDate: String, endDate: String, dateRange: String)
    }


    override fun onClick(p0: View?) {
        when (p0) {
            binding.tvCustomRange -> {
                showDatePickerDialog()
            }

            binding.ivBack -> {
                dismiss()
            }
        }
    }

}