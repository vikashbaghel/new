package com.app.rupyz.sales.orders

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCreateNewOrderForCustomerBinding
import com.app.rupyz.dialog.GeoFencingWarningDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharePrefConstant.CART_MODEL
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.cart.CartActivity
import com.app.rupyz.sales.customer.ProductCategoryListForAssignAdapter
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.orders.adapter.CrateOrderForCustomerAdapter
import com.app.rupyz.sales.product.IProductBottomSheetActionListener
import com.app.rupyz.sales.product.ProductCategoryFilterAdapter
import com.app.rupyz.sales.product.ProductDetailsBottomSheetDialogFragment
import com.app.rupyz.sales.product.ProductViewModel
import com.app.rupyz.sales.product.VariantsBottomSheetDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson

class CreateNewOrderForCustomerActivity : BaseActivity(), OrderActionListener,
    IProductBottomSheetActionListener,
    ProductCategoryFilterAdapter.IFilterCategoryListener,
    ProductCategoryListForAssignAdapter.IAssignCategoryListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    LocationPermissionUtils.ILocationPermissionListener,
    GeoFencingWarningDialogFragment.IGeoFencingActionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {

    private lateinit var binding: ActivityCreateNewOrderForCustomerBinding

    private lateinit var createOrderForCustomerAdapter: CrateOrderForCustomerAdapter
    private lateinit var brandAdapter: ProductCategoryListForAssignAdapter
    private lateinit var categoryFilterAdapter: ProductCategoryFilterAdapter

    private lateinit var productViewModel: ProductViewModel
    private lateinit var orderViewModel: OrderViewModel

    private var productList = ArrayList<ProductList>()
    private var cartAddedProductList = ArrayList<CartItem>()
    private var brandList = ArrayList<AllCategoryResponseModel>()
    private var filteredBrandList = ArrayList<String>()
    private var categoryList: ArrayList<AllCategoryResponseModel> = ArrayList()

    private var customerId: Int? = null
    private var paymentTerms: String? = null

    private var customerName: String? = null
    private var category: String = ""

    private var cartListResponseModel: OrderData? = null

    private var isPageLoading = false
    private var isPageLoadingForBrand = false

    private var isApiLastPage = false
    private var isApiLastPageForBrand = false

    private var currentPage = 1
    private var currentPageForBrand = 1

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private var isValidateSearch = false
    private var isKeyboardShowing = false
    private var isUserStartedHisDay = false

    var delay: Long = 500 // 1 seconds after user stops typing

    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)
    private lateinit var inputMethodManager: InputMethodManager

    private var customerModel: CustomerData? = null
    private var selectedDistributor: CustomerData? = null
    private var isTelePhonicOrder: Boolean = false

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationManager: LocationManager? = null
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG.not() && SharedPref.getInstance()
                .getBoolean(SharePrefConstant.DISABLE_SCREENSHOT_ON_PRODUCTS, false)
        ) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        binding = ActivityCreateNewOrderForCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationPermissionUtils = LocationPermissionUtils(this, this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        categoryList = ArrayList()


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

        isTelePhonicOrder = intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)

        getUserCurrentLocation()

        initRecyclerView()

        binding.btnCancel.setOnClickListener { finish() }

        initLayout()
        initObservers()

        binding.ivBack.setOnClickListener {
            if (getKeyBoardVisibility()) {
                hideKeyboard()
            } else {
                finish()
            }
        }
   
        if (intent.hasExtra(AppConstant.ORDER_EDIT).not()) {
            manageCart()
        }

        binding.ivQrCodeScanner.setOnClickListener {
            manageCart()
            checkCameraPermission()
        }
        

    }

    private fun checkCameraPermission() {
        activityResultLauncher.launch(
            arrayOf(Manifest.permission.CAMERA)
        )
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted = false

            permissions.entries.forEach {
                isGranted = it.value
            }

            if (isGranted) {
                openQrCodeScanner()
            } else {
                Toast.makeText(
                    this,
                    "Camera Permission is required to perform this action.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private fun openQrCodeScanner() {
        val qrCodeIntent = Intent(this, QrCodeScannerActivity::class.java)
        qrCodeIntent.putExtra(AppConstant.CUSTOMER_ID, customerId)
        qrCodeIntent.putExtra(AppConstant.CUSTOMER, customerModel)
        qrCodeIntent.putExtra(AppConstant.IS_TELEPHONIC_ORDER, isTelePhonicOrder)

        if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            qrCodeIntent.putExtra(AppConstant.ORDER_EDIT, true)
        }

        openQrCodeScannerResultLauncher.launch(qrCodeIntent)
    }

    @SuppressLint("SetTextI18n")
    var openQrCodeScannerResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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
                    val fragment =
                        MockLocationDetectedDialogFragment.getInstance(this@CreateNewOrderForCustomerActivity)
                    fragment.isCancelable = false
                    fragment.show(
                        supportFragmentManager,
                        MockLocationDetectedDialogFragment::class.java.name
                    )
                }
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                        if (intent.hasExtra(AppConstant.ORDER_EDIT).not() &&
                            SharedPref.getInstance()
                                .getBoolean(AppConstant.GEO_FENCING_ENABLE, false)
                        ) {
                            if (intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)
                                    .not()
                            ) {
                                registerGeofenceUpdates()
                            }
                        }
                    } else {
                        showStartDayDialog()
                    }
                }
            }
        }
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
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

        if (isUserInsideGeoFencing.not()) {
            val fragment = GeoFencingWarningDialogFragment.getInstance(this)
            fragment.show(supportFragmentManager, GeoFencingWarningDialogFragment::class.java.name)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {
        if (intent.hasExtra(AppConstant.CUSTOMER_NAME)) {
            customerName = intent.getStringExtra(AppConstant.CUSTOMER_NAME)
            if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                binding.ivCart.visibility = View.GONE
                binding.tvCartItemCount.visibility = View.GONE
                binding.tvToolbarTitle.text =
                    resources.getString(R.string.add_new_product_for_customer, customerName)
                binding.btnAdd.text = resources.getString(R.string.update)
            } else {
                binding.tvToolbarTitle.text =
                    resources.getString(R.string.create_order_for_customer, customerName)
            }
        }


        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
            binding.shimmerProduct.visibility = View.VISIBLE
        }




        getBrandList()
        allCategoryList()

        if (intent.hasExtra(AppConstant.PAYMENT_INFO)) {
            paymentTerms = intent.getStringExtra(AppConstant.PAYMENT_INFO)
        }

        binding.ivCart.setOnClickListener {
            manageCart()
            hideKeyboard()
            if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                updateProduct()
            } else {
                startActivity(
                    Intent(this, CartActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, customerId)
                        .putExtra(AppConstant.CUSTOMER, customerModel)
                        .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)
                        .putExtra(AppConstant.SELECTED_DISTRIBUTOR, selectedDistributor)
                        .putExtra(AppConstant.IS_TELEPHONIC_ORDER, isTelePhonicOrder)

                )
            }
        }

        binding.btnAdd.setOnClickListener {
            manageCart()
            hideKeyboard()
            if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                updateProduct()
            } else {
                startActivity(
                    Intent(this, CartActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, customerId)
                        .putExtra(AppConstant.CUSTOMER, customerModel)
                        .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)
                        .putExtra(AppConstant.SELECTED_DISTRIBUTOR, selectedDistributor)
                        .putExtra(AppConstant.IS_TELEPHONIC_ORDER, isTelePhonicOrder)
                )
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                validateSearch()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    if (isValidateSearch) {
                        isApiLastPage = false
                        getAllProductList()
                        Utils.hideKeyboard(this@CreateNewOrderForCustomerActivity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }
            }

        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            isApiLastPage = false
            binding.tvErrorMessage.visibility = View.GONE
            getAllProductList()
            Utils.hideKeyboard(this@CreateNewOrderForCustomerActivity)
        }

        // handing action for brand filter

        binding.etSearchBrand.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPageForBrand = 1
                getBrandList()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchBrand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerBrand);

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchBrand.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearchBrand.visibility = View.GONE
                    isApiLastPageForBrand = false
                    getBrandList()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerBrand, delay);
                }
            }

        })

        binding.ivClearSearchBrand.setOnClickListener {
            binding.etSearchBrand.setText("")
            isApiLastPageForBrand = false
            getBrandList()
        }

        //---------------

        binding.mainContent.setOnClickListener {
            hideKeyboard()
            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE
            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE
        }

        binding.tvBrandFilter.setOnClickListener {
            hideKeyboard()
            binding.clBrandFilter.isVisible = binding.clBrandFilter.isVisible.not()
            binding.clCategoryFilter.visibility = View.GONE
        }

        binding.tvCategoryFilter.setOnClickListener {
            hideKeyboard()
            binding.clCategoryFilter.isVisible = binding.clCategoryFilter.isVisible.not()
            binding.clBrandFilter.visibility = View.GONE
        }

        binding.tvClearFilter.setOnClickListener {
            category = ""
            filteredBrandList.clear()
            binding.tvBrandFilter.text = resources.getString(R.string.brand)
            binding.tvCategoryFilter.text = resources.getString(R.string.category)

            currentPage = 1
            isApiLastPage = false

            binding.tvErrorMessage.visibility = View.GONE

            productList.clear()
            createOrderForCustomerAdapter.notifyDataSetChanged()

            brandList.forEach {
                it.isSelected = false
            }
            brandAdapter.notifyDataSetChanged()

            categoryList.forEach {
                it.isSelected = false
            }

            categoryFilterAdapter.notifyDataSetChanged()

            binding.shimmerProduct.visibility = View.VISIBLE
            getAllProductList()

            binding.tvClearFilter.visibility = View.GONE

            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE

            hideKeyboard()
        }


        // handing action for category search filter ------

        binding.etSearchCategory.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                allCategoryList()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerCategory);

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchCategory.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearchCategory.visibility = View.GONE
                    allCategoryList()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerCategory, delay);
                }
            }

        })

        binding.ivClearSearchCategory.setOnClickListener {
            binding.etSearchCategory.setText("")
            allCategoryList()
        }

        //---------------

    }

    private val inputFinishCheckerCategory = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            allCategoryList()
        }
    }

    private fun allCategoryList() {
        productViewModel.getAllCategoryList(
            customerId,
            binding.etSearchCategory.text.toString(),
            hasInternetConnection()
        )
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            validateSearch()
        }
    }
    private val inputFinishCheckerBrand = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPageForBrand = 1
            getBrandList()
        }
    }

    private fun getAllProductList() {
        currentPage = 1
        productList.clear()
        initRecyclerView()
        binding.shimmerProduct.visibility = View.VISIBLE
        isValidateSearch = false

        loadProducts()
    }

    private fun validateSearch() {
        if (binding.etSearch.text.toString().isNotEmpty()) {
            isValidateSearch = true
            binding.shimmerProduct.visibility = View.VISIBLE
            binding.tvErrorMessage.visibility = View.GONE

            productList.clear()
            initRecyclerView()

            loadProducts()
        } else {
            Toast.makeText(this, "Please enter something!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProducts() {
        orderViewModel.getSearchResultProductList(
            customerId!!,
            binding.etSearch.text.toString(),
            category,
            filteredBrandList,
            currentPage,
            hasInternetConnection()
        )
    }

    private fun manageSharedPref() {
        if (SharedPref.getInstance().getString(CART_MODEL) != null) {
            val response = SharedPref.getInstance().getString(CART_MODEL)

            if (!response.equals("")) {
                cartListResponseModel = Gson().fromJson(response, OrderData::class.java)

                if (cartListResponseModel?.customerId != null
                    && cartListResponseModel?.customerId == customerId
                ) {
                    if (cartListResponseModel?.items.isNullOrEmpty().not()) {
                        cartAddedProductList = ArrayList()
                        cartAddedProductList.addAll(cartListResponseModel?.items!!)

                        binding.ivCart.visibility = View.VISIBLE
                        binding.tvCartItemCount.text = "" + cartListResponseModel?.items?.size
                        binding.tvCartItemCount.visibility = View.VISIBLE


                        cartAddedProductList.forEach { cartItem ->
                            productList.forEach { productList ->
                                run {
                                    if (cartItem.id == productList.id) {
                                        productList.qty = cartItem.qty
                                        productList.addedVariantSize = cartItem.variantSize
                                        productList.selectedPackagingLevel =
                                            cartItem.selectedPackagingLevel
                                        productList.addedToCart = true
                                        if (cartItem.updateOrder.not() && intent.hasExtra(
                                                AppConstant.ORDER_EDIT
                                            )
                                        ) {
                                            productList.isEnableUpdateQuantity = false
                                        }
                                    }
                                }
                            }
                        }

                        createOrderForCustomerAdapter.notifyDataSetChanged()


                        binding.btnLayout.visibility = View.VISIBLE
                    } else {
                        productList.forEach { productList ->
                            productList.addedToCart = false
                        }
                        cartAddedProductList.clear()
                        createOrderForCustomerAdapter.notifyDataSetChanged()
                        binding.tvCartItemCount.text = "0"
                        binding.tvCartItemCount.visibility = View.GONE
                    }
                }
            } else {
                binding.tvCartItemCount.visibility = View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvProductItem.layoutManager = linearLayoutManager
        createOrderForCustomerAdapter = CrateOrderForCustomerAdapter(productList, this)
        binding.rvProductItem.adapter = createOrderForCustomerAdapter

        binding.rvProductItem.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvProductItem.setOnTouchListener { _, _ ->
            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE
            hideKeyboard()
            false
        }

        val layoutManager1 = LinearLayoutManager(this)
        binding.rvBrandFilterList.setHasFixedSize(true)
        binding.rvBrandFilterList.layoutManager = layoutManager1
        brandAdapter = ProductCategoryListForAssignAdapter(
            brandList,
            this,
        )
        binding.rvBrandFilterList.adapter = brandAdapter
        binding.rvBrandFilterList.addOnScrollListener(object :
            PaginationScrollListener(layoutManager1) {
            override fun loadMoreItems() {
                isPageLoadingForBrand = true
                currentPageForBrand += 1
                getBrandList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPageForBrand
            }

            override fun isLoading(): Boolean {
                return isPageLoadingForBrand
            }
        })

        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.layoutManager = LinearLayoutManager(this)
        categoryFilterAdapter = ProductCategoryFilterAdapter(categoryList, this)
        binding.rvCategoryList.adapter = categoryFilterAdapter
    }

    private fun getBrandList() {
        productViewModel.getBrandList(
            binding.etSearchBrand.text.toString(),
            currentPageForBrand,
            hasInternetConnection()
        )
    }

    private fun loadNextPage() {
        loadProducts()

        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        orderViewModel.productListLiveData.observe(this) { data ->
            binding.paginationProgressBar.visibility = View.GONE
            binding.shimmerProduct.visibility = View.GONE
            binding.tvErrorMessage.visibility = View.GONE

            if (data.error != true) {
                isPageLoading = false

                data.data?.let { it ->
                    if (it.isNotEmpty()) {
                        productList.addAll(it)
                        createOrderForCustomerAdapter.notifyDataSetChanged()
                        manageSharedPref()

                        if (productList.size < 30) {
                            isApiLastPage = true
                        }
                    } else {
                        isApiLastPage = true
                        if (currentPage == 1) {
                            binding.tvErrorMessage.visibility = View.VISIBLE
                            productList.clear()
                            createOrderForCustomerAdapter.notifyDataSetChanged()
                        } else {
                            binding.tvErrorMessage.visibility = View.GONE
                        }
                    }
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }


        productViewModel.productCategoryLiveData.observe(this) {
            it.data?.let { list ->
                categoryList.clear()
                categoryList.addAll(list)
                categoryFilterAdapter.notifyDataSetChanged()
            }
        }

        productViewModel.brandListLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    if (currentPageForBrand == 1) {
                        brandList.clear()
                    }
                    it.data?.forEach { brand ->
                        val model = AllCategoryResponseModel()
                        model.name = brand.name
                        if (filteredBrandList.contains(brand.name)) {
                            model.isSelected = true
                        }
                        brandList.add(model)
                    }

                    brandAdapter.notifyDataSetChanged()

                    if (it.data!!.size < 30) {
                        isApiLastPageForBrand = true
                    }
                } else {
                    if (currentPageForBrand == 1) {
                        isApiLastPageForBrand = true
                    }
                }
            }
        }
    }

    override fun onAddToCart(model: ProductList, position: Int) {
        if (model.primaryProduct == null || model.primaryProduct == 0) {
            productList[position].addedToCart = true
            productList[position].qty = 1.0
            addToCart(model, position)
            binding.btnLayout.visibility = View.VISIBLE
        } else {
            val fragment =
                VariantsBottomSheetDialogFragment.newInstance(
                    this, model,
                    customerId!!, position
                )
            fragment.show(
                supportFragmentManager,
                VariantsBottomSheetDialogFragment::class.java.name
            )
        }
    }

    override fun onGetProductInfo(model: ProductList, position: Int) {
        if (getKeyBoardVisibility() || binding.clBrandFilter.isVisible || binding.clCategoryFilter.isVisible) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE
        } else {
            hideKeyboard()
            if (hasInternetConnection()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val fragment =
                        ProductDetailsBottomSheetDialogFragment(
                            this, model,
                            customerId!!, position
                        )
                    fragment.show(supportFragmentManager, AppConstant.GET_PRODUCT_INFO)
                }, 1000)
            }
        }
    }

    private fun getKeyBoardVisibility(): Boolean {

        val viewTreeObserver: ViewTreeObserver = binding.mainContent.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            val r = Rect();
            binding.mainContent.getWindowVisibleDisplayFrame(r);
            val screenHeight = binding.mainContent.rootView.height;

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                }
            }
        }

        return isKeyboardShowing
    }

    override fun onChangeQuantity(model: ProductList, qty: Double) {
        cartAddedProductList.forEach { cartItem ->
            if (cartItem.id == model.id) {
                cartItem.qty = qty
                return@forEach
            }
        }

        cartListResponseModel?.items = cartAddedProductList

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
                val cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null) {
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

            }
        }


        SharedPref.getInstance().putModelClass(CART_MODEL, cartListResponseModel)
    }

    override fun getPriceSlabInfo(model: ProductList) {
        if (getKeyBoardVisibility()) {
            hideKeyboard()
        } else {
            val bottomSheet = PriceSlabBottomSheetDialogFragment(model)
            bottomSheet.show(supportFragmentManager, AppConstant.PRODUCT_INFO)
        }
    }

    override fun getPackagingLevelInfo(model: ProductList) {
        val fragment = PkgLevelBottomSheetDialogFragment(model)
        fragment.show(supportFragmentManager, PkgLevelBottomSheetDialogFragment::class.java.name)
    }

    override fun onNextButtonClick() {
        manageCart()
        hideKeyboard()
        if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            updateProduct()
        } else {
            startActivity(
                Intent(this, CartActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER_ID, customerId)
                    .putExtra(AppConstant.CUSTOMER, customerModel)
                    .putExtra(AppConstant.PAYMENT_INFO, paymentTerms)
                    .putExtra(AppConstant.SELECTED_DISTRIBUTOR, selectedDistributor)
                    .putExtra(AppConstant.IS_TELEPHONIC_ORDER, isTelePhonicOrder)
            )
        }
    }

    override fun changePackagingLevel(
        model: ProductList,
        position: Int,
        selectedPackagingLevel: PackagingLevelModel
    ) {
        cartAddedProductList.forEach { cartItem ->
            if (cartItem.id == model.id) {
                cartItem.selectedPackagingLevel = selectedPackagingLevel
                cartItem.packagingSize = selectedPackagingLevel.size
                cartItem.packagingUnit = selectedPackagingLevel.unit
                return@forEach
            }
        }

        cartListResponseModel?.items = cartAddedProductList

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
                val cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null) {
                    cartListResponseModel?.fullFilledById = cartResponseModel?.fullFilledBy!!.id
                }

            }
        }


        SharedPref.getInstance().putModelClass(CART_MODEL, cartListResponseModel)
    }

    private fun manageCart() {

        val newCartListResponseModel = OrderData(customerId = customerId)
        newCartListResponseModel.customerId = customerId
        newCartListResponseModel.customerName = customerName
        newCartListResponseModel.customerPaymentTerms = paymentTerms
        newCartListResponseModel.paymentOptionCheck = paymentTerms

        newCartListResponseModel.items = cartAddedProductList

        if (cartListResponseModel != null) {
            newCartListResponseModel.id = cartListResponseModel?.id
            newCartListResponseModel.address = cartListResponseModel?.address
            newCartListResponseModel.discountDetails = cartListResponseModel?.discountDetails
            newCartListResponseModel.chargesDetails = cartListResponseModel?.chargesDetails
            newCartListResponseModel.paymentDetails = cartListResponseModel?.paymentDetails
            newCartListResponseModel.paymentOptionCheck = cartListResponseModel?.paymentOptionCheck
            newCartListResponseModel.paymentMode = cartListResponseModel?.paymentMode
            newCartListResponseModel.orderImagesInfo = cartListResponseModel?.orderImagesInfo
            newCartListResponseModel.adminComment = cartListResponseModel?.adminComment
            newCartListResponseModel.comment = cartListResponseModel?.comment
            newCartListResponseModel.remainingPaymentDays =
                cartListResponseModel?.remainingPaymentDays
            newCartListResponseModel.fullFilledBy = cartListResponseModel?.fullFilledBy
            if (cartListResponseModel?.items.isNullOrEmpty().not()) {
                newCartListResponseModel.items = cartListResponseModel?.items
            }

        }

        if (SharedPref.getInstance().getString(CART_MODEL).isNullOrEmpty().not()) {
            val response = SharedPref.getInstance().getString(CART_MODEL)
            if (!response.equals("")) {
                val cartResponseModel = Gson().fromJson(response, OrderData::class.java)
                if (cartResponseModel?.fullFilledBy != null) {
                    cartListResponseModel?.fullFilledById = cartResponseModel.fullFilledBy!!.id
                    newCartListResponseModel.fullFilledById = cartResponseModel.fullFilledBy!!.id
                } else if (selectedDistributor != null) {
                    cartListResponseModel?.fullFilledById = selectedDistributor?.id
                    newCartListResponseModel.fullFilledById = selectedDistributor?.id
                }

            }
        }


        SharedPref.getInstance().putModelClass(CART_MODEL, newCartListResponseModel)
    }


    @SuppressLint("SetTextI18n")
    private fun addToCart(model: ProductList, position: Int) {
        val cartItem = CartItem()
        cartItem.id = model.id
        cartItem.category = model.category
        cartItem.name = model.name
        cartItem.priceAfterDiscount = model.price
        cartItem.qty = 1.0
        cartItem.unit = model.unit
        cartItem.code = model.code
        cartItem.isAddedToCart = true
        cartItem.price = model.price
        cartItem.gst_exclusive = model.gst_exclusive
        cartItem.gst = model.gst
        cartItem.telescopePricing = model.telescopePricing
        cartItem.packagingLevel = model.packaging_level
        cartItem.selectedPackagingLevel = model.selectedPackagingLevel
        cartItem.variantName = model.variantName
        cartItem.updateOrder = true

        if (model.selectedPackagingLevel != null) {
            cartItem.packagingSize = model.selectedPackagingLevel.size
            cartItem.packagingUnit = model.selectedPackagingLevel.unit
        } else {
            cartItem.packagingSize = model.packaging_size
            cartItem.packagingUnit = model.packaging_unit
        }

        cartAddedProductList.add(cartItem)
        cartListResponseModel?.items = cartAddedProductList

        manageCart()

        binding.rvProductItem.scrollToPosition(position + 1)

        if (intent.hasExtra(AppConstant.ORDER_EDIT).not()) {
            binding.tvCartItemCount.visibility = View.VISIBLE
            binding.tvCartItemCount.text = "" + cartAddedProductList.size
        }
    }

    override fun onProductAddToCartFromBottomSheet(model: ProductDetailInfoModel, position: Int) {
        val indexForProductList = productList.indexOfFirst { it.id == model.id }

        if (indexForProductList != -1) {
            productList[indexForProductList].qty = model.qty
            createOrderForCustomerAdapter.notifyItemChanged(indexForProductList, true)
        }

        manageSharedPref()
    }


    private fun updateProduct() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        getAllProductList()
    }

    @SuppressLint("SetTextI18n")
    override fun setCategorySelect(checked: Boolean, model: AllCategoryResponseModel) {
        if (checked) {
            filteredBrandList.add(model.name ?: "")
        } else {
            if (filteredBrandList.size > 0) {
                val index = filteredBrandList.indexOfLast { it == model.name }
                if (index != -1) {
                    filteredBrandList.removeAt(index)
                }
            }
        }

        if (filteredBrandList.isEmpty()) {
            binding.tvBrandFilter.text = resources.getString(R.string.brand)

            if (category.isEmpty()) {
                binding.tvClearFilter.visibility = View.GONE
            }
        } else {
            binding.tvBrandFilter.text = "Brand (${filteredBrandList.size})"
            binding.tvClearFilter.visibility = View.VISIBLE
        }

        productList.clear()
        createOrderForCustomerAdapter.notifyDataSetChanged()

        binding.shimmerProduct.visibility = View.VISIBLE
        loadNextPage()
    }

    override fun filterCategory(position: Int, model: AllCategoryResponseModel) {
        for (i in categoryList.indices) {
            categoryList[i].isSelected = false
        }

        categoryList[position].isSelected = true
        categoryFilterAdapter.notifyDataSetChanged()

        category = model.name ?: ""

        binding.tvCategoryFilter.text = category
        currentPage = 1
        productList.clear()
        createOrderForCustomerAdapter.notifyDataSetChanged()

        binding.shimmerProduct.visibility = View.VISIBLE
        loadNextPage()

        binding.tvClearFilter.visibility = View.VISIBLE

        binding.clCategoryFilter.visibility = View.GONE

        hideKeyboard()
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
        getUserCurrentLocation()
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

    override fun onSuccessfullyMarkAttendance() {
        super.onSuccessfullyMarkAttendance()
        isUserStartedHisDay = true
    }

    override fun onDismissDialogForGeoFencing() {
        super.onDismissDialogForGeoFencing()
        finish()
    }

    override fun onDismissDialogForStartDay() {
        super.onDismissDialogForStartDay()
        finish()
    }
    
  
    

}
