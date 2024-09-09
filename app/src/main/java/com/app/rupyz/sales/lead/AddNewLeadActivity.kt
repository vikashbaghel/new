package com.app.rupyz.sales.lead

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewLeadBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.model_kt.GstInfoData
import com.app.rupyz.model_kt.LeadCategoryDataItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.PostOfficeItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomerGeoMapLocationActivity
import com.app.rupyz.sales.product.AddCategoryActivity
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
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class AddNewLeadActivity : BaseActivity(), LeadCategoryListForAddLeadAdapter.AllCategoryListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener, ImageUploadListener {
    private lateinit var binding: ActivityAddNewLeadBinding
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private val moreViewModel: MoreViewModel by viewModels()
    private var editLead: Boolean = false
    private var categoryList: ArrayList<LeadCategoryDataItem> = ArrayList()
    private lateinit var adapter: LeadCategoryListForAddLeadAdapter

    private var isPrimaryMobileNumberIsOnWhatsApp = false

    private var mFollowDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private val myCalendar = Calendar.getInstance()
    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]

    private var followDate = ""
    private var leadCategory = -1
    private var leadCategoryName = ""

    private lateinit var leadModel: LeadLisDataItem

    var delay: Long = 500 // 1 seconds after user stops typing

    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var currentGeoLocationLat: Double = 0.00
    private var currentGeoLocationLong: Double = 0.00
    private var currentGeoAddress: String? = null

    private var leadGeoLocationLat: Double = 0.00
    private var leadGeoLocationLong: Double = 0.00


    private var completeAddress: String? = ""

    private var logoImagePath: String? = null
    private var logoImageId: Int? = null
    private var prevS3LogoImageId: Int? = null

    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private var progressDialog: ProgressDialog? = null

    private var customerData = CustomerData()

    private val model = LeadLisDataItem()

    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewLeadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionUtils = LocationPermissionUtils(this, this)
        Utility(this)


        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("uploading ...")
        progressDialog!!.setCancelable(false)

        initRecyclerView()
        initObservers()
        loadLeadCategory()
        initLayout()
        getUserCurrentLocation()

        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            editLead = true
            leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)!!
            binding.tvToolbarTitle.text = resources.getString(R.string.edit_lead)
            if (leadModel.id != null) {
                leadViewModel.getLeadDetail(leadModel.id!!, hasInternetConnection())
            }

            binding.btnAdd.text = resources.getString(R.string.udpate_lead)
            binding.groupWhatsAppNotification.visibility = View.GONE
        } else {
            binding.groupWhatsAppNotification.visibility = View.VISIBLE
        }

        binding.etSearchCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    loadLeadCategory()
                }
            }
        })

        binding.etSearchCategory.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                categoryList.clear()
                loadLeadCategory()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchCategory.setText("")
            categoryList.clear()
            adapter.notifyDataSetChanged()
            loadLeadCategory()
        }

        binding.tvAddCategory.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this, AddCategoryActivity::class.java
                ).putExtra(AppConstant.LEAD_CATEGORY, true)
            )
        }
        binding.etPrimaryMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty() && p0.toString().length == 10) {
                    if (!editLead) {
                        leadViewModel.checkExistingLeadMobileNumber(p0.toString())
                    }
                }
            }
        })
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
                            this@AddNewLeadActivity,
                            longitude = currentGeoLocationLong,
                            latitude = currentGeoLocationLat
                        ) { address ->
                            currentGeoAddress = address
                        }
                    } else {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                        try {
                            if (isFinishing.not() && supportFragmentManager.isStateSaved.not()) {
                                val fragment =
                                    MockLocationDetectedDialogFragment.getInstance(this@AddNewLeadActivity)
                                fragment.isCancelable = false
                                fragment.show(
                                    supportFragmentManager,
                                    MockLocationDetectedDialogFragment::class.java.name
                                )
                            } else {

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
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

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            categoryList.clear()
            loadLeadCategory()
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadLeadCategory() {
        leadViewModel.getAllCategoryList(
            binding.etSearchCategory.text.toString(),
            hasInternetConnection()
        )
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            categoryList.clear()
            loadLeadCategory()

            adapter.notifyDataSetChanged()
        }
    }

    private fun initLayout() {

        if (PermissionModel.INSTANCE.getPermission(
                AppConstant.CREATE_LEAD_CATEGORY_PERMISSION,
                false
            )
                .not()
        ) {
            binding.tvAddCategory.visibility = View.GONE
        }

        binding.tvLeadCategory.setOnClickListener {
            if (binding.clLeadList.isVisible) {
                binding.clLeadList.visibility = View.GONE
            } else {
                binding.clLeadList.visibility = View.VISIBLE
            }
        }

        binding.mainContent.setOnClickListener {
            binding.clLeadList.visibility = View.GONE
        }

        binding.imgClose.setOnClickListener {
            finish()
        }

        binding.tvFetchGstDetails.setOnClickListener {
            if (binding.etGstInfo.text.toString().length == 15) {
                if (!intent.hasExtra(AppConstant.CUSTOMER_ID)) {
                    binding.progressBarGstVerified.visibility = View.VISIBLE
                    leadViewModel.getGstInfo(binding.etGstInfo.text.toString())
                    Utils.hideKeyboard(this)
                }
            } else {
                Toast.makeText(this, "Enter valid GST number!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etGstInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.ivGstVerified.visibility = View.GONE
                binding.tvFetchGstDetails.setBackgroundResource(R.drawable.add_product_green_btn_gredient)
                binding.tvFetchGstDetails.setTextColor(resources.getColor(R.color.white))
                binding.tvFetchGstDetails.isEnabled = true
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

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

            if (isFinishing.not() && isPaused.not() && hasFocus) {
                binding.spinnerState.showDropDown()
            }
        }

        binding.spinnerState.setOnClickListener {
            binding.spinnerState.showDropDown()
        }

        binding.ivClearStateName.setOnClickListener {
            binding.spinnerState.setText("")
            binding.ivClearStateName.visibility = View.GONE
            binding.ivDropDown.visibility = View.VISIBLE
        }

        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.hasFocus() && s.toString()
                        .isNotBlank() && s.toString().length == 6
                ) {
                    if (hasInternetConnection()) {
                        binding.pinCodeProgressBar.visibility = View.VISIBLE
                        moreViewModel.getPostalResponse(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.clPrimeWhatsapp.setOnClickListener {
            if (isPrimaryMobileNumberIsOnWhatsApp) {
                isPrimaryMobileNumberIsOnWhatsApp = false
                binding.ivWhatsAppOneCheck.setImageResource(R.drawable.ic_check_unselected)
            } else {
                isPrimaryMobileNumberIsOnWhatsApp = true
                binding.ivWhatsAppOneCheck.setImageResource(R.drawable.check)
            }
        }

        binding.clChangeLocation.setOnClickListener {
            startActivityForGeoLocation.launch(
                Intent(this, CustomerGeoMapLocationActivity::class.java).putExtra(
                    AppConstant.CUSTOMER, customerData
                )
            )
        }

        binding.tvRemoveLocation.setOnClickListener {
            leadGeoLocationLat = 0.0
            leadGeoLocationLong = 0.0

            customerData.mapLocationLat = leadGeoLocationLat
            customerData.mapLocationLong = leadGeoLocationLong
            binding.tvSetNewLocation.text = resources.getString(R.string.fetch_location)

            binding.tvGeoLocation.text = ""
            binding.groupMapView.visibility = View.GONE
        }


        binding.tvFollowDate.setOnClickListener {
            openFollowDateCalendar()
        }

        mFollowDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateStartDate()
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }

        binding.clImageUpload.setOnClickListener {
            val fragment = ImageUploadBottomSheetDialogFragment.newInstance(this)
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
            fragment.arguments = bundle
            fragment.show(supportFragmentManager, AppConstant.PROFILE_SLUG)
        }

        binding.btnCancel.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    private fun validateData() {
        when {
            leadCategory == -1 -> {
                showToast("Lead Category Required!")
            }

            binding.etBusinessName.text.trim().toString().isEmpty() -> {
                showToast("Business Name Required!")
            }

            logoImagePath.isNullOrBlank() && prevS3LogoImageId == null -> {
                showToast("Lead Profile Image Required!")
            }

            binding.etPrimaryMobileNumber.text.trim().toString().isEmpty() -> {
                showToast("Mobile Number Required!")
            }

            !Validations().isValidMobileNumber(
                binding.etPrimaryMobileNumber.text.trim().toString()
            ) -> {
                showToast("Valid Mobile Number Required!")
            }

            binding.etAddressLine1.text.trim().toString().isEmpty() -> {
                showToast("Address Line 1 Required!")
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
                    if (hasInternetConnection()) {
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                this@AddNewLeadActivity, File(logoImagePath)
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
                        model.logoImagePath = logoImagePath
                        updateInfo()
                    }
                } else {
                    updateInfo()
                }
            }
        }
    }

    private fun updateInfo() {
        model.businessName = binding.etBusinessName.text.trim().toString()
        model.leadCategory = leadCategory
        model.leadCategoryName = leadCategoryName
        model.addressLine1 = binding.etAddressLine1.text.toString()
        model.city = binding.etCity.text.toString()
        model.state = binding.spinnerState.text.toString()
        model.pincode = binding.etPinCode.text.toString()
        model.mobile = binding.etPrimaryMobileNumber.text.toString()
        model.contactPersonName = binding.etContactPerson.text.toString()
        model.designation = binding.etDesignation.text.toString()
        model.email = binding.etEmailId.text.toString()
        model.follow_update = followDate
        model.comments = binding.etComment.text.toString()
        model.gstin = binding.etGstInfo.text.toString()

        if (logoImageId != null) {
            model.imageLogo = logoImageId
        }


        Log.i("TAG", "updateInfo: $leadGeoLocationLat  :  $leadGeoLocationLong ")
        Log.i("TAG", "updateInfo: $completeAddress ")


        model.mapLocationLat = leadGeoLocationLat
        model.mapLocationLong = leadGeoLocationLong

        model.geoAddress = completeAddress

        model.deviceInformation = getDeviceInformation()
        model.batteryPercent = getBatteryInformation().first
        model.batteryOptimisation = isBatteryOptimizationEnabled()
        model.locationPermission = isGpsEnabled()


        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            leadViewModel.updateLead(model, leadModel.id!!, hasInternetConnection())
        } else {
            model.isDetailsSendToUser = binding.chbWhatsapp.isChecked
            model.geoLocationLat = currentGeoLocationLat
            model.geoLocationLong = currentGeoLocationLong
            model.activityGeoAddress = currentGeoAddress
            Log.i("TAG", "updateInfo: $currentGeoLocationLat  :  $currentGeoLocationLong ")
            Log.i("TAG", "updateInfo: $currentGeoAddress ")

            leadViewModel.addNewLead(model, hasInternetConnection())
        }
    }

    private fun openFollowDateCalendar() {
        val dialog = DatePickerDialog(
            this,
            android.R.style.ThemeOverlay_Material_Dialog,
            mFollowDateSetListener,
            year,
            month,
            day
        )
        dialog.updateDate(year, month, day)
        dialog.show()
    }


    private fun updateStartDate() {
        followDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvFollowDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
    }

    private fun initRecyclerView() {
        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.layoutManager = LinearLayoutManager(this)
        adapter = LeadCategoryListForAddLeadAdapter(categoryList, this)
        binding.rvCategoryList.adapter = adapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
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

        leadViewModel.leadCategoryLiveData.observe(this) {
            it.data?.let { list ->
                categoryList.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }

        leadViewModel.gstInfoLiveData.observe(this) {
            binding.progressBarGstVerified.visibility = View.GONE
            if (it.error == false) {
                binding.ivGstVerified.visibility = View.VISIBLE
                binding.tvFetchGstDetails.setBackgroundResource(R.drawable.check_score_button_style_disable)
                binding.tvFetchGstDetails.setTextColor(resources.getColor(R.color.black))
                binding.tvFetchGstDetails.isEnabled = false
                initGstData(it.data)
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        leadViewModel.addLeadLiveData.observe(this) {
            progressDialog?.dismiss()
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                val intent = Intent()
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

        leadViewModel.leadDetailLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { model ->

                    if (!model.leadCategoryName.isNullOrEmpty()) {
                        binding.tvLeadCategory.text = model.leadCategoryName?.replaceFirstChar(
                            Char::titlecase
                        )
                        leadCategory = model.leadCategory!!
                        leadCategoryName = model.leadCategoryName ?: ""
                    }
                    if (!model.contactPersonName.isNullOrEmpty()) {
                        binding.etContactPerson.setText(
                            model.contactPersonName?.replaceFirstChar(
                                Char::titlecase
                            )
                        )
                    }
                    if (!model.businessName.isNullOrEmpty()) {
                        binding.etBusinessName.setText(
                            model.businessName?.replaceFirstChar(
                                Char::titlecase
                            )
                        )
                    }
                    if (!model.gstin.isNullOrEmpty()) {
                        binding.etGstInfo.setText(model.gstin)
                    }
                    if (!model.designation.isNullOrEmpty()) {
                        binding.etDesignation.setText(
                            model.designation?.replaceFirstChar(
                                Char::titlecase
                            )
                        )
                    }
                    if (!model.mobile.isNullOrEmpty()) {
                        binding.etPrimaryMobileNumber.setText(model.mobile)
                    }

                    if (!model.email.isNullOrEmpty()) {
                        binding.etEmailId.setText(model.email)
                    }

                    if (hasInternetConnection()) {
                        if (model.logoImageUrl.isNullOrEmpty().not()) {
                            binding.ivCompanyImage.visibility = View.VISIBLE
                            ImageUtils.loadImage(model.logoImageUrl, binding.ivCompanyImage)

                            if (model.imageLogo != null) {
                                prevS3LogoImageId = model.imageLogo
                            }
                        }
                    } else if (model.logoImagePath.isNullOrEmpty().not()) {
                        logoImagePath = model.logoImagePath
                        binding.ivCompanyImage.visibility = View.VISIBLE
                        binding.ivCompanyImage.setImageURI(Uri.fromFile(File(model.logoImagePath!!)))
                    }

                    if (!model.addressLine1.isNullOrEmpty()) {
                        binding.etAddressLine1.setText(
                            model.addressLine1?.replaceFirstChar(
                                Char::titlecase
                            )
                        )
                    }
                    if (!model.city.isNullOrEmpty()) {
                        binding.etCity.setText(
                            model.city?.replaceFirstChar(
                                Char::titlecase
                            )
                        )
                    }
                    if (model.state != null) {
                        binding.spinnerState.setText(model.state)
                        binding.ivClearStateName.visibility = View.VISIBLE
                        binding.ivDropDown.visibility = View.GONE
                    }

                    if (!model.pincode.isNullOrEmpty()) {
                        binding.etPinCode.setText(model.pincode)
                    }

                    if (model.mapLocationLat != null && model.mapLocationLat != 0.0
                        && model.mapLocationLong != null && model.mapLocationLong != 0.0
                    ) {
                        setMapLocation(
                            LatLng(model.mapLocationLat!!, model.mapLocationLong!!),
                            true
                        )

                        leadGeoLocationLat = model.mapLocationLat ?: 0.0
                        leadGeoLocationLong = model.mapLocationLong ?: 0.0

                        binding.tvSetNewLocation.text =
                            resources.getString(R.string.update_location)

                        if (model.geoAddress.isNullOrEmpty().not()) {
                            completeAddress = model.geoAddress
                            binding.groupMapView.visibility = View.VISIBLE
                            binding.tvGeoLocation.text = model.geoAddress
                        }
                    }

                    if (!model.follow_update.isNullOrEmpty()) {
                        binding.tvFollowDate.text =
                            DateFormatHelper.getMonthDate(model.follow_update)
                        followDate = model.follow_update!!
                    }
                    if (!model.comments.isNullOrEmpty()) {
                        binding.etComment.setText(model.comments)
                    }
                }
            }
        }

        leadViewModel.mobileCheckLiveData.observe(this) {
            if (it.error == false && it.data?.isExists != null) {
                if (it.data?.isExists == true) {
                    Utils.hideKeyboard(this)
                    showMobileCheckDialog()
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

    private fun autoFillPostalOffice(postOfficeItem: PostOfficeItem) {
        binding.etCity.setText(postOfficeItem.district)
        if (postOfficeItem.state.isNullOrEmpty().not()) {
            binding.spinnerState.setText(postOfficeItem.state)
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }
    }

    private fun showMobileCheckDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.lead_exist)
        tvTitle.text = resources.getString(R.string.lead_already_exist)

        ivClose.visibility = View.GONE
        tvDelete.text = resources.getString(R.string.proceed_with_same_number)
        tvDelete.isAllCaps = false

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener {
            binding.etPrimaryMobileNumber.setText("")
            dialog.dismiss()
        }

        tvDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initGstData(data: GstInfoData?) {

        if (!data?.legalName.equals(null)) {
            binding.etBusinessName.setText(data?.legalName.toString())
        }
        if (!data?.addressLine1.equals(null)) {
            binding.etAddressLine1.setText(data?.addressLine1.toString())
        }
        if (!data?.city.equals(null)) {
            binding.etCity.setText(data?.city.toString())
        }
        if (!data?.pincode.equals(null)) {
            binding.etPinCode.setText(data?.pincode.toString())
        }

        if (data?.state != null) {
            binding.spinnerState.setText(data.state)
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }
    }

    override fun onSelect(model: LeadCategoryDataItem, position: Int) {
        binding.clLeadList.visibility = View.GONE
        binding.tvLeadCategory.text = model.name
        leadCategory = model.id ?: -1
        leadCategoryName = model.name ?: ""
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


    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        onBackPressedDispatcher.onBackPressed()
    }


    override fun onResume() {
        super.onResume()
        isPaused = false
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }
    override fun onPostResume() {
        super.onPostResume()
        // Now it's safe to show the dropdown if needed
        if (binding.spinnerState.hasFocus()){
            binding.spinnerState.showDropDown()
        }
    }
}