package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffActivityFilterBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.BeatListDataItem
import com.app.rupyz.model_kt.CustomerFilter
import com.app.rupyz.model_kt.CustomerFilterData
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.sales.customer.CustomerFilterType.*
import com.app.rupyz.sales.customer.adapters.BeatFilterAdapter
import com.app.rupyz.sales.customer.adapters.CustomerFilterTypeAdapter
import com.app.rupyz.sales.customer.adapters.CustomerLevelFilterAdapter
import com.app.rupyz.sales.customer.adapters.CustomerTypeFilterAdapter
import com.app.rupyz.sales.customer.adapters.StaffFilterAdapter
import com.app.rupyz.sales.staff.StaffViewModel

class CustomerFilterActivity : BaseActivity() {
    
    private lateinit var binding: StaffActivityFilterBinding
    private val linearLayoutManager = LinearLayoutManager(this)
    
    
    /***
     * Page No
     * **/
    private var staffCurrentPageNo = 1
    private var beatListPageNo = 1
    private var customerTypePageNo = 1
    /**********************************************************************************************************/
    
    /***
     * Last Page
     * **/
    private var isStaffApiLastPage = false
    private var isBeatApiLastPage = false
    private var isCustomerTypeApiLastPage = false
    private var isStaffPageLoading = false
    private var isBeatPageLoading = false
    private var isCustomerTypePageLoading = false
    /**********************************************************************************************************/
    
    
    /***
     * Adapters
     * **/
    private lateinit var customerFilterTypeAdapter : CustomerFilterTypeAdapter
    private lateinit var beatFilterAdapter : BeatFilterAdapter
    private lateinit var customerTypeFilterAdapter : CustomerTypeFilterAdapter
    private lateinit var customerLevelFilterAdapter : CustomerLevelFilterAdapter
    private lateinit var staffFilterAdapter : StaffFilterAdapter
    /**********************************************************************************************************/
    
    
    /***
     * DataLists
     * **/
    private val beatList = ArrayList<BeatListDataItem>()
    private val customerLevelList = ArrayList<Pair<String, String>>()
    private val customerTypeSpinnerList: MutableList<String> = mutableListOf()
    private val staffList = ArrayList<StaffData>()
    
    
    private var beatSearchText : String = ""
    private var staffSearchText : String = ""
    private var customerTypeSearchText  : String = ""
    /**********************************************************************************************************/
    
