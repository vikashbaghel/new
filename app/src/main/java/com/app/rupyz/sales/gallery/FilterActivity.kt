package com.app.rupyz.sales.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityFilterBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_FILTER
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_ITEM
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_ITEM
import com.app.rupyz.generic.utils.AppConstant.MODULE_TYPE
import com.app.rupyz.generic.utils.AppConstant.STAFF
import com.app.rupyz.generic.utils.AppConstant.STAFF_ITEM
import com.app.rupyz.generic.utils.AppConstant.STATE
import com.app.rupyz.generic.utils.AppConstant.STATE_ITEM
import com.app.rupyz.generic.utils.AppConstant.SUB_MODULE_TYPE
import com.app.rupyz.generic.utils.AppConstant.selected_item
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.gallery.FilterData
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.gallery.adapter.ActivityTypeAdapter
import com.app.rupyz.sales.gallery.adapter.CustomerAdapter
import com.app.rupyz.sales.gallery.adapter.DebounceClickListener
import com.app.rupyz.sales.gallery.adapter.FilterGalleryPicsAdapter
import com.app.rupyz.sales.gallery.adapter.OnItemActivityCheckListener
import com.app.rupyz.sales.gallery.adapter.OnItemCheckCustomerListener
import com.app.rupyz.sales.gallery.adapter.OnItemCheckListener
import com.app.rupyz.sales.gallery.adapter.OnItemStateCheckListener
import com.app.rupyz.sales.gallery.adapter.StaffAdapter
import com.app.rupyz.sales.gallery.adapter.StatesAdapter
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel

import java.util.Locale


