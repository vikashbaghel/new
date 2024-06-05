package com.app.rupyz.sales.expense

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddExpenseHeadBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import java.util.Calendar

class AddExpenseHeadActivity : BaseActivity() {
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var binding: ActivityAddExpenseHeadBinding
    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var mEndDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private val myCalendar = Calendar.getInstance()

    private var startDate: String? = null
    private var endDate: String? = null

    private var expenseTrackerModel: ExpenseTrackerDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseHeadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        initLayout()
        initObserver()

        mStartDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateStartDate()
            }

        mEndDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day

                if (startDate == null) {
                    Toast.makeText(this, "Please enter end Date first!!", Toast.LENGTH_SHORT).show()
                } else {
                    updateEndDate()
                }
            }

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObserver() {
        viewModel.addTotalExpenseLiveData.observe(this) {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        viewModel.updateExpenseStatusLiveData.observe(this) {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun initLayout() {

        if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {

            binding.tvToolbarTitle.text = resources.getString(R.string.update_expense_head)
            binding.btnAdd.text = resources.getString(R.string.update)

            expenseTrackerModel = intent.getParcelableExtra(AppConstant.EXPENSE_DETAILS)

            if (expenseTrackerModel != null) {

                if (expenseTrackerModel?.name != null) {
                    binding.etExpenseHead.setText(expenseTrackerModel?.name)
                }

                if (expenseTrackerModel?.description != null) {
                    binding.etDescription.setText(expenseTrackerModel?.description)
                }
                if (expenseTrackerModel?.startDateTime != null) {
                    startDate =
                        DateFormatHelper.convertDateToIsoFormat(expenseTrackerModel?.startDateTime)
                    binding.tvStartDate.text =
                        DateFormatHelper.getMonthDate(expenseTrackerModel?.startDateTime)
                }
                if (expenseTrackerModel?.endDateTime != null) {
                    endDate =
                        DateFormatHelper.convertDateToIsoFormat(expenseTrackerModel?.endDateTime)
                    binding.tvEndDate.text =
                        DateFormatHelper.getMonthDate(expenseTrackerModel?.endDateTime)
                }

            }
        }
        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            Utils.hideKeyboard(this)
            validateData()
        }

        binding.tvStartDate.setOnClickListener {
            Utils.hideKeyboard(this)
            openStartDateCalendar()
        }

        binding.tvEndDate.setOnClickListener {
            Utils.hideKeyboard(this)
            opeEndDateCalendar()
        }
    }

    private fun updateStartDate() {
        val tempStartDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
        if (endDate != null && DateFormatHelper.isDate1BeforeThenDate2(endDate!!, tempStartDate)) {
            Toast.makeText(this, "End date can not be less then start date!!", Toast.LENGTH_SHORT)
                .show()
        } else {
            startDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
            binding.tvStartDate.text =
                DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }

    private fun updateEndDate() {
        val tempEndDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)

        if (startDate != null && DateFormatHelper.isDate1BeforeThenDate2(
                tempEndDate!!,
                startDate
            )
        ) {
            Toast.makeText(this, "End date can not be less then start date!!", Toast.LENGTH_SHORT)
                .show()
        } else {
            endDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
            binding.tvEndDate.text =
                DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }


    private fun openStartDateCalendar() {
        val dialog = DatePickerDialog(
            this,
            android.R.style.ThemeOverlay_Material_Dialog,
            mStartDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.show()
    }

    private fun opeEndDateCalendar() {
        val dialog = DatePickerDialog(
            this,
            android.R.style.ThemeOverlay_Material_Dialog,
            mEndDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.show()
    }


    private fun validateData() {
        when {
            binding.etExpenseHead.text.trim().toString().isEmpty() -> {
                showToast("Expense Head Required!")
            }
            startDate == null -> {
                showToast("Start Date Required!")
            }
            else -> {
                val expenseModel = ExpenseTrackerDataItem()
                expenseModel.name = binding.etExpenseHead.text.toString()
                expenseModel.startDateTime = startDate
                expenseModel.endDateTime = endDate
                expenseModel.description = binding.etDescription.text.toString()

                binding.progressBar.visibility = View.VISIBLE

                if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {
                    viewModel.updateTotalExpenses(expenseTrackerModel?.id!!, expenseModel)
                } else {
                    viewModel.addTotalExpense(expenseModel, hasInternetConnection())
                }
            }
        }
    }
}