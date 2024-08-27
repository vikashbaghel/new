package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrderDetailNewBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.custom.DownloadPdfTask
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.collapse
import com.app.rupyz.generic.helper.expand
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.hideWithRightToLeftAnimation
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.helper.showWithRightToLeftAnimation
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.FileUtils.openPdf
import com.app.rupyz.generic.utils.ImageUtils
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
import com.app.rupyz.sales.orders.adapter.IOnItemListener
import com.app.rupyz.sales.orders.adapter.OrderDispatchHistoryListAdapter
import com.app.rupyz.sales.orders.adapter.OrderItemListAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : BaseActivity(),
    CartItemsDiscountListAdapter.CartDiscountDeleteListener,
    OrderRejectedBottomSheetDialogFragment.IOrderRejectedListener,
    OrderStatusBottomSheetDialogFragment.IOrderStatusChangeListener,
    OrderDispatchHistoryListAdapter.IOrderDispatchListener,
    ProductImageViewPagerAdapter.ProductImageClickListener,
    LocationPermissionUtils.ILocationPermissionListener,
    IOnItemListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener, View.OnClickListener {
    private lateinit var binding: ActivityOrderDetailNewBinding
    private var isDataChange: Boolean = false

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var dashboardViewModel: DashboardViewModel

    private var orderData: OrderData? = null

    private var customerId: Int? = null

    private lateinit var orderItemsListAdapter: OrderItemListAdapter
    private lateinit var cartItemsDiscountListAdapter: CartItemsDiscountListAdapter
    private lateinit var orderDispatchHistoryListAdapter: OrderDispatchHistoryListAdapter
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter


    private var cartItemDiscountList = ArrayList<CartItemDiscountModel>()
    private var otherChargesList = ArrayList<CartItemDiscountModel>()
    private var dispatchHistoryList = ArrayList<DispatchHistoryListModel>()
    private val cartItems: ArrayList<CartItem> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private var orderQuantitySetList: ArrayList<Map.Entry<String, Double>> = ArrayList()

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null

    private var customerModel: CustomerData? = null


    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        binding.layoutMain.hideView()

        getUserCurrentLocation()
        initRecyclerView()
        initObservers()

        binding.ivBack.setOnClickListener(this)
        binding.ivEdit.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)
        binding.ivClearSearch.setOnClickListener(this)

        binding.ivTopArrow.setOnClickListener(this)
        binding.tvOrderDetails.setOnClickListener(this)
        binding.ivBTopArrow.setOnClickListener(this)
        binding.tvBuyerDetails.setOnClickListener(this)
        binding.ivDTopArrow.setOnClickListener(this)
        binding.tvDeliveryDetails.setOnClickListener(this)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeCallbacks(inputFinishChecker)
                if (p0.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    orderItemsListAdapter.filterActivityList(cartItems)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }
        })

        binding.tvViewMore.setOnClickListener {
            if (orderData != null) {
                startActivity(
                    Intent(this, OrderPaymentDetailsActivity::class.java)
                        .putExtra(AppConstant.ORDER, orderData)
                        .putExtra(AppConstant.PRODUCT, 0)
                )
            }
        }
    }

    private fun filterActivity(text: String) {
        val filteredCustomerList: ArrayList<CartItem> = ArrayList()

        for (item in cartItems) {
            if (item.name?.lowercase(Locale.ROOT)!!.contains(text.lowercase(Locale.ROOT))
                || item.code?.lowercase(Locale.ROOT)!!.contains(text.lowercase(Locale.ROOT))
            ) {
                binding.rvCartItems.visibility()
                //  binding.clEmptyData.gone()
                filteredCustomerList.add(item)
            }
        }

        orderItemsListAdapter.filterActivityList(filteredCustomerList)

        if (filteredCustomerList.isEmpty()) {
            showToast("No Product Found")
        }
    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            filterActivity(binding.etSearch.text.toString())
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCartItems.layoutManager = linearLayoutManager
        // binding.clEmptyData.gone()
        orderItemsListAdapter = OrderItemListAdapter(cartItems, this)
        binding.rvCartItems.adapter = orderItemsListAdapter

        binding.rvOrderDiscount.layoutManager = LinearLayoutManager(this)
        cartItemsDiscountListAdapter = CartItemsDiscountListAdapter(
            cartItemDiscountList, this, false, isOthersCharges = false
        )
        binding.rvOrderDiscount.adapter = cartItemsDiscountListAdapter

        binding.rvPhotos.layoutManager = GridLayoutManager(this, 6)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

        binding.recyclerDispatch.layoutManager = LinearLayoutManager(this)
        orderDispatchHistoryListAdapter = OrderDispatchHistoryListAdapter(
            dispatchHistoryList, this
        )
        binding.recyclerDispatch.adapter = orderDispatchHistoryListAdapter


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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "SuspiciousIndentation")
    private fun initObservers() {
        orderViewModel.getOrderByIdLiveData().observe(this) { data ->
            binding.progressBar.gone()
            if (data.error == false) {
                data.data?.let { model ->
                    binding.layoutMain.visibility()
                    orderData = model
                    orderData?.id = model.id

                    cartItems.addAll(orderData?.items!!)
                    binding.listSizeCount.text =
                        "${orderData?.items!!.size} ${resources.getString(R.string.products)}"
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

                    val buyersAddStringBuilder = StringBuilder()

                    model.customer?.let { customer ->
                        if (customer.city.isNullOrEmpty().not()) {
                            buyersAddStringBuilder.append(customer.city?.replaceFirstChar(Char::titlecase))
                        }
                        if (customer.state.isNullOrEmpty().not()) {
                            buyersAddStringBuilder.append(", ")
                            buyersAddStringBuilder.append(customer.state?.replaceFirstChar(Char::titlecase))
                        }
                        if (customer.pincode.isNullOrEmpty().not()) {
                            buyersAddStringBuilder.append(" - ")
                            buyersAddStringBuilder.append(customer.pincode)
                        }
                    }

                    if (buyersAddStringBuilder.toString().isNotEmpty()) {
                        binding.tvBuyerAddress.text = buyersAddStringBuilder.toString()
                    }

                    binding.tvOrderNoData.text = model.orderId

                    binding.tvPGstData.text = CalculatorHelper().convertLargeAmount(
                        model.gstAmount ?: "".toDouble(),
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                    binding.tvDiscountData.text = CalculatorHelper().convertLargeAmount(
                        model.discountAmount ?: "".toDouble(),
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                    binding.tvOtherChargeData.text = CalculatorHelper().convertLargeAmount(
                        model.deliveryCharges ?: "".toDouble(),
                        AppConstant.FOUR_DECIMAL_POINTS
                    )


                    binding.tvPTotalAmountData.text = CalculatorHelper().convertLargeAmount(
                        model.totalAmount ?: "".toDouble(),
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                    binding.btnCancel.text =
                        CalculatorHelper().convertLargeAmount(
                            model.totalAmount ?: "".toDouble(),
                            AppConstant.FOUR_DECIMAL_POINTS
                        )

                    binding.tvOrderAmountData.text = CalculatorHelper().convertLargeAmount(
                        model.amount ?: "".toDouble(),
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                    binding.tvName.text = model.customer?.name
                    binding.tvMobileNo.text = model.customer?.mobile

                    val deliveryAddStringBuilder = StringBuilder()

                    if (model.address != null) {
                        model.address?.let { address ->
                            if (address.city.isNullOrEmpty().not()) {
                                deliveryAddStringBuilder.append(address.city?.replaceFirstChar(Char::titlecase))
                            }
                            if (address.state.isNullOrEmpty().not()) {
                                deliveryAddStringBuilder.append(", ")
                                deliveryAddStringBuilder.append(address.state?.replaceFirstChar(Char::titlecase))
                            }
                            if (address.pincode.isNullOrEmpty().not()) {
                                deliveryAddStringBuilder.append(" - ")
                                deliveryAddStringBuilder.append(address.pincode)
                            }
                        }

                        if (buyersAddStringBuilder.toString().isNotEmpty()) {
                            binding.tvDeliveryAddress.text = deliveryAddStringBuilder.toString()
                        }
                    }


                    if (model.customer?.email.isNullOrEmpty().not()) {
                        binding.tvEmailId.text = model.customer?.email ?: ""
                    } else {
                        binding.tvEmailId.hideView()
                        binding.tvEmail.hideView()
                    }

                    if (model.customer?.gstin.isNullOrEmpty().not()) {
                        binding.tvGstNo.text = model.customer?.gstin
                        binding.tvGstNo.showView()
                        binding.tvGst.showView()
                    } else {
                        binding.tvGstNo.hideView()
                        binding.tvGst.hideView()
                    }


                    var purchaseCounter = 0
                    if (model.expectedDeliveryDate != null) {
                        binding.tvDDateData.text = DateFormatHelper.convertSanctionDateOrder(
                            model.expectedDeliveryDate ?: ""
                        )
                        purchaseCounter++

                        binding.tvDDate.showView()
                        binding.tvDDateData.showView()
                    } else {
                        binding.tvDDateData.text = ""
                        binding.tvDDate.hideView()
                        binding.tvDDateData.hideView()
                    }

                    if (model.transactionRefNo.isNullOrEmpty().not()) {
                        binding.tvTransactionRefNo.showView()
                        binding.tvTransactionRefNoData.showView()
                        binding.tvTransactionRefNoData.text = model.transactionRefNo
                    } else if (model.paymentDetails?.transactionRefNo.isNullOrEmpty().not()) {
                        binding.tvTransactionRefNo.showView()
                        binding.tvTransactionRefNoData.showView()
                        binding.tvTransactionRefNoData.text = model.paymentDetails?.transactionRefNo
                    } else {
                        binding.tvTransactionRefNo.hideView()
                        binding.tvTransactionRefNoData.hideView()
                    }

                    if (model.purchaseOrderNumber != null) {
                        binding.tvPOrderData.text = model.purchaseOrderNumber
                        binding.tvPOrderData.showView()
                        binding.tvPOrder.showView()
                        purchaseCounter++
                    } else {
                        binding.tvPOrderData.hideView()
                        binding.tvPOrder.hideView()
                    }
                    if (purchaseCounter == 0) {
                        binding.dView.hideView()
                    } else {
                        binding.dView.showView()
                    }

                    binding.tvStaffName.text = model.createdBy?.firstName ?: buildString {
                        append("")
                        append(model.createdBy?.lastName)
                    }

                    ImageUtils.loadImage(model.customer?.logoImageUrl ?: "", binding.staffImg)

                    if (model.fullFilledBy != null) {
                        binding.groupPlaceTo.visibility = View.VISIBLE
                        binding.tvDeliverToName.text = model.fullFilledBy?.name
                    }

                    binding.tvBuyer.text =
                        model.customer?.name ?: ""

                    if (model.fullFilledBy?.name != null) {
                        binding.tvDelivery.text = " ${model.fullFilledBy?.name ?: ""}"
                    } else {
                        binding.tvDelivery.gone()
                        //binding.tvFullFiledBy.visibility = View.GONE
                    }
                    val dateFormat = SimpleDateFormat("dd MMM yy | hh:mm a")

                    binding.tvOrderNoData.text = model.orderId
                    binding.tvOrderDate.text = resources.getString(
                        R.string.order_date,
                        DateFormatHelper.convertSanctionDate(model.createdAt)
                    )

                    if (model.updatedAt.isNullOrEmpty().not()) {
                        binding.tvOrderDate.text =
                            "" + DateFormatHelper.convertStringISOToCustomDateAndTimeFormat(
                                model.updatedAt,
                                dateFormat
                            )
                    } else {
                        //binding.hdOrderStatus.visibility = View.GONE
                    }

                    if (model.source.isNullOrEmpty()
                            .not() && model.source.equals(AppConstant.STORE_FRONT)
                    ) {
                        binding.tvStoreFrontView.visibility = View.VISIBLE
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
                        binding.tvStoreFrontView.visibility = View.GONE
                    }

                    if (model.orderImagesInfo.isNullOrEmpty().not()) {
                        binding.hdImageUpload.visibility()
                        pics.clear()
                        pics.addAll(model.orderImagesInfo!!)
                        addPhotoListAdapter.notifyDataSetChanged()
                    } else {
                        binding.hdImageUpload.gone()
                    }

                    binding.tvRemaingDays.hideView()
                    binding.tvRemaingDaysData.hideView()

                    binding.tvAmountData.text =
                        CalculatorHelper().convertLargeAmount(
                            model.amount ?: 0.0,
                            AppConstant.TWO_DECIMAL_POINTS
                        )

                    if (model.paymentOptionCheck.isNullOrEmpty().not()) {
                        if (model.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                            binding.tvPaymentTermData.text =
                                "Credit Days"
                            binding.tvRemaingDaysData.text = "${model.remainingPaymentDays} Days"
                            binding.tvRemaingDays.showView()
                            binding.tvRemaingDaysData.showView()
                            binding.tvAmountData.hideView()
                            binding.tvAmount.hideView()
                        } else {
                            when (model.paymentOptionCheck) {
                                AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> {
                                    binding.tvPaymentTermData.text =
                                        StringModificationUtils.convertCamelCase(AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase())
                                    binding.tvAmountData.showView()
                                    binding.tvAmount.showView()
                                }

                                AppConstant.PARTIAL_PAYMENT_API -> {
                                    binding.tvPaymentTermData.text =
                                        StringModificationUtils.convertCamelCase(AppConstant.PARTIAL_PAYMENT.lowercase())

                                    binding.tvAmountData.text =
                                        CalculatorHelper().convertLargeAmount(
                                            model.paymentDetails?.amount ?: 0.0,
                                            AppConstant.TWO_DECIMAL_POINTS
                                        )

                                    binding.tvRemaingDaysData.text =
                                        "${model.remainingPaymentDays} Days"
                                    binding.tvRemaingDays.showView()
                                    binding.tvRemaingDaysData.showView()
                                }

                                AppConstant.PAYMENT_ON_DELIVERY_API -> {
                                    binding.tvPaymentTermData.text =
                                        StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_DELIVERY.lowercase())
                                    binding.tvAmountData.hideView()
                                    binding.tvAmount.hideView()
                                }

                                AppConstant.PAYMENT_ON_NEXT_ORDER_API -> {
                                    binding.tvPaymentTermData.text =
                                        StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_NEXT_ORDER.lowercase())
                                    binding.tvAmountData.hideView()
                                    binding.tvAmount.hideView()
                                }
                            }
                        }
                    } else {
                        binding.tvPaymentTermData.text =
                            resources.getString(R.string.no_payment_available)
                    }

                    if (model.paymentDetails?.paymentMode.isNullOrEmpty().not()) {
                        binding.tvPaymentModeData.text = model.paymentDetails?.paymentMode
                    } else {
                        binding.tvPaymentModeData.hideView()
                        binding.tvPaymentMode.hideView()
                    }

                    if (model.comment != null && model.comment?.isNotEmpty()!!) {
                        binding.tvNoteMsg.text = model.comment
                        binding.tvNote.visibility = View.VISIBLE
                    }

                    if (model.dispatchHistoryList != null && model.dispatchHistoryList!!.isNotEmpty()) {
                        binding.layoutDispatch.visibility()
                        dispatchHistoryList.addAll(model.dispatchHistoryList!!)
                        orderDispatchHistoryListAdapter.notifyDataSetChanged()
                    }

                    if (model.comment.isNullOrEmpty() && model.adminComment.isNullOrEmpty()) {
                        binding.layoutPhoto.gone()
                    }

                    if (isStaffUser.not()) {
                        if (model.adminComment != null && model.adminComment?.isNotEmpty()!!) {
                            binding.tvNoteAdminMsg.text = model.adminComment
                            binding.tvNoteAdmin.visibility = View.VISIBLE
                        }
                    }

                    binding.btnAdd.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.ic_arrow_drop_down, null),
                        null
                    )

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
                                binding.btnAdd.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    null,
                                    null
                                )

                                if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.APPROVE_ORDER_PERMISSION, false
                                    ).not()
                                ) {
                                    binding.btnAdd.visibility = View.GONE
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
                                binding.btnAdd.text = model.deliveryStatus
                            }

                            AppConstant.READY_TO_DISPATCH_ORDER -> {
                                binding.ivEdit.visibility = View.GONE
                                binding.btnLayout.visibility = View.VISIBLE
                                binding.btnAdd.text = model.deliveryStatus
                            }

                            AppConstant.SHIPPED_ORDER -> {

                                binding.btnLayout.visibility = View.VISIBLE
                                binding.ivEdit.visibility = View.GONE
                                binding.btnAdd.text = model.deliveryStatus

                            }

                            AppConstant.PARTIAL_SHIPPED_ORDER -> {
                                binding.btnLayout.visibility = View.VISIBLE

                                binding.ivEdit.visibility = View.GONE

                                binding.btnAdd.text = model.deliveryStatus
                            }

                            AppConstant.DELIVERED_ORDER -> {
                                binding.btnLayout.visibility = View.GONE
                                binding.ivEdit.visibility = View.GONE
                            }

                            AppConstant.ORDER_REJECTED -> {
                                binding.btnLayout.visibility = View.GONE
                                binding.ivEdit.visibility = View.GONE

                                binding.tvNoteMsg.text =
                                    resources.getString(R.string.rejected_reason)
                                binding.tvNote.setBackgroundResource(R.drawable.payement_rejected_comment_bg)

                                binding.tvNote.text = model.rejectReason

                                // binding.groupNotes.visibility = View.VISIBLE
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
                                cartItems.clear()
                                cartItemDiscountList.clear()
                                otherChargesList.clear()
                                dispatchHistoryList.clear()
                                orderQuantitySetList.clear()

                                model.deliveryStatus =
                                    AppConstant.getOrderStatusForUpdate(binding.btnAdd.text.toString())
                                dashboardViewModel.updateOrderStatus(model, model.id ?: 0)
                            } else {

                                val fragment = OrderStatusBottomSheetDialogFragment(
                                    model,
                                    binding.btnAdd.text.toString(),
                                    this
                                )
                                fragment.show(
                                    supportFragmentManager,
                                    AppConstant.UPDATE_ORDER_STATUS
                                )
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

    override fun onNewIntent(newIntent: Intent) {
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
                // binding.mainContent.visibility = View.GONE
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

    override fun onPdfClick(position: Int, url: String) {
        super.onPdfClick(position, url)
        if (url != null) {
            openPdf(url, this)
        }
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
        super.onBackPressed()
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

    override fun onClick(view: View?) {
        var rotationAngleOrder = 0
        var rotationAngleByers = 0
        var rotationAngleDelivery = 0

        when (view) {
            binding.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }

            binding.ivEdit -> {
                SharedPref.getInstance().putModelClass(SharePrefConstant.CART_MODEL, orderData)
                startActivity(
                    Intent(this, CartActivity::class.java)
                        .putExtra(AppConstant.ORDER_EDIT, orderData)
                        .putExtra(AppConstant.CUSTOMER_ID, customerId)
                        .putExtra(AppConstant.CUSTOMER, customerModel)
                )
            }

            binding.ivSearch -> {
                binding.layoutSearch.showWithRightToLeftAnimation()
                binding.ivSearch.gone()
                binding.ivEdit.gone()
            }

            binding.ivClearSearch -> {
                binding.layoutSearch.hideWithRightToLeftAnimation()
                binding.ivSearch.visibility()
                binding.ivEdit.visibility()
                binding.etSearch.setText("")
                orderItemsListAdapter = OrderItemListAdapter(cartItems, this)
                binding.rvCartItems.adapter = orderItemsListAdapter
                orderItemsListAdapter.notifyDataSetChanged()
            }


            binding.ivTopArrow, binding.tvOrderDetails -> {
                if (binding.layoutOrder.isVisible) {
                    rotationAngleOrder = 0
                    collapse(binding.layoutOrder)
                    binding.oView.visibility()
                } else {
                    rotationAngleOrder = 180
                    expand(binding.layoutOrder)
                    binding.oView.gone()
                }

                binding.ivTopArrow.animate()
                    .rotation(rotationAngleOrder.toFloat()).setDuration(300)
                    .start()
            }

            binding.ivBTopArrow, binding.tvBuyerDetails -> {
                if (binding.layoutBuyer.isVisible) {
                    rotationAngleByers = 0
                    collapse(binding.layoutBuyer)
                    binding.billView.visibility()
                } else {
                    rotationAngleByers = 180
                    expand(binding.layoutBuyer)
                    binding.billView.gone()
                }

                binding.ivBTopArrow.animate()
                    .rotation(rotationAngleByers.toFloat()).setDuration(300)
                    .start()


            }

            binding.ivDTopArrow, binding.tvDeliveryDetails -> {

                if (binding.layoutDelivrey.isVisible) {
                    rotationAngleDelivery = 0
                    collapse(binding.layoutDelivrey)
                    binding.delView.visibility()
                } else {
                    rotationAngleDelivery = 180
                    expand(binding.layoutDelivrey)
                    binding.delView.gone()
                }

                binding.ivDTopArrow.animate()
                    .rotation(rotationAngleDelivery.toFloat()).setDuration(300)
                    .start()

            }
        }
    }

    override fun onItemData(model: CartItem) {
        startActivity(
            Intent(this, OrderPaymentDetailsActivity::class.java)
                .putExtra(AppConstant.PRODUCT_DETAILS, model)
                .putExtra(AppConstant.PRODUCT, 1)
        )
    }

}


