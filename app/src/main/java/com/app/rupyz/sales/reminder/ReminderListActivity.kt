package com.app.rupyz.sales.reminder

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.databinding.ActivityReminderListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.google.android.material.tabs.TabLayout
import java.util.*

class ReminderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderListBinding
    private lateinit var reminderListPagerAdapter: ReminderListFragmentPagerAdapter

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTabLayout()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivReminderCalender.setOnClickListener {
            openDateCalendar()
        }

        mStartDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                openReminderForThisDay()
            }
    }

    private fun openReminderForThisDay() {
        val tempDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)

        startActivity(
            Intent(
                this,
                FragmentContainerActivity::class.java
            ).putExtra(AppConstant.REMINDERS, true).putExtra(AppConstant.DATE_FILTER, tempDate)
        )
    }

    private fun openDateCalendar() {
        val dialog = DatePickerDialog(
            this,
            android.R.style.ThemeOverlay_Material_Dialog,
            mStartDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.datePicker.minDate =
            Calendar.getInstance().time.time
        dialog.show()
    }


    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.TODAY))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.TOMORROW))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.THIS_WEEK))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.THIS_MONTH))

        val fragmentList = arrayListOf(
            AppConstant.TODAY,
            AppConstant.TOMORROW,
            AppConstant.THIS_WEEK,
            AppConstant.THIS_MONTH
        )

        reminderListPagerAdapter = ReminderListFragmentPagerAdapter(
            this, fragmentList
        )

        binding.viewPager.adapter = reminderListPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }
}