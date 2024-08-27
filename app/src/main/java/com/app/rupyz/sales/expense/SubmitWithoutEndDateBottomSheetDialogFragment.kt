package com.app.rupyz.sales.expense

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetSubmitWithoutEndDateBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class SubmitWithoutEndDateBottomSheetDialogFragment(
    var model: ExpenseTrackerDataItem?,
    var listener: IExpensesSubmitListener
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSubmitWithoutEndDateBinding
    private var mEndDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]

    private var startDate: String? = null
    private var endDate: String? = null

    private val myCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetSubmitWithoutEndDateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonProceed.text = resources.getString(R.string.submit)

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        startDate =
            DateFormatHelper.convertDateToIsoFormat(model?.startDateTime)

        if (model?.endDateTime != null) {
            endDate = DateFormatHelper.convertDateToIsoFormat(model?.endDateTime)
            binding.tvEndDate.text = DateFormatHelper.getMonthDate(model?.endDateTime)
        } else {
            endDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
            binding.tvEndDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }

        binding.tvEndDate.setOnClickListener {
            opeEndDateCalendar()
        }

        mEndDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateEndDate()
            }

        binding.buttonProceed.setOnClickListener {
            val isEndDateBeforeToStartDate = validateEndDate()
            if (isEndDateBeforeToStartDate) {
                Toast.makeText(
                    requireContext(),
                    "End date can not be less then start date!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                listener.expenseSubmit(endDate!!)
                dismiss()
            }
        }
    }


    private fun updateEndDate() {

        endDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)

        validateEndDate()
        val isEndDateBeforeToStartDate = validateEndDate()
        if (isEndDateBeforeToStartDate) {
            Toast.makeText(
                requireContext(),
                "End date can not be less then start date!!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            binding.tvEndDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }

    private fun validateEndDate(): Boolean {
        return DateFormatHelper.isDate1BeforeThenDate2(endDate!!, startDate)
    }

    private fun opeEndDateCalendar() {
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.ThemeOverlay_Material_Dialog,
            mEndDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.show()
    }

    interface IExpensesSubmitListener {
        fun expenseSubmit(endDate: String)
    }

}