package com.app.rupyz.sales.beatplan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySelectCustomerForBeatPlanBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRetailerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.order.customer.CustomerData

class SelectCustomerForBeatPlanPlanActivity : BaseActivity(), ChooseCustomerForBeatPlanListener {
    private var dailyModel: BeatRouteDayListModel? = null
    private lateinit var binding: ActivitySelectCustomerForBeatPlanBinding
    private var customerInfoForRetailer: CustomerData? = null
    private var customerSubLevel: String? = ""

    private val customerListViewModel: SelectCustomerForBeatViewModel by viewModels()
    private val retailerListViewModel: SelectRetailerForBeatViewModel by viewModels()

    private var beatCustomerResponseModel = BeatCustomerResponseModel()
    private var beatRetailerResponseModel = BeatRetailerResponseModel()

    private var isRetailerPageOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCustomerForBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()

        if (intent.hasExtra(AppConstant.BEAT)) {
            dailyModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.BEAT, BeatRouteDayListModel::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.BEAT)
            }
        }

        dailyModel?.let {
            binding.tvToolbarTitle.text = it.beatName
            binding.tvToolbarSubTitle.text = resources.getString(R.string.choose_customer)

            if (it.selectDayBeat != null) {
                it.selectDayBeat?.isUpdatedFromSearch = true
                customerListViewModel.setCustomerList(it.selectDayBeat!!)
            }
        }


        val beatCustomerResponseModel = BeatCustomerResponseModel()

        if (dailyModel?.selectDayBeat?.addCustomer.isNullOrEmpty().not()) {
            beatCustomerResponseModel.addCustomer = dailyModel?.selectDayBeat?.addCustomer
            beatCustomerResponseModel.isUpdatedFromSearch = true
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        } else {
            beatCustomerResponseModel.addCustomer = ArrayList()
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        }

        if (dailyModel?.selectDayBeat?.removeCustomer.isNullOrEmpty().not()) {
            beatCustomerResponseModel.removeCustomer = dailyModel?.selectDayBeat?.removeCustomer
            beatCustomerResponseModel.isUpdatedFromSearch = true
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        } else {
            beatCustomerResponseModel.removeCustomer = ArrayList()
            customerListViewModel.setCustomerList(beatCustomerResponseModel)
        }

        if (dailyModel?.selectDayBeat?.subLevelSet.isNullOrEmpty().not()) {
            val beatRetailerResponseModel = BeatRetailerResponseModel()
            val subLevelSet = ArrayList<CustomerData>()
            dailyModel?.selectDayBeat?.subLevelSet?.forEach {
                val customerData = CustomerData()
                customerData.id = it
                subLevelSet.add(customerData)
            }
            beatRetailerResponseModel.addCustomer = subLevelSet
            retailerListViewModel.setCustomerList(beatRetailerResponseModel)
        }

        selectCustomerForBeat()

        binding.ivSearch.setOnClickListener {
            if (isRetailerPageOpen) {
                val fragment =
                    SearchRetailerForBeatPlanFragment.newInstance(
                        this,
                        dailyModel,
                        customerSubLevel,
                        customerInfoForRetailer
                    )
                addFragment(R.id.container, fragment)
            } else {
                val fragment =
                    SearchCustomerForBeatFragment.newInstance(
                        this,
                        dailyModel,
                        customerSubLevel,
                        customerInfoForRetailer
                    )
                addFragment(R.id.container, fragment)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
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
            SelectCustomerForBeatPlanFragment.newInstance(this, dailyModel!!)
        addFragment(R.id.container, addBeatPlanDetails)
    }

    override fun onChooseCustomerList(model: BeatRouteDayListModel) {
        val intent = Intent()
        intent.putExtra(AppConstant.ALL_BEAT_PLAN, model)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCancelChooseCustomer() {
        finish()
    }

    override fun onChooseRetailer(
        dayListModel: BeatRouteDayListModel?,
        customerData: CustomerData,
        beatCustomerResponseModel: BeatCustomerResponseModel
    ) {
        customerInfoForRetailer = customerData
        customerData.beatId = dailyModel?.beatId
        binding.tvToolbarTitle.text = customerData.name


        when (customerData.customerLevel) {
            AppConstant.CUSTOMER_LEVEL_1 -> customerSubLevel =
                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
            AppConstant.CUSTOMER_LEVEL_2 -> customerSubLevel =
                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
        }

        binding.tvToolbarSubTitle.text =
            resources.getString(R.string.choose_dynamic_customer, customerSubLevel)

        val fragment =
            SelectRetailerForBeatPlanFragment.newInstance(
                this,
                dayListModel,
                customerData,
                beatCustomerResponseModel
            )
        addFragment(R.id.container, fragment)

        isRetailerPageOpen = true
    }

    override fun onSelectRetailerList() {
        onBackPressed()
    }

    override fun onCancelSearchCustomer() {
        onBackPressed()
    }

    override fun onSaveSearchCustomerListData() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            if (supportFragmentManager.backStackEntryCount == 2) {
                isRetailerPageOpen = false
                dailyModel?.let {
                    binding.tvToolbarTitle.text = it.beatName
                    binding.tvToolbarSubTitle.text = resources.getString(R.string.choose_customer)
                }
            } else {
                binding.tvToolbarTitle.text = customerInfoForRetailer?.name
                binding.tvToolbarSubTitle.text =
                    resources.getString(R.string.choose_dynamic_customer, customerSubLevel)
            }
            super.onBackPressed()
        } else {
            finish()
        }
    }
}