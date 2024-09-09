package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.ActivityCustomerDetailBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.dialog.DeleteDialogFragment.IDeleteDialogListener
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.checkIn.CheckInDialogFragment
import com.app.rupyz.dialog.checkIn.CheckOutAlert
import com.app.rupyz.dialog.checkIn.CheckOutConfiramation
import com.app.rupyz.dialog.checkIn.CheckOutDialog
import com.app.rupyz.dialog.checkIn.CheckedInDialogFragment
import com.app.rupyz.dialog.checkIn.ICheckInClickListener
import com.app.rupyz.dialog.checkIn.ICheckOutConClickListener
import com.app.rupyz.dialog.checkIn.ICheckOutConfirmationClickListener
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.disableAndAlpha
import com.app.rupyz.generic.helper.disableOnlyAlpha
import com.app.rupyz.generic.helper.divideHeadersIntoQueryParams
import com.app.rupyz.generic.helper.enable
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.AppConstant.IS_TELEPHONIC_ORDER
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CheckoutRequest
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.checkIn.CheckInStatus
import com.app.rupyz.model_kt.checkIn.CheckInType
import com.app.rupyz.model_kt.checkIn.CheckInType.CHECK_IN
import com.app.rupyz.model_kt.checkIn.CheckInType.CHECK_OUT
import com.app.rupyz.model_kt.checkIn.CheckInType.TELEPHONIC
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.sales.customer.adapters.DistributorRadioListAdapter
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.orders.IDataChangeListener
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout


class CustomerDetailActivity : BaseActivity(), IDataChangeListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    ICheckOutConfirmationClickListener, ICheckOutConClickListener {

    private lateinit var binding: ActivityCustomerDetailBinding

    private val customerViewModel: CustomerViewModel by viewModels()

    private var customerData: CustomerData? = null
    private var isDataChange: Boolean = false
    private var customerId: Int = -1
    private var isUserCheckedIn = false
    private var isOrderStatus = false
    private var orderMessage: String? = null
    private var isTelephonicOrder = false

    // private val checkOutViewModel: CheckOutViewModel by viewModels()
    private lateinit var customerDetailFragmentPagerAdapter: CustomerDetailFragmentPagerAdapter
    private var distributorListAdapter: DistributorRadioListAdapter = DistributorRadioListAdapter()
    private var chooseActivityBottomSheet: ChooseActivityBottomSheet = ChooseActivityBottomSheet()
    private val linearLayoutManager: LinearLayoutManager =
        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    private var isApiLastPage: Boolean = false
    private var isPageLoading: Boolean = false
    private var selectedStep: CustomerLevel? = null
    private var assignCustomerHeaders: String? = "selected=true&page_no=1"
    private var checkInData: CheckInStatus.Data? = null
    private val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
    private val isTelephonicEnabled =
        SharedPref.getInstance().getBoolean(AppConstant.TELEPHONIC_ORDER, false)
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null
    private var isDeleteCustomerDialogShow = false

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

