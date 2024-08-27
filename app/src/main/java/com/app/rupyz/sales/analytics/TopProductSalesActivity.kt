package com.app.rupyz.sales.analytics

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTopProductSalesBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.DateFilterModel
import com.app.rupyz.model_kt.TopCategoryDataItem
import com.app.rupyz.model_kt.TopProductDataItem
import com.app.rupyz.sales.filter.AnalyticsFilterBottomSheetDialogFragment
import com.app.rupyz.sales.filter.IAnalyticsFilterListener

class TopProductSalesActivity : AppCompatActivity(), IAnalyticsFilterListener {
    private lateinit var binding: ActivityTopProductSalesBinding
    private lateinit var customerViewModel: AnalyticsViewModel
    private lateinit var topProductSalesAdapter: TopProductSalesAdapter
    private lateinit var topCategorySalesAdapter: TopCategorySalesAdapter

    private var topProductList: ArrayList<TopProductDataItem> = ArrayList()
    private var topCategoryList: ArrayList<TopCategoryDataItem> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private lateinit var dateFilterModel: DateFilterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopProductSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customerViewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]

        initObservers()

        if (intent.hasExtra(AppConstant.DATE_FILTER)) {
            dateFilterModel = intent.getParcelableExtra(AppConstant.DATE_FILTER)!!
            binding.tvFilter.text = dateFilterModel.title
        }

        if (intent.hasExtra(AppConstant.TOP_PRODUCT)) {
            binding.clCategoryInfo.visibility = View.INVISIBLE
            binding.clProductInfo.visibility = View.VISIBLE
            binding.tvToolbarTitle.text = getString(R.string.top_products)
            initProductRecyclerView()
            loadNextProductPage(dateFilterModel)
        } else {
            binding.tvToolbarTitle.text = getString(R.string.top_category)
            initCategoryRecyclerView()
            loadNextCategoryPage(dateFilterModel)
        }


        binding.clFilter.setOnClickListener {
            val fragment = AnalyticsFilterBottomSheetDialogFragment(this, dateFilterModel)
            fragment.show(supportFragmentManager, "tag")
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun loadNextCategoryPage(dateFilterModel: DateFilterModel) {
        binding.shimmerTopProduct.visibility = View.VISIBLE
        binding.rvTopProducts.visibility = View.GONE

        customerViewModel.getTopCategoryList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            currentPage
        )
    }

    private fun loadNextProductPage(dateFilterModel: DateFilterModel) {
        binding.shimmerTopProduct.visibility = View.VISIBLE
        binding.rvTopProducts.visibility = View.GONE
        customerViewModel.getTopProductList(
            dateFilterModel.filter_type!!,
            dateFilterModel.startDate!!,
            dateFilterModel.end_date!!,
            currentPage
        )
    }


    private fun initCategoryRecyclerView() {
        val linearLayoutManager3 = LinearLayoutManager(this)
        binding.rvTopProducts.layoutManager = linearLayoutManager3
        topCategorySalesAdapter = TopCategorySalesAdapter(topCategoryList)
        binding.rvTopProducts.adapter = topCategorySalesAdapter
    }

    private fun initProductRecyclerView() {
        val linearLayoutManager2 = LinearLayoutManager(this)
        binding.rvTopProducts.layoutManager = linearLayoutManager2
        topProductSalesAdapter = TopProductSalesAdapter(topProductList)
        binding.rvTopProducts.adapter = topProductSalesAdapter
    }

    private fun initObservers() {
        customerViewModel.topProductLiveData.observe(this) { data ->
            binding.shimmerTopProduct.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { it ->
                    isPageLoading = false
                    if (it.isNotEmpty()) {
                        if (currentPage == 1) {
                            topProductList.clear()
                        }

                        topProductList.addAll(it)

                        binding.rvTopProducts.visibility = View.VISIBLE
                        topProductSalesAdapter.notifyDataSetChanged()

                    }
                }
            } else {
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
            }
        }

        customerViewModel.topCategoryLiveData.observe(this) { data ->
            binding.shimmerTopProduct.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { it ->
                    isPageLoading = false
                    if (it.isNotEmpty()) {

                        if (currentPage == 1) {
                            topCategoryList.clear()
                        }

                        topCategoryList.addAll(it)
                    }
                    binding.rvTopProducts.visibility = View.VISIBLE
                    topCategorySalesAdapter.notifyDataSetChanged()

                }
            } else {
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFilterDate(model: DateFilterModel) {
        currentPage = 1
        dateFilterModel = model
        binding.tvFilter.text = model.title.toString()

        if (intent.hasExtra(AppConstant.TOP_PRODUCT)) {
            topProductList.clear()
            loadNextProductPage(dateFilterModel)
        } else {
            topCategoryList.clear()
            loadNextCategoryPage(dateFilterModel)
        }
    }
}