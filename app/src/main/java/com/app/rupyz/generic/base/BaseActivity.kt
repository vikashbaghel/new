package com.app.rupyz.generic.base

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.rupyz.MyApplication
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.dialog.CustomProgressDialog
import com.app.rupyz.generic.helper.Actions
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.NetworkChangeReceiver
import com.app.rupyz.generic.utils.PermissionModel.Companion.INSTANCE
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.sales.login.LoginActivity
import com.app.rupyz.sales.preference.OfflineViewModel
import com.app.rupyz.sales.preference.UploadingOfflineDataActivity
import com.app.rupyz.service.EndlessService
import com.app.rupyz.service.ServiceState
import com.app.rupyz.service.getServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener,
        CoroutineScope by MainScope() {
    var tag: String = "RUPYZ_DEBUG"

    private val progressDialog by lazy { CustomProgressDialog(this) }
    private val baseViewModel: BaseViewModel by viewModels()
    private val offlineViewModel: OfflineViewModel by viewModels()

    private val pageSize = 100
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

    private var offlineDataLastSyncedTime: String? = null

    private val networkChangeReceiver = NetworkChangeReceiver(this)

    private var isReceiverRegistered = false

    var defaultHeader = "selected=true&page_no=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()

        if (hasInternetConnection()){
            onNetworkStatusChanged(true)
        }
    }

    override fun onResume() {
        super.onResume()
        registerNetworkChangeReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterNetworkChangeReceiver()
    }

    private fun registerNetworkChangeReceiver() {
        try {
            if (isReceiverRegistered.not()) {
                val filter = IntentFilter(CONNECTIVITY_ACTION)
                registerReceiver(networkChangeReceiver, filter)
                isReceiverRegistered = true
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun unregisterNetworkChangeReceiver() {
        if (isReceiverRegistered) {
            unregisterReceiver(networkChangeReceiver)
            isReceiverRegistered = false
        }
    }

    // Implement methods for NetworkChangeListener interface
    override fun onNetworkStatusChanged(networkAvailable: Boolean) {
        log("Network is $networkAvailable")
        if (networkAvailable.not()){
            MyApplication.instance.setPerformedValue(false)
        }

        if (MyApplication.instance.getActionPerformedValue().not()) {
            if (networkAvailable) {

                checkOfflineModule()
                MyApplication.instance.setPerformedValue(true)
            }
        }
    }

    private fun checkOfflineModule() {
        if (SharedPref.getInstance().getBoolean(AppConstant.ENABLE_ORG_OFFLINE_MODE, false)) {
            checkForDumpUpdate()
            offlineViewModel.isOfflineDataAvailable()
        }
    }

    private fun checkForDumpUpdate() {
        if (SharedPref.getInstance().getBoolean(SharePrefConstant.ENABLE_OFFLINE_DATA, false)) {
            offlineDataLastSyncedTime =
                    SharedPref.getInstance().getString(SharePrefConstant.OFFLINE_DATA_LAST_SYNC_TIME)
        }

        if (hasInternetConnection() && offlineDataLastSyncedTime.isNullOrEmpty().not()) {
            loadBrandList()
            loadCustomerTypeList()
            loadOrderList()
            loadStaffRoles()
            loadPaymentList()
            loadLeadCategoryList()
            loadExpenseHeadList()
            loadAddress()
            loadOrderDispatchList()
        }
    }

    private fun loadOrderDispatchList() {
        launch {
            offlineViewModel.dumpOrderDispatchList(
                    dispatchOrderPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadLeadCategoryList() {
        launch {
            offlineViewModel.dumpLeadCategory(
                    leadCategoryPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadStaffRoles() {
        launch {
            offlineViewModel.dumpStaffRoleList(
                    staffRolePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadAddress() {
        launch {
            offlineViewModel.dumpAddressList(addressPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadExpenseHeadList() {
        launch {
            offlineViewModel.dumpExpenseHeadList(
                    expenseHeadPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadExpenseList() {
        launch {
            offlineViewModel.dumpTotalExpenseList(
                    expensePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadLeadList() {
        launch {
            offlineViewModel.dumpLeadList(leadPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadPaymentList() {
        launch {
            offlineViewModel.dumpPaymentList(paymentPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadStaffList() {
        launch {
            offlineViewModel.dumpStaffList(staffPageCount, pageSize, offlineDataLastSyncedTime)
        }
    }

    private fun loadOrderList() {
        launch {
            offlineViewModel.dumpOfflineOrderData(
                    orderPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCustomerTypeList() {
        launch {
            offlineViewModel.dumpOfflineCustomerTypeList(
                    customerTypePageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCustomerList() {
        launch {
            offlineViewModel.dumpOfflineCustomerList(
                    customerPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadBrandList() {
        launch {
            offlineViewModel.dumpOfflineBrandList(
                    brandPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadCategoryList() {
        launch {
            offlineViewModel.dumpOfflineCategoryList(
                    categoryPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun loadProductList() {
        launch {
            offlineViewModel.dumpOfflineProductList(
                    productPageCount,
                    pageSize,
                    offlineDataLastSyncedTime
            )
        }
    }

    private fun initObservers() {
        offlineViewModel.offlineDataAvailableLiveData.observe(this) { data ->
            if (data.first) {
                Handler(Looper.myLooper()!!).postDelayed({
                    startActivity(Intent(this, UploadingOfflineDataActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                }, 1000)
            }
        }

        baseViewModel.logoutLiveData.observe(this) {
            stopDialog()
            SharedPref.getInstance().clearSharePref()
            INSTANCE.clearPermissionModel()
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }


        offlineViewModel.brandListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertBrandListData(list)
                        if (list.size < 100) {
                            loadCategoryList()
                        } else {
                            brandPageCount++
                            loadBrandList()
                        }
                    } else {
                        loadCategoryList()
                    }
                }
            }
        }

        offlineViewModel.productCategoryLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCategoryListData(list)
                        if (list.size < 100) {
                            loadProductList()
                        } else {
                            categoryPageCount++
                            loadCategoryList()
                        }
                    } else {
                        loadProductList()
                    }
                }
            }
        }

        offlineViewModel.productLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { productLists ->
                    if (productLists.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertProductListData(productLists)
                        if (productLists.size >= 100) {
                            productPageCount++
                            loadProductList()
                        }
                    }
                }
            }
        }

        offlineViewModel.customerTypeLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCustomerTypeData(list)
                        if (list.size < 100) {
                            loadCustomerList()
                        } else {
                            customerTypePageCount++
                            loadCustomerTypeList()
                        }
                    } else {
                        loadCustomerList()
                    }
                }
            }
        }


        offlineViewModel.customerListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertCustomerData(list)
                        if (list.size >= 100) {
                            customerPageCount++
                            loadCustomerList()
                        }
                    }
                }
            }
        }

        offlineViewModel.orderLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insetOrderData(list)
                        if (list.size >= 100) {
                            orderPageCount++
                            loadOrderList()
                        }
                    }
                }
            }
        }

        offlineViewModel.assignedRoleListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertStaffRoleData(list)
                        if (list.size < 100) {
                            loadStaffList()
                        } else {
                            staffRolePageCount++
                            loadStaffRoles()
                        }
                    } else {
                        loadStaffList()
                    }
                }
            }
        }

        offlineViewModel.staffListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertStaffData(list)
                        if (list.size >= 100) {
                            staffPageCount++
                            loadStaffList()
                        }
                    }
                }
            }
        }

        offlineViewModel.recordPaymentListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertPaymentData(list)
                        if (list.size >= 100) {
                            paymentPageCount++
                            loadPaymentList()
                        }
                    }
                }
            }
        }

        offlineViewModel.leadCategoryLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertLeadCategoryData(list)
                        if (list.size < 100) {
                            loadLeadList()
                        } else {
                            leadPageCount++
                            loadLeadCategoryList()
                        }
                    } else {
                        loadLeadList()
                    }
                }
            }
        }

        offlineViewModel.leadListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertLeadData(list)
                        if (list.size >= 100) {
                            leadPageCount++
                            loadLeadList()
                        }
                    }
                }
            }
        }

        offlineViewModel.expenseTrackerLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertExpenseHeadList(list)
                        if (list.size < 100) {
                            loadExpenseList()
                        } else {
                            expenseHeadPageCount++
                            loadExpenseHeadList()
                        }
                    } else {
                        loadExpenseList()
                    }
                }
            }
        }

        offlineViewModel.expenseLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertExpenseList(list)
                        if (list.size >= 100) {
                            expensePageCount++
                            loadExpenseList()
                        }
                    }
                }
            }
        }

        var addressCounter = 0
        offlineViewModel.addressLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    addressCounter += list.size
                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertAddressList(list)
                        if (list.size >= 100) {
                            addressPageCount++
                            loadAddress()
                        }
                    }
                }
            }
        }

        var orderDispatchCount = 0
        offlineViewModel.orderDispatchListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    orderDispatchCount += list.size

                    if (list.isEmpty().not()) {
                        DatabaseLogManager.getInstance().insertOrderDispatchListData(list)
                        if (list.size >= 100) {
                            dispatchOrderPageCount++
                            loadOrderDispatchList()
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun replaceFragment(container: Int?, fragment: Fragment?) {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(container!!, fragment)
                    .commit()
        }
    }

    fun addFragment(container: Int?, fragment: Fragment?) {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(container!!, fragment)
                    .addToBackStack(fragment.tag)
                    .commit()
        }
    }

    fun disableTouch() {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun enableTouch() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun hideKeyboard() {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    val isStaffUser: Boolean
        get() {
            val appAccessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
            return appAccessType != AppConstant.ACCESS_TYPE_MASTER
        }

    fun hasInternetConnection(): Boolean {
        if (SharedPref.getInstance().getBoolean(AppConstant.ENABLE_ORG_OFFLINE_MODE, false).not()) {
            return true
        }

        return Connectivity.hasInternetConnection(this)
    }

    fun showToast(message: String?) {
        MessageHelper().initMessage(
                message,
                findViewById(android.R.id.content)
        )
    }

    fun showLongToastWithAction(message: String?) {
        MessageHelper().initLongMessageWithAction(
                message,
                findViewById(android.R.id.content)
        )
    }

    open fun startDialog(message: String?) {
        if (message.isNullOrEmpty().not()) {
            progressDialog.start(message)
        } else {
            progressDialog.start()
        }
    }

    open fun stopDialog() {
        progressDialog.stop()
    }

    fun logout() {
        startDialog("Please wait")
        baseViewModel.logout()
        DatabaseLogManager.getInstance().deleteAllDatabase()

        if (isStaffUser && getServiceState(this) == ServiceState.STARTED) {
            Intent(this, EndlessService::class.java).also {
                it.action = Actions.STOP.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log("Starting the service in >=26 Mode")
                    ContextCompat.startForegroundService(this, it)
                    return
                }
                log("Starting the service in < 26 Mode")
                startService(it)
            }
        }
    }

}