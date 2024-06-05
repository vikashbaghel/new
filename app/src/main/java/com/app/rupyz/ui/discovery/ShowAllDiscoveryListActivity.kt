package com.app.rupyz.ui.discovery

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.databinding.ActivityShowAllDiscoveryListBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.OrgItem
import com.app.rupyz.model_kt.ProductItem
import com.app.rupyz.model_kt.ProductSource
import com.app.rupyz.ui.organization.profile.OrgProfileActivity

class ShowAllDiscoveryListActivity : AppCompatActivity(), DiscoverySelectedListener {
    private lateinit var binding: ActivityShowAllDiscoveryListBinding
    private lateinit var discoveryViewModel: DiscoveryViewModel

    private lateinit var discoveryAllProductListAdapter: DiscoveryAllProductListAdapter
    private lateinit var discoveryAllBusinessListAdapter: DiscoveryAllBusinessListAdapter

    private var orgList = ArrayList<OrgItem>()
    private var productList = ArrayList<ProductItem>()
    private lateinit var layoutManager: GridLayoutManager

    private var isPageLoading = false
    private var isLastPageExist = false
    private var currentPage = 1
    private var badge: Int = 0
    private var location: String = ""
    private var searchString: String = ""

    private lateinit var connectionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowAllDiscoveryListBinding.inflate(layoutInflater)

        discoveryViewModel = ViewModelProvider(this)[DiscoveryViewModel::class.java]

        initLayout()

        binding.progressBarOrg.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.LOCATION)) {
            location = intent.getStringExtra(AppConstant.LOCATION)!!
        }

        if (intent.hasExtra(AppConstant.BADGE)) {
            badge = intent.getIntExtra(AppConstant.BADGE, 0)!!
        }
        if (intent.hasExtra(AppConstant.SEARCH_STRING)) {
            searchString = intent.getStringExtra(AppConstant.SEARCH_STRING)!!
        }

        if (intent.hasExtra(AppConstant.CONNECTION_TYPE)) {
            connectionType = intent.getStringExtra(AppConstant.CONNECTION_TYPE)!!
            if (intent.getStringExtra(AppConstant.CONNECTION_TYPE)
                    .equals(AppConstant.ORGANIZATION)
            ) {
                initBusinessRecyclerView()
                binding.tvToolbarTitle.text = "Businesses"
                getResult()
            } else {
                initProductRecyclerView()
                binding.tvToolbarTitle.text = "Products"
                getResult()
            }
        }

        initObservers()

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun initLayout() {
        layoutManager = GridLayoutManager(this, 2)

        binding.rvOrgList.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getResult()
            }

            override fun isLastPage(): Boolean {
                return isLastPageExist
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun initObservers() {
        discoveryViewModel.discoveryListLiveData.observe(this) { data ->
            data?.data.let {
                isPageLoading = false
                if (currentPage == 1) {
                    orgList.clear()
                    productList.clear()
                }

                binding.progressBarOrg.visibility = View.GONE

                if (it?.org!!.isNotEmpty()) {
                    orgList.addAll(it.org)
                    if (orgList.isEmpty() || orgList.size < 30) {
                        isLastPageExist = true
                    }
                    discoveryAllBusinessListAdapter.notifyDataSetChanged()
                }

                if (it.product!!.isNotEmpty()) {
                    productList.addAll(it.product)
                    if (productList.isEmpty() || productList.size < 30) {
                        isLastPageExist = true
                    }
                    discoveryAllProductListAdapter.notifyDataSetChanged()

                }
            }

        }
    }

    private fun getResult() {
        if (currentPage > 1) {
            binding.progressBarOrg.visibility = View.VISIBLE
        }

        discoveryViewModel.getDiscoveryListWithPagination(
            connectionType,
            searchString,
            location,
            badge,
            currentPage
        )
    }

    private fun initProductRecyclerView() {
        binding.rvOrgList.layoutManager = layoutManager
        discoveryAllProductListAdapter = DiscoveryAllProductListAdapter(productList, this)
        binding.rvOrgList.adapter = discoveryAllProductListAdapter
    }

    private fun initBusinessRecyclerView() {
        binding.rvOrgList.layoutManager = layoutManager
        discoveryAllBusinessListAdapter = DiscoveryAllBusinessListAdapter(orgList, this)
        binding.rvOrgList.adapter = discoveryAllBusinessListAdapter
    }

    override fun onProductClick(slug: String?, product: ProductSource?) {
        startActivity(
            Intent(
                this,
                OrgProfileActivity::class.java
            )
                .putExtra(AppConstant.PROFILE_SLUG, slug)
                .putExtra(AppConstant.PRODUCT_INFO, product)
        )
    }

    override fun onOrgClick(slug: String?) {
        startActivity(
            Intent(
                this,
                OrgProfileActivity::class.java
            ).putExtra(AppConstant.PROFILE_SLUG, slug)
        )
    }

    override fun onProductShare(product: ProductSource?) {
        Utility.shareOthersProductWithAll(
            this, product?.name,
            AppConstant.getShareProductUrlForDiscovery(product?.orgSlug, product?.nanoId)
        )
    }

}