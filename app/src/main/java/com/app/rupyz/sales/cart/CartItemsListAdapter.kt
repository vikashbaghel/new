package com.app.rupyz.sales.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCartListItemBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.helper.DigitsInputFilterForCart
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import kotlin.math.roundToInt


class CartItemsListAdapter(
    private var data: ArrayList<CartItem>, private var listener: OnCartActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCartListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: CartItem, position: Int, listener: OnCartActionListener) {
            binding.tvOrderName.text = model.name

            if (model.variantName.isNullOrEmpty().not()) {
                val variantName = model.variantName
                binding.tvVariantName.text = variantName?.replaceFirstChar(Char::titlecase)
                val productNameAfterVariant = model.name?.replace(variantName ?: "", "")
                binding.tvOrderName.text = productNameAfterVariant
                binding.groupVariant.visibility = View.VISIBLE
            } else {
                binding.groupVariant.visibility = View.GONE
            }

            if (model.telescopePricing.isNullOrEmpty().not()) {
                val price = findTelescopicPrice(model.qty, model.telescopePricing, model)
                model.price = price
                model.priceAfterDiscount = price
                binding.tvOrderPrice.text =
                    CalculatorHelper().convertLargeAmount(price, AppConstant.FOUR_DECIMAL_POINTS)
            }

            if (model.gst != null) {
                binding.hdGstHeading.text =
                    CalculatorHelper().calculateGst(model.gst!!, model.gst_exclusive!!)
            }

            if (model.packagingLevel.isNullOrEmpty().not()) {
                if (model.updateOrder.not()) {
                    binding.tvProductUnit.visibility = View.VISIBLE
                    binding.spinnerPackagingLevel.visibility = View.GONE

                    binding.tvProductUnit.text =
                        model.selectedPackagingLevel?.unit?.replaceFirstChar(Char::titlecase)

                    val qty =
                        model.selectedPackagingLevel?.size!! * model.qty!!

                    binding.tvOrderQty.text = itemView.context.getString(
                        R.string.product_quantity_without_pre_text_string,
                        CalculatorHelper().calculateQuantity(qty),
                        model.unit?.replaceFirstChar(Char::titlecase)
                    )

                } else if (model.packagingLevel!!.size == 1) {
                    binding.tvProductUnit.visibility = View.VISIBLE
                    binding.spinnerPackagingLevel.visibility = View.GONE

                    binding.tvProductUnit.text =
                        model.packagingLevel!![0].unit?.replaceFirstChar(Char::titlecase)

                    val qty =
                        model.packagingLevel!![0].size!! * model.qty!!

                    binding.tvOrderQty.text = itemView.context.getString(
                        R.string.product_quantity_without_pre_text_string,
                        CalculatorHelper().calculateQuantity(qty),
                        model.unit?.replaceFirstChar(Char::titlecase)
                    )

                    model.selectedPackagingLevel = model.packagingLevel!![0]

                } else if (model.packagingLevel!!.size > 1) {
                    binding.tvProductUnit.visibility = View.GONE
                    binding.spinnerPackagingLevel.visibility = View.VISIBLE

                    val list = ArrayList<String>()
                    model.packagingLevel!!.forEach {
                        list.add(it.unit!!)
                    }

                    binding.spinnerPackagingLevel.adapter = ArrayAdapter(
                        itemView.context,
                        R.layout.single_text_view_spinner_12dp_text, list
                    )

                    if (model.selectedPackagingLevel != null) {
                        val selectedLevelPosition =
                            model.packagingLevel!!.indexOfFirst { level ->
                                level == model.selectedPackagingLevel
                            }

                        binding.spinnerPackagingLevel.setSelection(selectedLevelPosition)

                        val qty = (model.selectedPackagingLevel?.size ?: 1.0) * model.qty!!

                        binding.tvOrderQty.text = itemView.context.getString(
                            R.string.product_quantity_without_pre_text_string,
                            CalculatorHelper().calculateQuantity(qty),
                            model.unit?.replaceFirstChar(Char::titlecase)
                        )
                    }

                    binding.spinnerPackagingLevel.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long
                            ) {
                                val selectedPackagingLevel = model.packagingLevel!![spinnerPosition]
                                if (model.selectedPackagingLevel != selectedPackagingLevel) {
                                    model.selectedPackagingLevel = selectedPackagingLevel


                                    val qty =
                                        (model.selectedPackagingLevel?.size
                                            ?: 1.0) * model.qty!!

                                    binding.tvOrderQty.text = itemView.context.getString(
                                        R.string.product_quantity_without_pre_text_string,
                                        CalculatorHelper().calculateQuantity(qty),
                                        model.unit?.replaceFirstChar(Char::titlecase)
                                    )

                                    binding.tvOrderTotalPrice.text =
                                        CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                                            model,
                                            model.priceAfterDiscount?:0.0,
                                            AppConstant.FOUR_DECIMAL_POINTS
                                        )

                                    listener.onPackagingLevelChange(model, position)
                                }
                            }
                        }
                }
            }

            if (model.discount_details != null && model.discount_details?.type != null) {
                binding.groupDiscountedPrice.visibility = View.VISIBLE
                binding.clCartItemDiscountChoice.visibility = View.GONE

                binding.clCartItemDiscountApplied.visibility = View.VISIBLE

                binding.clCartItemDiscount.visibility = View.INVISIBLE

                model.discount_details?.let {
                    if (it.type?.lowercase() == AppConstant.DISCOUNT_TYPE_OFFER_PRICE.lowercase()) {
                        binding.tvAppliedDiscount.text =
                            itemView.context.getString(
                                R.string.offer_price_applied_with_rupee_symbol,
                                "" + CalculatorHelper().formatDoubleDecimalPoint(
                                    model.priceAfterDiscount!!,
                                    AppConstant.TWO_DECIMAL_POINTS
                                )
                            )

                    } else if (it.type?.lowercase() == AppConstant.DISCOUNT_TYPE_RUPEES.lowercase()
                        || it.type?.lowercase() == "rupees"
                    ) {
                        binding.tvAppliedDiscount.text =
                            itemView.context.getString(
                                R.string.discount_applied_with_rupee_symbol,
                                CalculatorHelper().formatDoubleDecimalPoint(
                                    model.discount_details?.value!!,
                                    AppConstant.TWO_DECIMAL_POINTS
                                )
                            )
                    } else {
                        binding.tvAppliedDiscount.text =
                            itemView.context.getString(
                                R.string.discount_applied_with_percentage_symbol,
                                "" + model.discount_details?.value!! + " %"
                            )
                    }
                }

                binding.tvOrderTotalPrice.text =
                    CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                        model,
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.tvOrderPrice.text =
                    CalculatorHelper().convertLargeAmount(
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.tvGstInfo.text =
                    CalculatorHelper().calculateGstAmountForSingleUnit(
                        model.priceAfterDiscount!!,
                        model.gst!!,
                        model.gst_exclusive!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

            } else {
                model.priceAfterDiscount = model.price
                binding.groupDiscountedPrice.visibility = View.GONE
                binding.clCartItemDiscountApplied.visibility = View.GONE
                binding.clCartItemDiscount.visibility = View.VISIBLE
                binding.tvOrderPrice.text = CalculatorHelper().convertLargeAmount(
                    model.price!!,
                    AppConstant.FOUR_DECIMAL_POINTS
                )

                binding.tvOrderTotalPrice.text =
                    CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                        model,
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.tvGstInfo.text =
                    CalculatorHelper().calculateGstAmountForSingleUnit(
                        model.price!!,
                        model.gst!!,
                        model.gst_exclusive!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )
            }

            binding.tvOrderNanoId.text = model.code

            if (model.isOutOfStock == true) {
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.tvIncrementalQty.filters =
                    arrayOf<InputFilter>(
                        DigitsInputFilterForCart(
                            itemView.context,
                            model.qty ?: 1000000.00,
                            AppConstant.MAX_DIGIT_AFTER_DECIMAL
                        )
                    )
            } else {
                binding.tvOutOfStock.visibility = View.GONE
                binding.tvIncrementalQty.filters = arrayOf<InputFilter>(
                    DigitsInputFilter(
                        7,
                        AppConstant.MAX_DIGIT_AFTER_DECIMAL
                    )
                )
            }

            binding.tvOrderDiscountedPrice.text =
                CalculatorHelper().convertLargeAmount(
                    model.price!!,
                    AppConstant.FOUR_DECIMAL_POINTS
                )

            binding.tvIncrementalQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(input: Editable?) {

                    if (input.toString().isNotEmpty() && input.toString() != ".") {

                        val qty = if (model.selectedPackagingLevel != null) {
                            input.toString().toDouble() * model.selectedPackagingLevel?.size!!
                        } else if (model.packagingLevel.isNullOrEmpty().not()) {
                            input.toString().toDouble() * model.packagingLevel!![0].size!!
                        } else {
                            input.toString().toDouble()
                        }

                        val priceAfterTelescopicPrice =
                            findTelescopicPrice(
                                input.toString().toDouble(),
                                model.telescopePricing,
                                model
                            )

                        model.price = priceAfterTelescopicPrice

                        if (model.discount_details != null && model.discount_details?.value != null) {
                            if (model.discount_details?.type == AppConstant.DISCOUNT_TYPE_OFFER_PRICE) {
                                model.priceAfterDiscount = model.discount_details?.value
                            } else if (model.discount_details?.type == AppConstant.DISCOUNT_TYPE_PERCENT) {
                                if (model.discount_details?.calculated_value != null) {
                                    model.priceAfterDiscount =
                                        model.price!! - model.discount_details!!.calculated_value!!
                                } else {
                                    val discountAmount =
                                        (model.price!! * model.discount_details?.value!!) / 100
                                    model.priceAfterDiscount =
                                        model.price!! - discountAmount
                                }
                            } else {
                                model.priceAfterDiscount =
                                    model.price!! - model.discount_details!!.value!!
                            }
                        } else {
                            model.priceAfterDiscount = priceAfterTelescopicPrice
                        }

                        model.qty = input.toString().toDouble()

                        binding.tvOrderPrice.text =
                            CalculatorHelper().convertLargeAmount(
                                model.priceAfterDiscount!!,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )

                        binding.tvOrderTotalPrice.text =
                            CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                                model,
                                model.priceAfterDiscount!!,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )

                        listener.onQuantityChange(model, input.toString(), position)

                        binding.tvGstInfo.text =
                            CalculatorHelper().calculateGstAmountForSingleUnit(
                                model.priceAfterDiscount!!,
                                model.gst!!,
                                model.gst_exclusive!!,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )

                        binding.tvOrderQty.text = itemView.context.getString(
                            R.string.product_quantity_without_pre_text_string,
                            CalculatorHelper().calculateQuantity(qty),
                            model.unit?.replaceFirstChar(Char::titlecase)
                        )
                    } else {
                        model.qty = 0.0
                        binding.tvOrderQty.text = itemView.context.getString(
                            R.string.product_quantity_without_pre_text_string, "0", model.unit
                        )
                        binding.tvOrderTotalPrice.text = "0.0"

                        listener.onQuantityChange(model, "0", position)

                        binding.tvGstInfo.text = "0"

                    }

                }
            })

            binding.tvIncrementalQty.setText(CalculatorHelper().calculateQuantity(model.qty))

            binding.clCartItemDiscount.setOnClickListener {
                binding.clCartItemDiscountChoice.visibility = View.VISIBLE
            }

            var discountChoice = AppConstant.ADD_DISCOUNT

            binding.rgDiscountType.setOnCheckedChangeListener { rg, _ ->
                val selectedId: Int = rg?.checkedRadioButtonId!!

                if (selectedId == R.id.rb_discount) {
                    discountChoice = AppConstant.ADD_DISCOUNT
                    binding.groupChooseDiscount.visibility = View.VISIBLE
                    binding.etOffersPrice.visibility = View.GONE
                } else {
                    discountChoice = AppConstant.ADD_OFFER
                    binding.groupChooseDiscount.visibility = View.GONE
                    binding.etOffersPrice.visibility = View.VISIBLE
                }
            }

            binding.spinnerDiscountUnit.adapter = ArrayAdapter(
                itemView.context,
                R.layout.single_text_view_spinner_12dp_text,
                itemView.context.resources.getStringArray(R.array.discount_type_symbol)
            )

            binding.etOffersPrice.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                    10,
                    AppConstant.FOUR_DECIMAL_POINTS
                )
            )

            binding.etDiscountValue.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                    2,
                    AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
            )

            var discountType = AppConstant.DISCOUNT_TYPE_PERCENT

            binding.spinnerDiscountUnit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        if (position == 0) {
                            discountType = AppConstant.DISCOUNT_TYPE_PERCENT

                            binding.etDiscountValue.filters = arrayOf<InputFilter>(
                                DigitsInputFilter(
                                    2,
                                    AppConstant.MAX_DIGIT_AFTER_DECIMAL
                                )
                            )

                        } else {
                            discountType = AppConstant.DISCOUNT_TYPE_RUPEES

                            binding.etDiscountValue.filters = arrayOf<InputFilter>(
                                DigitsInputFilter(
                                    10,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )
                            )
                        }

                        binding.tvDiscountUnit.text =
                            binding.spinnerDiscountUnit.selectedItem.toString()
                    }
                }

            binding.tvOrderCategory.text = model.category
            binding.imageView2.setOnClickListener { listener.onRemoveItem(model, position) }

            binding.etDiscountValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(t: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (t.toString().isNotEmpty()) {
                        binding.tvDiscountUnit.visibility = View.VISIBLE
                    } else {
                        binding.tvDiscountUnit.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            binding.tvApplyDiscount.setOnClickListener {
                if (discountChoice == AppConstant.ADD_DISCOUNT) {
                    if (binding.etDiscountValue.text.toString() != "") {

                        if (discountType == AppConstant.DISCOUNT_TYPE_PERCENT &&
                            binding.etDiscountValue.text.toString()
                                .toDouble() > 100.0
                        ) {
                            Toast.makeText(
                                itemView.context,
                                itemView.context.getString(R.string.discount_percentage_not_be_greater_then_hundred),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (discountType == AppConstant.DISCOUNT_TYPE_RUPEES
                            && binding.etDiscountValue.text.toString()
                                .toDouble() > model.price?.roundToInt()!!
                        ) {
                            Toast.makeText(
                                itemView.context,
                                itemView.context.getString(R.string.discount_value_not_be_greater_then_product_price),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            model.isDiscountOnParticularItem = true

                            val discountDetails = CartItemDiscountModel()
                            var discount = 0.0
                            discount = if (discountType == AppConstant.DISCOUNT_TYPE_PERCENT) {
                                (model.price!! * binding.etDiscountValue.text.toString()
                                    .toDouble()) / 100
                            } else {
                                binding.etDiscountValue.text.toString().toDouble()
                            }

                            if (discountType == AppConstant.DISCOUNT_TYPE_PERCENT) {
                                discountDetails.value =
                                    binding.etDiscountValue.text.toString().toDouble()
                                discountDetails.calculated_value = discount
                            } else {
                                discountDetails.value = discount
                            }

                            discountDetails.type = discountType

                            model.discount_details = discountDetails
                            model.discountValue =
                                CalculatorHelper().formatDoubleDecimalPoint(
                                    discount,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )?.toDouble()
                            model.discountType = discountType
                            val priceAfterDiscount = model.price?.minus(discount)
                            model.priceAfterDiscount =
                                CalculatorHelper().formatDoubleDecimalPoint(
                                    priceAfterDiscount,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )
                                    ?.toDouble()


                            binding.tvOrderTotalPrice.text =
                                CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                                    model,
                                    model.priceAfterDiscount!!,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )

                            listener.onApplyDiscount(
                                model,
                                discountType,
                                binding.etDiscountValue.text.toString().toDouble(),
                                position
                            )

                            binding.groupDiscountedPrice.visibility = View.VISIBLE
                            binding.clCartItemDiscountChoice.visibility = View.GONE

                            binding.clCartItemDiscountApplied.visibility = View.VISIBLE

                            if (discountType == AppConstant.DISCOUNT_TYPE_RUPEES) {
                                binding.tvAppliedDiscount.text =
                                    itemView.context.getString(
                                        R.string.discount_applied_with_rupee_symbol,
                                        "" + model.discount_details?.value!!
                                    )
                            } else {
                                binding.tvAppliedDiscount.text =
                                    itemView.context.getString(
                                        R.string.discount_applied_with_percentage_symbol,
                                        "" + model.discount_details?.value!! + " %"
                                    )
                            }

                            binding.clCartItemDiscount.visibility = View.INVISIBLE

                            binding.tvOrderPrice.text =
                                CalculatorHelper().convertLargeAmount(
                                    model.priceAfterDiscount!!,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )

                            binding.etDiscountValue.setText("")

                            binding.tvGstInfo.text =
                                CalculatorHelper().calculateGstAmountForSingleUnit(
                                    model.priceAfterDiscount!!,
                                    model.gst!!,
                                    model.gst_exclusive!!,
                                    AppConstant.FOUR_DECIMAL_POINTS
                                )
                        }
                    } else {
                        Toast.makeText(
                            itemView.context, "Enter discount value", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (discountChoice == AppConstant.ADD_OFFER) {
                    if (binding.etOffersPrice.text.toString().isNotEmpty()) {

                        if (binding.etOffersPrice.text.toString()
                                .toDouble() > model.price!!.roundToInt()
                        ) {
                            val alertDialogBuilder = AlertDialog.Builder(itemView.context)
                            alertDialogBuilder.setTitle("Offer Price")
                            alertDialogBuilder.setMessage("Are you sure offer price is more then actual price?")
                            alertDialogBuilder.setCancelable(false)

                            alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                                onApplyOffer(model, listener)
                            }
                            alertDialogBuilder.setNegativeButton("Enter new Offer") { _: DialogInterface?, _: Int ->
                                return@setNegativeButton
                            }

                            alertDialogBuilder.show()
                        } else {
                            onApplyOffer(model, listener)
                        }

                    } else {
                        Toast.makeText(
                            itemView.context, "Enter offers price", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            binding.view1.setOnClickListener {
                listener.onItemClick()
            }

            binding.ivCancelDiscount.setOnClickListener {
                model.priceAfterDiscount = model.price
                binding.groupDiscountedPrice.visibility = View.GONE
                binding.clCartItemDiscountApplied.visibility = View.GONE
                binding.clCartItemDiscount.visibility = View.VISIBLE
                binding.tvOrderPrice.text = CalculatorHelper().convertLargeAmount(
                    model.price!!,
                    AppConstant.FOUR_DECIMAL_POINTS
                )

                binding.tvOrderTotalPrice.text =
                    CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                        model,
                        model.price!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.tvGstInfo.text =
                    CalculatorHelper().calculateGstAmountForSingleUnit(
                        model.price!!,
                        model.gst!!,
                        model.gst_exclusive!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )
                listener.onRemoveDiscount(model, position)
            }
        }

        private fun findTelescopicPrice(
            qty: Double?,
            telescopicPriceList: List<TelescopicPricingModel>?,
            model: CartItem
        ): Double {
            var telescopicPriceModel: TelescopicPricingModel? = null
            return if (!telescopicPriceList.isNullOrEmpty()) {
                telescopicPriceModel =
                    telescopicPriceList.findLast {
                        (qty!! * (model.selectedPackagingLevel?.size ?: 1.0)) >= it.qty!!
                    }
                if (telescopicPriceModel != null) {
                    telescopicPriceModel.price!!
                } else {
                    model.price!!
                }
            } else {
                model.price!!
            }
        }

        private fun onApplyOffer(model: CartItem, listener: OnCartActionListener) {
            val discountDetails = CartItemDiscountModel()

            if (binding.etOffersPrice.text.toString().isNotEmpty()) {
                model.isDiscountOnParticularItem = true
                model.discountType = AppConstant.DISCOUNT_TYPE_OFFER_PRICE


                val discount = model.price?.minus(
                    binding.etOffersPrice.text.toString().toDouble()
                )

                discountDetails.value = binding.etOffersPrice.text.toString().toDouble()
                discountDetails.type = AppConstant.DISCOUNT_TYPE_OFFER_PRICE

                model.discount_details = discountDetails
                model.discountValue = CalculatorHelper().formatDoubleDecimalPoint(
                    discount,
                    AppConstant.FOUR_DECIMAL_POINTS
                )?.toDouble()
                val priceAfterDiscount = model.price?.minus(discount!!)
                model.priceAfterDiscount =
                    CalculatorHelper().formatDoubleDecimalPoint(
                        priceAfterDiscount,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )?.toDouble()
                model.isOfferPriceApplied = true

                listener.onApplyOfferPrice(
                    model, "" + binding.etOffersPrice.text, position
                )

                binding.groupDiscountedPrice.visibility = View.VISIBLE
                binding.clCartItemDiscountChoice.visibility = View.GONE

                binding.clCartItemDiscountApplied.visibility = View.VISIBLE

                binding.tvAppliedDiscount.text =
                    itemView.context.getString(
                        R.string.offer_price_applied_with_rupee_symbol,
                        "" + CalculatorHelper().formatDoubleDecimalPoint(
                            model.priceAfterDiscount,
                            AppConstant.FOUR_DECIMAL_POINTS
                        )
                    )

                binding.clCartItemDiscount.visibility = View.INVISIBLE

                binding.tvOrderPrice.text =
                    CalculatorHelper().convertLargeAmount(
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.tvOrderTotalPrice.text =
                    CalculatorHelper().calculateFinalProductPriceAfterDiscount(
                        model,
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                binding.etOffersPrice.setText("")

                binding.rbDiscount.isChecked = true

                binding.tvGstInfo.text =
                    CalculatorHelper().calculateGstAmountForSingleUnit(
                        model.priceAfterDiscount!!,
                        model.gst!!,
                        model.gst_exclusive!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )
            }
        }
    }

    interface OnCartActionListener {
        fun onRemoveItem(model: CartItem, position: Int)
        fun onQuantityChange(model: CartItem, quantity: String, position: Int)
        fun onPackagingLevelChange(model: CartItem, position: Int)
        fun onApplyDiscount(
            model: CartItem, discount_type: String, discount_value: Double, position: Int
        )

        fun onApplyOfferPrice(
            model: CartItem, offersPrice: String, position: Int
        )

        fun onRemoveDiscount(
            model: CartItem, position: Int
        )

        fun onItemClick()
    }

}