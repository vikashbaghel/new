package com.app.rupyz.ui.more

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityReportSettingBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.model_kt.PreferenceData

class ReportSettingActivity : AppCompatActivity(), SettingReportRvAdapter.IReportSettingListener {
    private lateinit var binding: ActivityReportSettingBinding
    private var reportDuration = ""
    private lateinit var whatsappAdapter: SettingReportRvAdapter
    private lateinit var emailIdAdapter: SettingReportRvAdapter

    private var dailyWhatsappNumberList: ArrayList<String> = ArrayList()
    private var weeklyWhatsappNumberList: ArrayList<String> = ArrayList()
    private var monthlyWhatsappNumberList: ArrayList<String> = ArrayList()

    private var whatsappNumberList: ArrayList<String> = ArrayList()

    private var dailyEmailIdList: ArrayList<String> = ArrayList()
    private var weeklyEmailIdList: ArrayList<String> = ArrayList()
    private var monthlyEmailIdList: ArrayList<String> = ArrayList()

    private var emailIdList: ArrayList<String> = ArrayList()

    private lateinit var moreViewModel: MoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        initLayout()
        initRecyclerView()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE
        moreViewModel.getPreferencesInfo()

        binding.btnAddWhatsappNumber.setOnClickListener {
            if (binding.etWhatsappNumber.text.toString().isEmpty()
                    .not() && Validations().isValidMobileNumber(binding.etWhatsappNumber.text.toString())
                    .not()
            ) {
                Toast.makeText(this, "Please enter valid mobile number!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                whatsappNumberList.add(binding.etWhatsappNumber.text.toString())
                whatsappAdapter.notifyItemInserted(whatsappNumberList.size - 1)
                whatsappAdapter.notifyItemRangeChanged(
                    whatsappNumberList.size - 1,
                    whatsappNumberList.size
                )
                binding.etWhatsappNumber.setText("")
            }
        }

        binding.btnAddEmailId.setOnClickListener {
            if (binding.etEmailId.text.toString().isEmpty()
                    .not() && Validations().isValidEmail(binding.etEmailId.text.toString())
                    .not()
            ) {
                Toast.makeText(this, "Please enter valid email address!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                emailIdList.add(binding.etEmailId.text.toString())
                emailIdAdapter.notifyItemInserted(emailIdList.size - 1)
                emailIdAdapter.notifyItemRangeChanged(
                    emailIdList.size - 1,
                    emailIdList.size
                )

                binding.etEmailId.setText("")
            }
        }

        binding.btnSave.setOnClickListener {

            val preferenceModel = PreferenceData()

            if (reportDuration.lowercase() == AppConstant.DAILY.lowercase()) {
                if (whatsappNumberList.isEmpty().not()) {
                    preferenceModel.dailyReportWhatsappMobiles = whatsappNumberList
                }
                if (emailIdList.isEmpty().not()) {
                    preferenceModel.dailyReportEmailAddresses = emailIdList
                }
            }
            if (reportDuration.lowercase() == AppConstant.WEEKLY.lowercase()) {
                if (whatsappNumberList.isEmpty().not()) {
                    preferenceModel.weeklyReportWhatsappMobiles = whatsappNumberList
                }
                if (emailIdList.isEmpty().not()) {
                    preferenceModel.weeklyReportEmailAddresses = emailIdList
                }
            }
            if (reportDuration.lowercase() == AppConstant.MONTHLY.lowercase()) {
                if (whatsappNumberList.isEmpty().not()) {
                    preferenceModel.monthlyReportWhatsappMobiles = whatsappNumberList
                }
                if (emailIdList.isEmpty().not()) {
                    preferenceModel.monthlyReportEmailAddresses = emailIdList
                }
            }

            binding.progressBar.visibility = View.VISIBLE
            moreViewModel.updatePreferences(preferenceModel)
        }

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        binding.rvWhatsappNumber.layoutManager = LinearLayoutManager(this)
        whatsappAdapter = SettingReportRvAdapter(whatsappNumberList, AppConstant.WHATSAPP, this)
        binding.rvWhatsappNumber.adapter = whatsappAdapter

        binding.rvEmailId.layoutManager = LinearLayoutManager(this)
        emailIdAdapter = SettingReportRvAdapter(emailIdList, AppConstant.EMAIL, this)
        binding.rvEmailId.adapter = emailIdAdapter

    }

    private fun initLayout() {
        binding.spinnerReportDuration.adapter = ArrayAdapter(
            this, R.layout.single_text_view_spinner_16dp_text,
            this.resources.getStringArray(R.array.report_duration)
        )

        binding.spinnerReportDuration.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    reportDuration = binding.spinnerReportDuration.selectedItem.toString()

                    if (reportDuration.lowercase() == AppConstant.DAILY.lowercase()) {
                        whatsappNumberList.clear()
                        whatsappNumberList.addAll(dailyWhatsappNumberList)
                        whatsappAdapter.notifyDataSetChanged()

                        emailIdList.clear()
                        emailIdList.addAll(dailyEmailIdList)
                        emailIdAdapter.notifyDataSetChanged()

                    } else if (reportDuration.lowercase() == AppConstant.WEEKLY.lowercase()) {
                        whatsappNumberList.clear()
                        whatsappNumberList.addAll(weeklyWhatsappNumberList)
                        whatsappAdapter.notifyDataSetChanged()

                        emailIdList.clear()
                        emailIdList.addAll(weeklyEmailIdList)
                        emailIdAdapter.notifyDataSetChanged()
                    } else if (reportDuration.lowercase() == AppConstant.MONTHLY.lowercase()) {
                        whatsappNumberList.clear()
                        whatsappNumberList.addAll(monthlyWhatsappNumberList)
                        whatsappAdapter.notifyDataSetChanged()

                        emailIdList.clear()
                        emailIdList.addAll(monthlyEmailIdList)
                        emailIdAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun initObservers() {
        moreViewModel.preferenceLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { preferenceData ->

                    if (preferenceData.dailyReportWhatsappMobiles.isNullOrEmpty().not()) {
                        dailyWhatsappNumberList.addAll(preferenceData.dailyReportWhatsappMobiles!!)
                        whatsappNumberList.addAll(dailyWhatsappNumberList)
                        whatsappAdapter.notifyDataSetChanged()
                    }
                    if (preferenceData.weeklyReportWhatsappMobiles.isNullOrEmpty().not()) {
                        weeklyWhatsappNumberList.addAll(preferenceData.weeklyReportWhatsappMobiles!!)
                    }
                    if (preferenceData.monthlyReportWhatsappMobiles.isNullOrEmpty().not()) {
                        monthlyWhatsappNumberList.addAll(preferenceData.monthlyReportWhatsappMobiles!!)
                    }

                    if (preferenceData.dailyReportEmailAddresses.isNullOrEmpty().not()) {
                        dailyEmailIdList.addAll(preferenceData.dailyReportEmailAddresses!!)
                        emailIdList.addAll(dailyEmailIdList)
                        emailIdAdapter.notifyDataSetChanged()
                    }
                    if (preferenceData.weeklyReportEmailAddresses.isNullOrEmpty().not()) {
                        weeklyEmailIdList.addAll(preferenceData.weeklyReportEmailAddresses!!)
                    }
                    if (preferenceData.monthlyReportEmailAddresses.isNullOrEmpty().not()) {
                        monthlyEmailIdList.addAll(preferenceData.monthlyReportEmailAddresses!!)
                    }
                }
            } else {
                Toast.makeText(this, "${data.message}", Toast.LENGTH_SHORT).show()
            }
        }

        moreViewModel.updatePreferenceLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                finish()
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun removeItem(type: String, position: Int) {
        if (type == AppConstant.WHATSAPP) {
            if (whatsappNumberList.size > 0) {
                whatsappNumberList.removeAt(position)
                whatsappAdapter.notifyDataSetChanged()
            }
        } else if (type == AppConstant.EMAIL) {
            emailIdList.removeAt(position)
            emailIdAdapter.notifyDataSetChanged()
        }
    }
}