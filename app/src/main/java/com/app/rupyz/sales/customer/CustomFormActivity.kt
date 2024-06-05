package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomerFormActivityBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.UploadDataWithImageDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.UploadingActionModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customforms.FormBinding
import com.app.rupyz.sales.customforms.FormItemHandlerFactory
import com.app.rupyz.sales.customforms.FormItemType
import com.app.rupyz.sales.customforms.isValidateInput
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class CustomFormActivity : BaseActivity(),
        MockLocationDetectedDialogFragment.IMockLocationActionListener,
        LocationPermissionUtils.ILocationPermissionListener,
        MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener,
        GeoFencingWarningDialogFragment.IGeoFencingActionListener {
    private lateinit var binding: CustomerFormActivityBinding

    private val activityViewModel: StaffActivityViewModel by viewModels()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private var locationManager: LocationManager? = null

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var addressByLatLang: String? = ""

    private var customerModel: CustomerData? = null

    private var leadModel: LeadLisDataItem? = null

    private var isUpdate = false
    private val feedbackList: ArrayList<CustomerFeedbackStringItem> = ArrayList()

    private var feedbackId: Int = -1
    private var multiplePicCount = 0

    private val pics: ArrayList<PicMapModel> = ArrayList()

    private var activitySelected: String = ""

    private var customerId: Int = -1
    private var activityType: String = ""

    private lateinit var addCustomerActivityModel: CustomerFollowUpDataItem

    // Initialize a list to hold the form item models
    private val formItemModels = ArrayList<NameAndValueSetInfoModel>()

    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null

    private val headingParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private val uploadingFragment = UploadDataWithImageDialogFragment()
    private var uploadingActionModel = UploadingActionModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomerFormActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionUtils = LocationPermissionUtils(this, this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        getUserCurrentLocation()
        initLayout()
        initObservers()

        activityViewModel.getOfflineFollowUpList()
    }

    private fun initActivityType() {
        val customList = ArrayList<String>()
        feedbackList.forEach {
            customList.add(it.stringValue ?: "")
        }
        binding.spinnerActivity.adapter = ArrayAdapter(
                this, R.layout.single_text_view_spinner_16dp_text,
                customList
        )

        binding.spinnerActivity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        if (position != 0) {
                            activitySelected = binding.spinnerActivity.selectedItem.toString()

                            if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK).not()
                                    || intent.hasExtra(AppConstant.REMINDERS).not()) {
                                binding.formLayout.removeAllViews()
                                val feedbackId = feedbackList[position].id
                                activityViewModel.getCustomForInputList(feedbackId)
                            }
                        } else {
                            activitySelected = ""
                        }
                    }
                }
    }

    private fun initLayout() {

        headingParams.setMargins(50, 40, 16, 0)
        inputParams.setMargins(50, 30, 16, 0)

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }

