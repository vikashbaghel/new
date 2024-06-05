package com.app.rupyz.sales.payment

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddRecordPaymentBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.generic.utils.splitFullName
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.app.rupyz.model_kt.order.payment.Customer
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.ProductPhotoListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordPaymentActivity : BaseActivity(),
        LocationPermissionUtils.ILocationPermissionListener,
        ProductPhotoListAdapter.OnImageDeleteListener, MultipleImageUploadListener,
        MockLocationDetectedDialogFragment.IMockLocationActionListener,
        GeoFencingWarningDialogFragment.IGeoFencingActionListener,
        MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityAddRecordPaymentBinding
    private lateinit var recordPaymentViewModel: RecordPaymentViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel

    var customerId: Int? = 0

    private lateinit var addPhotoListAdapter: ProductPhotoListAdapter

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var multiplePicCount = 0

    private var followUpDateAndTime = ""

    private lateinit var progressDialog: ProgressDialog

    private val pics: ArrayList<PicMapModel> = ArrayList()
    private var photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()

    private lateinit var permissionUtils: LocationPermissionUtils
    private var customerModel: CustomerData? = null

    private var modeOfPayment = ""
    private val recordPaymentData = RecordPaymentData()
    private  var  fragment : MarkAttendanceBottomSheetDialogFragment? = null
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordPaymentViewModel = ViewModelProvider(this)[RecordPaymentViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]

        permissionUtils = LocationPermissionUtils(this, this)

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)

        initRecyclerView()
        initLayout()

        getUserCurrentLocation()

        binding.ivBack.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {
            validateData()
        }

        initObservers()
    }


    private fun initRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = ProductPhotoListAdapter(photoModelList, this, true)
        binding.rvImages.adapter = addPhotoListAdapter
    }

    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (permissionUtils.hasPermission()) {
            if (permissionUtils.isGpsEnabled(this)) {
                setUpdatedLocationListener()
            } else {
                permissionUtils.showEnableGpsDialog()
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
                val fragment =
                        MockLocationDetectedDialogFragment.getInstance(this@AddRecordPaymentActivity)
                fragment.isCancelable = false
                fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                        if (SharedPref.getInstance()
                                        .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                        ) {
                            registerGeofenceUpdates()
                        } else {
                            runOnUiThread {
                                binding.btnLayout.visibility = View.VISIBLE
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
        if (supportFragmentManager.fragments.firstOrNull { it.tag?.equals(MarkAttendanceBottomSheetDialogFragment::class.java.name) == true } == null){
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
            fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
        }else{
            if (fragment?.isVisible == false && fragment?.isAdded == false){
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
            }
        }
        
//        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
//        fragment.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
    
    }


    private fun registerGeofenceUpdates() {
        val isUserInsideGeoFencing =
                if (customerModel?.mapLocationLat != 0.0 && customerModel?.mapLocationLong != 0.0) {
                    findUserIsInGeoFencingArea(
                            customerModel?.mapLocationLat ?: 0.0,
                            customerModel?.mapLocationLong ?: 0.0,
                            geoLocationLat,
                            geoLocationLong
                    ).first
                } else {
                    true
                }

        if (isUserInsideGeoFencing) {
            runOnUiThread {
                binding.btnLayout.visibility = View.VISIBLE
            }
        } else {
            val fragment = GeoFencingWarningDialogFragment.getInstance(this)
            fragment.show(supportFragmentManager, GeoFencingWarningDialogFragment::class.java.name)
        }
    }

    private fun initLayout() {
        binding.etAmount.filters =
                arrayOf<InputFilter>(DigitsInputFilter(9, AppConstant.MAX_DIGIT_AFTER_DECIMAL))

        if (intent.hasExtra(AppConstant.CUSTOMER_NAME)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
            binding.tvCustomerName.text = intent.getStringExtra(AppConstant.CUSTOMER_NAME)
        }
        binding.spModeOfPayment.adapter = ArrayAdapter(
                this, R.layout.single_text_view_spinner_16dp_text,
                resources.getStringArray(R.array.mode_of_payment)
        )

        binding.spModeOfPayment.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                modeOfPayment = if (position == 0) {
                    ""
                } else {
                    binding.spModeOfPayment.selectedItem.toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        photoModelList.add(null)
        addPhotoListAdapter.notifyItemInserted(0)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("uploading ...")
        progressDialog.setCancelable(false)

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }


        binding.tvFollowUpDateAndTime.setOnClickListener {
            val currentDate = Calendar.getInstance()
            if (followUpDateAndTime.isEmpty().not()) {
                currentDate.time =
                        DateFormatHelper.convertStringToDate(followUpDateAndTime)
            }

            val date = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                    this,
                    { _, year, monthOfYear, dayOfMonth ->
                        date.set(year, monthOfYear, dayOfMonth)
                        followUpDateAndTime =
                                DateFormatHelper.convertDateToIsoFormat(date.time)

                        binding.tvFollowUpDateAndTime.text =
                                DateFormatHelper.convertDateToCustomDateFormat(
                                        date.time,
                                        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                                )
                    },
                    currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE]
            )

            datePicker.show()
        }

    }

    private fun validateData() {
        when {
            binding.tvCustomerName.text.trim().toString().isEmpty() -> {
                showToast("Customer Name Required!")
            }

            binding.etAmount.text.trim().toString().isEmpty() -> {
                showToast("Amount Required!")
            }

            modeOfPayment.isEmpty() -> {
                showToast("Please select mode of payment!")
            }

            else -> {
                if (photoModelList.size > 0) {
                    if (hasInternetConnection()) {
                        onAddPaymentImages()
                    } else {
                        photoModelList.forEach {
                            it?.let {
                                pics.add(PicMapModel(it.imageId, it.imagePath))
                            }
                        }

                        recordPaymentData.paymentImagesInfo = pics
                        addPayment()
                    }
                } else {
                    addPayment()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionUtils.setActivityResult(requestCode, resultCode, data)
    }

    override fun onGpsEnabled() {
        super.onGpsEnabled()
        Handler(Looper.myLooper()!!).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onCameraUpload(
            fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

        } else {
            Toast.makeText(
                    this,
                    getString(R.string.upload_max_six_images),
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }

    override fun onGallerySingleUpload(
            fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

        } else {
            Toast.makeText(
                    this,
                    getString(R.string.upload_max_six_images),
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }

    override fun onGalleryMultipleUpload(
            fileList: List<String>?
    ) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                addedPhotoModel.isDisplayPicEnable = false
                photoModelList.add(addedPhotoModel)
            }

            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

            multiplePicCount += fileList.size

        } else {
            Toast.makeText(
                    this,
                    getString(R.string.upload_max_six_images),
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }


    override fun onDeleteImage(
            position: Int, key: Long?
    ) {
        if (photoModelList.size == 1) {
            photoModelList.clear()
            pics.clear()
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            photoModelList.removeAt(position)
            if (pics.isNotEmpty()) {
                pics.removeAt(position)
            }
            if (photoModelList.size < 6 && photoModelList[photoModelList.size - 1] != null) {
                photoModelList.add(null)
            }
            initRecyclerView()
        }
        multiplePicCount--
    }

    override fun onImageSelect(model: AddedPhotoModel, position: Int) {
    }

    override fun onEditImage(model: AddedPhotoModel, position: Int) {
    }

    override fun onAddImage() {
        Utils.hideKeyboard(this)
        val frag = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)
        val bundle = Bundle()
        bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
        frag.arguments = bundle
        frag.show(supportFragmentManager, AppConstant.IMAGE_UPLOAD_TAG)
    }

    override fun onEditAlreadyUploadedImage() {
    }


    private fun onAddPaymentImages() {
        progressDialog.show()
        var isEditImageAvailable = false
        if (pics.size > 0) {
            for (i in pics.size - 1 until photoModelList.size) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null
                        && photoModelList[i]?.type != AppConstant.DOCUMENT
                ) {
                    if (!photoModelList[i]!!.onEditProduct) {
                        isEditImageAvailable = true
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                    this@AddRecordPaymentActivity,
                                    File(photoModelList[i]!!.imagePath!!)
                            ) {
                                quality(30)
                                resolution(512, 512)
                                size(197_152)
                            }
                            imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                        }
                    }
                }
            }
        } else {
            for (i in photoModelList.indices) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null
                        && photoModelList[i]?.type != AppConstant.DOCUMENT
                ) {
                    isEditImageAvailable = true
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                                this@AddRecordPaymentActivity,
                                File(photoModelList[i]!!.imagePath!!)
                        ) {
                            quality(30)
                            resolution(512, 512)
                            size(197_152)
                        }
                        imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                    }
                }
            }
        }
        if (!isEditImageAvailable) {
            addPayment()
        }
    }

    private fun addPayment() {
        recordPaymentData.paymentMode = modeOfPayment
        recordPaymentData.amount = binding.etAmount.text.toString().toDouble()
        recordPaymentData.transactionRefNo = binding.etTransactionId.text.toString()
        recordPaymentData.comment = binding.etComment.text.toString()
        recordPaymentData.transactionTimeStamp = followUpDateAndTime
        recordPaymentData.customerId = customerId

        recordPaymentData.geoLocationLat = geoLocationLat
        recordPaymentData.geoLocationLong = geoLocationLong

        if (hasInternetConnection().not()) {
            val customer = Customer()
            customer.name = customerModel?.name
            customer.city = customerModel?.city

            recordPaymentData.customer = customer

            val createdBy = CreatedBy()
            val namePair = SharedPref.getInstance().getString(AppConstant.USER_NAME).splitFullName()
            createdBy.firstName = namePair.first
            createdBy.lastName = namePair.second
            recordPaymentData.createdBy = createdBy

            if (customerModel?.isSyncedToServer == null || customerModel?.isSyncedToServer == true){
                recordPaymentData.isCustomerIdUpdated = true
            }
        } else {
            val list = java.util.ArrayList<Int>()
            if (pics.size > 0) {
                pics.forEach {
                    list.add(it.id!!)
                }
            }
            recordPaymentData.paymentImages = list
        }

        recordPaymentViewModel.recordPayment(recordPaymentData, hasInternetConnection())
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
                            addPayment()
                        }
                    }
                }
            } else {
                progressDialog.dismiss()
                if (genericResponseModel.errorCode != null && genericResponseModel.errorCode == 403) {
                    logout()
                } else {
                    showToast(genericResponseModel.message)
                }
            }
        }

        recordPaymentViewModel.updatePaymentRecordLiveData.observe(this) { data ->
            Toast.makeText(this, "${data.message}", Toast.LENGTH_SHORT).show()

            data?.let {
                if (data.error == false) {
                    showToast(data.message)
                    progressDialog.dismiss()
                    val intent = Intent()
                    intent.putExtra(AppConstant.CUSTOMER_ID, customerId)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    progressDialog.dismiss()
                    if (it.errorCode != null && it.errorCode == 403) {
                        logout()
                    } else {
                        showToast(it.message)
                    }
                }
            }
        }
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
    }

    override fun onDismissDialogForGeoFencing() {
        finish()
    }

    override fun onDismissDialogForStartDay() {
        finish()
    }

    override fun onSuccessfullyMarkAttendance() {
        if (SharedPref.getInstance()
                        .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
        ) {
            registerGeofenceUpdates()
        } else {
            runOnUiThread {
                binding.btnLayout.visibility = View.VISIBLE
            }
        }
    }
}