package com.app.rupyz.sales.staffactivitytrcker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityDailySalesReportBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CategoryMetricsItem
import com.app.rupyz.model_kt.DailySalesReportData
import com.app.rupyz.model_kt.ProductMetricsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


class DailySalesReportActivity : BaseActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityDailySalesReportBinding
    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var productCategoryAdapter: DailySalesReportCategoryAdapter
    private lateinit var customerSalesAdapter: DailySalesReportCategoryAdapter
    private lateinit var productSummeryAdapter: ProductSummeryDetailsAdapter

    private var productCategoryList = ArrayList<CategoryMetricsItem>()
    private var customerSalesList = ArrayList<CategoryMetricsItem>()
    private var productSummeryList = ArrayList<ProductMetricsItem>()

    private var userId: Int? = null
    private var date: String = ""

    private var rotationAngleCustomer = 0
    private var rotationAngleTc = 0
    private var rotationAnglePC = 0
    private var rotationAngleCs = 0
    private var rotationAnglePs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailySalesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        initRecyclerView()

        binding.scrollView.visibility = View.GONE

        if (intent.hasExtra(AppConstant.STAFF_ID)) {
            userId = intent.getIntExtra(AppConstant.STAFF_ID, 0)
        }

        if (intent.hasExtra(AppConstant.DATE)) {
            date = intent.getStringExtra(AppConstant.DATE)!!
        }

        if (userId == 0) {
            userId = null
        }

        activityViewModel.getDailySalesReport(userId, date)

        initLayout()

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    private fun initLayout() {

        binding.clNewCustomer.setOnClickListener {
            binding.clNewCustomerDetails.isVisible =
                binding.clNewCustomerDetails.isVisible.not()

            rotationAngleCustomer = if (rotationAngleCustomer == 0) 180 else 0

            binding.ivCustomerLevelDropDown.animate().rotation(rotationAngleCustomer.toFloat())
                .setDuration(300).start()
        }

        binding.clTotalCall.setOnClickListener {
            binding.clTcDetails.isVisible =
                binding.clTcDetails.isVisible.not()

            rotationAngleTc = if (rotationAngleTc == 0) 180 else 0

            binding.ivTcDropDown.animate().rotation(rotationAngleTc.toFloat()).setDuration(300)
                .start()
        }

        binding.clProductiveCall.setOnClickListener {
            binding.clProductiveCallDetails.isVisible =
                binding.clProductiveCallDetails.isVisible.not()

            rotationAnglePC = if (rotationAnglePC == 0) 180 else 0

            binding.ivPcDropDown.animate().rotation(rotationAnglePC.toFloat()).setDuration(300)
                .start()
        }

        binding.clCategorySummery.setOnClickListener {
            binding.clCategorySummeryDetails.isVisible =
                binding.clCategorySummeryDetails.isVisible.not()

            rotationAngleCs = if (rotationAngleCs == 0) 180 else 0

            binding.ivCategorySummeryDropDown.animate().rotation(rotationAngleCs.toFloat())
                .setDuration(300)
                .start()
        }

        binding.clProductSummary.setOnClickListener {
            binding.clProductSummeryDetails.isVisible =
                binding.clProductSummeryDetails.isVisible.not()

            rotationAnglePs = if (rotationAnglePs == 0) 180 else 0

            binding.ivProductSummaryDropDown.animate().rotation(rotationAnglePs.toFloat())
                .setDuration(300)
                .start()

            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvProductCategorySales.layoutManager = LinearLayoutManager(this)
        productCategoryAdapter =
            DailySalesReportCategoryAdapter(productCategoryList, AppConstant.PRODUCT)
        binding.rvProductCategorySales.adapter = productCategoryAdapter

        binding.rvCustomerWithSales.layoutManager = LinearLayoutManager(this)
        customerSalesAdapter =
            DailySalesReportCategoryAdapter(customerSalesList, AppConstant.CUSTOMER)
        binding.rvCustomerWithSales.adapter = customerSalesAdapter

        binding.rvProductSales.layoutManager = LinearLayoutManager(this)
        productSummeryAdapter = ProductSummeryDetailsAdapter(productSummeryList)
        binding.rvProductSales.adapter = productSummeryAdapter
    }

    private fun initObservers() {
        activityViewModel.dailySalesReportLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                if (it.data != null) {
                    if (it.data.date != null) {
                        initData(it.data)
                    } else {
                        binding.icHoliday.setImageResource(R.drawable.holiday_beat_plan)
                        binding.tvErrorMessage.text =
                            resources.getString(R.string.no_activity_performed)
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                } else {
                    binding.icHoliday.setImageResource(R.drawable.no_data_available)
                    binding.tvErrorMessage.text =
                        resources.getString(R.string.no_data_for_this_date)
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            } else {
                showToast(it.message)
            }
        }
    }

    private fun initData(model: DailySalesReportData) {
        binding.tvBeatPlanName.text = model.beatName
        binding.tvDistributorName.text = model.distributorName
        binding.tvNewLead.text = ""
        binding.tvTotalCall.text = ""

        if (model.beatList.isNullOrEmpty().not()) {
            binding.tvBeatPlanName.text = "${model.beatList!![0].name}"
        }

        binding.tvUserName.text = model.userName
        ImageUtils.loadTeamImage(model.profilePicUrl, binding.ivUser)
        binding.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
            model.date, SimpleDateFormat(
                "dd MMM yyyy",
                Locale.ENGLISH
            )
        )

        var totalCall = 0
        if (model.tcVisitedCustomerIds.isNullOrEmpty().not()) {
            totalCall += model.tcVisitedCustomerIds?.size!!
        }

        if (model.tcVisitedLeadIds.isNullOrEmpty().not()) {
            totalCall += model.tcVisitedLeadIds?.size!!
        }

        binding.tvCustomerCall.text = "${model.tcVisitedCustomerIds?.size ?: 0}"
        binding.tvLeadCall.text = "${model.tcVisitedLeadIds?.size ?: 0}"

        binding.tvTotalCall.text = "$totalCall"
        binding.tvNewLead.text = "${model.newLeadIds?.size ?: 0}"

        binding.tvProductiveCall.text = "${model.customerMetrics?.size ?: 0}"

        binding.tvTotalOrderAmount.text =
            CalculatorHelper().convertLargeAmount(model.totalOrderValue ?: 0.00, AppConstant.TWO_DECIMAL_POINTS)

        var totalNewCustomerCount = 0
        if (model.newCustomerData != null) {
            if (model.newCustomerData.lEVEL1?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL1.count

                binding.hdLevel1.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                binding.tvLevelOneCount.text = "${model.newCustomerData.lEVEL1.count}"
            }

            if (model.newCustomerData.lEVEL2?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL2.count
                binding.hdLevel2.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                binding.tvLevelTwoCount.text = "${model.newCustomerData.lEVEL2.count}"
            }

            if (model.newCustomerData.lEVEL3?.count != null) {
                totalNewCustomerCount += model.newCustomerData.lEVEL3.count

                binding.hdLevel3.text =
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                binding.tvLevelThreeCount.text = "${model.newCustomerData.lEVEL3.count}"
            }
        }

        binding.tvNewCustomerCount.text = "$totalNewCustomerCount"

        if (model.categoryMetrics.isNullOrEmpty().not()) {
            productCategoryList.addAll(model.categoryMetrics!!)
            productCategoryAdapter.notifyDataSetChanged()
        }

        if (model.customerMetrics.isNullOrEmpty().not()) {
            customerSalesList.addAll(model.customerMetrics!!)
            customerSalesAdapter.notifyDataSetChanged()
        }

        if (model.productMetrics.isNullOrEmpty().not()) {
            productSummeryList.addAll(model.productMetrics!!)
            productSummeryAdapter.notifyDataSetChanged()
        }

        binding.scrollView.visibility = View.VISIBLE
        binding.ivShare.visibility = View.VISIBLE

        binding.ivShare.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.clNewCustomerDetails.visibility = View.VISIBLE
            binding.clTcDetails.visibility = View.VISIBLE
            binding.clProductiveCallDetails.visibility = View.VISIBLE
            binding.clCategorySummeryDetails.visibility = View.VISIBLE
            binding.clProductSummeryDetails.visibility = View.VISIBLE
            binding.dsrHeader.visibility = View.VISIBLE
            binding.tvUserName.setTextColor(resources.getColor(R.color.white))
            binding.tvDate.setTextColor(resources.getColor(R.color.white))

            Handler(Looper.myLooper()!!).postDelayed({
                launch {
                    val bitmap = loadBitmapFromView(binding.mainContent)
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.dsrHeader.visibility = View.GONE
                        binding.tvUserName.setTextColor(resources.getColor(R.color.black))
                        binding.tvDate.setTextColor(resources.getColor(R.color.sales_text_color_light_black))
                        if (bitmap != null) {
                            shareResultAsImage(bitmap)
                        }
                    }
                }
            }, 1000)
        }
    }

    private fun loadBitmapFromView(v: View): Bitmap? {

        val bitmap = Bitmap.createBitmap(
            binding.scrollView.getChildAt(0).width,
            binding.scrollView.getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawARGB(100, 49, 43, 129)
        binding.scrollView.getChildAt(0).draw(canvas)

        // Do whatever you want with your bitmap

        // Do whatever you want with your bitmap
        return bitmap
    }

    private fun shareResultAsImage(bitmap: Bitmap) {
        try {
            val pathOfBmp = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap, UUID.randomUUID().toString() + ".png", null
            )
            Logger.errorLogger("Bitmap Crash", pathOfBmp)
            if (pathOfBmp !== "") {
                val bmpUri = Uri.parse(pathOfBmp)
                val emailIntent1 = Intent(Intent.ACTION_SEND)
                emailIntent1.setType("image/png")
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri)
                startActivity(emailIntent1)
            } else {
                Logger.errorLogger("Bitmap Crash", "null path ")
            }
        } catch (ex: Exception) {
            Logger.errorLogger("Bitmap Crash", ex.message)
            Logger.errorLogger("Bitmap Crash", ex.localizedMessage)
        }
    }
}