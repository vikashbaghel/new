package com.app.rupyz.sales.beat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySelectCustomerForBeatBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRetailerResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.beatplan.SelectCustomerForBeatViewModel
import com.app.rupyz.sales.beatplan.SelectRetailerForBeatViewModel

class SelectCustomerForBeatActivity : BaseActivity(), ChooseCustomerForBeatListener {
    private var addBeatModel: AddBeatModel? = null
    private lateinit var binding: ActivitySelectCustomerForBeatBinding
    private var customerSubLevel: String? = ""

    private val customerListViewModel: SelectCustomerForBeatViewModel by viewModels()
    private val retailerListViewModel: SelectRetailerForBeatViewModel by viewModels()

    private var beatCustomerResponseModel = BeatCustomerResponseModel()
    private var beatRetailerResponseModel = BeatRetailerResponseModel()

    private var isRetailerPageOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCustomerForBeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()

        if (intent.hasExtra(AppConstant.BEAT)) {
            addBeatModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.BEAT, AddBeatModel::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.BEAT)
            }
        }

        addBeatModel?.let {

            if (addBeatModel?.name.isNullOrEmpty().not() || addBeatModel?.parentCustomerName.isNullOrEmpty().not()){
                binding.tvToolbarSubTitle.visibility = View.VISIBLE
            } else {
                binding.tvToolbarSubTitle.visibility = View.GONE
            }

            binding.tvToolbarSubTitle.text = resources.getString(
                R.string.add_customer_for_beat_sub_title,
                addBeatModel?.name ?: "".trim(),
                addBeatModel?.parentCustomerName ?: "".trim()
            )

            if (it.selectCustomer != null) {
                it.selectCustomer?.isUpdatedFromSearch = true
                customerListViewModel.setCustomerList(it.selectCustomer!!)
            }
        }


        val beatCustomerResponseModel = BeatCustomerResponseModel()

        if (addBeatModel?.selectCustomer?.addCustomer.isNullOrEmpty().not()) {
            beatCustomerResponseModel.addCustomer = addBeatModel?.selectCustomer?.addCustomer
            beatCustomerResponseModel.isUpdatedFromSearch = true
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        } else {
            beatCustomerResponseModel.addCustomer = ArrayList()
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        }

        if (addBeatModel?.selectCustomer?.removeCustomer.isNullOrEmpty().not()) {
            beatCustomerResponseModel.removeCustomer = addBeatModel?.selectCustomer?.removeCustomer
            beatCustomerResponseModel.isUpdatedFromSearch = true
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        } else {
            beatCustomerResponseModel.removeCustomer = ArrayList()
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        }

        if (addBeatModel?.selectCustomer?.subLevelSet.isNullOrEmpty().not()) {
            val beatRetailerResponseModel = BeatRetailerResponseModel()
            val subLevelSet = ArrayList<CustomerData>()
            addBeatModel?.selectCustomer?.subLevelSet?.forEach {
                val customerData = CustomerData()
                customerData.id = it
                subLevelSet.add(customerData)
            }
            beatRetailerResponseModel.addCustomer = subLevelSet
            retailerListViewModel.setCustomerList(beatRetailerResponseModel)
        }

        selectCustomerForBeat()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initObservers() {
        customerListViewModel.selectCustomerListLiveData.observe(this) {
            beatCustomerResponseModel = it
        }

        retailerListViewModel.selectCustomerListLiveData.observe(this) {
            beatRetailerResponseModel = it
        }
    }

    private fun selectCustomerForBeat() {
        val addBeatPlanDetails =
            CustomerListForBeatFragment.newInstance(this, addBeatModel)
        addFragment(R.id.container, addBeatPlanDetails)
    }

    override fun onChooseCustomerList(model: AddBeatModel) {
        val intent = Intent()
        intent.putExtra(AppConstant.ALL_BEAT_PLAN, model)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCancelChooseCustomer() {
        finish()
    }


    override fun onCancelSearchCustomer() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onSaveSearchCustomerListData() {
        onBackPressedDispatcher.onBackPressed()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            if (supportFragmentManager.backStackEntryCount == 2) {
                isRetailerPageOpen = false
                addBeatModel?.let {
                    binding.tvToolbarTitle.text = resources.getString(R.string.choose_customer)
                }
            } else {
                binding.tvToolbarTitle.text =
                    resources.getString(R.string.choose_dynamic_customer, customerSubLevel)
            }
            @Suppress("DEPRECATION")
            super.onBackPressed()
        } else {
            finish()
        }
    }
}