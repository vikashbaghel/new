package com.app.rupyz.sales.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.special.AssignBeatDropDown
import com.app.rupyz.custom_view.special.AssignPricingGroupDropDown
import com.app.rupyz.custom_view.special.CustomerLevelAndType
import com.app.rupyz.custom_view.special.CustomerMapAddressPicker
import com.app.rupyz.custom_view.special.PinCodeEditor
import com.app.rupyz.custom_view.special.StateDropDown
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ActivityNewAddCustomerBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddNewCustomerFormDataModel
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.NewUpdateCustomerInfoModel
import com.app.rupyz.model_kt.Sections
import com.app.rupyz.sales.customer.adapters.AddCustomerStepsAdapter
import com.app.rupyz.sales.customer.adapters.AddNewCustomerTabPagerAdapter
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class NewAddCustomerActivity : BaseActivity(), LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener {

    private lateinit var binding: ActivityNewAddCustomerBinding
    private val viewModel: CustomerViewModel by viewModels<CustomerViewModel>()
    private val sharedCustomerViewModel: SharedCustomerViewModel by viewModels<SharedCustomerViewModel>()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels<ImageUploadViewModel>()

    private lateinit var stepsAdapter: AddCustomerStepsAdapter
    private lateinit var vpStepPagerAdapter: AddNewCustomerTabPagerAdapter
    private lateinit var steps: MutableList<Sections?>
    private var leadModel: LeadLisDataItem? = null

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentGeoLocationLat: Double = 0.00
    private var currentGeoLocationLong: Double = 0.00
    private var currentGeoAddress: String? = null
    
    private var currentLocationPinCode: String? = null
    private var currentLocationCity: String? = null
    private var currentLocationState: String? = null
    private var currentLocationTerritory: String? = null


    private var currentIndex = 0
    private val serverFileMap = hashMapOf<String, Pair<FormItemsItem, ArrayList<PicMapModel?>?>>()
    private val serverFileDataMap =
        hashMapOf<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>>()
    private var totalImages: Int = 0
    private var key: String = ""
    private var businessName = ""
    private var pageFetched = 0
    private var customerId = 0
    private var editCustomerData: NewUpdateCustomerInfoModel.Data? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(AppConstant.CUSTOMER_ID) && intent.getIntExtra(
                AppConstant.CUSTOMER_ID,
                0
            ) != 0
        ) {
            customerId = (intent.getIntExtra(AppConstant.CUSTOMER_ID, 0))
            binding.progressBar.showView()
            if (hasInternetConnection() && intent.hasExtra(AppConstant.ANDROID_OFFLINE_TAG)) {
                viewModel.getCustomerByIdNew(intent.getIntExtra(AppConstant.CUSTOMER_ID, 0), false)
            } else {
                viewModel.getCustomerByIdNew(
                    intent.getIntExtra(AppConstant.CUSTOMER_ID, 0),
                    hasInternetConnection()
                )
            }
            binding.tvToolbarTitle.text = resources.getString(R.string.edit_customer)
        } else {
            if (pageFetched >= 3) {
                if (intent.hasExtra(AppConstant.LEAD_INFO)) {
                    leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)
                    initLeadData(leadModel)
                }
            }
            binding.tvToolbarTitle.text = resources.getString(R.string.add_customer)
        }

        locationPermissionUtils = LocationPermissionUtils(this, this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        initObservers()
        initAdapters()
        setListener()
        getUserCurrentLocation()


    }


    private fun initAdapters() {
        steps = getStepsList()
        vpStepPagerAdapter =
            AddNewCustomerTabPagerAdapter(customerId, steps, supportFragmentManager, lifecycle)
        stepsAdapter =
            AddCustomerStepsAdapter(Resources.getSystem().displayMetrics.widthPixels) { step, _ ->
                goToNextPage(step)
            }
        binding.apply {
            vpStepsPager.adapter = vpStepPagerAdapter
            rvSteps.adapter = stepsAdapter
            stepsAdapter.setSteps(steps)
            rvSteps.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    // Set all offsets to 0
                    outRect[0, 0, 0] = 0
                }
            })
            vpStepsPager.offscreenPageLimit = 3
            vpStepsPager.isUserInputEnabled = false
        }
    }

    private fun setListener() {

        onBackPressedDispatcher.addCallback(
            this@NewAddCustomerActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.vpStepsPager.currentItem == 0) {
                        finish()
                    } else {
                        binding.btnBack.performClick()
                    }
                }
            })

        binding.apply {
            imgClose.setOnClickListener {
                if (binding.vpStepsPager.currentItem == 0) {
                    finish()
                } else {
                    binding.btnBack.performClick()
                }
            }
        }

        binding.vpStepsPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                stepsAdapter.setCurrentStep((position + 1))
                changeButtons(position)
                if (position == 1){
                    setAddress()
                }
            }
        })

        binding.btnBack.setOnClickListener {
            if (stepsAdapter.getCurrentStep() - 2 >= 0) {
                binding.vpStepsPager.currentItem = ((stepsAdapter.getCurrentStep() - 2))
            }
        }

        binding.btnNext.setOnClickListener {
            goToNextPage()
        }

        binding.btnSave.setOnClickListener {
            prepareDataForSave()
        }
    }

    private fun initObservers() {

        viewModel.addCustomerLiveData().observe(this) { data ->
            binding.progressBar.hideView()
            data?.let {
                if (data.error == false) {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(AppConstant.CUSTOMER_NAME, businessName)
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

        viewModel.updateCustomerNewLiveData().observe(this) { data ->
            binding.progressBar.hideView()
            data?.let {
                if (data.error == false) {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(AppConstant.CUSTOMER_NAME, businessName)
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

        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            binding.progressBar.visibility = View.GONE
            if (model.error == false) {
                model.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel(
                            id = data.id!!.toInt(), url = data.url
                        )
                        val list = (serverFileMap[key]?.second ?: arrayListOf())
                        list.add(picMapModel)
                        serverFileDataMap[key]?.let {
                            serverFileMap[key] = Pair(it.first, list)
                        }
                        if (list.size == serverFileDataMap[key]?.second?.size) {
                            currentIndex++
                            uploadFiles(serverFileDataMap, currentIndex, totalImages)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "" + model.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getCustomerByIdDataNew().observe(this) {
            if (it.error == false) {
                editCustomerData = it.data
                if (pageFetched >= 3) {
                    setEditData()
                }

            }
        }

        sharedCustomerViewModel.getPageSyncCount().observe(this) {
            if (it >= 3) {
                pageFetched = it
                if (editCustomerData != null) {
                    setEditData()
                }
                if (leadModel != null || intent.hasExtra(AppConstant.LEAD_INFO)) {
                    leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)
                    initLeadData(leadModel)
                }
            }
            
        }

    }

    private fun setEditData() {
        val allViewMap = getAllViewsFromSharedModel()
        editCustomerData?.let { dataSet ->
            if (dataSet.customFormData.isEmpty()) {
                CustomViewUtils.setEditDataInFieldsForOldUser(data = dataSet, viewMap = allViewMap)
                Handler(Looper.getMainLooper()).postDelayed(
                    { binding.progressBar.hideView() },
                    1000
                )
            } else {
                dataSet.customFormData.forEach { customData ->
                    if (enumContains<FormItemType>(customData.type ?: "")) {
                        val formItemType = FormItemType.valueOf(customData.type ?: "")
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
                                geoAddressValue = dataSet.geoAddress,
                                geoLocationLat = dataSet.geoLocationLat ?: 0.0,
                                geoLocationLong = dataSet.geoLocationLong ?: 0.0,
                                value = customData.value,
                            ),
                            viewMap = allViewMap,
                            beatList = dataSet.beats,
                            pricingGroupId = dataSet.pricingGroup.toString()
                        )
                    }
                }

                if (editCustomerData?.pricingGroup != null) {
                    val formItemType = FormItemType.DROPDOWN
                    allViewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP]?.let { view ->
                        if (view is AssignPricingGroupDropDown) {
                            view.getFormFields()?.let { formField ->
                                CustomViewUtils.setEditDataInFields(
                                    formItemType = formItemType,
                                    data = NameAndValueSetInfoModel(
                                        name = formField.fieldProps?.name,
                                        label = formField.fieldProps?.label,
                                        isRequired = formField.fieldProps?.required,
                                        isCustom = formField.isCustom,
                                        type = formField.type,
                                        subModuleType = formField.type,
                                        dataType = formField.type,
                                        value = dataSet.pricingGroupName,
                                        geoAddressValue = dataSet.geoAddress,
                                        geoLocationLat = dataSet.mapLocationLat ?: 0.0,
                                        geoLocationLong = dataSet.mapLocationLong ?: 0.0,
                                    ),
                                    viewMap = allViewMap,
                                    beatList = dataSet.beats,
                                    pricingGroupId = dataSet.pricingGroup.toString()
                                )
                            }
                        }
                    }
                } else {  /*TODO NOTHING*/
                }

                if (editCustomerData?.beats.isNullOrEmpty().not()) {
                    val formItemType = FormItemType.DROPDOWN
                    allViewMap[AppConstant.SECTION_NAME_SELECT_BEAT]?.let { view ->
                        if (view is AssignBeatDropDown) {
                            view.getFormField()?.let { formField ->
                                CustomViewUtils.setEditDataInFields(
                                    formItemType = formItemType,
                                    data = NameAndValueSetInfoModel(
                                        name = formField.fieldProps?.name,
                                        label = formField.fieldProps?.label,
                                        isRequired = formField.fieldProps?.required,
                                        isCustom = formField.isCustom,
                                        type = formField.type,
                                        subModuleType = formField.type,
                                        dataType = formField.type,
                                        geoAddressValue = dataSet.geoAddress,
                                        geoLocationLat = dataSet.mapLocationLat ?: 0.0,
                                        geoLocationLong = dataSet.mapLocationLong ?: 0.0,
                                    ),
                                    viewMap = allViewMap,
                                    beatList = dataSet.beats,
                                    pricingGroupId = dataSet.pricingGroup.toString()
                                )
                            }
                        }
                    }
                } else {  /*TODO NOTHING*/
                }

                if (editCustomerData?.geoAddress.isNullOrBlank().not()) {
                    val formItemType = FormItemType.SHORT_ANSWER
                    allViewMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let { view ->
                        if (view is CustomerMapAddressPicker) {
                            view.getFormFields()?.let { formField ->
                                CustomViewUtils.setEditDataInFields(
                                    formItemType = formItemType,
                                    data = NameAndValueSetInfoModel(
                                        name = formField.fieldProps?.name,
                                        label = formField.fieldProps?.label,
                                        isRequired = formField.fieldProps?.required,
                                        isCustom = formField.isCustom,
                                        type = formField.type,
                                        subModuleType = formField.type,
                                        dataType = formField.type,
                                        geoAddressValue = dataSet.geoAddress,
                                        geoLocationLat = dataSet.mapLocationLat ?: 0.0,
                                        geoLocationLong = dataSet.mapLocationLong ?: 0.0,
                                    ),
                                    viewMap = allViewMap,
                                    beatList = dataSet.beats,
                                    pricingGroupId = dataSet.pricingGroup.toString()
                                )
                            }
                        }
                    }
                } else {  /*TODO NOTHING*/
                }


                Handler(Looper.getMainLooper()).postDelayed(
                    { binding.progressBar.hideView() },
                    1000
                )
            }
        }
    }

    private fun initLeadData(leadModel: LeadLisDataItem?) {
        val allViewMap = getAllViewsFromSharedModel()
        leadModel?.let { dataSet ->
            CustomViewUtils.setDataForLeadToCustomerConversion(data = dataSet, viewMap = allViewMap)
            Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.hideView() }, 1000)
        }
    }

    private fun goToNextPage(step: Int? = null) {
        Utils.hideKeyboard(this)
        CoroutineScope(Dispatchers.IO).launch {
            if (step == null) {
                val nonValidFields = mutableListOf<String>()
                val data = sharedCustomerViewModel.backupData[stepsAdapter.getCurrentStep()]
                val validationMap = hashMapOf<String, Pair<String?, Boolean>>()
                data?.first?.sections?.forEach { section ->
                    section?.formItems?.forEach { formItem ->
                        if (enumContains<FormItemType>(formItem?.type!!)) {
                            val formItemType = FormItemType.valueOf(formItem.type)
                            val validityCheckResult = CustomViewUtils.validateViews(
                                formItemType = formItemType,
                                data = formItem,
                                viewMap = data.second,
                                editCustomerData?.parentsCount ?: 0
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
                        binding.vpStepsPager.currentItem = (stepsAdapter.getCurrentStep())
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
    }

    private fun prepareDataForSave() {
        binding.progressBar.showView()
        CoroutineScope(Dispatchers.IO).launch {
            val data = sharedCustomerViewModel.backupData
            val serverDatMap = hashMapOf<String, NameAndValueSetInfoModel?>()
            for ((pageNo, page) in data) {
                page.first.sections?.forEach { section ->
                    section?.formItems?.forEach { formItem ->
                        if (enumContains<FormItemType>(formItem?.type!!)) {
                            val formItemType = FormItemType.valueOf(formItem.type)
                            if (formItemType == FormItemType.FILE_UPLOAD) {
                                serverFileDataMap.putAll(
                                    CustomViewUtils.getFileDataToSendToServer(
                                        formItemType = formItemType,
                                        data = formItem,
                                        viewMap = page.second
                                    )
                                )
                            } else {
                                serverDatMap.putAll(
                                    CustomViewUtils.getDataToSendToServer(
                                        formItemType = formItemType,
                                        data = formItem,
                                        viewMap = page.second
                                    )
                                )
                            }
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


            if (serverFileDataMap.values.isEmpty() || serverFileMap.values.sumOf {
                    it.second?.size ?: 0
                } >= totalImages) {
                if (customerId != 0 && customerId != -1) {
                    updateCustomer(serverDatMap, serverFileMap, totalImages)
                } else {
                    createCustomer(serverDatMap, serverFileMap, totalImages)
                }
            } else {
                if (serverFileDataMap.values.none { it.second.isNullOrEmpty().not() }.not()) {
                    currentIndex = 0
                    uploadFiles(serverFileDataMap, currentIndex, totalImages)
                } else {
                    if (customerId != 0 && customerId != -1) {
                        updateCustomer(serverDatMap, serverFileMap, totalImages)
                    } else {
                        createCustomer(serverDatMap, serverFileMap, totalImages)
                    }
                }
            }
        }
    }

    private fun updateCustomer(
        serverDatMap: HashMap<String, NameAndValueSetInfoModel?>,
        serverFileMap: HashMap<String, Pair<FormItemsItem, ArrayList<PicMapModel?>?>>,
        totalImages: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val mapParams: HashMap<String, NameAndValueSetInfoModel?> = hashMapOf()
            mapParams.putAll(serverDatMap)
            for ((key, value) in serverFileMap) {
                val imgIds: String = if (value.second?.size == 1) {
                    value.second?.get(0)?.id.toString()
                } else {
                    value.second?.mapNotNull { it?.id }?.joinToString { "," } ?: ""
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

            var lat = 0.0
            var lang = 0.0
            serverDatMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let {
                lat = it.geoLocationLat ?: 0.0
                lang = it.geoLocationLong ?: 0.0
            }


            viewModel.updateCustomerNew(
                AddNewCustomerFormDataModel(
                    customFormData = ArrayList(
                        mapParams.values.filterNotNull()
                    ),
                    mapLocationLat = lat,
                    mapLocationLong = lang,
                    activityGeoAddress = currentGeoAddress,
                    geoLocationLat = currentGeoLocationLat,
                    geoLocationLong = currentGeoLocationLong
                ), intent.getIntExtra(AppConstant.CUSTOMER_ID, 0), hasInternetConnection()
            )

        }
    }

    private fun createCustomer(
        serverDatMap: HashMap<String, NameAndValueSetInfoModel?>,
        serverFileMap: HashMap<String, Pair<FormItemsItem, ArrayList<PicMapModel?>?>>,
        totalImages: Int
    ) {
        val mapParams: HashMap<String, NameAndValueSetInfoModel?> = hashMapOf()
        mapParams.putAll(serverDatMap)
        for ((key, value) in serverFileMap) {
            val imgIds: String = if (value.second?.size == 1) {
                value.second?.get(0)?.id.toString()
            } else {
                value.second?.mapNotNull { it?.id }?.joinToString { "," } ?: ""
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

        var lat = 0.0
        var lang = 0.0
        serverDatMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let {
            lat = it.geoLocationLat ?: 0.0
            lang = it.geoLocationLong ?: 0.0
        }

        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)
            viewModel.saveCustomerNew(
                AddNewCustomerFormDataModel(
                    customFormData = ArrayList(
                        mapParams.values.filterNotNull()
                    ),
                    lead = leadModel?.id,
                    mapLocationLat = lat,
                    mapLocationLong = lang,
                    activityGeoAddress = currentGeoAddress,
                    geoLocationLat = currentGeoLocationLat,
                    geoLocationLong = currentGeoLocationLong
                ), hasInternetConnection()
            )
        } else {
            viewModel.saveCustomerNew(
                AddNewCustomerFormDataModel(
                    customFormData = ArrayList(
                        mapParams.values.filterNotNull()
                    ),
                    mapLocationLat = lat,
                    mapLocationLong = lang,
                    activityGeoAddress = currentGeoAddress,
                    geoLocationLat = currentGeoLocationLat,
                    geoLocationLong = currentGeoLocationLong
                ), hasInternetConnection()
            )
        }

    }

    private fun uploadFiles(
        serverFileDatMap: HashMap<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>>,
        index: Int,
        totalCount: Int
    ) {
        if (index < serverFileDatMap.size) {
            key = serverFileDatMap.keys.elementAt(index)
            val photoList = serverFileDatMap[key]?.second
            if ((photoList?.size ?: 0) > 0) {
                val photoCountForUploading = photoList?.filter { it?.onEditProduct == false }?.size
                for (i in 0 until photoList?.size!!) {
                    if (photoList[i]?.imagePath != null) {
                        if (photoList[i]?.onEditProduct!!.not()) {
                            lifecycleScope.launch {
                                val compressedImageFile = Compressor.compress(
                                    this@NewAddCustomerActivity, File(photoList[i]!!.imagePath!!)
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
            prepareDataForSave()
        }
    }

    private fun changeButtons(position: Int) {
        when {
            position == 0 -> {
                binding.btnBack.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
            }

            (position < 3) -> {
                binding.btnBack.visibility = View.VISIBLE
                binding.btnSave.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
            }

            else -> {
                binding.btnBack.visibility = View.VISIBLE
                binding.btnSave.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
            }
        }
    }

    private fun getStepsList(): MutableList<Sections?> {
        return mutableListOf(
            Sections(
                name = resources.getString(R.string.customerLevelAndType)
            ), Sections(
                name = resources.getString(R.string.businessDetails)
            ), Sections(
                name = resources.getString(R.string.assign)
            ), Sections(
                name = resources.getString(R.string.preview)
            )
        )
    }

    private fun getAllViewsFromSharedModel(): HashMap<String, View> {
        val map: HashMap<String, View> = hashMapOf()
        sharedCustomerViewModel.backupData.values.filterNotNull().forEach {
            if (it.second.containsKey(AppConstant.SECTION_NAME_CUSTOMER_LEVEL)) {
                if (it.second[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] is CustomerLevelAndType && it.second[AppConstant.SECTION_NAME_CUSTOMER_LEVEL]?.tag == CustomerLevelAndType::class.java.name) {
                    map.putAll((it.second[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getViewMap())
                }
            }
            map.putAll(it.second)
        }
        return map
    }

    fun showProgress() {
        binding.progressBar.showView()
    }

    fun hideProgress() {
        if ((intent.hasExtra(AppConstant.CUSTOMER_ID) && intent.getIntExtra(AppConstant.CUSTOMER_ID, 0) != 0) || intent.hasExtra(AppConstant.LEAD_INFO)) {
            /*TODO NOTHING FOR NOW*/
        } else {
            Handler(Looper.getMainLooper()).postDelayed({ binding.progressBar.hideView() }, 500)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.vpStepsPager.currentItem == 0) {
            finish()
        } else {
            binding.btnBack.performClick()
        }
        super.onBackPressed()
    }
    
    fun setAddress(){
        
        val allViewMap = getAllViewsFromSharedModel()
        if ((intent.hasExtra(AppConstant.CUSTOMER_ID) && intent.getIntExtra(AppConstant.CUSTOMER_ID, 0) != 0) || intent.hasExtra(AppConstant.LEAD_INFO)) {
            /*TODO NOTHING FOR NOW*/
        }
        else{
            allViewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
                if (view is EditInputLayout && view.getFieldValue().value.isNullOrEmpty()) {
                    allViewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
                        if (view is EditInputLayout) {
                            currentGeoAddress?.let { addressLine1 ->
                                try {
                                    view.setValue(addressLine1)
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                    
                    allViewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
                        if (view is PinCodeEditor) {
                            currentLocationPinCode?.let { pinCode ->
                                try {
                                    view.setValue(pinCode)
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                    
                    allViewMap[AppConstant.SECTION_NAME_CITY]?.let { view ->
                        if (view is EditInputLayout) {
                            currentLocationCity?.let { city ->
                                try {
                                    view.setValue(city)
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                    
                    allViewMap[AppConstant.SECTION_NAME_STATE]?.let { view ->
                        if (view is EditInputLayout) {
                            currentLocationState?.let { state ->
                                try {
                                    view.setValue(state)
                                } catch (_: Exception) {
                                }
                            }
                        }
                        if (view is StateDropDown) {
                            currentLocationState?.let { state ->
                                try {
                                    view.setValue(state)
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                    
                    allViewMap[AppConstant.SECTION_NAME_TERRITORY]?.let  { view ->
                        if (view is EditInputLayout) {
                            currentLocationTerritory?.let { territory ->
                                try {
                                    view.setValue(territory)
                                } catch (_: Exception) {
                                }
                            }
                        }
                    }
                }
            }
        }
        
        allViewMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let { geoAddressLayout ->
            if (geoAddressLayout is CustomerMapAddressPicker){
                geoAddressLayout.setOnLocationFetchedListener { location ->
                    
                    if (location != null){
                        allViewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
                            if (view is EditInputLayout) {
                                buildString {
                                    location?.maxAddressLineIndex?.let {
                                        for (i in 0..it) {
                                            append(location.getAddressLine(i)).append(" ")
                                        }
                                    }
                                }.trim().let { addressLine1 ->
                                    try {
                                        view.setValue(addressLine1)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
                            if (view is PinCodeEditor) {
                                location?.postalCode?.let { pinCode ->
                                    try {
                                        view.setValue(pinCode)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_CITY]?.let { view ->
                            if (view is EditInputLayout) {
                                location?.locality?.let { city ->
                                    try {
                                        view.setValue(city)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_STATE]?.let { view ->
                            if (view is EditInputLayout) {
                                location?.adminArea?.let { state ->
                                    try {
                                        view.setValue(state)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                            if (view is StateDropDown) {
                                location?.adminArea?.let { state ->
                                    try {
                                        view.setValue(state)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_TERRITORY]?.let  { view ->
                            if (view is EditInputLayout) {
                                location?.locality?.let { territory ->
                                    try {
                                        view.setValue(territory)
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }
                    }
                    /*else{
                        
                        allViewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
                            if (view is EditInputLayout) {
                                view.clearValue()
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
                            if (view is PinCodeEditor) {
                                view.clearValue()
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_CITY]?.let { view ->
                            if (view is EditInputLayout) {
                                view.clearValue()
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_STATE]?.let { view ->
                            if (view is EditInputLayout) {
                                view.clearValue()
                            }
                            if (view is StateDropDown) {
                                view.clearValue()
                            }
                        }
                        
                        allViewMap[AppConstant.SECTION_NAME_TERRITORY]?.let  { view ->
                            if (view is EditInputLayout) {
                                view.clearValue()
                            }
                        }
                        
                    }*/
                }
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
                            this@NewAddCustomerActivity,
                            longitude = currentGeoLocationLong,
                            latitude = currentGeoLocationLat
                        ) { address ->
                            currentGeoAddress = address
                        }.setOnLocationFetchedListener { address ->
                            currentLocationCity = address?.locality
                            currentLocationState = address?.adminArea
                            currentLocationTerritory = address?.locality
                            currentLocationPinCode = address?.postalCode
                        }
                    } else {
                        val fragment =
                            MockLocationDetectedDialogFragment.getInstance(this@NewAddCustomerActivity)
                        fragment.isCancelable = false
                        fragment.show(
                            supportFragmentManager,
                            MockLocationDetectedDialogFragment::class.java.name
                        )
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
        onBackPressedDispatcher.onBackPressed()
    }
    
   
}
