package com.app.rupyz.sales.beat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewBeatBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatListDataItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.sales.customer.CustomerNameRvAdapter
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.customer.StaffListForAssignAdapter
import com.app.rupyz.ui.more.MoreViewModel

class AddNewBeatActivity : BaseActivity(), StaffListForAssignAdapter.IAssignStaffListener,
    CustomerNameRvAdapter.ICustomerSelectListener {
    private lateinit var binding: ActivityAddNewBeatBinding

    private val beatViewModel: BeatViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()

    private var customerLevelList: ArrayList<String> = ArrayList()
    private var addStaffSetList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var removeStaffSetList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var staffList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var customerList = java.util.ArrayList<CustomerData>()

    private lateinit var staffListForAssignAdapter: StaffListForAssignAdapter
    private lateinit var customerAdapter: CustomerNameRvAdapter

    private var isPageLoading = false
    private var isApiLastPage = false
    private var isFirstTimeLoadCustomer = true
    private var isStaffSetUpdated = false

    private var staffCurrentPage = 1
    private var customerCurrentPage = 1
    private var customerCount = 0

    private var customerParentId: Int? = null
    private var customerLevel = ""

    private var delay: Long = 500

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    private val addBeatModel = AddBeatModel()

    private var beatDetailsModel: BeatListDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewBeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initObservers()

        moreViewModel.getPreferencesInfo()

        if (intent.hasExtra(AppConstant.BEAT_ID)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.update_beat)
            binding.mainScrollView.visibility = View.GONE
            binding.btnLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            addBeatModel.id = intent.getIntExtra(AppConstant.BEAT_ID, 0)
            beatViewModel.getBeatDetails(intent.getIntExtra(AppConstant.BEAT_ID, 0))
        } else {
            initLayout(null)

            val selectCustomer = BeatCustomerResponseModel()
            addBeatModel.selectCustomer = selectCustomer
        }
    }

    private fun initLayout(beatDetailsModel: BeatListDataItem?) {

        loadStaff()

        binding.etBeatName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                addBeatModel.name = "$s"
            }
        })

        binding.ivSearchStaff.setOnClickListener {
            staffCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            Utils.hideKeyboard(this)
            loadStaff()
        }

        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                staffCurrentPage = 1
                isApiLastPage = false
                isPageLoading = true
                staffList.clear()
                loadStaff()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchStaff.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForStaff);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForStaff, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchStaff.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearchStaff.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearchStaff.setOnClickListener {
            binding.etSearchStaff.setText("")
            staffCurrentPage = 1
            isApiLastPage = false
            staffList.clear()
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }

        binding.spinnerCustomerLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (position) {
                        0 -> {
                            customerLevel = ""
                            customerParentId = null
                            binding.hdCustomerLevelParent.hint =
                                resources.getString(R.string.customer_level_parent)
                            binding.tvParentCustomerName.text = ""
                            addBeatModel.parentCustomer = null
                        }

                        1 -> {
                            customerLevel = AppConstant.CUSTOMER_LEVEL_1

                            val mapString = resources.getString(
                                R.string.select_customer_level_parent_mapping, customerLevelList[1]
                            )
                            binding.hdCustomerLevelParent.text = mapString

                            if (beatDetailsModel?.parentCustomerName.isNullOrEmpty().not()) {
                                binding.tvParentCustomerName.text =
                                    beatDetailsModel?.parentCustomerName
                            } else {

                                binding.tvParentCustomerName.hint = "Select ${customerLevelList[1]}"
                                binding.tvParentCustomerName.text = ""

                                binding.tvCustomerCount.text = ""
                                binding.tvCustomerCount.visibility = View.GONE
                                customerParentId = null
                            }
                            binding.etSearchCustomer.hint = "Search ${customerLevelList[1]}"
                            binding.rvCustomerList.scrollToPosition(0)

                            binding.selectSecondLevelCustomerProgressBar.visibility = View.VISIBLE
                            loadCustomerPage()
                        }

                        2 -> {
                            customerLevel = AppConstant.CUSTOMER_LEVEL_2

                            val mapString = resources.getString(
                                R.string.select_customer_level_parent_mapping, customerLevelList[2]
                            )
                            binding.hdCustomerLevelParent.text = mapString

                            if (beatDetailsModel?.parentCustomerName.isNullOrEmpty().not()) {
                                binding.tvParentCustomerName.text =
                                    beatDetailsModel?.parentCustomerName
                            } else {
                                binding.tvParentCustomerName.hint = "Select ${customerLevelList[2]}"
                                binding.tvParentCustomerName.text = ""
                                binding.tvCustomerCount.text = ""
                                binding.tvCustomerCount.visibility = View.GONE
                                customerParentId = null
                            }
                            binding.etSearchCustomer.hint = "Search ${customerLevelList[2]}"
                            binding.rvCustomerList.scrollToPosition(0)

                            binding.selectSecondLevelCustomerProgressBar.visibility = View.VISIBLE
                            loadCustomerPage()
                        }
                    }
                }
            }

        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etSearchCustomer.text.toString().isNotEmpty()) {
                    customerCurrentPage = 1
                    customerList.clear()
                    loadCustomerPage()
                    Utils.hideKeyboard(this)
                } else {
                    Toast.makeText(this, "Please enter some value!!", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })


        binding.ivClearSearch.setOnClickListener {
            binding.etSearchCustomer.setText("")
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerPage()
            binding.ivClearSearch.visibility = View.GONE
        }

        binding.hdAssignStaff.setOnClickListener {
            binding.progressBarStaffListPagination.visibility = View.GONE
            binding.clAssignStaff.isVisible = binding.clAssignStaff.isVisible.not()
            hideKeyboard()
        }

        binding.tvParentCustomerName.setOnClickListener {
            if (customerList.isNotEmpty()) {
                binding.clCustomerList.isVisible = !binding.clCustomerList.isVisible
            } else {
                showToast("Please select customer level")
            }
        }

        binding.tvAddCustomer.setOnClickListener {
            hideKeyboard()
            binding.etBeatName.clearFocus()
            binding.etLocality.clearFocus()
            someActivityResultLauncher.launch(
                Intent(
                    this, SelectCustomerForBeatActivity::class.java
                ).putExtra(AppConstant.BEAT, addBeatModel)
            )
        }

        binding.imgClose.setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            validateData()
        }

        binding.root.setOnClickListener {
            hideDropDown()
        }

        binding.mainContent.setOnClickListener {
            hideDropDown()
        }

        binding.mainScrollView.setOnClickListener {
            hideDropDown()
        }
    }

    private fun hideDropDown() {
        binding.clAssignStaff.visibility = View.GONE
        binding.clCustomerList.visibility = View.GONE
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null && result.data!!.hasExtra(
                AppConstant.ALL_BEAT_PLAN
            )
        ) {
            val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data!!.getParcelableExtra(
                    AppConstant.ALL_BEAT_PLAN, AddBeatModel::class.java
                )
            } else {
                result.data!!.getParcelableExtra(
                    AppConstant.ALL_BEAT_PLAN
                )
            }

            if (model != null) {
                addBeatModel.selectCustomer = model.selectCustomer

                var selectAllApplied = false
                model.isFirstTime = false
                addBeatModel.allowAll = model.selectCustomer?.selectAllCustomer ?: false

                model.selectCustomer?.let { customerSet ->

                    if (customerSet.selectAllCustomer == true) {
                        selectAllApplied = true
                        if (customerSet.isPaginationAvailable) {
                            customerCount = 31
                        } else {
                            customerCount = customerSet.selectedAllCustomerCount ?: 31

                            if (customerSet.removeCustomer.isNullOrEmpty().not()) {
                                customerCount -= customerSet.removeCustomer?.size!!
                            }
                        }
                    } else if (customerSet.deSelectAllCustomer == true) {
                        customerCount = 0
                        if (customerSet.addCustomer.isNullOrEmpty().not()){
                            customerCount = customerSet.addCustomer?.size!!
                        }
                    } else {
                        if (customerSet.addCustomer.isNullOrEmpty().not()) {
                            customerCount = customerSet.addCustomer?.size ?: 0
                        }
                        if (customerSet.removeCustomer.isNullOrEmpty().not()) {

                            customerCount =
                                customerCount.minus(customerSet.removeCustomer?.size ?: 0)
                        }
                    }

                    var customerCountString = ""
                    customerCountString = if (selectAllApplied && customerCount > 30) {
                        "30 +"
                    } else {
                        "$customerCount"
                    }

                    binding.tvCustomerCount.text = resources.getString(
                        R.string.customer_count_for_beat, customerCountString
                    )
                    binding.tvCustomerCount.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateData() {
        if (binding.etBeatName.text.toString().isEmpty()) {
            showToast("Enter Beat Name")
        } else {
            binding.btnContinue.isEnabled = false
            createBeat()
        }
    }

    private fun createBeat() {
        addBeatModel.name = binding.etBeatName.text.toString()

        addBeatModel.locality = binding.etLocality.text.toString()
        addBeatModel.parentCustomer = customerParentId

        val updateStaffMappingModel = UpdateMappingModel()

        updateStaffMappingModel.allowAll = false
        updateStaffMappingModel.disallowAll = false

        if (removeStaffSetList.size > 0) {
            val list = java.util.ArrayList<Int?>()
            removeStaffSetList.forEach {
                list.add(it.id!!)
            }

            updateStaffMappingModel.removeSet = list
        } else {
            updateStaffMappingModel.removeSet = java.util.ArrayList()
        }
        if (addStaffSetList.size > 0) {
            val list = java.util.ArrayList<Int?>()
            addStaffSetList.forEach {
                list.add(it.id!!)
            }

            updateStaffMappingModel.addSet = list
        } else {
            updateStaffMappingModel.addSet = java.util.ArrayList()
        }

        addBeatModel.selectStaff = updateStaffMappingModel

        binding.progressBar.visibility = View.VISIBLE
        if (intent.hasExtra(AppConstant.BEAT_ID)) {
            beatViewModel.updateBeat(addBeatModel, intent.getIntExtra(AppConstant.BEAT_ID, -1))
        } else {
            beatViewModel.createBeat(addBeatModel)
        }
    }

    private val inputFinishCheckerForStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            staffCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            customerCurrentPage = 1
            customerList.clear()
            loadCustomerPage()
            customerAdapter.notifyDataSetChanged()
        }
    }


    private fun loadCustomerPage() {
        customerViewModel.getCustomerList(
            null,
            binding.etSearchCustomer.text.toString(),
            customerLevel,
            java.util.ArrayList(),
            AppConstant.SORTING_LEVEL_ASCENDING,
            customerCurrentPage,
            hasInternetConnection()
        )
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStaffListForAssign.layoutManager = linearLayoutManager
        staffListForAssignAdapter = StaffListForAssignAdapter(staffList, this)
        binding.rvStaffListForAssign.adapter = staffListForAssignAdapter


        binding.rvStaffListForAssign.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                staffCurrentPage += 1
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        val llM2 = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = llM2
        customerAdapter = CustomerNameRvAdapter(customerList, this)
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object : PaginationScrollListener(llM2) {
            override fun loadMoreItems() {
                isPageLoading = true
                customerCurrentPage += 1
                loadCustomerPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadStaff() {
        binding.progressBarStaffListPagination.visibility = View.VISIBLE
        beatViewModel.getStaffListWithBeatMapping(
            beatDetailsModel?.id ?: 0,
            binding.etSearchStaff.text.toString(),
            false,
            staffCurrentPage
        )
    }

    private fun initObservers() {
        beatViewModel.beatDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { data ->
                    initData(data)
                }
            }
        }

        moreViewModel.preferenceLiveData.observe(this) { data ->
            data.data?.let {
                if (it.customerLevelConfig != null) {
                    it.customerLevelConfig?.let { config ->

                        customerLevelList.add(resources.getString(R.string.select_customer_level))

                        if (!config.LEVEL_1.isNullOrEmpty()) {
                            customerLevelList.add(config.LEVEL_1!!)
                        }
                        if (!config.LEVEL_2.isNullOrEmpty()) {
                            customerLevelList.add(config.LEVEL_2!!)
                        }
                    }

                    val arrayAdapter = ArrayAdapter(
                        this, R.layout.single_text_view_spinner_12dp_text, customerLevelList
                    )

                    arrayAdapter.setDropDownViewResource(R.layout.single_text_view_spinner_12dp_text)
                    binding.spinnerCustomerLevel.adapter = arrayAdapter


                    if (beatDetailsModel?.parentCustomerLevel.isNullOrEmpty().not()) {
                        when (beatDetailsModel?.parentCustomerLevel) {
                            AppConstant.CUSTOMER_LEVEL_1 -> {
                                binding.spinnerCustomerLevel.setSelection(1)

                                if (beatDetailsModel?.parentCustomerName.isNullOrEmpty().not()) {
                                    binding.tvParentCustomerName.text =
                                        beatDetailsModel?.parentCustomerName
                                }
                            }

                            AppConstant.CUSTOMER_LEVEL_2 -> {
                                binding.spinnerCustomerLevel.setSelection(2)
                            }
                        }
                    }
                }
            }
        }

        beatViewModel.staffListWithCustomerBeatLiveData.observe(this) {
            binding.progressBarStaffListPagination.visibility = View.GONE
            isPageLoading = false
            if (it.data.isNullOrEmpty().not()) {
                isPageLoading = false
                if (staffCurrentPage == 1) {
                    staffList.clear()
                }

                if (addStaffSetList.isNotEmpty() || removeStaffSetList.isNotEmpty()) {

                    it.data?.forEach { staff ->

                        if (addStaffSetList.isNotEmpty()) {
                            val index =
                                addStaffSetList.indexOfLast { addedStaff -> addedStaff.id == staff.id }
                            staff.isSelected = index != -1
                        }

                        if (removeStaffSetList.isNotEmpty()) {

                            val index =
                                removeStaffSetList.indexOfLast { removedStaff -> removedStaff.id == staff.id }

                            if (index != -1) {
                                staff.isSelected = false
                            }
                        }
                        staffList.add(staff)
                    }

                } else {
                    staffList.addAll(it.data!!)
                }

                staffListForAssignAdapter.notifyDataSetChanged()

                if (it.data!!.size < 30) {
                    isApiLastPage = true
                }

                isFirstTimeLoadCustomer = false
            } else {
                isApiLastPage = true
            }
        }

        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.selectSecondLevelCustomerProgressBar.visibility = View.GONE
            data.data?.let { it ->
                isPageLoading = false
                if (it.isNotEmpty()) {
                    if (customerCurrentPage == 1) {
                        customerList.clear()
                    }
                    customerList.addAll(it)
                    customerAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                    if (customerCurrentPage == 1) {
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        beatViewModel.addBeatLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                showToast(it.message)
                binding.btnContinue.isEnabled = true
            }
        }
    }

    private fun initData(model: BeatListDataItem) {
        beatDetailsModel = model

        initLayout(beatDetailsModel)

        binding.etBeatName.setText(model.name)
        binding.etLocality.setText(model.locality)

        if (!model.parentCustomerLevel.isNullOrEmpty()) {
            when (model.parentCustomerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.spinnerCustomerLevel.setSelection(1)

                    if (model.parentCustomerName.isNullOrEmpty().not()) {
                        binding.tvParentCustomerName.text = model.parentCustomerName
                    }
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.spinnerCustomerLevel.setSelection(2)
                }
            }
        }

        if (model.parentCustomerName.isNullOrEmpty().not()) {
            binding.tvParentCustomerName.text = model.parentCustomerName
            addBeatModel.parentCustomerName = model.parentCustomerName
        }

        if (model.parentCustomer != null) {
            customerParentId = model.parentCustomer
            addBeatModel.parentCustomer = model.parentCustomer
        }

        addBeatModel.allowAll = model.allowAll

        val selectCustomer = BeatCustomerResponseModel()
        selectCustomer.selectAllCustomer = model.allowAll
        selectCustomer.addCustomer = ArrayList()
        selectCustomer.removeCustomer = ArrayList()
        addBeatModel.selectCustomer = selectCustomer

        if (model.customerCount != null) {
            customerCount = model.customerCount
            binding.tvCustomerCount.text =
                resources.getString(R.string.customer_count_for_beat, "${model.customerCount}")
            binding.tvCustomerCount.visibility = View.VISIBLE
        }

        binding.mainScrollView.visibility = View.VISIBLE
        binding.btnLayout.visibility = View.VISIBLE
    }

    override fun setCustomerSelect(checked: Boolean, customerData: NameAndIdSetInfoModel) {
        if (!isFirstTimeLoadCustomer) {
            isStaffSetUpdated = true
            val idModel = NameAndIdSetInfoModel()
            idModel.id = customerData.id
            idModel.name = customerData.name

            if (checked) {
                customerData.isSelected = true

                if (removeStaffSetList.size > 0) {
                    val index = removeStaffSetList.indexOfLast { it.id == customerData.id }
                    if (index != -1) {
                        removeStaffSetList.removeAt(index)
                    } else {
                        addStaffSetList.add(customerData)
                    }
                } else {
                    addStaffSetList.add(customerData)
                }


                customerData.id
            } else {
                onRemoveCustomer(idModel)
            }
        }
    }

    private fun onRemoveCustomer(staffSet: NameAndIdSetInfoModel) {
        if (addStaffSetList.size > 0) {
            val index = addStaffSetList.indexOfLast { it.id == staffSet.id }
            if (index != -1) {
                addStaffSetList.removeAt(index)
            } else {
                removeStaffSetList.add(staffSet)
            }
        } else {
            removeStaffSetList.add(staffSet)
        }

        staffList.forEachIndexed { index, _ ->
            if (staffList[index].id == staffSet.id) {
                staffList[index].isSelected = false
            }
        }

        staffListForAssignAdapter.notifyDataSetChanged()

    }

    override fun onCustomerSelect(customerData: CustomerData, position: Int) {
        binding.tvParentCustomerName.text = customerData.name
        customerParentId = customerData.id
        binding.clCustomerList.visibility = View.GONE
        addBeatModel.parentCustomerName = customerData.name
        addBeatModel.parentCustomer = customerData.id
    }

}