    private lateinit var transferCustomerActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        transferCustomerActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    //DeleteDialog.showDeleteDialog(this,"",0,AppConstant.Delete.DELETE_CUSTOMER,this)
                    showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                }
            }

        editActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    isDataChange = true
                    binding.detailsProgressBar.visibility = View.VISIBLE
                    customerViewModel.getCustomerById(customerId, hasInternetConnection())
                }
            }

        someActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    isDataChange = true
                    binding.detailsProgressBar.visibility = View.VISIBLE
                    customerViewModel.getCustomerById(customerId, hasInternetConnection())
                }
            }




        binding.mainContent.visibility = View.GONE
        initObservers()

        if (checkSetting) {
            binding.btnChoseActivity.disableOnlyAlpha()
            binding.btnTelephonicOrder.disableOnlyAlpha()
            binding.btnCheckIn.disableOnlyAlpha()
            if (isTelephonicEnabled) {
                binding.btnTelephonicOrder.showView()
            } else {
                binding.btnTelephonicOrder.hideView()
            }
            customerViewModel.getUserCheckInStatus(hasInternetConnection())
        }


        /**
         *
         * Location Gathering Part
         *
         */
        locationPermissionUtils = LocationPermissionUtils(this, this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        /********************************************************/


        binding.detailsProgressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
            customerViewModel.getCustomerById(customerId, hasInternetConnection())
        }
        isOrderStatus = intent.getBooleanExtra(AppConstant.ORDER_STATUS, false)
        orderMessage = intent.getStringExtra(AppConstant.ORDER_MESSAGE)

        if (SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)) {
            if (intent.hasExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS)) {
                isUserCheckedIn =
                    intent.getBooleanExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS, false)
            }
            checkInStatusChanged()
        } else {
            isUserCheckedIn = true
            checkInStatusChanged()
        }
        if (isOrderStatus == true) {
            val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
            CheckOutConfiramation.showCheckOutConfirmationDialog(
                this, "customerData?.name!!", orderMessage!!, customerId, this, checkSetting
            )
        }


        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        chooseActivityBottomSheet.setModuleType(AppConstant.CUSTOMER_FEEDBACK)
        setListener()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (intent.hasExtra(AppConstant.CLEAR_TOP_DONE) && intent.getBooleanExtra(
                        AppConstant.CLEAR_TOP_DONE, false
                    )
                ) {
                    startActivity(
                        Intent(
                            this@CustomerDetailActivity, SalesMainActivity::class.java
                        )
                    )
                    finish()
                } else {
                    if (((selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) && binding.selectDistributorLayout.visibility == View.VISIBLE)) {
                        changeDistributorToCustomer(false)
                    } else {
                        val sendIntent = Intent()
                        if (isDataChange) {
                            sendIntent.putExtra("isDataChange", true)
                            setResult(RESULT_OK, sendIntent)
                        }
                        if (intent.hasExtra("isDataChange") && intent.getBooleanExtra(
                                "isDataChange", false
                            )
                        ) {
                            sendIntent.putExtra("isDataChange", true)
                            setResult(RESULT_OK, sendIntent)
                        }
                        finish()
                    }
                }

            }
        })

        binding.rvDistributorList.layoutManager = linearLayoutManager
        binding.rvDistributorList.adapter = distributorListAdapter

    }


    private fun changeDistributorToCustomer(isDistributorSelector: Boolean) {
        if (isDistributorSelector) {
            if (SharedPref.getInstance().getBoolean(CUSTOMER_LEVEL_ORDER, false)) {
                if (distributorListAdapter.itemCount == 1 && distributorListAdapter.getSelectedDistributor() != null) {
                    binding.btnProceedToCreateOrder.performClick()
                } else {
                    binding.selectDistributorLayout.showView()
                    binding.tabLayout.hideView()
                    binding.viewPager.hideView()
                    binding.clBottomButtonLayout.hideView()
                    binding.clDistributorBottomButtonLayout.showView()
                }
            } else {
                customerData?.let { customerData ->
                    startActivity(
                        Intent(
                            this@CustomerDetailActivity,
                            CreateNewOrderForCustomerActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER, customerData)
                            .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                            .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                            .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm).putExtra(
                                IS_TELEPHONIC_ORDER,
                                intent.getBooleanExtra(IS_TELEPHONIC_ORDER, false)
                            )
                    )
                }
            }
        } else {
            binding.selectDistributorLayout.hideView()
            binding.tabLayout.showView()
            binding.viewPager.showView()
            binding.clBottomButtonLayout.showView()
            binding.clDistributorBottomButtonLayout.hideView()
        }
    }


    private fun setListener() {


        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.clCustomerDetails.root.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this, CustomerProfileActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_ID, customerId)
            )
        }
        binding.btnTelephonicOrder.setOnClickListener {
            isTelephonicOrder = true
            if (binding.btnTelephonicOrder.alpha == 1f) {
                createCheckInRequest(TELEPHONIC)
            } else {
                checkInData?.let { checkInUserdata ->
                    val fragment = CheckedInDialogFragment.getInstance(
                        checkInUserdata.customerId ?: 0,
                        buildString {
                            append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                            append(" ")
                            append(checkInUserdata.customerName)
                        })
                    fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
                }
            }
        }

        binding.btnCheckIn.setOnClickListener {
            isTelephonicOrder = false
            if (binding.btnCheckIn.alpha == 1f) {
                createCheckInRequest(CHECK_IN)
            } else {
                checkInData?.let { checkInUserdata ->
                    val fragment = CheckedInDialogFragment.getInstance(
                        checkInUserdata.customerId ?: 0,
                        buildString {
                            append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                            append(" ")
                            append(checkInUserdata?.customerName)
                        })
                    fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
                }
            }
        }
        binding.btnChoseActivity.setOnClickListener {
            isTelephonicOrder = false
            if (binding.btnChoseActivity.alpha == 1f) {

                if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                    customerData?.let { customeData -> checkGeoFencingAndOpenChooser(customeData) }
                } else {
                    showStartDayDialog(object : (Boolean) -> Unit {
                        override fun invoke(onSuccessStartDay: Boolean) {
                            if (onSuccessStartDay) {
                                if (SharedPref.getInstance()
                                        .getBoolean(AppConstant.START_DAY, false)
                                ) {
                                    customerData?.let { customeData ->
                                        checkGeoFencingAndOpenChooser(
                                            customeData
                                        )
                                    }
                                }
                            }
                        }
                    })
                }
            } else {

                checkInData?.let { checkInUserdata ->
                    val fragment = CheckedInDialogFragment.getInstance(
                        checkInUserdata.customerId ?: 0,
                        buildString {
                            append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                            append(" ")
                            append(checkInUserdata?.customerName)
                        })
                    fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
                }                /*showToast(buildString {
					append(resources.getString(R.string.you_are_check_in_at))
					append(" ")
					append(checkInData?.customerName)
				})*/

            }


        }
        binding.btnCancelDistributorSelection.setOnClickListener {
            isTelephonicOrder = false
            if (intent.hasExtra(AppConstant.DISTRIBUTOR_SELECTOR) && intent.getBooleanExtra(
                    AppConstant.DISTRIBUTOR_SELECTOR, false
                )
            ) {
                finish()
            } else {
                changeDistributorToCustomer(false)
            }
        }
        binding.btnProceedToCreateOrder.setOnClickListener {
            val selectedDistributor = distributorListAdapter.getSelectedDistributor()
            if (selectedDistributor == null) {
                if (distributorListAdapter.itemCount == 0) {
                    showToast(resources.getString(R.string.please_add_distributor_for_this_client))
                } else {
                    showToast(resources.getString(R.string.select_distributor_first))
                }
            } else {
                customerData?.let { customerData ->
                    startActivity(
                        Intent(
                            this@CustomerDetailActivity,
                            CreateNewOrderForCustomerActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER, customerData)
                            .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                            .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                            .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)
                            .putExtra(AppConstant.SELECTED_DISTRIBUTOR, selectedDistributor)
                            .putExtra(
                                IS_TELEPHONIC_ORDER,
                                intent.getBooleanExtra(
                                    IS_TELEPHONIC_ORDER,
                                    false
                                ) || isTelephonicOrder
                            )

                    )
                }
            }
        }
        chooseActivityBottomSheet.setOnCreateOrderListener {
            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.CREATE_ORDER_PERMISSION, false
                )
            ) {
                if ((selectedStep == CustomerLevel.LEVEL_TWO
                            || selectedStep == CustomerLevel.LEVEL_THREE) && SharedPref.getInstance()
                        .getBoolean(CUSTOMER_LEVEL_ORDER, false)
                ) {
                    changeDistributorToCustomer(true)
                } else {
                    customerData?.let { customerData ->
                        startActivity(
                            Intent(
                                this@CustomerDetailActivity,
                                CreateNewOrderForCustomerActivity::class.java
                            ).putExtra(AppConstant.CUSTOMER, customerData)
                                .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                                .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)
                                .putExtra(
                                    IS_TELEPHONIC_ORDER,
                                    intent.getBooleanExtra(
                                        IS_TELEPHONIC_ORDER,
                                        false
                                    ) || isTelephonicOrder
                                )
                        )
                    }
                }
            } else {
                showToast(resources.getString(R.string.create_order_permission))
            }

        }
        binding.etSearchField.addDelayedTextChangeListener(500) {
            if (customerId != -1) {
                assignCustomerHeaders = defaultHeader
                selectedStep?.let { it1 -> loadCustomerLevel(it1, assignCustomerHeaders) }
            }
        }

        binding.ivMore.setOnClickListener { v ->
            //creating a popup menu
            val popup = PopupMenu(v.context, binding.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.customer_action_menu)

            if (PermissionModel.INSTANCE.getPermission(AppConstant.EDIT_CUSTOMER_PERMISSION, false)
                    .not()
            ) {
                popup.menu.getItem(0).isVisible = false
            }

            if (PermissionModel.INSTANCE.getPermission(
                    AppConstant.DELETE_CUSTOMER_PERMISSION, false
                ).not()
            ) {
                popup.menu.getItem(1).isVisible = false
            }

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.edit_product -> {
                        val intent = Intent(this, NewAddCustomerActivity::class.java)
                        intent.putExtra(AppConstant.CUSTOMER_ID, customerData?.id)
                        intent.putExtra(AppConstant.CUSTOMER_TYPE, customerData?.customerLevel)
                        if (customerData?.errorMessage != null) {
                            intent.putExtra(AppConstant.ANDROID_OFFLINE_TAG, true)
                        }

                        editActivityResultLauncher.launch(intent)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_inactive_customer -> {
                        val customer = CustomerDeleteOptionModel()
                        customer.checkChildren = true
                        binding.detailsProgressBar.visibility = View.VISIBLE
                        customerViewModel.inactiveCustomer(
                            customerData?.id!!, customer, hasInternetConnection()
                        )
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

    }

    fun checkGeoFencingAndOpenChooser(customeData: CustomerData) {
        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
            registerGeofenceUpdates(customeData, object : (Boolean) -> Unit {
                override fun invoke(isInsideArea: Boolean) {
                    if (isInsideArea) {
                        if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
                            chooseActivityBottomSheet.show(
                                supportFragmentManager, ChooseActivityBottomSheet::class.java.name
                            )
                        }else{
                            supportFragmentManager.fragments.remove(chooseActivityBottomSheet)
                            if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
                                chooseActivityBottomSheet.show(
                                    supportFragmentManager, ChooseActivityBottomSheet::class.java.name
                                )
                            }
                        }
                    } else {
                        CheckOutAlert.showCheckOutAlertDialog(
                            this@CustomerDetailActivity, customeData.name ?: ""
                        )
                    }
                }
            })
        } else {
            if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
                chooseActivityBottomSheet.show(
                    supportFragmentManager, ChooseActivityBottomSheet::class.java.name
                )
            }else{
                supportFragmentManager.fragments.remove(chooseActivityBottomSheet)
                if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
                    chooseActivityBottomSheet.show(
                        supportFragmentManager, ChooseActivityBottomSheet::class.java.name
                    )
                }
            }
        }
    }

    private fun createCheckInRequest(telephonic: CheckInType) {
        when (telephonic) {
            CHECK_IN -> {
                if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                    customerData?.let { checkGeoFencingAndStartCheckIn(it) }
                } else {
                    showStartDayDialog(object : (Boolean) -> Unit {
                        override fun invoke(onSuccessStartDay: Boolean) {
                            if (onSuccessStartDay) {
                                if (SharedPref.getInstance()
                                        .getBoolean(AppConstant.START_DAY, false)
                                ) {
                                    customerData?.let { checkGeoFencingAndStartCheckIn(it) }
                                }
                            }
                        }
                    })
                }
            }

            CHECK_OUT -> {
                // TODO Nothing
            }

            TELEPHONIC -> {
                if (SharedPref.getInstance().getBoolean(CUSTOMER_LEVEL_ORDER, false)) {
                    if (selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) {
                        changeDistributorToCustomer(true)
                        intent.putExtra(IS_TELEPHONIC_ORDER, true)
                    } else {
                        startActivity(
                            Intent(
                                binding.root.context, CreateNewOrderForCustomerActivity::class.java
                            ).putExtra(AppConstant.CUSTOMER, customerData)
                                .putExtra(AppConstant.CUSTOMER_NAME, customerData?.name)
                                .putExtra(AppConstant.CUSTOMER_ID, customerData?.id)
                                .putExtra(AppConstant.PAYMENT_INFO, customerData?.paymentTerm)
                                .putExtra(IS_TELEPHONIC_ORDER, true)
                        )
                    }
                } else {
                    startActivity(
                        Intent(
                            binding.root.context, CreateNewOrderForCustomerActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER, customerData)
                            .putExtra(AppConstant.CUSTOMER_NAME, customerData?.name)
                            .putExtra(AppConstant.CUSTOMER_ID, customerData?.id)
                            .putExtra(AppConstant.PAYMENT_INFO, customerData?.paymentTerm)
                            .putExtra(IS_TELEPHONIC_ORDER, true)
                    )
                }

            }
        }
    }

    fun checkGeoFencingAndStartCheckIn(customeData: CustomerData) {
        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
            registerGeofenceUpdates(customeData, object : (Boolean) -> Unit {
                override fun invoke(isInsideArea: Boolean) {
                    if (isInsideArea) {
                        if ((customeData.checkInTime.isNullOrEmpty())) {
                            customerData?.let { customerData ->
                                val fragment =
                                    CheckInDialogFragment.getInstance(customerData, object :
                                        ICheckInClickListener {
                                        override fun onConfirm(model: CheckInRequest) {
                                            model.geo_location_lat = currentGeoLocationLat
                                            model.geo_location_long = currentGeoLocationLong
                                            binding.detailsProgressBar.visibility = View.VISIBLE
                                            customerViewModel.getCheckInData(
                                                model, hasInternetConnection()
                                            )
                                        }

                                    })
                                fragment.show(
                                    supportFragmentManager, DeleteDialogFragment::class.java.name
                                )
                            }
                        }
                    } else {
                        CheckOutAlert.showCheckOutAlertDialog(
                            this@CustomerDetailActivity, customeData.name ?: ""
                        )
                    }
                }
            })

        } else {
            if ((customeData.checkInTime.isNullOrEmpty())) {
                customerData?.let { customerData ->
                    val fragment = CheckInDialogFragment.getInstance(customerData, object :
                        ICheckInClickListener {
                        override fun onConfirm(model: CheckInRequest) {
                            model.geo_location_lat = currentGeoLocationLat
                            model.geo_location_long = currentGeoLocationLong
                            binding.detailsProgressBar.visibility = View.VISIBLE
                            customerViewModel.getCheckInData(model, hasInternetConnection())
                        }

                    })
                    fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
                }
            }
        }
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
                }, currentGeoLocationLat, currentGeoLocationLong
            )
            fragment?.show(
                supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name
            )
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(
                    supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name
                )
            }
        }
    }


    private fun checkInStatusChanged() {
        if (isUserCheckedIn) {
            binding.btnChoseActivity.showView()
            binding.gpCheckInButtonGroup.hideView()
        } else {
            binding.btnChoseActivity.hideView()
            if (isTelephonicEnabled) {
                binding.gpCheckInButtonGroup.showView()
                binding.btnTelephonicOrder.showView()
            } else {
                binding.btnTelephonicOrder.visibility = View.GONE
                binding.btnCheckIn.showView()
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {

        customerViewModel.checkInStatusLiveData.observe(this) { data ->
            if (data.error == false) {
                checkInData = data.data
                if (binding.clLoadingLocation.isVisible.not()) {
                    checkInDataValidation()
                }
            }
        }

        customerViewModel.getCustomerByIdData().observe(this) { data ->
            binding.detailsProgressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { model ->
                    customerData = model


                    if (binding.clLoadingLocation.isVisible.not()) {
                        checkInDataValidation()
                    }
                    customerData?.let { customerData ->
                        chooseActivityBottomSheet.setCustomerData(
                            customerData
                        )
                    }
                    selectedStep = customerData?.customerLevel?.let {
                        when (it) {
                            AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                            AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                            AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                            else -> null
                        }
                    }
                    if (selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) {
                        selectedStep?.let {
                            loadCustomerLevel(it, assignCustomerHeaders)
                            setPaginationHandler()
                        }
                    }

                    binding.tvChooseActivity.text = when (selectedStep) {
                        CustomerLevel.LEVEL_ONE -> ""
                        CustomerLevel.LEVEL_TWO -> buildString {
                            append(resources.getString(R.string.select))
                            append(" ")
                            append(
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                                    ?: binding.root.context.resources.getString(R.string.primary_customer)
                            )
                        }

                        CustomerLevel.LEVEL_THREE -> buildString {
                            append(resources.getString(R.string.select))
                            append(" ")
                            append(
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                    ?: binding.root.context.resources.getString(R.string.distributor)
                            )
                        }

                        null -> ""
                    }

                    customerData?.let { customerData ->
                        chooseActivityBottomSheet.setCustomerData(
                            customerData
                        )
                    }
                    initCustomerData(customerData!!)

                    if (intent.hasExtra(AppConstant.DISTRIBUTOR_SELECTOR) && intent.getBooleanExtra(
                            AppConstant.DISTRIBUTOR_SELECTOR, false
                        ) && distributorListAdapter.itemCount > 0
                    ) {
                        if (selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) {
                            if (distributorListAdapter.itemCount == 1) {
                                binding.btnProceedToCreateOrder.performClick()
                            } else {
                                changeDistributorToCustomer(true)
                            }
                        } else {
                            changeDistributorToCustomer(false)

                            customerData?.let { customerData ->
                                startActivity(
                                    Intent(
                                        this@CustomerDetailActivity,
                                        CreateNewOrderForCustomerActivity::class.java
                                    ).putExtra(AppConstant.CUSTOMER, customerData)
                                        .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                        .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                                        .putExtra(
                                            AppConstant.PAYMENT_INFO, customerData.paymentTerm
                                        ).putExtra(
                                            IS_TELEPHONIC_ORDER,
                                            intent.getBooleanExtra(
                                                IS_TELEPHONIC_ORDER,
                                                false
                                            ) || isTelephonicOrder
                                        )

                                )
                            }
                        }
                    } else {
                        changeDistributorToCustomer(false)
                    }

                    if (hasInternetConnection() && PermissionModel.INSTANCE.getPermission(
                            AppConstant.EDIT_CUSTOMER_PERMISSION, false
                        ) || hasInternetConnection() && PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELETE_CUSTOMER_PERMISSION, false
                        )
                    ) {
                        binding.ivMore.visibility = View.VISIBLE
                    } else {
                        binding.ivMore.visibility = View.GONE
                    }

                    if (hasInternetConnection().not()) {
                        if (customerData?.isSyncedToServer == false) {
                            binding.ivMore.visibility = View.VISIBLE
                        } else {
                            binding.ivMore.visibility = View.GONE
                        }
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
            binding.detailsProgressBar.visibility = View.GONE
            if (data.error == false) {
                showToast(data.message)
                isUserCheckedIn = true
                isDataChange = true
                intent.putExtra(IS_TELEPHONIC_ORDER, false)
                checkInStatusChanged()
            } else {
                showToast(data.message)                /*CheckedInDialog.showCheckedInDialog(
					this,
					customerName = "",
					data.message!!,
					customerId,
					this
				)*/

            }
        }

        checkOutViewModel.getCheckOut().observe(this) { data ->
            if (data.error == false) {
                showToast(data.message)
                if (isOrderStatus == true) {
                    openCustomerList()
                }

            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }

        customerViewModel.getCustomerListData().observe(this) { response ->
            binding.progressbar.hideView()
            isPageLoading = false
            response.data?.let { customerDataList ->
                if (customerDataList.isNotEmpty()) {
                    selectedStep?.let {
                        setSpinnerItems(
                            defaultHeader == assignCustomerHeaders, customerDataList.toMutableList()
                        )
                    }
                }
            }

            response.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty()
                        .not() && headers.nextParams.divideHeadersIntoQueryParams().first
                ) {
                    assignCustomerHeaders = headers.nextParams
                    if (response.data.isNullOrEmpty()) {
                        selectedStep?.let { selectedStep ->
                            loadCustomerLevel(
                                selectedStep, assignCustomerHeaders
                            )
                        }
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }

        customerViewModel.customerDeleteLiveData.observe(this) {
            binding.detailsProgressBar.visibility = View.GONE
            if (it.error == false) {
                isDataChange = true
                if (it.data != null) {
                    it.data.let { data ->
                        if (data.isUsed == true && data.childCount != null && data.childCount!! > 0) {
                            showDeleteDialog(AppConstant.TRANSFER_CUSTOMER, data.childCount)
                        } else {
                            if (isDeleteCustomerDialogShow.not()) {
                                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                            } else {
                                isDeleteCustomerDialogShow = false
                                openCustomerList()
                            }
                        }
                    }
                }
            } else {
                showToast(it.message)
            }
        }


    }

    private fun showDeleteDialog(action: String, count: Int?) {
        val fragment = DeleteDialogFragment.getInstance(action, object : IDeleteDialogListener {
            override fun onDeleteButtonClick() {
                super.onDeleteButtonClick()
                if (action == AppConstant.TRANSFER_CUSTOMER) {
                    transferCustomerActivityResultLauncher.launch(
                        Intent(
                            this@CustomerDetailActivity, TransferCustomerActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER, customerData)
                    )
                } else {
                    isDeleteCustomerDialogShow = true
                    val customer = CustomerDeleteOptionModel()
                    customer.checkChildren = false
                    customer.isCustomerDelete = true
                    binding.detailsProgressBar.visibility = View.VISIBLE
                    customerViewModel.inactiveCustomer(
                        customerData?.id!!, customer, hasInternetConnection()
                    )
                }
            }
        })

        if (action == AppConstant.TRANSFER_CUSTOMER) {
            val bundle = Bundle()
            bundle.putInt(AppConstant.ADD_COUNT, count ?: 0)
            bundle.putString(AppConstant.CUSTOMER_LEVEL, customerData?.customerLevel)
            bundle.putString(AppConstant.CUSTOMER_NAME, customerData?.name)
            fragment.arguments = bundle
        }

        fragment.show(supportFragmentManager, DeleteDialogFragment::class.java.name)
    }


    private fun setSpinnerItems(clearList: Boolean, customerList: MutableList<CustomerData>) {
        if (clearList) {
            distributorListAdapter.setCustomerList(customerList)
            if (intent.hasExtra(AppConstant.DISTRIBUTOR_SELECTOR) && intent.getBooleanExtra(
                    AppConstant.DISTRIBUTOR_SELECTOR, false
                )
            ) {
                changeDistributorToCustomer(true)
            }
        } else {
            distributorListAdapter.addCustomer(customerList)
        }
    }

    private fun setPaginationHandler() {
        binding.rvDistributorList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                selectedStep?.let { loadCustomerLevel(it, assignCustomerHeaders) }
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadCustomerLevel(customerLevel: CustomerLevel, currentPage: String?) {
        if (customerLevel != CustomerLevel.LEVEL_ONE) {
            this.assignCustomerHeaders = currentPage
            customerViewModel.getCustomerListMapped(
                customerId = customerId,
                name = if (binding.etSearchField.text.isNullOrBlank()) {
                    ""
                } else {
                    binding.etSearchField.text.toString()
                },
                filterCustomerLevel = when (customerLevel) {
                    CustomerLevel.LEVEL_ONE -> AppConstant.CUSTOMER_LEVEL_1
                    CustomerLevel.LEVEL_TWO -> AppConstant.CUSTOMER_LEVEL_2
                    CustomerLevel.LEVEL_THREE -> AppConstant.CUSTOMER_LEVEL_3
                },
                filterCustomerType = ArrayList(),
                ignoreMapping = true,
                sortByOrder = AppConstant.SORTING_LEVEL_ASCENDING,
                header = assignCustomerHeaders ?: defaultHeader,
                hasInternetConnection = hasInternetConnection()
            )
            if (defaultHeader == assignCustomerHeaders) {
                binding.progressbar.showView()
            }
        }
    }

    private fun initCustomerData(model: CustomerData) {
        binding.clCustomerDetails.tvBusinessName.text =
            model.name?.replaceFirstChar(Char::titlecase)


        if (model.checkInTime.isNullOrBlank().not()) {
            isUserCheckedIn = true
            checkInStatusChanged()
        } else {
            val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
            if (checkSetting.not()) {
                isUserCheckedIn = true
                checkInStatusChanged()
            }
        }




        if (model.logoImageUrl.isNullOrEmpty().not()) {
            ImageUtils.loadImage(model.logoImageUrl, binding.clCustomerDetails.ivCustomer)

            binding.clCustomerDetails.ivCustomer.setOnClickListener {
                viewCustomerPhoto(model)
            }
        }

        if (model.customerLevel.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvCustomerLevel.text =
                SharedPref.getInstance().getString(model.customerLevel)
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.VISIBLE

            var customerSubLevelName = ""
            var customerSubLevelCount = 0
            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(
                            getColor(R.color.customer_level_one_background)
                        )
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_one_text_color))
                    customerSubLevelName =
                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                    customerSubLevelCount = model.level_2_customer_count ?: 0
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(getColor(R.color.customer_level_two_background))
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_two_text_color))

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                            "${
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                            } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

                        spannable.setSpan(
                            ForegroundColorSpan(getColor(R.color.theme_purple)), start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        spannable.setSpan(
                            UnderlineSpan(), start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )


                    }

                    customerSubLevelName =
                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                    customerSubLevelCount = model.level_3_customer_count ?: 0
                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(
                            getColor(R.color.customer_level_three_background)
                        )

                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_three_text_color))

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                            "${
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                            } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

                        spannable.setSpan(
                            ForegroundColorSpan(getColor(R.color.theme_purple)), start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        spannable.setSpan(
                            UnderlineSpan(), start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )


                    }
                }
            }

            if (isDataChange.not()) {
                initTabLayout(customerSubLevelName, customerSubLevelCount)
            }
        } else {
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.GONE
        }



        if (!model.contactPersonName.isNullOrEmpty()) {
            binding.clCustomerDetails.tvAuthorizePersonName.text =
                model.contactPersonName?.replaceFirstChar(Char::titlecase)
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.GONE
        }

        if (model.mobile.isNullOrEmpty().not()) {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = true
            binding.clCustomerDetails.ivPhoneCall.alpha = 1f
        } else {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = false
            binding.clCustomerDetails.ivPhoneCall.alpha = 0.3f
        }

        /*val stringBuilder = StringBuilder()
        if (model.city.isNullOrEmpty().not()) {
            stringBuilder.append(model.city?.replaceFirstChar(Char::titlecase))
        }

        if (model.state.isNullOrEmpty().not()) {
            stringBuilder.append(" , ")
            stringBuilder.append(
                    model.state?.replaceFirstChar(
                            Char::titlecase
                                                 )
                                )
        }

        if (model.pincode.isNullOrEmpty().not()) {
            stringBuilder.append(" - ")
            stringBuilder.append(model.pincode)
        }

        if (stringBuilder.isEmpty().not()) {
            binding.clCustomerDetails.tvLocation.text = stringBuilder
            binding.clCustomerDetails.tvLocation.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.tvLocation.visibility = View.GONE
        }*/

        if (model.city.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvLocation.text = model.city
            binding.clCustomerDetails.tvLocation.visibility = View.VISIBLE

            if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
                binding.clCustomerDetails.tvLocation.paintFlags =
                    binding.clCustomerDetails.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                binding.clCustomerDetails.tvLocation.paintFlags =
                    binding.clCustomerDetails.tvLocation.paintFlags or Paint.ANTI_ALIAS_FLAG
            }

            binding.clCustomerDetails.tvLocation.setOnClickListener {
                viewCustomerLocation(model)
            }
        } else {
            binding.clCustomerDetails.tvLocation.visibility = View.GONE
        }

        binding.mainContent.visibility = View.VISIBLE



        binding.clCustomerDetails.ivPhoneCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${model.mobile}")
            startActivity(intent)
        }


    }

    private fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }
    }

    private fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                Intent(this, OrgPhotosViewActivity::class.java).putExtra(
                    AppConstant.PRODUCT_INFO, imageListModel
                ).putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }


    private fun initTabLayout(customerSubLevelName: String, customerSubLevelCount: Int) {

        binding.tabLayout.removeAllTabs()
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.lbl_insights))
                .setIcon(R.drawable.ic_activity_record)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.orders))
                .setIcon(R.drawable.ic_order_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getString(R.string.lbl_activity))
                .setIcon(R.drawable.ic_record_activity)
        )

        if (customerSubLevelName.isNotBlank() && customerSubLevelCount != 0) {
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText("$customerSubLevelCount $customerSubLevelName")
                    .setIcon(R.drawable.ic_customer_tab_theme)
            )
        }

        //binding.tabLayout.addTab(binding.tabLayout.newTab().setText(resources.getString(R.string.lbl_inventory)).setIcon(R.drawable.ic_inventory))

        val  tabsTitleList =  if (customerSubLevelName.isNotBlank() && customerSubLevelCount != 0) {
            arrayListOf(
                AppConstant.INSIGHTS, AppConstant.ACTIVITY, AppConstant.ORDER, "$customerSubLevelCount $customerSubLevelName"
            )
        }else{
            arrayListOf(
                AppConstant.INSIGHTS, AppConstant.ACTIVITY, AppConstant.ORDER,
            )
        }

        customerDetailFragmentPagerAdapter = CustomerDetailFragmentPagerAdapter(
            this, customerId, tabsTitleList , false, this
        )

        binding.viewPager.adapter = customerDetailFragmentPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

    }


    override fun onNotifyDataChange() {
        isDataChange = true
    }

    /*override fun onConfirm(customerName: String, customerID: Int) {
        val checkoutRequest = CheckoutRequest(customerID)
        checkOutViewModel.getCheckOutData(
            checkoutRequest,
            hasInternetConnection = hasInternetConnection()
        )

    }*/

    override fun onCheckOutConfirm(customerName: String, customerID: Int) {

        CheckOutDialog.showCheckOutDialog(
            this,
            customerName = customerData?.name ?: "",
            customerID = customerData?.id ?: 0,
            listener = this
        )


    }

    private fun openCustomerList() {
        val i = Intent(this, ListOfCustomerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    override fun openActivity() {
        if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
            chooseActivityBottomSheet.show(
                supportFragmentManager, ChooseActivityBottomSheet::class.java.name
            )
        }else{
            supportFragmentManager.fragments.remove(chooseActivityBottomSheet)
            if (supportFragmentManager.fragments.contains(chooseActivityBottomSheet).not()){
                chooseActivityBottomSheet.show(
                    supportFragmentManager, ChooseActivityBottomSheet::class.java.name
                )
            }
        }
    }

    override fun onCheckoutConfirm(customerName: String, customerID: Int) {
        val checkoutRequest = CheckoutRequest(customerID)
        checkOutViewModel.getCheckOutData(
            checkoutRequest, hasInternetConnection = hasInternetConnection()
        )
    }


    fun checkInDataValidation() {
        if (checkSetting) {
            if (checkInData?.isCheckedIn == true) {
                if (customerData?.id == checkInData?.customerId) {
                    binding.btnChoseActivity.enable()
                    binding.btnTelephonicOrder.enable()
                    binding.btnCheckIn.enable()
                    if (isTelephonicEnabled && binding.btnCheckIn.isVisible) {
                        binding.btnTelephonicOrder.showView()
                    } else {
                        binding.btnTelephonicOrder.hideView()
                    }
                } else {
                    binding.btnChoseActivity.disableOnlyAlpha()
                    binding.btnTelephonicOrder.disableOnlyAlpha()
                    binding.btnCheckIn.disableOnlyAlpha()
                }
            } else {
                binding.btnChoseActivity.enable()
                binding.btnTelephonicOrder.enable()
                binding.btnCheckIn.enable()
                if (isTelephonicEnabled && binding.btnCheckIn.isVisible) {
                    binding.btnTelephonicOrder.showView()
                } else {
                    binding.btnTelephonicOrder.hideView()
                }
            }
        } else {
            binding.btnChoseActivity.enable()
            binding.btnTelephonicOrder.enable()
            binding.btnCheckIn.enable()
            if (isTelephonicEnabled && binding.btnCheckIn.isVisible) {
                binding.btnTelephonicOrder.showView()
            } else {
                binding.btnTelephonicOrder.hideView()
            }
        }
    }


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
                        AppConstant.LOCATION_TRACKING, false
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
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {

        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
            binding.btnChoseActivity.disableAndAlpha()
            binding.btnCheckIn.disableAndAlpha()
            binding.btnProceedToCreateOrder.disableAndAlpha()
            binding.clLoadingLocation.showView()
        }

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
                            this@CustomerDetailActivity,
                            longitude = currentGeoLocationLong,
                            latitude = currentGeoLocationLat
                        ) { address ->
                            currentGeoAddress = address
                        }

                        if (SharedPref.getInstance().getBoolean(
                                AppConstant.GEO_FENCING_ENABLE, false
                            ) && binding.clLoadingLocation.isVisible
                        ) {
                            binding.btnChoseActivity.enable()
                            binding.btnCheckIn.enable()
                            binding.btnProceedToCreateOrder.enable()
                            binding.clLoadingLocation.hideView()
                        }

                        checkInDataValidation()


                    } else {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

                        if (isFinishing.not() && supportFragmentManager.isStateSaved.not()) {
                            val fragment =
                                MockLocationDetectedDialogFragment.getInstance(this@CustomerDetailActivity)
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
        customerModel: CustomerData, onInsideFence: (Boolean) -> Unit
    ) {
        val isUserInsideGeoFencing =
            if (customerModel?.mapLocationLat != 0.0 && customerModel?.mapLocationLong != 0.0) {
                findUserIsInGeoFencingArea(
                    customerModel?.mapLocationLat ?: 0.0,
                    customerModel?.mapLocationLong ?: 0.0,
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

    override fun onResume() {
        super.onResume()
        getUserCurrentLocation()
    }

}




