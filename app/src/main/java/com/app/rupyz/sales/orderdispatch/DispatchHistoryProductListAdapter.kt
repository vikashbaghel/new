package com.app.rupyz.sales.orderdispatch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDispatchHistoryProductListBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.CartItem

class DispatchHistoryProductListAdapter(
    private var data: ArrayList<CartItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dispatch_history_product_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDispatchHistoryProductListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: CartItem, position: Int) {

            if (model.code != null) {
                binding.tvOrderNanoId.text = model.code
            } else {
                binding.tvOrderNanoId.text = ""
            }

            ImageUtils.loadImage(model.displayPicUrl, binding.ivProduct)

            binding.tvOrderName.text = model.name
             val variantName=model.variantName ?: ""
            binding.tvOrderCategory.text = model.category+"/"+variantName

            if (model.totalPrice != null) {
                val doublePrice = model.totalPrice
                binding.tvOrderTotalPrice.text =
                    (CalculatorHelper().convertLargeAmount(doublePrice, AppConstant.FOUR_DECIMAL_POINTS))
            } else {
                binding.tvOrderTotalPrice.text = "0.0"
            }

            binding.tvOrderedUnit.text = CalculatorHelper().calculateQuantity(model.qty)

            binding.etShipmentUnit.text = CalculatorHelper().calculateQuantity(model.dispatchQty)


            if (model.discount_details != null && model.discount_details?.type != null) {

                model.priceAfterDiscount = model.price
                model.price = model.original_price

                binding.tvDiscountApplied.visibility = View.VISIBLE

                model.discount_details?.let {
                    if (it.type.equals(AppConstant.DISCOUNT_TYPE_RUPEES)) {
                        binding.tvDiscountApplied.text = itemView.context.getString(
                            R.string.discount_applied_with_rupee_symbol,
                            "" + CalculatorHelper().formatDoubleDecimalPoint(it.value, AppConstant.FOUR_DECIMAL_POINTS)
                        )
                    } else if (it.type.equals(AppConstant.DISCOUNT_TYPE_PERCENT)) {
                        binding.tvDiscountApplied.text =
                            CalculatorHelper().calculateQuantity(it.value) + "% Discount Applied"
                    } else {
                        binding.tvDiscountApplied.text = itemView.context.getString(
                            R.string.offer_price_applied_with_rupee_symbol,
                            "" + CalculatorHelper().formatDoubleDecimalPoint(it.value, AppConstant.FOUR_DECIMAL_POINTS)
                        )
                    }
                }

                binding.groupDiscountedPriceAfterDiscount.visibility = View.VISIBLE

                binding.tvOrderPrice.text = itemView.context.getString(
                    R.string.price_with_rupee_symbol, CalculatorHelper().formatDoubleDecimalPoint(
                        model.priceAfterDiscount,
                        AppConstant.FOUR_DECIMAL_POINTS
                )
                )

                binding.tvOrderDiscountedPrice.text = itemView.context.getString(
                    R.string.price_with_rupee_symbol,
                    "" + CalculatorHelper().formatDoubleDecimalPoint(model.price ?: 0.0, AppConstant.FOUR_DECIMAL_POINTS)
                )
            } else {
                binding.tvDiscountApplied.visibility = View.GONE
                binding.tvOrderPrice.text = itemView.context.getString(
                    R.string.price_with_rupee_symbol,
                    "" + CalculatorHelper().formatDoubleDecimalPoint(model.price!!, AppConstant.FOUR_DECIMAL_POINTS)
                )
                binding.groupDiscountedPriceAfterDiscount.visibility = View.GONE
            }

            if (model.packagingUnit != null) {
                binding.tvOrderQty.text = itemView.context.getString(
                    R.string.product_quantity_without_pre_text_string,
                    CalculatorHelper().calculateQuantity(model.qty),
                    model.packagingUnit
                )
            } else if (model.unit != null) {
                binding.tvOrderQty.text = itemView.context.getString(
                    R.string.product_quantity_without_pre_text_string,
                    CalculatorHelper().calculateQuantity(model.qty),
                    model.unit
                )
            }

        }
    }
}