class FilterActivity : BaseActivity(), OnItemCheckListener, OnItemCheckCustomerListener,
    OnItemStateCheckListener, OnItemActivityCheckListener, DebounceClickListener {
    private lateinit var binding: ActivityFilterBinding

    private lateinit var adapter: FilterGalleryPicsAdapter
    private lateinit var stateAdapter: StatesAdapter
    private lateinit var activityTypeAdapter: ActivityTypeAdapter
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var staffAdapter: StaffAdapter

    private var currentSelectedStaffItems: ArrayList<Int> = ArrayList()
    private var currentSelectedCustomerItems: ArrayList<Int> = ArrayList()
    private var currentSelectedStateItems: ArrayList<String> = ArrayList()
    private var currentSelectedModulesItems: ArrayList<String?> = ArrayList()
    private var currentSelectedSubModulesItems: ArrayList<String?> = ArrayList()

    private var isApiLastPage = false
    private var isPageLoading = false
    private var currentPage = 1
    private lateinit var filterList: ArrayList<FilterData>
    private var stateList = ArrayList<String>()
    private var customerList = ArrayList<CustomerData>()
    private var staffList = ArrayList<StaffData>()
    private var filterCustomerLevel = ""
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""
    private lateinit var staffRole: String
    private val activityViewModel: StaffActivityViewModel by viewModels()
    var delay: Long = 500
    var selectItem: Int = 0
    var lastTextEdit: Long = 0
    private val activityTypeList: ArrayList<CustomerFeedbackStringItem> = ArrayList()
    var handler: Handler = Handler(Looper.myLooper()!!)
    private val staffViewModel: StaffViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()
    private lateinit var checkboxStateMap: HashMap<String, Boolean>
    private lateinit var checkboxCustomerMap: HashMap<Int, Boolean>
    private lateinit var checkboxStaffMap: HashMap<Int, Boolean>
    private lateinit var checkboxActivityMap: HashMap<Int, Boolean>

    companion object {
        private lateinit var listerPicture: IPictureFilterListener
        private lateinit var listenerStateData: OnItemStateListener
        fun newInstance(
            listener: IPictureFilterListener,
            listenerState: OnItemStateListener
        ): FilterActivity {
            listerPicture = listener
            listenerStateData = listenerState
            return FilterActivity()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(AppConstant.STATE_MAP, checkboxStateMap)
        outState.putSerializable(AppConstant.CUSTOMER_MAP, checkboxCustomerMap)
        outState.putSerializable(AppConstant.STAFF_MAP, checkboxStaffMap)
        outState.putSerializable(AppConstant.ACTIVITY_MAP, checkboxActivityMap)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        checkboxStateMap =
            savedInstanceState.getSerializable(AppConstant.STATE_MAP) as HashMap<String, Boolean>
        checkboxCustomerMap =
            savedInstanceState.getSerializable(AppConstant.CUSTOMER_MAP) as HashMap<Int, Boolean>
        checkboxStaffMap =
            savedInstanceState.getSerializable(AppConstant.STAFF_MAP) as HashMap<Int, Boolean>
        checkboxActivityMap =
            savedInstanceState.getSerializable(AppConstant.ACTIVITY_MAP) as HashMap<Int, Boolean>

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        staffRole = ""
        checkboxStateMap =
            intent.getSerializableExtra(AppConstant.STATE_MAP) as? HashMap<String, Boolean>
                ?: HashMap()
        checkboxCustomerMap =
            intent.getSerializableExtra(AppConstant.CUSTOMER_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()
        checkboxStaffMap =
            intent.getSerializableExtra(AppConstant.STAFF_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()
        checkboxActivityMap =
            intent.getSerializableExtra(AppConstant.ACTIVITY_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()

        currentSelectedStateItems = intent.getSerializableExtra(STATE) as ArrayList<String>
        currentSelectedStaffItems = intent.getSerializableExtra(STAFF) as ArrayList<Int>
        currentSelectedCustomerItems =
            intent.getSerializableExtra(CUSTOMER) as ArrayList<Int>
        currentSelectedModulesItems =
            intent.getSerializableExtra(MODULE_TYPE) as ArrayList<String?>
        currentSelectedSubModulesItems =
            intent.getSerializableExtra(SUB_MODULE_TYPE) as ArrayList<String?>

        binding.imgClose.setOnClickListener {
            listenerStateData.onItemStateHolder(
                checkboxStateMap,
                checkboxCustomerMap,
                checkboxStaffMap,
                checkboxActivityMap
            )
            finish()
            selected_item = 0
        }

        filterData()
        listData()
        initRecyclerView()
        initRecyclerViewStaff()
        initRecyclerViewCustomer()
        initObservers()
        stateList()
        activityViewModel.getFollowUpList()
        binding.btnApply.setOnClickListener {
            listerPicture.applyFilter(
                currentSelectedCustomerItems,
                currentSelectedStaffItems,
                currentSelectedStateItems,
                currentSelectedModulesItems,
                currentSelectedSubModulesItems
            )
            listenerStateData.onItemStateHolder(
                checkboxStateMap,
                checkboxCustomerMap,
                checkboxStaffMap,
                checkboxActivityMap
            )
            finish()
            selected_item = 0

        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                listenerStateData.onItemStateHolder(
                    checkboxStateMap,
                    checkboxCustomerMap,
                    checkboxStaffMap, checkboxActivityMap
                )
                finish()
                selected_item = 0
            }
        })

    }


    private fun activityTypeData() {
        activityTypeList.add(
            CustomerFeedbackStringItem(
                0, AppConstant.ORDER, MODULE_TYPE
            )
        )
        activityTypeList.add(
            CustomerFeedbackStringItem(
                1, AppConstant.PAYMENT, MODULE_TYPE
            )
        )
    }

    private val inputFinishStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadStaffData()
        }
    }

    private val inputFinishState = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            stateList()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadCustomerList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterData() {
        binding.ivSearch.setOnClickListener {
            when (selectItem) {
                STATE_ITEM -> {

                }

                CUSTOMER_ITEM -> {
                    currentPage = 1
                    customerList.clear()
                    customerAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadCustomerList()
                }

                STAFF_ITEM -> {
                    currentPage = 1
                    staffList.clear()
                    staffAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadStaffData()
                }

                ACTIVITY_ITEM -> {
                    activityTypeList.clear()
                    activityTypeAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    activityViewModel.getFollowUpList()

                }

            }

        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                when (selectItem) {
                    STATE_ITEM -> {
                        stateList()
                        Utils.hideKeyboard(this)
                    }

                    STAFF_ITEM -> {
                        currentPage = 1
                        staffList.clear()
                        staffAdapter.notifyDataSetChanged()
                        loadStaffData()
                        Utils.hideKeyboard(this)
                    }

                    CUSTOMER_ITEM -> {
                        currentPage = 1
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                        loadCustomerList()
                        Utils.hideKeyboard(this)
                    }

                    ACTIVITY_ITEM -> {
                        activityTypeList.clear()
                        activityTypeAdapter.notifyDataSetChanged()
                        activityViewModel.getFollowUpList()
                        Utils.hideKeyboard(this)
                    }

                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)
                handler.removeCallbacks(inputFinishStaff)
                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    when (selectItem) {
                        ACTIVITY_ITEM -> {
                            activityViewModel.getFollowUpList()
                        }

                        STATE_ITEM -> {
                            stateList()
                        }

                        CUSTOMER_ITEM -> {
                            loadCustomerList()
                        }

                        STAFF_ITEM -> {
                            loadStaffData()
                        }

                    }

                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    when (selectItem) {
                        STATE_ITEM -> {
                            filterState(s.toString())
                        }

                        CUSTOMER_ITEM -> {
                            lastTextEdit = System.currentTimeMillis()
                            handler.postDelayed(inputFinishChecker, delay)
                        }

                        STAFF_ITEM -> {
                            lastTextEdit = System.currentTimeMillis()
                            handler.postDelayed(inputFinishStaff, delay)
                        }

                        ACTIVITY_ITEM -> {
                            filterActivity(s.toString())
                        }
                    }

                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            when (selectItem) {
                STATE_ITEM -> {
                    stateList()
                }

                CUSTOMER_ITEM -> {
                    binding.clEmptyData.visibility = View.GONE
                    customerList.clear()
                    loadCustomerList()
                    customerAdapter.notifyDataSetChanged()
                }

                STAFF_ITEM -> {
                    binding.clEmptyData.visibility = View.GONE
                    staffList.clear()
                    loadStaffData()
                    staffAdapter.notifyDataSetChanged()
                }

                ACTIVITY_ITEM -> {
                    binding.clEmptyData.visibility = View.GONE
                    activityViewModel.getFollowUpList()
                    activityTypeAdapter.notifyDataSetChanged()


                }
            }
        }

    }


    private fun filterState(text: String) {
        val filteredList: ArrayList<String> = ArrayList()
        for (item in stateList) {
            if (item.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            binding.clEmptyData.visibility = View.VISIBLE
            binding.rvDataView.visibility = View.GONE
        } else {
            binding.clEmptyData.visibility = View.GONE
            binding.rvDataView.visibility = View.VISIBLE
            stateAdapter.filterList(filteredList)

        }

    }

    private fun filterActivity(text: String) {
        val filteredCustomerList: ArrayList<CustomerFeedbackStringItem> = ArrayList()

        for (item in activityTypeList) {
            if (item.stringValue?.lowercase(Locale.ROOT)!!.contains(text.lowercase(Locale.ROOT))) {
                binding.rvDataView.visibility = View.VISIBLE
                filteredCustomerList.add(item)
            }
        }

        if (filteredCustomerList.isEmpty()) {
            binding.clEmptyData.visibility = View.VISIBLE
            binding.rvDataView.visibility = View.GONE
        } else {
            binding.clEmptyData.visibility = View.GONE
            binding.rvDataView.visibility = View.VISIBLE
            activityTypeAdapter.filterActivityList(filteredCustomerList)

        }

    }

    private fun initRecyclerView() {
        adapter = FilterGalleryPicsAdapter(filterList, this)
        binding.rvFilter.adapter = adapter

    }

    private fun loadCustomerList() {
        staffList.clear()
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        customerViewModel.getCustomerList(
            null,
            binding.etSearch.text.toString(),
            filterCustomerLevel,
            filterCustomerType,
            sortByOrder,
            currentPage,
            hasInternetConnection()
        )
    }

    private fun initRecyclerViewCustomer() {
        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvCustomer.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE

        customerAdapter = CustomerAdapter(customerList, this, checkboxCustomerMap)
        binding.rvCustomer.adapter = customerAdapter

        binding.rvCustomer.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }


    private fun initRecyclerViewStaff() {

        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvStaff.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        staffAdapter = StaffAdapter(staffList, this, checkboxStaffMap)
        binding.rvStaff.adapter = staffAdapter
        binding.rvStaff.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadStaffData()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })


    }

    private fun loadStaffData() {
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        staffViewModel.getStaffList(
            staffRole, binding.etSearch.text.toString(), currentPage, hasInternetConnection()
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        staffViewModel.getStaffListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            staffList.clear()
                        }

                        staffList.addAll(it)
                        staffAdapter.notifyDataSetChanged()



                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        staffList.clear()
                        staffAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                showToast(data.message)
            }
        }

        // Customer list observer data
        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false

                        if (currentPage == 1) {
                            customerList.clear()
                        }
                        customerList.addAll(it)
                        customerAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }


        binding.progressBar.visibility = View.VISIBLE
        activityViewModel.getFollowUpListLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { list ->
                    binding.clEmptyData.visibility = View.GONE
                    activityTypeList.clear()
                    if (list.isNotEmpty()) {
                        activityTypeData()
                        activityTypeList.addAll(list)
                    } else {
                        activityTypeData()
                    }
                    activityTypeAdapter =
                        ActivityTypeAdapter(activityTypeList, this, checkboxActivityMap)
                    binding.rvDataView.adapter = activityTypeAdapter
                    Log.e("tag", "" + activityTypeList)
                    activityTypeAdapter.notifyDataSetChanged()

                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                } else {
                    showToast(it.message)
                }
            }
        }
    }


    private fun listData() {
        filterList = ArrayList()
        filterList.add(FilterData(ACTIVITY_FILTER, true))
        filterList.add(FilterData(STATE, false))
        filterList.add(FilterData(CUSTOMER, false))
        filterList.add(FilterData(STAFF, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun stateList() {
        customerList.clear()
        staffList.clear()
        activityTypeList.clear()
        val res: android.content.res.Resources? = resources
        if (res != null) {
            binding.clEmptyData.visibility = View.GONE
            binding.rvDataView.visibility = View.VISIBLE
            binding.rvStaff.visibility = View.GONE
            binding.rvCustomer.visibility = View.GONE
            stateList = res.getStringArray(R.array.states).toList() as ArrayList<String>
            stateList.removeAt(0)
            stateAdapter = StatesAdapter(stateList, this, checkboxStateMap)
            binding.rvDataView.adapter = stateAdapter
            stateAdapter.notifyDataSetChanged()
        }
    }

    interface IPictureFilterListener {
        fun applyFilter(
            customerType: ArrayList<Int>,
            staffType: ArrayList<Int>,
            stateType: ArrayList<String>,
            moduleType: ArrayList<String?>,
            subModuleType: ArrayList<String?>,

            )
    }

    interface OnItemStateListener {
        fun onItemStateHolder(
            checkboxStateMap: HashMap<String, Boolean>,
            checkboxCustomerMap: HashMap<Int, Boolean>,
            checkboxStaffMap: HashMap<Int, Boolean>,
            checkboxActivityMap: HashMap<Int, Boolean>
        )

    }

    override fun onItemCheck(id: Int?) {
        currentSelectedStaffItems.add(id!!)
    }

    override fun onItemUncheck(id: Int?) {
        currentSelectedStaffItems.remove(id)

    }

    override fun onItemCustomerCheck(id: Int?) {
        currentSelectedCustomerItems.add(id!!)
    }

    override fun onItemCustomerUncheck(id: Int?) {
        currentSelectedCustomerItems.clear()
        currentSelectedCustomerItems.remove(id)
    }

    override fun onItemStateCheck(id: String?) {
        currentSelectedStateItems.add(id!!)
    }

    override fun onItemStateUncheck(id: String?) {
        currentSelectedStateItems.remove(id!!)
    }

    override fun onItemActivityCheck(name: String?, type: String) {
        currentSelectedSubModulesItems.add(name!!)
    }

    override fun onItemActivityUncheck(name: String?, type: String) {
        currentSelectedSubModulesItems.remove(name!!)
    }

    override fun onDebounceClick(position: Int, data: FilterData) {
        if (data.isSelected.not()) {
            binding.clEmptyData.visibility = View.GONE
            binding.etSearch.setText("")

            customerList.clear()
            activityTypeList.clear()
            stateList.clear()
            staffList.clear()

            selectItem = position
            currentPage = 1

            customerAdapter.notifyDataSetChanged()
            staffAdapter.notifyDataSetChanged()
            stateAdapter.notifyDataSetChanged()
            activityTypeAdapter.notifyDataSetChanged()

            when (position) {
                0 -> {
                    binding.rvDataView.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvCustomer.visibility = View.GONE
                    activityViewModel.getFollowUpList()
                }

                1 -> {
                    binding.rvDataView.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvCustomer.visibility = View.GONE
                    handler.postDelayed(inputFinishState, delay)

                }

                2 -> {
                    binding.rvCustomer.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    handler.postDelayed(inputFinishChecker, delay)
                }

                3 -> {
                    binding.rvStaff.visibility = View.VISIBLE
                    binding.rvCustomer.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    handler.postDelayed(inputFinishStaff, delay)
                }
            }

            filterList.forEach { it.isSelected = false }
            filterList[position].isSelected = true
            adapter.notifyDataSetChanged()
        }
    }
}

