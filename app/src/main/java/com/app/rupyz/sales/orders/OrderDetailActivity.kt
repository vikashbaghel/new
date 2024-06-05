package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrderDetailBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.custom.DownloadPdfTask
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.StringModificationUtils
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.OrderStatusModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.DispatchHistoryListModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.cart.CartActivity
import com.app.rupyz.sales.cart.CartItemsDiscountListAdapter
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.orderdispatch.CreateShipmentOrderActivity
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.sales.orderdispatch.OrderDispatchHistoryActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import kotlin.math.roundToInt

class OrderDetailActivity : BaseActivity(),
        CartItemsDiscountListAdapter.CartDiscountDeleteListener,
        OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
        OrderStatusBottomSheetDialogFragment.IOrderStatusChangeListener,
        OrderDispatchHistoryListAdapter.IOrderDispatchListener,
        ProductImageViewPagerAdapter.ProductImageClickListener,
        LocationPermissionUtils.ILocationPermissionListener,
        MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityOrderDetailBinding
    private var isDataChange: Boolean = false

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var dashboardViewModel: DashboardViewModel

    private var orderData: OrderData? = null

    private var customerId: Int? = null

    private lateinit var orderItemsListAdapter: OrderItemListAdapter
    private lateinit var cartItemsDiscountListAdapter: CartItemsDiscountListAdapter
    private lateinit var otherChargesListAdapter: CartItemsDiscountListAdapter
    private lateinit var orderDispatchHistoryListAdapter: OrderDispatchHistoryListAdapter
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private lateinit var orderQuantitySetAdapter: OrderQuantitySetAdapter

    private var cartItemDiscountList = ArrayList<CartItemDiscountModel>()
    private var otherChargesList = ArrayList<CartItemDiscountModel>()
    private var dispatchHistoryList = ArrayList<DispatchHistoryListModel>()
    private val cartItems: ArrayList<CartItem> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private var orderQuantitySetList: ArrayList<Map.Entry<String, Double>> = ArrayList()

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private  var  fragment : MarkAttendanceBottomSheetDialogFragment? = null

    private var customerModel: CustomerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        binding.mainContent.visibility = View.GONE

        getUserCurrentLocation()

        initRecyclerView()

        initObservers()

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.ivEdit.setOnClickListener {
            SharedPref.getInstance().putModelClass(SharePrefConstant.CART_MODEL, orderData)
            startActivity(
                    Intent(this, CartActivity::class.java)
                            .putExtra(AppConstant.ORDER_EDIT, orderData)
                            .putExtra(AppConstant.CUSTOMER_ID, customerId)
                            .putExtra(AppConstant.CUSTOMER, customerModel)
            )
        }

        binding.paymentStatusLayout.setOnClickListener {
            if (orderData?.paymentOptionCheck.isNullOrEmpty().not()) {
                startActivity(Intent(this, OrderPaymentDetailsActivity::class.java)
                        .putExtra(AppConstant.ORDER, orderData))
            }
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCartItems.layoutManager = linearLayoutManager
        orderItemsListAdapter = OrderItemListAdapter(cartItems)
        binding.rvCartItems.adapter = orderItemsListAdapter

        binding.rvOrderDiscount.layoutManager = LinearLayoutManager(this)
        cartItemsDiscountListAdapter = CartItemsDiscountListAdapter(
                cartItemDiscountList, this, false, isOthersCharges = false
        )
        binding.rvOrderDiscount.adapter = cartItemsDiscountListAdapter

        binding.rvOthersCharges.layoutManager = LinearLayoutManager(this)
        otherChargesListAdapter = CartItemsDiscountListAdapter(
                otherChargesList, this, false, isOthersCharges = true
        )
        binding.rvOthersCharges.adapter = otherChargesListAdapter


        binding.rvDispatchHistory.layoutManager = LinearLayoutManager(this)
        orderDispatchHistoryListAdapter = OrderDispatchHistoryListAdapter(dispatchHistoryList, this)
        binding.rvDispatchHistory.adapter = orderDispatchHistoryListAdapter

        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

        binding.rvOrderQuantity.layoutManager = LinearLayoutManager(this)
        orderQuantitySetAdapter = OrderQuantitySetAdapter(orderQuantitySetList)
        binding.rvOrderQuantity.adapter = orderQuantitySetAdapter

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
                        MockLocationDetectedDialogFragment.getInstance(null)
                fragment.isCancelable = false
                fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        orderViewModel.getOrderByIdLiveData().observe(this) { data ->

            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { model ->
                    binding.mainContent.visibility = View.VISIBLE

                    orderData = model
                    orderData?.id = model.id

                    cartItems.addAll(orderData?.items!!)
                    orderItemsListAdapter.notifyDataSetChanged()

                    if (model.customer?.id != null) {
                        customerId = model.customer?.id
                    }

                    customerModel = model.customer

                    if (model.purchaseOrderUrl != null && model.purchaseOrderUrl != "") {
                        if (hasInternetConnection()) {
                            binding.ivDownloadPdf.visibility = View.VISIBLE
                        }
                        binding.ivDownloadPdf.setOnClickListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                mGalleryPermissionResult.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                mGalleryPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }
                    }

                    binding.tvOrderAddress.text =
                            model.customer?.name?.replaceFirstChar(Char::titlecase) +
                                    ", " + model.customer?.city?.replaceFirstChar(
                                    Char::titlecase
                            )

                    if (model.source.isNullOrEmpty()
                                    .not() && model.source.equals(AppConstant.STORE_FRONT)
                    ) {
                        binding.groupStoreFront.visibility = View.VISIBLE
                        binding.tvStoreFrontView.setOnClickListener {
                            val fragment = InfoBottomSheetDialogFragment()
                            val bundle = Bundle()
                            bundle.putString(
                                    AppConstant.HEADING,
                                    resources.getString(R.string.storefront_order)
                            )
                            bundle.putString(
                                    AppConstant.MESSAGE,
                                    resources.getString(R.string.storefront_order_message)
                            )
                            fragment.arguments = bundle
                            fragment.show(supportFragmentManager, AppConstant.STORE_FRONT)
                        }
                    } else {
                        binding.groupStoreFront.visibility = View.GONE
                    }

                    binding.tvOrderBy.text =
                            "Ordered by : ${model.createdBy?.firstName} ${model.createdBy?.lastName}"

                    if (model.fullFilledBy?.name != null) {
                        binding.tvFullFiledBy.text = "Fulfilled by  : ${model.fullFilledBy?.name}"
                    } else {
                        binding.tvFullFiledBy.visibility = View.GONE
                    }

                    binding.tvToolbarTitle.text = "Order No: " + model.orderId
                    binding.tvOrderDate.text = resources.getString(R.string.order_date,
                            DateFormatHelper.getMonthDate(model.createdAt))

                    if (model.updatedAt.isNullOrEmpty().not()) {
                        binding.hdOrderStatus.text =
                                "Updated on " + DateFormatHelper.convertSanctionDate(model.updatedAt)
                    } else {
                        binding.hdOrderStatus.visibility = View.GONE
                    }

                    binding.tvOrderTotalAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmount(model.amount, AppConstant.TWO_DECIMAL_POINTS)

                    binding.tvDiscountAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmount(model.discountAmount, AppConstant.TWO_DECIMAL_POINTS)

                    if (model.discountDetails != null && model.discountDetails!!.isNotEmpty()) {
                        binding.groupDiscount.visibility = View.VISIBLE
                        cartItemDiscountList.addAll(model.discountDetails!!)
                        cartItemsDiscountListAdapter.notifyDataSetChanged()
                    } else {
                        binding.groupDiscount.visibility = View.GONE
                    }

                    if (model.deliveryCharges != null && model.deliveryCharges?.roundToInt() != 0) {
                        binding.tvDeliveryCharge.text =
                                CalculatorHelper().convertLargeAmount(model.deliveryCharges!!, AppConstant.TWO_DECIMAL_POINTS)
                        binding.groupOtherCharges.visibility = View.VISIBLE
                    } else {
                        binding.groupOtherCharges.visibility = View.GONE
                    }

                    if (model.chargesDetails.isNullOrEmpty().not()) {
                        binding.rvOthersCharges.visibility = View.VISIBLE
                        otherChargesList.addAll(model.chargesDetails!!)
                        otherChargesListAdapter.notifyDataSetChanged()
                    } else {
                        binding.rvOthersCharges.visibility = View.GONE
                    }


                    if (model.paymentOptionCheck.isNullOrEmpty().not()) {
                        if (model.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                            binding.tvPaymentStatus.text =
                                    "Credit Days " + model.remainingPaymentDays
                        } else {
                            when (model.paymentOptionCheck) {
                                AppConstant.FULL_PAYMENT_IN_ADVANCE_API ->
                                    binding.tvPaymentStatus.text =
                                            StringModificationUtils.convertCamelCase(AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase())

                                AppConstant.PARTIAL_PAYMENT_API ->
                                    binding.tvPaymentStatus.text =
                                            StringModificationUtils.convertCamelCase(AppConstant.PARTIAL_PAYMENT.lowercase())

                                AppConstant.PAYMENT_ON_DELIVERY_API ->
                                    binding.tvPaymentStatus.text =
                                            StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_DELIVERY.lowercase())
                            }
                        }
                    } else {
                        binding.tvPaymentStatus.text = resources.getString(R.string.no_payment_available)
                    }

                    if (model.deliveryStatus.isNullOrEmpty().not()) {
                        binding.tvOrderStatus.text = model.deliveryStatus

                        if (model.deliveryStatus.equals(AppConstant.RECEIVED_ORDER)) {
                            binding.hdOrderQtyMessageForOrderReceive.visibility = View.VISIBLE
                            binding.groupOrderQuantity.visibility = View.GONE
                        } else {
                            orderQuantitySetList.addAll(
                                    CalculatorHelper().calculateTotalOrderQuantity(
                                            orderData?.items!!, false
                                    )
                            )
                            orderQuantitySetAdapter.notifyDataSetChanged()
                            binding.hdOrderQtyMessageForOrderReceive.visibility = View.GONE
                            binding.groupOrderQuantity.visibility = View.VISIBLE
                        }
                    } else if (model.isSyncedToServer == false) {
                        binding.tvOrderStatus.text = resources.getText(R.string.offline)
                        binding.tvOrderStatus.setTextColor(resources.getColor(R.color.order_gray))
                        binding.ivOrderStatus.setImageResource(R.drawable.assign_customer_background_gray)
                    }

                    binding.tvOrderGst.text =
                            CalculatorHelper().convertCommaSeparatedAmount(model.gstAmount, AppConstant.TWO_DECIMAL_POINTS)

                    binding.tvOrderTotalAmountInc.text =
                            CalculatorHelper().convertCommaSeparatedAmount(model.totalAmount, AppConstant.TWO_DECIMAL_POINTS)


                    if (model.address != null) {
                        binding.groupDeliverAddress.visibility = View.VISIBLE

                        if (model.address?.name != null) {
                            binding.tvAddressType.text = model.address?.name + " "
                        }
                        if (model.address?.addressLine1 != null) {
                            binding.tvAddressTitle.text = model.address?.addressLine1
                        }

                        binding.tvAddressLine.text =
                                "${model.address?.city}, ${model.address?.state}, ${model.address?.pincode}"

                    } else {
                        binding.groupDeliverAddress.visibility = View.GONE
                    }

                    if (model.comment != null && model.comment?.isNotEmpty()!!) {
                        binding.tvNote.text = model.comment
                        binding.groupNotes.visibility = View.VISIBLE
                    } else {
                        binding.groupNotes.visibility = View.GONE
                    }

                    if (isStaffUser.not()) {
                        if (model.adminComment != null && model.adminComment?.isNotEmpty()!!) {
                            binding.tvNoteForAdmin.text = model.adminComment
                            binding.groupAdminNotes.visibility = View.VISIBLE
                        } else {
                            binding.groupAdminNotes.visibility = View.GONE
                        }
                    } else {
                        binding.groupAdminNotes.visibility = View.GONE
                    }

                    if (model.orderImagesInfo.isNullOrEmpty().not()) {
                        binding.groupImages.visibility = View.VISIBLE
                        pics.clear()
                        pics.addAll(model.orderImagesInfo!!)
                        addPhotoListAdapter.notifyDataSetChanged()
                    } else {
                        binding.groupImages.visibility = View.GONE
                    }

                    if (model.dispatchHistoryList != null && model.dispatchHistoryList!!.isNotEmpty()) {
                        dispatchHistoryList.addAll(model.dispatchHistoryList!!)
                        orderDispatchHistoryListAdapter.notifyDataSetChanged()

                        binding.groupDispatchHistory.visibility = View.VISIBLE

                    } else {
                        binding.groupDispatchHistory.visibility = View.GONE
                        binding.btnLayout.visibility = View.GONE
                    }


                    if (model.deliveryStatus.isNullOrEmpty().not()) {
                        when (model.deliveryStatus) {
                            AppConstant.RECEIVED_ORDER -> {
                                binding.btnLayout.visibility = View.VISIBLE
                                if (PermissionModel.INSTANCE.getPermission(
                                                AppConstant.EDIT_ORDER_PERMISSION,
                                                false
                                        )
                                ) {
                                    binding.ivEdit.visibility = View.VISIBLE
                                }

                                binding.btnAdd.text = AppConstant.APPROVE
                                binding.btnCancel.text = AppConstant.REJECT

                                if (PermissionModel.INSTANCE.getPermission(
                                                AppConstant.APPROVE_ORDER_PERMISSION, false
                                        ).not()
                                ) {
                                    binding.btnAdd.visibility = View.GONE
                                }

                                if (PermissionModel.INSTANCE.getPermission(
                                                AppConstant.REJECT_ORDER_PERMISSION, false
                                        ).not()
                                ) {
                                    binding.btnCancel.visibility = View.GONE
                                }
                            }

                            AppConstant.APPROVED_ORDER,
                            AppConstant.PROCESSING_ORDER -> {
                                if (PermissionModel.INSTANCE.getPermission(
                                                AppConstant.EDIT_ORDER_PERMISSION,
                                                false
                                        )
                                ) {
                                    binding.ivEdit.visibility = View.VISIBLE
                                }
                                binding.btnLayout.visibility = View.VISIBLE
                                binding.btnCancel.visibility = View.GONE

                                binding.btnAdd.text = AppConstant.UPDATE_ORDER_STATUS
                                binding.ivDropArrow.visibility = View.VISIBLE
                            }

                            AppConstant.READY_TO_DISPATCH_ORDER -> {
                                binding.ivEdit.visibility = View.GONE
                                binding.btnLayout.visibility = View.VISIBLE
                                binding.btnCancel.visibility = View.GONE

                                binding.btnAdd.text = AppConstant.UPDATE_ORDER_STATUS
                                binding.ivDropArrow.visibility = View.VISIBLE
                            }

                            AppConstant.SHIPPED_ORDER -> {

                                binding.btnLayout.visibility = View.VISIBLE
                                binding.btnCancel.visibility = View.GONE

                                binding.ivEdit.visibility = View.GONE

                                binding.btnAdd.text = AppConstant.UPDATE_ORDER_STATUS
                                binding.ivDropArrow.visibility = View.VISIBLE
                            }

                            AppConstant.PARTIAL_SHIPPED_ORDER -> {
                                binding.btnLayout.visibility = View.VISIBLE
                                binding.btnCancel.visibility = View.GONE

                                binding.ivEdit.visibility = View.GONE

                                binding.btnAdd.text = AppConstant.UPDATE_ORDER_STATUS
                                binding.ivDropArrow.visibility = View.VISIBLE
                            }

                            AppConstant.DELIVERED_ORDER -> {
                                binding.btnLayout.visibility = View.GONE
                                binding.ivEdit.visibility = View.GONE
                            }

                            AppConstant.ORDER_REJECTED -> {
                                binding.btnLayout.visibility = View.GONE
                                binding.ivEdit.visibility = View.GONE

                                binding.hdTvNotes.text =
                                        resources.getString(R.string.rejected_reason)
                                binding.tvNote.setBackgroundResource(R.drawable.payement_rejected_comment_bg)

                                binding.tvNote.text = model.rejectReason

                                binding.groupNotes.visibility = View.VISIBLE
                            }
                        }
                    } else if (model.isSyncedToServer == false) {
                        binding.btnLayout.visibility = View.GONE
                        binding.ivEdit.visibility = View.VISIBLE
                    }

                    binding.btnAdd.setOnClickListener {
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            if (binding.btnAdd.text.toString() == AppConstant.APPROVE) {
                                binding.btnAdd.isEnabled = false

                                binding.progressBar.visibility = View.VISIBLE
                                binding.mainContent.visibility = View.GONE

                                cartItems.clear()
                                cartItemDiscountList.clear()
                                otherChargesList.clear()
                                dispatchHistoryList.clear()
                                orderQuantitySetList.clear()

                                model.deliveryStatus =
                                        AppConstant.getOrderStatusForUpdate(binding.btnAdd.text.toString())
                                dashboardViewModel.updateOrderStatus(model, model.id ?: 0)
                            } else {
                                binding.ivDropArrow.visibility = View.VISIBLE
                                val fragment = OrderStatusBottomSheetDialogFragment(model, this)
                                fragment.show(
                                        supportFragmentManager,
                                        AppConstant.UPDATE_ORDER_STATUS
                                )
                            }
                        } else {
                            showStartDayDialog()
                        }
                    }

                    binding.btnCancel.setOnClickListener {
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            if (model.deliveryStatus == AppConstant.SHIPPED_ORDER) {
                                finish()
                            } else {
                                openRejectOrderBottomSheet(model)
                            }
                        } else {
                            showStartDayDialog()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "${data.message}", Toast.LENGTH_SHORT).show()
            }
        }

        dashboardViewModel.updateOrderStatusLiveData.observe(this) { data ->
            data?.let {
                if (data.error == false) {
                    isDataChange = true
                    Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
                    binding.mainContent.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE

                    orderViewModel.getOrderDataById(
                            intent.getIntExtra(
                                    AppConstant.ORDER_ID, 0
                            ), hasInternetConnection()
                    )
                } else {
                    Toast.makeText(this, "" + data.message, Toast.LENGTH_LONG).show()
                }

                binding.btnAdd.isEnabled = true
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

    private fun openRejectOrderBottomSheet(model: OrderData) {
        val fragment = OrderRejectedBottomSheetDialogFragment(model, this)
        fragment.show(supportFragmentManager, AppConstant.ORDER_REJECTED)
    }


    override fun commentOfOrderRejected(model: OrderData) {

        cartItems.clear()
        cartItemDiscountList.clear()
        otherChargesList.clear()
        dispatchHistoryList.clear()
        orderQuantitySetList.clear()

        model.deliveryStatus = AppConstant.ORDER_REJECTED
        dashboardViewModel.updateOrderStatus(model, model.id ?: 0)
    }

    override fun onDismissDialog(model: OrderData) {}
    override fun onEditDiscount(position: Int, model: CartItemDiscountModel) {}
    override fun onEditOthersCharges(position: Int, model: CartItemDiscountModel) {}

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        isDataChange = true
    }

    override fun onOrderStatusUpdate(model: OrderStatusModel) {
        when (AppConstant.getOrderStatusForUpdate(model.name!!)) {
            AppConstant.ORDER_REJECTED -> {
                openRejectOrderBottomSheet(orderData!!)
            }

            AppConstant.SHIPPED_ORDER -> {
                startActivity(
                        Intent(this, CreateShipmentOrderActivity::class.java).putExtra(
                                AppConstant.ORDER_ID,
                                orderData?.id
                        )
                )
            }

            else -> {
                binding.mainContent.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE

                orderData?.deliveryStatus = model.name
                cartItems.clear()
                cartItemDiscountList.clear()
                otherChargesList.clear()
                dispatchHistoryList.clear()
                orderQuantitySetList.clear()

                dashboardViewModel.updateOrderStatus(orderData!!, orderData?.id ?: 0)
            }
        }
    }

    override fun onGetDispatchHistory(model: DispatchHistoryListModel) {
        startActivity(
                Intent(this, OrderDispatchHistoryActivity::class.java)
                        .putExtra(AppConstant.ORDER_ID, intent.getIntExtra(AppConstant.ORDER_ID, 0))
                        .putExtra(AppConstant.DISPATCH_ID, model.id)
                        .putExtra(
                                AppConstant.ORDER_CLOSE,
                                orderData?.isClosed
                        )
        )
    }

    private val mGalleryPermissionResult: ActivityResultLauncher<String> =
            registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
            ) { result ->
                if (result) {
                    val fileName = orderData?.customer?.name + "-" + orderData?.orderId + ".pdf"
                    DownloadPdfTask(this, orderData?.purchaseOrderUrl!!, fileName)
                } else {
                    Toast.makeText(
                            this,
                            "Media Permission is required to perform this action.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
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
                    Intent(this, OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, position)
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

    override fun onResume() {
        super.onResume()

        SharedPref.getInstance().clearCart()

        if (intent.hasExtra(AppConstant.ORDER_ID)) {
            cartItems.clear()
            dispatchHistoryList.clear()
            cartItemDiscountList.clear()
            otherChargesList.clear()
            orderQuantitySetList.clear()

            binding.progressBar.visibility = View.VISIBLE

            orderViewModel.getOrderDataById(
                    intent.getIntExtra(
                            AppConstant.ORDER_ID, 0
                    ),
                    hasInternetConnection()
            )
        }
    }

    override fun onDismissDialogForStartDay() {
        super.onDismissDialogForStartDay()
        finish()
    }

    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.NOTIFICATION)) {
            startActivity(
                    Intent(
                            this,
                            SalesMainActivity::class.java
                    )
            )
        } else if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}