//        if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
//            feedbackId = intent.getIntExtra(AppConstant.CUSTOMER_FEEDBACK, -1)
//            activityViewModel.getCustomerFeedbackDetails(null, feedbackId)
//
//            binding.btnAdd.text = resources.getString(R.string.update)
//
//            isUpdate = true
//        }

        addCustomerActivityModel = CustomerFollowUpDataItem(moduleId = customerId)

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }

        if (intent.hasExtra(AppConstant.ACTIVITY_TYPE)) {
            activityType = intent.getStringExtra(AppConstant.ACTIVITY_TYPE)!!
        }

        if (intent.hasExtra(AppConstant.LEAD)) {
            leadModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.LEAD, LeadLisDataItem::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.LEAD)
            }
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun validateData() {
        var isValidate = true
        log("$formItemModels")
        formItemModels.forEach { formItemModel ->
            if (formItemModel.isRequired == true) {
                if (formItemModel.value.isNullOrEmpty()) {
                    showToast(resources.getString(R.string.please_provide_all_mandatory_data, formItemModel.label))
                    isValidate = false
                    return@forEach
                } else if (formItemModel.isValidateInput(this).first.not()) {
                    showToast(formItemModel.isValidateInput(this).second)
                    isValidate = false
                    return@forEach
                }
            } else if (formItemModel.value.isNullOrEmpty().not()) {
                if (formItemModel.isValidateInput(this).first.not()) {
                    showToast(formItemModel.isValidateInput(this).second)
                    isValidate = false
                    return@forEach
                }
            }
        }


        if (isValidate) {
            uploadingFragment.show(supportFragmentManager,
                    UploadDataWithImageDialogFragment::class.java.name)

            uploadingActionModel.type = AppConstant.ACTIVITY

            val fileUploadExistsWithValue = formItemModels.any {
                it.type == FormItemType.FILE_UPLOAD.name
                        && it.value.isNullOrEmpty().not()
            }

            if (fileUploadExistsWithValue) {
                val imageList = formItemModels.last { it.type == FormItemType.FILE_UPLOAD.name }
                addActivityImage(imageList.value)
            } else {
                uploadingActionModel.imageExist = false
                Handler(Looper.myLooper()!!).postDelayed({
                    uploadingFragment.setListener(uploadingActionModel)
                }, 300)

                uploadActivity()
            }
        }
    }


    private fun uploadActivity() {
        addCustomerActivityModel.feedbackType = activitySelected
        addCustomerActivityModel.moduleType = activityType
        addCustomerActivityModel.geoLocationLat = geoLocationLat
        addCustomerActivityModel.geoLocationLong = geoLocationLong
        addCustomerActivityModel.geoAddress = addressByLatLang

        addCustomerActivityModel.deviceInformation = getDeviceInformation()
        addCustomerActivityModel.batteryPercent = getBatteryInformation().first
        addCustomerActivityModel.batteryOptimisation = isBatteryOptimizationEnabled()
        addCustomerActivityModel.locationPermission = isGpsEnabled()

        addCustomerActivityModel.customFormData = formItemModels
        log("$formItemModels")

        if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
            activityViewModel.updateCustomerFeedback(feedbackId, addCustomerActivityModel)
        } else {
            activityViewModel.addFeedbackFollowUp(addCustomerActivityModel, hasInternetConnection())
        }
    }

    private fun addActivityImage(value: String?) {

        if (Connectivity.hasInternetConnection(this)) {
            val photoModelList = ArrayList<String>()

            if (value!!.contains(",")) {
                photoModelList.addAll(value.split(","))
            } else {
                photoModelList.add(value)
            }

            if (photoModelList.isNotEmpty()) {
                uploadingActionModel.imageExist = true
                uploadingActionModel.imageCount = photoModelList.size
            }

            Handler(Looper.myLooper()!!).postDelayed({
                uploadingFragment.setListener(uploadingActionModel)
            }, 300)

            multiplePicCount = photoModelList.size

            for (i in photoModelList.indices) {
                if (photoModelList[i].isNotEmpty()) {
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                                this@CustomFormActivity,
                                File(photoModelList[i])
                        ) {
                            quality(30)
                            resolution(512, 512)
                            size(597_152)
                        }
                        imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                    }
                }
            }
        }
    }

    private fun initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                genericResponseModel.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (multiplePicCount == pics.size) {

                            val stringBuilder = StringBuilder()
                            pics.forEachIndexed { index, opt ->
                                stringBuilder.append(opt.id)
                                if (index != pics.size - 1) {
                                    stringBuilder.append(",")
                                }
                            }

                            val existingModel = formItemModels.find { it.type == FormItemType.FILE_UPLOAD.name }
                            if (existingModel != null) {
                                // Update the existing model's value
                                existingModel.value = stringBuilder.toString()
                            }

                            uploadingFragment.completeImageUploading()
                            uploadActivity()
                        }
                    }
                }
            } else {
                uploadingFragment.dismissOnError()
                showToast("${genericResponseModel.message}")
            }
        }

        activityViewModel.getFollowUpListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    feedbackList.add(CustomerFeedbackStringItem(0, "Select Activity Type"))
                    feedbackList.addAll(list)
                    initActivityType()
                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                } else {
                    showToast(it.message)
                }
            }
        }

        activityViewModel.getCustomFormFieldLiveData.observe(this) {
            if (it.error == false) {
                it.data.let { schema ->

                    if (schema?.sections.isNullOrEmpty().not()) {
                        binding.formLayout.removeAllViews()
                        formItemModels.clear()

                        schema?.sections!![0]?.formItems?.forEach { formItem ->

                            if (formItem?.status == AppConstant.VISIBLE) {
                                val model = NameAndValueSetInfoModel()
                                model.name = formItem.fieldProps?.name
                                model.label = formItem.fieldProps?.label
                                model.isRequired = formItem.fieldProps?.required
                                model.isCustom = formItem.isCustom
                                model.type = formItem.type
                                model.subModuleType = formItem.type
                                model.subModuleId = formItem.fieldProps?.name

                                formItemModels.add(model)

                                val textView = TextView(this)
                                textView.setTextSize(TypedValue.TYPE_NULL, resources.getDimension(R.dimen.size_14sp))
                                textView.setTextColor(resources.getColor(R.color.black))
                                // Load the font from resources and set it to the TextView
                                val typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
                                textView.typeface = typeface
                                textView.layoutParams = headingParams

                                formItem.fieldProps.let { prop ->
                                    if (prop?.required == true) {
                                        val formattedText = SpannableString(
                                                resources.getString(R.string.custom_forms_mandatory_heading, prop.label))
                                        formattedText.setSpan(ForegroundColorSpan(Color.RED),
                                                formattedText.length - 1,
                                                formattedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        textView.text = formattedText
                                    } else {
                                        textView.text = resources.getString(R.string.custom_forms_heading,
                                                prop?.label)
                                    }
                                }

                                binding.formLayout.addView(textView)

                                val formItemType = FormItemType.valueOf(formItem.type!!)
                                // Assuming form item type string matches enum name
                                val handler = FormItemHandlerFactory.getFormCreationHandler(formItemType)
                                handler.handleCreationFormItem(this, formItem,
                                        FormBinding(binding.formLayout),
                                        formItemModels, supportFragmentManager)
                            }
                        }
                    } else {
                        binding.formLayout.removeAllViews()

                    }
                }
            }
        }


        activityViewModel.addFeedbackFollowUpLiveData.observe(this) {
            showToast("${it.message}")
            if (hasInternetConnection()) {
                uploadingFragment.completeApiUploading()
            }

            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                }
            }
        }

        activityViewModel.getFeedbackDetailLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    initData(data)
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

    private fun initData(data: CustomerFollowUpDataItem) {
        if (data.feedbackType != null) {
            val index = feedbackList.indexOfFirst { it.stringValue == data.feedbackType }
            binding.spinnerActivity.setSelection(index)
        }

        binding.formLayout.removeAllViews()
        formItemModels.clear()

        data.customFormData?.forEach { formItem ->

            formItemModels.add(formItem)

            val textView = TextView(this)
            textView.setTextSize(TypedValue.TYPE_NULL, resources.getDimension(R.dimen.size_14sp))
            textView.setTextColor(resources.getColor(R.color.black))
            // Load the font from resources and set it to the TextView
            val typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
            textView.typeface = typeface
            textView.layoutParams = headingParams

            formItem.let { prop ->
                if (prop.isRequired == true) {
                    val formattedText = SpannableString(
                            resources.getString(R.string.custom_forms_mandatory_heading, prop.label))
                    formattedText.setSpan(ForegroundColorSpan(Color.RED),
                            formattedText.length - 1,
                            formattedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    textView.text = formattedText
                } else {
                    textView.text = resources.getString(R.string.custom_forms_heading,
                            prop?.label)
                }
            }

            binding.formLayout.addView(textView)

            val formItemType = FormItemType.valueOf(formItem.type!!)
            // Assuming form item type string matches enum name

//            val handler = FormItemHandlerFactory.getFormCreationHandler(formItemType)
//            handler.handleCreationFormItem(this, formItem,
//                    FormBinding(binding.formLayout),
//                    formItemModels, supportFragmentManager)

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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getUserCurrentLocation()
        } else {
            if (isStaffUser && PermissionModel.INSTANCE.getPermission(AppConstant.LOCATION_TRACKING, false)) {
                getUserCurrentLocation()
            } else {
                binding.progressBar.visibility = View.GONE
                binding.clBtnLayout.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val myLocation = MyLocation()
        myLocation.getLocation(this, locationResult)
    }

    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {

            if (Utils.isMockLocation(myLocation)) {
                val fragment = MockLocationDetectedDialogFragment.getInstance(this@CustomFormActivity)
                fragment.isCancelable = false
                fragment.show(supportFragmentManager, MockLocationDetectedDialogFragment::class.java.name)
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    addressByLatLang = GeoLocationUtils.getAddress(this@CustomFormActivity,
                            longitude = geoLocationLong, latitude = geoLocationLat)

                    if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                        if (isUpdate.not() && SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
                            registerGeofenceUpdates()
                        } else {
                            runOnUiThread {
                                binding.progressBar.visibility = View.GONE
                                binding.clBtnLayout.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        showStartDayDialog()
                    }
                }
            }
        }
    }

    private fun showStartDayDialog() {
        if (supportFragmentManager.fragments.firstOrNull { it.tag?.equals(MarkAttendanceBottomSheetDialogFragment::class.java.name) == true } == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
            fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
            }
        }
    }


    private fun registerGeofenceUpdates() {
        val isUserInsideGeoFencing = if (customerModel?.mapLocationLat != 0.0 && customerModel?.mapLocationLong != 0.0) {
            findUserIsInGeoFencingArea(customerModel?.mapLocationLat
                    ?: leadModel?.mapLocationLat
                    ?: 0.0, customerModel?.mapLocationLong ?: leadModel?.mapLocationLong
            ?: 0.0, geoLocationLat, geoLocationLong).first
        } else {
            true
        }

        if (isUserInsideGeoFencing) {
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.clBtnLayout.visibility = View.VISIBLE
                enableTouch()
            }
        } else {
            val fragment = GeoFencingWarningDialogFragment.getInstance(this)
            fragment.show(supportFragmentManager, GeoFencingWarningDialogFragment::class.java.name)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            binding.progressBar.visibility = View.GONE
            binding.clBtnLayout.visibility = View.VISIBLE
        }
        locationPermissionUtils.setActivityResult(resultCode, requestCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
        getUserCurrentLocation()
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        binding.progressBar.visibility = View.GONE
        binding.clBtnLayout.visibility = View.VISIBLE
        onBackPressed()
    }

    override fun onPermissionsGiven() {
        super.onPermissionsGiven()
        getUserCurrentLocation()
    }

    override fun onPermissionsDenied() {
        super.onPermissionsDenied()
        getUserCurrentLocation()
    }

    override fun onGpsEnabled() {
        super.onGpsEnabled()
        Handler(Looper.myLooper()!!).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onDismissDialogForGeoFencing() {
        finish()
    }

    override fun onDismissDialogForStartDay() {
        finish()
    }

    override fun onSuccessfullyMarkAttendance() {
        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
            registerGeofenceUpdates()
        } else {
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.clBtnLayout.visibility = View.VISIBLE
            }
        }
    }

}
