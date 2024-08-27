package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.organization.profile.CategoryListAdapter
import com.app.rupyz.adapter.organization.profile.CategoryListener
import com.app.rupyz.databinding.BottomSheetBeatCustomerFilterBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomerNameRvAdapter
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.staff.StaffViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BeatCustomerFilterBottomSheetDialogFragment : BottomSheetDialogFragment(), CategoryListener,
        StaffNameRvAdapter.IStaffSelectListener, CustomerNameRvAdapter.ICustomerSelectListener {
    private lateinit var binding: BottomSheetBeatCustomerFilterBinding
    private lateinit var customerViewModel: CustomerViewModel
    private val staffViewModel: StaffViewModel by viewModels()

    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var customerAdapter: CustomerNameRvAdapter
    private lateinit var staffNameRvAdapter: StaffNameRvAdapter

    private var customerLevelList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var customerList = ArrayList<CustomerData>()
    private var staffList = ArrayList<NameAndIdSetInfoModel>()

    private var isPageLoading = false
    private var isPageLoadingForStaff = false

    private var isApiLastPage = false
    private var isApiForStaffLastPage = false

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    private var delay: Long = 500 // 1 seconds after user stops typing

    private var customerCurrentPage = 1
    private var staffCurrentPage = 1
    private var orderSelectedIndex = 0

    companion object {
        private lateinit var listener: IBeatCustomerFilterListener
        private var customerLevel = ""
        private var assignedStaff: Pair<Int, String> = Pair(0, "")
        private var parenCustomer: Pair<Int, String> = Pair(0, "")

        @JvmStatic
        fun newInstance(
                listener1: IBeatCustomerFilterListener,
                level: String,
                assignedStaff: Pair<Int, String>,
                parenCustomer: Pair<Int, String>
        ): BeatCustomerFilterBottomSheetDialogFragment {
            val fragment = BeatCustomerFilterBottomSheetDialogFragment()
            listener = listener1
            customerLevel = level
            this.assignedStaff = assignedStaff
            this.parenCustomer = parenCustomer
            return fragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetBeatCustomerFilterBinding.inflate(layoutInflater)
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

        if (arguments?.getBoolean(AppConstant.CUSTOMER_LEVEL, true) == false) {
            binding.groupCustomerLevel.visibility = View.GONE
        }

        binding.groupStaffAssign.visibility = View.GONE

        if (arguments?.getBoolean(AppConstant.STAFF_DETAILS, false) == true) {
            binding.groupStaffAssign.visibility = View.VISIBLE
        }

        initRecyclerView()
        initObservers()

        loadCustomerPage()
        loadCustomerLevel()
        loadStaff()

        if (assignedStaff.first != 0 && assignedStaff.second.isNotEmpty()) {
            binding.tvAssignStaff.text = assignedStaff.second
        }

        if (parenCustomer.first != 0 && parenCustomer.second.isNotEmpty()) {
            binding.tvSeelctedCustomerType.text = parenCustomer.second
            binding.groupCustomerList.visibility = View.VISIBLE
        }

        binding.buttonCancel.setOnClickListener {
            clearFilter()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {

            listener.applyFilter(customerLevel, parenCustomer, assignedStaff)
            dismiss()
        }

        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                resetStaffList()
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
                } else {
                    resetStaffList()
                    loadStaff()
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchStaff.setText("")
            resetStaffList()
            loadStaff()
        }

        binding.tvAssignStaff.setOnClickListener {
            hideSoftKeyboardBottomSheet(binding.tvAssignStaff)
            binding.clAssignStaffList.isVisible = binding.clAssignStaffList.isVisible.not()
            binding.clCustomerList.visibility = View.GONE

        }

        binding.tvSeelctedCustomerType.setOnClickListener {
            binding.clCustomerList.isVisible = binding.clCustomerList.isVisible.not()
            binding.clAssignStaffList.visibility = View.GONE
        }


        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                resetCustomerList()
                loadCustomerPage()
                Utils.hideKeyboard(requireActivity())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForCustomer);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForCustomer, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchCustomer.visibility = View.VISIBLE
                } else {
                    resetCustomerList()
                    loadCustomerPage()
                    binding.ivClearSearchCustomer.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearchCustomer.setOnClickListener {
            binding.etSearchCustomer.setText("")
            resetCustomerList()
            loadCustomerPage()
        }
    }

    private fun resetCustomerList() {
        customerCurrentPage = 1
        isApiLastPage = true
        customerList.clear()
        customerAdapter.notifyDataSetChanged()
    }

    private fun resetStaffList() {
        staffCurrentPage = 1
        isApiForStaffLastPage = false
        staffList.clear()
        staffNameRvAdapter.notifyDataSetChanged()
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

    private fun hideSoftKeyboardBottomSheet(view: View) {
        val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as
                        InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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


    private val inputFinishCheckerForCustomer = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            customerCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerPage()
        }
    }

    private fun clearFilter() {
        orderSelectedIndex = 0
        customerLevel = ""
        assignedStaff = Pair(0, "")
        parenCustomer = Pair(0, "")
        listener.applyFilter(customerLevel, parenCustomer, assignedStaff)
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

        val llM3 = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = llM3
        binding.rvCustomerList.isNestedScrollingEnabled = true
        customerAdapter = CustomerNameRvAdapter(customerList, this)
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object : PaginationScrollListener(llM3) {
            override fun loadMoreItems() {
                isPageLoading = true
                customerCurrentPage += 1
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
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
            isPageLoading = false
            if (data.data.isNullOrEmpty().not()) {
                if (customerCurrentPage == 1) {
                    customerList.clear()
                }
                data.data?.let {
                    customerList.addAll(it)
                    customerAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                }
            } else {
                isApiLastPage = true
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

            binding.tvSeelctedCustomerType.text = ""
            parenCustomer = Pair(0, "")

            if (arguments?.getBoolean(AppConstant.STAFF_DETAILS, false) == true) {
                binding.groupCustomerList.visibility = View.VISIBLE

                binding.hdSelectedCustomerType.text =
                        resources.getString(R.string.select_customer_level_list, model.name)
                binding.etSearchCustomer.hint =
                        resources.getString(R.string.search_with_string, model.name)

                resetCustomerList()
                loadCustomerPage()
            }
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
                hasInternetConnection
        )
    }

    val hasInternetConnection: Boolean
        get() {
            return Connectivity.hasInternetConnection(requireContext())
        }

    interface IBeatCustomerFilterListener {
        fun applyFilter(
                customerLevel: String,
                parentCustomer: Pair<Int, String>,
                assignedStaff: Pair<Int, String>
        )
    }

    override fun onAssignedStaffSelect(model: NameAndIdSetInfoModel, position: Int) {
        assignedStaff = Pair(model.id ?: 0, model.name ?: "")
        binding.tvAssignStaff.text = model.name
        binding.clAssignStaffList.visibility = View.GONE
    }

    override fun onCustomerSelect(model: CustomerData, position: Int) {
        binding.tvSeelctedCustomerType.text = model.name
        binding.clCustomerList.visibility = View.GONE
        parenCustomer = Pair(model.id ?: 0, model.name ?: "")
    }

}