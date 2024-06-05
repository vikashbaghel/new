package com.app.rupyz.ui.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityUserInfoBinding
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var orgViewModel: OrgViewModel
    private var isDataChange: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orgViewModel = ViewModelProvider(this)[OrgViewModel::class.java]

        initObservers()

        orgViewModel.getInfo()

        binding.imgClose.setOnClickListener {
            onBackPressed()
        }

        binding.ivEdit.setOnClickListener {
            someActivityResultLauncher.launch(Intent(this, UpdateUserInfoActivity::class.java))
        }
    }


    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataChange =  true
            orgViewModel.getInfo()
        }
    }


    private fun initObservers() {
        orgViewModel.getLiveData().observe(this) { it ->
            binding.progressBar.visibility = View.GONE
            it.data?.let { data ->
                inflateData(data)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun inflateData(it: OrgProfileDetail) {
        if (!it.primaryGstin.isNullOrEmpty()) {
            binding.tvGst.text = it.primaryGstin
        }

        if (!it.legalName.isNullOrEmpty()) {
            binding.tvCompanyName.text = it.legalName
        }

        if (!it.mobile.isNullOrEmpty()) {
            binding.tvMobile.text = it.mobile
        }

        if (!it.email.isNullOrEmpty()) {
            binding.tvEmail.text = it.email
        }

        if (!it.addressLine1.isNullOrEmpty()) {
            binding.tvCompanyLocation.text = it.addressLine1
        }

        val name = it.first_name + " " + it.last_name
        binding.tvUser.text = name.replace("null", "").trim() + " (Admin)"
    }

    override fun onBackPressed() {
        if (isDataChange){
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
       finish()
    }
}