    /***
     * ViewModels
     * **/
    private val beatViewModel: BeatViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()
    private val staffViewModel: StaffViewModel  by viewModels()
    /**********************************************************************************************************/
    
    
    /***
     * Filter Type Selector Items
     * */
    private var filterType : CustomerFilterType = BEAT
    private lateinit var filterList: ArrayList<CustomerFilterData>
    /**********************************************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StaffActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        

        populateFilterSelector()
        initRecyclerView()
        initObservers()

        binding.btnApply.setOnClickListener {
            val customerFilter : CustomerFilter = CustomerFilter(selectedBeatList = beatFilterAdapter.getSelectedBeatList(),
                                                                 selectedCustomerLevel = customerLevelFilterAdapter.getSelectedCustomerLevel(),
                                                                 selectedCustomerType = customerTypeFilterAdapter.getSelectedCustomerType(),
                                                                 selectedStaff = staffFilterAdapter.getSelectedStaffList())
            val intent = Intent()
            intent.putExtra(AppConstant.CUSTOMER_FILTER, customerFilter)
            intent.putExtra(AppConstant.SELECTED_CUSTOMER_FILTER, filterType.title)
            setResult(RESULT_OK,intent)
            finish()
        }

        binding.imgClose.setOnClickListener {
            setResult(RESULT_CANCELED, intent)
            finish()
        }

        binding.etSearch.setOnEditorActionListener{ _, actionId ,_ ->
           val s = binding.etSearch.text
           when (filterType) {
               BEAT           -> {
                   beatSearchText = s.toString()
                   getBeatList(1)
               }
               
               CUSTOMER_LEVEL -> {}
               CUSTOMER_TYPE  -> {
                   customerTypeSearchText = s.toString()
                   loadCustomerType(1)
               }
               
               STAFF          -> {
                   staffSearchText = s.toString()
                   getStaffList(1)
               }
           }
            binding.ivClearSearch.visibility = View.VISIBLE
            return@setOnEditorActionListener true
       }
        
        binding.etSearch.addDelayedTextChangeListener(500) { s ->
            if (binding.etSearch.isFocused){
                when (filterType) {
                    BEAT           -> {
                        beatSearchText = s.toString()
                        getBeatList(1)
                    }
                    
                    CUSTOMER_LEVEL -> {}
                    CUSTOMER_TYPE  -> {
                        customerTypeSearchText = s.toString()
                        loadCustomerType(1)
                    }
                    
                    STAFF          -> {
                        staffSearchText = s.toString()
                        getStaffList(1)
                    }
                }
            }
	        if (binding.etSearch.text.isNullOrBlank().not()){
                binding.ivClearSearch.visibility = View.VISIBLE
            }else{
                binding.ivClearSearch.visibility = View.GONE
            }
        }
	    
	    binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText(buildString { append("") })
            when (filterType) {
                BEAT           -> {
                    binding.ivClearSearch.visibility = View.VISIBLE
                    beatSearchText = ""
                    getBeatList(1)
                }
                CUSTOMER_LEVEL -> {}
                CUSTOMER_TYPE  -> {
                    binding.ivClearSearch.visibility = View.VISIBLE
                    customerTypeSearchText = ""
                    loadCustomerType(1)
                }
                STAFF          -> {
                    binding.ivClearSearch.visibility = View.VISIBLE
                    staffSearchText = ""
                    getStaffList(1)
                }
            }
        }
        
        binding.buttonClearFilter.setOnClickListener{
            val customerFilter : CustomerFilter = CustomerFilter(selectedBeatList = mutableSetOf(),
                                                                 selectedCustomerLevel = mutableSetOf(),
                                                                 selectedCustomerType = mutableSetOf(),
                                                                 selectedStaff = mutableSetOf())
            val intent = Intent()
            intent.putExtra(AppConstant.CUSTOMER_FILTER, customerFilter)
            setResult(RESULT_OK,intent)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED, intent)
                finish()
            }
        })
        
        binding.rvDataView.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                when (filterType) {
                    BEAT           -> {
                        isBeatPageLoading = true
                        getBeatList(beatListPageNo + 1)
                    }
                    
                    CUSTOMER_LEVEL -> {}
                    CUSTOMER_TYPE  -> {
                        isCustomerTypePageLoading = true
                        loadCustomerType(customerTypePageNo + 1)
                    }
                    
                    STAFF          -> {
                        isStaffPageLoading = true
                        getStaffList(staffCurrentPageNo + 1)
                    }
                }
            }
            
            override fun isLastPage(): Boolean {
               return when(filterType){
                   BEAT           -> isBeatApiLastPage
                   CUSTOMER_LEVEL -> true
                   CUSTOMER_TYPE  -> isCustomerTypeApiLastPage
                   STAFF          -> isStaffApiLastPage
               }
            }
            
            override fun isLoading(): Boolean {
                return when(filterType){
                    BEAT           -> isBeatPageLoading
                    CUSTOMER_LEVEL -> false
                    CUSTOMER_TYPE  -> isCustomerTypePageLoading
                    STAFF          -> isStaffPageLoading
                }
            }
        })
        
    }
    
    
    private fun initRecyclerView() {
        beatFilterAdapter = BeatFilterAdapter()
        customerTypeFilterAdapter = CustomerTypeFilterAdapter()
        customerLevelFilterAdapter = CustomerLevelFilterAdapter()
        staffFilterAdapter = StaffFilterAdapter()
        if (intent.hasExtra(AppConstant.SELECTED_CUSTOMER_FILTER)){
            val selectedFilter = intent.getStringExtra(AppConstant.SELECTED_CUSTOMER_FILTER)?:""
            filterType = if (enumContains<CustomerFilterType>(selectedFilter)){
                CustomerFilterType.valueOf(selectedFilter)
            }else{
                BEAT
            }
            changeFilterType(filterType)
        } else{
            filterType = BEAT
            changeFilterType(filterType)
        }
        if (intent.hasExtra(AppConstant.CUSTOMER_FILTER)){
            val filterData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra<CustomerFilter?>(AppConstant.CUSTOMER_FILTER, CustomerFilter::class.java)
            }else{
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<CustomerFilter?>(AppConstant.CUSTOMER_FILTER) as CustomerFilter?
            }
            filterData?.let {  selectedData ->
                beatFilterAdapter.setSelectedBeats(selectedData.selectedBeatList)
                customerLevelFilterAdapter.setSelectedCustomerLevel(selectedData.selectedCustomerLevel)
                customerTypeFilterAdapter.setSelectedCustomerType(selectedData.selectedCustomerType)
                staffFilterAdapter.setSelectedStaff(selectedData.selectedStaff)
            }
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        beatViewModel.beatListLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            isBeatPageLoading = false
            if (it.error == false) {
                isBeatApiLastPage = false
                if (it.data.isNullOrEmpty().not()) {
                    if (beatListPageNo == 1) {
                        beatList.clear()
                    }
                    beatList.addAll(it.data!!)
                    setBeadListAdapter(beatListPageNo == 1,it.data!!.toMutableList())
                    if (it.data.size < 30) {
                        isBeatApiLastPage = true
                    }
                } else {
                    isBeatApiLastPage = true
                    if (beatListPageNo == 1) {
                        beatList.clear()
                        if (filterType == BEAT){
                            binding.clEmptyData.visibility = View.VISIBLE
                        }
                        setBeadListAdapter(true, mutableListOf())
                    }
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                } else {
                    showToast(it.message)
                }
            }
        }
        
        customerLevelList.clear()
        customerLevelList.add(Pair(AppConstant.CUSTOMER_LEVEL_1, SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)?:binding.root.context.resources.getString(R.string.primary_customer)))
        customerLevelList.add(Pair(AppConstant.CUSTOMER_LEVEL_2, SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)?:binding.root.context.resources.getString(R.string.distributor)))
        customerLevelList.add(Pair(AppConstant.CUSTOMER_LEVEL_3, SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)?:binding.root.context.resources.getString(R.string.retailer)))
        setCustomerLevelAdapter(true,customerLevelList)
        
        customerViewModel.customerTypeLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            data.data?.let { customerDataList ->
                isCustomerTypePageLoading = false
                if (customerDataList.isNotEmpty()) {
                    if (customerTypePageNo == 1) {
                        this.customerTypeSpinnerList.clear()
                    }
                    val newData = mutableListOf<String>()
                    customerDataList.forEach { customerData ->
                        this.customerTypeSpinnerList.add(customerData.name ?: "")
                        newData.add(customerData.name ?: "")
                    }
                    
                    if (customerTypePageNo == 1){
                        setCustomerTypeSpinnerItems(customerTypePageNo == 1, newData)
                    }
                    isCustomerTypeApiLastPage = false
                }else{
                    isCustomerTypeApiLastPage = true
                    if (customerTypePageNo == 1) {
                        customerTypeSpinnerList.clear()
                        if (filterType == CUSTOMER_TYPE){
                            binding.clEmptyData.visibility = View.VISIBLE
                        }
                        setCustomerTypeSpinnerItems(customerTypePageNo == 1, arrayListOf())
                    }
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }
            }
        }
        
        staffViewModel.getStaffListData().observe(this) {
            binding.progressBar.visibility = View.GONE
            isStaffPageLoading = false
            if (it.data.isNullOrEmpty().not()) {
                binding.tvErrorMessage.visibility = View.GONE
                if (staffCurrentPageNo == 1) {
                    staffList.clear()
                }
                staffList.addAll(it.data!!)
                if (it.data!!.size < 30) {
                    isStaffApiLastPage = true
                }
                it.data?.let { list -> setStaffAdapter(staffCurrentPageNo == 1,list.toMutableList() ) }
            } else {
                isStaffApiLastPage = true
                if (staffCurrentPageNo == 1) {
                    staffList.clear()
                    if (filterType == STAFF){
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                    setStaffAdapter(true,mutableListOf())
                }
                binding.tvErrorMessage.visibility = View.VISIBLE
            }
        }
    }
    
    private fun populateFilterSelector() {
        filterList = ArrayList()
        CustomerFilterType.values().forEach { value ->
            filterList.add(CustomerFilterData(value.title, value == filterType,value))
        }
        customerFilterTypeAdapter = CustomerFilterTypeAdapter(filterList)
        binding.rvFilter.adapter = customerFilterTypeAdapter
        customerFilterTypeAdapter.setOnDebounceClickListener { position, data ->
            filterType = data.type
            changeFilterType(filterType)
        }
    }
    
    private fun changeFilterType(type : CustomerFilterType) {
        when(type){
            BEAT           -> {
                binding.etSearch.showView()
                binding.ivSearch.showView()
                binding.clEmptyData.hideView()
                binding.etSearch.setText(beatSearchText)
                if (beatListPageNo == 1){
                    if (isBeatPageLoading){
                        binding.progressBar.showView()
                        binding.rvDataView.adapter = beatFilterAdapter
                    }else{
                        binding.progressBar.hideView()
                        binding.rvDataView.adapter = beatFilterAdapter
                        if (isBeatApiLastPage.not()){
                            getBeatList(1)
                        }else{
                            if (beatList.isEmpty()){
                                binding.clEmptyData.visibility = View.VISIBLE
                            }
                        }
                    }
                    
                }else{
                    binding.progressBar.hideView()
                    if (beatList.isNotEmpty()){
                        binding.rvDataView.adapter = beatFilterAdapter
                    }else{
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            }
            CUSTOMER_LEVEL -> {
                binding.progressBar.hideView()
                binding.etSearch.hideView()
                binding.ivSearch.hideView()
                binding.rvDataView.adapter = customerLevelFilterAdapter
            }
            CUSTOMER_TYPE  -> {
                binding.etSearch.showView()
                binding.ivSearch.showView()
                binding.etSearch.setText(customerTypeSearchText)
                binding.clEmptyData.hideView()
                if (customerTypePageNo == 1){
                    if (isCustomerTypePageLoading){
                        binding.progressBar.showView()
                        binding.rvDataView.adapter = customerTypeFilterAdapter
                    }else{
                        binding.progressBar.hideView()
                        binding.rvDataView.adapter = customerTypeFilterAdapter
                        if (isCustomerTypeApiLastPage.not()){
                            loadCustomerType(1)
                        }else{
                            if (customerTypeSpinnerList.isEmpty()){
                                binding.clEmptyData.visibility = View.VISIBLE
                            }
                        }
                    }
                    
                }else{
                    binding.progressBar.hideView()
                    if (customerTypeSpinnerList.isNotEmpty()){
                        binding.rvDataView.adapter = customerTypeFilterAdapter
                    }else{
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            }
            STAFF          -> {
                binding.etSearch.showView()
                binding.ivSearch.showView()
                binding.clEmptyData.hideView()
                binding.etSearch.setText(staffSearchText)
                if (staffCurrentPageNo == 1){
                    if (isStaffPageLoading){
                        binding.progressBar.showView()
                        binding.rvDataView.adapter = staffFilterAdapter
                    }else{
                        binding.progressBar.hideView()
                        binding.rvDataView.adapter = staffFilterAdapter
                        if (isStaffApiLastPage.not()){
                            getStaffList(1)
                        }else{
                            if (staffList.isEmpty()){
                                binding.clEmptyData.visibility = View.VISIBLE
                            }
                        }
                    }
                    
                }else{
                    binding.progressBar.hideView()
                    if (staffList.isNotEmpty()){
                        binding.rvDataView.adapter = staffFilterAdapter
                    }else{
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    
    private fun setBeadListAdapter(clearAdapter : Boolean, items : MutableList<BeatListDataItem>) {
        if (clearAdapter){
            beatFilterAdapter.setBeats(items)
        }else{
            beatFilterAdapter.addBeats(items)
        }
    }
    
    private fun setCustomerTypeSpinnerItems(clearAdapter : Boolean, list : MutableList<String>) {
        if (clearAdapter){
            customerTypeFilterAdapter.setCustomerType(list)
        }else{
            customerTypeFilterAdapter.addCustomerType(list)
        }
    }
    
    private fun setStaffAdapter(clearAdapter : Boolean, list : MutableList<StaffData>) {
        if (clearAdapter){
            staffFilterAdapter.setStaffList(list)
        }else{
            staffFilterAdapter.addStaffList(list)
        }
    }
    
    private fun setCustomerLevelAdapter(clearAdapter : Boolean, list : MutableList<Pair<String, String>>) {
        if (clearAdapter){
            customerLevelFilterAdapter.setCustomerLevel(list)
        }else{
            customerLevelFilterAdapter.addCustomerLevel(list)
        }
    }
    
    private fun getStaffList(staffCurrentPageNo : Int) {
        if (filterType == STAFF){
            if (staffCurrentPageNo == 1) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        this.staffCurrentPageNo = staffCurrentPageNo
        isStaffPageLoading = true
        staffViewModel.getStaffList(null, staffSearchText, staffCurrentPageNo, hasInternetConnection(),true)
    }
    
    private fun loadCustomerType(customerTypePageNo: Int) {
        if (filterType == CUSTOMER_TYPE) {
            if (customerTypePageNo == 1) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        this.customerTypePageNo = customerTypePageNo
        isCustomerTypePageLoading = true
        customerViewModel.getCustomerTypeList(customerTypeSearchText, customerTypePageNo, Connectivity.hasInternetConnection(this))
    }
    
    private fun getBeatList(beatListPageNo : Int) {
        if (filterType == BEAT) {
            if (beatListPageNo == 1) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        this.beatListPageNo = beatListPageNo
        beatViewModel.getBeatList(beatSearchText, beatListPageNo,true)
    }
    
}

