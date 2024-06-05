package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityProductListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.*
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.sales.customer.ProductCategoryListForAssignAdapter
import com.app.rupyz.sales.orders.PkgLevelBottomSheetDialogFragment
import com.app.rupyz.ui.organization.profile.ProductActionListener
import java.lang.IndexOutOfBoundsException

class ProductListActivity : BaseActivity(), ProductActionListener,
    ProductCategoryFilterAdapter.IFilterCategoryListener,
    ProductCategoryListForAssignAdapter.IAssignCategoryListener {
    private lateinit var binding: ActivityProductListBinding

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productItemListAdapter: ProductItemListAdapter
    private lateinit var brandAdapter: ProductCategoryListForAssignAdapter
    private lateinit var categoryFilterAdapter: ProductCategoryFilterAdapter

    private var layoutManager: LinearLayoutManager? = null
    private var layoutManager1: LinearLayoutManager? = null

    private var categoryList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var productLists: ArrayList<ProductList?> = ArrayList()
    private var brandList = ArrayList<AllCategoryResponseModel>()

    private var filteredBrandList = ArrayList<String?>()

    private var isPageLoading = false
    private var isPageLoadingForBrand = false

    private var isApiLastPage = false
    private var isApiLastPageForBrand = false

    private var isDataChange = false
    private var editProductPos = -1
    private var deleteProductPosition = -1
    private var deleteProductModel: ProductList? = null

    private var currentPage = 1
    private var currentPageForBrand = 1

    private var category: String = ""

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

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

        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding.shimmerProduct.visibility = View.VISIBLE

        productLists = ArrayList()
        categoryList = ArrayList()

        category = ""

        initRecyclerView()
        initObservers()

        getBrandList()
        allCategoryList()
        productList()
        initToolbar()


        if (hasInternetConnection().not()
            || PermissionModel.INSTANCE.getPermission(
                AppConstant.CREATE_PRODUCT_PERMISSION,
                false
            ).not()
        ) {
            binding.fabAdd.visibility = View.GONE
        }

        binding.fabAdd.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this,
                    AddProductActivity::class.java
                )
            )
        }

        binding.ivSearch.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchProductListActivity::class.java
                )
            )
        }

        binding.mainContent.setOnClickListener {
            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE
            hideKeyboard()
        }

        binding.tvBrandFilter.setOnClickListener {
            binding.clBrandFilter.isVisible = binding.clBrandFilter.isVisible.not()
            binding.clCategoryFilter.visibility = View.GONE
            hideKeyboard()
        }

        binding.tvCategoryFilter.setOnClickListener {
            binding.clCategoryFilter.isVisible = binding.clCategoryFilter.isVisible.not()
            binding.clBrandFilter.visibility = View.GONE
            hideKeyboard()
        }

        binding.tvClearFilter.setOnClickListener {
            category = ""
            filteredBrandList.clear()
            binding.tvBrandFilter.text = resources.getString(R.string.brand)
            binding.tvCategoryFilter.text = resources.getString(R.string.category)

            currentPage = 1

            productLists.clear()
            productItemListAdapter.notifyDataSetChanged()

            brandList.forEach {
                it.isSelected = false
            }
            brandAdapter.notifyDataSetChanged()

            categoryList.forEach {
                it.isSelected = false
            }

            categoryFilterAdapter.notifyDataSetChanged()

            binding.shimmerProduct.visibility = View.VISIBLE
            productList()

            binding.tvClearFilter.visibility = View.GONE

            binding.clBrandFilter.visibility = View.GONE
            binding.clCategoryFilter.visibility = View.GONE

            hideKeyboard()
        }

        // handing action for brand filter ------

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
            hideKeyboard()
            allCategoryList()
        }

        //---------------
    }

    private val inputFinishCheckerBrand = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPageForBrand = 1
            getBrandList()
        }
    }

    private val inputFinishCheckerCategory = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            allCategoryList()
        }
    }

    private fun getBrandList() {
        productViewModel.getBrandList(
            binding.etSearchBrand.text.toString(),
            currentPageForBrand, hasInternetConnection()
        )
    }

    private fun initObservers() {
        productViewModel.productLiveData.observe(this) {
            binding.shimmerProduct.visibility = View.GONE

            if (it.error == false) {
                if (it.data.isNotEmpty()) {
                    it.data?.let { list ->
                        isPageLoading = false
                        loadProductList(list)
                        if (list.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    if (currentPage == 1) {
                        isApiLastPage = true
                        binding.rvProductItem.visibility = View.GONE
                        binding.clEmptyData.visibility = View.VISIBLE
                        productLists.clear()
                        productItemListAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                }  else {
                    showToast(it.message)
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

        productViewModel.deleteProductLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                if (it.data != null && it.data?.isUsed == true) {
                    showDeleteDialog(deleteProductModel, true, it.message)
                } else if (deleteProductPosition != -1) {
                    try {
                        productLists.removeAt(deleteProductPosition)

                        productLists.removeAt(deleteProductPosition)
                        productItemListAdapter.notifyItemRemoved(deleteProductPosition)

                        productItemListAdapter.notifyItemRangeChanged(
                                deleteProductPosition,
                                productLists.size
                        )
                    } catch (e: IndexOutOfBoundsException){
                        e.printStackTrace()
                        currentPage = 1
                        productLists.clear()
                        binding.shimmerProduct.visibility = View.VISIBLE
                        productList()
                    }
                }
                isDataChange = true
            } else {
                Toast.makeText(
                    this@ProductListActivity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun loadProductList(list: ArrayList<ProductList>) {
        binding.rvProductItem.visibility = View.VISIBLE
        binding.clEmptyData.visibility = View.GONE

        if (productLists.size > 1 && currentPage > 1) {
            productLists.removeAt(productLists.size - 1)
            productItemListAdapter.notifyItemRemoved(productLists.size)
        }
        productLists.addAll(list)

        productItemListAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(this)
        binding.rvProductItem.setHasFixedSize(true)
        binding.rvProductItem.layoutManager = layoutManager
        productItemListAdapter = ProductItemListAdapter(productLists, this, hasInternetConnection())
        binding.rvProductItem.adapter = productItemListAdapter
        binding.rvProductItem.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                productLists.add(null)
                productItemListAdapter.notifyItemInserted(productLists.size)
                productList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvProductItem.setOnTouchListener { _, _ ->
            binding.clCategoryFilter.visibility = View.GONE
            binding.clBrandFilter.visibility = View.GONE
            hideKeyboard()
            false
        }

        layoutManager1 = LinearLayoutManager(this)
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

    private fun initToolbar() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun productList() {
        productViewModel.getProductList(
            "",
            filteredBrandList,
            category,
            currentPage,
            hasInternetConnection()
        )
    }

    private fun allCategoryList() {
        productViewModel.getAllCategoryList(
            null,
            binding.etSearchCategory.text.toString(),
            hasInternetConnection()
        )
    }

    override fun onEditProduct(product: ProductList, position: Int) {
        editProductPos = position
        someActivityResultLauncher.launch(
            Intent(this, AddProductActivity::class.java)
                .putExtra(AppConstant.EDIT_PRODUCT, "true")
                .putExtra(AppConstant.PRODUCT_ID, product.id)
        )
    }

    override fun onDeleteProduct(product: ProductList, position: Int) {
        deleteProductPosition = position
        deleteProductModel = product
        showDeleteDialog(
            product,
            false,
            resources.getString(R.string.delete_product_message)
        )
    }


    private fun showDeleteDialog(
        product: ProductList?,
        isForced: Boolean,
        message: String?
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_item)
        tvTitle.text = message

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            deleteProduct(product, isForced)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteProduct(product: ProductList?, isForced: Boolean) {
        if (product?.id != null) {
            binding.progressBar.visibility = View.VISIBLE
            productViewModel.deleteProduct(product.id!!, isForced)
        } else {
            Toast.makeText(
                this, "Something went wrong, try after sometimes!!",
                Toast.LENGTH_SHORT
            ).show()
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
        if (binding.clCategoryFilter.isVisible || binding.clBrandFilter.isVisible) {
            binding.clCategoryFilter.visibility = View.GONE
            binding.clBrandFilter.visibility = View.GONE
            hideKeyboard()
        } else {
            if (hasInternetConnection()) {
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("product_id", product.id)
                intent.putExtra("product_name", product.name)
                someActivityResultLauncher.launch(intent)
            }
        }
    }

    override fun getPackagingLevelInfo(model: ProductList) {
        val fragment = PkgLevelBottomSheetDialogFragment(model)
        fragment.show(supportFragmentManager, PkgLevelBottomSheetDialogFragment::class.java.name)
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            currentPage = 1
            isApiLastPage = false
            productLists.clear()

            binding.rvProductItem.scrollToPosition(0)

            productList()
            isDataChange = true
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setCategorySelect(checked: Boolean, model: AllCategoryResponseModel) {
        if (checked) {
            filteredBrandList.add(model.name)
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

        productLists.clear()
        productItemListAdapter.notifyDataSetChanged()

        binding.shimmerProduct.visibility = View.VISIBLE
        productList()
    }

    override fun filterCategory(position: Int, model: AllCategoryResponseModel) {
        if (model.isSelected == false) {
            binding.clEmptyData.visibility = View.GONE
            for (i in categoryList.indices) {
                categoryList[i].isSelected = false
            }

            categoryList[position].isSelected = true
            categoryFilterAdapter.notifyDataSetChanged()

            category = model.name?: ""

            binding.tvCategoryFilter.text = category
            currentPage = 1
            productLists.clear()
            productItemListAdapter.notifyDataSetChanged()

            binding.shimmerProduct.visibility = View.VISIBLE
            productList()

            binding.tvClearFilter.visibility = View.VISIBLE
        }

        binding.clCategoryFilter.visibility = View.GONE

        hideKeyboard()
    }


    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}