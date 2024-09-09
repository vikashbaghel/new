package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.lifecycle.lifecycleScope
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils
import com.app.rupyz.custom_view.basic.DateTimePicker
import com.app.rupyz.custom_view.basic.ImageSelector
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.CustomerFormActivityBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.UploadDataWithImageDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.enable
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.Sections
import com.app.rupyz.model_kt.UploadingActionModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CustomFormActivity : BaseActivity(),
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener,
    GeoFencingWarningDialogFragment.IGeoFencingActionListener {

    
    @Suppress("PrivatePropertyName")
    private val HIDE_PROGRESS_DELAY :Long = 3000
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
//    private var multiplePicCount = 0

//    private val pics: ArrayList<PicMapModel> = ArrayList()

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
    private var customerFollowUpData: CustomerFollowUpDataItem? = null
    private var customerFormSections: List<Sections?>? = null
    private var isEditDataSet = false
    private var activityId: Int = -1

    private val viewMap: HashMap<String, View> = hashMapOf()

    private var currentIndex = 0
    private val serverFileMap = hashMapOf<String, Pair<FormItemsItem, ArrayList<PicMapModel?>?>>()
    private val serverFileDataMap = hashMapOf<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>>()
    private var totalImages: Int = 0
    private var key: String = ""
    private var isPopupShown = false

    
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

        activityViewModel.getFollowUpList()
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
        
    }

    private fun initActivityType() {
        val customList = ArrayList<String>()
        feedbackList.forEach {
            customList.add(it.stringValue ?: "")
        }

        customerFollowUpData?.let { customerData ->
            if (customerData.feedbackType != null) {
                binding.etActivityType.setText(customerData.feedbackType)
                /*val index = feedbackList.indexOfFirst { it.stringValue == customerData.feedbackType }*/
            }
        }


        binding.etActivityType.addDelayedTextChangeListener{
            activitySelected = binding.etActivityType.text.toString()
            createCustomFormRequest(feedbackList.indexOfFirst { it.stringValue == binding.etActivityType.text.toString().trim() })
        }


        if (activityId != -1){
            createCustomFormRequestById(activityId)
        }else{
            activitySelected = binding.etActivityType.text.toString()
            createCustomFormRequest(feedbackList.indexOfFirst { it.stringValue == binding.etActivityType.text.toString().trim() })
        }

        if ((intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)).not()){
            if (intent.hasExtra(AppConstant.FEEDBACK_ID).not()){
                showFeedbackTypeSpinner()
            }else{
                binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }else{
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

    }
    
    private fun createCustomFormRequest(position : Int) {
        if (position != -1 && position < feedbackList.size){
            binding.formLayout.removeAllViews()
            val feedbackId = feedbackList[position].id
            activityViewModel.getCustomForInputList(feedbackId)
        }
    }
    
    private fun createCustomFormRequestById(feedbackId : Int) {
        val index = feedbackList.indexOfFirst { it.id == feedbackId }
        if (index >= 0){
            binding.etActivityType.setText(feedbackList[index].stringValue.toString())
            binding.formLayout.removeAllViews()
            /*activityViewModel.getCustomForInputList(feedbackId)*/
        }
    }
    
    private fun initLayout() {

        binding.progressBar.showView()

        headingParams.setMargins(50, 40, 16, 0)
        inputParams.setMargins(50, 30, 16, 0)

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }
        

        if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
            feedbackId = intent.getIntExtra(AppConstant.CUSTOMER_FEEDBACK, -1)
            activityViewModel.getCustomerFeedbackDetails(null, feedbackId)
            binding.tvToolbarTitle.text = resources.getString(R.string.update_activity)
            binding.btnAdd.text = resources.getString(R.string.update)
            uploadingActionModel.typeUploaded = true
            isUpdate = true
            binding.etActivityType.disable()
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            if (intent.hasExtra(AppConstant.FEEDBACK_ID)){
                binding.tvToolbarTitle.text = resources.getString(R.string.record_activity)
                activityId = intent.getIntExtra(AppConstant.FEEDBACK_ID,-1)
                binding.etActivityType.disable()
                binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }else{
                binding.etActivityType.isFocusable = false
                binding.etActivityType.isClickable = true
                binding.etActivityType.isCursorVisible = false
                showFeedbackTypeSpinner()
                binding.tvToolbarTitle.text = resources.getString(R.string.record_activity)
            }
        }
        
        if (intent.hasExtra(AppConstant.FEEDBACK_ID)) {
            activityId = intent.getIntExtra(AppConstant.FEEDBACK_ID,-1)
            /*createCustomFormRequestById(feedbackId)*/
            binding.etActivityType.disable()
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }else{
            binding.etActivityType.isFocusable = false
            binding.etActivityType.isClickable = true
            binding.etActivityType.isCursorVisible = false
            showFeedbackTypeSpinner()
            binding.tvToolbarTitle.text = resources.getString(R.string.record_activity)
        }
        
        
        addCustomerActivityModel = CustomerFollowUpDataItem(moduleId = customerId)

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                @Suppress("DEPRECATION")
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
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(AppConstant.LEAD)
            }
        }

        binding.btnAdd.setOnClickListener {
            if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
                if (isEditDataSet) {
                    validateData()
                }
            } else {
                validateData()
            }
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.ivClose.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun validateData() {
        Utils.hideKeyboard(this)
        CoroutineScope(Dispatchers.IO).launch {
            val nonValidFields = mutableListOf<String>()
            val validationMap = hashMapOf<String, Pair<String?, Boolean>>()
            customerFormSections?.forEach { section ->
                section?.formItems?.forEach { formItem ->
                    if (enumContains<com.app.rupyz.custom_view.type.FormItemType>(formItem?.type!!)) {
                        val formItemType = com.app.rupyz.custom_view.type.FormItemType.valueOf(formItem.type)
                        val validityCheckResult = CustomViewUtils.validateViews(
                            formItemType = formItemType,
                            data = formItem,
                            viewMap = viewMap,
                             0
                        )
                        if (validityCheckResult.values.firstOrNull { !it.second } != null) {
                            formItem.fieldProps?.label?.let { nonValidFields.add(it) }
                        }
                        validationMap.putAll(validityCheckResult)
                    }
                }
            }

            runOnUiThread {
                if (validationMap.values.firstOrNull { !it.second } == null) {
                    prepareDataForSave()
                } else {
                    val error = validationMap.values.firstOrNull { !it.second }
                    if (error?.first != null) {
                        showToast(error.first)
                    } else {
                        showToast(buildString {
                            append(resources.getString(R.string.fillMandateFields))
                            if (nonValidFields.isEmpty().not()) {
                                append(" ")
                                append(nonValidFields[0])
                            }
                        })
                    }
                }
            }
        }
    }

    private fun prepareDataForSave() {

        CoroutineScope(Dispatchers.IO).launch {
            val serverDatMap = hashMapOf<String, NameAndValueSetInfoModel?>()
            customerFormSections?.forEach { section ->
                section?.formItems?.forEach { formItem ->
                    if (enumContains<com.app.rupyz.custom_view.type.FormItemType>(formItem?.type!!)) {
                        val formItemType = com.app.rupyz.custom_view.type.FormItemType.valueOf(formItem.type)
                        if (formItemType == com.app.rupyz.custom_view.type.FormItemType.FILE_UPLOAD) {
                            serverFileDataMap.putAll(
                                CustomViewUtils.getFileDataToSendToServer(
                                    formItemType = formItemType,
                                    data = formItem,
                                    viewMap = viewMap
                                )
                            )
                        } else {
                            serverDatMap.putAll(
                                CustomViewUtils.getDataToSendToServer(
                                    formItemType = formItemType,
                                    data = formItem,
                                    viewMap = viewMap
                                )
                            )
                        }
                    }
                }
            }


            totalImages = serverFileDataMap.values.sumOf { value ->
                value.second?.filter { it?.onEditProduct == false }?.size ?: 0
            }

            serverFileDataMap.forEach { (mapKey, value) ->
                val oldPhotoList = value.second?.filter { it?.onEditProduct == true }
                if (oldPhotoList.isNullOrEmpty().not()) {
                    val photoList: ArrayList<PicMapModel?> = arrayListOf()
                    oldPhotoList?.forEach { imageModel ->
                        photoList.add(
                            PicMapModel(
                                id = imageModel?.imageId,
                                url = imageModel?.imagePath
                            )
                        )
                    }
                    serverFileMap[mapKey] = Pair(value.first, photoList)
                }
            }


            if (serverFileDataMap.values.isEmpty() || (serverFileMap.values.sumOf { it.second?.size ?: 0 }) >= totalImages) {
                if (supportFragmentManager.fragments.contains(uploadingFragment).not()){
                    uploadingActionModel.type = AppConstant.ACTIVITY
                    uploadingFragment.show(supportFragmentManager, UploadDataWithImageDialogFragment::class.java.name)
                }
               uploadActivity(serverDatMap)
            } else {
                if (serverFileDataMap.values.none { it.second.isNullOrEmpty().not() }.not()) {
                    uploadingActionModel.imageExist = true
                    uploadingActionModel.imageCount = totalImages
                    if (supportFragmentManager.fragments.contains(uploadingFragment).not()){
                        uploadingActionModel.type = AppConstant.ACTIVITY
                        uploadingFragment.show(supportFragmentManager, UploadDataWithImageDialogFragment::class.java.name)
                    }
                    currentIndex = 0
                    uploadFiles(serverFileDataMap, currentIndex)
                } else {
                    if (supportFragmentManager.fragments.contains(uploadingFragment).not()){
                        uploadingActionModel.type = AppConstant.ACTIVITY
                        uploadingFragment.show(supportFragmentManager, UploadDataWithImageDialogFragment::class.java.name)
                    }
                    uploadActivity(serverDatMap)
                }
            }
        }
    }

    private fun uploadFiles(serverFileDatMap: HashMap<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>>, index: Int) {

        uploadingActionModel.imageCount = totalImages - serverFileMap.values.sumOf { it.second?.size ?: 0 }
        uploadingActionModel.imageExist = true
        if (totalImages - serverFileMap.values.sumOf { it.second?.size ?: 0 } <= 0){
            uploadingActionModel.imageCount = 0
            uploadingActionModel.imageExist = false
        }
        Handler(Looper.getMainLooper()).postDelayed({
            uploadingFragment.setListener(uploadingActionModel)
        }, 300)

        if (index < serverFileDatMap.size) {
            key = serverFileDatMap.keys.elementAt(index)
            val photoList = serverFileDatMap[key]?.second
            if ((photoList?.size ?: 0) > 0) {
                for (i in 0 until photoList?.size!!) {
                    if (photoList[i]?.imagePath != null) {
                        if (photoList[i]?.onEditProduct!!.not()) {
                            lifecycleScope.launch {
                                val compressedImageFile = Compressor.compress(
                                    this@CustomFormActivity, File(photoList[i]!!.imagePath!!)
                                ) {
                                    quality(100)
                                    size(4_197_152)
                                }
                                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                            }
                        }
                    }
                }
            }
        } else {
            if ((serverFileMap.values.sumOf { it.second?.size ?: 0 }) >= totalImages){
                uploadingFragment.completeImageUploading()
                prepareDataForSave()
            }
        }
    }

    private fun uploadActivity(serverDatMap: HashMap<String, NameAndValueSetInfoModel?>) {
        uploadingActionModel.imageExist = false
        Handler(Looper.getMainLooper()).postDelayed({
            uploadingFragment.setListener(uploadingActionModel)
        }, 300)

        val mapParams: HashMap<String, NameAndValueSetInfoModel?> = hashMapOf()
        mapParams.putAll(serverDatMap)
        for ((key, value) in serverFileMap) {
            val imgIds: String = if (value.second?.size == 1) {
                value.second?.get(0)?.id.toString()
            } else {
                value.second?.mapNotNull { "${it?.id}" }?.toList()?.joinToString(",") ?: ""
            }
            val model = NameAndValueSetInfoModel(
                name = value.first.fieldProps?.name,
                label = value.first.fieldProps?.label,
                isRequired = value.first.fieldProps?.required,
                isCustom = value.first.isCustom,
                type = value.first.type,
                subModuleType = value.first.type,
                subModuleId = value.first.fieldProps?.name,
                value = imgIds
            )
            mapParams[key] = model
        }

        addCustomerActivityModel.feedbackType = activitySelected
        addCustomerActivityModel.moduleType = activityType
        addCustomerActivityModel.geoLocationLat = geoLocationLat
        addCustomerActivityModel.geoLocationLong = geoLocationLong
        addCustomerActivityModel.geoAddress = addressByLatLang
        addCustomerActivityModel.deviceInformation = getDeviceInformation()
        addCustomerActivityModel.batteryPercent = getBatteryInformation().first
        addCustomerActivityModel.batteryOptimisation = isBatteryOptimizationEnabled()
        addCustomerActivityModel.locationPermission = isGpsEnabled()

        addCustomerActivityModel.customFormData = ArrayList(mapParams.values.filterNotNull())
        log("$formItemModels")


        if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
            activityViewModel.updateCustomerFeedback(feedbackId, addCustomerActivityModel)
        } else {
            activityViewModel.addFeedbackFollowUp(addCustomerActivityModel, hasInternetConnection())
        }
    }

    private fun initObservers() {

        imageUploadViewModel.getCredLiveData().observe(this) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                genericResponseModel.data?.let { data ->

                    if (data.id != null) {
                        val picMapModel = PicMapModel(
                            id = data.id!!.toInt(), url = data.url
                        )
                        val list = (serverFileMap[key]?.second ?: arrayListOf())
                        list.add(picMapModel)
                        serverFileDataMap[key]?.let {
                            serverFileMap[key] = Pair(it.first, list)
                        }
                    }

                    if ((serverFileMap.values.sumOf { it.second?.size ?: 0 }) >= totalImages){
                        uploadingFragment.completeImageUploading()
                        prepareDataForSave()
                    }else{
                        currentIndex++
                        uploadFiles(serverFileDataMap, currentIndex)
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
                    feedbackList.add(CustomerFeedbackStringItem(id = 0,  stringValue = "Select Activity Type"))
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

        activityViewModel.getCustomFormFieldLiveData.observe(this) { response ->
            if (response.error == false) {
                response.data.let { schema ->
                    customerFormSections = schema?.sections
                    if (schema?.sections.isNullOrEmpty().not()) {
                        binding.formLayout.removeAllViews()
                        formItemModels.clear()
                        schema?.let { data ->
                            data.sections?.forEach { section ->
                                if (section?.formItems.isNullOrEmpty().not()
                                    && (section?.formItems?.filter
                                    { it?.status != AppConstant.VISIBLE }?.size == section?.formItems?.size).not()
                                ) {
                                    section?.formItems?.forEach { field ->
                                        if (enumContains<com.app.rupyz.custom_view.type.FormItemType>(field?.type!!)) {
                                            val formItemType = com.app.rupyz.custom_view.type.FormItemType.valueOf(field.type)
                                            viewMap.putAll(
                                                CustomViewUtils.createAndAddCustomView(
                                                    this,
                                                    formItemType,
                                                    field,
                                                    binding.formLayout,
                                                    customerId,
                                                    CustomerLevel.LEVEL_ONE
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        setConstrains()
                    } else {
                        binding.formLayout.removeAllViews()
                    }

                    if (customerFormSections.isNullOrEmpty().not()) {
                        customerFollowUpData?.let { csd ->
                            setFeedBackData(data = csd)
                        }
                    }

                    if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(
                            AppConstant.REMINDERS
                        )
                    ) {
                        /*TODO NOTHING*/
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({  binding.progressBar.hideView() },HIDE_PROGRESS_DELAY)
                    }


                }
            } else {
                if (intent.hasExtra(AppConstant.CUSTOMER_FEEDBACK) || intent.hasExtra(AppConstant.REMINDERS)) {
                    /*TODO NOTHING*/
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({  binding.progressBar.hideView() },HIDE_PROGRESS_DELAY)
                }
            }
        }

        activityViewModel.addFeedbackFollowUpLiveData.observe(this) {
            showToast("${it.message}")
            Log.e("tag", "${it.message}")
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
                Handler(Looper.getMainLooper()).postDelayed({  binding.progressBar.hideView() },HIDE_PROGRESS_DELAY)
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                } else {
                    showToast(it.message)
                }
            }
        }

    }

    private fun setConstrains() {
        viewMap.forEach { (_, view) ->
            if ((view is DateTimePicker)){
                view.setPastDatSelectionBard(true)
            }
        }
        viewMap.forEach { (_, view) ->
            if ((view is ImageSelector)){
                view.setAllowDeleteOnEdit(true)
            }
        }
    }

    private fun initData(data: CustomerFollowUpDataItem) {
        customerFollowUpData = data
        if (data.feedbackType != null) {
            val index = feedbackList.indexOfFirst { it.stringValue == data.feedbackType }
            if (index != -1){
                binding.etActivityType.setText(data.feedbackType)
            }
        }
        if (customerFormSections.isNullOrEmpty().not()) {
            setFeedBackData(data)
        }
    }

    private fun setFeedBackData(data: CustomerFollowUpDataItem) {

        if (customerFormSections.isNullOrEmpty().not()) {
            data.customFormData?.forEach { customData ->
                if (enumContains<com.app.rupyz.custom_view.type.FormItemType>(customData.type ?: "")) {
                    val formItemType = com.app.rupyz.custom_view.type.FormItemType.valueOf(customData.type ?: "")
                    CustomViewUtils.setEditDataInFields(
                        formItemType = formItemType,
                        data = NameAndValueSetInfoModel(
                            name = customData.name,
                            label = customData.label,
                            isRequired = customData.isRequired,
                            isCustom = customData.isCustom,
                            type = customData.type,
                            subModuleType = customData.subModuleType,
                            subModuleId = customData.subModuleId,
                            imgUrls = customData.imgUrls,
                            dataType = customData.dataType,
                            geoAddressValue = "",
                            geoLocationLat =  0.0,
                            geoLocationLong =  0.0,
                            value = customData.value,
                        ),
                        viewMap = viewMap,
                        beatList = arrayListOf(),
                        pricingGroupId = ""
                    )
                }
            }

            isEditDataSet = true
            if (binding.formLayout.childCount >= 1){
                Handler(Looper.getMainLooper()).postDelayed({  binding.progressBar.hideView() },HIDE_PROGRESS_DELAY)
                binding.btnAdd.enable()
            }
        }else {
            Handler(Looper.getMainLooper()).postDelayed({  binding.progressBar.hideView() },HIDE_PROGRESS_DELAY)
        }

        binding.btnAdd.enable()

    }

    private fun showFeedbackTypeSpinner(){
        binding.btnAdd.disable()
        binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
            null
        )
        val feedBackList  = feedbackList.map { it.stringValue }.toList()
        val listPopupWindow = ListPopupWindow(this, null, androidx.appcompat.R.attr.listPopupWindowStyle)
        listPopupWindow.anchorView = binding.etActivityTypeLayout
        val adapter = ArrayAdapter(this, R.layout.single_text_view_spinner_16dp_text, feedBackList)
        listPopupWindow.isModal = true
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
            if (position < feedBackList.size) {
                binding.etActivityType.setText(feedBackList[position])
            }

            if (position != 0){
                binding.btnAdd.enable()
            }else{
                binding.btnAdd.disable()
            }

            isPopupShown = false
            listPopupWindow.dismiss()
        }
        binding.etActivityType.setOnClickListener {
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_inverted_black, null),
                null
            )
            if (isPopupShown) {
                listPopupWindow.dismiss()
            } else {
                listPopupWindow.show()
            }
            isPopupShown = !isPopupShown
        }
        listPopupWindow.setOnDismissListener {
            binding.etActivityType.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
            isPopupShown = false
        }
        listPopupWindow.setSelection(0)
    }

/*************************************************************************************************************************************************
 *
 *  Location And Attendance Work
 *
 ***********************************************************************************************************************************************/

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
                if (isStaffUser && PermissionModel.INSTANCE.getPermission(
                        AppConstant.LOCATION_TRACKING,
                        false
                    )
                ) {
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
            if (myLocation != null) {
                if (Utils.isMockLocation(myLocation)) {
                    val fragment =
                        MockLocationDetectedDialogFragment.getInstance(this@CustomFormActivity)
                    fragment.isCancelable = false
                    fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                    )
                } else {
                    myLocation.let {
                        geoLocationLat = it.latitude
                        geoLocationLong = it.longitude
                        GeoLocationUtils.getAddress(
                            this@CustomFormActivity,
                            longitude = geoLocationLong,
                            latitude = geoLocationLat
                        ) { address ->
                            addressByLatLang = address
                        }

                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            if (isUpdate.not() && SharedPref.getInstance()
                                    .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                            ) {
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
            } else {
                runOnUiThread {
                    showToast(resources.getString(R.string.something_went_wrong_with_location))
                    binding.progressBar.visibility = View.GONE
                    binding.clBtnLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showStartDayDialog() {
        if (supportFragmentManager.fragments.firstOrNull {
                it.tag?.equals(
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                ) == true
            } == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
                this,
                geoLocationLat,
                geoLocationLong
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


    private fun registerGeofenceUpdates() {
        val isUserInsideGeoFencing =
            if (customerModel?.mapLocationLat != 0.0 && customerModel?.mapLocationLong != 0.0) {
                findUserIsInGeoFencingArea(
                    customerModel?.mapLocationLat
                        ?: leadModel?.mapLocationLat
                        ?: 0.0, customerModel?.mapLocationLong ?: leadModel?.mapLocationLong
                    ?: 0.0, geoLocationLat, geoLocationLong
                ).first
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
        getUserCurrentLocation()
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        binding.progressBar.visibility = View.GONE
        binding.clBtnLayout.visibility = View.VISIBLE
        onBackPressedDispatcher.onBackPressed()
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
        Handler(Looper.getMainLooper()).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onDismissDialogForGeoFencing() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onDismissDialogForStartDay() {
        setResult(RESULT_CANCELED)
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
 /***********************************************************************************************************************************************/
}
