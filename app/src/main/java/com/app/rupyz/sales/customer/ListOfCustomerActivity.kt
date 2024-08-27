package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.ActivityListOfCustomerBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.dialog.DeleteDialogFragment.IDeleteDialogListener
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.checkIn.CheckInDialogFragment
import com.app.rupyz.dialog.checkIn.CheckOutAlert
import com.app.rupyz.dialog.checkIn.CheckOutConfiramation
import com.app.rupyz.dialog.checkIn.CheckedInDialogFragment
import com.app.rupyz.dialog.checkIn.ICheckInClickListener
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_ID
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.AppConstant.IS_TELEPHONIC_ORDER
import com.app.rupyz.generic.utils.AppConstant.ORDER_MESSAGE
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CustomerFilter
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.sales.beatplan.SelectCustomerForBeatPlanFragment
import com.app.rupyz.sales.beatplan.SortByBottomSheetDialogFragment
import com.app.rupyz.sales.customer.ListOfAllCustomerAdapter.LIST_OF_CUSTOMER_ACTIONS
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.more.MoreViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class ListOfCustomerActivity : BaseActivity(), CustomerActionListener,
    SortByBottomSheetDialogFragment.ISortByCustomerListener,
    CustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener {
    private lateinit var binding: ActivityListOfCustomerBinding
    private lateinit var customerAdapter: ListOfAllCustomerAdapter
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var moreViewModel: MoreViewModel
    private var customerList = ArrayList<CustomerData>()

    private var customerID: Int? = null
    private var customerName: String? = null
    private var isDataChange = false

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    private var customerInActivePosition = -1
    private var customerInActiveModel: CustomerData? = null

    private var customerLevel = ""
    private var assignedStaffFilterApply = false

    private var isDeleteCustomerDialogShow = false

    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterCustomerLevel = ""
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""

    private var filterCount = 0
    private var levelFilterApply = false
    private var customerTypeFilterApply = false

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)
    private val chooseActivityBottomSheet = ChooseActivityBottomSheet()
    private var selectedCustomerData: CustomerData? = null
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null
    private var isOrderStatus = false

    /**
     *
     * Location Gathering Part
     *
     */
    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentGeoLocationLat: Double = 0.00
    private var currentGeoLocationLong: Double = 0.00
    private var currentGeoAddress: String? = null
    /**************************************************************/

    /**
     *
     * Filter Launcher
     *
     */
    private var customerFilter: CustomerFilter? = null
    private var lastSelectedFilter: CustomerFilterType? = null
    private var customerListFilterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                isDataChange = true
                currentPage = 1
                customerFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra<CustomerFilter?>(
                        AppConstant.CUSTOMER_FILTER,
                        CustomerFilter::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    (result.data?.getParcelableExtra<CustomerFilter?>(AppConstant.CUSTOMER_FILTER))
                }
                val selectedFilter =
                    result.data?.getStringExtra(AppConstant.SELECTED_CUSTOMER_FILTER) ?: ""
                lastSelectedFilter = if (enumContains<CustomerFilterType>(selectedFilter)) {
                    CustomerFilterType.valueOf(selectedFilter)
                } else {
                    null
                }
                customerList.clear()
                loadCustomerList()
            }
        }

    /**************************************************************/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        /**
         *
         * Location Gathering Part
         *
         */
        locationPermissionUtils = LocationPermissionUtils(this, this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getUserCurrentLocation()
        /********************************************************/


        initLayout()
        initRecyclerView()
        initObservers()
        loadCustomerList()


        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(
                supportFragmentManager,
                SelectCustomerForBeatPlanFragment::class.java.name
            )
        }

        binding.tvFilter.setOnClickListener {

            val intent = Intent(this@ListOfCustomerActivity, CustomerFilterActivity::class.java)
            intent.putExtra(AppConstant.CUSTOMER_FILTER, customerFilter)
            intent.putExtra(AppConstant.SELECTED_CUSTOMER_FILTER, lastSelectedFilter?.title)
            customerListFilterLauncher.launch(intent)

            /* val fragment = CustomerFilterBottomSheetDialogFragment.newInstance(
                this,
                filterCustomerLevel,
                filterAssignedStaff,
                filterCustomerType
            )
            fragment.show(
                supportFragmentManager,
                SelectCustomerForBeatPlanFragment::class.java.name
            )*/
        }

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            Utils.hideKeyboard(this)
            loadCustomerList()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                customerList.clear()
                customerAdapter.notifyDataSetChanged()
                loadCustomerList()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    loadCustomerList()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            binding.clEmptyData.visibility = View.GONE

            customerList.clear()
            customerAdapter.notifyDataSetChanged()
        }

        binding.ivAddCustomer.isVisible =
            PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_CUSTOMER_PERMISSION, false)

        isOrderStatus = intent.getBooleanExtra(AppConstant.ORDER_STATUS, false)
        if (isOrderStatus == true) {
            val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
            CheckOutConfiramation.showCheckOutConfirmationDialog(
                this,
                "customerData?.name!!",
                intent.getStringExtra(ORDER_MESSAGE)!!,
                intent.getIntExtra(CUSTOMER_ID, 0),
                null,
                checkSetting
            )
        }
    }

    private fun openActivityChooser(customerData: CustomerData) {
        chooseActivityBottomSheet.setCustomerData(customerData)
        chooseActivityBottomSheet.setModuleType(AppConstant.CUSTOMER_FEEDBACK)
        chooseActivityBottomSheet.setOnCreateOrderListener {

            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.CREATE_ORDER_PERMISSION,
                    false
                )
            ) {
                SharedPref.getInstance().clearCart()

                val selectedStep = customerData.customerLevel?.let {
                    when (it) {
                        AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                        AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                        AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                        else -> null
                    }
                }
                if (((selectedStep == null) || selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) && SharedPref.getInstance()
                        .getBoolean(CUSTOMER_LEVEL_ORDER, false)
                ) {
                    getCustomerInfoResultLauncher.launch(
                        Intent(this@ListOfCustomerActivity, CustomerDetailActivity::class.java)
                            .putExtra(CUSTOMER_ID, customerData.id)
                            .putExtra(AppConstant.CUSTOMER_TYPE, customerData.customerLevel)
                            .putExtra(AppConstant.DISTRIBUTOR_SELECTOR, true)
                            .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)

                    )
                } else {
                    customerData.let { customerData ->
                        startActivity(
                            Intent(
                                this@ListOfCustomerActivity,
                                CreateNewOrderForCustomerActivity::class.java
                            ).putExtra(
                                AppConstant.CUSTOMER,
                                customerData
                            )
                                .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                .putExtra(CUSTOMER_ID, customerData.id)
                                .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)
                        )
                    }
                }

            } else {
                Toast.makeText(
                    this@ListOfCustomerActivity,
                    resources.getString(R.string.create_order_permission),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        chooseActivityBottomSheet.show(
            supportFragmentManager,
            ChooseActivityBottomSheet::class.java.name
        )

    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            binding.rvCustomerList.visibility = View.GONE
            loadCustomerList()
        }
    }

    private fun initLayout() {
        binding.ivAddCustomer.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this@ListOfCustomerActivity,
                    NewAddCustomerActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_TYPE, customerLevel)
            )
        }

        moreViewModel.getPreferencesInfo()

        customerViewModel.checkOfflineCustomerWithErrorList()

        SharedPref.getInstance().clearCart()

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, SalesMainActivity::class.java))
            // finish()
            //onBackPressed()
        }
    }

    private fun loadCustomerList() {
        if (currentPage == 1) {
            binding.shimmerCustomer.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        isApiLastPage = false
        binding.clEmptyData.visibility = View.GONE
        val customerFilterParams: HashMap<String, String> = hashMapOf()
        customerFilter?.let { customerFilter ->
            if (customerFilter.selectedBeatList.isEmpty().not()) {
                if (customerFilter.selectedBeatList.size == 1) {
                    customerFilterParams.put(
                        "beat_ids",
                        customerFilter.selectedBeatList.first().toString()
                    )
                } else {
                    customerFilterParams.put(
                        "beat_ids",
                        customerFilter.selectedBeatList.joinToString(",")
                    )
                }
            }
            if (customerFilter.selectedCustomerLevel.isEmpty().not()) {
                if (customerFilter.selectedCustomerLevel.size == 1) {
                    customerFilterParams.put(
                        "customer_level",
                        customerFilter.selectedCustomerLevel.first().toString()
                    )
                } else {
                    customerFilterParams.put(
                        "customer_level",
                        customerFilter.selectedCustomerLevel.joinToString(",")
                    )
                }
            }
            if (customerFilter.selectedCustomerType.isEmpty().not()) {
                if (customerFilter.selectedCustomerType.size == 1) {
                    customerFilterParams.put(
                        "customer_type",
                        customerFilter.selectedCustomerType.first().toString()
                    )
                } else {
                    customerFilterParams.put(
                        "customer_type",
                        customerFilter.selectedCustomerType.joinToString(",")
                    )
                }
            }
            if (customerFilter.selectedStaff.isEmpty().not()) {
                if (customerFilter.selectedStaff.size == 1) {
                    customerFilterParams.put(
                        "staff_ids",
                        customerFilter.selectedStaff.first().toString()
                    )
                } else {
                    customerFilterParams.put(
                        "staff_ids",
                        customerFilter.selectedStaff.joinToString(",")
                    )
                }
            }
        }

        customerViewModel.getCustomerListFiltered(
            null,
            binding.etSearch.text.toString(),
            null,
            filterCustomerType,
            sortByOrder,
            currentPage,
            customerFilterParams,
            hasInternetConnection()
        )
    }


    var someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                isDataChange = true
                currentPage = 1
                customerList.clear()
                loadCustomerList()
            }
        }

    var transferCustomerActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                //DeleteDialog.showDeleteDialog(this,"",0,AppConstant.Delete.DELETE_CUSTOMER,this)
                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
            }
        }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerAdapter = ListOfAllCustomerAdapter(
            customerList,
            this,
            supportFragmentManager,
            true,
            hasInternetConnection(),
            true
        )
        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object :
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

        customerAdapter.setOnOptionClickListener(object :
                (CustomerData, LIST_OF_CUSTOMER_ACTIONS) -> Unit {
            override fun invoke(customeData: CustomerData, type: LIST_OF_CUSTOMER_ACTIONS) {
                when (type) {
                    LIST_OF_CUSTOMER_ACTIONS.CHOSE_ACTIVITY -> {
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            checkGeoFencingAndOpenChooser(customeData)
                        } else {
                            showStartDayDialog(object : (Boolean) -> Unit {
                                override fun invoke(onSuccessStartDay: Boolean) {
                                    if (onSuccessStartDay) {
                                        if (SharedPref.getInstance()
                                                .getBoolean(AppConstant.START_DAY, false)
                                        ) {
                                            checkGeoFencingAndOpenChooser(customeData)
                                        }
                                    }
                                }
                            })
                        }
                    }

                    LIST_OF_CUSTOMER_ACTIONS.CHECK_IN -> {
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            checkGeoFencingAndStartCheckIn(customeData)
                        } else {
                            showStartDayDialog(object : (Boolean) -> Unit {
                                override fun invoke(onSuccessStartDay: Boolean) {
                                    if (onSuccessStartDay) {
                                        if (SharedPref.getInstance()
                                                .getBoolean(AppConstant.START_DAY, false)
                                        ) {
                                            checkGeoFencingAndStartCheckIn(customeData)
                                        }
                                    }
                                }
                            })
                        }
                    }

                    LIST_OF_CUSTOMER_ACTIONS.RELOAD -> {
                        isDataChange = true
                        currentPage = 1
                        customerList.clear()
                        loadCustomerList()
                    }
                }
            }

            fun checkGeoFencingAndOpenChooser(customeData: CustomerData) {
                if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
                    registerGeofenceUpdates(customeData, object : (Boolean) -> Unit {
                        override fun invoke(isInsideArea: Boolean) {
                            if (isInsideArea) {
                                openActivityChooser(customeData)
                            } else {
                                CheckOutAlert.showCheckOutAlertDialog(
                                    this@ListOfCustomerActivity,
                                    customeData.name ?: ""
                                )
                            }
                        }
                    })
                } else {
                    openActivityChooser(customeData)
                }
            }

            fun checkGeoFencingAndStartCheckIn(customeData: CustomerData) {
                val checkInUserdata =
                    customerList.firstOrNull { it.checkInTime.isNullOrEmpty().not() }
                val isAnyUserCheckIn = (checkInUserdata != null)
                if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
                    getUserCurrentLocation()
                    registerGeofenceUpdates(customeData, object : (Boolean) -> Unit {
                        override fun invoke(isInsideArea: Boolean) {
                            if (isInsideArea) {
                                if (isAnyUserCheckIn.not() || (customeData.checkInTime.isNullOrEmpty()
                                        .not())
                                ) {
                                    val fragment = CheckInDialogFragment.getInstance(
                                        customeData,
                                        object : ICheckInClickListener {
                                            override fun onConfirm(model: CheckInRequest) {
                                                customerID = model.customer_id
                                                model.geo_location_lat = currentGeoLocationLat
                                                model.geo_location_long = currentGeoLocationLong
                                                customerViewModel.getCheckInData(
                                                    model,
                                                    hasInternetConnection()
                                                )
                                            }
                                        })
                                    fragment.show(
                                        supportFragmentManager,
                                        CheckInDialogFragment::class.java.name
                                    )
                                } else {
                                    checkInUserdata?.let { checkInUserdata ->
                                        val fragment = CheckedInDialogFragment.getInstance(
                                            checkInUserdata.id ?: 0, buildString {
                                                append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                                                append(" ")
                                                append(checkInUserdata.name)
                                            }, object : ICheckInClickListener {
                                                override fun onConfirm(ckRequest: CheckInRequest) {
                                                    isDataChange = true
                                                    currentPage = 1
                                                    customerList.clear()
                                                    loadCustomerList()
                                                }

                                            })
                                        fragment.show(
                                            supportFragmentManager,
                                            DeleteDialogFragment::class.java.name
                                        )
                                    }
                                }
                            } else {
                                CheckOutAlert.showCheckOutAlertDialog(
                                    this@ListOfCustomerActivity,
                                    customeData.name ?: ""
                                )
                            }
                        }
                    })

                } else {
                    if (isAnyUserCheckIn.not() || (customeData.checkInTime.isNullOrEmpty().not())) {
                        val fragment = CheckInDialogFragment.getInstance(
                            customeData,
                            object : ICheckInClickListener {
                                override fun onConfirm(model: CheckInRequest) {
                                    customerID = model.customer_id
                                    model.geo_location_lat = currentGeoLocationLat
                                    model.geo_location_long = currentGeoLocationLong
                                    customerViewModel.getCheckInData(model, hasInternetConnection())
                                }
                            })
                        fragment.show(
                            supportFragmentManager,
                            CheckInDialogFragment::class.java.name
                        )
                    } else {
                        checkInUserdata?.let { checkInUserdata ->
                            val fragment = CheckedInDialogFragment.getInstance(
                                checkInUserdata.id ?: 0,
                                buildString {
                                    append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                                    append(" ")
                                    append(checkInUserdata.name)
                                },
                                object : ICheckInClickListener {
                                    override fun onConfirm(ckRequest: CheckInRequest) {
                                        isDataChange = true
                                        currentPage = 1
                                        customerList.clear()
                                        loadCustomerList()
                                    }

                                })
                            fragment.show(
                                supportFragmentManager,
                                DeleteDialogFragment::class.java.name
                            )
                        }
                    }
                }
            }

        })
    }

    private fun showStartDayDialog(onStartDaySuccess: (Boolean) -> Unit) {
        if (supportFragmentManager.fragments.firstOrNull {
                it.tag?.equals(
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                ) == true
            } == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
                object : IStartDayActionListener {
                    override fun onDismissDialogForStartDay() {
                        super.onDismissDialogForStartDay()
                        onStartDaySuccess.invoke(false)
                    }

                    override fun onSuccessfullyMarkAttendance() {
                        super.onSuccessfullyMarkAttendance()

                        onStartDaySuccess.invoke(true)
                    }
                },
                currentGeoLocationLat,
                currentGeoLocationLong
            )
            fragment?.show(
                supportFragmentManager,
                MarkAttendanceBottomSheetDialogFragment::class.java.name
            )
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(
                    supportFragmentManager,
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                )
            }
        }
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.offlineCustomerListWithErrorLiveData.observe(this) {
            if (it.first and it.second.data.isNullOrEmpty().not()) {
                customerList.addAll(it.second.data!!)
                customerAdapter.notifyDataSetChanged()
                loadCustomerList()
            } else {
                loadCustomerList()
            }
        }
        checkOutViewModel.getCheckOut().observe(this) { data ->
            if (data.error == false) {
                showToast(data.message)
                isDataChange = true
                currentPage = 1
                customerList.clear()
                loadCustomerList()
            } else {
                if (data.errorCode != 0) {
                    showToast(data.message)

                }
            }

        }
        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    binding.rvCustomerList.visibility = View.VISIBLE
                    data.data?.let { it ->
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

        customerViewModel.getCheckIn().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                showToast(data.message)
                if (customerID != null) {
                    getCustomerInfoResultLauncher.launch(
                        Intent(
                            this,
                            CustomerDetailActivity::class.java
                        ).putExtra(CUSTOMER_ID, customerID)
                            .putExtra(AppConstant.CUSTOMER_TYPE, customerLevel)
                            .putExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS, true)
                            .putExtra("isDataChange", true)
                    )
                }

            } else {

                val fragment = CheckedInDialogFragment.getInstance(customerID!!, data.message!!)
                fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
            }


        }
        customerViewModel.customerDeleteLiveData.observe(this) {
            binding.mainProgressBar.visibility = View.GONE
            if (it.error == false) {
                isDataChange = true
                if (it.data != null) {
                    it.data.let { data ->
//                        if (data.isUsed == true && data.childCount != null && data.childCount!! > 0) {
//                            showDeleteDialog(AppConstant.TRANSFER_CUSTOMER, data.childCount)
//                        } else {
//                            if (isDeleteCustomerDialogShow.not()) {
//                                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
//                            } else {
                        isDeleteCustomerDialogShow = false
                        if (customerInActivePosition != -1) {
                            customerList.removeAt(customerInActivePosition)
                            customerAdapter.notifyItemRemoved(customerInActivePosition)
                            customerAdapter.notifyItemRangeChanged(
                                customerInActivePosition,
                                customerList.size
                            )
                            customerInActivePosition = -1
                        }
                    }
//                        }
//                    }
                }
            } else {
                showToast(it.message)
            }
        }


        customerViewModel.distributorListLiveData.observe(this) { response ->
            binding.progressBar.hideView()
            isPageLoading = false
            response.data?.let { customerDataList ->
                if (customerDataList.isNotEmpty()) {
                    if (customerDataList.size == 1) {
                        selectedCustomerData?.let { customerData ->
                            startActivity(
                                Intent(
                                    this@ListOfCustomerActivity,
                                    CreateNewOrderForCustomerActivity::class.java
                                ).putExtra(AppConstant.CUSTOMER, customerData)
                                    .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                    .putExtra(CUSTOMER_ID, customerData.id)
                                    .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)
                                    .putExtra(
                                        AppConstant.SELECTED_DISTRIBUTOR,
                                        customerDataList.get(0)
                                    ).putExtra(
                                        IS_TELEPHONIC_ORDER, true
                                    )
                            )
                        }
                    } else {
                        startActivity(
                            Intent(
                                this@ListOfCustomerActivity,
                                CustomerDetailActivity::class.java
                            )
                                .putExtra(CUSTOMER_ID, selectedCustomerData?.id)
                                .putExtra(
                                    AppConstant.CUSTOMER_TYPE,
                                    selectedCustomerData?.customerLevel
                                )
                                .putExtra(AppConstant.DISTRIBUTOR_SELECTOR, true)
                                .putExtra(IS_TELEPHONIC_ORDER, true)
                                .putExtra(
                                    AppConstant.PAYMENT_INFO,
                                    selectedCustomerData?.paymentTerm
                                )
                        )
                    }
                }
            }
        }


    }

    override fun onCall(model: CustomerData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: CustomerData, position: Int) {
        val uri =
            Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile}&text=Hi, ${model.name}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onNewOrder(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
            SharedPref.getInstance().clearCart()
            startActivity(
                Intent(this, CreateNewOrderForCustomerActivity::class.java).putExtra(
                    AppConstant.CUSTOMER_NAME,
                    model.name
                ).putExtra(CUSTOMER_ID, model.id).putExtra(AppConstant.CUSTOMER, model)
                    .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
            )
        } else {
            showToast(resources.getString(R.string.create_order_permission))
        }
    }

    override fun onRecordPayment(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
            getRecordPaymentResultLauncher.launch(
                Intent(this, AddRecordPaymentActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER, model)
                    .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                    .putExtra(CUSTOMER_ID, model.id)
            )
        } else {
            showToast(resources.getString(R.string.payment_permission))
        }
    }

    private var getRecordPaymentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                isDataChange = true
            }
        }

    override fun onEdit(model: CustomerData, position: Int) {
        val intent = Intent(this, NewAddCustomerActivity::class.java)
        intent.putExtra(CUSTOMER_ID, model.id)
        intent.putExtra(AppConstant.CUSTOMER_TYPE, model.customerLevel)

        if (model.errorMessage != null) {
            intent.putExtra(AppConstant.ANDROID_OFFLINE_TAG, true)
        }

        someActivityResultLauncher.launch(intent)
    }

    override fun onInActiveCustomer(model: CustomerData, position: Int) {
        customerInActivePosition = position
        customerInActiveModel = model
        showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
//        val customer = CustomerDeleteOptionModel()
//        customer.checkChildren = true
//        binding.mainProgressBar.visibility = View.VISIBLE
//        customerViewModel.inactiveCustomer(model.id!!, customer, hasInternetConnection())
    }

    private fun showDeleteDialog(action: String, count: Int?) {
        val fragment = DeleteDialogFragment.getInstance(action, object : IDeleteDialogListener {
            override fun onDeleteButtonClick() {
                super.onDeleteButtonClick()
                if (action == AppConstant.TRANSFER_CUSTOMER) {
                    transferCustomerActivityResultLauncher.launch(
                        Intent(
                            this@ListOfCustomerActivity,
                            TransferCustomerActivity::class.java
                        )
                            .putExtra(
                                AppConstant.CUSTOMER,
                                customerInActiveModel
                            )
                    )
                } else {
                    isDeleteCustomerDialogShow = true
                    val customer = CustomerDeleteOptionModel()
                    customer.checkChildren = false
                    customer.isCustomerDelete = true
                    binding.mainProgressBar.visibility = View.VISIBLE
                    customerViewModel.inactiveCustomer(
                        customerInActiveModel?.id!!,
                        customer,
                        hasInternetConnection()
                    )
                }
            }
        })

        if (action == AppConstant.TRANSFER_CUSTOMER) {
            val bundle = Bundle()
            bundle.putInt(AppConstant.ADD_COUNT, count ?: 0)
            bundle.putString(AppConstant.CUSTOMER_LEVEL, customerInActiveModel?.customerLevel)
            bundle.putString(AppConstant.CUSTOMER_NAME, customerInActiveModel?.name)
            fragment.arguments = bundle
        }

        fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
    }


    override fun onGetCustomerInfo(model: CustomerData, isUserCheckIn: Boolean) {
        getCustomerInfoResultLauncher.launch(
            Intent(
                this,
                CustomerDetailActivity::class.java
            ).putExtra(CUSTOMER_ID, model.id)
                .putExtra(AppConstant.CUSTOMER_TYPE, customerLevel)
                .putExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS, isUserCheckIn)
        )

    }

    override fun onGetCheckInfo(model: CheckInRequest) {
        customerID = model.customer_id
        customerViewModel.getCheckInData(model, hasInternetConnection())
    }

    override fun recordCustomerActivity(model: CustomerData) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            someActivityResultLauncher.launch(
                Intent(
                    this,
                    CustomFormActivity::class.java
                )
                    .putExtra(CUSTOMER_ID, model.id)
                    .putExtra(AppConstant.CUSTOMER, model)
                    .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            )
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    override fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                Intent(
                    this,
                    OrgPhotosViewActivity::class.java
                ).putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                    .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun getCustomerParentDetails(model: CustomerData, position: Int) {
        if (model.customerParent != null) {
            getCustomerInfoResultLauncher.launch(
                Intent(
                    this,
                    CustomerDetailActivity::class.java
                ).putExtra(CUSTOMER_ID, model.customerParent)
            )
        }
    }

    override fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }

    }

    private var getCustomerInfoResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                isDataChange = true
                currentPage = 1
                customerList.clear()
                customerAdapter.notifyDataSetChanged()
                loadCustomerList()
            }
        }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun applyFilter(
        customerLevel: String,
        customerType: ArrayList<CustomerTypeDataItem>,
        assignedStaff: Pair<Int, String>
    ) {
        filterCustomerType = customerType
        filterCustomerLevel = customerLevel
        filterAssignedStaff = assignedStaff

        if (filterAssignedStaff.first != 0 && assignedStaffFilterApply.not()) {
            ++filterCount
            assignedStaffFilterApply = true
        }

        if (filterCustomerLevel.isEmpty().not() && levelFilterApply.not()) {
            ++filterCount
            levelFilterApply = true
        }

        if (filterCustomerType.isEmpty().not() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }

        if (filterAssignedStaff.first == 0 && assignedStaffFilterApply) {
            --filterCount
            assignedStaffFilterApply = false
        }

        if (filterCustomerLevel.isEmpty() && levelFilterApply) {
            --filterCount
            levelFilterApply = false
        }

        if (filterCustomerType.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        loadCustomerList()
    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        loadCustomerList()
    }


    /* override fun onConfirm(customerName: String, customerID: Int) {
         val checkoutRequest = CheckoutRequest(customerID)
         checkOutViewModel.getCheckOutData(checkoutRequest,
             hasInternetConnection()
         )

     }*/

    /**
     *
     * Location Gathering Part
     *
     */


    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(this)) {
                setUpdatedLocationListener()
            } else {
                locationPermissionUtils.showEnableGpsDialog()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getUserCurrentLocation()
            } else {
                if (isStaffUser && PermissionModel.INSTANCE.getPermission(
                        AppConstant.LOCATION_TRACKING,
                        false
                    )
                ) {
                    getUserCurrentLocation()
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationPermissionUtils.setActivityResult(resultCode, requestCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false).setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(100).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    if (Utils.isMockLocation(it).not()) {
                        currentGeoLocationLat = it.latitude
                        currentGeoLocationLong = it.longitude
                        GeoLocationUtils.getAddress(
                            this@ListOfCustomerActivity,
                            longitude = currentGeoLocationLong,
                            latitude = currentGeoLocationLat
                        ) { address ->
                            currentGeoAddress = address
                        }
                    } else {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                        if (isFinishing.not() && supportFragmentManager.isStateSaved.not()) {
                            val fragment =
                                MockLocationDetectedDialogFragment.getInstance(this@ListOfCustomerActivity)
                            fragment.isCancelable = false
                            fragment.show(
                                supportFragmentManager,
                                MockLocationDetectedDialogFragment::class.java.name
                            )
                        }
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }


    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
    }


    private fun registerGeofenceUpdates(
        customerModel: CustomerData,
        onInsideFence: (Boolean) -> Unit
    ) {
        val isUserInsideGeoFencing =
            if (customerModel.mapLocationLat != 0.0 && customerModel.mapLocationLong != 0.0) {
                findUserIsInGeoFencingArea(
                    customerModel.mapLocationLat ?: 0.0,
                    customerModel.mapLocationLong ?: 0.0,
                    currentGeoLocationLat,
                    currentGeoLocationLong
                ).first
            } else {
                true
            }

        if (isUserInsideGeoFencing) {
            onInsideFence.invoke(true)
        } else {
            onInsideFence.invoke(false)
        }
    }

    override fun createTelephonicOrder(
        model: CustomerData,
        isMultipleDistributorSelector: Boolean
    ) {
        super.createTelephonicOrder(model, isMultipleDistributorSelector)
        selectedCustomerData = model
        if (isMultipleDistributorSelector) {
            startActivity(
                Intent(
                    this@ListOfCustomerActivity,
                    CustomerDetailActivity::class.java
                )
                    .putExtra(CUSTOMER_ID, model.id)
                    .putExtra(
                        AppConstant.CUSTOMER_TYPE,
                        model.customerLevel
                    )
                    .putExtra(AppConstant.DISTRIBUTOR_SELECTOR, true)
                    .putExtra(IS_TELEPHONIC_ORDER, true)
                    .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
            )
        } else {
            val selectedStep = model.customerLevel?.let {
                when (it) {
                    AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                    AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                    AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                    else -> null
                }
            }
            selectedStep?.let { loadCustomerLevel(model, it) }
        }
    }


    private fun loadCustomerLevel(customerData: CustomerData, customerLevel: CustomerLevel) {
        if (customerLevel != CustomerLevel.LEVEL_ONE) {
            customerViewModel.getDistributorListMapped(
                customerId = customerData.id,
                name = "",
                filterCustomerLevel = when (customerLevel) {
                    CustomerLevel.LEVEL_ONE -> AppConstant.CUSTOMER_LEVEL_1
                    CustomerLevel.LEVEL_TWO -> AppConstant.CUSTOMER_LEVEL_2
                    CustomerLevel.LEVEL_THREE -> AppConstant.CUSTOMER_LEVEL_3
                },
                filterCustomerType = ArrayList(),
                sortByOrder = AppConstant.SORTING_LEVEL_ASCENDING,
                header = defaultHeader,
                hasInternetConnection = hasInternetConnection()
            )
            binding.progressBar.showView()
        }
    }

}