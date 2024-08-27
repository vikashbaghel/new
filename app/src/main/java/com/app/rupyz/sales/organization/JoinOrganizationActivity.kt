package com.app.rupyz.sales.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityJoinOrganizationBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.model_kt.AddOrganizationModel

class JoinOrganizationActivity : BaseActivity() {
    private lateinit var binding: ActivityJoinOrganizationBinding
    private lateinit var organizationViewModel: OrganizationViewModel

    private lateinit var addOrganizationModel: AddOrganizationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]

        initObservers()

        binding.btnContinue.setOnClickListener {
            validateData()
        }

        binding.imgClose.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun initObservers() {
        organizationViewModel.addOrgLiveData.observe(this) {
            ButtonStyleHelper(this).initDisableButton(
                true,
                binding.btnContinue,
                resources.getString(R.string.str_continue)
            )

            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                startActivity(
                    Intent(
                        this, OtpForAddOrganizationActivity::class.java
                    ).putExtra(AppConstant.ORGANIZATION, addOrganizationModel)
                        .putExtra(AppConstant.OTP_REF, it.data?.otpRef)
                )
            }
        }
    }

    private fun validateData() {
        when {
            binding.etOrgName.text.trim().toString().isEmpty() -> {
               showToast("Organization Name Required!")
            }

            binding.etPan.text.trim().toString().isEmpty() -> {
               showToast("PAN Required!")
            }

            binding.etPan.text.toString()
                .isNotEmpty() && !Validations().panValidation(binding.etPan.text.toString()) -> {

               showToast("Enter Valid PAN Number")
            }

            binding.etMobileNumber.text.trim().toString().isEmpty() -> {
               showToast("Mobile Number Required!")
            }

            binding.etMobileNumber.text.trim().toString().length < 10 -> {
               showToast("Valid Mobile Number Required!")
            }

            else -> {
                val model = AddOrganizationModel()
                model.legalName = binding.etOrgName.text.toString()
                model.mobile = binding.etMobileNumber.text.toString()
                model.panId = binding.etPan.text.toString().uppercase()

                addOrganizationModel = model

                ButtonStyleHelper(this).initDisableButton(false, binding.btnContinue, "Loading...")

                organizationViewModel.addOrganization(model)

            }
        }
    }
}