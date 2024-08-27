package com.app.rupyz.sales.payment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityPaymentRecordBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.sales.beatplan.ApproveBottomSheetDialogFragment
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.orders.adapter.StatusFilterAdapter
import com.google.gson.JsonObject

class ListOfPaymentActivity : BaseActivity(), RecordPaymentActionListener,
    PaymentRejectedBottomSheetDialogFragment.IPaymentRejectedListener,
    StatusFilterAdapter.StatusSelectListener,
    ApproveBottomSheetDialogFragment.IApproveActionListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener,
    DeleteDialog.IOnClickListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityPaymentRecordBinding

    private lateinit var recordPaymentViewModel: RecordPaymentViewModel

    private var paymentList: ArrayList<RecordPaymentData> = ArrayList()
    private var statusList: ArrayList<AllCategoryResponseModel> = ArrayList()

    private lateinit var horizontalLayoutManager: LinearLayoutManager

    private lateinit var paymentInfoAdapter: AllRecordPaymentAdapter
    private lateinit var statusFilterAdapter: StatusFilterAdapter

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var paymentStatus = ""

    private var rejectedPosition = -1
    private var deletePaymentPosition: Int = -1

    private var rejectedPaymentModel: RecordPaymentData? = null

    private var approvePaymentModel: RecordPaymentData? = null
    private var approvePaymentPosition: Int? = null

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recordPaymentViewModel = ViewModelProvider(this)[RecordPaymentViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        getUserCurrentLocation()

        setStatusList()

        initObservers()
        initRecyclerView()

        binding.progressBar.visibility = View.VISIBLE
        currentPage = 1
        getPaymentList()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            getPaymentList()
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.ivBack.setOnClickListener {
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
                        MockLocationDetectedDialogFragment.getInstance(this@ListOfPaymentActivity)
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
                }
            }
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvPaymentList.layoutManager = linearLayoutManager
        paymentInfoAdapter = AllRecordPaymentAdapter(paymentList, this, hasInternetConnection())
        binding.rvPaymentList.adapter = paymentInfoAdapter

        binding.rvPaymentList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getPaymentList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun setStatusList() {
        val list = resources.getStringArray(R.array.payment_status_for_view)

        list.forEachIndexed { index, value ->
            val model = AllCategoryResponseModel()
            if (index == 0) {
                model.isSelected = true
            }
            model.name = value
            statusList.add(model)
        }
        initTabLayout()
    }

    private fun getPaymentList() {
        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        recordPaymentViewModel.getPaymentList(currentPage, paymentStatus, hasInternetConnection())
    }

    private fun initTabLayout() {
        binding.rvStatus.setHasFixedSize(true)
        horizontalLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvStatus.layoutManager = horizontalLayoutManager

        statusFilterAdapter = StatusFilterAdapter(statusList, this)
        binding.rvStatus.adapter = statusFilterAdapter
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        recordPaymentViewModel.getRecordPaymentListData().observe(this) { data ->
            data.data?.let {
                isPageLoading = false
                binding.progressBar.visibility = View.GONE
                binding.paginationProgressBar.visibility = View.GONE

                if (it.isNotEmpty()) {
                    if (currentPage == 1) {
                        paymentList.clear()
                    }

                    if (it.size < 30) {
                        isApiLastPage = true
                    }

                    paymentList.addAll(it)
                    paymentInfoAdapter.notifyDataSetChanged()
                } else {
                    if (currentPage == 1) {
                        isApiLastPage = true
                        paymentList.clear()
                        paymentInfoAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        recordPaymentViewModel.updatePaymentRecordLiveData.observe(this) {
            stopDialog()
            if (it.error == false) {
                if (rejectedPosition != -1) {
                    if (rejectedPaymentModel != null) {
                        paymentList[rejectedPosition] = rejectedPaymentModel!!
                    }
                    paymentInfoAdapter.notifyItemChanged(rejectedPosition)
                }
            } else {
                showToast(it.message)
            }
        }

        recordPaymentViewModel.deletePaymentLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                if (deletePaymentPosition != -1) {
                    paymentList.removeAt(deletePaymentPosition)
                    paymentInfoAdapter.notifyItemRemoved(deletePaymentPosition)
                    paymentInfoAdapter.notifyItemRangeChanged(
                        deletePaymentPosition,
                        paymentList.size
                    )
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
//        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
//        fragment.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
    }

    override fun onStatusChange(status: String, model: RecordPaymentData, position: Int) {
        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
            if (status == AppConstant.APPROVE) {
                approvePaymentModel = model
                approvePaymentPosition = position
                val fragment = ApproveBottomSheetDialogFragment.getInstance(
                    this,
                    ListOfPaymentActivity::class.java.name
                )
                fragment.show(supportFragmentManager, AppConstant.BEAT)
            } else if (status == AppConstant.REJECT) {
                rejectedPosition = position
                val fragment = PaymentRejectedBottomSheetDialogFragment(model, this)
                fragment.show(supportFragmentManager, "Payment")
            }
        } else {
            showStartDayDialog()
        }
    }

    override fun getPaymentInfo(model: RecordPaymentData, position: Int) {
        startActivity(
            Intent(
                this,
                PaymentDetailsActivity::class.java
            ).putExtra(AppConstant.PAYMENT_INFO, model)
        )
    }

    override fun onDeletePayment(model: RecordPaymentData, position: Int) {
        DeleteDialog.showDeleteDialog(
            this,
            model.id,
            position,
            resources.getString(R.string.delete_payment),
            resources.getString(R.string.delete_payment_message),
            this
        )
    }


    override fun commentOfPaymentRejected(model: RecordPaymentData) {
        rejectedPaymentModel = model
        rejectedPaymentModel?.status = AppConstant.STATUS_DISHONOUR
        startDialog("Rejecting Payment")
        recordPaymentViewModel.updateRecordPayment(model, model.id ?: 0)
    }

    override fun onDismissDialog(model: RecordPaymentData) {
        paymentList[rejectedPosition].status = model.status
        paymentInfoAdapter.notifyItemChanged(rejectedPosition)
    }

    override fun onStatusSelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected.not()) {
            for (i in statusList.indices) {
                statusList[i].isSelected = false
            }

            paymentStatus = when (position) {
                0 -> {
                    ""
                }

                3 -> AppConstant.STATUS_DISHONOUR

                else -> {
                    statusList[position].name.toString()
                }
            }

            statusList[position].isSelected = true
            statusFilterAdapter.notifyDataSetChanged()

            paymentList.clear()
            paymentInfoAdapter.notifyDataSetChanged()

            binding.progressBar.visibility = View.VISIBLE
            currentPage = 1
            getPaymentList()
        }
    }

    override fun approvalConformation(reason: String) {
        if (approvePaymentModel != null && approvePaymentPosition != null) {
            approvePaymentModel?.status = AppConstant.STATUS_APPROVED
            paymentInfoAdapter.notifyItemChanged(approvePaymentPosition!!)

            startDialog("Approving Payment")
            recordPaymentViewModel.updateRecordPayment(
                approvePaymentModel!!,
                approvePaymentModel?.id ?: 0
            )
        }
    }

    override fun onCancelApproval() {
        super.onCancelApproval()
        paymentInfoAdapter.notifyItemChanged(approvePaymentPosition!!)
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
        paymentInfoAdapter.notifyDataSetChanged()
    }

    override fun onDismissDialogForMockLocation() {
        super.onDismissDialogForMockLocation()
        finish()
    }
    override fun onSuccessfullyMarkAttendance() {
        super.onSuccessfullyMarkAttendance()
        paymentInfoAdapter.notifyDataSetChanged()
    }

    override fun onDelete(model: Any, position: Any) {
        deletePaymentPosition = position as Int
        val jsonObject = JsonObject()
        jsonObject.addProperty("is_archived", true)

        binding.progressBar.visibility = View.VISIBLE
        recordPaymentViewModel.deletePayment(jsonObject, model as Int, hasInternetConnection())

    }
}