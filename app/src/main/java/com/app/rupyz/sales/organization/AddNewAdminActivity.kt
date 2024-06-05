package com.app.rupyz.sales.organization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewAdminBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddNewAdminModel

class AddNewAdminActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNewAdminBinding
    private lateinit var organizationViewModel: OrganizationViewModel

    private lateinit var addNewAdminModel: AddNewAdminModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAdminBinding.inflate(layoutInflater)
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
        organizationViewModel.addNewAdminLiveData.observe(this) {
            ButtonStyleHelper(this).initDisableButton(
                true,
                binding.btnContinue,
                resources.getString(R.string.str_continue)
            )

            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                if (it.data?.otpRef != null) {
                    addNewAdminModel.otpRef = it.data?.otpRef
                    startActivity(
                        Intent(
                            this, OtpForAddOrganizationActivity::class.java
                        ).putExtra(AppConstant.NEW_ADMIN, addNewAdminModel)
                            .putExtra(AppConstant.OTP_REF, it.data?.otpRef)
                    )
                }
            }
        }
    }

    private fun validateData() {
        when {
            binding.etFirstName.text.trim().toString().isEmpty() -> {
                showToast("First Name Required!")
            }

            binding.etMobileNumber.text.trim().toString().isEmpty() -> {
                showToast("Mobile Number Required!")
            }

            binding.etMobileNumber.text.trim().toString().length < 10 -> {
                showToast("Valid Mobile Number Required!")
            }

            else -> {

                Utils.hideKeyboard(this)
                ButtonStyleHelper(this).initDisableButton(false, binding.btnContinue, "Loading...")

                addNewAdminModel = AddNewAdminModel()
                addNewAdminModel.firstName = binding.etFirstName.text.toString()
                addNewAdminModel.lastName = binding.etLastName.text.toString()
                addNewAdminModel.mobile = binding.etMobileNumber.text.toString()
                addNewAdminModel.email = binding.etEmailId.text.toString()

                organizationViewModel.addNewAdmin(addNewAdminModel)

            }
        }
    }
}