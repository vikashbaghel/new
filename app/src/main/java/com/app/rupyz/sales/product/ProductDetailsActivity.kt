package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrgProductDetailBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.VariantDataInfoItem
import com.app.rupyz.model_kt.VariantOptionsItem
import com.app.rupyz.model_kt.VariantsItem
import com.app.rupyz.ui.organization.adapter.OrgProductSpecificationAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class ProductDetailsActivity : BaseActivity(),
    ProductImageViewPagerAdapter.ProductImageClickListener,
    ProductVariantsOptionsAdapter.VariantOptionSelectedListener {
    private lateinit var binding: ActivityOrgProductDetailBinding
    private val productViewModel: ProductViewModel by viewModels()
    private var dotCount = 0
    private lateinit var mProductImageViewPagerAdapter: ProductImageViewPagerAdapter
    private lateinit var adapter: OrgProductSpecificationAdapter
    private lateinit var variantsAdapter: ProductVariantsAdapter

    private var imageViewModelArrayList: ArrayList<ImageViewModel> = ArrayList()
    private var variantsItemList: ArrayList<VariantsItem> = ArrayList()
    private var specificationList: HashMap<String, String> = HashMap()
    private var imageListModel: OrgImageListModel? = null
    private var productModel: ProductDetailInfoModel? = null
    private var selectedSetsHashMap = HashMap<String?, String?>()
    private var variantsDataList: ArrayList<VariantDataInfoItem> = ArrayList()

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
        binding = ActivityOrgProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        initObservers()
        if (intent.hasExtra(AppConstant.PRODUCT_ID)) {
            binding.mainContent.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            productViewModel.getProductDetails(
                intent.getIntExtra("product_id", 0),
                customerId = null,
                hasInternetConnection()
            )
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding.rvVariants.layoutManager = LinearLayoutManager(this)
        variantsAdapter = ProductVariantsAdapter(variantsItemList, this)
        binding.rvVariants.adapter = variantsAdapter

    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val intent = Intent()
            intent.putExtra(AppConstant.EDIT_PRODUCT, true)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        productViewModel.productDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { mData ->
                    productModel = mData

                    showProductDetails(mData)

                    if (mData.variants.isNullOrEmpty().not()) {
                        mData.variants?.forEach { variant ->
                            if (variant.options.isNullOrEmpty().not()) {
                                selectedSetsHashMap[variant.name] = variant.options!![0].optionId
                            }
                            variantsItemList.add(variant)
                            variantsAdapter.notifyItemInserted(variantsItemList.size)
                        }
                        binding.groupVariants.visibility = View.VISIBLE
                    }

                    if (mData.variant_data.isNullOrEmpty().not()) {
                        variantsDataList.addAll(mData.variant_data)

                        if (mData.primary_product != null) {
                            val product =
                                variantsDataList.find { product -> product.productData?.id == mData.id }

                            if (product != null) {
                                val identifier = product.identifier?.split("-")

                                for (variant in variantsItemList) {
                                    if (variant.options.isNullOrEmpty().not()) {
                                        for (option in variant.options!!) {
                                            if (identifier!!.contains(option.optionId!!)) {
                                                option.isSelected = true
                                            }
                                        }
                                    }
                                }
                            }

                            variantsAdapter.notifyDataSetChanged()
                        }
                    }

                    binding.mainContent.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProductDetails(mData: ProductDetailInfoModel) {

        binding.txvProductName.text = mData.name
        binding.headerText.text = mData.name

        if (mData.variant_name.isNullOrEmpty().not()) {
            val productNameForVariant = mData.name.replace(mData.variant_name, "")
            binding.txvProductName.text = productNameForVariant.replaceFirstChar(Char::titlecase)
            binding.headerText.text = productNameForVariant.replaceFirstChar(Char::titlecase)
        }

        binding.txtCategory.text = mData.category
        if (mData.code != null) {
            binding.txvProductCode.text = mData.code
        } else {
            binding.txvProductCode.visibility = View.GONE
        }

        if (mData.brand != null && mData.brand.isNotEmpty()) {
            binding.tvBrand.text = mData.brand
            binding.groupBrand.visibility = View.VISIBLE
        } else {
            binding.groupBrand.visibility = View.GONE
        }

        binding.txtMrpPrice.text =
            CalculatorHelper().convertCommaSeparatedAmount(
                mData.mrp_price,
                AppConstant.FOUR_DECIMAL_POINTS
            )

        binding.tvMrpUnit.text = "per " + mData.mrp_unit

        if (mData.gst_exclusive != null) {
            if (mData.gst_exclusive) {
                binding.txtGstAmount.text = mData.gst.toString() + " %  (Exclusive)"
            } else {
                binding.txtGstAmount.text = mData.gst.toString() + " %  (Inclusive)"
            }
        }

        binding.tvHsnCode.text = mData.hsn_code

        binding.txtBuyersPrice.text =
            CalculatorHelper().convertCommaSeparatedAmount(
                mData.price,
                AppConstant.FOUR_DECIMAL_POINTS
            ) + " per " + mData.unit

        if (mData.is_out_of_stock != null && mData.is_out_of_stock) {
            binding.tvOutOfStock.visibility = View.VISIBLE
        } else {
            binding.tvOutOfStock.visibility = View.GONE
        }

        if (mData.description.isNullOrEmpty().not()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.txtDesc.text =
                    Html.fromHtml(mData.description, Html.FROM_HTML_MODE_LEGACY)
            } else {
                binding.txtDesc.text =
                    Html.fromHtml(mData.description)
            }
            binding.groupDescription.visibility = View.VISIBLE
        } else {
            binding.groupDescription.visibility = View.GONE
        }

        if (mData.specification.isNullOrEmpty().not()) {
            specificationList = mData.specification
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = OrgProductSpecificationAdapter(specificationList)
            binding.recyclerView.adapter = adapter

            binding.groupSpecification.visibility = View.VISIBLE
        } else {
            binding.groupSpecification.visibility = View.GONE
        }

        if (mData.packaging_level.isNullOrEmpty().not()) {
            val stringBuilder = StringBuilder()
            for (i in mData.packaging_level.indices) {
                stringBuilder.append(CalculatorHelper().calculateQuantity(mData.packaging_level[i].size))
                    .append(" \u0078 ")
                    .append(mData.unit).append(" = ").append(
                        mData.packaging_level[i].unit
                    )
                if (i < mData.packaging_level.size - 1) {
                    stringBuilder.append("\n")
                }
            }
            binding.txtPackagingSize.text = stringBuilder.toString()
        } else {
            binding.txtPackagingSize.text = resources.getString(
                R.string.packaging_size_with_unit_with_cross,
                "${mData.packaging_size}", mData.unit
            )
        }

        try {
            if (mData.pics_urls.size > 0) {
                mProductImageViewPagerAdapter = ProductImageViewPagerAdapter(
                    this@ProductDetailsActivity, mData.pics_urls, this
                )
                binding.viewPager.adapter = mProductImageViewPagerAdapter
                initViewPager()

                for (url in mData.pics_urls) {
                    val model = ImageViewModel(0, 0, url)
                    imageViewModelArrayList.add(model)
                }
            }
        } catch (ex: Exception) {
            log(ex.message.toString())
        }
    }

    private fun initViewPager() {
        dotCount = mProductImageViewPagerAdapter.count
        val dots: ArrayList<ImageView> = ArrayList()

        for (i in 0 until dotCount) {
            val image = ImageView(this)
            image.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.pager_non_active_dot
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dots.add(image)
            binding.sliderDots.addView(dots[i], params)
        }

        dots[0].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.pager_active_dot))

        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                dots.forEach { image ->
                    image.setImageResource(
                        R.drawable.pager_non_active_dot
                    )
                }

                if (position < dots.size) {
                    dots[position].setImageResource(
                        R.drawable.pager_active_dot
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onImageClick(position: Int) {
        if (imageViewModelArrayList.size > 0) {
            imageListModel = OrgImageListModel()
            imageListModel?.data = imageViewModelArrayList
            startActivity(
                Intent(this, OrgPhotosViewActivity::class.java)
                    .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                    .putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onVariantOptionSelected(variantName: String?, mode: VariantOptionsItem) {
        binding.sliderDots.removeAllViews()
        dotCount = 0
        imageViewModelArrayList.clear()

        selectedSetsHashMap[variantName] = mode.optionId

        val idList = mutableListOf<String?>()
        selectedSetsHashMap.forEach { entry ->
            idList.add(entry.value)
        }

        if (variantsDataList.isNotEmpty()) {
            val index = variantsDataList.indexOfLast {
                val identifierParts = it.identifier?.split("-")?.sorted()
                identifierParts == idList.filterNotNull().sorted()
            }.takeIf { it != -1 }

            index?.let {
                if (variantsDataList[index].productData != null) {
                    showProductDetails(variantsDataList[index].productData!!)
                }
            }
        }

        var selectedIndex = -1
        variantsItemList.forEachIndexed { index, variant ->
            variant.options?.forEach { option ->
                if (option.name == mode.name) {
                    selectedIndex = index
                    return@forEach
                }
            }
        }
        if (selectedIndex != -1) {
            variantsItemList[selectedIndex].options?.forEach {
                it.isSelected = false
            }

            variantsItemList[selectedIndex].options?.forEach {
                if (it.name == mode.name) {
                    it.isSelected = true
                }
            }
        }
        variantsAdapter.notifyDataSetChanged()
    }
}