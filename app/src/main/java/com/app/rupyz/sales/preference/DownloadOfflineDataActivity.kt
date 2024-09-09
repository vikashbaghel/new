package com.app.rupyz.sales.preference

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityDownloadOfflineDataBinding
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.fromJson
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.OfflineProgressModel
import com.app.rupyz.model_kt.order.dashboard.DashboardData
import com.app.rupyz.sales.home.DashboardViewModel
import com.app.rupyz.sales.home.SalesMainActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar


class DownloadOfflineDataActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityDownloadOfflineDataBinding

    private val offlineViewModel: OfflineViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()

    private val pageSize = 100

    private var mainProgress = 0
    private var downloadCounter = 0

    private var brandPageCount = 1
    private var categoryPageCount = 1
    private var productPageCount = 1
    private var customerPageCount = 1
    private var customerTypePageCount = 1
    private var orderPageCount = 1
    private var staffPageCount = 1
    private var paymentPageCount = 1
    private var expenseHeadPageCount = 1
    private var expensePageCount = 1
    private var leadPageCount = 1
    private var leadCategoryPageCount = 1
    private var addressPageCount = 1
    private var staffRolePageCount = 1
    private var dispatchOrderPageCount = 1
    private var beatPageCount = 1

    private var productCount = 0
    private var customerCount = 0
    private var orderCount = 0
    private var staffCount = 0
    private var paymentCount = 0
    private var expenseCount = 0
    private var leadCount = 0
    private var addressCount = 0
    private var dispatchOrderCount = 0
    private var beatCount = 0

    private var offlineDataLastSyncedTime: String? = null

    private var offlineProgressHashMap: HashMap<String, Pair<Boolean, Int?>>? = null

    private var offlineProgressModel: OfflineProgressModel? = null

    private var dashBoardData: DashboardData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set FLAG_KEEP_SCREEN_ON to keep the screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityDownloadOfflineDataBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initLayout()

        initObservers()

        dashboardViewModel.getDashboardData(false)
    }

    private fun showCompletedDump(key: String) {
        when (key) {
            AppConstant.PRODUCT -> {
                binding.ivProductDownloadComplete.visibility = View.VISIBLE
                binding.productProgressBar.visibility = View.GONE
                binding.tvProductProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvProductProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.PRODUCT)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.PRODUCT)?.second}"
                        )
                incrementMainProgressBar(10)
            }

            AppConstant.CUSTOMER -> {
                binding.ivCustomerDownloadComplete.visibility = View.VISIBLE
                binding.customerProgressBar.visibility = View.GONE
                binding.tvCustomerProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvCustomerProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.CUSTOMER)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.CUSTOMER)?.second}"
                        )
                incrementMainProgressBar(8)
            }

            AppConstant.ORDER -> {
                binding.ivOrdersDownloadComplete.visibility = View.VISIBLE
                binding.ordersProgressBar.visibility = View.GONE
                binding.tvOrdersProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvOrdersProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.ORDER)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.ORDER)?.second}"
                        )
                incrementMainProgressBar(10)
            }

            AppConstant.STAFF -> {
                binding.ivStaffDownloadComplete.visibility = View.VISIBLE
                binding.staffProgressBar.visibility = View.GONE
                binding.tvStaffProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvStaffProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.STAFF)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.STAFF)?.second}"
                        )
                incrementMainProgressBar(10)
            }

            AppConstant.PAYMENT -> {
                binding.ivPaymentsDownloadComplete.visibility = View.VISIBLE
                binding.paymentsProgressBar.visibility = View.GONE
                binding.tvPaymentsProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvPaymentsProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.PAYMENT)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.PAYMENT)?.second}"
                        )
                incrementMainProgressBar(12)
            }

            AppConstant.EXPENSE -> {
                binding.ivExpensesDownloadComplete.visibility = View.VISIBLE
                binding.expensesProgressBar.visibility = View.GONE
                binding.tvExpensesProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvExpensesProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.EXPENSE)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.EXPENSE)?.second}"
                        )
                incrementMainProgressBar(10)
            }

            AppConstant.ADDRESS -> {
                binding.ivCustomerAddressDownloadComplete.visibility = View.VISIBLE
                binding.customerAddressProgressBar.visibility = View.GONE
                binding.tvCustomerAddressProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvCustomerAddressProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.ADDRESS)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.ADDRESS)?.second}"
                        )
                incrementMainProgressBar(10)
            }

            AppConstant.LEAD -> {
                binding.ivLeadsDownloadComplete.visibility = View.VISIBLE
                binding.leadsProgressBar.visibility = View.GONE
                binding.tvLeadsProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvLeadsProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.LEAD)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.LEAD)?.second}"
                        )

                incrementMainProgressBar(10)
            }

            AppConstant.ORDER_DISPATCH -> {
                binding.ivDispatchOrderDownloadComplete.visibility = View.VISIBLE
                binding.dispatchOrderProgressBar.visibility = View.GONE
                binding.tvDispatchOrderProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvDispatchOrderProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.ORDER_DISPATCH)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.ORDER_DISPATCH)?.second}"
                        )
                incrementMainProgressBar(12)
            }

            AppConstant.BEAT -> {
                binding.ivBeatDownloadComplete.visibility = View.VISIBLE
                binding.beatProgressBar.visibility = View.GONE
                binding.tvBeatProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                binding.tvBeatProgress.text =
                        resources.getString(R.string.donwload_progress_counter,
                                "${offlineProgressHashMap?.get(AppConstant.BEAT)?.second}",
                                "${offlineProgressHashMap?.get(AppConstant.BEAT)?.second}"
                        )
                incrementMainProgressBar(8)
            }
        }
    }

    private fun initLayout() {
        binding.imgClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCancelDownload.setOnClickListener {
            showCancelDialog()
        }

        binding.ivProductRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.PRODUCT)
        }

        binding.ivCustomerRetry.setOnClickListener {
            decrementMainProgressBar(8)
            loadRetryApi(AppConstant.CUSTOMER)
        }

        binding.ivOrderRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.ORDER)
        }

        binding.ivDispatchOrderRetry.setOnClickListener {
            decrementMainProgressBar(12)
            loadRetryApi(AppConstant.ORDER_DISPATCH)
        }
        binding.ivStaffRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.STAFF)
        }

        binding.ivLeadRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.LEAD)
        }
        binding.ivPaymentRetry.setOnClickListener {
            decrementMainProgressBar(12)
            loadRetryApi(AppConstant.PAYMENT)
        }

        binding.ivExpenseRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.EXPENSE)
        }
        binding.ivAddressRetry.setOnClickListener {
            decrementMainProgressBar(10)
            loadRetryApi(AppConstant.ADDRESS)
        }

    }

    private fun loadRetryApi(key: String) {
        when (key) {
            AppConstant.PRODUCT -> {
                binding.tvProductErrorMessage.visibility = View.GONE
                binding.ivProductRetry.visibility = View.GONE
                binding.productProgressBar.visibility = View.VISIBLE
                loadBrandList()
            }

            AppConstant.CUSTOMER -> {
                binding.tvCustomerErrorMessage.visibility = View.GONE
                binding.ivCustomerRetry.visibility = View.GONE
                binding.customerProgressBar.visibility = View.VISIBLE
                loadCustomerTypeList()
            }

            AppConstant.ORDER -> {
                binding.tvOrderErrorMessage.visibility = View.GONE
                binding.ivOrderRetry.visibility = View.GONE
                binding.ordersProgressBar.visibility = View.VISIBLE
                loadOrderList()
            }

            AppConstant.STAFF -> {
                binding.tvStaffErrorMessage.visibility = View.GONE
                binding.ivStaffRetry.visibility = View.GONE
                binding.staffProgressBar.visibility = View.VISIBLE
                loadStaffRoles()
            }

            AppConstant.PAYMENT -> {
                binding.tvPaymentsErrorMessage.visibility = View.GONE
                binding.ivPaymentRetry.visibility = View.GONE
                binding.paymentsProgressBar.visibility = View.VISIBLE
                loadPaymentList()
            }

            AppConstant.EXPENSE -> {
                binding.tvExpensesErrorMessage.visibility = View.GONE
                binding.ivExpenseRetry.visibility = View.GONE
                binding.expensesProgressBar.visibility = View.VISIBLE
                loadExpenseHeadList()
            }

            AppConstant.ADDRESS -> {
                binding.tvAddressErrorMessage.visibility = View.GONE
                binding.ivAddressRetry.visibility = View.GONE
                binding.customerAddressProgressBar.visibility = View.VISIBLE
                loadAddress()
            }

            AppConstant.LEAD -> {
                binding.tvLeadsErrorMessage.visibility = View.GONE
                binding.ivLeadRetry.visibility = View.GONE
                binding.leadsProgressBar.visibility = View.VISIBLE
                loadLeadCategoryList()
            }

            AppConstant.ORDER_DISPATCH -> {
                binding.tvDispatchErrorMessage.visibility = View.GONE
                binding.ivDispatchOrderRetry.visibility = View.GONE
                binding.dispatchOrderProgressBar.visibility = View.VISIBLE
                loadOrderDispatchList()
            }

            AppConstant.BEAT -> {
                binding.tvBeatErrorMessage.visibility = View.GONE
                binding.ivBeatRetry.visibility = View.GONE
                binding.beatProgressBar.visibility = View.VISIBLE
                loadBeatList()
            }
        }
    }

    private fun loadAllApi() {
        loadBrandList()
        loadCustomerTypeList()
        loadOrderList()
        loadStaffRoles()
        loadPaymentList()
        loadLeadCategoryList()
        loadExpenseHeadList()
        loadAddress()
        loadOrderDispatchList()
        loadBeatList()
    }

    private fun loadBeatList() {
        binding.beatProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpBeatList(
                    beatPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadOrderDispatchList() {
        binding.dispatchOrderProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOrderDispatchList(
                    dispatchOrderPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadLeadCategoryList() {
        binding.leadsProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpLeadCategory(
                    leadCategoryPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadStaffRoles() {
        binding.staffProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpStaffRoleList(
                    staffRolePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadAddress() {
        binding.customerAddressProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpAddressList(addressPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadExpenseHeadList() {
        binding.expensesProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpExpenseHeadList(
                    expenseHeadPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadExpenseList() {
        binding.expensesProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpTotalExpenseList(
                    expensePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadLeadList() {
        binding.leadsProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpLeadList(leadPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadPaymentList() {
        binding.paymentsProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpPaymentList(paymentPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadStaffList() {
        binding.staffProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpStaffList(staffPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadOrderList() {
        binding.ordersProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineOrderData(
                    orderPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCustomerTypeList() {
        binding.customerProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineCustomerTypeList(
                    customerTypePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCustomerList() {
        binding.customerProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineCustomerList(
                    customerPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadBrandList() {
        binding.productProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineBrandList(
                    brandPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCategoryList() {
        binding.productProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineCategoryList(
                    categoryPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadProductList() {
        binding.productProgressBar.visibility = View.VISIBLE
        launch {
            offlineViewModel.dumpOfflineProductList(
                    productPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun inflateDashBoardData(it: DashboardData) {
        dashBoardData = it
        binding.tvProductProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0",
                        it.products.toString())
        productCount = it.products ?: 0
        binding.tvCustomerProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", it.customers.toString())
        customerCount = it.customers ?: 0
        binding.tvOrdersProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", it.orders.toString())
        orderCount = it.orders ?: 0
        binding.tvStaffProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", it.staff.toString())
        staffCount = it.staff ?: 0
        binding.tvLeadsProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", it.leadCount.toString())
        leadCount = it.leadCount ?: 0
        binding.tvPaymentsProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", it.payment.toString())
        paymentCount = it.payment ?: 0

        binding.tvCustomerAddressProgress.text =
                resources.getString(
                        R.string.donwload_progress_counter,
                        "0",
                        it.customerAddress.toString()
                )
        addressCount = it.customerAddress ?: 0

        expenseCount = (it.reimbursementTracker?.plus(it.reimbursement ?: 0)) ?: 0

        binding.tvExpensesProgress.text =
                resources.getString(R.string.donwload_progress_counter, "0", "$expenseCount")

        binding.tvDispatchOrderProgress.text =
                resources.getString(
                        R.string.donwload_progress_counter,
                        "0",
                        it.orderDispatch.toString()
                )
        dispatchOrderCount = it.orderDispatch ?: 0


        binding.tvBeatProgress.text =
                resources.getString(
                        R.string.donwload_progress_counter,
                        "0",
                        it.beat.toString()
                )
        beatCount = it.beat ?: 0

    }

    private fun initSharedPreference() {
        offlineProgressModel = Gson().fromJson(SharedPref.getInstance().getString(AppConstant.ANDROID_OFFLINE_TAG))

        if (offlineProgressModel != null) {
            offlineProgressHashMap = offlineProgressModel?.offlineProgressHashMap

            if (offlineProgressHashMap != null) {
                for (entry: Map.Entry<String, Pair<Boolean, Int?>> in offlineProgressHashMap?.entries!!) {
                    if (entry.value.first.not()) {
                        loadRetryApi(entry.key)
                    } else {
                        showCompletedDump(entry.key)
                    }
                }
            }
        } else {
            offlineProgressModel = OfflineProgressModel()
            offlineProgressHashMap = HashMap()
            DatabaseLogManager.getInstance().deleteAllDatabase()
            loadAllApi()
        }
    }

    private fun initObservers() {
        dashboardViewModel.getDashboardLiveData().observe(this) {
            if (it.error == false) {
                it.data?.let { dashboardData ->
                    inflateDashBoardData(dashboardData)
                    initSharedPreference()
                }
            }
        }

        offlineViewModel.brandListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertBrandListData(list)
                        if (list.size < 100) {
                            loadCategoryList()
                            incrementMainProgressBar(2)
                            downloadEachItemsProgress(AppConstant.PRODUCT, 30)
                        } else {
                            brandPageCount++
                            loadBrandList()
                        }
                    } else {
                        incrementMainProgressBar(2)
                        downloadEachItemsProgress(AppConstant.PRODUCT, 30)
                        loadCategoryList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.PRODUCT, Pair(false, 0))
                binding.productProgressBar.visibility = View.INVISIBLE
                binding.ivProductRetry.visibility = View.VISIBLE
                binding.tvProductErrorMessage.text = it.message
                binding.tvProductErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        offlineViewModel.productCategoryLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCategoryListData(list)
                        if (list.size < 100) {
                            loadProductList()
                            incrementMainProgressBar(2)
                            downloadEachItemsProgress(AppConstant.PRODUCT, 30)
                        } else {
                            categoryPageCount++
                            loadCategoryList()
                        }
                    } else {
                        incrementMainProgressBar(2)
                        downloadEachItemsProgress(AppConstant.PRODUCT, 30)
                        loadProductList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.PRODUCT, Pair(false, 0))
                binding.productProgressBar.visibility = View.INVISIBLE
                binding.ivProductRetry.visibility = View.VISIBLE
                binding.tvProductErrorMessage.text = it.message
                binding.tvProductErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var productCounter = 0
        offlineViewModel.productLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { productLists ->
                    productCounter += productLists.size
                    binding.tvProductProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$productCounter",
                                    "$productCount"
                            )

                    if (productLists.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertProductListData(productLists)
                        if (productLists.size < 100) {
                            downloadEachItemsProgress(AppConstant.PRODUCT, 100)
                            incrementMainProgressBar(6)
                        } else {
                            productPageCount++
                            loadProductList()
                        }
                    } else {
                        incrementMainProgressBar(6)
                        downloadEachItemsProgress(AppConstant.PRODUCT, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.PRODUCT, Pair(false, 0))
                binding.productProgressBar.visibility = View.INVISIBLE
                binding.ivProductRetry.visibility = View.VISIBLE
                binding.tvProductErrorMessage.text = it.message
                binding.tvProductErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        offlineViewModel.customerTypeLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCustomerTypeData(list)
                        if (list.size < 100) {
                            loadCustomerList()
                            incrementMainProgressBar(2)
                            downloadEachItemsProgress(AppConstant.CUSTOMER, 30)
                        } else {
                            customerTypePageCount++
                            loadCustomerTypeList()
                        }
                    } else {
                        incrementMainProgressBar(2)
                        downloadEachItemsProgress(AppConstant.CUSTOMER, 30)
                        loadCustomerList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.CUSTOMER, Pair(false, 0))
                binding.customerProgressBar.visibility = View.INVISIBLE
                binding.ivCustomerRetry.visibility = View.VISIBLE
                binding.tvCustomerErrorMessage.text = it.message
                binding.tvCustomerErrorMessage.visibility = View.VISIBLE

                incrementMainProgressBar(8)
            }
        }

        var customerCounter = 0
        offlineViewModel.customerListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    customerCounter += list.size
                    binding.tvCustomerProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$customerCounter",
                                    "$customerCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCustomerData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.CUSTOMER, 100)
                            incrementMainProgressBar(6)
                        } else {
                            customerPageCount++
                            loadCustomerList()
                        }
                    } else {
                        incrementMainProgressBar(6)
                        downloadEachItemsProgress(AppConstant.CUSTOMER, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.CUSTOMER, Pair(false, 0))
                binding.customerProgressBar.visibility = View.INVISIBLE
                binding.ivCustomerRetry.visibility = View.VISIBLE
                binding.tvCustomerErrorMessage.text = it.message
                binding.tvCustomerErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(8)
            }
        }

        var orderCounter = 0
        offlineViewModel.orderLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    orderCounter += list.size
                    binding.tvOrdersProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$orderCounter",
                                    "$orderCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insetOrderData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.ORDER, 100)
                            incrementMainProgressBar(10)
                        } else {
                            orderPageCount++
                            loadOrderList()
                        }
                    } else {
                        incrementMainProgressBar(10)
                        downloadEachItemsProgress(AppConstant.ORDER, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.ORDER, Pair(false, 0))
                binding.ordersProgressBar.visibility = View.INVISIBLE
                binding.ivOrderRetry.visibility = View.VISIBLE
                binding.tvOrderErrorMessage.text = it.message
                binding.tvOrderErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        offlineViewModel.assignedRoleListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertStaffRoleData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.STAFF, 20)
                            incrementMainProgressBar(2)
                            loadStaffList()
                        } else {
                            staffRolePageCount++
                            loadStaffRoles()
                        }
                    } else {
                        downloadEachItemsProgress(AppConstant.STAFF, 20)
                        incrementMainProgressBar(2)
                        loadStaffList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.STAFF, Pair(false, 0))
                binding.staffProgressBar.visibility = View.INVISIBLE
                binding.ivStaffRetry.visibility = View.VISIBLE
                binding.tvStaffErrorMessage.text = it.message
                binding.tvStaffErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var staffCounter = 0
        offlineViewModel.staffListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    staffCounter += list.size
                    binding.tvStaffProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$staffCounter",
                                    "$staffCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertStaffData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.STAFF, 100)
                            incrementMainProgressBar(8)
                        } else {
                            staffPageCount++
                            loadStaffList()
                        }
                    } else {
                        incrementMainProgressBar(8)
                        downloadEachItemsProgress(AppConstant.STAFF, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.STAFF, Pair(false, 0))
                binding.staffProgressBar.visibility = View.INVISIBLE
                binding.ivStaffRetry.visibility = View.VISIBLE
                binding.tvStaffErrorMessage.text = it.message
                binding.tvStaffErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var paymentCounter = 0
        offlineViewModel.recordPaymentListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    paymentCounter += list.size
                    binding.tvPaymentsProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$paymentCounter",
                                    "$paymentCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertPaymentData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.PAYMENT, 100)
                            incrementMainProgressBar(12)
                        } else {
                            paymentPageCount++
                            loadPaymentList()
                        }
                    } else {
                        incrementMainProgressBar(12)
                        downloadEachItemsProgress(AppConstant.PAYMENT, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.PAYMENT, Pair(false, 0))
                binding.paymentsProgressBar.visibility = View.INVISIBLE
                binding.ivPaymentRetry.visibility = View.VISIBLE
                binding.tvPaymentsErrorMessage.text = it.message
                binding.tvPaymentsErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(12)
            }
        }

        offlineViewModel.leadCategoryLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertLeadCategoryData(list)
                        if (list.size < 100) {
                            incrementMainProgressBar(4)
                            loadLeadList()
                        } else {
                            leadCategoryPageCount++
                            loadLeadCategoryList()
                        }
                    } else {
                        incrementMainProgressBar(4)
                        loadLeadList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.LEAD, Pair(false, 0))
                binding.leadsProgressBar.visibility = View.INVISIBLE
                binding.ivLeadRetry.visibility = View.VISIBLE
                binding.tvLeadsErrorMessage.text = it.message
                binding.tvLeadsErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var leadCounter = 0
        offlineViewModel.leadListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    leadCounter += list.size
                    binding.tvLeadsProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$leadCounter",
                                    "$leadCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertLeadData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.LEAD, 100)
                            incrementMainProgressBar(6)
                        } else {
                            leadPageCount++
                            loadLeadList()
                        }
                    } else {
                        incrementMainProgressBar(6)
                        downloadEachItemsProgress(AppConstant.LEAD, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.LEAD, Pair(false, 0))
                binding.leadsProgressBar.visibility = View.INVISIBLE
                binding.ivLeadRetry.visibility = View.VISIBLE
                binding.tvLeadsErrorMessage.text = it.message
                binding.tvLeadsErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var expenseCounter = 0
        offlineViewModel.expenseTrackerLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    expenseCounter += list.size
                    binding.tvExpensesProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$expenseCounter",
                                    "$expenseCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertExpenseHeadList(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.EXPENSE, 50)
                            incrementMainProgressBar(5)
                            loadExpenseList()
                        } else {
                            expenseHeadPageCount++
                            loadExpenseHeadList()
                        }
                    } else {
                        incrementMainProgressBar(5)
                        downloadEachItemsProgress(AppConstant.EXPENSE, 50)
                        loadExpenseList()
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.EXPENSE, Pair(false, 0))
                binding.expensesProgressBar.visibility = View.INVISIBLE
                binding.ivExpenseRetry.visibility = View.VISIBLE
                binding.tvExpensesErrorMessage.text = it.message
                binding.tvExpensesErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        offlineViewModel.expenseLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    expenseCounter += list.size
                    binding.tvExpensesProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$expenseCounter",
                                    "$expenseCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertExpenseList(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.EXPENSE, 100)
                            incrementMainProgressBar(5)
                        } else {
                            expensePageCount++
                            loadExpenseList()
                        }
                    } else {
                        incrementMainProgressBar(5)
                        downloadEachItemsProgress(AppConstant.EXPENSE, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.EXPENSE, Pair(false, 0))
                binding.expensesProgressBar.visibility = View.INVISIBLE
                binding.ivExpenseRetry.visibility = View.VISIBLE
                binding.tvExpensesErrorMessage.text = it.message
                binding.tvExpensesErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var addressCounter = 0
        offlineViewModel.addressLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    addressCounter += list.size
                    binding.tvCustomerAddressProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$addressCounter",
                                    "$addressCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertAddressList(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.ADDRESS, 100)
                            incrementMainProgressBar(10)
                        } else {
                            addressPageCount++
                            downloadEachItemsProgress(AppConstant.ADDRESS, 30)
                            loadAddress()
                        }
                    } else {
                        incrementMainProgressBar(10)
                        downloadEachItemsProgress(AppConstant.ADDRESS, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.ADDRESS, Pair(false, 0))
                binding.customerAddressProgressBar.visibility = View.INVISIBLE
                binding.ivAddressRetry.visibility = View.VISIBLE
                binding.tvAddressErrorMessage.text = it.message
                binding.tvAddressErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(10)
            }
        }

        var orderDispatchCount = 0
        offlineViewModel.orderDispatchListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    orderDispatchCount += list.size
                    binding.tvDispatchOrderProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$orderDispatchCount",
                                    "$dispatchOrderCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertOrderDispatchListData(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.ORDER_DISPATCH, 100)
                            incrementMainProgressBar(12)
                        } else {
                            dispatchOrderPageCount++
                            downloadEachItemsProgress(AppConstant.ORDER_DISPATCH, 30)
                            loadOrderDispatchList()
                        }
                    } else {
                        incrementMainProgressBar(12)
                        downloadEachItemsProgress(AppConstant.ORDER_DISPATCH, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.ORDER_DISPATCH, Pair(false, 0))
                binding.dispatchOrderProgressBar.visibility = View.INVISIBLE
                binding.ivDispatchOrderRetry.visibility = View.VISIBLE
                binding.tvDispatchErrorMessage.text = it.message
                binding.tvDispatchErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(12)
            }
        }

        var beatCount = 0
        offlineViewModel.beatListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    beatCount += list.size
                    binding.tvBeatProgress.text =
                            resources.getString(
                                    R.string.donwload_progress_counter,
                                    "$beatCount",
                                    "$beatCount"
                            )

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertBeatList(list)
                        if (list.size < 100) {
                            downloadEachItemsProgress(AppConstant.BEAT, 100)
                            incrementMainProgressBar(8)
                        } else {
                            beatPageCount++
                            downloadEachItemsProgress(AppConstant.BEAT, 30)
                            incrementMainProgressBar(4)
                            loadBeatList()
                        }
                    } else {
                        incrementMainProgressBar(8)
                        downloadEachItemsProgress(AppConstant.BEAT, 100)
                    }
                }
            } else {
                offlineProgressHashMap?.put(AppConstant.BEAT, Pair(false, 0))
                binding.beatProgressBar.visibility = View.INVISIBLE
                binding.ivBeatRetry.visibility = View.VISIBLE
                binding.tvBeatErrorMessage.text = it.message
                binding.tvBeatErrorMessage.visibility = View.VISIBLE
                incrementMainProgressBar(8)
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun incrementMainProgressBar(progress: Int) {
        mainProgress += progress
        binding.downloadProgressBar.progress = mainProgress

        binding.tvRemainingTime.text =
                resources.getString(R.string.custum_percentage_progress, "$mainProgress")

        if (mainProgress == 100) {
            offlineProgressModel?.isOfflineDumpComplete = true
            offlineProgressModel?.offlineProgressHashMap = offlineProgressHashMap

            SharedPref.getInstance().putBoolean(SharePrefConstant.ENABLE_OFFLINE_DATA, true)
            SharedPref.getInstance().putModelClass(AppConstant.ANDROID_OFFLINE_TAG, offlineProgressModel)
            SharedPref.getInstance().putString(
                    SharePrefConstant.OFFLINE_DATA_LAST_SYNC_TIME,
                    DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)
            )
            binding.tvCancelDownload.text = resources.getString(R.string.done)
            binding.tvCancelDownload.setTextColor(resources.getColor(R.color.theme_purple))
            binding.tvCancelDownload.setOnClickListener {
                openMainActivity()
            }
        }
    }

    private fun decrementMainProgressBar(progress: Int) {
        mainProgress -= progress
        binding.downloadProgressBar.progress = mainProgress

        binding.tvRemainingTime.text =
                resources.getString(R.string.custum_percentage_progress, "$mainProgress")

        offlineProgressModel?.isOfflineDumpComplete = false
    }

    @SuppressLint("StringFormatInvalid")
    private fun downloadEachItemsProgress(item: String, percentage: Int) {
        when (item) {
            AppConstant.PRODUCT -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.PRODUCT, Pair(true, dashBoardData?.products))
                    binding.ivProductDownloadComplete.visibility = View.VISIBLE
                    binding.productProgressBar.visibility = View.GONE
                    binding.tvProductProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvProductProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.CUSTOMER -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.CUSTOMER, Pair(true, dashBoardData?.customers))
                    binding.ivCustomerDownloadComplete.visibility = View.VISIBLE
                    binding.customerProgressBar.visibility = View.GONE
                    binding.tvCustomerProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvCustomerProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.ORDER -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.ORDER, Pair(true, dashBoardData?.orders))
                    binding.ivOrdersDownloadComplete.visibility = View.VISIBLE
                    binding.ordersProgressBar.visibility = View.GONE
                    binding.tvOrdersProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvOrdersProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.STAFF -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.STAFF, Pair(true, dashBoardData?.staff))
                    binding.ivStaffDownloadComplete.visibility = View.VISIBLE
                    binding.staffProgressBar.visibility = View.GONE
                    binding.tvStaffProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvStaffProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.PAYMENT -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.PAYMENT, Pair(true, dashBoardData?.payment))
                    binding.ivPaymentsDownloadComplete.visibility = View.VISIBLE
                    binding.paymentsProgressBar.visibility = View.GONE
                    binding.tvPaymentsProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvPaymentsProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.EXPENSE -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.EXPENSE, Pair(true, dashBoardData?.reimbursementTracker?.plus(dashBoardData?.reimbursement
                            ?: 0)))
                    binding.ivExpensesDownloadComplete.visibility = View.VISIBLE
                    binding.expensesProgressBar.visibility = View.GONE
                    binding.tvExpensesProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvExpensesProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.ADDRESS -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.ADDRESS, Pair(true, dashBoardData?.customerAddress))
                    binding.ivCustomerAddressDownloadComplete.visibility = View.VISIBLE
                    binding.customerAddressProgressBar.visibility = View.GONE
                    binding.tvCustomerAddressProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvCustomerAddressProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.LEAD -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.LEAD, Pair(true, dashBoardData?.leadCount))
                    binding.ivLeadsDownloadComplete.visibility = View.VISIBLE
                    binding.leadsProgressBar.visibility = View.GONE
                    binding.tvLeadsProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvLeadsProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.ORDER_DISPATCH -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.ORDER_DISPATCH, Pair(true, dashBoardData?.orderDispatch))
                    binding.ivDispatchOrderDownloadComplete.visibility = View.VISIBLE
                    binding.dispatchOrderProgressBar.visibility = View.GONE
                    binding.tvDispatchOrderProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvDispatchOrderProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }

            AppConstant.BEAT -> {
                if (percentage == 100) {
                    offlineProgressHashMap?.put(AppConstant.BEAT, Pair(true, dashBoardData?.beat))
                    binding.ivBeatDownloadComplete.visibility = View.VISIBLE
                    binding.beatProgressBar.visibility = View.GONE
                    binding.tvBeatProgressPercentage.text = AppConstant.HUNDRED_PERCENT
                    downloadCounter++
                } else {
                    binding.tvBeatProgressPercentage.text =
                            resources.getString(R.string.custum_percentage_progress, "$percentage")
                }
            }
        }
    }

    private fun showCancelDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)


        tvHeading.text = resources.getString(R.string.cancel_download)
        tvTitle.text = resources.getString(R.string.cancel_download_message)


        ivClose.visibility = View.GONE

        tvCancel.text = resources.getString(R.string.yes_cancel)

        tvDelete.text = resources.getString(R.string.no_continue)

        tvCancel.setOnClickListener {
            dialog.dismiss()
            DatabaseLogManager.getInstance().deleteAllDatabase()
            SharedPref.getInstance().putBoolean(SharePrefConstant.ENABLE_OFFLINE_DATA, false)
            finish()
        }

        tvDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showToast(message: String?) {
        MessageHelper().initMessage(
                message,
                findViewById(android.R.id.content)
        )
    }

    override fun onBackPressed() {
        if (offlineProgressModel?.isOfflineDumpComplete == true) {
            openMainActivity()
        } else {
            showCancelDialog()
        }
    }

    private fun openMainActivity() {
        val i = Intent(this, SalesMainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }
}