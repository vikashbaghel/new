package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetVariantDetailsBinding
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.VariantDataInfoItem
import com.app.rupyz.model_kt.VariantOptionsItem
import com.app.rupyz.model_kt.VariantsItem
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VariantsBottomSheetDialogFragment : BottomSheetDialogFragment(),
    ProductVariantsOptionsAdapter.VariantOptionSelectedListener {

    private lateinit var binding: BottomSheetVariantDetailsBinding
    private val productViewModel: ProductViewModel by viewModels()

    private var productModel: ProductDetailInfoModel? = null

    private var selectedSetsHashMap = HashMap<String?, String?>()
    private var variantsDataList: ArrayList<VariantDataInfoItem> = ArrayList()

    private var variantsItemList: ArrayList<VariantsItem> = ArrayList()

    private lateinit var variantsAdapter: ProductVariantsAdapter

    private var cartListResponseModel: OrderData? = null
    private var cartAddedProductList = ArrayList<CartItem>()
    private var selectedProductId: Int? = null
    private lateinit var onGlobalObserver : ViewTreeObserver.OnGlobalLayoutListener

    companion object {
        private var listener: IProductBottomSheetActionListener? = null
        private var model: ProductList? = null
        private var customerId: Int? = null
        private var position: Int? = null

        @JvmStatic
        fun newInstance(
            listener: IProductBottomSheetActionListener,
            model: ProductList,
            customerId: Int?,
            position: Int
        ): VariantsBottomSheetDialogFragment {
            val fragment = VariantsBottomSheetDialogFragment()

            this.listener = listener
            this.model = model
            this.customerId = customerId
            this.position = position
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetVariantDetailsBinding.inflate(layoutInflater)
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

        // Get the 3/4 screen height
        val screenHeight = resources.displayMetrics.heightPixels
        val threeFourthScreenHeight = screenHeight * 3 / 5
        // Ensure the correct height is measured

        onGlobalObserver = ViewTreeObserver.OnGlobalLayoutListener {

            // Now you can get the correct measured height
            val clSuperParentHeight = binding.clSuperParent.measuredHeight

            // Determine the min height
            val minHeight = if (clSuperParentHeight < threeFourthScreenHeight) {
                clSuperParentHeight
            } else {
                threeFourthScreenHeight
            }

            // Set the minimum height constraint
            val constraintLayout = binding.clSuperParent
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)

            // Apply the minimum height constraint
            constraintSet.constrainMinHeight(binding.scrollView.id, minHeight)
            constraintSet.applyTo(constraintLayout)
        }

        binding.clSuperParent.viewTreeObserver.addOnGlobalLayoutListener (onGlobalObserver)

        setUpListener()
        initRecyclerView()
        initObservers()

        if (model?.id == null && model?.code != null) {
            binding.tvHeading.visibility = View.GONE
        }

        binding.progressBar.visibility = View.VISIBLE

        getProductDetail()

        binding.ivBack.setOnClickListener {
            listener?.onDismissDialog()
            dismiss()
        }

       disableAddToCartButton()

    }


    override fun onDetach() {
        super.onDetach()
        if (::onGlobalObserver.isInitialized){
            binding.clSuperParent.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalObserver)
        }
    }

    private fun getProductDetail() {
        CoroutineScope(Dispatchers.IO).launch {
            if (model?.id != null) {
                productViewModel.getProductDetails(
                    model?.id ?: 0,
                    customerId,
                    hasInternetConnection(requireContext())
                )
            } else if (model?.code != null) {
                productViewModel.getProductDetailsUsingCode(
                    model?.code!!,
                    customerId
                )
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
                    }

                    if (mData.variant_data.isNullOrEmpty().not()) {
                        variantsDataList.addAll(mData.variant_data)

                        if (mData.primary_product != null) {
                            selectedSetsHashMap = HashMap()
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
                }
            } else {
                if (arguments?.getBoolean(AppConstant.QR_CODE, false) == true) {
                    listener?.onDismissDialogWithMessage("${it.message}")
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
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

    private fun initRecyclerView() {
        binding.rvVariants.layoutManager = LinearLayoutManager(requireContext())
        variantsAdapter = ProductVariantsAdapter(variantsItemList, this)
        binding.rvVariants.adapter = variantsAdapter

    }

    private fun showProductDetails(mData: ProductDetailInfoModel) {
        productModel = mData
        selectedProductId = mData.id

        getPricingDetails(productModel?.id)

        binding.tvProductName.text = mData.name

        if (mData.variant_name.isNullOrEmpty().not()) {
            val productNameForVariant = mData.name.replace(mData.variant_name, "")
            binding.tvProductName.text = productNameForVariant.replaceFirstChar(Char::titlecase)
        }

        binding.tvProductMrp.text =
            CalculatorHelper().convertCommaSeparatedAmount(
                mData.mrp_price,
                AppConstant.FOUR_DECIMAL_POINTS
            )

        binding.tvMrpUnit.text = "per " + mData.mrp_unit

        if (mData.gst_exclusive != null) {
            if (mData.gst_exclusive) {
                binding.tvGst.text = "${mData.gst}% (Exclusive)"
            } else if (mData.gst != 0.0) {
                binding.tvGst.text = "${mData.gst}% (Inclusive)"
            } else {
                binding.tvGst.text = "0% (Inclusive)"
            }
        }

        binding.tvProductPrice.text =
            CalculatorHelper().convertCommaSeparatedAmount(
                mData.price,
                AppConstant.FOUR_DECIMAL_POINTS
            )

        binding.tvBuyersUnit.text = " per " + mData.unit

        if (mData.is_out_of_stock != null && mData.is_out_of_stock) {
            binding.tvOutOfStock.visibility = View.VISIBLE
        } else {
            binding.tvOutOfStock.visibility = View.GONE
        }


        try {
            if (mData.pics_urls.isNullOrEmpty().not()) {
                ImageUtils.loadImage(mData.pics_urls[0], binding.ivProduct)
            }
        } catch (ex: Exception) {
            log(ex.message.toString())
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

            disableAddToCartButton()
        } else if (productModel?.is_out_of_stock != null && productModel?.is_out_of_stock == true) {
            binding.tvOutOfStock.visibility = View.VISIBLE
            binding.tvOutOfStock.text = resources.getString(R.string.out_of_stock)
            binding.tvAddToCart.isEnabled = false
            binding.tvAddToCart.setBackgroundResource(R.drawable.check_score_button_style_disable)
            binding.tvAddToCart.setTextColor(resources.getColor(R.color.color_727176))

            disableAddToCartButton()
        } else {
            binding.tvOutOfStock.visibility = View.GONE
            binding.tvAddToCart.isEnabled = true
            binding.tvAddToCart.setBackgroundResource(R.drawable.add_to_cart_button_bg)
            binding.tvAddToCart.setTextColor(resources.getColor(R.color.theme_purple))

            enableAddToCartButton()
        }

        binding.tvAddToCart.setOnClickListener {
            productModel?.qty = 1.0

            binding.tvIncrementalQty.requestFocus()
            binding.tvIncrementalQty.setText("")
            binding.llQuantity.visibility = View.VISIBLE
            binding.tvAddToCart.visibility = View.GONE

            addToCart(productModel!!)
            enableAddToCartButton()
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

                    binding.tvProductPrice.text =
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
            listener?.onProductAddToCartFromBottomSheet(productModel!!, position!!)
            dismiss()
        }
    }

    private fun getPricingDetails(id: Int?) {
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

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView = mDialog.findViewById<View>(com.denzcoskun.imageslider.R.id.design_bottom_sheet)
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


    override fun onVariantOptionSelected(variantName: String?, mode: VariantOptionsItem) {
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
        variantsAdapter.notifyItemChanged(selectedIndex)

        manageSharedPref()
    }

    private fun enableAddToCartButton(){
        binding.buttonAddToCart.isEnabled = true
        binding.buttonAddToCart.setBackgroundColor(
            resources.getColor(
                R.color.filter_proceed_green,
                resources.newTheme()
            )
        )
        binding.buttonAddToCart.setTextColor(resources.getColor(R.color.white))
    }

    private fun disableAddToCartButton(){
        binding.buttonAddToCart.isEnabled = false
        binding.buttonAddToCart.setBackgroundColor(
            resources.getColor(
                R.color.button_disable_color,
                resources.newTheme()
            )
        )
        binding.buttonAddToCart.setTextColor(resources.getColor(R.color.color_727176))
    }
}