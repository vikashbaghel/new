package com.app.rupyz.sales.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrdersFilterBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER
import com.app.rupyz.generic.utils.AppConstant.FULLFILLED
import com.app.rupyz.generic.utils.AppConstant.FULLFILLED_MAP
import com.app.rupyz.generic.utils.AppConstant.ORDER
import com.app.rupyz.generic.utils.AppConstant.ORDER_MAP
import com.app.rupyz.generic.utils.AppConstant.PAYMENT
import com.app.rupyz.generic.utils.AppConstant.PAYMENT_MAP
import com.app.rupyz.generic.utils.AppConstant.RECEIVED
import com.app.rupyz.generic.utils.AppConstant.STAFF
import com.app.rupyz.generic.utils.AppConstant.selected_item
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AdminData
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.gallery.FilterData
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.ReceivedData
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.gallery.CustomLinearLayoutManager
import com.app.rupyz.sales.gallery.adapter.CustomerAdapter
import com.app.rupyz.sales.gallery.adapter.DebounceClickListener
import com.app.rupyz.sales.gallery.adapter.FilterGalleryPicsAdapter
import com.app.rupyz.sales.gallery.adapter.OnItemCheckCustomerListener
import com.app.rupyz.sales.gallery.adapter.OnItemCheckListener
import com.app.rupyz.sales.gallery.adapter.StaffAdapter
import com.app.rupyz.sales.orders.adapter.FullFilledAdapter
import com.app.rupyz.sales.orders.adapter.OnItemCheckAdminListener
import com.app.rupyz.sales.orders.adapter.OnItemCheckFullFilledListener
import com.app.rupyz.sales.orders.adapter.OnItemPaymentListener
import com.app.rupyz.sales.orders.adapter.OnItemReceivedOnListener
import com.app.rupyz.sales.orders.adapter.OrderReceivedOnAdapter
import com.app.rupyz.sales.orders.adapter.OrderTakenAdminAdapter
import com.app.rupyz.sales.orders.adapter.PaymentTermAdapterAdapter
import com.app.rupyz.sales.staff.StaffViewModel


