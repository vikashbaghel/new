package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityPaymentAgainstOrderBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.helper.InputFilterMinMax
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.sales.cart.CartViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.SalesMainActivity
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

class PaymentAgainstOrderActivity : BaseActivity(), AdapterView.OnItemSelectedListener,
        LocationPermissionUtils.ILocationPermissionListener,
        ProductPhotoListAdapter.OnImageDeleteListener,
        MultipleImageUploadListener, MockLocationDetectedDialogFragment.IMockLocationActionListener,
        GeoFencingWarningDialogFragment.IGeoFencingActionListener,
        MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityPaymentAgainstOrderBinding
    private lateinit var cartListResponseModel: OrderData
    private lateinit var imageUploadViewModel: ImageUploadViewModel

    private lateinit var addPhotoListAdapter: ProductPhotoListAdapter
    private val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private lateinit var cartViewModel: CartViewModel
    private var paymentAmount: String? = null
    private var paymentTerms: String? = null

    private var paymentOptionCheck = AppConstant.PAYMENT_ON_DELIVERY
    private var paymentTransactionMode = ""

    private var isDataSubmitted = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var geoAddress: String? = null

    private var multiplePicCount = 0
    private lateinit var progressDialog: ProgressDialog

    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private var customerModel: CustomerData? = null
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentAgainstOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)

        locationPermissionUtils = LocationPermissionUtils(this, this)

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }

        if (intent.hasExtra(AppConstant.CART_ITEM)) {
            cartListResponseModel = intent.getParcelableExtra(AppConstant.CART_ITEM)!!
            paymentAmount = cartListResponseModel.amount.toString()
            paymentTerms = cartListResponseModel.customerPaymentTerms

            if (cartListResponseModel.orderImagesInfo.isNullOrEmpty().not()) {
                for (i in cartListResponseModel.orderImagesInfo!!.indices) {
                    pics.add(cartListResponseModel.orderImagesInfo!![i])
                    multiplePicCount += 1
                    val addedPhotoModel = AddedPhotoModel()
                    addedPhotoModel.imageId = cartListResponseModel.orderImagesInfo!![i].id
                    addedPhotoModel.imagePath = cartListResponseModel.orderImagesInfo!![i].url
                    addedPhotoModel.onEditProduct = true
                    addedPhotoModel.isDisplayPicEnable = false
                    photoModelList.add(addedPhotoModel)
                }

                if (photoModelList.size <= 5) {
                    photoModelList.add(null)
                }
            } else {
                photoModelList.add(null)
            }

            if (cartListResponseModel.comment.isNullOrEmpty().not()) {
                binding.etComment.setText(cartListResponseModel.comment)
            }

            if (cartListResponseModel.adminComment.isNullOrEmpty().not()) {
                binding.etAdminComment.setText(cartListResponseModel.adminComment)
            }

            if (cartListResponseModel.remainingPaymentDays != 0) {
                binding.etPaymentDays.setText("${cartListResponseModel.remainingPaymentDays ?: ""}")
            }
            if (cartListResponseModel.transactionRefNo.isNullOrEmpty().not()) {
                binding.etTransactionRef.setText("${cartListResponseModel.transactionRefNo}")
            }
            if (cartListResponseModel.paymentMode.isNullOrEmpty().not()) {
                paymentTransactionMode = cartListResponseModel.paymentMode!!
            }

        } else {
            photoModelList.add(null)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("uploading ...")
        progressDialog.setCancelable(false)

        initRecyclerView()
        initObservers()
        getUserCurrentLocation()

        binding.etPaymentAmount.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                        9,
                        AppConstant.MAX_DIGIT_AFTER_DECIMAL,
                )
        )

        binding.btnAdd.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnAdd.windowToken, 0)
            validateData()
        }

        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.btnCancel.setOnClickListener { onBackPressed() }

        initLayout()

    }

    private fun validateData() {
        if (paymentOptionCheck == AppConstant.FULL_PAYMENT_IN_ADVANCE_API
                || paymentOptionCheck == AppConstant.PARTIAL_PAYMENT_API) {
            if (binding.etPaymentAmount.text.isEmpty()) {
                showToast("Please enter amount!!")
                return
            } else if (paymentTransactionMode.isEmpty()) {
                showToast("Please select payment mode!!")
                return
            } else {
                cartListResponseModel.paymentAmount =
                        binding.etPaymentAmount.text.toString().toDouble()
            }
        }

        if (binding.etPaymentDays.isVisible) {
            if (binding.etPaymentDays.text.isEmpty()) {
                Toast.makeText(this, "Enter Credit Days!! ", Toast.LENGTH_SHORT).show()
                return
            } else {
                cartListResponseModel.remainingPaymentDays =
                        binding.etPaymentDays.text.trim().toString().toInt()
            }
        }

        if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            pics.clear()
            photoModelList.forEach {
                it?.let {
                    pics.add(PicMapModel(it.imageId, it.imagePath))
                }
            }

            cartListResponseModel.orderImagesInfo = pics
            cartListResponseModel.comment = binding.etComment.text.trim().toString()
            cartListResponseModel.adminComment = binding.etAdminComment.text.trim().toString()

            val paymentDetailsModel = PaymentDetailsModel()
            if (paymentOptionCheck != AppConstant.CREDIT_DAYS_API && paymentOptionCheck != AppConstant.PAYMENT_ON_DELIVERY_API) {
                paymentDetailsModel.amount = binding.etPaymentAmount.text.toString().toDouble()
            }
            paymentDetailsModel.paymentMode = paymentOptionCheck
            cartListResponseModel.paymentDetails = paymentDetailsModel

            cartListResponseModel.paymentOptionCheck = paymentOptionCheck

            if (binding.rlPaymentMode.isVisible) {
                cartListResponseModel.paymentMode = paymentTransactionMode
            }
            if (binding.etTransactionRef.isVisible) {
                cartListResponseModel.transactionRefNo =
                        binding.etTransactionRef.text.trim().toString()
            }

            val intent = Intent()
            intent.putExtra(AppConstant.CART_ITEM, cartListResponseModel)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (photoModelList.size > 1) {
                if (hasInternetConnection()) {
                    addImage()
                } else {
                    photoModelList.forEach {
                        it?.let {
                            pics.add(PicMapModel(it.imageId, it.imagePath))
                        }
                    }

                    cartListResponseModel.orderImagesInfo = pics
                    submitData()
                }
            } else {
                progressDialog.show()
                submitData()
            }
        }
    }

    private fun addImage() {
        progressDialog.show()
        var isEditImageAvailable = false
        if (pics.size > 0) {
            for (i in pics.size until photoModelList.size) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null) {
                    if (!photoModelList[i]!!.onEditProduct) {
                        isEditImageAvailable = true
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                    this@PaymentAgainstOrderActivity,
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
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null) {
                    isEditImageAvailable = true
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                                this@PaymentAgainstOrderActivity,
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
            submitData()
        }
    }

    private fun initRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = ProductPhotoListAdapter(photoModelList, this, true)
        binding.rvImages.adapter = addPhotoListAdapter
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
                        MockLocationDetectedDialogFragment.getInstance(this@PaymentAgainstOrderActivity)
                fragment.isCancelable = false
                fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    geoAddress = GeoLocationUtils.getAddress(this@PaymentAgainstOrderActivity, longitude = geoLocationLong, latitude = geoLocationLat)

                    if (intent.hasExtra(AppConstant.CART_ITEM).not()) {
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            if (SharedPref.getInstance()
                                            .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                            ) {
                                registerGeofenceUpdates()
                            } else {
                                runOnUiThread {
                                    binding.btnLayout.visibility = View.VISIBLE
                                    enableTouch()
                                }
                            }
                        } else {
                            showStartDayDialog()
                        }
                    } else {
                        runOnUiThread {
                            binding.btnLayout.visibility = View.VISIBLE
                            enableTouch()
                        }
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

//        if (supportFragmentManager.isDestroyed.not()) {
//            val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this,
//                    geoLocationLat, geoLocationLong)
//            fragment.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
//        }
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
                enableTouch()
            }
        } else {
            val fragment = GeoFencingWarningDialogFragment.getInstance(this)
            fragment.show(supportFragmentManager, GeoFencingWarningDialogFragment::class.java.name)
        }
    }


    private fun initLayout() {
        binding.spinnerPaymentOption.adapter = ArrayAdapter(
                this, R.layout.single_text_view_spinner_16dp_text,
                resources.getStringArray(R.array.payment_options)
        )

        if (!paymentTerms.isNullOrEmpty()) {
            if (paymentTerms!!.contains("days")) {
                binding.spinnerPaymentOption.setSelection(3)
                val days = (paymentTerms?.split(" ")?.get(0)?.trim()) ?: ""
                binding.etPaymentDays.setText(days)
            } else if (paymentTerms.equals("Advance")
                    || paymentTerms.equals(AppConstant.FULL_PAYMENT_IN_ADVANCE_API)) {
                binding.spinnerPaymentOption.setSelection(1)
            } else if (paymentTerms.equals(AppConstant.PARTIAL_PAYMENT_API)) {
                binding.spinnerPaymentOption.setSelection(2)
            } else {
                binding.spinnerPaymentOption.setSelection(0)
            }
        } else if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            if (cartListResponseModel.paymentOptionCheck.isNullOrEmpty().not()) {
                if (cartListResponseModel.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                    binding.spinnerPaymentOption.setSelection(3)
                    if (cartListResponseModel.remainingPaymentDays != null) {
                        binding.etPaymentDays.setText("${cartListResponseModel.remainingPaymentDays}")
                    } else {
                        binding.etPaymentDays.setText("")
                    }
                } else {
                    when (cartListResponseModel.paymentOptionCheck) {
                        AppConstant.PAYMENT_ON_DELIVERY_API ->
                            binding.spinnerPaymentOption.setSelection(0)

                        AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> {
                            binding.spinnerPaymentOption.adapter = ArrayAdapter(
                                    this, R.layout.single_text_view_spinner_16dp_text,
                                    resources.getStringArray(R.array.payment_options_for_edit)
                            )
                            binding.spinnerPaymentOption.setSelection(0)
                        }

                        AppConstant.PARTIAL_PAYMENT_API -> {
                            binding.spinnerPaymentOption.adapter = ArrayAdapter(
                                    this, R.layout.single_text_view_spinner_16dp_text,
                                    resources.getStringArray(R.array.payment_options_for_edit)
                            )
                            binding.spinnerPaymentOption.setSelection(1)
                        }
                    }
                }
            }
        } else {
            binding.spinnerPaymentOption.setSelection(0)
        }

        binding.spinnerPaymentMode.adapter = ArrayAdapter(
                this, R.layout.single_text_view_spinner_16dp_text,
                resources.getStringArray(R.array.mode_of_payment)
        )

        binding.spinnerPaymentMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, adapterPosition: Int, p3: Long) {
                paymentTransactionMode = if (adapterPosition != 0) {
                    resources.getStringArray(R.array.mode_of_payment)[adapterPosition]
                } else {
                    ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        if (paymentTransactionMode.isNotEmpty()) {
            val index = resources.getStringArray(R.array.mode_of_payment).indexOfLast { it.toString() == paymentTransactionMode }
            if (index != -1) {
                binding.spinnerPaymentMode.setSelection(index)
            }
        }

        binding.etPaymentDays.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "750"))

        binding.spinnerPaymentOption.onItemSelectedListener = this

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (binding.spinnerPaymentOption.selectedItem.toString() == AppConstant.FULL_PAYMENT_IN_ADVANCE) {
            binding.etPaymentDays.isVisible = false
            binding.tvDays.isVisible = false
            binding.hdRemainingPayment.isVisible = false
            binding.hdPaymentAmount.isVisible = true
            binding.etPaymentAmount.isVisible = true
            binding.hdRupee.isVisible = true
            binding.hdPaymentMode.isVisible = true
            binding.rlPaymentMode.isVisible = true
            binding.hdRefNo.isVisible = true
            binding.etTransactionRef.isVisible = true
            binding.etPaymentAmount.setText(
                    CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                            paymentAmount
                    )
            )
            binding.etPaymentAmount.isEnabled = false

            paymentOptionCheck = AppConstant.FULL_PAYMENT_IN_ADVANCE_API

        } else if (binding.spinnerPaymentOption.selectedItem.toString() == AppConstant.PARTIAL_PAYMENT) {
            binding.etPaymentDays.isVisible = true
            binding.tvDays.isVisible = true
            binding.hdRemainingPayment.isVisible = true
            binding.hdPaymentAmount.isVisible = true
            binding.etPaymentAmount.isVisible = true
            binding.hdRupee.isVisible = true
            binding.hdPaymentMode.isVisible = true
            binding.rlPaymentMode.isVisible = true
            binding.hdRefNo.isVisible = true
            binding.etTransactionRef.isVisible = true


            if (intent.hasExtra(AppConstant.ORDER_EDIT).not()
                    && cartListResponseModel.paymentDetails != null
                    && cartListResponseModel.paymentDetails?.amount != null
            ) {
                binding.etPaymentAmount.setText(
                        CalculatorHelper().formatDoubleDecimalPoint(
                                cartListResponseModel.paymentDetails?.amount!!,
                                AppConstant.TWO_DECIMAL_POINTS
                        )
                )
            } else if (cartListResponseModel.paymentDetails?.amount != null) {
                binding.etPaymentAmount.setText(
                        CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                                "" + cartListResponseModel.paymentDetails?.amount!!
                        )
                )
            } else {
                binding.etPaymentAmount.setText(
                        CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                                paymentAmount
                        )
                )
            }
            binding.etPaymentAmount.isEnabled = true

            paymentOptionCheck = AppConstant.PARTIAL_PAYMENT_API

        } else if (binding.spinnerPaymentOption.selectedItem.toString() == AppConstant.PAYMENT_ON_DELIVERY) {
            binding.etPaymentDays.isVisible = false
            binding.tvDays.isVisible = false
            binding.hdRemainingPayment.isVisible = false
            binding.hdPaymentAmount.isVisible = false
            binding.etPaymentAmount.isVisible = false
            binding.hdRupee.isVisible = false
            binding.hdPaymentMode.isVisible = false
            binding.rlPaymentMode.isVisible = false
            binding.hdRefNo.isVisible = false
            binding.etTransactionRef.isVisible = false
            binding.etPaymentAmount.setText("")

            paymentOptionCheck = AppConstant.PAYMENT_ON_DELIVERY_API

        } else if (binding.spinnerPaymentOption.selectedItem.toString() == AppConstant.CREDIT_DAYS) {
            binding.etPaymentDays.isVisible = true
            binding.tvDays.isVisible = true
            binding.hdRemainingPayment.isVisible = true
            binding.hdPaymentAmount.isVisible = false
            binding.etPaymentAmount.isVisible = false
            binding.hdRupee.isVisible = false
            binding.hdPaymentMode.isVisible = false
            binding.rlPaymentMode.isVisible = false
            binding.hdRefNo.isVisible = false
            binding.etTransactionRef.isVisible = false
            binding.etPaymentAmount.setText("")

            paymentOptionCheck = AppConstant.CREDIT_DAYS_API
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
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
                            submitData()
                        }
                    }
                }
            } else {
                showToast("${genericResponseModel.message}")
                progressDialog.dismiss()
            }
        }

        cartViewModel.confirmOrderLiveData.observe(this) { order ->
            progressDialog.dismiss()
            showToast("${order.message}")
            if (order.error == false) {
                openOrderActivity()
            } else {
                binding.btnAdd.isEnabled = true
            }
        }
    }

    private fun openOrderActivity() {
        val i = Intent(this, AllListOfOrdersActivity::class.java)
        i.putExtra(AppConstant.ORDER_ID, true)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun submitData() {
        cartListResponseModel.comment = binding.etComment.text.trim().toString()
        cartListResponseModel.adminComment = binding.etAdminComment.text.trim().toString()
        cartListResponseModel.paymentOptionCheck = paymentOptionCheck

        if (binding.rlPaymentMode.isVisible) {
            cartListResponseModel.paymentMode = paymentTransactionMode
        }
        if (binding.etTransactionRef.isVisible) {
            cartListResponseModel.transactionRefNo =
                    binding.etTransactionRef.text.trim().toString()
        }

        cartListResponseModel.geoLocationLat = geoLocationLat
        cartListResponseModel.geoLocationLong = geoLocationLong
        cartListResponseModel.geoAddress = geoAddress

        cartListResponseModel.deviceInformation = getDeviceInformation()
        cartListResponseModel.batteryPercent = getBatteryInformation().first
        cartListResponseModel.batteryOptimisation = isBatteryOptimizationEnabled()
        cartListResponseModel.locationPermission = isGpsEnabled()


        if (hasInternetConnection()) {
            val list = ArrayList<Int>()
            if (pics.size > 0) {
                pics.forEach {
                    list.add(it.id!!)
                }
            }
            cartListResponseModel.orderImages = list
        } else {
            cartListResponseModel.totalAmount = cartListResponseModel.amount
            cartListResponseModel.customer = customerModel
            cartListResponseModel.customerLevel = customerModel?.customerLevel

            if (customerModel?.isSyncedToServer == null || customerModel?.isSyncedToServer == true) {
                cartListResponseModel.isCustomerIdUpdated = true
            }
        }

        binding.btnAdd.isEnabled = false
        cartViewModel.confirmOrder(cartListResponseModel, hasInternetConnection())
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
        val fragment = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)

        val bundle = Bundle()
        bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
        fragment.arguments = bundle

        fragment.show(
                supportFragmentManager,
                AppConstant.IMAGE_UPLOAD_TAG
        )
    }

    override fun onEditAlreadyUploadedImage() {
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
            ).show()
        }
    }

    override fun onGalleryMultipleUpload(
            fileList: List<String>
            ?
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
            ).show()
        }
    }

    override fun onGpsEnabled() {
        super.onGpsEnabled()
        Handler(Looper.myLooper()!!).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        onBackPressed()
    }

    override fun onBackPressed() {
        if (isDataSubmitted) {
            val i = Intent(this, SalesMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
        finish()
    }

    override fun onDismissDialogForGeoFencing() {
        val i = Intent(this, SalesMainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    override fun onDismissDialogForStartDay() {
        onDismissDialogForGeoFencing()
    }

    override fun onSuccessfullyMarkAttendance() {
        if (SharedPref.getInstance()
                        .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
        ) {
            registerGeofenceUpdates()
        } else {
            runOnUiThread {
                binding.btnLayout.visibility = View.VISIBLE
                enableTouch()
            }
        }
    }
}