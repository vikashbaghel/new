package com.app.rupyz.sales.orders

import android.annotation.SuppressLint
import android.app.Service
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ProductItemBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel

class CrateOrderForCustomerAdapter(
    private var data: ArrayList<ProductList>, private var listener: OrderActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).enableTextWatcher()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).disableTextWatcher()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductItemBinding.bind(itemView)
        var textWatcher: TextWatcher? = null

        @SuppressLint("SetTextI18n")
        fun bindItem(model: ProductList, position: Int, listener: OrderActionListener) {

            binding.tvIncrementalQty.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                    7,
                    AppConstant.MAX_DIGIT_AFTER_DECIMAL,
                )
            )

            binding.ivMenu.visibility = View.GONE
            binding.tvProductName.text = model.name
            binding.tvProductPrice.text =
                CalculatorHelper().convertCommaSeparatedAmount(model.price.toDouble(), AppConstant.FOUR_DECIMAL_POINTS) + " /"
            binding.tvProductCode.text = model.code
            binding.tvProductMrp.text =
                "MRP:  " + CalculatorHelper().convertCommaSeparatedAmount(model.mrp_price.toDouble(), AppConstant.FOUR_DECIMAL_POINTS) + " /"

            var packagingLevel: PackagingLevelModel? = null

            if (model.packaging_level.isNullOrEmpty().not()) {
                packagingLevel = model.packaging_level[0]
                if (model.selectedPackagingLevel == null) {
                    model.selectedPackagingLevel = packagingLevel
                }

                if (model.packaging_level.size == 1) {
                    binding.tvOrderQty.visibility = View.VISIBLE
                    binding.tvPackagingLevel.visibility = View.GONE
                    binding.tvOrderQty.text =
                        itemView.context.getString(
                            R.string.product_quantity_string,
                            CalculatorHelper().calculateQuantity(model.packaging_level[0].size),
                            model.unit,
                            model.packaging_level[0].unit?.replaceFirstChar(Char::titlecase)
                        )

                    binding.tvProductUnit.text =
                        model.packaging_level[0].unit?.replaceFirstChar(Char::titlecase)
                    binding.tvProductUnit.visibility = View.VISIBLE
                    binding.tvPackagingLevel.visibility = View.GONE

                } else if (model.packaging_level.size > 1) {
                    binding.tvPackagingLevel.visibility = View.VISIBLE
                    binding.tvPackagingLevel.text = "Pkg Level (${model.packaging_level.size})"
                    binding.tvOrderQty.visibility = View.GONE
                    binding.tvPackagingLevel.setOnClickListener {
                        listener.getPackagingLevelInfo(model)
                    }

                    val list = ArrayList<String>()
                    model.packaging_level.forEach {
                        list.add(it.unit!!)
                    }

                    binding.spinnerPackagingLevel.adapter = ArrayAdapter(
                        itemView.context,
                        R.layout.single_text_view_spinner_12dp_text, list
                    )

                    binding.spinnerPackagingLevel.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long
                            ) {
                                val selectedPackagingLevel = model.packaging_level[spinnerPosition]
                                listener.changePackagingLevel(
                                    model,
                                    position,
                                    selectedPackagingLevel
                                )
                            }
                        }
                } else {
                    binding.tvOrderQty.visibility = View.GONE
                }
            }

            binding.tvGst.text =
                CalculatorHelper().calculateGst(model.gst, model.gst_exclusive)
                    .replace(":", "")

            if (model.mrp_unit != null) {
                binding.tvMrpUnit.text =
                    model.mrp_unit.toString().replaceFirstChar(Char::titlecase)
            }

            if (model.unit != null) {
                binding.tvBuyersUnit.text =
                    model.unit.toString().replaceFirstChar(Char::titlecase)
            }

            if (model.telescopePricing.isNullOrEmpty()) {
                binding.clPriceSlab.visibility = View.GONE
            } else {
                binding.clPriceSlab.visibility = View.VISIBLE
            }

            binding.clPriceSlab.setOnClickListener {
                listener.getPriceSlabInfo(model)
            }

            textWatcher = MyTextWatcher(listener, model, binding.tvProductPrice)

            if (model.outOfStock != null && model.outOfStock) {
                binding.tvAddToCart.setOnClickListener(null)
                binding.tvAddToCart.visibility = View.GONE
                binding.llAddToCart.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.VISIBLE
            } else if (model.addedToCart) {
                binding.tvOutOfStock.visibility = View.GONE
                binding.tvAddToCart.setOnClickListener(null)
                binding.llAddToCart.visibility = View.VISIBLE
                binding.tvAddToCart.visibility = View.GONE

                binding.tvIncrementalQty.setText(CalculatorHelper().calculateQuantity(model.qty))

                if (model.isEnableUpdateQuantity) {
                    if (model.packaging_level.isNullOrEmpty().not()) {
                        if (model.packaging_level.size == 1) {
                            binding.tvProductUnit.visibility = View.VISIBLE
                            binding.spinnerPackagingLevel.visibility = View.GONE
                        } else if (model.packaging_level.size > 1) {
                            binding.tvProductUnit.visibility = View.GONE
                            binding.spinnerPackagingLevel.visibility = View.VISIBLE
                        }
                    }

                    if (model.selectedPackagingLevel != null) {
                        val selectedLevelPosition =
                            model.packaging_level!!.indexOfFirst { level -> level == model.selectedPackagingLevel }

                        binding.spinnerPackagingLevel.setSelection(selectedLevelPosition)
                    }
                } else {
                    if (model.selectedPackagingLevel != null) {
                        binding.tvProductUnit.visibility = View.VISIBLE
                        binding.spinnerPackagingLevel.visibility = View.GONE

                        binding.tvProductUnit.text = model.selectedPackagingLevel.unit
                    }
                }
            } else {
                binding.tvOutOfStock.visibility = View.GONE
                binding.llAddToCart.visibility = View.GONE
                binding.tvProductUnit.visibility = View.GONE
                binding.tvAddToCart.visibility = View.VISIBLE

                binding.tvAddToCart.setOnClickListener {
                    listener.onAddToCart(model, position)

                    model.addedToCart = true

                    binding.tvIncrementalQty.setText("")
                    binding.tvIncrementalQty.setSelection(0)
                    binding.tvIncrementalQty.isFocusable = true
                    binding.tvIncrementalQty.requestFocus()
                    binding.tvIncrementalQty.showSoftInputOnFocus = true

                    val imm: InputMethodManager =
                        itemView.context.getSystemService(Service.INPUT_METHOD_SERVICE)
                                as InputMethodManager

                    imm.showSoftInput(
                        binding.tvIncrementalQty,
                        InputMethodManager.RESULT_SHOWN
                    )

                    binding.tvAddToCart.visibility = View.GONE
                    binding.llAddToCart.visibility = View.VISIBLE

                    if (model.packaging_level.isNullOrEmpty().not()) {
                        if (model.packaging_level.size == 1) {
                            binding.tvProductUnit.visibility = View.VISIBLE
                            binding.spinnerPackagingLevel.visibility = View.GONE
                        } else if (model.packaging_level.size > 1) {
                            binding.tvProductUnit.visibility = View.GONE
                            binding.spinnerPackagingLevel.visibility = View.VISIBLE
                        }
                    }

                }
            }

            binding.tvIncrementalQty.setOnEditorActionListener(
                TextView.OnEditorActionListener { _, actionId, _ ->

                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        // Do something of your interest.
                        // We in this examples created the following Toasts
                        listener.onNextButtonClick()
                        return@OnEditorActionListener true
                    }
                    false
                })

            try {
                if (model.displayPicUrl != null) {
                    ImageUtils.loadImage(model.displayPicUrl, binding.ivProduct)
                } else if (model.pics_urls != null && model.pics_urls.size > 0) {
                    ImageUtils.loadImage(model.pics_urls[0], binding.ivProduct)
                } else {
                    binding.ivProduct.setImageResource(R.drawable.business_banner_background)
                }
            } catch (Ex: Exception) {
                Logger.errorLogger("ProductAdapter", Ex.message)
            }

            binding.mainContent.setOnClickListener {
                binding.tvIncrementalQty.removeTextChangedListener(textWatcher)
                listener.onGetProductInfo(model, position)
            }
        }

        fun enableTextWatcher() {
            binding.tvIncrementalQty.addTextChangedListener(textWatcher)
            binding.tvIncrementalQty.tag = adapterPosition
        }

        fun disableTextWatcher() {
            binding.tvIncrementalQty.removeTextChangedListener(textWatcher)
        }
    }

    class MyTextWatcher(
        private val listener: OrderActionListener,
        private val model: ProductList,
        private val tvProductPrice: TextView
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            if (input.toString().isNotEmpty() && input.toString() != ".") {
                model.qty = input.toString().toDouble()

                val priceAfterTelescopicPrice =
                    findTelescopicPrice(input.toString().toDouble(), model.telescopePricing)

                tvProductPrice.text =
                    CalculatorHelper().convertCommaSeparatedAmount(priceAfterTelescopicPrice, AppConstant.FOUR_DECIMAL_POINTS) + " /"
                listener.onChangeQuantity(
                    model,
                    input.toString().toDouble()
                )
            }
        }

        override fun afterTextChanged(s: Editable) {}

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
                    model.price
                }
            } else {
                model.price
            }
        }
    }

}