package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetProductDetailsBinding
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.network.EquiFaxApiInterface
import com.app.rupyz.generic.utils.DimenUtils
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.ui.organization.adapter.OrgProductSpecificationAdapter
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailsBottomSheetDialogFragment(
    var listener: IProductBottomSheetActionListener,
    var model: ProductList,
    var customerId: Int?,
    var position: Int
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetProductDetailsBinding
    private var mProductImageViewPagerAdapter: ProductImageViewPagerAdapter? = null
    private var adapter: OrgProductSpecificationAdapter? = null
    private var dotCount = 0
    private var dots: Array<ImageView?> = arrayOfNulls(dotCount)

    private var imageViewModelArrayList: ArrayList<ImageViewModel>? = null
    private var mEquiFaxApiInterface: EquiFaxApiInterface? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG.not() && SharedPref.getInstance()
                .getBoolean(SharePrefConstant.DISABLE_SCREENSHOT_ON_PRODUCTS, false)
        ) {
            dialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()

        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface::class.java)

        initData()

        binding.ivBack.setOnClickListener { dismiss() }

        binding.buttonCancel.setOnClickListener { dismiss() }
    }

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView = mDialog.findViewById<View>(R.id.design_bottom_sheet)
            var bottomSheetBehavior: BottomSheetBehavior<View>
            bottomSheetView?.let {
                val windowHeight = DimenUtils.getWindowVisibleHeight(activity as AppCompatActivity)
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView!!)
                bottomSheetBehavior.isDraggable = true
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.halfExpandedRatio = 0.6f
                val visibleHeight = windowHeight - bottomSheetView.top
                binding.linBottom.translationY =
                    (visibleHeight - binding.linBottom.height).toFloat()
                bottomSheetBehavior.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(
                            bottomSheet: View,
                            newState: Int
                        ) {
                            bottomSheet.requestLayout();
                            bottomSheet.invalidate();
                        }

                        override fun onSlide(
                            bottomSheet: View,
                            slideOffset: Float
                        ) {
                            if (slideOffset >= 0.0f) {
                                val visibleHeight1 = windowHeight - bottomSheetView.top
                                binding.linBottom.translationY =
                                    (visibleHeight1 - binding.linBottom.height).toFloat()
                            }
                            if (slideOffset == -1.0f) {
                                dismissAllowingStateLoss()
                            }
                        }
                    })

            }
        }
    }


    private fun initData() {
        val call = mEquiFaxApiInterface!!.getProductDetailsForCustomer(
            SharedPref.getInstance().getInt(ORG_ID), model.id, customerId!!, "Bearer " +
                    SharedPref.getInstance().getString(TOKEN)
        )

        call.enqueue(object : Callback<String?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.code() == 200) {
                    val jsonParser = JsonParser()
                    val jsonObj = jsonParser.parse(response.body()) as JsonObject
                    val gson = Gson()
                    if (isAdded) {
                        renderUi(gson.fromJson(jsonObj["data"], ProductDetailInfoModel::class.java))
                        setUpListener()
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                call.cancel()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun renderUi(model: ProductDetailInfoModel) {
        binding.txvProductName.text = "" + model.name

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.txtDesc.text =
                Html.fromHtml(model.description, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.txtDesc.text =
                Html.fromHtml(model.description)
        }

        binding.txtCategory.text = "" + model.category

        binding.txtMrpPrice.text = String.format(
            resources.getString(R.string.price_with_rupee_symbol), "" +
                    model.mrp_price
        )

        binding.tvMrpUnit.text = " per " + model.mrp_unit

        if (model.gst_exclusive) {
             binding.txtGstAmount.text = "${model.gst} % (Exclusive)"
        } else {
             binding.txtGstAmount.text = "${model.gst} % (Inclusive)"
        }

        binding.tvHsnCode.text = model.hsn_code

        binding.txtBuyersPrice.text = String.format(
            resources.getString(R.string.price_with_rupee_symbol),
            "" + model.price
        ) + " per " + model.unit

        if (model.specification.isNullOrEmpty().not()) {
            try {
                binding.recyclerView.setHasFixedSize(true)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                adapter = OrgProductSpecificationAdapter(
                    requireContext(), model.specification
                )
                binding.recyclerView.adapter = adapter
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            binding.hdSpecification.visibility = View.GONE
        }


        if (model.brand.isNullOrEmpty().not()) {
            binding.tvBrand.text = model.brand
            binding.groupBrand.visibility = View.VISIBLE
        } else {
            binding.groupBrand.visibility = View.GONE
        }

        binding.txvProductCode.text = model.code

        if (model.packaging_level.isNullOrEmpty().not()) {
            val stringBuilder = StringBuilder()
            for (i in model.packaging_level.indices) {
                stringBuilder.append(model.packaging_level[i].size).append(" \u0078 ")
                    .append(model.unit).append(" = ")
                    .append(model.packaging_level[i].unit)
                if (i < model.packaging_level.size - 1) {
                    stringBuilder.append("\n")
                }
            }
            binding.txtPackagingSize.text = stringBuilder.toString()
        } else {
            binding.txtPackagingSize.text = model.packaging_size.toString() + " \u0078 " + model.unit

            val packagingLevelModel = PackagingLevelModel()
            packagingLevelModel.unit = model.packaging_unit
            packagingLevelModel.size = model.packaging_size

            val array = ArrayList<PackagingLevelModel>()
            array.add(packagingLevelModel)
            model.packaging_level = array
        }

        try {
            if (model.pics_urls.size > 0) {
                mProductImageViewPagerAdapter = ProductImageViewPagerAdapter(
                    requireContext(), model.pics_urls, null
                )
                binding.viewPager.adapter = mProductImageViewPagerAdapter
                initViewPager()
                imageViewModelArrayList = ArrayList()
                for (url in model.pics_urls) {
                    val model = ImageViewModel(0, 0, url)
                    imageViewModelArrayList!!.add(model)
                }
            }
        } catch (Ex: Exception) {
            Logger.errorLogger("ProductAdapter", Ex.message)
        }

        if (model.is_out_of_stock != null && model.is_out_of_stock) {
            binding.tvOutOfStock.visibility = View.VISIBLE
            binding.buttonAddToCart.isEnabled = false
            binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.gray))
        } else {
            binding.buttonAddToCart.setOnClickListener {
                listener.onProductAddToCartFromBottomSheet(model, position)
                dismiss()
            }
        }
    }


    private fun initViewPager() {

        dotCount = mProductImageViewPagerAdapter!!.count
        dots = arrayOfNulls(dotCount)

        for (i in 0 until dotCount) {
            dots[i] = ImageView(requireContext())
            dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.pager_non_active_dot))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.SliderDots.addView(dots[i], params)
        }

        dots[0]!!.setImageDrawable(resources.getDrawable(R.drawable.pager_active_dot))
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotCount) {
                    dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.pager_non_active_dot))
                }
                dots[position]!!.setImageDrawable(resources.getDrawable(R.drawable.pager_active_dot))
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    interface IProductBottomSheetActionListener {
        fun onProductAddToCartFromBottomSheet(model: ProductDetailInfoModel, position: Int)
    }

}