package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetProductDetailsBinding
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.model_kt.VariantDataInfoItem
import com.app.rupyz.model_kt.VariantOptionsItem
import com.app.rupyz.model_kt.VariantsItem
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.app.rupyz.ui.organization.adapter.OrgProductSpecificationAdapter
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsBottomSheetDialogFragment(
    var listener: IProductBottomSheetActionListener,
    var model: ProductList,
    var customerId: Int?,
    var position: Int
) : BottomSheetDialogFragment(), ProductVariantsOptionsAdapter.VariantOptionSelectedListener {
    private lateinit var binding: BottomSheetProductDetailsBinding

    private val productViewModel: ProductViewModel by viewModels()

    private var mProductImageViewPagerAdapter: ProductImageViewPagerAdapter? = null
    private lateinit var variantsAdapter: ProductVariantsAdapter
    private var adapter: OrgProductSpecificationAdapter? = null

    private var dotCount = 0
    private var dots: Array<ImageView?> = arrayOfNulls(dotCount)
    private var variantsItemList: ArrayList<VariantsItem> = ArrayList()

    private var productModel: ProductDetailInfoModel? = null

    private var selectedSetsHashMap = HashMap<String?, String?>()
    private var variantsDataList: ArrayList<VariantDataInfoItem> = ArrayList()

    private var cartListResponseModel: OrderData? = null
    private var cartAddedProductList = ArrayList<CartItem>()
    private var selectedProductId: Int? = null
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

        initRecyclerView()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            productViewModel.getProductDetails(
                model.id,
                customerId,
                Connectivity.hasInternetConnection(requireContext())
            )
        }

        binding.ivBack.setOnClickListener { dismiss() }

    }

    private fun initRecyclerView() {
        binding.rvVariants.layoutManager = LinearLayoutManager(requireContext())
        variantsAdapter = ProductVariantsAdapter(variantsItemList, this)
        binding.rvVariants.adapter = variantsAdapter
    }

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView = mDialog.findViewById<View>(R.id.design_bottom_sheet)
            var bottomSheetBehavior: BottomSheetBehavior<View>

            if (bottomSheetView != null) {
                BottomSheetBehavior.from(bottomSheetView).state =
                    BottomSheetBehavior.STATE_EXPANDED;
            }

            bottomSheetView?.let {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
                bottomSheetBehavior.isDraggable = false
                bottomSheetBehavior.isHideable = false

                // Ensure focus is not on an EditText to prevent the keyboard from opening
                it.clearFocus()

                // Hide the keyboard
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val currentFocus = requireActivity().currentFocus
                currentFocus?.let { view ->
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }

            }
        }
    }


    private fun initObservers() {
        productViewModel.productDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { mData ->
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
                                                selectedSetsHashMap[variant.name] = option.optionId
                                            }
                                        }
                                    }
                                }
                            }

                            variantsAdapter.notifyDataSetChanged()
                        }
                    }


                    binding.mainContent.visibility = View.VISIBLE

                    setUpListener()
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.productTelescopicPricingLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { details ->
                    if (details.telescope_pricing.isNullOrEmpty().not()) {
                        productModel?.telescope_pricing = details.telescope_pricing
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showProductDetails(model: ProductDetailInfoModel) {
        productModel = model
        selectedProductId = model.id

        getPricingDetail(productModel?.id)

        binding.txvProductName.text = "" + model.name

        if (model.variant_name.isNullOrEmpty().not()) {
            val productNameForVariant = model.name.replace(model.variant_name, "")
            binding.txvProductName.text = productNameForVariant.replaceFirstChar(Char::titlecase)
        }

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
                adapter = OrgProductSpecificationAdapter(model.specification)
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
            binding.txtPackagingSize.text =
                model.packaging_size.toString() + " \u0078 " + model.unit

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
            }
        } catch (ex: Exception) {
            Logger.errorLogger("ProductAdapter", ex.message)
        }

        if (productModel?.packaging_level.isNullOrEmpty().not()) {
            if (productModel?.packaging_level!!.size == 1) {
                binding.tvProductUnit.visibility = View.VISIBLE
                binding.spinnerPackagingLevel.visibility = View.GONE

                binding.tvProductUnit.text =
                    productModel?.packaging_level!![0].unit?.replaceFirstChar(Char::titlecase)

                productModel?.selectedPackagingLevel = productModel?.packaging_level!![0]

            } else if (productModel?.packaging_level!!.size > 1) {
                binding.tvProductUnit.visibility = View.GONE
                binding.spinnerPackagingLevel.visibility = View.VISIBLE

                val list = ArrayList<String>()
                productModel?.packaging_level!!.forEach {
                    list.add(it.unit!!)
                }

                binding.spinnerPackagingLevel.adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.single_text_view_spinner_12dp_text, list
                )

                binding.spinnerPackagingLevel.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long
                        ) {
                            val selectedPackagingLevel =
                                productModel?.packaging_level!![spinnerPosition]

                            if (productModel?.selectedPackagingLevel != selectedPackagingLevel) {
                                productModel?.selectedPackagingLevel = selectedPackagingLevel


                                cartAddedProductList.forEach { cartItem ->
                                    if (cartItem.id == model?.id) {
                                        cartItem.selectedPackagingLevel = selectedPackagingLevel
                                        cartItem.packagingSize = selectedPackagingLevel.size
                                        cartItem.packagingUnit = selectedPackagingLevel.unit
                                        return@forEach
                                    }
                                }

                                cartListResponseModel?.items = cartAddedProductList

                                SharedPref.getInstance().putModelClass(
                                    SharePrefConstant.CART_MODEL,
                                    cartListResponseModel
                                )

                                calculatePackagingSize()
                            }
                        }
                    }

                if (productModel?.selectedPackagingLevel != null) {
                    val selectedLevelPosition =
                        productModel?.packaging_level!!.indexOfFirst { level ->
                            level == productModel?.selectedPackagingLevel
                        }

                    binding.spinnerPackagingLevel.setSelection(selectedLevelPosition)
                }
            }
        }

        calculatePackagingSize()

        manageSharedPref()

        if (productModel?.is_published != null && productModel?.is_published == false) {
            binding.tvOutOfStock.visibility = View.VISIBLE
            binding.tvOutOfStock.text = resources.getString(R.string.inactive_product)
            binding.tvAddToCart.isEnabled = false
            binding.tvAddToCart.setBackgroundResource(R.drawable.check_score_button_style_disable)
            binding.tvAddToCart.setTextColor(resources.getColor(R.color.color_727176))
        } else if (productModel?.is_out_of_stock != null && productModel?.is_out_of_stock == true) {
            binding.tvOutOfStock.visibility = View.VISIBLE
            binding.tvOutOfStock.text = resources.getString(R.string.out_of_stock)
            binding.tvAddToCart.isEnabled = false
            binding.tvAddToCart.setBackgroundResource(R.drawable.check_score_button_style_disable)
            binding.tvAddToCart.setTextColor(resources.getColor(R.color.color_727176))
        } else {
            binding.tvOutOfStock.visibility = View.GONE
            binding.tvAddToCart.isEnabled = true
            binding.tvAddToCart.setBackgroundResource(R.drawable.add_to_cart_button_bg)
            binding.tvAddToCart.setTextColor(resources.getColor(R.color.theme_purple))
        }

        binding.tvAddToCart.setOnClickListener {
            productModel?.qty = 1.0

            binding.tvIncrementalQty.setText(CalculatorHelper().calculateQuantity(productModel?.qty))
            binding.llQuantity.visibility = View.VISIBLE
            binding.tvAddToCart.visibility = View.GONE

            addToCart(productModel!!)
        }

        binding.tvIncrementalQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (input.toString().isNotEmpty() && input.toString() != ".") {
                    productModel?.qty = input.toString().toDouble()

                    val priceAfterTelescopicPrice =
                        findTelescopicPrice(
                            input.toString().toDouble(),
                            productModel?.telescope_pricing
                        )

                    binding.txtBuyersPrice.text =
                        CalculatorHelper().convertCommaSeparatedAmount(
                            priceAfterTelescopicPrice,
                            AppConstant.FOUR_DECIMAL_POINTS
                        )

                    onChangeQuantity(
                        productModel,
                        input.toString().toDouble()
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.buttonAddToCart.setOnClickListener {
            listener.onProductAddToCartFromBottomSheet(productModel!!, position)
            dismiss()
        }
    }

    private fun getPricingDetail(id: Int?) {
        binding.progressBar.visibility = View.VISIBLE
        productViewModel.getProductTelescopicPricing(
            id ?: 0,
            customerId
        )
    }

    private fun onChangeQuantity(model: ProductDetailInfoModel?, qty: Double) {
        cartAddedProductList.forEach { cartItem ->
            if (cartItem.id == model?.id) {
                cartItem.qty = qty
                return@forEach
            }
        }

        cartListResponseModel?.items = cartAddedProductList

        SharedPref.getInstance().putModelClass(SharePrefConstant.CART_MODEL, cartListResponseModel)
    }

    private fun findTelescopicPrice(
        qty: Double,
        telescopicPriceList: List<TelescopicPricingModel>?
    ): Double {
        var telescopicPriceModel: TelescopicPricingModel? = null
        return if (!telescopicPriceList.isNullOrEmpty()) {
            telescopicPriceModel = telescopicPriceList.findLast { qty >= it.qty as Double }
            if (telescopicPriceModel != null) {
                telescopicPriceModel.price!!
            } else {
                productModel?.price ?: 1.0
            }
        } else {
            productModel?.price ?: 1.0
        }
    }

    private fun addToCart(model: ProductDetailInfoModel) {
        val cartItem = CartItem()
        cartItem.id = model.id
        cartItem.category = model.category
        cartItem.name = model.name
        cartItem.priceAfterDiscount = model.price
        cartItem.qty = 1.0
        cartItem.unit = model.unit
        cartItem.code = model.code
        cartItem.isAddedToCart = true
        cartItem.price = model.price
        cartItem.gst_exclusive = model.gst_exclusive
        cartItem.gst = model.gst
        cartItem.telescopePricing = model.telescope_pricing
        cartItem.packagingLevel = model.packaging_level
        cartItem.selectedPackagingLevel = model.selectedPackagingLevel
        cartItem.updateOrder = true
        cartItem.variantName = model.variant_name
        cartItem.primaryProduct = model.primary_product

        if (model.selectedPackagingLevel != null) {
            cartItem.packagingSize = model.selectedPackagingLevel.size
            cartItem.packagingUnit = model.selectedPackagingLevel.unit
        } else {
            cartItem.packagingSize = model.packaging_size
            cartItem.packagingUnit = model.packaging_unit
        }

        cartAddedProductList.add(cartItem)

        val variantSize =
            cartAddedProductList.filter { it.primaryProduct == model.primary_product }.size

        cartAddedProductList.forEach { item ->
            if (item.primaryProduct != null && item.primaryProduct == model.primary_product) {
                item.variantSize = variantSize
            }
        }

        cartListResponseModel?.items = cartAddedProductList

        SharedPref.getInstance().putModelClass(SharePrefConstant.CART_MODEL, cartListResponseModel)
    }

    private fun manageSharedPref() {
        if (SharedPref.getInstance().getString(SharePrefConstant.CART_MODEL) != null) {
            val response = SharedPref.getInstance().getString(SharePrefConstant.CART_MODEL)

            if (response.isNotEmpty()) {
                cartListResponseModel = Gson().fromJson(response, OrderData::class.java)
            }

            if (cartListResponseModel?.customerId != null
                && cartListResponseModel?.customerId == customerId
            ) {
                if (cartListResponseModel?.items.isNullOrEmpty().not()) {
                    cartAddedProductList = ArrayList()

                    cartAddedProductList.addAll(cartListResponseModel?.items!!)
                }
            }
        }

        var isProductExistInCart = false
        CoroutineScope(Dispatchers.IO).launch {
            cartAddedProductList.forEach { cartItem ->
                run {
                    if (cartItem.id == selectedProductId) {
                        productModel?.qty = cartItem.qty
                        productModel?.selectedPackagingLevel =
                            cartItem.selectedPackagingLevel

                        isProductExistInCart = true
                        return@forEach
                    }
                }
            }

            requireActivity().runOnUiThread {
                if (isProductExistInCart) {
                    binding.llQuantity.visibility = View.VISIBLE
                    binding.tvAddToCart.visibility = View.GONE
                    binding.tvIncrementalQty.setText(
                        CalculatorHelper().calculateQuantity(
                            productModel?.qty
                        )
                    )
                } else {
                    binding.tvAddToCart.visibility = View.VISIBLE
                    binding.llQuantity.visibility = View.GONE
                }
            }
        }
    }

    private fun calculatePackagingSize() {
        val qty = if (productModel?.selectedPackagingLevel != null) {
            (productModel?.qty ?: 1.0) * productModel?.selectedPackagingLevel?.size!!
        } else if (productModel?.packaging_level.isNullOrEmpty().not()) {
            (productModel?.qty ?: 1.0) * productModel?.packaging_level!![0].size!!
        } else {
            (productModel?.qty ?: 1.0)
        }

        binding.tvOrderQty.text = requireContext().getString(
            R.string.product_quantity_without_pre_text_string,
            CalculatorHelper().calculateQuantity(qty),
            productModel?.unit?.replaceFirstChar(Char::titlecase)
        )
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
                dots.forEach { image ->
                    image?.setImageResource(
                        R.drawable.pager_non_active_dot
                    )
                }

                if (position < dots.size) {
                    dots[position]?.setImageResource(
                        R.drawable.pager_active_dot
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onVariantOptionSelected(variantName: String?, mode: VariantOptionsItem) {
        binding.SliderDots.removeAllViews()
        dotCount = 0

        selectedSetsHashMap[variantName] = mode.optionId

        val idList = mutableListOf<String?>()
        selectedSetsHashMap.forEach { entry ->
            idList.add(entry.value)
        }

        log(idList.sortBy { it }.toString())

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