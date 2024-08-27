package com.app.rupyz.sales.cart

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCartBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharePrefConstant.CART_MODEL
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.StringModificationUtils
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.sales.address.ConfirmAddressActivity
import com.app.rupyz.sales.cart.CartItemsDiscountListAdapter.CartDiscountDeleteListener
import com.app.rupyz.sales.cart.CartItemsListAdapter.OnCartActionListener
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.orders.OrderViewModel
import com.app.rupyz.sales.orders.PaymentAgainstOrderActivity
import com.app.rupyz.sales.product.ProductViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

class CartActivity : BaseActivity(), OnCartActionListener, CartDiscountDeleteListener,
    AddDiscountBottomSheetDialogFragment.IAddDiscountListener,
    AddDeliveryChargesBottomSheetDialogFragment.IAddDeliveryChargesListener,
    ProductImageViewPagerAdapter.ProductImageClickListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener,
    LocationPermissionUtils.ILocationPermissionListener {
    private lateinit var binding: ActivityCartBinding

    private lateinit var cartItemsListAdapter: CartItemsListAdapter
    private lateinit var cartItemsDiscountListAdapter: CartItemsDiscountListAdapter
    private lateinit var cartItemsOthersChargesAdapter: CartItemsDiscountListAdapter
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter

    private var cartItemList = ArrayList<CartItem>()
    private var cartItemDiscountList = ArrayList<CartItemDiscountModel>()
    private var cartItemOthersChargesList = ArrayList<CartItemDiscountModel>()
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val orderImageLisForUpload: ArrayList<PicMapModel> = ArrayList()

    private var cartListResponseModel: OrderData? = null

    private val orderViewModel: OrderViewModel by viewModels()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()

    private var totalCartAmount: Double = 0.0
    private var orderAmount: Double = 0.0
    private var totalGstAmount: Double = 0.0
    private var totalOrderAmountWithoutGst = 0.0
    private var totalGstAmountForFinalCalculation = 0.0
    private var totalDiscountAmount: Double = 0.0
    private var totalDeliverCharges: Double = 0.0
    private var totalOrderCharges: Double = 0.0
    private var customerId: Int = -1
    private var multiplePicCount = 0

    private var isZeroQuantityExist: Boolean = false
    private var emptyCartItem: CartItem? = null

    //    private var orderData: OrderData? = null
    private lateinit var progressDialog: ProgressDialog

    private var customerModel: CustomerData? = null


    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null
    private var selectedDistributor: CustomerData? = null
    private var paymentTerms: String? = null
    private lateinit var locationPermissionUtils: LocationPermissionUtils

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionUtils = LocationPermissionUtils(this, this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("uploading ...")
        progressDialog.setCancelable(false)

        initApiObservers()
        initRecyclerView()

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }

        if (intent.hasExtra(AppConstant.PAYMENT_INFO)) {
            paymentTerms = intent.getStringExtra(AppConstant.PAYMENT_INFO)
        }

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

        if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            getUserCurrentLocation()

            if (hasInternetConnection()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLayout.visibility = View.GONE
                binding.mainContent.visibility = View.GONE

                val productId = ArrayList<Int?>()
                cartListResponseModel?.items?.let { list ->
                    list.forEach { item ->
                        productId.add(item.id)
                    }
                }

                productViewModel.getProductListForOrderEdit(productId)
            } else {
                initProductData(ArrayList())
            }
        } else {
            binding.groupNotes.visibility = View.GONE
            initSharedPrefObservers()
        }


        initClickListener()
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
                if (isFinishing.not() && supportFragmentManager.isStateSaved.not()) {
                    val fragment = MockLocationDetectedDialogFragment.getInstance(null)
                    fragment.isCancelable = false
                    fragment.show(
                        supportFragmentManager, MockLocationDetectedDialogFragment::class.java.name
                    )
                }
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude

                    if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false).not()) {
                        showStartDayDialog()
                    }
                }
            }
        }
    }

    private fun showStartDayDialog() {
        if (fragment == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
                this, geoLocationLat, geoLocationLong
            )
        }
        if (fragment?.isVisible == false) {
            fragment?.show(
                supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name
            )
        }
    }

    private fun initClickListener() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvCancel.setOnClickListener { finish() }

        binding.tvAddDiscount.setOnClickListener {
            validateDiscount(false, null, null)
        }

        binding.tvAddMoreDiscount.setOnClickListener {
            validateDiscount(false, null, null)
        }

        binding.tvAddDeliveryCharges.setOnClickListener {
            validateDeliveryCharges(false, null, null)
        }
        binding.tvAddMoreOthersCharges.setOnClickListener {
            validateDeliveryCharges(false, null, null)
        }

        binding.mainContent.setOnClickListener {
            Utils.hideKeyboard(this)
        }

        binding.clCartItem.setOnClickListener {
            Utils.hideKeyboard(this)
        }

        binding.tvCheckout.setOnClickListener {
            cartListResponseModel?.items?.forEach {
                if (it.qty == 0.0) {
                    Toast.makeText(this, "Please enter some qty for ${it.name}", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }

            cartListResponseModel?.deliveryCharges = totalDeliverCharges
            cartListResponseModel?.discountDetails = cartItemDiscountList
            cartListResponseModel?.chargesDetails = cartItemOthersChargesList
            cartListResponseModel?.customerId = customerId

            if (binding.tvNote.text.toString().isNotEmpty()) {
                cartListResponseModel?.comment = binding.tvNote.text.toString()
            }

            if (binding.tvAdminNote.text.toString().isNotEmpty()) {
                cartListResponseModel?.adminComment = binding.tvAdminNote.text.toString()
            }

            if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                verifyImage()
            } else {
                startActivity(
                    Intent(
                        this, PaymentAgainstOrderActivity::class.java
                    ).putExtra(AppConstant.CART_ITEM, cartListResponseModel)
                        .putExtra(AppConstant.CUSTOMER, customerModel)
                        .putExtra(AppConstant.SELECTED_DISTRIBUTOR, selectedDistributor)
                        .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)
                        .putExtra(
                            AppConstant.IS_TELEPHONIC_ORDER,
                            intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)
                        )

                )
            }
        }
    }

    private fun verifyImage() {
        progressDialog.show()
        var isEditImageAvailable = false
        if (cartListResponseModel?.orderImagesInfo.isNullOrEmpty().not()) {
            cartListResponseModel?.orderImagesInfo?.let { picList ->
                multiplePicCount = picList.size
                picList.forEach {
                    if (it.id != null) {
                        orderImageLisForUpload.add(it)
                    } else {
                        isEditImageAvailable = true
                        lifecycleScope.launch {
                            it.url?.let { url ->
                                val compressedImageFile =
                                    Compressor.compress(this@CartActivity, File(url)) {
                                        quality(30)
                                        resolution(512, 512)
                                        size(197_152)
                                    }
                                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                            }
                        }
                    }
                }
            }
        }
        if (!isEditImageAvailable) {
            submitData()
        }
    }

    private fun submitData() {

        val list = ArrayList<Int>()
        if (orderImageLisForUpload.isEmpty().not()) {
            orderImageLisForUpload.forEach {
                list.add(it.id!!)
            }
        }

        cartListResponseModel?.orderImages = list

        cartListResponseModel?.deviceInformation = getDeviceInformation()
        cartListResponseModel?.batteryPercent = getBatteryInformation().first
        cartListResponseModel?.batteryOptimisation = isBatteryOptimizationEnabled()
        cartListResponseModel?.locationPermission = isGpsEnabled()

        if (hasInternetConnection().not()) {
            cartListResponseModel?.customer = customerModel
            cartListResponseModel?.customerLevel = customerModel?.customerLevel
        }


        if (cartListResponseModel?.fullFilledBy != null) {
            cartListResponseModel?.fullFilledById = cartListResponseModel?.fullFilledBy!!.id
        }

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
               val  cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null){
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

            }
        }



        cartListResponseModel?.id?.let { id ->
            orderViewModel.updateOrder(
                id, cartListResponseModel, hasInternetConnection()
            )
        }

    }

    @SuppressLint("SetTextI18n")
    var addProductResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            cartItemList.clear()
            initSharedPrefObservers()
        }
    }

    @SuppressLint("SetTextI18n")
    var paymentAgainstOrderResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            if (result.data?.hasExtra(AppConstant.CART_ITEM)!!) {
                val refactorCartModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(AppConstant.CART_ITEM, OrderData::class.java)
                } else {
                    result.data?.getParcelableExtra(AppConstant.CART_ITEM)
                }

                if (refactorCartModel?.transactionRefNo.isNullOrEmpty().not()) {
                    binding.tvTransactionRef.text =
                        "Transaction Ref : ${refactorCartModel?.transactionRefNo}"
                    binding.tvTransactionRef.visibility = View.VISIBLE
                    cartListResponseModel?.transactionRefNo = refactorCartModel?.transactionRefNo
                } else {
                    binding.tvTransactionRef.visibility = View.GONE
                }

                if (refactorCartModel?.paymentDetails?.amount != null) {
                    binding.tvPaymentAmount.text = CalculatorHelper().convertLargeAmount(
                        refactorCartModel.paymentDetails?.amount!!, AppConstant.TWO_DECIMAL_POINTS
                    )
                    binding.tvPaymentAmount.visibility = View.VISIBLE
                } else {
                    binding.tvPaymentAmount.visibility = View.GONE
                }

                binding.tvPaymentMode.text =
                    "Payment Mode : ${refactorCartModel?.paymentMode ?: ""}"

                if (refactorCartModel?.purchaseOrderNumber.isNullOrEmpty().not()) {
                    cartListResponseModel?.purchaseOrderNumber =
                        refactorCartModel?.purchaseOrderNumber
                }
                if (refactorCartModel?.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                    binding.tvPaymentTerms.text =
                        "Payment Terms : Credit Days - " + refactorCartModel.remainingPaymentDays
                } else {
                    when (refactorCartModel?.paymentOptionCheck) {
                        AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> binding.tvPaymentTerms.text =
                            "Payment Terms : ${
                                StringModificationUtils.convertCamelCase(AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase())
                            }"

                        AppConstant.PARTIAL_PAYMENT_API -> binding.tvPaymentTerms.text =
                            "Payment Terms : ${
                                StringModificationUtils.convertCamelCase(AppConstant.PARTIAL_PAYMENT.lowercase())
                            }"

                        AppConstant.PAYMENT_ON_DELIVERY_API -> binding.tvPaymentTerms.text =
                            "Payment Terms : ${
                                StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_DELIVERY.lowercase())
                            }"
                    }
                }

                val paymentDetails = PaymentDetailsModel()
                paymentDetails.paymentMode = refactorCartModel?.paymentMode
                paymentDetails.amount = refactorCartModel?.paymentDetails?.amount
                paymentDetails.transactionRefNo = refactorCartModel?.transactionRefNo
                paymentDetails.paymentMode = refactorCartModel?.paymentMode

                cartListResponseModel?.remainingPaymentDays =
                    refactorCartModel?.remainingPaymentDays
                cartListResponseModel?.paymentOptionCheck = refactorCartModel?.paymentOptionCheck
                cartListResponseModel?.paymentMode = refactorCartModel?.paymentMode
                cartListResponseModel?.paymentDetails = paymentDetails
                cartListResponseModel?.remainingPaymentDays =
                    refactorCartModel?.remainingPaymentDays

                if (refactorCartModel?.comment.isNullOrEmpty().not()) {
                    binding.tvNote.text = refactorCartModel?.comment.toString()
                    binding.groupNotes.visibility = View.VISIBLE
                    cartListResponseModel?.comment = refactorCartModel?.comment
                } else {
                    binding.groupNotes.visibility = View.GONE
                }

                if (refactorCartModel?.adminComment.isNullOrEmpty().not()) {
                    binding.tvAdminNote.text = refactorCartModel?.adminComment.toString()
                    binding.groupAdminNotes.visibility = View.VISIBLE
                    cartListResponseModel?.adminComment = refactorCartModel?.adminComment
                } else {
                    binding.groupAdminNotes.visibility = View.GONE
                }

                if (refactorCartModel?.orderImagesInfo.isNullOrEmpty().not()) {
                    cartListResponseModel?.orderImagesInfo = refactorCartModel?.orderImagesInfo
                    binding.groupImages.visibility = View.VISIBLE
                    pics.clear()
                    pics.addAll(refactorCartModel?.orderImagesInfo!!)
                    addPhotoListAdapter.notifyDataSetChanged()
                } else {
                    binding.groupImages.visibility = View.GONE
                }

                if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
                    val response = SharedPref.getInstance().getString(CART_MODEL)
                    if (!response.equals("")) {
                        val  cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                        if (cartResponseModel?.fullFilledBy != null){
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

                    }
                }



                SharedPref.getInstance().putModelClass(CART_MODEL, cartListResponseModel)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    var changeDeliveryAddressResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {

            if (result.data?.hasExtra(AppConstant.ORDER_ADDRESS_ID)!!) {
                val model: CustomerAddressDataItem? =
                    result.data?.getParcelableExtra(AppConstant.ORDER_ADDRESS_ID)

                val address = Address()
                address.id = model?.id
                address.name = model?.name
                address.addressLine1 = model?.addressLine1
                address.city = model?.city
                address.state = model?.state
                address.pincode = model?.pincode

                cartListResponseModel?.address = address

                if (model?.name != null) {
                    binding.tvAddressType.text = model.name + " "
                }
                if (model?.addressLine1 != null) {
                    binding.tvAddressTitle.text = model.addressLine1
                }

                binding.tvAddressLine.text = "${model?.city}, ${model?.state}, ${model?.pincode}"

            }
        }
    }

    private fun validateDiscount(
        editDiscount: Boolean, model: CartItemDiscountModel?, position: Int?
    ) {
        val fragment = AddDiscountBottomSheetDialogFragment(
            this, editDiscount, model, position
        )
        fragment.show(supportFragmentManager, AppConstant.ADD_DISCOUNT)
    }

    private fun validateDeliveryCharges(
        editDelivery: Boolean, charges: CartItemDiscountModel?, position: Int?
    ) {
        val fragment = AddDeliveryChargesBottomSheetDialogFragment(
            this, editDelivery, charges, position
        )
        fragment.show(supportFragmentManager, AppConstant.ADD_DELIVERY_CHARGES)
    }


    @SuppressLint("SetTextI18n")
    private fun manageOrderSummery() {
        isZeroQuantityExist = false
        orderAmount = 0.0
        totalGstAmount = 0.0
        totalOrderCharges = 0.0

        cartItemList.forEach {

            val qty = if (it.selectedPackagingLevel != null) {
                it.qty!! * it.selectedPackagingLevel?.size!!
            } else if (it.packagingLevel.isNullOrEmpty().not()) {
                it.qty!! * it.packagingLevel!![0].size!!
            } else {
                it.qty!!
            }

            orderAmount = orderAmount.plus(
                CalculatorHelper().calculatePriceWithoutGst(
                    qty,
                    (it.priceAfterDiscount ?: it.price ?: 1.0),
                    it.gst!!,
                    it.gst_exclusive!!,
                    AppConstant.FOUR_DECIMAL_POINTS
                )
            )

            totalGstAmount = totalGstAmount.plus(
                CalculatorHelper().calculateGstPercentAmount(
                    qty,
                    (it.priceAfterDiscount ?: it.price ?: 1.0),
                    it.gst!!,
                    it.gst_exclusive!!,
                    AppConstant.TWO_DECIMAL_POINTS
                ).second
            )
        }

        binding.tvProductCount.text = "You have " + cartItemList.size + " items in your cart!"

        cartItemList.forEach { cartItem ->
            if (cartItem.qty == 0.0) {
                isZeroQuantityExist = true
                emptyCartItem = cartItem
                return@forEach
            }
        }

        if (cartItemList.size == 0) {
            binding.mainContent.visibility = View.GONE
            binding.btnLayout.visibility = View.GONE
            binding.clEmptyCart.visibility = View.VISIBLE
        }

        manageDiscount()
    }


    private fun manageDiscount() {

        totalOrderAmountWithoutGst = orderAmount
        totalGstAmountForFinalCalculation = totalGstAmount

        totalDeliverCharges = 0.0
        totalDiscountAmount = 0.0

        if (cartItemDiscountList.size > 0) {
            cartItemDiscountList.forEach { discountItem ->
                if (discountItem.type == AppConstant.DISCOUNT_TYPE_PERCENT) {
                    val discountAmount = (orderAmount * discountItem.value!!) / 100
                    discountItem.calculated_value = discountAmount
                    totalDiscountAmount += discountAmount

                } else if (discountItem.value != null) {
                    val discountAmount = discountItem.value!!.toDouble()
                    discountItem.calculated_value = discountAmount
                    totalDiscountAmount += discountAmount
                }
            }

            val priceGstPair = splitAmountByRatio(totalDiscountAmount)

            totalOrderAmountWithoutGst = priceGstPair.second
            totalGstAmountForFinalCalculation = priceGstPair.first

            binding.tvDiscountAmount.visibility = View.VISIBLE
            binding.tvDiscountAmount.text = CalculatorHelper().convertCommaSeparatedAmount(
                totalDiscountAmount, AppConstant.TWO_DECIMAL_POINTS
            )

            cartListResponseModel?.discountDetails = cartItemDiscountList

        } else {
            totalDiscountAmount = 0.0
        }

        cartItemsDiscountListAdapter.notifyDataSetChanged()

        manageOthersCharges()

    }

    private fun manageOthersCharges() {
        totalDeliverCharges = 0.0
        if (cartItemOthersChargesList.size > 0) {
            cartItemOthersChargesList.forEach { discountItem ->
                if (discountItem.value != null) {
                    val discountAmount = discountItem.value!!.toDouble()
                    totalDeliverCharges += discountAmount
                    discountItem.calculated_value = discountAmount
                }
            }

            cartListResponseModel?.chargesDetails = cartItemOthersChargesList
        } else {
            totalDeliverCharges = 0.0
        }

        cartItemsOthersChargesAdapter.notifyDataSetChanged()

        manageTotalAmount()

    }

    private fun manageTotalAmount() {

        binding.tvOrderTotalAmount.text = CalculatorHelper().convertCommaSeparatedAmount(
            totalOrderAmountWithoutGst, AppConstant.TWO_DECIMAL_POINTS
        )
        binding.tvOrderTotalGst.text = CalculatorHelper().convertCommaSeparatedAmount(
            totalGstAmountForFinalCalculation, AppConstant.TWO_DECIMAL_POINTS
        )

        totalCartAmount = totalOrderAmountWithoutGst.plus(totalDeliverCharges)
            .plus(totalGstAmountForFinalCalculation)

        cartListResponseModel?.amount =
            CalculatorHelper().roundDecimalPoint(totalCartAmount, AppConstant.TWO_DECIMAL_POINTS)
        cartListResponseModel?.paymentAmount =
            CalculatorHelper().roundDecimalPoint(totalCartAmount, AppConstant.TWO_DECIMAL_POINTS)

        binding.tvOrderTotalAmountInc.text = CalculatorHelper().convertCommaSeparatedAmount(
            totalCartAmount, AppConstant.TWO_DECIMAL_POINTS
        )

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
                val  cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null){
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

            }
        }

        SharedPref.getInstance().putModelClass(CART_MODEL, cartListResponseModel)
    }

    private fun splitAmountByRatio(
        discountAmount: Double
    ): Pair<Double, Double> {

        var totalGstAmount = 0.0
        var discountedTotalGstAmountTotalItem = 0.0

        var discountRatioAmountSingleItem = 0.0
        var discountedTotalGstAmountSingleItem = 0.0
        var gstPercentage = 0.0


        var priceWithoutGstForTotalItem = 0.0

        cartItemList.forEach {

            val qty = if (it.selectedPackagingLevel != null) {
                it.qty!! * it.selectedPackagingLevel?.size!!
            } else if (it.packagingLevel.isNullOrEmpty().not()) {
                it.qty!! * it.packagingLevel!![0].size!!
            } else {
                it.qty!!
            }

            val amountWithoutGst = (CalculatorHelper().calculatePriceWithoutGst(
                qty,
                it.priceAfterDiscount ?: it.price ?: 1.0,
                it.gst!!,
                it.gst_exclusive!!,
                AppConstant.FOUR_DECIMAL_POINTS
            ))

            priceWithoutGstForTotalItem += amountWithoutGst
        }

        cartItemList.forEach {


            val qty = if (it.selectedPackagingLevel != null) {
                it.qty!! * it.selectedPackagingLevel?.size!!
            } else if (it.packagingLevel.isNullOrEmpty().not()) {
                it.qty!! * it.packagingLevel!![0].size!!
            } else {
                it.qty!!
            }

            val amountWithoutGst = (CalculatorHelper().calculatePriceWithoutGst(
                qty,
                it.priceAfterDiscount ?: it.price ?: 1.0,
                it.gst!!,
                it.gst_exclusive!!,
                AppConstant.FOUR_DECIMAL_POINTS
            ))

            discountRatioAmountSingleItem =
                discountAmount * amountWithoutGst / priceWithoutGstForTotalItem

            discountedTotalGstAmountSingleItem = amountWithoutGst - discountRatioAmountSingleItem

            gstPercentage = discountedTotalGstAmountSingleItem * it.gst!! / 100

            totalGstAmount += gstPercentage

            discountedTotalGstAmountTotalItem += discountedTotalGstAmountSingleItem
        }

        return Pair(totalGstAmount, discountedTotalGstAmountTotalItem)
    }

    @SuppressLint("SetTextI18n")
    private fun initSharedPrefObservers() {
        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)

            if (!response.equals("")) {
                binding.mainContent.visibility = View.VISIBLE
                binding.clEmptyCart.visibility = View.GONE

                cartListResponseModel = Gson().fromJson(response, OrderData::class.java)

                cartListResponseModel?.customer = customerModel
                if (cartListResponseModel?.customerId == customerId) {

                    binding.tvCompanyName.text = cartListResponseModel?.customer?.name

                    if (cartListResponseModel?.items.isNullOrEmpty().not()) {
                        cartItemList.addAll(cartListResponseModel?.items!!)
                        cartItemsListAdapter.notifyDataSetChanged()
                    }

                    if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                        manageProductVariantCount()
                    }
                }
            }
        }

        if (cartListResponseModel?.address?.id != null) {
            binding.groupDeliverAddress.visibility = View.VISIBLE

            if (cartListResponseModel?.address?.name != null) {
                binding.tvAddressType.text = cartListResponseModel?.address?.name + " "
            }
            if (cartListResponseModel?.address?.addressLine1 != null) {
                binding.tvAddressTitle.text = cartListResponseModel?.address?.addressLine1
            }

            binding.tvAddressLine.text =
                "${cartListResponseModel?.address?.city}," + " ${cartListResponseModel?.address?.state}," + " ${cartListResponseModel?.address?.pincode}"

        } else {
            binding.groupDeliverAddress.visibility = View.GONE
        }

        if (cartListResponseModel?.paymentDetails != null) {
            cartListResponseModel?.paymentDetails?.let {
                binding.tvPaymentId.text = "Payment ID : ${it.paymentNumber ?: ""}"

                if (it.amount != null) {
                    binding.tvPaymentAmount.text = CalculatorHelper().convertLargeAmount(
                        it.amount!!, AppConstant.TWO_DECIMAL_POINTS
                    )
                    binding.tvPaymentAmount.visibility = View.VISIBLE
                } else {
                    binding.tvPaymentAmount.visibility = View.GONE
                }

                binding.tvPaymentMode.text = "Payment Mode : ${it.paymentMode ?: ""}"

                cartListResponseModel?.paymentMode = it.paymentMode

                if (it.transactionRefNo.isNullOrEmpty().not()) {
                    binding.tvTransactionRef.text = "Transaction Ref : ${it.transactionRefNo}"
                    binding.tvTransactionRef.visibility = View.VISIBLE
                } else {
                    binding.tvTransactionRef.visibility = View.GONE
                }

                if (cartListResponseModel?.paymentOptionCheck != null) {
                    if (cartListResponseModel?.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                        binding.tvPaymentTerms.text =
                            "Payment Terms : Credit Days - " + cartListResponseModel?.remainingPaymentDays
                    } else {
                        when (cartListResponseModel?.paymentOptionCheck) {
                            AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> binding.tvPaymentTerms.text =
                                "Payment Terms : ${
                                    StringModificationUtils.convertCamelCase(AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase())
                                }"

                            AppConstant.PARTIAL_PAYMENT_API -> binding.tvPaymentTerms.text =
                                "Payment Terms : ${
                                    StringModificationUtils.convertCamelCase(AppConstant.PARTIAL_PAYMENT.lowercase())
                                }"

                            AppConstant.PAYMENT_ON_DELIVERY_API -> binding.tvPaymentTerms.text =
                                "Payment Terms : ${
                                    StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_DELIVERY.lowercase())
                                }"

                            AppConstant.PAYMENT_ON_NEXT_ORDER_API -> binding.tvPaymentTerms.text =
                                "Payment Terms : ${
                                    StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_NEXT_ORDER.lowercase())
                                }"
                        }
                    }
                }
            }
        }

        if (cartListResponseModel?.discountDetails.isNullOrEmpty().not()) {
            cartItemDiscountList.clear()
            cartItemDiscountList.addAll(cartListResponseModel?.discountDetails!!)
            cartItemsDiscountListAdapter.notifyDataSetChanged()
            if (cartItemDiscountList.size > 0) {
                binding.tvAddDiscount.visibility = View.GONE
                binding.groupAddedDiscount.visibility = View.VISIBLE
            }

            if (cartItemDiscountList.size >= 5) {
                binding.tvAddMoreDiscount.visibility = View.GONE
            } else {
                binding.tvAddMoreDiscount.visibility = View.VISIBLE
            }

            manageOrderSummery()
        }

        if (cartListResponseModel?.chargesDetails.isNullOrEmpty().not()) {
            cartItemOthersChargesList.clear()
            cartItemOthersChargesList.addAll(cartListResponseModel?.chargesDetails!!)
            cartItemsOthersChargesAdapter.notifyDataSetChanged()

            if (cartItemOthersChargesList.size > 0) {
                binding.tvAddDeliveryCharges.visibility = View.GONE
                binding.groupAddedCharges.visibility = View.VISIBLE
            }

            if (cartItemOthersChargesList.size >= 5) {
                binding.tvAddMoreOthersCharges.visibility = View.GONE
            } else {
                binding.tvAddMoreOthersCharges.visibility = View.VISIBLE
            }

            totalDeliverCharges = 0.0
            manageOthersCharges()
        }
        else if (cartListResponseModel?.deliveryCharges != null
            && cartListResponseModel?.deliveryCharges!!.roundToInt() != 0
        ) {
            binding.tvAddDeliveryCharges.visibility = View.GONE
            val chargesModel = CartItemDiscountModel()
            chargesModel.name = "Total Charges"
            chargesModel.value = cartListResponseModel?.deliveryCharges

            cartItemOthersChargesList.add(chargesModel)
            cartItemsOthersChargesAdapter.notifyItemInserted(0)

            binding.groupAddedCharges.visibility = View.VISIBLE
            binding.tvAddMoreOthersCharges.visibility = View.VISIBLE
            totalDeliverCharges = 0.0
            manageOthersCharges()
        }


        if (cartListResponseModel?.comment != null && cartListResponseModel?.comment?.isNotEmpty()!!) {
            binding.tvNote.text = cartListResponseModel?.comment.toString()
            binding.groupNotes.visibility = View.VISIBLE
        } else {
            binding.groupNotes.visibility = View.GONE
        }

        if (cartListResponseModel?.orderImagesInfo.isNullOrEmpty().not()) {
            binding.groupImages.visibility = View.VISIBLE
            pics.clear()
            pics.addAll(cartListResponseModel?.orderImagesInfo!!)
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            binding.groupImages.visibility = View.GONE
        }

        if (cartListResponseModel?.adminComment != null && cartListResponseModel?.adminComment?.isNotEmpty()!!) {
            binding.tvAdminNote.text = cartListResponseModel?.adminComment.toString()
            binding.groupAdminNotes.visibility = View.VISIBLE
        } else {
            binding.groupAdminNotes.visibility = View.GONE
        }

        binding.ivEditDelivery.setOnClickListener {
            changeDeliveryAddressResultLauncher.launch(
                Intent(
                    this, ConfirmAddressActivity::class.java
                ).putExtra(AppConstant.ORDER_ADDRESS_ID, cartListResponseModel?.address?.id)
                    .putExtra(AppConstant.CUSTOMER_ID, cartListResponseModel?.customer?.id)
                    .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)

            )
        }

        binding.ivEditPaymentAgainstOrder.setOnClickListener {
            paymentAgainstOrderResultLauncher.launch(
                Intent(
                    this, PaymentAgainstOrderActivity::class.java
                ).putExtra(AppConstant.CART_ITEM, cartListResponseModel)
                    .putExtra(AppConstant.ORDER_EDIT, true)
                    .putExtra(
                        AppConstant.IS_TELEPHONIC_ORDER,
                        intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)
                    )
                    .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)

            )
        }


        binding.tvAddProduct.setOnClickListener {
            addProductResultLauncher.launch(
                Intent(
                    this,
                    CreateNewOrderForCustomerActivity::class.java
                ).putExtra(AppConstant.ORDER_EDIT, true)
                    .putExtra(AppConstant.CUSTOMER_ID, cartListResponseModel?.customer?.id)
                    .putExtra(AppConstant.CUSTOMER, cartListResponseModel?.customer)
                    .putExtra(AppConstant.CUSTOMER_NAME, cartListResponseModel?.customer?.name)
                    .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)

            )
        }


        if (cartItemList.isEmpty()) {
            binding.mainContent.visibility = View.GONE
            binding.btnLayout.visibility = View.GONE
            binding.clEmptyCart.visibility = View.VISIBLE
        }
    }

    private fun manageProductVariantCount() {
        val idCounterMap: HashMap<Int?, Int?> = HashMap()

        cartItemList.forEach { item ->
            if (item.primaryProduct != null) {
                if (idCounterMap.containsKey(item.primaryProduct)) {
                    idCounterMap[item.primaryProduct] = idCounterMap[item.primaryProduct]!! + 1
                } else {
                    idCounterMap[item.primaryProduct] = 1
                }
            }
        }

        cartItemList.forEach {
            if (it.primaryProduct != null) {
                it.variantSize = idCounterMap[it.primaryProduct] ?: 0
            }
        }

        cartListResponseModel?.items = cartItemList

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
                val  cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null){
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

            }
        }

        SharedPref.getInstance().putModelClass(CART_MODEL, cartListResponseModel)
    }

    private fun initApiObservers() {
        orderViewModel.orderLiveData.observe(this) {
            progressDialog.dismiss()
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                finish()
            }
        }

        imageUploadViewModel.getCredLiveData().observe(this) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                genericResponseModel.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        orderImageLisForUpload.add(picMapModel)
                        if (multiplePicCount == orderImageLisForUpload.size) {
                            submitData()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "${genericResponseModel.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }

        productViewModel.productLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    it.data?.let { productList ->
                        initProductData(productList)
                    }
                }
            } else {
                initProductData(ArrayList())
            }
        }
    }

    private fun initProductData(
        productList: ArrayList<ProductList>
    ) {
        binding.mainContent.visibility = View.VISIBLE
        binding.btnLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE


        if (hasInternetConnection()) {
            productList.forEachIndexed { index, cartItem ->
                cartListResponseModel?.items?.get(index)?.isOutOfStock = cartItem.outOfStock
            }
        }

        binding.tvCheckout.text = resources.getString(R.string.update)
        binding.tvToolbarTitle.text = resources.getString(R.string.update_order)

        binding.llAddProductBackground.visibility = View.VISIBLE

        if (hasInternetConnection()) {
            binding.groupPaymentInfo.visibility = View.VISIBLE
        }

        initSharedPrefObservers()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCartItems.layoutManager = linearLayoutManager
        cartItemsListAdapter = CartItemsListAdapter(cartItemList, this)
        binding.rvCartItems.adapter = cartItemsListAdapter

        binding.rvCartItems.setOnTouchListener { _, _ ->
            Utils.hideKeyboard(this)
            false
        }

        binding.rvDiscountUnit.layoutManager = LinearLayoutManager(this)
        cartItemsDiscountListAdapter = CartItemsDiscountListAdapter(
            cartItemDiscountList, this, true, false,
        )
        binding.rvDiscountUnit.adapter = cartItemsDiscountListAdapter

        binding.rvOthersCharges.layoutManager = LinearLayoutManager(this)
        cartItemsOthersChargesAdapter = CartItemsDiscountListAdapter(
            cartItemOthersChargesList, this, true, true,
        )
        binding.rvOthersCharges.adapter = cartItemsOthersChargesAdapter

        binding.rvPhotos.layoutManager = GridLayoutManager(this, 6)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter
    }


    @SuppressLint("SetTextI18n")
    private fun showRemoveDialog(position: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_item)
        tvTitle.text = resources.getString(R.string.delete_product__from_cart_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            cartItemList.removeAt(position)

            cartListResponseModel?.items = cartItemList

            initRecyclerView()
            manageOrderSummery()

            manageProductVariantCount()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onRemoveItem(model: CartItem, position: Int) {
        showRemoveDialog(position)
    }

    override fun onQuantityChange(model: CartItem, quantity: String, position: Int) {
        if (quantity.isNotEmpty()) {
            cartItemList[position].qty = quantity.toDouble()
            cartListResponseModel?.items = cartItemList
            manageOrderSummery()
        } else {
            cartItemList[position].qty = 0.0
            cartListResponseModel?.items = cartItemList
            manageOrderSummery()
        }
    }

    override fun onPackagingLevelChange(model: CartItem, position: Int) {
        cartItemList[position].selectedPackagingLevel = model.selectedPackagingLevel
        cartItemList[position].packagingSize = model.selectedPackagingLevel?.size
        cartItemList[position].packagingUnit = model.selectedPackagingLevel?.unit
        cartListResponseModel?.items = cartItemList
        manageOrderSummery()
    }

    override fun onApplyDiscount(
        model: CartItem, discountType: String, discountValue: Double, position: Int
    ) {
        cartItemList[position] = model
        cartListResponseModel?.items = cartItemList
        manageOrderSummery()
        Utils.hideKeyboard(this)
    }

    override fun onApplyOfferPrice(model: CartItem, offersPrice: String, position: Int) {
        cartItemList[position] = model
        cartListResponseModel?.items = cartItemList
        manageOrderSummery()
        Utils.hideKeyboard(this)
    }

    override fun onRemoveDiscount(model: CartItem, position: Int) {
        model.isDiscountOnParticularItem = false
        model.priceAfterDiscount = null
        model.discount_details = null
        model.discountValue = null
        model.discountType = null
        cartItemList[position] = model
        cartListResponseModel?.items = cartItemList

        manageOrderSummery()
    }

    override fun onEditDiscount(position: Int, model: CartItemDiscountModel) {
        validateDiscount(true, cartItemDiscountList[position], position)
    }

    override fun onEditOthersCharges(position: Int, model: CartItemDiscountModel) {
        validateDeliveryCharges(true, cartItemOthersChargesList[position], position)
    }

    override fun onAddDiscount(model: CartItemDiscountModel) {

        cartItemDiscountList.add(model)

        if (cartItemDiscountList.size > 0) {
            binding.tvAddDiscount.visibility = View.GONE
            binding.groupAddedDiscount.visibility = View.VISIBLE
        }

        if (cartItemDiscountList.size >= 5) {
            binding.tvAddMoreDiscount.visibility = View.GONE
        } else {
            binding.tvAddMoreDiscount.visibility = View.VISIBLE
        }

        manageOrderSummery()

        Utils.hideKeyboard(this)

    }

    override fun onDeleteDiscount(model: CartItemDiscountModel?, position: Int?) {
        cartItemDiscountList.removeAt(position!!)
        cartItemsDiscountListAdapter.notifyDataSetChanged()
        manageOrderSummery()

        if (cartItemDiscountList.size >= 5) {
            binding.tvAddMoreDiscount.visibility = View.GONE
        } else {
            binding.tvAddMoreDiscount.visibility = View.VISIBLE
        }

        if (cartItemDiscountList.size == 0) {
            binding.tvAddDiscount.visibility = View.VISIBLE
            binding.groupAddedDiscount.visibility = View.GONE
            binding.tvAddMoreDiscount.visibility = View.GONE
            binding.tvDiscountAmount.visibility = View.GONE
        }
    }

    override fun onEditDiscount(model: CartItemDiscountModel?, position: Int?) {
        cartItemDiscountList[position!!] = model!!
        cartItemsDiscountListAdapter.notifyDataSetChanged()

        totalDiscountAmount = 0.0
        manageOrderSummery()

        Utils.hideKeyboard(this)
    }

    override fun onAddCharges(model: CartItemDiscountModel) {
        cartItemOthersChargesList.add(model)
        cartItemsOthersChargesAdapter.notifyDataSetChanged()

        if (cartItemOthersChargesList.size > 0) {
            binding.tvAddDeliveryCharges.visibility = View.GONE
            binding.groupAddedCharges.visibility = View.VISIBLE
        }

        if (cartItemOthersChargesList.size >= 5) {
            binding.tvAddMoreOthersCharges.visibility = View.GONE
        } else {
            binding.tvAddMoreOthersCharges.visibility = View.VISIBLE
        }

        manageOrderSummery()

        Utils.hideKeyboard(this)
    }

    override fun onItemClick() {
        Utils.hideKeyboard(this)
    }

    override fun onDeleteCharges(model: CartItemDiscountModel?, position: Int?) {
        cartItemOthersChargesList.removeAt(position!!)
        cartItemsOthersChargesAdapter.notifyDataSetChanged()
        manageOrderSummery()

        if (cartItemOthersChargesList.size >= 5) {
            binding.tvAddMoreOthersCharges.visibility = View.GONE
        } else {
            binding.tvAddMoreOthersCharges.visibility = View.VISIBLE
        }

        if (cartItemOthersChargesList.size == 0) {
            binding.tvAddDeliveryCharges.visibility = View.VISIBLE
            binding.groupAddedCharges.visibility = View.GONE
            binding.tvAddMoreOthersCharges.visibility = View.GONE
        }
    }

    override fun onEditCharges(model: CartItemDiscountModel?, position: Int?) {
        cartItemOthersChargesList[position!!] = model!!
        cartItemsOthersChargesAdapter.notifyDataSetChanged()

        totalDeliverCharges = 0.0
        manageOrderSummery()

        Utils.hideKeyboard(this)
    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            startActivity(
                Intent(this, OrgPhotosViewActivity::class.java).putExtra(
                    AppConstant.PRODUCT_INFO,
                    imageListModel
                ).putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
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


    override fun onDismissDialogForStartDay() {
        super.onDismissDialogForStartDay()
        finish()
    }

    override fun onPdfClick(position: Int, url: String) {
        super.onPdfClick(position, url)
        if (url != null) {
            FileUtils.openPdf(url, this)
        }


    }
}