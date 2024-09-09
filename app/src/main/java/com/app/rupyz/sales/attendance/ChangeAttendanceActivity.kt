package com.app.rupyz.sales.attendance

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityChangeAttendanceBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AttendanceDataItem
import java.util.*


class ChangeAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeAttendanceBinding
    private lateinit var viewModel: AttendanceViewModel

    private var selectedStatus = ""
    private var checkInTime = ""
    private var checkOutTime = ""
    private lateinit var attendanceDataItem: AttendanceDataItem

    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]

        initLayout()

        if (intent.hasExtra(AppConstant.ATTENDANCE)) {
            attendanceDataItem = intent.getParcelableExtra(AppConstant.ATTENDANCE)!!

            binding.btnCancel.isEnabled = true

            if (attendanceDataItem.apiDate != null) {
                date = attendanceDataItem.apiDate

                binding.tvAttendanceDate.text = resources.getString(
                    R.string.change_attendance_str,
                    DateFormatHelper.getMonthDate(date)
                )
            }

            if (attendanceDataItem.attendanceType != null) {
                val index = listOf(*resources.getStringArray(R.array.attendance_status))
                    .indexOf(attendanceDataItem.attendanceType)
                binding.spinnerStatus.setSelection(index)
            } else {
                binding.btnCancel.isEnabled = false
                binding.btnCancel.setBackgroundColor(resources.getColor(R.color.gray))
            }

            if (attendanceDataItem.timeIn != null) {
                checkInTime = attendanceDataItem.timeIn!!
                binding.tvCheckIn.text =
                    DateFormatHelper.convertIsoToDateAndTimeFormat(attendanceDataItem.timeIn)
            }

            if (attendanceDataItem.timeOut != null) {
                checkOutTime = attendanceDataItem.timeOut!!
                binding.tvCheckOut.text =
                    DateFormatHelper.convertIsoToDateAndTimeFormat(attendanceDataItem.timeOut)
            }

            if (!attendanceDataItem.comments.isNullOrEmpty()) {
                binding.etRemark.setText(attendanceDataItem.comments)
            }

            binding.btnCancel.setOnClickListener {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.deleteAttendance(attendanceDataItem.id!!)
            }
        }

        initObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {
        val arrayAdapter = ArrayAdapter(
            this, R.layout.single_text_view_spinner_16dp_text,
            resources.getStringArray(R.array.attendance_status)
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = arrayAdapter

        binding.spinnerStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedStatus = binding.spinnerStatus.selectedItem.toString()
                }
            }


        binding.tvCheckIn.setOnClickListener {
            val currentDate = Calendar.getInstance()
            currentDate.time = DateFormatHelper.convertStringToDate(date)
            val date = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            date.set(Calendar.MINUTE, minute)
                            binding.tvCheckIn.text =
                                DateFormatHelper.convertStringToDateAndTimeFormat(date.time)
                            checkInTime = DateFormatHelper.convertDateToIsoFormat(date.time)
                        }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false
                    ).show()
                },
                currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE]
            ).show()
        }

        binding.tvCheckOut.setOnClickListener {
            val currentDate = Calendar.getInstance()
            currentDate.time = DateFormatHelper.convertStringToDate(date)
            val date = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            date.set(Calendar.MINUTE, minute)
                            binding.tvCheckOut.text =
                                DateFormatHelper.convertStringToDateAndTimeFormat(date.time)
                            checkOutTime = DateFormatHelper.convertDateToIsoFormat(date.time)
                        }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false
                    ).show()
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE]
            ).show()
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }

        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun validateData() {
        val model = AttendanceDataItem()
        model.attendanceType = selectedStatus
        model.date = date
        model.timeIn = checkInTime
        model.timeOut = checkOutTime
        model.comments = binding.etRemark.text.toString()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateAttendance(model)
    }

    private fun initObservers() {
        viewModel.updateAttendanceLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

}