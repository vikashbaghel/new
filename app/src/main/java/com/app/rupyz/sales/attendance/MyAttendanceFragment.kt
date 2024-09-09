package com.app.rupyz.sales.attendance

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentMyAttendaceBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AttendanceDataItem
import java.util.*

class MyAttendanceFragment : Fragment(),
    AttendanceListAdapter.IAttendanceActionListener {
    private lateinit var binding: FragmentMyAttendaceBinding
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var attendanceListAdapter: AttendanceListAdapter
    private var attendanceList = ArrayList<AttendanceDataItem>()

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var viewMonth = 1
    private var viewYear = 2023
    private var weekEndDayArray = arrayOf("Sat", "Sun")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyAttendaceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]

        myCalendar[year, month] = 1

        initRecyclerView()
        initObservers()

        mStartDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateStartDate()
            }

        viewMonth = DateFormatHelper.getMonth(myCalendar.time)
        viewYear = DateFormatHelper.getYear(myCalendar.time)
        getAttendance(viewMonth, viewYear)

        binding.ivDecrementMonth.setOnClickListener {
            myCalendar.add(Calendar.MONTH, -1)
            binding.tvDate.text = DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)

            viewMonth = DateFormatHelper.getMonth(myCalendar.time)
            viewYear = DateFormatHelper.getYear(myCalendar.time)
            getAttendance(viewMonth, viewYear)
        }

        binding.ivIncrementMonth.setOnClickListener {
            myCalendar.add(Calendar.MONTH, 1)
            binding.tvDate.text = DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)

            viewMonth = DateFormatHelper.getMonth(myCalendar.time)
            viewYear = DateFormatHelper.getYear(myCalendar.time)
            getAttendance(viewMonth, viewYear)
        }
    }

    private fun getAttendance(viewMonth: Int, viewYear: Int) {
        attendanceList.clear()

        printDatesInMonth(viewYear, viewMonth)

        binding.tvDate.text = DateFormatHelper.convertDateToMonthAndYearFormat(myCalendar.time)

        binding.progressBar.visibility = View.VISIBLE
        viewModel.getAttendanceList((viewMonth + 1).toString(), viewYear.toString())
    }


    private fun printDatesInMonth(year: Int, month: Int) {
        val cal = Calendar.getInstance()
        cal.clear()
        cal[year, month] = 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until daysInMonth) {
            val attendance = AttendanceDataItem()
            attendance.calenderDate = DateFormatHelper.convertDateToMonthWithoutYearFormat(cal.time)
            attendance.apiDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(cal.time)
            val day = DateFormatHelper.getDayOfWeek(cal.time)
            attendance.weekDay = day
            if (weekEndDayArray.contains(day)) {
                attendance.isWeekEnd = true
            }

            attendanceList.add(attendance)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        attendanceListAdapter.notifyDataSetChanged()
    }

    private fun updateStartDate() {
        viewMonth = DateFormatHelper.getMonth(myCalendar.time)
        viewYear = DateFormatHelper.getYear(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthWithoutYearFormat(myCalendar.time)
        getAttendance(viewMonth, viewYear)
    }

    private fun initObservers() {
        viewModel.attendanceListLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { data ->
                    if (data.isNotEmpty()) {
                        if (attendanceList.size > 0) {
                            attendanceList.forEachIndexed { index, attendanceDataItem ->

                                data.forEach { apiAttendance ->
                                    if (attendanceDataItem.calenderDate ==
                                        DateFormatHelper.convertStringToMonthFormat(
                                            apiAttendance.date
                                        )
                                    ) {
                                        apiAttendance.calenderDate = attendanceDataItem.calenderDate
                                        apiAttendance.weekDay = attendanceDataItem.weekDay
                                        apiAttendance.apiDate = attendanceDataItem.apiDate
                                        attendanceList[index] = apiAttendance
                                        attendanceListAdapter.notifyItemChanged(index)
                                    }
                                }
                            }
                        }
                    }

                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvAttendance.layoutManager = linearLayoutManager

        attendanceListAdapter =
            AttendanceListAdapter(
                attendanceList, this
            )

        binding.rvAttendance.adapter = attendanceListAdapter
    }


    override fun editAttendance(model: AttendanceDataItem, position: Int) {
        someActivityResultLauncher.launch(
            Intent(
                requireContext(),
                ChangeAttendanceActivity::class.java
            ).putExtra(AppConstant.ATTENDANCE, model)
        )
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            getAttendance(viewMonth, viewYear)
        }
    }
}