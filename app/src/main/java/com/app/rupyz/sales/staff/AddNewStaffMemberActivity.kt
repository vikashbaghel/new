package com.app.rupyz.sales.staff

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewStaffMemberBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.model_kt.AssignedRoleItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.order.sales.BankDetails
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.ui.more.MoreViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewStaffMemberActivity : BaseActivity(),
    CustomerListForAssignAdapter.IAssignCustomerListener,
    AssignManagerStaffListAdapter.IAssignManagerListener,
    AssignBeatForStaffAdapter.IAssignBeatForStaffListener {
    private lateinit var binding: ActivityAddNewStaffMemberBinding
    private lateinit var staffViewModel: StaffViewModel
    private lateinit var moreViewModel: MoreViewModel
    private lateinit var beatViewModel: BeatViewModel

    private var mDateSetListener: OnDateSetListener? = null
    private var staffData = StaffData()
    private val myCalendar = Calendar.getInstance()

    private var customerList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var addCustomerIdSet: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var removeCustomerIdSet: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var assignedRoleList: ArrayList<AssignedRoleItem> = ArrayList()
    private var staffList = ArrayList<StaffData>()
    private var beatList: ArrayList<NameAndIdSetInfoModel> = ArrayList()

    private var addBeatSet: ArrayList<Int?> = ArrayList()
    private var removeBeatSet: ArrayList<Int?> = ArrayList()
    private var alreadyAddedBeatList: ArrayList<Int> = ArrayList()

    private lateinit var customerListForAssignAdapter: CustomerListForAssignAdapter
    private lateinit var assignManagerStaffAdapter: AssignManagerStaffListAdapter
    private lateinit var assignBeatForStaffAdapter: AssignBeatForStaffAdapter

    private var currentPageForCustomer = 1
    private var staffCurrentPage = 1
    private var assignedManagerId = -1
    private var assignStaffRole = ""

    private var isAllCustomerChecked = false
    private var isDeSelectAllCustomer = false
    private var isFirstTimeLoadStaff = true
    private var isCustomerSetUpdated = false
    private var isUpdate: Boolean = false

    private var isPageLoading = false
    private var isPageLoadingForCustomer = false

    private var isApiLastPage = false
    private var isApiLastPageForCustomer = false

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)
    private var delay: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStaffMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]
        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        beatViewModel.searchBeat("", 1, hasInternetConnection())
        moreViewModel.getPreferencesInfo()

        initRecyclerView()
        initObservers()
        initLayout()


        staffViewModel.getRoleList(1, hasInternetConnection())

        if (intent.hasExtra(AppConstant.STAFF_ID)) {
            isUpdate = true
            binding.tvToolbarTitle.text = resources.getString(R.string.update_staff)
            binding.btnAdd.text = resources.getString(R.string.update)
            staffViewModel.getStaffById(intent.getIntExtra(AppConstant.STAFF_ID, 0))
        }

        loadNextPage()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initLayout() {
        mDateSetListener = OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateLabel()
        }

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                assignStaffRole = if (position != 0) {
                    binding.spinnerRole.selectedItem.toString()
                } else {
                    ""
                }
            }
        }

        var isAssignCustomerOpen = false

        binding.clAssignCustomer.setOnClickListener {
            binding.progressBarCustomerListPagination.visibility = View.GONE
            hideKeyboard()
            if (isAssignCustomerOpen) {
                isAssignCustomerOpen = false
                binding.groupDiscountInput.visibility = View.GONE
            } else {
                isAssignCustomerOpen = true
                binding.groupDiscountInput.visibility = View.VISIBLE
            }
        }

        var isAssignManagerOpen = false

        binding.tvAssignedManager.setOnClickListener {
            if (isAssignManagerOpen) {
                isAssignManagerOpen = false
                binding.clStaffList.visibility = View.GONE
            } else {
                isAssignManagerOpen = true
                binding.clStaffList.visibility = View.VISIBLE
            }
        }

        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPageForCustomer = 1
                isApiLastPageForCustomer = false
                isPageLoadingForCustomer = true

                customerList.clear()
                loadNextPage()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.ivSearchStaff.setOnClickListener {
            staffCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            Utils.hideKeyboard(this)
            loadStaffPage()
        }

        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                staffCurrentPage = 1
                isApiLastPage = false
                isPageLoading = true
                staffList.clear()
                loadStaffPage()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.ivClearStaffSearch.setOnClickListener {
            binding.etSearchStaff.setText("")
            staffCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            loadStaffPage()
        }

        binding.etSearchStaff.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeCallbacks(inputFinishCheckerForStaff);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForStaff, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearStaffSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearStaffSearch.visibility = View.GONE
                }
            }

        })

        binding.cbAllStaffName.setOnCheckedChangeListener { _, isChecked ->
            isCustomerSetUpdated = true
            isAllCustomerChecked = isChecked
            customerList.forEach {
                it.isSelected = isChecked
            }
            customerListForAssignAdapter.notifyDataSetChanged()

            isDeSelectAllCustomer = if (isChecked) {
                false
            } else {
                addCustomerIdSet.clear()
                true
            }
        }


        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForCustomer)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForCustomer, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            currentPageForCustomer = 1
            isApiLastPageForCustomer = false
            binding.etSearchCustomer.setText("")
            customerList.clear()
            hideKeyboard()
            binding.ivClearSearch.visibility = View.GONE
            customerListForAssignAdapter.notifyDataSetChanged()
            loadNextPage()
        }

        binding.clBeat.setOnClickListener {
            binding.rvBeatList.isVisible = binding.rvBeatList.isVisible.not()
        }

        binding.nsvMain.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        binding.mainContent.setOnClickListener {
            hideKeyboard()
        }

        binding.imgClose.setOnClickListener {
            finish()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {
            submitData()
        }
        binding.etDoj.setOnClickListener {
            openCalendar()
        }
    }

    private val inputFinishCheckerForCustomer = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPageForCustomer = 1
            customerList.clear()
            customerListForAssignAdapter.notifyDataSetChanged()

            loadNextPage()
        }
    }

    private val inputFinishCheckerForStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            staffCurrentPage = 1
            staffList.clear()
            assignManagerStaffAdapter.notifyDataSetChanged()

            loadStaffPage()
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerListForAssignAdapter = CustomerListForAssignAdapter(customerList, this)
        binding.rvCustomerList.adapter = customerListForAssignAdapter

        binding.rvCustomerList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoadingForCustomer = true
                currentPageForCustomer += 1
                loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForCustomer
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForCustomer
            }
        })

        val llM2 = LinearLayoutManager(this)
        binding.rvStaffList.layoutManager = llM2
        assignManagerStaffAdapter = AssignManagerStaffListAdapter(staffList, this)
        binding.rvStaffList.adapter = assignManagerStaffAdapter

        binding.rvStaffList.addOnScrollListener(object : PaginationScrollListener(llM2) {
            override fun loadMoreItems() {
                isPageLoading = true
                staffCurrentPage += 1
                loadStaffPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvBeatList.layoutManager = LinearLayoutManager(this)
        assignBeatForStaffAdapter = AssignBeatForStaffAdapter(beatList, this)
        binding.rvBeatList.adapter = assignBeatForStaffAdapter
    }

    private fun loadStaffPage() {
        staffViewModel.getStaffListForAssignManager(
            staffData.id ?: 0,
            binding.etSearchStaff.text.toString(), staffCurrentPage
        )
    }

    private fun loadNextPage() {
        binding.progressBarCustomerListPagination.visibility = View.VISIBLE

        staffViewModel.getCustomerListWithStaffMapping(
            intent.getIntExtra(AppConstant.STAFF_ID, 0),
            binding.etSearchCustomer.text.toString(),
            currentPageForCustomer
        )
    }

    private fun initObservers() {
        staffViewModel.getStaffByIdData().observe(this) { data ->
            data.data?.let { it ->
                if (!it.mobile.isNullOrEmpty()) {
                    staffData = data.data
                    binding.etStaffName.setText(staffData.name)
                    binding.etPan.setText(staffData.panId)
                    binding.etEmployeeId.setText(staffData.employeeId)
                    binding.etMobileNumber.setText(staffData.mobile)
                    binding.etEmailId.setText(staffData.email)
                    binding.etAddress.setText(staffData.addressLine1)
                    binding.etDoj.text = staffData.joiningDate
                    binding.etBank.setText(staffData.bankDetails?.bank ?: "")
                    binding.etAccountNo.setText(staffData.bankDetails?.accountNumber ?: "")
                    binding.etBranch.setText(staffData.bankDetails?.branch ?: "")
                    binding.etIfsc.setText(staffData.bankDetails?.ifscCode ?: "")
                    binding.switchAutoAssignNewCustomer.isChecked =
                        staffData.auto_assign_new_customers

                    if (it.roles.isNullOrEmpty().not()) {
                        assignStaffRole = it.roles!![0]
                        if (assignedRoleList.isNotEmpty()) {
                            val index =
                                assignedRoleList.indexOfLast { assignedRoleItem -> assignedRoleItem.name == assignStaffRole }

                            if (index != -1) {
                                binding.spinnerRole.setSelection(index)
                            }
                        }
                    }

                    if (staffData.beats.isNullOrEmpty().not()) {
                        alreadyAddedBeatList.addAll(staffData.beats!!)

                        if (beatList.isNotEmpty()) {
                            beatList.forEachIndexed { index, nameAndIdSetInfoModel ->
                                if (alreadyAddedBeatList.contains(nameAndIdSetInfoModel.id)) {
                                    beatList[index].isSelected = true
                                }
                            }

                            assignBeatForStaffAdapter.notifyDataSetChanged()
                        }
                    }

                    binding.cbAllStaffName.isChecked = staffData.allow_all_customer

                    staffCurrentPage = 1
                    loadStaffPage()

                    if (staffData.parent != null) {
                        binding.tvAssignedManager.text = staffData.managerName
                        assignedManagerId = staffData.parent!!
                    }

                    if (!it.customerSetInfo.isNullOrEmpty()) {
                        addCustomerIdSet.addAll(it.customerSetInfo!!)
                    }
                }
            }
        }

        moreViewModel.preferenceLiveData.observe(this) { data ->
            data.data?.let {
                if (it.staffCustomerMapping != null && it.staffCustomerMapping == true) {
                    binding.groupMapping.visibility = View.VISIBLE
                } else {
                    binding.groupMapping.visibility = View.GONE
                }

                if (it.enableHierarchyManagement != null && it.enableHierarchyManagement == true) {
                    binding.groupAssignManager.visibility = View.VISIBLE
                    binding.clStaffList.visibility = View.GONE

                    if (intent.hasExtra(AppConstant.STAFF_ID).not()) {
                        staffCurrentPage = 1
                        loadStaffPage()
                    }

                    if (isStaffUser) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.ASSIGN_MANAGER_PERMISSION,
                                false
                            ).not()
                        ) {
                            binding.groupAssignManager.visibility = View.GONE
                        }
                    }
                } else {
                    binding.groupAssignManager.visibility = View.GONE
                }
            }
        }

        staffViewModel.addStaffLiveData().observe(this) { data ->
            data?.let {
                binding.btnAdd.isEnabled = true
                if (data.error == false) {
                    showToast(data.message)
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent()
                    intent.putExtra(AppConstant.STAFF_NAME, binding.etStaffName.text)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    showToast(data.message)
                    binding.btnAdd.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        staffViewModel.updateStaffLiveData().observe(this) { data ->
            data?.let {
                if (data.error == false) {
                    showToast(data.message)
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    showToast(data.message)
                    binding.btnAdd.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        staffViewModel.customerListLiveData.observe(this) {
            binding.progressBarCustomerListPagination.visibility = View.GONE
            isPageLoadingForCustomer = false

            if (!it.data.isNullOrEmpty()) {
                if (currentPageForCustomer == 1) {
                    customerList.clear()
                }

                if (isAllCustomerChecked) {
                    it.data.forEach { customer ->
                        customer.isSelected = true
                        customerList.add(customer)
                    }
                } else if (isDeSelectAllCustomer) {
                    it.data.forEach { customer ->
                        customer.isSelected = false
                        customerList.add(customer)
                    }
                } else {
                    customerList.addAll(it.data)
                }

                if (it.data.size < 30) {
                    isApiLastPageForCustomer = true
                }

                customerListForAssignAdapter.notifyDataSetChanged()

                isFirstTimeLoadStaff = false
            } else {
                if (currentPageForCustomer == 1) {
                    isApiLastPageForCustomer = true
                }
            }
        }

        staffViewModel.assignedRoleListLiveData.observe(this) { data ->
            data?.let { res ->
                if (res.error == false) {
                    res.data?.let {
                        assignedRoleList.add(AssignedRoleItem(name = "select Role"))
                        assignedRoleList.addAll(it)

                        val roleList = ArrayList<String>()

                        assignedRoleList.forEach { role ->
                            roleList.add(role.name!!)
                        }
                        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                            this, R.layout.single_text_view_spinner_16dp_text, roleList
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerRole.adapter = arrayAdapter

                        if (assignStaffRole.isNotEmpty()) {
                            assignedRoleList.forEachIndexed { index, assignedRoleItem ->
                                if (assignStaffRole == assignedRoleItem.name) {
                                    binding.spinnerRole.setSelection(index)
                                }
                            }
                        }
                    }
                }
            }
        }

        staffViewModel.getStaffListData().observe(this) { data ->
            data.data?.let { it ->
                isPageLoading = false
                if (it.isNotEmpty()) {
                    if (staffCurrentPage == 1) {
                        staffList.clear()
                    }
                    staffList.addAll(it)
                    assignManagerStaffAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }

        beatViewModel.orgBeatListLiveData.observe(this) {
            if (it.error == false) {
                isPageLoading = false
                if (it.data.isNullOrEmpty().not()) {
                    it.data?.let { beatPlan ->
                        beatPlan.forEach { plan ->

                            val model = NameAndIdSetInfoModel(plan.id, plan.name)
                            if (alreadyAddedBeatList.isNotEmpty() && alreadyAddedBeatList.contains(
                                    plan.id
                                )
                            ) {
                                model.isSelected = true
                            }
                            beatList.add(model)
                        }

                        assignBeatForStaffAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun submitData() {
        when {
            binding.etStaffName.text.trim().toString().isEmpty() -> {
                showToast("Name Required!")
            }

            !Validations().isValidName(
                binding.etStaffName.text.trim().toString()
            ) -> {
                showToast("Number and spacial character is not allow in name field!")
            }

            binding.etMobileNumber.text.trim().toString().isEmpty() -> {
                showToast("Mobile Number Required!")
            }

            !Validations().isValidMobileNumber(binding.etMobileNumber.text.trim().toString()) -> {
                showToast("Valid Mobile Number Required!")
            }

            binding.etEmployeeId.text.trim().toString().isEmpty() -> {
                showToast("Employee Id Required!")
            }

            binding.etPan.text.toString()
                .isNotEmpty() && !Validations().panValidation(binding.etPan.text.toString()) -> {

                showToast("Enter Valid PAN Number")
            }

            assignStaffRole.isEmpty() -> {
                showToast("Please Assign Role!")
            }

            else -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnAdd.isEnabled = false

                val staff = StaffData()
                staff.name = binding.etStaffName.text.toString()
                staff.mobile = binding.etMobileNumber.text.trim().toString()
                staff.employeeId = binding.etEmployeeId.text.trim().toString()
                staff.panId = binding.etPan.text.trim().toString()
                staff.email = binding.etEmailId.text.trim().toString()
                staff.addressLine1 = binding.etAddress.text.trim().toString()
                if (binding.etDoj.text.trim().toString().isNotEmpty()) {
                    staff.joiningDate = binding.etDoj.text.trim().toString()
                }

                val bankDetails = BankDetails()
                bankDetails.bank = binding.etBank.text.trim().toString()
                bankDetails.accountNumber = binding.etAccountNo.text.trim().toString()
                bankDetails.ifscCode = binding.etIfsc.text.trim().toString()
                bankDetails.branch = binding.etBranch.text.trim().toString()
                staff.bankDetails = bankDetails

                val selectedRoleList = ArrayList<String>()
                selectedRoleList.add(assignStaffRole)
                staff.roles = selectedRoleList

                staff.auto_assign_new_customers = binding.switchAutoAssignNewCustomer.isChecked

                if (assignedManagerId != -1) {
                    staff.managerStaffId = assignedManagerId
                }

                val updateCustomerMappingModel = UpdateMappingModel()
                if (isAllCustomerChecked) {
                    updateCustomerMappingModel.allowAll = true

                    if (removeCustomerIdSet.size > 0) {

                        val list = ArrayList<Int?>()
                        removeCustomerIdSet.forEach {
                            list.add(it.id!!)
                        }

                        updateCustomerMappingModel.removeSet = list
                    } else {
                        updateCustomerMappingModel.removeSet = ArrayList()
                    }

                } else if (isDeSelectAllCustomer) {
                    updateCustomerMappingModel.disallowAll = true
                    updateCustomerMappingModel.removeSet = ArrayList()

                    if (addCustomerIdSet.size > 0) {
                        val list = ArrayList<Int?>()
                        addCustomerIdSet.forEach {
                            list.add(it.id!!)
                        }
                        updateCustomerMappingModel.addSet = list
                    } else {
                        updateCustomerMappingModel.addSet = ArrayList()
                    }

                } else {
                    if (isCustomerSetUpdated) {
                        updateCustomerMappingModel.allowAll = false

                        if (addCustomerIdSet.size > 0) {
                            val list = ArrayList<Int?>()
                            addCustomerIdSet.forEach {
                                list.add(it.id!!)
                            }
                            updateCustomerMappingModel.addSet = list
                        } else {
                            updateCustomerMappingModel.addSet = ArrayList()
                        }

                        if (removeCustomerIdSet.size > 0) {
                            val list = ArrayList<Int?>()
                            removeCustomerIdSet.forEach {
                                list.add(it.id!!)
                            }

                            updateCustomerMappingModel.removeSet = list
                        } else {
                            updateCustomerMappingModel.removeSet = ArrayList()
                        }
                    } else {
                        updateCustomerMappingModel.removeSet = ArrayList()
                        updateCustomerMappingModel.addSet = ArrayList()
                    }
                }

                staff.selectCustomer = updateCustomerMappingModel

                if (addBeatSet.isEmpty().not() || removeBeatSet.isEmpty().not()) {
                    val updateBeatMappingModel = UpdateMappingModel()
                    updateBeatMappingModel.addSet = addBeatSet
                    updateBeatMappingModel.removeSet = removeBeatSet

                    staff.selectBeat = updateBeatMappingModel
                }

                if (isUpdate) {
                    staffViewModel.updateStaff(
                        staff, intent.getIntExtra(AppConstant.STAFF_ID, 0)
                    )
                } else {
                    staffViewModel.saveStaff(staff)
                }
            }
        }
    }

    private fun updateLabel() {
        try {
            val myFormat = "yyyy-MM-dd" //In which you need put here
            val sdf2 = SimpleDateFormat(myFormat, Locale.US)
            binding.etDoj.text = (sdf2.format(myCalendar.time))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun openCalendar() {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(
            this@AddNewStaffMemberActivity,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            mDateSetListener,
            year,
            month,
            day
        )
        dialog.updateDate(year, month - 1, day)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun setCustomerSelect(checked: Boolean, model: NameAndIdSetInfoModel) {
        if (!isFirstTimeLoadStaff) {
            isCustomerSetUpdated = true
            val idModel = NameAndIdSetInfoModel()
            idModel.id = model.id
            idModel.name = model.name

            if (checked) {
                model.isSelected = true
                if (!isAllCustomerChecked) {
                    addCustomerIdSet.add(idModel)
                }

                if (removeCustomerIdSet.size > 0) {
                    val index = removeCustomerIdSet.indexOfLast { it.id == model.id }
                    if (index != -1) {
                        removeCustomerIdSet.removeAt(index)
                    }
                }
            } else {
                onRemoveCustomer(idModel)
            }
        }
    }

    private fun onRemoveCustomer(model: NameAndIdSetInfoModel) {
        if (addCustomerIdSet.size > 0) {
            val index = addCustomerIdSet.indexOfLast { it.id == model.id }
            if (index != -1) {
                addCustomerIdSet.removeAt(index)
            }
        }

        removeCustomerIdSet.add(model)

        customerList.forEachIndexed { index, _ ->
            if (customerList[index].id == model.id) {
                customerList[index].isSelected = false
            }
        }

        customerListForAssignAdapter.notifyDataSetChanged()
    }

    override fun onAssignStaff(model: StaffData) {
        binding.tvAssignedManager.text = model.name
        assignedManagerId = model.id!!
        binding.clStaffList.visibility = View.GONE
    }

    override fun onBeatSelect(checked: Boolean, model: NameAndIdSetInfoModel) {
        if (checked) {
            addBeatSet.add(model.id!!)

            if (removeBeatSet.isNotEmpty()) {
                val index = removeBeatSet.indexOfLast { it == model.id }
                if (index != -1) {
                    removeBeatSet.removeAt(index)
                }
            }

        } else {
            removeBeatSet.add(model.id!!)

            if (addBeatSet.isNotEmpty()) {
                val index = addBeatSet.indexOfLast { it == model.id }
                if (index != -1) {
                    addBeatSet.removeAt(index)
                }
            }
        }
    }
}