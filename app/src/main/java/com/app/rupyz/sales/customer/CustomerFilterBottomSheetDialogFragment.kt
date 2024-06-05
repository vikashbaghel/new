package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.organization.profile.CategoryListAdapter
import com.app.rupyz.adapter.organization.profile.CategoryListener
import com.app.rupyz.databinding.BottomSheetCustomerFilterBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.beatplan.StaffNameRvAdapter
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.ui.organization.profile.adapter.CustomAutoCompleteAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CustomerFilterBottomSheetDialogFragment : BottomSheetDialogFragment(), CategoryListener,
    StaffNameRvAdapter.IStaffSelectListener {
    private lateinit var binding: BottomSheetCustomerFilterBinding
    private lateinit var customerViewModel: CustomerViewModel
    private val staffViewModel: StaffViewModel by viewModels()

    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var customerAdapter: CustomerNameRvAdapter
    private lateinit var staffNameRvAdapter: StaffNameRvAdapter

    private var customerLevelList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var customerList = ArrayList<CustomerData>()
    private var staffList = ArrayList<NameAndIdSetInfoModel>()
    private var customerTypeList = ArrayList<CustomerTypeDataItem>()

    private var isPageLoading = false
    private var isPageLoadingForStaff = false
    private var isApiLastPage = false
    private var isApiForStaffLastPage = false

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    private var delay: Long = 500 // 1 seconds after user stops typing

    private var customerCurrentPage = 1
    private var staffCurrentPage = 1
    private var customerTypeCurrentPage = 1
    private var orderSelectedIndex = 0

    companion object {
        private lateinit var listener: IBeatCustomerFilterListener
        private var customerLevel = ""
        private var assignedStaff: Pair<Int, String> = Pair(0, "")
        private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()

        @JvmStatic
        fun newInstance(
            listener1: IBeatCustomerFilterListener,
            level: String,
            assignedStaff: Pair<Int, String>,
            filterCustomerType: ArrayList<CustomerTypeDataItem>
        ): CustomerFilterBottomSheetDialogFragment {
            val fragment = CustomerFilterBottomSheetDialogFragment()
            listener = listener1
            customerLevel = level
            Companion.assignedStaff = assignedStaff
            Companion.filterCustomerType = filterCustomerType
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetCustomerFilterBinding.inflate(layoutInflater)
        setUpListener()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView = mDialog.findViewById<View>(R.id.design_bottom_sheet)
            var bottomSheetBehavior: BottomSheetBehavior<View>
            bottomSheetView?.let {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
                bottomSheetBehavior.isDraggable = false
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.halfExpandedRatio = 0.6f
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        binding.groupCustomerLevel.visibility = View.VISIBLE

        binding.groupStaffAssign.visibility = View.GONE

        binding.groupCustomerType.visibility = View.VISIBLE

        initRecyclerView()
        initObservers()

        customerViewModel.getCustomerTypeList("", customerTypeCurrentPage, hasInternetConnection)

        loadCustomerLevel()
        loadStaff()

        if (assignedStaff.first != 0 && assignedStaff.second.isNotEmpty()) {
            binding.tvAssignStaff.text = assignedStaff.second
        }

        if (filterCustomerType.isEmpty().not()) {
            filterCustomerType.forEach {
                addNewChip(it)
            }
        }

        binding.buttonCancel.setOnClickListener {
            clearFilter()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            val customerType: ArrayList<CustomerTypeDataItem> = ArrayList()
            for (item in 0 until binding.chipGroup.childCount) {
                val chip: Chip = binding.chipGroup.getChildAt(item) as Chip
                customerType.add(
                    CustomerTypeDataItem(
                        name = chip.text.toString(),
                        id = chip.id
                    )
                )
            }

            listener.applyFilter(customerLevel, customerType, assignedStaff)
            dismiss()
        }

        binding.actvCustomerType.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                extendBottomSheet()
            }
        }

        binding.actvCustomerType.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                val index =
                    customerTypeList.indexOfFirst { it.name == binding.actvCustomerType.text.toString() }
                if (index != -1) {
                    val customerTypeModel = customerTypeList[index]

                    addNewChip(customerTypeModel)
                    binding.actvCustomerType.setText("")
                }
            }

        binding.actvCustomerType.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.actvCustomerType.showDropDown()
                binding.clAssignStaffList.visibility = View.GONE
            }
        }

        binding.actvCustomerType.setOnClickListener {
            setUpListener()
            binding.actvCustomerType.showDropDown()
            binding.clAssignStaffList.visibility = View.GONE
        }


        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                staffCurrentPage = 1
                isApiForStaffLastPage = true
                staffList.clear()
                loadStaff()
                Utils.hideKeyboard(requireActivity())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchStaff.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForStaff);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForStaff, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                    loadStaff()
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchStaff.setText("")
            staffCurrentPage = 1
            isApiForStaffLastPage = false
            staffList.clear()
            staffNameRvAdapter.notifyDataSetChanged()
            loadStaff()
        }

        binding.tvAssignStaff.setOnClickListener {
            binding.actvCustomerType.clearFocus()
            hideSoftKeyboardBottomSheet(binding.tvAssignStaff)
            binding.clAssignStaffList.isVisible = binding.clAssignStaffList.isVisible.not()

        }
    }

    private fun loadCustomerLevel() {

        val model = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                .not()
        ) {
            model.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
        }
        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_1
        ) {
            model.isSelected = true
        }
        customerLevelList.add(model)

        val model2 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                .not()
        ) {
            model2.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
        }

        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_2
        ) {
            model2.isSelected = true
        }
        customerLevelList.add(model2)

        if (arguments?.getBoolean(
                AppConstant.STAFF_DETAILS,
                false
            ) == null || arguments?.getBoolean(
                AppConstant.STAFF_DETAILS,
                false
            ) == false
        ) {
            val model3 = AllCategoryResponseModel()
            if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                    .not()
            ) {
                model3.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
            }
            if (customerLevel.isEmpty()
                    .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_3
            ) {
                model3.isSelected = true
            }
            customerLevelList.add(model3)
        }

        categoryListAdapter.notifyDataSetChanged()
    }

    private fun setBottomSheetExpanded(bottomSheetDialog: Dialog?) {
        val bottomSheet =
            bottomSheetDialog?.findViewById<View>(R.id.cl_filter) as ConstraintLayout?
        bottomSheet?.let {
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            val layoutParams = bottomSheet.layoutParams
            bottomSheet.layoutParams = layoutParams
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

    }

    private fun hideSoftKeyboardBottomSheet(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as
                    InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun addNewChip(model: CustomerTypeDataItem) {
        val inflater = LayoutInflater.from(requireContext())

        val newChip =
            inflater.inflate(
                R.layout.layout_chip_customer_type,
                binding.chipGroup,
                false
            ) as Chip
        newChip.text = model.name
        newChip.transitionName = model.name
        newChip.tag = model.id

        newChip.isCloseIconVisible = true

        newChip.setOnCloseIconClickListener {
            handleChipCloseIconClicked(newChip)
        }
        binding.chipGroup.addView(newChip)
    }

    private val inputFinishCheckerForStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            staffCurrentPage = 1
            isApiForStaffLastPage = false
            isPageLoadingForStaff = true
            staffList.clear()
            staffNameRvAdapter.notifyDataSetChanged()
            loadStaff()
        }
    }

    private fun handleChipCloseIconClicked(chip: Chip) {
        val parent = chip.parent as ChipGroup
        parent.removeView(chip)
    }

    private fun extendBottomSheet() {
        val mDialog = dialog as BottomSheetDialog
        val bottomSheetView = mDialog.findViewById<View>(R.id.design_bottom_sheet)
        var bottomSheetBehavior: BottomSheetBehavior<View>
        bottomSheetView?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun clearFilter() {
        orderSelectedIndex = 0
        customerLevel = ""
        assignedStaff = Pair(0, "")
        filterCustomerType = ArrayList()
        listener.applyFilter(customerLevel, filterCustomerType, assignedStaff)
        dismiss()
    }


    private fun loadStaff() {
        staffViewModel.getStaffList(
                "",
                binding.etSearchStaff.text.toString(),
                staffCurrentPage,
                hasInternetConnection
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        binding.rvCustomerType.setHasFixedSize(true)
        binding.rvCustomerType.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryListAdapter = CategoryListAdapter(customerLevelList, this)
        binding.rvCustomerType.adapter = categoryListAdapter

        val llM2 = LinearLayoutManager(requireContext())
        binding.rvStaffList.layoutManager = llM2
        binding.rvStaffList.isNestedScrollingEnabled = true
        staffNameRvAdapter = StaffNameRvAdapter(staffList, this)
        binding.rvStaffList.adapter = staffNameRvAdapter

        binding.rvStaffList.addOnScrollListener(object : PaginationScrollListener(llM2) {
            override fun loadMoreItems() {
                isPageLoadingForStaff = true
                staffCurrentPage += 1
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiForStaffLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForStaff
            }
        })

        disableBottomSheetDrag()
    }

    private fun disableBottomSheetDrag() {
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }


    private fun initObservers() {

        customerViewModel.getCustomerListData().observe(this) { data ->
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
                }
            }
        }

        customerViewModel.customerTypeLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->

                    if (data.isNotEmpty()) {
                        customerTypeList.addAll(data)
                        val str: MutableList<String> = java.util.ArrayList()
                        data.forEach { type ->
                            str.add(type.name!!)
                        }
                        if (data.size == 30) {
                            customerTypeCurrentPage++
                            customerViewModel.getCustomerTypeList(
                                "",
                                customerTypeCurrentPage,
                                hasInternetConnection
                            ) 
                        } else {
                            val adapter = CustomAutoCompleteAdapter(requireContext(), str)
                            binding.actvCustomerType.threshold = 1
                            binding.actvCustomerType.setAdapter(adapter)
                        }
                    }
                }
            }
        }

        staffViewModel.getStaffListData().observe(this) {
            isPageLoadingForStaff = false
            if (!it.data.isNullOrEmpty()) {
                if (staffCurrentPage == 1) {
                    staffList.clear()
                }

                it.data?.forEach { staff ->
                    staffList.add(NameAndIdSetInfoModel(staff.id, staff.name))
                }

                staffNameRvAdapter.notifyDataSetChanged()

                if (it.data!!.size < 30) {
                    isApiForStaffLastPage = true
                }
            } else {
                isApiForStaffLastPage = true
            }
        }
    }

    override fun onCategorySelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected == false) {
            for (i in customerLevelList.indices) {
                customerLevelList[i].isSelected = false
            }

            customerLevel = customerLevelList[position].name.toString()
            customerLevelList[position].isSelected = true
            categoryListAdapter.notifyDataSetChanged()

            when (position) {
                0 -> customerLevel = AppConstant.CUSTOMER_LEVEL_1
                1 -> customerLevel = AppConstant.CUSTOMER_LEVEL_2
                2 -> customerLevel = AppConstant.CUSTOMER_LEVEL_3
            }
        }
    }

    interface IBeatCustomerFilterListener {
        fun applyFilter(
            customerLevel: String,
            customerType: ArrayList<CustomerTypeDataItem>,
            assignedStaff: Pair<Int, String>
        )
    }

    val hasInternetConnection: Boolean
        get() {
            return Connectivity.hasInternetConnection(requireContext())
        }

    override fun onAssignedStaffSelect(model: NameAndIdSetInfoModel, position: Int) {
        assignedStaff = Pair(model.id ?: 0, model.name ?: "")
        binding.tvAssignStaff.text = model.name
        binding.clAssignStaffList.visibility = View.GONE
    }
}