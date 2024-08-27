package com.app.rupyz.sales.product

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.ActivitySearchProductListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.sales.orders.PkgLevelBottomSheetDialogFragment
import com.app.rupyz.ui.organization.profile.ProductActionListener

class SearchProductListActivity : BaseActivity(), ProductActionListener {
    private lateinit var binding: ActivitySearchProductListBinding
    private var isDataChange: Boolean = false
    private lateinit var adapter: ProductItemListAdapter

    private lateinit var productViewModel: ProductViewModel

    private var productList: ArrayList<ProductList> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    private var deleteProductPosition = -1
    private var deleteProductModel: ProductList? = null

    var delay: Long = 500 // 1 seconds after user stops typing

    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        initRecyclerView()
        initObservers()

        validateSearch()

        binding.etSearch.requestFocus()

        binding.ivSearch.setOnClickListener {
            if (binding.etSearch.text.toString().isNotEmpty()) {
                currentPage = 1
                productList.clear()
                Utils.hideKeyboard(this)
                validateSearch()
            } else {
                Toast.makeText(this, "Please enter some value!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etSearch.text.toString().isNotEmpty()) {
                    currentPage = 1
                    productList.clear()
                    validateSearch()
                    Utils.hideKeyboard(this)
                } else {
                    Toast.makeText(this, "Please enter some value!!", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }
        
        binding.etSearch.addDelayedTextChangeListener(500){ s ->
            
            if (s.toString().isNotEmpty()) {
                binding.ivClearSearch.visibility = View.VISIBLE
            } else {
                binding.ivClearSearch.visibility = View.GONE
                validateSearch()
            }
            
            currentPage = 1
            productList.clear()
            validateSearch()
            
            binding.tvErrorMessage.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            binding.tvErrorMessage.visibility = View.GONE

            productList.clear()
            adapter.notifyDataSetChanged()

            validateSearch()
        }

        binding.ivBack.setOnClickListener { finish() }
        
    }


    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvProductList.setHasFixedSize(true)
        binding.rvProductList.layoutManager = layoutManager
        adapter = ProductItemListAdapter(
            productList,
            this,
            hasInternetConnection(),
        )
        binding.rvProductList.adapter = adapter

        binding.rvProductList.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                validateSearch()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun validateSearch() {
        isApiLastPage = false
        binding.shimmerProduct.visibility = View.VISIBLE
        productViewModel.getProductList(
            binding.etSearch.text.toString(),
            ArrayList(),
            "",
            currentPage,
            hasInternetConnection()
        )
    }

    private fun initObservers() {
        productViewModel.productLiveData.observe(this) {
            binding.shimmerProduct.visibility = View.GONE
            if (it.error == false) {
                if (it.data.isNotEmpty()) {
                    binding.rvProductList.visibility = View.VISIBLE

                    isPageLoading = false
                    productList.addAll(it.data)
                    binding.tvErrorMessage.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    if (it.data.size < 30) {
                        isApiLastPage = true
                    }
                } else if (currentPage != 1 && productList.size == 0) {
                    isApiLastPage = true
                } else {
                    isApiLastPage = true
                    binding.rvProductList.visibility = View.GONE
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(
                    this@SearchProductListActivity, it.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onShareProduct(product: ProductList, position: Int) {
        Utility.shareMyProductWithAll(
            this,
            product.name,
            product.productUrl
        )
    }

    override fun getProductDetails(product: ProductList, position: Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra("product_id", product.id)
        intent.putExtra("product_name", product.name)
        someActivityResultLauncher.launch(intent)
    }

    override fun getPackagingLevelInfo(model: ProductList) {
        val fragment = PkgLevelBottomSheetDialogFragment(model)
        fragment.show(supportFragmentManager, PkgLevelBottomSheetDialogFragment::class.java.name)
    }


    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            productList = java.util.ArrayList<ProductList>()
            validateSearch()
            isDataChange = true
        }
    }

}