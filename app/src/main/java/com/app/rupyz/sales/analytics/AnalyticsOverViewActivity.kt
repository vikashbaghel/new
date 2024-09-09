package com.app.rupyz.sales.analytics

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAnalyticsOverViewBinding
import com.app.rupyz.generic.custom.MyMarkerView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.*
import com.app.rupyz.sales.customer.StaffWiseSalesAdapter
import com.app.rupyz.sales.filter.AnalyticsFilterBottomSheetDialogFragment
import com.app.rupyz.sales.filter.IAnalyticsFilterListener
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kotlin.math.roundToInt


class AnalyticsOverViewActivity : AppCompatActivity(), OnChartValueSelectedListener,
    IAnalyticsFilterListener {
    private lateinit var binding: ActivityAnalyticsOverViewBinding

    private lateinit var customerWiseSalesAdapter: CustomerWiseSalesAdapter
    private lateinit var staffWiseSalesAdapter: StaffWiseSalesAdapter
    private lateinit var topProductSalesAdapter: TopProductSalesAdapter
    private lateinit var topCategorySalesAdapter: TopCategorySalesAdapter

    private lateinit var customerViewModel: AnalyticsViewModel

    private var isPageLoading = false

    private var customerWiseSalesList: ArrayList<CustomerWiseSalesDataItem> = ArrayList()
    private var staffWiseSalesList: ArrayList<StaffWiseSalesDataItem> = ArrayList()
    private var topProductList: ArrayList<TopProductDataItem> = ArrayList()
    private var topCategoryList: ArrayList<TopCategoryDataItem> = ArrayList()

    private var dateFilterModel: DateFilterModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsOverViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customerViewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]

        initRecyclerView()
        initObservers()
        initLineChart()

        dateFilterModel = Utility.getLastTwelveMonthDateFilterModel()
        initLayout(dateFilterModel!!)

        binding.tvCustomerWiseSalesViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CustomerWiseSalesActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_ID)
                    .putExtra(AppConstant.DATE_FILTER, dateFilterModel)
            )
        }

        binding.tvStaffWiseSalesViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CustomerWiseSalesActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_TYPE, AppConstant.STAFF_ID)
                    .putExtra(AppConstant.DATE_FILTER, dateFilterModel)
            )
        }

        binding.tvTopProductsViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    TopProductSalesActivity::class.java
                ).putExtra(AppConstant.TOP_PRODUCT, true)
                    .putExtra(AppConstant.DATE_FILTER, dateFilterModel)
            )
        }

        binding.tvTopCategoryViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    TopProductSalesActivity::class.java
                ).putExtra(AppConstant.TOP_CATEGORY, true)
                    .putExtra(AppConstant.DATE_FILTER, dateFilterModel)
            )
        }


        binding.tvFilter.setOnClickListener {
            val fragment = AnalyticsFilterBottomSheetDialogFragment(this, dateFilterModel!!)
            fragment.show(supportFragmentManager, "tag")
        }

        binding.ivBack.setOnClickListener { finish() }
    }


    private fun initLayout(dateFilterModel: DateFilterModel) {

        binding.progressBar.visibility = View.VISIBLE

        binding.tvFilter.text = dateFilterModel.title
        binding.tvDateCustomerWiseSales.text = dateFilterModel.title
        binding.tvDateStaffWiseSales.text = dateFilterModel.title
        binding.tvDateTopProducts.text = dateFilterModel.title
        binding.tvDateTopCategory.text = dateFilterModel.title

        binding.shimmerCustomerWiseSales.visibility = View.VISIBLE
        binding.rvCustomerWiseSales.visibility = View.GONE
        binding.tvCustomerWiseSalesErrorMessage.visibility = View.GONE
        binding.tvCustomerWiseSalesViewAll.visibility = View.GONE

        customerViewModel.getCustomerWiseSalesList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            1
        )

        binding.shimmerStaffWiseSales.visibility = View.VISIBLE
        binding.rvStaffWiseSales.visibility = View.GONE
        binding.tvStaffWiseSalesErrorMessage.visibility = View.GONE
        binding.tvStaffWiseSalesViewAll.visibility = View.GONE

        customerViewModel.getStaffWiseSalesList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            1
        )

        binding.shimmerTopProduct.visibility = View.VISIBLE
        binding.rvStaffWiseSales.visibility = View.GONE

        customerViewModel.getOrganizationWiseSalesList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            1
        )

        if (dateFilterModel.filter_type != AppConstant.WEEKLY &&
            dateFilterModel.filter_type != AppConstant.CUSTOMER_DATE_FILTER
        ) {
            binding.shimmerTopProduct.visibility = View.VISIBLE
            binding.shimmerTopCategory.visibility = View.VISIBLE

            binding.tvProductErrorMessage.visibility = View.GONE
            binding.tvCategoryErrorMessage.visibility = View.GONE
            binding.tvTopProductsViewAll.visibility = View.GONE
            binding.tvTopCategoryViewAll.visibility = View.GONE

            customerViewModel.getTopProductList(
                dateFilterModel.filter_type!!,
                dateFilterModel.startDate!!,
                dateFilterModel.end_date!!,
                1
            )

            customerViewModel.getTopCategoryList(
                dateFilterModel.filter_type!!,
                dateFilterModel.startDate!!,
                dateFilterModel.end_date!!,
                1
            )
        } else {
            binding.shimmerTopProduct.visibility = View.GONE
            binding.shimmerTopCategory.visibility = View.GONE

            binding.tvProductErrorMessage.visibility = View.VISIBLE
            binding.tvCategoryErrorMessage.visibility = View.VISIBLE

            binding.tvTopProductsViewAll.visibility = View.GONE
            binding.tvTopCategoryViewAll.visibility = View.GONE

            binding.tvProductErrorMessage.text =
                getString(R.string.no_data_available_for_weekly_and_monthly)
            binding.tvCategoryErrorMessage.text =
                getString(R.string.no_data_available_for_weekly_and_monthly)
        }

    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerWiseSales.layoutManager = linearLayoutManager
        customerWiseSalesAdapter = CustomerWiseSalesAdapter(customerWiseSalesList)
        binding.rvCustomerWiseSales.adapter = customerWiseSalesAdapter

        val linearLayoutManager1 = LinearLayoutManager(this)
        binding.rvStaffWiseSales.layoutManager = linearLayoutManager1
        staffWiseSalesAdapter = StaffWiseSalesAdapter(staffWiseSalesList)
        binding.rvStaffWiseSales.adapter = staffWiseSalesAdapter

        val linearLayoutManager2 = LinearLayoutManager(this)
        binding.rvTopProducts.layoutManager = linearLayoutManager2
        topProductSalesAdapter = TopProductSalesAdapter(topProductList)
        binding.rvTopProducts.adapter = topProductSalesAdapter

        val linearLayoutManager3 = LinearLayoutManager(this)
        binding.rvTopCategory.layoutManager = linearLayoutManager3
        topCategorySalesAdapter = TopCategorySalesAdapter(topCategoryList)
        binding.rvTopCategory.adapter = topCategorySalesAdapter
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.customerWiseSalesLiveData.observe(this) { data ->
            data.data?.let {
                binding.shimmerCustomerWiseSales.visibility = View.GONE
                isPageLoading = false
                if (it.isNotEmpty()) {
                    binding.tvCustomerWiseSalesErrorMessage.visibility = View.GONE
                    binding.tvCustomerWiseSalesViewAll.visibility = View.VISIBLE
                    if (it.size > 5) {
                        customerWiseSalesList.addAll(it.take(5))
                    } else {
                        customerWiseSalesList.addAll(it)
                    }

                    binding.rvCustomerWiseSales.visibility = View.VISIBLE
                    customerWiseSalesAdapter.notifyDataSetChanged()

                } else {
                    binding.tvCustomerWiseSalesErrorMessage.visibility = View.VISIBLE
                    binding.tvCustomerWiseSalesViewAll.visibility = View.GONE
                    binding.tvCustomerWiseSalesErrorMessage.text =
                        "No sales found for " + dateFilterModel?.title
                }
            }
        }

        customerViewModel.staffWiseSalesLiveData.observe(this) { data ->
            data.data?.let {
                isPageLoading = false
                binding.shimmerStaffWiseSales.visibility = View.GONE
                if (it.isNotEmpty()) {
                    binding.tvStaffWiseSalesErrorMessage.visibility = View.GONE
                    binding.tvStaffWiseSalesViewAll.visibility = View.VISIBLE
                    if (it.size > 5) {
                        staffWiseSalesList.addAll(it.take(5))
                    } else {
                        staffWiseSalesList.addAll(it)
                    }

                    binding.rvStaffWiseSales.visibility = View.VISIBLE

                    staffWiseSalesAdapter.notifyDataSetChanged()

                } else {
                    binding.tvStaffWiseSalesErrorMessage.visibility = View.VISIBLE
                    binding.tvStaffWiseSalesErrorMessage.text =
                        "No sales found for " + dateFilterModel?.title
                    binding.tvStaffWiseSalesViewAll.visibility = View.GONE
                }
            }
        }

        customerViewModel.topProductLiveData.observe(this) { data ->
            data.data?.let {
                isPageLoading = false
                binding.shimmerTopProduct.visibility = View.GONE
                if (it.isNotEmpty()) {
                    binding.groupTopProduct.visibility = View.VISIBLE
                    binding.tvTopProductsViewAll.visibility = View.VISIBLE

                    if (it.size > 5) {
                        topProductList.addAll(it.take(5))
                    } else {
                        topProductList.addAll(it)
                    }

                    topProductSalesAdapter.notifyDataSetChanged()


                } else {
                    topProductList.clear()
                    topProductSalesAdapter.notifyDataSetChanged()

                    binding.tvProductErrorMessage.visibility = View.VISIBLE
                    binding.tvProductErrorMessage.text =
                        "No product found for " + dateFilterModel?.title
                    binding.tvTopProductsViewAll.visibility = View.GONE
                }
            }

            customerViewModel.topCategoryLiveData.observe(this) { customerData ->
                customerData.data?.let {
                    topCategoryList.clear()
                    isPageLoading = false
                    binding.shimmerTopCategory.visibility = View.GONE
                    if (it.isNotEmpty()) {
                        binding.groupTopCategory.visibility = View.VISIBLE
                        binding.tvTopCategoryViewAll.visibility = View.VISIBLE

                        if (it.size > 5) {
                            topCategoryList.addAll(it.take(5))
                        } else {
                            topCategoryList.addAll(it)
                        }

                        topCategorySalesAdapter.notifyDataSetChanged()

                    } else {
                        topCategoryList.clear()
                        topCategorySalesAdapter.notifyDataSetChanged()

                        binding.tvCategoryErrorMessage.visibility = View.VISIBLE
                        binding.tvCategoryErrorMessage.text =
                            "No category found for " + dateFilterModel?.title
                        binding.tvTopCategoryViewAll.visibility = View.GONE
                    }

                }
            }

            customerViewModel.organizationWiseSalesLiveData.observe(this) { it ->
                binding.progressBar.visibility = View.GONE
                it.data?.let {
                    isPageLoading = false
                    if (it.isNotEmpty()) {
                        setData(it.asReversed())
                    }
                }
            }
        }
    }


    private fun initLineChart() {
        // background color
        binding.chart.setBackgroundColor(Color.TRANSPARENT)

        // disable description text
        binding.chart.description.isEnabled = false

        // enable touch gestures
        binding.chart.setTouchEnabled(true)

        // set listeners
        binding.chart.setOnChartValueSelectedListener(this)
        binding.chart.setDrawGridBackground(false)

        // create marker to display box when values are selected
        val mv = MyMarkerView(this, R.layout.custom_marker_view)

        // Set the marker to the binding.chart
        mv.chartView = binding.chart
        binding.chart.marker = mv

        // enable scaling and dragging
        binding.chart.isDragEnabled = false
        binding.chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        binding.chart.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis? = binding.chart.xAxis
        xAxis?.gridColor = ContextCompat.getColor(applicationContext,R.color.chart_grid_color)
        xAxis?.textColor = ContextCompat.getColor(applicationContext,R.color.chart_grid_color)

        // vertical grid lines
        xAxis?.enableGridDashedLine(10f, 0f, 0f)
        xAxis!!.position = XAxis.XAxisPosition.BOTTOM

        xAxis.textColor = ContextCompat.getColor(applicationContext,R.color.theme_purple)
        xAxis.typeface = Typeface.DEFAULT_BOLD

        // // Y-Axis Style // //
        val yAxis: YAxis = binding.chart.axisLeft
        yAxis.gridColor = ContextCompat.getColor(applicationContext,R.color.chart_grid_color)
        yAxis.axisLineColor = ContextCompat.getColor(applicationContext,R.color.chart_grid_color)

        // disable dual axis (only use LEFT axis)
        binding.chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(0f, 0f, 0f)

        // axis range
        yAxis.axisMinimum = 0f
        yAxis.textColor = ContextCompat.getColor(applicationContext,R.color.theme_purple)
        yAxis.typeface = Typeface.DEFAULT_BOLD

        // // Create Limit Lines // //
        val llXAxis = LimitLine(0f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.lineColor = ContextCompat.getColor(applicationContext,R.color.chart_grid_color)

        llXAxis.enableDashedLine(0f, 10f, 0f)

        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(false)
        xAxis.setDrawLimitLinesBehindData(false)

        // draw points over time

        // draw points over time
        binding.chart.animateX(500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l = binding.chart.legend

        // draw legend entries as lines

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE
    }


    private fun setData(list: MutableList<OrganizationWiseSalesDataItem>) {
        val values = ArrayList<Entry>()
        val xAxis = binding.chart.xAxis
        val xAxisValues: ArrayList<String> = ArrayList()

        xAxis.labelCount = list.size - 1

        list.forEach {
            xAxisValues.add(it.month.toString())
        }

        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        list.forEachIndexed { index, model ->
            val value: Double? = model.totalAmountSales
            values.add(Entry((index).toFloat(), value?.toFloat()!!, null))
        }

        val yAxis: YAxis = binding.chart.axisLeft
        yAxis.axisMaximum = getMax(list).toFloat()

        binding.chart.invalidate()

        val set1: LineDataSet
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            set1 = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "Months")
            set1.setDrawIcons(false)

            // black lines and points
            set1.color = ContextCompat.getColor(applicationContext,R.color.check_score_bg_first)
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 3f
            set1.circleRadius = 4f

            // draw points as solid circles
            set1.setDrawCircleHole(true)

            // text size of values
            set1.valueTextSize = 0f
            set1.valueTextColor = ContextCompat.getColor(applicationContext,R.color.theme_purple)

            // set the filled area
            set1.setDrawFilled(false)

            set1.fillFormatter =
                IFillFormatter { _, _ -> binding.chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.border_black)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = java.util.ArrayList<ILineDataSet>()

            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            binding.chart.data = data
        }
    }

    private fun getMax(list: MutableList<OrganizationWiseSalesDataItem>): Int {
        var max = Int.MIN_VALUE
        for (i in 0 until list.size) {
            if (list[i].totalAmountSales?.roundToInt()!! > max) {
                max = list[i].totalAmountSales?.toInt()!!
            }
        }

        max += max / 2
        return max
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
    }

    override fun onNothingSelected() {
    }

    override fun onFilterDate(model: DateFilterModel) {
        customerWiseSalesList.clear()
        staffWiseSalesList.clear()
        topProductList.clear()
        topCategoryList.clear()
        dateFilterModel = model
        initLayout(model)
    }

}