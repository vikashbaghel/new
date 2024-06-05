package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrgProductDetailBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.ui.organization.adapter.OrgProductSpecificationAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class ProductDetailsActivity : BaseActivity(),
        ProductImageViewPagerAdapter.ProductImageClickListener {
    private lateinit var binding: ActivityOrgProductDetailBinding
    private lateinit var productViewModel: ProductViewModel
    private var adapter: OrgProductSpecificationAdapter? = null
    private var dotCount = 0
    private lateinit var mProductImageViewPagerAdapter: ProductImageViewPagerAdapter
    private var imageViewModelArrayList: ArrayList<ImageViewModel> = ArrayList()
    private var imageListModel: OrgImageListModel? = null
    private var productModel: ProductDetailInfoModel? = null

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

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        initToolbar()
        initLayout()
        initObservers()

        if (intent.hasExtra(AppConstant.PRODUCT_ID)) {
            binding.progressBar.visibility = View.VISIBLE;
            productViewModel.getProductDetails(
                intent.getIntExtra("product_id", 0),
                hasInternetConnection()
            )
        }
    }

    private fun initLayout() {
        binding.btnOverflow.isVisible = hasInternetConnection()

        binding.btnOverflow.setOnClickListener { v ->
            //creating a popup menu
            val popup =
                    PopupMenu(v.context, binding.btnOverflow)
            //inflating menu from xml resource
            popup.inflate(R.menu.product_action_menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.edit_product -> {
                        someActivityResultLauncher.launch(
                                Intent(this, AddProductActivity::class.java)
                                        .putExtra(AppConstant.EDIT_PRODUCT, "true")
                                        .putExtra(AppConstant.PRODUCT_ID, productModel?.id)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    R.id.delete_product -> {
                        showDeleteDialog(
                                false,
                                resources.getString(R.string.delete_product_message)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }
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


    private fun showDeleteDialog(isForced: Boolean, message: String?) {
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
            deleteProduct(isForced)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteProduct(isForced: Boolean) {
        if (productModel?.id != null) {
            binding.progressBar.visibility = View.VISIBLE
            productViewModel.deleteProduct(productModel?.id!!, isForced)
        } else {
            Toast.makeText(
                    this, "Something went wrong, try after sometimes!!",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        productViewModel.productDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { mData ->
                    productModel = mData

                    binding.txvProductName.text = mData.name
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.txtDesc.text =
                                Html.fromHtml(mData.description, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        binding.txtDesc.text =
                                Html.fromHtml(mData.description)
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
                            CalculatorHelper().convertCommaSeparatedAmount(mData.mrp_price, AppConstant.FOUR_DECIMAL_POINTS)

                    binding.tvMrpUnit.text = "per " + mData.mrp_unit

                    if (mData.gst_exclusive) {
                        binding.txtGstAmount.text = mData.gst.toString() + " %  (Exclusive)"
                    } else {
                        binding.txtGstAmount.text = mData.gst.toString() + " %  (Inclusive)"
                    }

                    binding.tvHsnCode.text = mData.hsn_code

                    binding.txtBuyersPrice.text =
                            CalculatorHelper().convertCommaSeparatedAmount(mData.price, AppConstant.FOUR_DECIMAL_POINTS) + " per " + mData.unit

                    if (mData.is_out_of_stock != null && mData.is_out_of_stock) {
                        binding.tvOutOfStock.visibility = View.VISIBLE
                    } else {
                        binding.tvOutOfStock.visibility = View.GONE
                    }

                    if (mData.specification.isNotEmpty()) {
                        try {
                            binding.recyclerView.setHasFixedSize(true)
                            binding.recyclerView.layoutManager =
                                    LinearLayoutManager(this@ProductDetailsActivity)
                            adapter = OrgProductSpecificationAdapter(
                                    this@ProductDetailsActivity,
                                    mData.specification
                            )
                            binding.recyclerView.adapter = adapter
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    } else {
                        binding.hdSpecification.visibility = View.GONE
                    }

                    if (mData.packaging_level.isNullOrEmpty().not()) {
                        val stringBuilder = StringBuilder()
                        for (i in mData.packaging_level.indices) {
                            stringBuilder.append(CalculatorHelper().calculateQuantity(mData.packaging_level[i].size)).append(" \u0078 ")
                                    .append(mData.unit).append(" = ").append(
                                            mData.packaging_level[i].unit
                                    )
                            if (i < mData.packaging_level.size - 1) {
                                stringBuilder.append("\n")
                            }
                        }
                        binding.txtPackagingSize.text = stringBuilder.toString()
                    } else {
                        binding.txtPackagingSize.text =
                                mData.packaging_size.toString() + " \u0078 " + mData.unit
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
                    } catch (Ex: Exception) {
                        Logger.errorLogger("ProductAdapter", Ex.message)
                    }

                    binding.mainContent.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.deleteProductLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                if (it.data != null && it.data?.isUsed == true) {
                    showDeleteDialog(true, it.message)
                } else {
                    Toast.makeText(this@ProductDetailsActivity, it.message, Toast.LENGTH_SHORT)
                            .show()

                    val intent = Intent()
                    intent.putExtra(AppConstant.EDIT_PRODUCT, true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun initViewPager() {
        dotCount = mProductImageViewPagerAdapter.count
        val dots: ArrayList<ImageView> = ArrayList()

        for (i in 0 until dotCount) {
            val image = ImageView(this)
            image.setImageDrawable(getDrawable(R.drawable.pager_non_active_dot))
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dots.add(image)
            binding.sliderDots.addView(dots[i], params)
        }

        dots[0].setImageDrawable(getDrawable(R.drawable.pager_active_dot))

        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotCount) {
                    dots[i].setImageDrawable(getDrawable(R.drawable.pager_non_active_dot))
                }
                dots[position].setImageDrawable(getDrawable(R.drawable.pager_active_dot))
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initToolbar() {
        binding.headerText.text = intent.extras!!.getString("product_name")
        binding.imgBack.setOnClickListener { onBackPressed() }
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
}