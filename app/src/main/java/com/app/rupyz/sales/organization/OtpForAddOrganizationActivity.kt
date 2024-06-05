package com.app.rupyz.sales.organization

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOtpForAddOrganizationBinding
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_INFO
import com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddNewAdminModel
import com.app.rupyz.model_kt.AddOrganizationModel
import com.app.rupyz.sales.home.SalesMainActivity

class OtpForAddOrganizationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpForAddOrganizationBinding
    private lateinit var organizationViewModel: OrganizationViewModel

    private lateinit var addOrganizationModel: AddOrganizationModel
    private lateinit var addNewAdminModel: AddNewAdminModel
    private var otpRef: String? = null
    private var mUtil: Utility? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpForAddOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]
        mUtil = Utility(this)

        initObservers()

        initTimer()

        if (intent.hasExtra(AppConstant.ORGANIZATION)) {
            addOrganizationModel = intent.getParcelableExtra(AppConstant.ORGANIZATION)!!

            if (!addOrganizationModel.mobile.isNullOrEmpty()) {
                binding.tvMobileNumber.text = addOrganizationModel.mobile
            }
        }

        if (intent.hasExtra(AppConstant.NEW_ADMIN)) {
            addNewAdminModel = intent.getParcelableExtra(AppConstant.NEW_ADMIN)!!

            if (addNewAdminModel.mobile.isNullOrEmpty().not()) {
                binding.tvMobileNumber.text = addNewAdminModel.mobile
            }
        }

        if (intent.hasExtra(AppConstant.OTP_REF)) {
            otpRef = intent.getStringExtra(AppConstant.OTP_REF)
        }

        binding.btnResend.setOnClickListener {
            if (intent.hasExtra(AppConstant.ORGANIZATION)) {
                organizationViewModel.addOrganization(addOrganizationModel)
            } else if (intent.hasExtra(AppConstant.NEW_ADMIN)) {
                organizationViewModel.addNewAdmin(addNewAdminModel)
            }
        }

        binding.btnVerify.setOnClickListener {
            validateData()
        }

        binding.imgClose.setOnClickListener {
            finish()
        }

        binding.etOtp.otpListener = object : OTPListener {
            override fun onInteractionListener() {}
            override fun onOTPComplete(otp: String) {
                Utils.hideKeyboard(this@OtpForAddOrganizationActivity)
            }
        }

    }

    private fun validateData() {
        if (binding.etOtp.otp.isEmpty()) {
            Toast.makeText(this, "OTP Required", Toast.LENGTH_SHORT).show()
        } else if (binding.etOtp.otp.length == 4) {
            ButtonStyleHelper(this).initDisableButton(false, binding.btnVerify, "Please wait...")

            if (intent.hasExtra(AppConstant.ORGANIZATION)) {
                addOrganizationModel.otp = binding.etOtp.otp
                addOrganizationModel.otpRef = otpRef

                organizationViewModel.verifyOrganization(addOrganizationModel)
            } else if (intent.hasExtra(AppConstant.NEW_ADMIN)) {
                addNewAdminModel.otp = binding.etOtp.otp
                addNewAdminModel.otpRef = otpRef

                organizationViewModel.verifyNewAdmin(addNewAdminModel)
            }
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initObservers() {
        organizationViewModel.addOrgLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                initTimer()
            }
        }

        organizationViewModel.addNewAdminLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                initTimer()
            }
        }

        organizationViewModel.verifyOrgLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                organizationViewModel.getProfileInfo()
            } else {
                ButtonStyleHelper(this).initDisableButton(
                    true,
                    binding.btnVerify,
                    resources.getString(R.string.verify)
                )
            }
        }

        organizationViewModel.verifyNewAdminLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                startActivity(
                    Intent(this, SalesMainActivity::class.java).addFlags(
                        FLAG_ACTIVITY_CLEAR_TOP
                    )
                )
                finish()
            } else {
                ButtonStyleHelper(this).initDisableButton(
                    true,
                    binding.btnVerify,
                    resources.getString(R.string.verify)
                )
            }
        }

        organizationViewModel.profileLiveData.observe(this) {
            if (it.error == false) {

                it.data?.let { mData ->
                    if (mData.orgIds.size > 0) {
                        mData.orgIds[0].isSelected = true
                        SharedPref.getInstance().putModelClass(ORG_INFO, mData.orgIds[0])
                    }

                    SharedPref.getInstance().putModelClass(USER_INFO, mData)

                    startActivity(
                        Intent(this, SalesMainActivity::class.java).addFlags(
                            FLAG_ACTIVITY_CLEAR_TOP
                        )
                    )
                    finish()
                }

            } else {
                ButtonStyleHelper(this).initDisableButton(
                    true,
                    binding.btnVerify,
                    resources.getString(R.string.verify)
                )
            }
        }
    }

    private fun initTimer() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 > 0) {
                    binding.clCounterLayout.visibility = View.VISIBLE
                    binding.tvCounter.text = "" + millisUntilFinished / 1000
                    binding.clResentOtp.visibility = View.GONE
                }
            }

            override fun onFinish() {
                binding.clCounterLayout.visibility = View.GONE
                binding.clResentOtp.visibility = View.VISIBLE
            }
        }.start()
    }
}