class OrdersFilterActivity : BaseActivity(), OnItemCheckListener, OnItemCheckCustomerListener,
    OnItemReceivedOnListener, OnItemCheckFullFilledListener, DebounceClickListener,
    OnItemPaymentListener, OnItemCheckAdminListener {
    private lateinit var binding: ActivityOrdersFilterBinding

    private lateinit var adapter: FilterGalleryPicsAdapter
    private lateinit var orderReceivedOnAdapter: OrderReceivedOnAdapter
    private lateinit var paymentTermAdapterAdapter: PaymentTermAdapterAdapter
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var fullFilledAdapter: FullFilledAdapter
    private lateinit var orderTakenAdminAdapter: OrderTakenAdminAdapter
    private lateinit var staffAdapter: StaffAdapter

    private var currentSelectedStaffItems: ArrayList<Int> = ArrayList()
    private var currentSelectedCustomerItems: ArrayList<Int> = ArrayList()
    private var currentSelectedFullFilledItems: ArrayList<Int?> = ArrayList()
    private var currentSelectedOrderTakenItems: ArrayList<Int> = ArrayList()
    private var currentSelectedReceivedOnItems: ArrayList<String> = ArrayList()
    private var currentSelectedPaymentOnItems: ArrayList<String> = ArrayList()
    private var isApiLastPage = false
    private var isPageLoading = false
    private var currentPage = 1
    private lateinit var filterList: ArrayList<FilterData>
    private lateinit var orderReceivedList: ArrayList<ReceivedData>
    private var paymentList = ArrayList<ReceivedData>()
    private var customerList = ArrayList<CustomerData>()
    private var orderAdminList = ArrayList<AdminData>()
    private var fullFilledList = ArrayList<CustomerData>()
    private var staffList = ArrayList<StaffData>()
    private var filterCustomerLevel = ""
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""
    private lateinit var staffRole: String
    var delay: Long = 500
    var selectItem: Int = 0
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)
    private val staffViewModel: StaffViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()
    private lateinit var checkboxFullFilledMap: HashMap<Int, Boolean>
    private lateinit var checkboxCustomerMap: HashMap<Int, Boolean>
    private lateinit var checkboxStaffMap: HashMap<Int, Boolean>
    private lateinit var checkboxReceivedOnMap: HashMap<Int, Boolean>
    private lateinit var checkboxPaymentMap: HashMap<Int, Boolean>
    private lateinit var checkboxAdminMap: HashMap<Int, Boolean>


    companion object {
        private lateinit var listerPicture: IOrderFilterListener
        private lateinit var listenerStateData: OnItemStateListener
        fun newInstance(
            listener: IOrderFilterListener,
            listenerState: OnItemStateListener
        ): OrdersFilterActivity {
            listerPicture = listener
            listenerStateData = listenerState
            return OrdersFilterActivity()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(FULLFILLED_MAP, checkboxFullFilledMap)
        outState.putSerializable(AppConstant.CUSTOMER_MAP, checkboxCustomerMap)
        outState.putSerializable(AppConstant.STAFF_MAP, checkboxStaffMap)
        outState.putSerializable(AppConstant.RECEIVED_MAP, checkboxReceivedOnMap)
        outState.putSerializable(PAYMENT_MAP, checkboxPaymentMap)
        outState.putSerializable(ORDER_MAP, checkboxAdminMap)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        checkboxFullFilledMap =
            savedInstanceState.getSerializable(FULLFILLED_MAP) as HashMap<Int, Boolean>
        checkboxCustomerMap =
            savedInstanceState.getSerializable(AppConstant.CUSTOMER_MAP) as HashMap<Int, Boolean>
        checkboxStaffMap =
            savedInstanceState.getSerializable(AppConstant.STAFF_MAP) as HashMap<Int, Boolean>
        checkboxReceivedOnMap =
            savedInstanceState.getSerializable(AppConstant.RECEIVED_MAP) as HashMap<Int, Boolean>
        checkboxPaymentMap =
            savedInstanceState.getSerializable(PAYMENT_MAP) as HashMap<Int, Boolean>
        checkboxAdminMap =
            savedInstanceState.getSerializable(ORDER_MAP) as HashMap<Int, Boolean>

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //intent.hasExtra(AppConstant.ORDER_ID)
        staffRole = ""
        checkboxFullFilledMap =
            intent?.getSerializableExtra(FULLFILLED_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()
        checkboxStaffMap =
            intent?.getSerializableExtra(AppConstant.STAFF_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()
        checkboxReceivedOnMap =
            intent?.getSerializableExtra(AppConstant.RECEIVED_MAP) as? HashMap<Int, Boolean>
                ?: HashMap()

        if (intent.hasExtra(AppConstant.CUSTOMER_MAP)) {
            checkboxCustomerMap =
                intent?.getSerializableExtra(AppConstant.CUSTOMER_MAP) as? HashMap<Int, Boolean>
                    ?: HashMap()
        }
        if (intent.hasExtra(ORDER_MAP)) {
            checkboxAdminMap =
                intent?.getSerializableExtra(ORDER_MAP) as? HashMap<Int, Boolean>
                    ?: HashMap()
        }
        if (intent.hasExtra(PAYMENT_MAP)) {
            checkboxPaymentMap =
                intent?.getSerializableExtra(PAYMENT_MAP) as? HashMap<Int, Boolean>
                    ?: HashMap()
        }

        // currentSelectedStateItems = intent.getSerializableExtra(STATE) as ArrayList<String>
        currentSelectedStaffItems = intent.getSerializableExtra(STAFF) as ArrayList<Int>
        currentSelectedCustomerItems =
            intent.getSerializableExtra(CUSTOMER) as ArrayList<Int>
        currentSelectedFullFilledItems =
            intent.getSerializableExtra(FULLFILLED) as ArrayList<Int?>
        currentSelectedReceivedOnItems = intent.getSerializableExtra(RECEIVED) as ArrayList<String>
        currentSelectedPaymentOnItems = intent.getSerializableExtra(PAYMENT) as ArrayList<String>
        currentSelectedOrderTakenItems = intent.getSerializableExtra(ORDER) as ArrayList<Int>

        binding.imgClose.setOnClickListener {
            listenerStateData.onItemStateHolder(
                checkboxReceivedOnMap,
                checkboxCustomerMap,
                checkboxStaffMap,
                checkboxFullFilledMap,
                checkboxPaymentMap,
                checkboxAdminMap

            )
            finish()
            selected_item = 0
        }

        filterData()
        listData()
        initRecyclerView()
        initRecyclerViewStaff()
        initRecyclerViewCustomer()
        initRecyclerViewFullFilled()
        initRecyclerViewAdmin()
        initObservers()
        binding.btnApply.setOnClickListener {
            listerPicture.applyFilter(
                currentSelectedCustomerItems,
                currentSelectedStaffItems,
                currentSelectedReceivedOnItems,
                currentSelectedFullFilledItems,
                currentSelectedOrderTakenItems,
                currentSelectedPaymentOnItems

            )
            listenerStateData.onItemStateHolder(
                checkboxReceivedOnMap,
                checkboxCustomerMap,
                checkboxStaffMap,
                checkboxFullFilledMap,
                checkboxPaymentMap,
                checkboxAdminMap

            )
            finish()
            selected_item = 0

        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                listenerStateData.onItemStateHolder(
                    checkboxReceivedOnMap,
                    checkboxCustomerMap,
                    checkboxStaffMap,
                    checkboxFullFilledMap,
                    checkboxPaymentMap,
                    checkboxAdminMap
                )
                finish()
                selected_item = 0
            }
        })

    }


    private val inputFinishStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadStaffData()
        }
    }

    private val inputFinishFullFilled = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadCustomerFullFilledPage()
        }
    }
    private val inputFinishOrderTaken = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadOrderTakenByAdminPage()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            loadCustomerList()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun filterData() {
        binding.ivSearch.setOnClickListener {
            when (selectItem) {
                1 -> {
                    currentPage = 1
                    customerList.clear()
                    customerAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadCustomerList()
                }

                2 -> {
                    currentPage = 1
                    staffList.clear()
                    staffAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadStaffData()
                }


                3 -> {
                    if (!isStaffUserActivity()) {
                        currentPage = 1
                        orderAdminList.clear()
                        orderTakenAdminAdapter.notifyDataSetChanged()
                        Utils.hideKeyboard(this)
                        loadOrderTakenByAdminPage()
                    } else {
                        currentPage = 1
                        fullFilledList.clear()
                        fullFilledAdapter.notifyDataSetChanged()
                        Utils.hideKeyboard(this)
                        loadCustomerFullFilledPage()
                    }


                }

                4 -> {
                    currentPage = 1
                    fullFilledList.clear()
                    fullFilledAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadCustomerFullFilledPage()
                }


            }

        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                when (selectItem) {


                    1 -> {
                        currentPage = 1
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                        loadCustomerList()
                        Utils.hideKeyboard(this)


                    }

                    2 -> {
                        currentPage = 1
                        staffList.clear()
                        staffAdapter.notifyDataSetChanged()
                        loadStaffData()
                        Utils.hideKeyboard(this)
                    }


                    3 -> {
                        if (!isStaffUserActivity()) {
                            currentPage = 1
                            orderAdminList.clear()
                            orderTakenAdminAdapter.notifyDataSetChanged()
                            Utils.hideKeyboard(this)
                            loadCustomerFullFilledPage()
                        } else {
                            currentPage = 1
                            fullFilledList.clear()
                            fullFilledAdapter.notifyDataSetChanged()
                            Utils.hideKeyboard(this)
                            loadCustomerFullFilledPage()
                        }


                    }

                    4 -> {
                        currentPage = 1
                        fullFilledList.clear()
                        fullFilledAdapter.notifyDataSetChanged()
                        Utils.hideKeyboard(this)
                        loadCustomerFullFilledPage()

                    }


                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)
                handler.removeCallbacks(inputFinishStaff)
                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    if (!isStaffUserActivity()) {
                        when (selectItem) {

                            1 -> {
                                loadCustomerList()
                            }

                            2 -> {
                                loadStaffData()
                            }


                            3, 4 -> {
                                loadOrderTakenByAdminPage()
                                loadCustomerFullFilledPage()
                            }


                        }
                    } else {
                        when (selectItem) {

                            1 -> {
                                loadCustomerList()
                            }

                            2 -> {
                                loadStaffData()
                            }


                            3 -> {
                                loadCustomerFullFilledPage()
                            }


                        }
                    }


                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    when (selectItem) {

                        1 -> {
                            lastTextEdit = System.currentTimeMillis()
                            handler.postDelayed(inputFinishChecker, delay)
                        }

                        2 -> {
                            lastTextEdit = System.currentTimeMillis()
                            handler.postDelayed(inputFinishStaff, delay)
                        }

                        3 -> {
                            if (!isStaffUserActivity()) {
                                lastTextEdit = System.currentTimeMillis()
                                handler.postDelayed(inputFinishOrderTaken, delay)
                            } else {
                                lastTextEdit = System.currentTimeMillis()
                                handler.postDelayed(inputFinishFullFilled, delay)
                            }

                        }

                        4 -> {
                            lastTextEdit = System.currentTimeMillis()
                            handler.postDelayed(inputFinishFullFilled, delay)
                        }
                    }

                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            when (selectItem) {
                1 -> {
                    binding.clEmptyData.visibility = View.GONE
                    customerList.clear()
                    loadCustomerList()
                    customerAdapter.notifyDataSetChanged()
                }

                2 -> {
                    binding.clEmptyData.visibility = View.GONE
                    staffList.clear()
                    loadStaffData()
                    staffAdapter.notifyDataSetChanged()
                }

                3 -> {
                    currentPage = 1
                    orderAdminList.clear()
                    orderTakenAdminAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadOrderTakenByAdminPage()


                }

                4 -> {
                    currentPage = 1
                    fullFilledList.clear()
                    fullFilledAdapter.notifyDataSetChanged()
                    Utils.hideKeyboard(this)
                    loadCustomerFullFilledPage()


                }
            }
        }

    }


    private fun initRecyclerView() {
        listOrderData()
        // listPaymentData()
        adapter = FilterGalleryPicsAdapter(filterList, this)
        binding.rvFilter.adapter = adapter
        orderReceivedOnAdapter =
            OrderReceivedOnAdapter(orderReceivedList, this, checkboxReceivedOnMap)
        binding.rvOrder.adapter = orderReceivedOnAdapter
        paymentTermAdapterAdapter = PaymentTermAdapterAdapter(paymentList, this, checkboxPaymentMap)
        binding.rvPayment.adapter = paymentTermAdapterAdapter
        adapter.notifyDataSetChanged()

    }

    private fun loadCustomerList() {
        staffList.clear()
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        customerViewModel.getCustomerList(
            null,
            binding.etSearch.text.toString(),
            filterCustomerLevel,
            filterCustomerType,
            sortByOrder,
            currentPage,
            hasInternetConnection()
        )
    }

    private fun initRecyclerViewCustomer() {
        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvCustomer.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        customerAdapter = CustomerAdapter(customerList, this, checkboxCustomerMap)
        binding.rvCustomer.adapter = customerAdapter
        binding.rvCustomer.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }


    private fun initRecyclerViewFullFilled() {
        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvDataView.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        fullFilledAdapter = FullFilledAdapter(fullFilledList, this, checkboxFullFilledMap)
        binding.rvDataView.adapter = fullFilledAdapter

        binding.rvDataView.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerFullFilledPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }

    private fun initRecyclerViewAdmin() {
        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvOrderAdmin.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        orderTakenAdminAdapter = OrderTakenAdminAdapter(orderAdminList, this, checkboxAdminMap)
        binding.rvOrderAdmin.adapter = orderTakenAdminAdapter

        binding.rvOrderAdmin.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadOrderTakenByAdminPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }


    private fun initRecyclerViewStaff() {

        val linearLayoutManager = CustomLinearLayoutManager(this)
        binding.rvStaff.layoutManager = linearLayoutManager
        binding.clEmptyData.visibility = View.GONE
        staffAdapter = StaffAdapter(staffList, this, checkboxStaffMap)
        binding.rvStaff.adapter = staffAdapter
        binding.rvStaff.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadStaffData()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })


    }

    private fun loadStaffData() {
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        staffViewModel.getStaffList(
            staffRole, binding.etSearch.text.toString(), currentPage, hasInternetConnection()
        )
    }

    private fun loadCustomerFullFilledPage() {

        staffList.clear()
        customerList.clear()
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        customerViewModel.getCustomerList(
            null,
            binding.etSearch.text.toString(),
            "",
            ArrayList(),
            AppConstant.SORTING_LEVEL_ASCENDING,
            currentPage,
            hasInternetConnection()
        )
    }

    private fun loadOrderTakenByAdminPage() {

        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }
        isApiLastPage = false
        customerViewModel.getOrderTakenByAdminList(
            currentPage,
            binding.etSearch.text.toString(),
            true,
            hasInternetConnection()
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        staffViewModel.getStaffListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            staffList.clear()
                        }

                        staffList.addAll(it)
                        staffAdapter.notifyDataSetChanged()



                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        staffList.clear()
                        staffAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                showToast(data.message)
            }
        }

        customerViewModel.getCustomerListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            customerList.clear()
                            fullFilledList.clear()
                        }
                        if (selectItem == 1) {
                            customerList.addAll(it)
                            customerAdapter.notifyDataSetChanged()
                        } else {
                            fullFilledList.addAll(it)
                            fullFilledAdapter.notifyDataSetChanged()
                        }


                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        customerList.clear()
                        fullFilledList.clear()
                        customerAdapter.notifyDataSetChanged()
                        fullFilledAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
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

        customerViewModel.orderAdminLiveData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            orderAdminList.clear()
                            orderAdminList.addAll(it)
                            orderTakenAdminAdapter.notifyDataSetChanged()
                        }

                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        customerList.clear()
                        fullFilledList.clear()
                        orderAdminList.clear()
                        customerAdapter.notifyDataSetChanged()
                        fullFilledAdapter.notifyDataSetChanged()
                        orderTakenAdminAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    // logout()
                } else {
                    showToast(data.message)
                }
            }
        }

    }


    private fun listData() {

        filterList = ArrayList()
        filterList.add(FilterData(resources.getString(R.string.order_received), true, 0))
        filterList.add(FilterData(resources.getString(R.string.customer), false, 1))
        filterList.add(FilterData(resources.getString(R.string.order_taken_staff), false, 2))
        if (!isStaffUserActivity()) {
            filterList.add(FilterData(resources.getString(R.string.order_taken_admin), false, -1))

        }
        filterList.add(FilterData(resources.getString(R.string.fulfilled_by), false, 3))
        filterList.add(FilterData(resources.getString(R.string.payment_terms), false, 4))
    }

    private fun listOrderData() {
        orderReceivedList = ArrayList()
        orderReceivedList.add(ReceivedData(resources.getString(R.string.all)))
        orderReceivedList.add(ReceivedData(resources.getString(R.string.sales_app)))
        orderReceivedList.add(ReceivedData(resources.getString(R.string.storefront)))

    }

    private fun listPaymentData() {
        paymentList.clear()
        paymentList.add(ReceivedData(AppConstant.PAYMENT_ON_DELIVERY))
        paymentList.add(ReceivedData(AppConstant.FULL_PAYMENT_IN_ADVANCE))
        paymentList.add(ReceivedData(AppConstant.PAYMENT_ON_NEXT_ORDER))
        paymentList.add(ReceivedData(AppConstant.PARTIAL_PAYMENT))
        paymentList.add(ReceivedData(AppConstant.CREDIT_DAYS))

    }


    interface IOrderFilterListener {
        fun applyFilter(
            customerType: ArrayList<Int>,
            staffType: ArrayList<Int>,
            receivedType: ArrayList<String>,
            fullFilledType: ArrayList<Int?>,
            orderAdminType: ArrayList<Int>,
            currentSelectedPaymentOnItems: ArrayList<String>


        )
    }

    interface OnItemStateListener {
        fun onItemStateHolder(
            checkboxReceivedOnMap: HashMap<Int, Boolean>,
            checkboxCustomerMap: HashMap<Int, Boolean>,
            checkboxStaffMap: HashMap<Int, Boolean>,
            checkboxFullFillMap: HashMap<Int, Boolean>,
            checkboxPaymentMap: HashMap<Int, Boolean>,
            checkboxAdminMap: HashMap<Int, Boolean>
        )

    }

    override fun onItemCheck(id: Int?) {
        currentSelectedStaffItems.add(id!!)
    }

    override fun onItemUncheck(id: Int?) {
        currentSelectedStaffItems.remove(id)

    }

    override fun onItemCustomerCheck(id: Int?) {
        currentSelectedCustomerItems.add(id!!)
        Log.e("tag,", "Customer$id")
    }

    override fun onItemCustomerUncheck(id: Int?) {
        currentSelectedCustomerItems.remove(id)
    }


    override fun onDebounceClick(position: Int, data: FilterData) {
        if (data.isSelected.not()) {
            binding.clEmptyData.visibility = View.GONE
            binding.etSearch.setText("")
            customerList.clear()
            staffList.clear()
            fullFilledList.clear()
            orderAdminList.clear()
            selectItem = position
            currentPage = 1
            customerAdapter.notifyDataSetChanged()
            staffAdapter.notifyDataSetChanged()
            orderReceivedOnAdapter.notifyDataSetChanged()
            //orderTakenAdminAdapter.notifyDataSetChanged()
            fullFilledAdapter.notifyDataSetChanged()

            when (position) {
                0 -> {
                    selectItem = position
                    binding.etSearch1.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvCustomer.visibility = View.GONE
                    binding.rvPayment.visibility = View.GONE
                    binding.rvOrder.visibility = View.VISIBLE
                    binding.rvOrderAdmin.gone()
                    listOrderData()
                }

                1 -> {
                    selectItem = position
                    binding.etSearch1.visibility = View.VISIBLE
                    binding.rvCustomer.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    binding.rvOrder.visibility = View.GONE
                    binding.rvPayment.visibility = View.GONE
                    binding.rvOrderAdmin.gone()
                    handler.postDelayed(inputFinishChecker, delay)

                }

                2 -> {
                    selectItem = position
                    binding.etSearch1.visibility = View.VISIBLE
                    binding.rvStaff.visibility = View.VISIBLE
                    binding.rvCustomer.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    binding.rvOrder.visibility = View.GONE
                    binding.rvPayment.visibility = View.GONE
                    binding.rvOrderAdmin.gone()
                    handler.postDelayed(inputFinishStaff, delay)

                }

                3 -> {
                    if (!isStaffUserActivity()) {
                        binding.etSearch1.visibility = View.VISIBLE
                        selectItem = position
                        binding.rvCustomer.visibility = View.GONE
                        binding.rvStaff.visibility = View.GONE
                        binding.rvOrder.visibility = View.GONE
                        binding.rvPayment.visibility = View.GONE
                        binding.rvDataView.gone()
                        binding.rvOrderAdmin.visibility()
                        handler.postDelayed(inputFinishOrderTaken, delay)
                    } else {
                        binding.etSearch1.visibility = View.VISIBLE
                        selectItem = position
                        binding.rvCustomer.visibility = View.GONE
                        binding.rvStaff.visibility = View.GONE
                        binding.rvOrder.visibility = View.GONE
                        binding.rvPayment.visibility = View.GONE
                        binding.rvDataView.visibility = View.VISIBLE
                        binding.rvOrderAdmin.gone()
                        handler.postDelayed(inputFinishFullFilled, delay)
                    }

                }

                4 -> {
                    if (!isStaffUserActivity()) {
                        binding.etSearch1.visibility = View.VISIBLE
                        selectItem = position
                        binding.rvCustomer.visibility = View.GONE
                        binding.rvStaff.visibility = View.GONE
                        binding.rvOrder.visibility = View.GONE
                        binding.rvPayment.visibility = View.GONE
                        binding.rvDataView.visibility = View.VISIBLE
                        binding.rvOrderAdmin.gone()
                        handler.postDelayed(inputFinishFullFilled, delay)
                    } else {
                        binding.etSearch1.gone()
                        selectItem = position
                        binding.rvCustomer.visibility = View.GONE
                        binding.rvStaff.visibility = View.GONE
                        binding.rvOrder.visibility = View.GONE
                        binding.rvDataView.visibility = View.GONE
                        binding.rvPayment.visibility = View.VISIBLE
                        binding.rvOrderAdmin.gone()
                        listPaymentData()

                    }


                }

                5 -> {
                    binding.etSearch1.gone()
                    selectItem = position
                    binding.rvCustomer.visibility = View.GONE
                    binding.rvStaff.visibility = View.GONE
                    binding.rvOrder.visibility = View.GONE
                    binding.rvDataView.visibility = View.GONE
                    binding.rvPayment.visibility = View.VISIBLE
                    binding.rvOrderAdmin.gone()
                    listPaymentData()
                }
            }





            filterList.forEach { it.isSelected = false }
            filterList[position].isSelected = true
            adapter.notifyDataSetChanged()
        }
    }


    override fun onItemFullFilledCheck(id: Int?) {
        currentSelectedFullFilledItems.add(id!!)
        Log.e("tag,", "Full$id")
    }

    override fun onItemFullFilledUncheck(id: Int?) {
        currentSelectedFullFilledItems.remove(id!!)
        Log.e("tag,", "Full$id")
    }

    override fun onItemReceivedOnCheck(id: String?) {
        currentSelectedReceivedOnItems.add(id!!)
        Log.e("tag,", "recived$id")
    }

    override fun onItemReceivedOnUncheck(id: String?) {
        currentSelectedReceivedOnItems.remove(id!!)
    }

    override fun onItemPaymentOnCheck(id: String?) {
        currentSelectedPaymentOnItems.add(id!!)
    }

    override fun onItemPaymentOnUncheck(id: String?) {
        currentSelectedPaymentOnItems.remove(id!!)
    }

    override fun onItemAdminCheck(id: Int?) {
        currentSelectedOrderTakenItems.add(id!!)
    }

    override fun onItemAdminUncheck(id: Int?) {
        currentSelectedOrderTakenItems.remove(id)
    }
}

