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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityPaymentAgainstOrderBinding
import com.app.rupyz.databinding.MenuEditBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.helper.InputFilterMinMax
import com.app.rupyz.generic.helper.extractNumber
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.PAYMENT_ON_DELIVERY
import com.app.rupyz.generic.utils.AppConstant.PAYMENT_ON_DELIVERY_API
import com.app.rupyz.generic.utils.AppConstant.PAYMENT_ON_NEXT_ORDER
import com.app.rupyz.generic.utils.AppConstant.PAYMENT_ON_NEXT_ORDER_API
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.sales.address.AddNewAddressActivity
import com.app.rupyz.sales.address.AddressViewModel
import com.app.rupyz.sales.address.ConfirmAddressActivity
import com.app.rupyz.sales.cart.CartViewModel
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.ListOfCustomerActivity
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.pdfupload.PdfUploadViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.ProductPhotoListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import java.util.Locale

class PaymentAgainstOrderActivity : BaseActivity(),
    LocationPermissionUtils.ILocationPermissionListener,
    ProductPhotoListAdapter.OnImageDeleteListener,
    MultipleImageUploadListener, MockLocationDetectedDialogFragment.IMockLocationActionListener,
    GeoFencingWarningDialogFragment.IGeoFencingActionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityPaymentAgainstOrderBinding
    private lateinit var cartListResponseModel: OrderData
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private val addressViewModel: AddressViewModel by viewModels()
    private lateinit var pdfUploadViewModel: PdfUploadViewModel

    private lateinit var addPhotoListAdapter: ProductPhotoListAdapter
    private val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private lateinit var cartViewModel: CartViewModel
    private var paymentAmount: String? = null
    private var paymentTerms: String? = null

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
    private var isPopupShown = false
    private val paymentOptionListPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(
            this@PaymentAgainstOrderActivity,
            null,
            R.attr.listPopupWindowStyle
        )
    }
    private val paymentModeListPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(
            this@PaymentAgainstOrderActivity,
            null,
            R.attr.listPopupWindowStyle
        )
    }
    private var defaultAddress: CustomerAddressDataItem? = null
    private var selectedDistributor: CustomerData? = null

    private val editAddressActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                customerModel?.id?.let { customerId ->
                    addressViewModel.getAddressList(
                        customerId,
                        hasInternetConnection()
                    )
                }
            }
        }

    private var paymentOptionCheck = PAYMENT_ON_DELIVERY_API


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentAgainstOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]
        pdfUploadViewModel = ViewModelProvider(this)[PdfUploadViewModel::class.java]

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


        if (intent.hasExtra(AppConstant.SELECTED_DISTRIBUTOR)) {
            selectedDistributor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    AppConstant.SELECTED_DISTRIBUTOR,
                    CustomerData::class.java
                )
            } else {
                intent.getParcelableExtra(AppConstant.SELECTED_DISTRIBUTOR)
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

            if (cartListResponseModel.purchaseOrderNumber.isNullOrEmpty().not()) {
                binding.etPurchaseOrderNumber.setText(cartListResponseModel.purchaseOrderNumber)
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

        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnCancel.setOnClickListener { onBackPressed() }

        binding.ivToggleButtonGeneralComments.setOnClickListener {
            binding.etComment.isVisible = binding.etComment.isVisible.not()
            binding.ivToggleButtonGeneralComments.setImageResource(
                if (binding.etComment.isVisible) {
                    R.drawable.ic_minus
                } else {
                    R.drawable.ic_add
                }
            )
        }
        binding.ivToggleButtonForAdminComments.setOnClickListener {
            binding.etAdminComment.isVisible = binding.etAdminComment.isVisible.not()
            binding.ivToggleButtonForAdminComments.setImageResource(
                if (binding.etAdminComment.isVisible) {
                    R.drawable.ic_minus
                } else {
                    R.drawable.ic_add
                }
            )
        }
        binding.etExpectedDeliveryDate.setOnClickListener {
            openDatePicker()
        }
        binding.tvSeeAllAddress.setOnClickListener {
            selectAddressActivityResultLauncher.launch(
                Intent(
                    this,
                    ConfirmAddressActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_ID, cartListResponseModel?.customer?.id)
            )
        }
        binding.clAddNewAddress.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddNewAddressActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_ID, customerModel?.id)
            )
        }
        binding.ivShippingAddressMoreOptions.setOnClickListener {

            //creating a popup menu
            val elevation = binding.defaultAddressCard.elevation
            val translationZ = binding.defaultAddressCard.translationZ
//            val popup = PopupMenu(this, binding.ivShippingAddressMoreOptions, Gravity.NO_GRAVITY)
            // Inflate a custom view for the PopupWindow
            val view =
                MenuEditBinding.inflate(LayoutInflater.from(this)) // Inflate your custom layout

            // Create the PopupWindow
            val popupWindow = PopupWindow(
                view.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            // Set elevation for the PopupWindow
            popupWindow.elevation = elevation
            binding.defaultAddressCard.elevation = 0f
            binding.defaultAddressCard.translationZ = 0f
            //inflating menu from xml resource\

            //adding click listener
            view.edit.setOnClickListener {
                popupWindow.dismiss()
                editAddressActivityResultLauncher.launch(
                    Intent(
                        this,
                        AddNewAddressActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, customerModel?.id)
                        .putExtra(AppConstant.ORDER_ADDRESS_ID, defaultAddress)
                )
            }

            popupWindow.setOnDismissListener {
                binding.defaultAddressCard.elevation = elevation
                binding.defaultAddressCard.translationZ = translationZ
            }

            //displaying the popup
            popupWindow.showAsDropDown(binding.ivShippingAddressMoreOptions)


        }
        initLayout()
        customerModel?.id?.let { customerId ->
            binding.clShippingAddressParent.hideView(); binding.detailsProgressBar.showView(); addressViewModel.getAddressList(
            customerId,
            hasInternetConnection()
        )
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isDataSubmitted) {
                    val i = Intent(this@PaymentAgainstOrderActivity, SalesMainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                }
                finish()
            }
        })

    }

    private val selectAddressActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val res = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        AppConstant.ORDER_ADDRESS_ID,
                        CustomerAddressDataItem::class.java
                    )
                } else {
                    result.data?.getParcelableExtra(AppConstant.ORDER_ADDRESS_ID) as CustomerAddressDataItem?
                }
                res?.let { setDefaultAddress(it) }
            }
        }


    private fun openDatePicker() {

        val constraint = CalendarConstraints.Builder().build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraint)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText(resources.getString(R.string.app_name))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            datePicker.dismiss()
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = it
            binding.etExpectedDeliveryDate.setText(
                DateFormatHelper.convertDateToMonthStringFormat(
                    calendar.time
                )
            )
        }

        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }

        datePicker.show(supportFragmentManager, MaterialDatePicker::class.java.name)

    }

    private fun validateData() {

        if (paymentOptionCheck == AppConstant.FULL_PAYMENT_IN_ADVANCE_API
            || paymentOptionCheck == AppConstant.PARTIAL_PAYMENT_API
        ) {
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

        if (binding.tlPaymentDays.isVisible) {
            if (binding.etPaymentDays.text.isEmpty()) {
                showToast("Enter Credit Days!!")
                return
            } else {
                cartListResponseModel.remainingPaymentDays =
                    binding.etPaymentDays.text.toString().extractNumber()
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
            if (paymentOptionCheck != AppConstant.CREDIT_DAYS_API
                && paymentOptionCheck != PAYMENT_ON_DELIVERY_API
                && paymentOptionCheck != PAYMENT_ON_NEXT_ORDER_API
            ) {
                paymentDetailsModel.amount = binding.etPaymentAmount.text.toString().toDouble()
            }

            paymentDetailsModel.paymentMode = paymentTransactionMode

            cartListResponseModel.paymentDetails = paymentDetailsModel

            cartListResponseModel.paymentOptionCheck = paymentOptionCheck

            if (binding.tlSpinnerPaymentMode.isVisible) {
                cartListResponseModel.paymentMode = paymentTransactionMode
            }
            if (binding.tlTransactionRef.isVisible) {
                cartListResponseModel.transactionRefNo =
                    binding.etTransactionRef.text.trim().toString()
            }

            if (binding.etPurchaseOrderNumber.text.toString().isNotEmpty()) {
                cartListResponseModel.purchaseOrderNumber =
                    binding.etPurchaseOrderNumber.text.toString()
            }

            val intent = Intent()
            intent.putExtra(AppConstant.CART_ITEM, cartListResponseModel)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (photoModelList.size > 1) {
                if (hasInternetConnection()) {
                    submitData()
                    //addImage()
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

            if (myLocation != null) {
                if (Utils.isMockLocation(myLocation)) {
                    val fragment =
                        MockLocationDetectedDialogFragment.getInstance(this@PaymentAgainstOrderActivity)
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
                            this@PaymentAgainstOrderActivity,
                            longitude = geoLocationLong,
                            latitude = geoLocationLat
                        ) { address ->
                            geoAddress = address
                        }

                        if (intent.hasExtra(AppConstant.CART_ITEM).not()) {
                            if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                                if (SharedPref.getInstance()
                                        .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                                ) {
                                    if ((intent.hasExtra(AppConstant.IS_TELEPHONIC_ORDER) && intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)).not()) {
                                        registerGeofenceUpdates()
                                    }
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
            } else {
                runOnUiThread {
                    showToast(resources.getString(R.string.something_went_wrong_with_location))
                    binding.btnLayout.visibility = View.VISIBLE
                    enableTouch()
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
        var paymentList =
            resources.getStringArray(R.array.payment_options)

        binding.spinnerPaymentOption.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
            null
        )
        paymentOptionListPopupWindow.anchorView = binding.tlSpinnerPaymentOption
        var adapter = ArrayAdapter(this, R.layout.single_text_view_spinner_16dp_text, paymentList)
        paymentOptionListPopupWindow.isModal = true
        paymentOptionListPopupWindow.setAdapter(adapter)
        paymentOptionListPopupWindow.setOnItemClickListener { _: AdapterView<*>?,
                                                              _: View?, position: Int, _: Long ->
            setPaymentSelection(paymentList, position)
            paymentOptionListPopupWindow.dismiss()
            isPopupShown = false
        }

        binding.spinnerPaymentOption.setOnClickListener {
            binding.spinnerPaymentOption.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_inverted_black, null),
                null
            )
            if (isPopupShown) {
                paymentOptionListPopupWindow.dismiss()
            } else {
                paymentOptionListPopupWindow.show()
            }
            isPopupShown = !isPopupShown
        }

        paymentOptionListPopupWindow.setOnDismissListener {
            binding.spinnerPaymentOption.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
            isPopupShown = false
        }

        if (!paymentTerms.isNullOrEmpty()) {
            if (paymentTerms!!.contains("Days")) {
                val days = "${paymentTerms?.extractNumber()}"
                binding.etPaymentDays.setText(days)
                setPaymentSelection(paymentList, 4)
            } else if (paymentTerms.equals("Advance")
                || paymentTerms.equals(AppConstant.FULL_PAYMENT_IN_ADVANCE_API)
            ) {
                setPaymentSelection(paymentList, 2)
            } else if (paymentTerms.equals("Partial Payment")
                || paymentTerms.equals(AppConstant.PARTIAL_PAYMENT_API)
            ) {
                setPaymentSelection(paymentList, 3)
            } else if (paymentTerms.equals("Payment On Next Order") ||
                paymentTerms.equals(AppConstant.PAYMENT_ON_NEXT_ORDER_API)
            ) {
                setPaymentSelection(paymentList, 1)
            } else {
                setPaymentSelection(paymentList, 0)
            }
        } else if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            if (cartListResponseModel.paymentOptionCheck.isNullOrEmpty().not()) {
                if (cartListResponseModel.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                    setPaymentSelection(paymentList, 4)
                    if (cartListResponseModel.remainingPaymentDays != null) {
                        binding.etPaymentDays.setText("${cartListResponseModel.remainingPaymentDays}")
                    } else {
                        binding.etPaymentDays.setText("")
                    }
                } else {
                    when (cartListResponseModel.paymentOptionCheck) {
                        PAYMENT_ON_DELIVERY_API -> {
                            setPaymentSelection(paymentList, 0)
                        }

                        PAYMENT_ON_NEXT_ORDER_API -> {
                            setPaymentSelection(paymentList, 1)
                        }
                        AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> {
                            paymentList =
                                resources.getStringArray(R.array.payment_options_for_edit)
                            adapter = ArrayAdapter(
                                this,
                                R.layout.single_text_view_spinner_16dp_text,
                                paymentList
                            )
                            paymentOptionListPopupWindow.isModal = true
                            paymentOptionListPopupWindow.setAdapter(adapter)

                            setPaymentSelection(paymentList, 0)
                        }

                        AppConstant.PARTIAL_PAYMENT_API -> {
                            paymentList =
                                resources.getStringArray(R.array.payment_options_for_edit)
                            adapter = ArrayAdapter(
                                this,
                                R.layout.single_text_view_spinner_16dp_text,
                                paymentList
                            )
                            paymentOptionListPopupWindow.isModal = true
                            paymentOptionListPopupWindow.setAdapter(adapter)

                            setPaymentSelection(paymentList, 1)


                            if (cartListResponseModel.paymentDetails != null
                                && cartListResponseModel.paymentDetails?.amount != null
                            ) {
                                binding.etPaymentAmount.setText(
                                    CalculatorHelper().formatDoubleDecimalPoint(
                                        cartListResponseModel.paymentDetails?.amount!!,
                                        AppConstant.TWO_DECIMAL_POINTS
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
                        }
                    }
                }
            }
        } else {
            setPaymentSelection(paymentList, 0)
        }

        val paymentModeList = resources.getStringArray(R.array.mode_of_payment)

        binding.spinnerPaymentMode.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
            null
        )

        paymentModeListPopupWindow.anchorView = binding.tlSpinnerPaymentMode

        val paymentModeAdapter =
            ArrayAdapter(this, R.layout.single_text_view_spinner_16dp_text, paymentModeList)
        paymentModeListPopupWindow.isModal = true
        paymentModeListPopupWindow.setAdapter(paymentModeAdapter)

        paymentModeListPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (position < paymentModeList.size) {
                binding.spinnerPaymentMode.setText(paymentModeList[position])
                paymentTransactionMode = if (position != 0) {
                    resources.getStringArray(R.array.mode_of_payment)[position]
                } else {
                    ""
                }
            }
            paymentModeListPopupWindow.dismiss()
            isPopupShown = false
        }
        binding.spinnerPaymentMode.setOnClickListener {
            if (isPopupShown) {
                paymentModeListPopupWindow.dismiss()
            } else {
                paymentModeListPopupWindow.show()
            }
            isPopupShown = !isPopupShown
        }
        paymentModeListPopupWindow.setOnDismissListener {
            isPopupShown = false
        }

        if (paymentTransactionMode.isNotEmpty()) {
            val index = resources.getStringArray(R.array.mode_of_payment)
                .findLast { it.toString() == paymentTransactionMode }
            if (index.isNullOrEmpty().not()) {
                binding.spinnerPaymentMode.setText(index)
            }
        }

        binding.etPaymentDays.filters = arrayOf<InputFilter>(InputFilterMinMax("1", "750"))

    }

    private fun initObservers() {
        progressDialog.dismiss()
        imageUploadViewModel.getCredLiveData().observe(this) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                progressDialog.dismiss()
                genericResponseModel.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (photoModelList.size < 7) {
                            multiplePicCount += 1
                            photoModelList.removeAt(photoModelList.size - 1)
                            val addedPhotoModel = AddedPhotoModel()
                            addedPhotoModel.imagePath = picMapModel.url
                            addedPhotoModel.onEditProduct = false
                            addedPhotoModel.isUploading = false
                            photoModelList.add(addedPhotoModel)
                            if (photoModelList.size < 6) {
                                photoModelList.add(null)
                            }

                        }

                        addPhotoListAdapter.notifyDataSetChanged()
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

        pdfUploadViewModel.uploadCredLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    progressDialog.dismiss()
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (photoModelList.size < 7) {
                            multiplePicCount += 1
                            photoModelList.removeAt(photoModelList.size - 1)
                            val addedPhotoModel = AddedPhotoModel()
                            addedPhotoModel.imagePath = picMapModel.url
                            addedPhotoModel.onEditProduct = false
                            addedPhotoModel.isUploading = false
                            photoModelList.add(addedPhotoModel)
                            if (photoModelList.size < 6) {
                                photoModelList.add(null)
                            }

                        }

                        addPhotoListAdapter.notifyDataSetChanged()
                        if (multiplePicCount == pics.size) {

                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()


                        }
                    }
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }


        cartViewModel.confirmOrderLiveData.observe(this) { order ->
            isDataSubmitted = true
            showToast("${order.message}")
            progressDialog.dismiss()
            if (order.error == false) {
                if (intent.hasExtra(AppConstant.IS_TELEPHONIC_ORDER) && intent.getBooleanExtra(
                        AppConstant.IS_TELEPHONIC_ORDER,
                        false
                    )
                ) {
                    openCustomerList(order.message!!)
                } else {
                    openCustomerDetails(order.message!!)
                }
            } else {
                binding.btnAdd.isEnabled = true
            }
        }

        addressViewModel.addressLiveData.observe(this) { addressListResponseModel ->
            binding.clShippingAddressParent.showView()
            binding.detailsProgressBar.hideView()
            if (addressListResponseModel.data.isNullOrEmpty().not()) {

                val defaultAddress =
                    if (::cartListResponseModel.isInitialized && cartListResponseModel.address != null && cartListResponseModel.address?.id != null) {
                        addressListResponseModel.data?.firstOrNull { it.id == cartListResponseModel.address?.id }
                    } else {
                        addressListResponseModel.data?.firstOrNull { it.isDefault }
                    }
                if (defaultAddress != null) {
                    setDefaultAddress(defaultAddress)
                } else {
                    if (addressListResponseModel.data?.isNotEmpty() == true) {
                        addressListResponseModel.data?.get(0)?.let { setDefaultAddress(it) }
                    } else {
                        binding.tvAddNewAddressText.text = getString(R.string.add_new_address)
                        binding.noAddressFound.hideView()
                        binding.tvSeeAllAddress.hideView()
                    }
                }
            } else {
                binding.tvAddNewAddressText.text = getString(R.string.add_new_address)
                binding.noAddressFound.hideView()
                binding.tvSeeAllAddress.hideView()
            }
        }
    }

    private fun setDefaultAddress(defaultAddress: CustomerAddressDataItem) {
        val address = Address().apply {
            id = defaultAddress.id
            name = defaultAddress.name
            addressLine1 = defaultAddress.addressLine1
            city = defaultAddress.city
            state = defaultAddress.state
            pincode = defaultAddress.pincode
        }
        cartListResponseModel.address = address
        this.defaultAddress = defaultAddress
        binding.tvDefaultAddressTitle.text = defaultAddress.name
        binding.tvDefaultAddress.text = buildString {
            append(defaultAddress.addressLine1)
            append(", ")
            append(defaultAddress.city)
            append(", ")
            append(defaultAddress.state)
            append(", ")
            append(defaultAddress.pincode)
        }

    }


    private fun openCustomerDetails(orderMessage: String) {
        val i = Intent(this, CustomerDetailActivity::class.java)
        i.putExtra(AppConstant.CUSTOMER_ID, customerModel?.id)
        i.putExtra(AppConstant.ORDER_STATUS, true)
        i.putExtra(AppConstant.ORDER_MESSAGE, orderMessage)
        i.putExtra(AppConstant.CLEAR_TOP_DONE, true)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun openCustomerList(orderMessage: String) {
        val i = Intent(this, ListOfCustomerActivity::class.java)
        i.putExtra(AppConstant.CUSTOMER_ID, customerModel?.id)
        i.putExtra(AppConstant.ORDER_STATUS, true)
        i.putExtra(AppConstant.ORDER_MESSAGE, orderMessage)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun submitData() {
        cartListResponseModel.comment = binding.etComment.text.trim().toString()
        cartListResponseModel.adminComment = binding.etAdminComment.text.trim().toString()
        cartListResponseModel.paymentOptionCheck = paymentOptionCheck

        if (binding.tlSpinnerPaymentMode.isVisible) {
            cartListResponseModel.paymentMode = paymentTransactionMode
        }
        if (binding.tlTransactionRef.isVisible) {
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

        cartListResponseModel.purchaseOrderNumber = binding.etPurchaseOrderNumber.text.toString()
        selectedDistributor?.let {
            cartListResponseModel.fullFilledById = selectedDistributor?.id
        }

        if (intent.hasExtra(AppConstant.IS_TELEPHONIC_ORDER) && intent.getBooleanExtra(
                AppConstant.IS_TELEPHONIC_ORDER,
                false
            )
        ) {
            cartListResponseModel.isTelephonicOrder = true
        }


        val expectedDeliveryDate =
            DateFormatHelper.convertStringToMonthAndYearFormatYYYMMDD(binding.etExpectedDeliveryDate.text.toString())
        if (expectedDeliveryDate != null) {
            cartListResponseModel.expectedDeliveryDate = expectedDeliveryDate
        }

        if (hasInternetConnection()) {
            val list = ArrayList<Int>()
            if (pics.size > 0) {
                pics.forEach {
                    list.add(it.id?:0)
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

    override fun onDeleteImage(position: Int, key: Long?) {
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
        bundle.putBoolean(AppConstant.DOCUMENT, true)
        fragment.arguments = bundle

        fragment.show(
            supportFragmentManager,
            AppConstant.IMAGE_UPLOAD_TAG
        )
    }

    override fun onEditAlreadyUploadedImage() {
    }

    override fun onCameraUpload(fileName: String?) {
       /* if (photoModelList.size < 7) {
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
        }*/
        if (photoModelList.size < 6) {
            progressDialog.show()
            multiplePicCount += 1
            lifecycleScope.launch {
                val compressedImageFile = Compressor.compress(
                    this@PaymentAgainstOrderActivity,
                    File(fileName!!)
                ) {
                    quality(30)
                    resolution(512, 512)
                    size(197_152)
                }
                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onGallerySingleUpload(fileName: String?) {
        if (photoModelList.size < 6) {
            progressDialog.show()
            multiplePicCount += 1
            lifecycleScope.launch {
                val compressedImageFile = Compressor.compress(
                    this@PaymentAgainstOrderActivity,
                    File(fileName!!)
                ) {
                    quality(30)
                    resolution(512, 512)
                    size(197_152)
                }
                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
        /*if (photoModelList.size < 7) {
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
        }*/
    }
    override fun onGalleryMultipleUpload(fileList: List<String>?) {

        if (fileList != null && photoModelList.size < 6 && photoModelList.size + fileList.size <= 6) {
            val uploadedList = java.util.ArrayList<String>()
            if (fileList.size > 5) {
                for (i in 0..5) {
                    uploadedList.add(fileList[i])
                }
            } else if (fileList.size > 1) {
                uploadedList.addAll(fileList)
            }
           progressDialog.show()

            multiplePicCount += fileList.size

            for (i in fileList.indices) {
                lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(
                        this@PaymentAgainstOrderActivity,
                        File(fileList[i])
                    ) {
                        quality(30)
                        resolution(512, 512)
                        size(197_152)
                    }
                    imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

   /* override fun onGalleryMultipleUpload(fileList: List<String>?) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
               *//* val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                addedPhotoModel.isDisplayPicEnable = false
                photoModelList.add(addedPhotoModel)*//*
                lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(
                        this@PaymentAgainstOrderActivity,
                        File(path)
                    ) {
                        quality(30)
                        resolution(512, 512)
                        size(197_152)
                    }
                    imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                }
            }



            multiplePicCount += fileList.size

        } else {
            Toast.makeText(
                this,
                getString(R.string.upload_max_six_images),
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    override fun onUploadPdf() {
        super.onUploadPdf()
        onAddPdf()
    }


    private fun onAddPdf() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        uploadPdfActivityResultLauncher.launch(pdfIntent)
    }

    @SuppressLint("SuspiciousIndentation")
    private var uploadPdfActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {


            if (photoModelList.size < 7) {
                progressDialog.show()
                multiplePicCount += 1
                var isDocAvailable = false
                photoModelList.forEach {
                    if (it != null && it.type == AppConstant.DOCUMENT && !it.onEditProduct
                        && !it.isUploading
                    ) {
                        isDocAvailable = true
                        it.isUploading = true

                    }
                }
                val uri = result.data!!.data
                val path: String = FileUtils.getPdfFile(this, uri!!).absolutePath
                pdfUploadViewModel.uploadCredentials(path)

            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
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
        onBackPressedDispatcher.onBackPressed()
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (isDataSubmitted) {
            val i = Intent(this, SalesMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
        finish()
        super.onBackPressed()
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
            if ((intent.hasExtra(AppConstant.IS_TELEPHONIC_ORDER) && intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)).not()) {
                registerGeofenceUpdates()
            }
        } else {
            runOnUiThread {
                binding.btnLayout.visibility = View.VISIBLE
                enableTouch()
            }
        }
    }


    private fun setPaymentSelection(paymentList: Array<String>, position: Int) {
        if (position < paymentList.size) {
            val selectedOptions = paymentList[position]

            paymentTerms = selectedOptions

            if (cartListResponseModel.transactionRefNo.isNullOrEmpty().not()) {
                binding.etTransactionRef.setText("${cartListResponseModel.transactionRefNo}")
            } else if (cartListResponseModel.paymentDetails?.transactionRefNo.isNullOrEmpty()
                    .not()
            ) {
                binding.etTransactionRef.setText("${cartListResponseModel.paymentDetails?.transactionRefNo}")
            }

            when (selectedOptions.lowercase()) {
                AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase() -> {
                    binding.tlHdPaymentAmount.showView()
                    binding.tlSpinnerPaymentMode.showView()
                    binding.tlTransactionRef.showView()
                    binding.groupPaymentDays.hideView()
                    binding.tvTotalPaymentAmountLabel.hideView()
                    binding.tvTotalPaymentAmount.hideView()
                    binding.tvTotalPaymentAmount.text =
                        CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(paymentAmount)
                    binding.etPaymentAmount.setText(
                        CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                            paymentAmount
                        )
                    )
                    binding.etPaymentAmount.isEnabled = false
                    paymentOptionCheck = AppConstant.FULL_PAYMENT_IN_ADVANCE_API

                }

                AppConstant.PARTIAL_PAYMENT.lowercase() -> {
                    binding.tlHdPaymentAmount.showView()
                    binding.tlSpinnerPaymentMode.showView()
                    binding.tlTransactionRef.showView()
                    binding.groupPaymentDays.showView()
                    binding.tvTotalPaymentAmountLabel.showView()
                    binding.tvTotalPaymentAmount.showView()

                    if (intent.hasExtra(AppConstant.ORDER_EDIT)
                            .not() && cartListResponseModel.paymentDetails != null
                        && cartListResponseModel.paymentDetails?.amount != null
                    ) {
                        binding.tvTotalPaymentAmount.text =
                            CalculatorHelper().formatDoubleDecimalPoint(
                                cartListResponseModel.paymentDetails?.amount!!,
                                AppConstant.TWO_DECIMAL_POINTS
                            )
                    } else if (cartListResponseModel.paymentDetails?.amount != null) {
                        binding.tvTotalPaymentAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                                "" + cartListResponseModel.paymentDetails?.amount!!
                            )
                    } else {
                        binding.tvTotalPaymentAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmountWithoutSymbol(
                                paymentAmount
                            )
                    }
                    binding.etPaymentAmount.isEnabled = true

                    paymentOptionCheck = AppConstant.PARTIAL_PAYMENT_API

                }

                PAYMENT_ON_DELIVERY.lowercase() -> {
                    binding.tlHdPaymentAmount.hideView()
                    binding.tlSpinnerPaymentMode.hideView()
                    binding.tlTransactionRef.hideView()
                    binding.groupPaymentDays.hideView()
                    binding.tvTotalPaymentAmountLabel.hideView()
                    binding.tvTotalPaymentAmount.hideView()
                    binding.etPaymentAmount.setText("")

                    paymentOptionCheck = PAYMENT_ON_DELIVERY_API

                }

                PAYMENT_ON_NEXT_ORDER.lowercase() -> {
                    binding.tlHdPaymentAmount.hideView()
                    binding.tlSpinnerPaymentMode.hideView()
                    binding.tlTransactionRef.hideView()
                    binding.groupPaymentDays.hideView()
                    binding.tvTotalPaymentAmountLabel.hideView()
                    binding.tvTotalPaymentAmount.hideView()
                    binding.etPaymentAmount.setText("")

                    paymentOptionCheck = AppConstant.PAYMENT_ON_NEXT_ORDER_API
                }

                AppConstant.CREDIT_DAYS.lowercase() -> {
                    binding.tlHdPaymentAmount.hideView()
                    binding.tlSpinnerPaymentMode.hideView()
                    binding.tlTransactionRef.hideView()
                    binding.groupPaymentDays.showView()
                    binding.tvTotalPaymentAmountLabel.hideView()
                    binding.tvTotalPaymentAmount.hideView()
                    binding.etPaymentAmount.setText("")

                    paymentOptionCheck = AppConstant.CREDIT_DAYS_API
                }

                else -> {
                    paymentOptionCheck = ""
                }
            }
            binding.spinnerPaymentOption.setText(selectedOptions)
        }
    }

}