package com.app.rupyz.sales.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.databinding.ActivityAddCategoryBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.lead.LeadViewModel
import com.google.gson.JsonObject


class AddCategoryActivity : BaseActivity() {
    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        Utility(this)

        initObservers()

        binding.btnAdd.setOnClickListener {
            submitData()
        }
        binding.btnCancel.setOnClickListener { finish() }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun submitData() {
        when {
            binding.etName.text.trim().toString().isEmpty() -> {
                Utils.hideKeyboard(this)
                showToast("Name Required!")
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE

                val jsonObject = JsonObject()
                jsonObject.addProperty("name", binding.etName.text.toString())

                if (intent.hasExtra(AppConstant.LEAD_CATEGORY)) {
                    leadViewModel.createNewLeadCategory(jsonObject)
                } else {
                    customerViewModel.addNewCategory(jsonObject)
                }
            }
        }
    }

    private fun initObservers() {
        customerViewModel.addNewCategoryLiveData.observe(this) {
            if (it.error == false) {
                binding.progressBar.visibility = View.GONE
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else if (it.error == true) {
                binding.progressBar.visibility = View.GONE
                Utils.hideKeyboard(this)
                showToast(it.message)
            }
        }

        leadViewModel.createLeadLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                showToast(it.message)
            }
        }

        productViewModel.productCategoryLiveData.observe(this){
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                showToast(it.message)
            }
        }
    }
}