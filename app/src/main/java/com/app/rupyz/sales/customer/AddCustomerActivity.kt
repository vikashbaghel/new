package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddCustomerBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.helper.InputFilterMinMax
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.scrollToBottom
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharePrefConstant.CUSTOMER_CATEGORY_MAPPING_ENABLE
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.STAFF_AND_CUSTOMER_MAPPING
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.PostOfficeItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.PanDataInfoModel
import com.app.rupyz.model_kt.order.customer.SegmentDataItem
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.app.rupyz.sales.preference.UploadingOfflineDataActivity
import com.app.rupyz.sales.product.ProductViewModel
import com.app.rupyz.sales.staff.AssignBeatForStaffAdapter
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.ImageUploadListener
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.more.MoreViewModel
import com.app.rupyz.ui.organization.profile.adapter.CustomAutoCompleteAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class AddCustomerActivity : BaseActivity(), StaffListForAssignAdapter.IAssignStaffListener,
        CustomerNameRvAdapter.ICustomerSelectListener,
        LocationPermissionUtils.ILocationPermissionListener,
        ProductCategoryListForAssignAdapter.IAssignCategoryListener,
        MockLocationDetectedDialogFragment.IMockLocationActionListener, ImageUploadListener,
        AssignBeatForStaffAdapter.IAssignBeatForStaffListener {
    private lateinit var binding: ActivityAddCustomerBinding

    private var customerParentId: Int? = null
    private var customerParentName: String? = null
    private val customerViewModel: CustomerViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private val beatViewModel: BeatViewModel by viewModels()
    private var customerData = CustomerData()

    private var paymentTermsSelected: String = ""

    private var selectedSegment: SegmentDataItem? = null
    private var staffList: ArrayList<NameAndIdSetInfoModel> = ArrayList()

    private var addStaffSetList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var removeStaffSetList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var customerLevelList: ArrayList<String> = ArrayList()
    private var pricingGroupList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var customerList = ArrayList<CustomerData>()
    private var productCategoryList = ArrayList<AllCategoryResponseModel>()
    private var addCategoryList = ArrayList<Int?>()
    private var removeCategoryList = ArrayList<Int?>()
    private var beatList: ArrayList<NameAndIdSetInfoModel> = ArrayList()
    private var addBeatSet: ArrayList<Int?> = ArrayList()
    private var removeBeatSet: ArrayList<Int?> = ArrayList()
    private var alreadyAddedBeatList: ArrayList<Int> = ArrayList()
    private var customerTypeList: ArrayList<String> = ArrayList()

    private lateinit var staffListForAssignAdapter: StaffListForAssignAdapter
    private lateinit var customerAdapter: CustomerNameRvAdapter
    private lateinit var productCategoryListForAssignAdapter: ProductCategoryListForAssignAdapter
    private lateinit var assignBeatForStaffAdapter: AssignBeatForStaffAdapter

    private var customerCurrentPage = 1
    private var customerTypeCurrentPage = 1

    private var categoryHeaders: String? = defaultHeader
    private var assignStaffHeaders: String? = defaultHeader
    private var assignBeatHeaders: String? = defaultHeader

    private var selectedPricingGroupId: Int? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var currentGeoLocationLat: Double = 0.00
    private var currentGeoLocationLong: Double = 0.00
    private var currentGeoAddress : String? = null

    private var leadGeoLocationLat: Double = 0.00
    private var leadGeoLocationLong: Double = 0.00

    private var completeAddress: String? = ""

    private var creditLimit: Double = 0.0
    private var outstanding: Double = 0.0

    private var delay: Long = 500 // 1 seconds after user stops typing

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    private var isPageLoading = false
    private var isPageLoadingForProductCategory = false
    private var isPageLoadingForAssignBeat = false
    private var isPageLoadingForCustomer = false

    private var isApiLastPage = false
    private var isApiLastPageForCustomer = false
    private var isApiLastPageForAssignBeat = false
    private var isCategoryApiLastPage = false

    private var isUpdate: Boolean = false
    private var isAllStaffChecked = false
    private var isDeselectAllStaff = false
    private var isFirstTimeLoadCustomer = true
    private var isFirstTimeLoadCategory = true
    private var isStaffSetUpdated = false

    private var customerLevel = AppConstant.CUSTOMER_LEVEL_1
    private var customerLevelForApiMapping = ""

    private var leadModel: LeadLisDataItem? = null
    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private var logoImagePath: String? = null
    private var logoImageId: Int? = null
    private var prevS3LogoImageId: Int? = null

    private var progressDialog: ProgressDialog? = null

    val customer = CustomerData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionUtils = LocationPermissionUtils(this, this)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("uploading ...")
        progressDialog!!.setCancelable(false)

        launch {
            initLayout()
        }

        initObservers()

        launch {
            loadCustomerLevel()
            moreViewModel.getPricingGroupList()
            loadBeatList()
            customerViewModel.getCustomerTypeList("", customerTypeCurrentPage, hasInternetConnection())
        }

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            isUpdate = true
            binding.tvToolbarTitle.text = resources.getString(R.string.update_customer)
            binding.btnAdd.text = resources.getString(R.string.update)

            binding.progressBar.visibility = View.VISIBLE


            launch {
                if (hasInternetConnection() && intent.hasExtra(AppConstant.ANDROID_OFFLINE_TAG)) {
                    customerViewModel.getCustomerById(
                            intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                            false
                    )
                } else {
                    customerViewModel.getCustomerById(
                            intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                            hasInternetConnection()
                    )
                }
            }

        } else if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)
            initLeadData(leadModel)
        } else {
            binding.mainContent.visibility = View.VISIBLE
        }

        initRecyclerView()
    }

    private fun initLeadData(leadModel: LeadLisDataItem?) {
        binding.etBusinessName.setText(leadModel?.businessName)

        binding.etMobileNumber.setText(leadModel?.mobile)
        binding.etGstNo.setText(leadModel?.gstin)
        binding.etContactPerson.setText(leadModel?.contactPersonName)
        binding.etEmailId.setText(leadModel?.email)
        binding.etAddressLine1.setText(leadModel?.addressLine1)
        binding.etCity.setText(leadModel?.city)

        if (leadModel?.logoImageUrl.isNullOrEmpty().not()) {
            binding.ivCompanyImage.visibility = View.VISIBLE
            ImageUtils.loadImage(leadModel?.logoImageUrl, binding.ivCompanyImage)
        }

        if (leadModel?.imageLogo != null) {
            logoImageId = leadModel.imageLogo
        }

        if (leadModel?.mapLocationLat != null && leadModel.mapLocationLat != 0.0
                && leadModel.mapLocationLong != null && leadModel.mapLocationLong != 0.0
        ) {
            setMapLocation(LatLng(leadModel.mapLocationLat!!, leadModel.mapLocationLong!!), true)

            binding.tvSetNewLocation.text =
                    resources.getString(R.string.update_location)

            if (leadModel.geoAddress.isNullOrEmpty().not()) {
                binding.groupMapView.visibility = View.VISIBLE
                binding.tvGeoLocation.text = leadModel.geoAddress
            }
        }

        if (leadModel?.state != null) {
            binding.spinnerState.setText(leadModel.state)
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }

        binding.etPinCode.setText(leadModel?.pincode)

        binding.mainContent.visibility = View.VISIBLE
    }

    private fun loadCustomerLevel() {

        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                        .not()
        ) {
            customerLevelList.add(SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1))
        }

        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                        .not()
        ) {
            customerLevelList.add(SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2))
        }


        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                        .not()
        ) {
            customerLevelList.add(SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3))
        }

        val arrayAdapter = ArrayAdapter(
                this, R.layout.single_text_view_spinner_16dp_text, customerLevelList
        )

        arrayAdapter.setDropDownViewResource(R.layout.single_text_view_spinner_16dp_text)
        binding.spinnerCustomerLevel.adapter = arrayAdapter
    }

    private fun setMapLocation(position: LatLng, updateLeadLocation: Boolean) {
        if (updateLeadLocation) {
            leadGeoLocationLat = position.latitude
            leadGeoLocationLong = position.longitude
            customerData.mapLocationLat = leadGeoLocationLat
            customerData.mapLocationLong = leadGeoLocationLong
            customerData.activityGeoAddress = currentGeoAddress
        }
    }

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

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    if (Utils.isMockLocation(it)) {
                        val fragment =
                                MockLocationDetectedDialogFragment.getInstance(this@AddCustomerActivity)
                        fragment.isCancelable = false
                        fragment.show(
                                supportFragmentManager,
                                MockLocationDetectedDialogFragment::class.java.name
                        )
                    } else {
                        currentGeoLocationLat = it.latitude
                        currentGeoLocationLong = it.longitude
                        currentGeoAddress = GeoLocationUtils.getAddress(this@AddCustomerActivity,
                                longitude = currentGeoLocationLong, latitude =  currentGeoLocationLat)
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun initLayout() {

        binding.tvFetchPanDetails.setOnClickListener {
            if (binding.etBusinessPan.text.toString().length == 10) {
                if (intent.hasExtra(AppConstant.CUSTOMER_ID).not() && hasInternetConnection()) {
                    val panDataInfoModel = PanDataInfoModel()
                    panDataInfoModel.org_id = SharedPref.getInstance().getInt(ORG_ID)
                    panDataInfoModel.pan_id = binding.etBusinessPan.text.toString()
                    panDataInfoModel.in_details = true

                    binding.progressBarPanVerified.visibility = View.VISIBLE
                    customerViewModel.getCustomerDetailsByPan(panDataInfoModel)

                    Utils.hideKeyboard(this)
                }
            } else {
                Toast.makeText(this, "Enter valid PAN number!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etGstNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etGstNo.hasFocus() && s.toString().length >= 15 && hasInternetConnection()) {
                    binding.progressBarGst.visibility = View.VISIBLE
                    customerViewModel.getCustomerDetailsByGst(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.etBusinessPan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.ivPanVerified.visibility = View.GONE
                binding.tvFetchPanDetails.setBackgroundResource(R.drawable.add_product_green_btn_gredient)
                binding.tvFetchPanDetails.setTextColor(resources.getColor(R.color.white))
                binding.tvFetchPanDetails.isEnabled = true
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.spinnerPaymentTerms.adapter = ArrayAdapter(
                this,
                R.layout.single_text_view_spinner_16dp_text,
                this.resources.getStringArray(R.array.payment_terms)
        )

        binding.spinnerPaymentTerms.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        paymentTermsSelected = if (position != 0) {
                            binding.spinnerPaymentTerms.selectedItem.toString()
                        } else {
                            ""
                        }
                    }
                }

        binding.spinnerPricingGroup.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        if (position != 0) {
                            selectedPricingGroupId = pricingGroupList[position - 1].id
                        }
                    }
                }

        val stateList: MutableList<String> =
                resources.getStringArray(R.array.states).toMutableList()

        val adapter = CustomAutoCompleteAdapter(this, stateList)
        binding.spinnerState.threshold = 0
        binding.spinnerState.setAdapter(adapter)

        binding.spinnerState.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }

        binding.spinnerState.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isDestroyed.not())
                binding.spinnerState.showDropDown()
        }

        binding.spinnerState.setOnClickListener {
            if (isDestroyed.not())
                binding.spinnerState.showDropDown()
        }

        binding.ivClearStateName.setOnClickListener {
            binding.spinnerState.setText("")
            binding.ivClearStateName.visibility = View.GONE
            binding.ivDropDown.visibility = View.VISIBLE
        }

        binding.spinnerCustomerLevel.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        when (position) {
                            0 -> {
                                customerLevel = AppConstant.CUSTOMER_LEVEL_1
                                binding.clCustomerLevelParent.visibility = View.GONE
                                binding.clCustomerList.visibility = View.GONE
                                binding.tvParentCustomerName.text = ""
                                customerParentId = null
                                customerParentName = null
                            }

                            1 -> {
                                customerLevel = AppConstant.CUSTOMER_LEVEL_2

                                val mapString = resources.getString(
                                        R.string.customer_level_parent_mapping, customerLevelList[0]
                                )
                                val length = mapString.length
                                val spannable = SpannableString(mapString)
                                spannable.setSpan(ForegroundColorSpan(Color.RED), length - 1, length, 0)
                                binding.hdCustomerLevelParent.text = spannable

                                binding.clCustomerLevelParent.visibility = View.VISIBLE

                                binding.clCustomerList.visibility = View.GONE
                                customerLevelForApiMapping = AppConstant.CUSTOMER_LEVEL_1


                                binding.tvParentCustomerName.text = ""

                                customerParentId = null
                                customerParentName = null

                                loadCustomerPage()
                            }

                            2 -> {
                                customerLevel = AppConstant.CUSTOMER_LEVEL_3

                                val mapString = resources.getString(
                                        R.string.customer_level_parent_mapping, customerLevelList[1]
                                )
                                val length = mapString.length
                                val spannable = SpannableString(mapString)
                                spannable.setSpan(ForegroundColorSpan(Color.RED), length - 1, length, 0)
                                binding.hdCustomerLevelParent.text = spannable

                                binding.clCustomerLevelParent.visibility = View.VISIBLE

                                binding.clCustomerList.visibility = View.GONE
                                customerLevelForApiMapping = AppConstant.CUSTOMER_LEVEL_2


                                binding.tvParentCustomerName.text = ""

                                customerParentId = null
                                customerParentName = null

                                loadCustomerPage()
                            }
                        }
                    }
                }

        binding.clChangeLocation.setOnClickListener {
            startActivityForGeoLocation.launch(
                    Intent(this, CustomerGeoMapLocationActivity::class.java).putExtra(
                            AppConstant.CUSTOMER, customerData
                    )
            )
        }

        if (isStaffUser.not() || SharedPref.getInstance().getBoolean(STAFF_AND_CUSTOMER_MAPPING, false)) {
            binding.groupCustomerMapping.visibility = View.VISIBLE
            loadStaff()
        } else {
            binding.groupCustomerMapping.visibility = View.GONE
        }

        if (isStaffUser.not() || SharedPref.getInstance().getBoolean(CUSTOMER_CATEGORY_MAPPING_ENABLE, false)) {
            binding.groupProductCategoryMapping.visibility = View.VISIBLE
            loadProductCategory()
        } else {
            binding.groupProductCategoryMapping.visibility = View.GONE
        }

        binding.clAssignCustomer.setOnClickListener {
            binding.progressBarStaffListPagination.visibility = View.GONE
            binding.groupStaffAssignInput.isVisible = binding.groupStaffAssignInput.isVisible.not()

            Utils.hideKeyboard(this)
            if (binding.groupStaffAssignInput.isVisible) {
                binding.groupCategoryAssignInput.visibility = View.GONE
            }

            binding.mainScrollView.post {
                binding.mainScrollView.scrollToBottom()
            }
        }

        binding.clAssignProductCategory.setOnClickListener {
            binding.groupCategoryAssignInput.isVisible = !binding.groupCategoryAssignInput.isVisible
            if (binding.groupCategoryAssignInput.isVisible) {
                binding.groupStaffAssignInput.visibility = View.GONE
            }
            Utils.hideKeyboard(this)
            binding.mainScrollView.post {
                binding.mainScrollView.scrollToBottom()
            }
        }

        binding.clImageUpload.setOnClickListener {
            val fragment = ImageUploadBottomSheetDialogFragment.newInstance(this)
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
            fragment.arguments = bundle
            fragment.show(supportFragmentManager, AppConstant.PROFILE_SLUG)
        }

        binding.ivSearchStaff.setOnClickListener {
            assignStaffHeaders = defaultHeader
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            Utils.hideKeyboard(this)
            loadStaff()
        }

        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                assignStaffHeaders = defaultHeader
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
            assignStaffHeaders = defaultHeader
            isApiLastPage = false
            staffList.clear()
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }

        binding.ivSearchProductCategory.setOnClickListener {
            categoryHeaders = defaultHeader
            isCategoryApiLastPage = false
            isPageLoadingForProductCategory = true
            productCategoryList.clear()
            loadProductCategory()
            Utils.hideKeyboard(this)
        }

        binding.etSearchProductCategory.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                categoryHeaders = defaultHeader
                isCategoryApiLastPage = false
                isPageLoadingForProductCategory = true
                productCategoryList.clear()
                loadProductCategory()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchProductCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForCategory);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForCategory, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchCategory.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearchCategory.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearchCategory.setOnClickListener {
            binding.etSearchProductCategory.setText("")
            categoryHeaders = defaultHeader
            isCategoryApiLastPage = false
            productCategoryList.clear()
            productCategoryListForAssignAdapter.notifyDataSetChanged()
            loadProductCategory()
        }

        binding.tvRemoveLocation.setOnClickListener {
            leadGeoLocationLat = 0.0
            leadGeoLocationLong = 0.0
            
            customerData.mapLocationLat = leadGeoLocationLat
            customerData.mapLocationLong = leadGeoLocationLong
            customerData.activityGeoAddress = currentGeoAddress

            binding.tvSetNewLocation.text = resources.getString(R.string.fetch_location)

            binding.tvGeoLocation.text = ""
            binding.groupMapView.visibility = View.GONE
        }

        binding.imgClose.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {
            submitData()
        }

        binding.cbAllCustomerName.setOnCheckedChangeListener { _, isChecked ->
            isStaffSetUpdated = true
            isAllStaffChecked = isChecked
            staffList.forEach {
                it.isSelected = isChecked
            }
            staffListForAssignAdapter.notifyDataSetChanged()

            if (isChecked) {
                isDeselectAllStaff = false
            } else {
                isDeselectAllStaff = true
                addStaffSetList.clear()
            }
        }

        binding.mainContent.setOnClickListener {
            binding.clCustomerList.visibility = View.GONE
        }

        binding.tvParentCustomerName.setOnClickListener {
            binding.clCustomerList.isVisible = !binding.clCustomerList.isVisible
        }

        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.hasFocus() && s.toString()
                                .isNotBlank() && s.toString().length == 6 && hasInternetConnection()
                ) {
                    binding.pinCodeProgressBar.visibility = View.VISIBLE
                    moreViewModel.getPostalResponse(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

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

        binding.clBeat.setOnClickListener {
            binding.rvBeatList.isVisible = binding.rvBeatList.isVisible.not()
        }

        binding.etOutStanding.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                        9, AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
        )

        binding.etOutStanding.filters = arrayOf<InputFilter>(InputFilterMinMax("0", "100000000"))

        binding.etCreditLimit.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                        9, AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
        )
        binding.etCreditLimit.filters = arrayOf<InputFilter>(InputFilterMinMax("0", "100000000"))

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchCustomer.setText("")
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerPage()
            binding.ivClearSearch.visibility = View.GONE
        }

        binding.actvCustomerType.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isDestroyed.not())
                binding.actvCustomerType.showDropDown()
        }

        binding.actvCustomerType.setOnClickListener {
            if (isDestroyed.not())
                binding.actvCustomerType.showDropDown()
        }
    }

    private var startActivityForGeoLocation =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK && result.data != null && result.data!!.hasExtra(
                                AppConstant.LOCATION
                        )
                ) {
                    binding.tvSetNewLocation.text = resources.getString(R.string.update_location)

                    result.data?.let {
                        val latLong = it.getParcelableExtra<LatLng>(AppConstant.LOCATION)!!
                        completeAddress = it.getStringExtra(AppConstant.ADDRESS)

                        binding.groupMapView.visibility = View.VISIBLE
                        binding.tvGeoLocation.text = completeAddress?.trim()

                        setMapLocation(latLong, true)
                    }
                }
            }

    private fun loadProductCategory() {
        if (categoryHeaders.isNullOrEmpty().not()) {
            binding.progressBarCategoryListPagination.visibility = View.VISIBLE
        }

        productViewModel.getAllCategoryListWithCustomer(
                intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                binding.etSearchProductCategory.text.toString(),
                categoryHeaders, hasInternetConnection()
        )
    }

    private fun loadBeatList() {
        beatViewModel.getCustomerBeatMapping(intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                "", assignBeatHeaders, hasInternetConnection())
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            customerCurrentPage = 1
            customerList.clear()
            loadCustomerPage()
            customerAdapter.notifyDataSetChanged()
        }
    }


    private val inputFinishCheckerForStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            assignStaffHeaders = defaultHeader
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }
    }


    private val inputFinishCheckerForCategory = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            categoryHeaders = defaultHeader
            productCategoryList.clear()
            productCategoryListForAssignAdapter.notifyDataSetChanged()
            loadProductCategory()
        }
    }

    private fun loadStaff() {
        binding.progressBarStaffListPagination.visibility = View.VISIBLE
        customerViewModel.getStaffListWithCustomerMapping(
                intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                binding.etSearchStaff.text.toString(),
                assignStaffHeaders, hasInternetConnection()
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
                isPageLoadingForCustomer = true
                customerCurrentPage += 1
                loadCustomerPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForCustomer
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForCustomer
            }
        })

        val llM3 = LinearLayoutManager(this)
        binding.rvCategoryListForAssign.layoutManager = llM3
        productCategoryListForAssignAdapter =
                ProductCategoryListForAssignAdapter(productCategoryList, this)
        binding.rvCategoryListForAssign.adapter = productCategoryListForAssignAdapter

        binding.rvCategoryListForAssign.addOnScrollListener(object :
                PaginationScrollListener(llM3) {
            override fun loadMoreItems() {
                isPageLoadingForProductCategory = true
                loadProductCategory()
            }

            override fun isLastPage(): Boolean {
                return isCategoryApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForProductCategory
            }
        })

        val llM4 = LinearLayoutManager(this)
        binding.rvBeatList.layoutManager = llM4
        assignBeatForStaffAdapter = AssignBeatForStaffAdapter(beatList, this)
        binding.rvBeatList.adapter = assignBeatForStaffAdapter

        binding.rvBeatList.addOnScrollListener(object : PaginationScrollListener(llM4) {
            override fun loadMoreItems() {
                isPageLoadingForAssignBeat = true
                loadBeatList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForAssignBeat
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForAssignBeat
            }
        })
    }

    private fun loadCustomerPage() {
        customerViewModel.getCustomerList(
                null,
                binding.etSearchCustomer.text.toString(),
                customerLevelForApiMapping,
                ArrayList(),
                AppConstant.SORTING_LEVEL_ASCENDING,
                customerCurrentPage,
                hasInternetConnection()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        customerViewModel.getCustomerByIdData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { it ->
                    customerData = it
                    binding.etBusinessName.setText(customerData.name)
                    binding.etBusinessPan.setText(customerData.panId)

                    binding.etMobileNumber.setText(customerData.mobile)
                    binding.etGstNo.setText(customerData.gstin)
                    binding.etContactPerson.setText(customerData.contactPersonName)
                    binding.etEmailId.setText(customerData.email)
                    binding.etAddressLine1.setText(customerData.addressLine1)
                    binding.etCity.setText(customerData.city)

                    if (customerData.state != null) {
                        binding.spinnerState.setText(customerData.state)
                        binding.ivClearStateName.visibility = View.VISIBLE
                        binding.ivDropDown.visibility = View.GONE
                    }

                    if (customerData.logoImageUrl.isNullOrEmpty().not()) {
                        binding.ivCompanyImage.visibility = View.VISIBLE
                        ImageUtils.loadImage(customerData.logoImageUrl, binding.ivCompanyImage)
                    }

                    if (!customerData.pricingGroupName.isNullOrEmpty()) {
                        var index =
                                pricingGroupList.indexOfFirst { it.name == customerData.pricingGroupName }
                        index++
                        binding.spinnerPricingGroup.setSelection(index)
                    }


                    if (customerData.mapLocationLat != null && customerData.mapLocationLat != 0.0
                            && customerData.mapLocationLong != null && customerData.mapLocationLong != 0.0
                    ) {
                        leadGeoLocationLat = customerData.mapLocationLat ?: 0.0
                        leadGeoLocationLong = customerData.mapLocationLong ?: 0.0
                        
                        binding.tvSetNewLocation.text =
                                resources.getString(R.string.update_location)

                        if (customerData.geoAddress.isNullOrEmpty().not()) {
                            completeAddress = customerData.geoAddress

                            binding.groupMapView.visibility = View.VISIBLE
                            binding.tvGeoLocation.text = customerData.geoAddress
                        }
                    }

                    binding.actvCustomerType.setText(customerData.customer_type)

                    if (!customerData.customerLevel.isNullOrEmpty()) {
                        when (customerData.customerLevel) {
                            AppConstant.CUSTOMER_LEVEL_1 -> binding.spinnerCustomerLevel.setSelection(
                                    0
                            )

                            AppConstant.CUSTOMER_LEVEL_2 -> {
                                binding.spinnerCustomerLevel.setSelection(1)
                                Handler(Looper.myLooper()!!).postDelayed({
                                    if (!customerData.customerParentName.isNullOrEmpty()) {
                                        customerParentId = customerData.customerParent
                                        customerParentName = customerData.customerParentName
                                        binding.tvParentCustomerName.text =
                                                customerData.customerParentName
                                    }
                                }, 1000)
                            }

                            AppConstant.CUSTOMER_LEVEL_3 -> {
                                binding.spinnerCustomerLevel.setSelection(2)
                                Handler(Looper.myLooper()!!).postDelayed({
                                    if (!customerData.customerParentName.isNullOrEmpty()) {
                                        customerParentId = customerData.customerParent
                                        customerParentName = customerData.customerParentName
                                        binding.tvParentCustomerName.text =
                                                customerData.customerParentName
                                    }
                                }, 1000)
                            }
                        }
                    }

                    if (customerData.allow_all_staff != null) {
                        binding.cbAllCustomerName.isChecked = customerData.allow_all_staff!!
                    }

                    binding.etPinCode.setText(customerData.pincode)

                    if (customerData.paymentTerm != null) {
                        val index =
                                listOf(*resources.getStringArray(R.array.payment_terms)).indexOf(
                                        customerData.paymentTerm
                                )
                        binding.spinnerPaymentTerms.setSelection(index)
                    }

                    if (customerData.beats.isNullOrEmpty().not()) {
                        alreadyAddedBeatList.addAll(customerData.beats!!)

                        if (beatList.isNotEmpty()) {
                            beatList.forEachIndexed { index, nameAndIdSetInfoModel ->
                                if (alreadyAddedBeatList.contains(nameAndIdSetInfoModel.id)) {
                                    beatList[index].isSelected = true
                                }
                            }

                            assignBeatForStaffAdapter.notifyDataSetChanged()
                        }
                    }

                    binding.etCreditLimit.setText(
                            "" + CalculatorHelper().formatDoubleDecimalPoint(
                                    customerData.creditLimit!!,
                                    AppConstant.TWO_DECIMAL_POINTS
                            )
                    )

                    binding.etOutStanding.setText(
                            "" + CalculatorHelper().formatDoubleDecimalPoint(
                                    customerData.outstandingAmount!!,
                                    AppConstant.TWO_DECIMAL_POINTS
                            )
                    )

                    if (hasInternetConnection().not()) {
                        if (customerData.selectStaff != null && customerData.selectStaff?.addSet.isNullOrEmpty().not()) {
                            customerData.selectStaff?.addSet?.forEach {
                                val idModel = NameAndIdSetInfoModel()
                                idModel.id = it
                                addStaffSetList.add(idModel)
                            }

                            for (staff in staffList) {
                                if (customerData.selectStaff?.addSet!!.contains(staff.id!!)) {
                                    staff.isSelected = true
                                }
                            }

                            staffListForAssignAdapter.notifyDataSetChanged()
                        }

                        if (customerData.selectBeat != null && customerData.selectBeat?.addSet.isNullOrEmpty().not()) {
                            addBeatSet.addAll(customerData.selectBeat?.addSet!!)

                            for (beat in beatList) {
                                if (addBeatSet.contains(beat.id)) {
                                    beat.isSelected = true
                                }
                            }

                            assignBeatForStaffAdapter.notifyDataSetChanged()
                        }
                    }
                    binding.mainContent.visibility = View.VISIBLE
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }

        customerViewModel.getCustomerListData().observe(this) { data ->
            data.data?.let { it ->
                binding.progressBar.visibility = View.GONE
                isPageLoadingForCustomer = false
                if (it.isNotEmpty()) {
                    if (customerCurrentPage == 1) {
                        customerList.clear()
                    }
                    customerList.addAll(it)
                    customerAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPageForCustomer = true
                    }
                } else {
                    isApiLastPageForCustomer = true
                    if (customerCurrentPage == 1) {
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        customerViewModel.addCustomerLiveData().observe(this) { data ->
            binding.btnAdd.isEnabled = true
            progressDialog?.dismiss()
            data?.let {
                if (data.error == false) {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(AppConstant.CUSTOMER_NAME, binding.etBusinessName.text)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    if (it.errorCode != null && it.errorCode == 403) {
                        logout()
                    } else {
                        showToast(it.message)
                    }
                }
            }
        }

        customerViewModel.updateCustomerLiveData().observe(this) { data ->
            binding.btnAdd.isEnabled = true
            progressDialog?.dismiss()
            data?.let {
                if (data.error == false) {
                    showToast(data.message)

                    if (intent.hasExtra(AppConstant.ANDROID_OFFLINE_TAG).not()) {
                        val intent = Intent()
                        intent.putExtra(AppConstant.CUSTOMER_ID, data.data?.id)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        someActivityResultLauncher.launch(Intent(this, UploadingOfflineDataActivity::class.java))
                    }
                } else {
                    showToast(data.message)
                }
            }
        }

        customerViewModel.customerDetailsLiveData.observe(this) { data ->
            data?.let {
                binding.progressBarPanVerified.visibility = View.GONE
                binding.progressBarGst.visibility = View.GONE
                binding.etBusinessPan.isEnabled = true

                if (data.error == false) {

                    if (binding.etGstNo.hasFocus().not()) {
                        binding.ivPanVerified.visibility = View.VISIBLE
                        binding.tvFetchPanDetails.setBackgroundResource(R.drawable.check_score_button_style_disable)
                        binding.tvFetchPanDetails.setTextColor(resources.getColor(R.color.black))
                        binding.tvFetchPanDetails.isEnabled = false
                    }

                    showToast(data.message)

                    binding.progressBar.visibility = View.GONE

                    data.data?.let { customerInfo ->
                        if (customerInfo.legalName.isNullOrEmpty().not()) {
                            binding.etBusinessName.setText(customerInfo.legalName.toString())
                        }
                        if (customerInfo.name.isNullOrEmpty().not()) {
                            binding.etBusinessName.setText(customerInfo.name.toString())
                        }
                        if (customerInfo.firstName.isNullOrEmpty().not()) {
                            binding.etContactPerson.setText(customerInfo.firstName.toString() + " " + customerInfo.lastName.toString())
                        }
                        if (customerInfo.email.isNullOrEmpty().not()) {
                            binding.etEmailId.setText(customerInfo.email.toString())
                        }
                        if (customerInfo.addressLine1.isNullOrEmpty().not()) {
                            binding.etAddressLine1.setText(customerInfo.addressLine1.toString() + " " + customerInfo.addressLine2.toString())
                        }
                        if (customerInfo.city.isNullOrEmpty().not()) {
                            binding.etCity.setText(customerInfo.city.toString())
                        }
                        if (customerInfo.pincode.isNullOrEmpty().not()) {
                            binding.etPinCode.setText(customerInfo.pincode.toString())
                        }
                        if (customerInfo.state.isNullOrEmpty().not()) {
                            binding.spinnerState.setText(customerInfo.state)
                        }
                    }
                } else {
                    showToast(data.message)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        customerViewModel.staffListWithCustomerMappingLiveDataWith.observe(this) {
            binding.progressBarStaffListPagination.visibility = View.GONE
            isPageLoading = false
            if (it.data.isNullOrEmpty().not()) {
                isPageLoading = false
                if (assignStaffHeaders == defaultHeader) {
                    staffList.clear()
                }

                if (isAllStaffChecked) {
                    it.data!!.forEach { model ->
                        model.isSelected = true
                        staffList.add(model)
                    }
                } else if (isDeselectAllStaff) {
                    it.data!!.forEach { model ->
                        model.isSelected = false
                        staffList.add(model)
                    }
                } else if (addStaffSetList.isNotEmpty() || removeStaffSetList.isNotEmpty()) {

                    it.data!!.forEach { staff ->
                        if (addStaffSetList.contains(staff)) {
                            staff.isSelected = true
                        }
                        if (removeStaffSetList.contains(staff)) {
                            staff.isSelected = false
                        }
                        staffList.add(staff)
                    }

                } else {
                    staffList.addAll(it.data!!)
                }

                staffListForAssignAdapter.notifyDataSetChanged()

                isFirstTimeLoadCustomer = false
            }

            it.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty().not()) {
                    assignStaffHeaders = headers.nextParams
                    if (it.data.isNullOrEmpty()) {
                        loadStaff()
                    }
                } else {
                    isApiLastPage = true
                }
            }
        }

        moreViewModel.pricingGroupLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { pricing ->

                    pricingGroupList.addAll(pricing)

                    val stringList = ArrayList<String>()
                    stringList.add("Select Pricing Group")

                    pricing.forEach { model ->
                        stringList.add(model.name!!)
                    }

                    binding.spinnerPricingGroup.adapter = ArrayAdapter(
                            this, R.layout.single_text_view_spinner_16dp_text, stringList
                    )
                }
            }
        }

        customerViewModel.customerTypeLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    if (data.isNotEmpty()) {

                        data.forEach { type ->
                            customerTypeList.add(type.name!!)
                        }
                        if (data.size == 30) {
                            customerTypeCurrentPage++
                            customerViewModel.getCustomerTypeList(
                                    "",
                                    customerTypeCurrentPage,
                                    hasInternetConnection()
                            )
                        } else {
                            val adapter = CustomAutoCompleteAdapter(this, customerTypeList)
                            binding.actvCustomerType.threshold = 1
                            binding.actvCustomerType.setAdapter(adapter)
                        }
                    }
                }
            }
        }

        productViewModel.categoryWithCustomerLiveData.observe(this) {
            isPageLoadingForProductCategory = false
            binding.progressBarCategoryListPagination.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { categoryList ->
                    if (categoryList.isNotEmpty()) {
                        if (addCategoryList.isNotEmpty() || removeCategoryList.isNotEmpty()) {
                            categoryList.forEach { cat ->
                                if (addCategoryList.contains(cat.id)) {
                                    cat.isSelected = true
                                }

                                if (removeCategoryList.contains(cat.id)) {
                                    cat.isSelected = false
                                }

                                productCategoryList.add(cat)
                            }
                        } else {
                            productCategoryList.addAll(categoryList)
                        }

                        productCategoryListForAssignAdapter.notifyDataSetChanged()

                        isFirstTimeLoadCategory = false
                    }

                    it.headers?.let { headers ->
                        if (headers.nextParams.isNullOrEmpty().not()) {
                            categoryHeaders = headers.nextParams
                            if (it.data.isNullOrEmpty()){
                                loadProductCategory()
                            }
                        } else {
                            isCategoryApiLastPage = true
                        }
                    }
                }
            }
        }

        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            if (model.error != null && model.error!!.not()) {
                progressDialog?.show()
                model.data?.let { data ->
                    if (data.id != null) {
                        logoImageId = data.id?.toInt()
                        updateInfo()
                    }
                }
            } else {
                progressDialog?.dismiss()
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            }
        }

        beatViewModel.orgBeatListLiveData.observe(this) {
            if (it.error == false) {
                isPageLoadingForAssignBeat = false
                if (it.data.isNullOrEmpty().not()) {
                    it.data?.let { beatPlan ->
                        beatPlan.forEach { plan ->
                            val model = NameAndIdSetInfoModel(plan.id, plan.name)
                            if (alreadyAddedBeatList.isNotEmpty()
                                    && alreadyAddedBeatList.contains(plan.id)
                            ) {
                                model.isSelected = true
                            }

                            beatList.add(model)
                        }

                        assignBeatForStaffAdapter.notifyDataSetChanged()
                    }
                }

                it.headers?.let { headers ->
                    if (headers.nextParams.isNullOrEmpty().not()) {
                        assignBeatHeaders = headers.nextParams

                        if (it.data.isNullOrEmpty()){
                            loadBeatList()
                        }
                    } else {
                        isApiLastPageForAssignBeat = true
                    }
                }
            }
        }

        moreViewModel.postalCodeResponseLiveData.observe(this) {
            binding.pinCodeProgressBar.visibility = View.GONE
            if (it.status == "Success") {
                it.postOffice?.let { postal ->
                    if (postal.isNotEmpty()) {
                        autoFillPostalOffice(postal[0])
                    }
                }
            } else if (it.status != "Failed") {
                showToast(it.message)
            }
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun autoFillPostalOffice(postOfficeItem: PostOfficeItem) {
        binding.etCity.setText(postOfficeItem.district)
        if (postOfficeItem.state.isNullOrEmpty().not()) {
            binding.spinnerState.setText(postOfficeItem.state)
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }
    }

    private fun submitData() {
        when {
            binding.etBusinessName.text.trim().toString().isEmpty() -> {
                showToast("Business Name Required!")
            }

            customerLevel != AppConstant.CUSTOMER_LEVEL_1 && customerParentId == null -> {
                showToast("Please select Customer parent!")
            }

            binding.etContactPerson.text.trim().toString().isEmpty() -> {
                showToast("Contact Person Name Required!")
            }

            !Validations().isValidName(
                    binding.etContactPerson.text.trim().toString()
            ) -> {
                showToast("Number and spacial character are not allowed!")
            }

            binding.etMobileNumber.text.trim().toString().isEmpty() -> {
                showToast("Mobile Number Required!")
            }

            !Validations().isValidMobileNumber(binding.etMobileNumber.text.trim().toString()) -> {
                showToast("Valid Mobile Number Required!")
            }

            binding.etGstNo.text.toString()
                    .isNotEmpty() && !Validations().gstValidation(binding.etGstNo.text.toString()) -> {
                showToast("Valid GST Number Required!")
            }

            binding.etEmailId.text.toString()
                    .isNotEmpty() && !Validations().isValidEmail(binding.etEmailId.text) -> {
                showToast("Valid Email Required!")
            }

            binding.etCity.text.trim().toString().isEmpty() -> {
                showToast("City Required!")
            }

            binding.spinnerState.text.toString().isEmpty() -> {
                showToast("State Required!")
            }

            else -> {
                progressDialog?.show()
                if (logoImagePath != null) {
                    if (hasInternetConnection() && intent.hasExtra(AppConstant.ANDROID_OFFLINE_TAG).not()) {
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                    this@AddCustomerActivity, File(logoImagePath)
                            ) {
                                quality(30)
                                resolution(512, 512)
                                size(197_152)
                            }
                            imageUploadViewModel.uploadCredentialsWithPrevS3Id(
                                    compressedImageFile.path, prevS3LogoImageId
                            )
                        }
                    } else {
                        customer.logoImageUrl = logoImagePath
                        updateInfo()
                    }
                } else {
                    updateInfo()
                }

            }
        }
    }

    private fun updateInfo() {
        binding.btnAdd.isEnabled = false

        customer.name = binding.etBusinessName.text.toString()
        customer.panId = binding.etBusinessPan.text.toString()
        customer.mobile = binding.etMobileNumber.text.toString()
        customer.gstin = binding.etGstNo.text.toString()
        customer.contactPersonName = binding.etContactPerson.text.toString()
        customer.email = binding.etEmailId.text.toString()
        customer.addressLine1 = binding.etAddressLine1.text.toString()
        customer.city = binding.etCity.text.toString()
        customer.state = binding.spinnerState.text.toString()
        customer.pincode = binding.etPinCode.text.toString()

        customer.customer_type = binding.actvCustomerType.text.toString()
        customer.customerLevel = customerLevel
        if (customerLevel != AppConstant.CUSTOMER_LEVEL_1) {
            customer.customerParent = customerParentId
            customer.customerParentName = customerParentName
        }

        if (selectedPricingGroupId != null) {
            customer.pricingGroup = selectedPricingGroupId
        }

        if (logoImageId != null) {
            customer.imageLogo = logoImageId
        }

        customer.segment_name = selectedSegment?.name
        customer.segment_discount_value = selectedSegment?.discountValue
        customer.segment_discount_unit = selectedSegment?.discountUnit

        customer.paymentTerm = paymentTermsSelected

        if (binding.etCreditLimit.text.toString().isNotEmpty()) {
            creditLimit = binding.etCreditLimit.text.toString().toDouble()
        }
        if (binding.etOutStanding.text.toString().isNotEmpty()) {
            outstanding = binding.etOutStanding.text.toString().toDouble()
        }
        customer.creditLimit = creditLimit
        customer.outstandingAmount = outstanding

        customer.mapLocationLat = leadGeoLocationLat
        customer.mapLocationLong = leadGeoLocationLong

        customer.geoAddress = completeAddress

        customer.deviceInformation = getDeviceInformation()
        customer.batteryPercent = getBatteryInformation().first
        customer.batteryOptimisation = isBatteryOptimizationEnabled()
        customer.locationPermission = isGpsEnabled()


        val updateStaffMappingModel = UpdateMappingModel()

        if (isAllStaffChecked) {
            updateStaffMappingModel.allowAll = true

            if (removeStaffSetList.size > 0) {
                val list = ArrayList<Int?>()
                removeStaffSetList.forEach {
                    list.add(it.id!!)
                }

                updateStaffMappingModel.removeSet = list
            } else {
                updateStaffMappingModel.removeSet = ArrayList()
            }

        } else if (isDeselectAllStaff) {
            updateStaffMappingModel.disallowAll = true
            updateStaffMappingModel.removeSet = ArrayList()
            if (addStaffSetList.size > 0) {
                val list = ArrayList<Int?>()
                addStaffSetList.forEach {
                    list.add(it.id!!)
                }

                updateStaffMappingModel.addSet = list
            } else {
                updateStaffMappingModel.addSet = ArrayList()
            }
        } else {
            if (isStaffSetUpdated) {
                updateStaffMappingModel.allowAll = false

                if (addStaffSetList.size > 0) {
                    val list = ArrayList<Int?>()
                    addStaffSetList.forEach {
                        list.add(it.id!!)
                    }

                    updateStaffMappingModel.addSet = list
                } else {
                    updateStaffMappingModel.addSet = ArrayList()
                }

                if (removeStaffSetList.size > 0) {
                    val list = ArrayList<Int?>()
                    removeStaffSetList.forEach {
                        list.add(it.id!!)
                    }

                    updateStaffMappingModel.removeSet = list
                } else {
                    updateStaffMappingModel.removeSet = ArrayList()
                }

            } else {
                updateStaffMappingModel.addSet = ArrayList()
                updateStaffMappingModel.removeSet = ArrayList()
            }
        }

        customer.selectStaff = updateStaffMappingModel

        val updateCategoryMappingModel = UpdateMappingModel()
        updateCategoryMappingModel.addSet = addCategoryList
        updateCategoryMappingModel.removeSet = removeCategoryList

        customer.selectCategory = updateCategoryMappingModel

        if (addBeatSet.isEmpty().not() || removeBeatSet.isEmpty().not()) {
            val updateBeatMappingModel = UpdateMappingModel()
            updateBeatMappingModel.addSet = addBeatSet
            updateBeatMappingModel.removeSet = removeBeatSet

            customer.selectBeat = updateBeatMappingModel
        }

        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            customer.lead = leadModel?.id
        }

        if (isUpdate) {
            if (hasInternetConnection() && intent.hasExtra(AppConstant.ANDROID_OFFLINE_TAG).not()) {
                customerViewModel.updateCustomer(
                        customer, intent.getIntExtra(AppConstant.CUSTOMER_ID, 0), hasInternetConnection()
                )
            } else {
                customerViewModel.updateCustomer(
                        customer, intent.getIntExtra(AppConstant.CUSTOMER_ID, 0), false
                )
            }
        } else {
            customer.geoLocationLat = currentGeoLocationLat
            customer.geoLocationLong = currentGeoLocationLong
            customer.geoAddress = currentGeoAddress
            customerViewModel.saveCustomer(customer, hasInternetConnection())
        }
    }

    override fun setCustomerSelect(checked: Boolean, model: NameAndIdSetInfoModel) {
        if (!isFirstTimeLoadCustomer) {
            isStaffSetUpdated = true
            val idModel = NameAndIdSetInfoModel()
            idModel.id = model.id
            idModel.name = model.name

            if (checked) {
                model.isSelected = true

                if (!isAllStaffChecked) {
                    addStaffSetList.add(idModel)
                }

                if (removeStaffSetList.size > 0) {
                    val index = removeStaffSetList.indexOfLast { it.id == model.id }
                    if (index != -1) {
                        removeStaffSetList.removeAt(index)
                    }
                }
            } else {
                onRemoveCustomer(idModel)
            }
        }
    }

    private fun onRemoveCustomer(model: NameAndIdSetInfoModel) {
        if (addStaffSetList.size > 0) {
            val index = addStaffSetList.indexOfLast { it.id == model.id }
            if (index != -1) {
                addStaffSetList.removeAt(index)
            }
        }

        removeStaffSetList.add(model)

        staffList.forEachIndexed { index, _ ->
            if (staffList[index].id == model.id) {
                staffList[index].isSelected = false
            }
        }

        staffListForAssignAdapter.notifyDataSetChanged()

    }

    override fun onCustomerSelect(model: CustomerData, position: Int) {
        binding.tvParentCustomerName.text = model.name
        customerParentId = model.id
        customerParentName = model.name
        binding.clCustomerList.visibility = View.GONE
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

    override fun setCategorySelect(checked: Boolean, model: AllCategoryResponseModel) {
        if (isFirstTimeLoadCategory.not()) {
            if (checked) {
                if (removeCategoryList.size > 0) {
                    val index = removeCategoryList.indexOfLast { it == model.id }
                    if (index != -1) {
                        removeCategoryList.removeAt(index)
                    } else {
                        addCategoryList.add(model.id)
                    }
                } else {
                    addCategoryList.add(model.id)
                }

            } else {
                if (addCategoryList.size > 0) {
                    val index = addCategoryList.indexOfLast { it == model.id }
                    if (index != -1) {
                        addCategoryList.removeAt(index)
                    } else {
                        removeCategoryList.add(model.id)
                    }
                } else {
                    removeCategoryList.add(model.id)
                }
            }
        }
    }

    override fun onCameraUpload(fileName: String?) {
        logoImagePath = fileName
        binding.ivCompanyImage.visibility = View.VISIBLE
        binding.ivCompanyImage.setImageURI(Uri.fromFile(File(fileName!!)))
    }

    override fun onGalleryUpload(fileName: String?) {
        logoImagePath = fileName
        binding.ivCompanyImage.visibility = View.VISIBLE
        binding.ivCompanyImage.setImageURI(Uri.fromFile(File(fileName!!)))
    }

    override fun onResume() {
        super.onResume()
        getUserCurrentLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
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