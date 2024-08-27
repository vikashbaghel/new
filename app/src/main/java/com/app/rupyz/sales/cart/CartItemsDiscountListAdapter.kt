package com.app.rupyz.sales.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCartDiscountListItemBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItemDiscountModel

class CartItemsDiscountListAdapter(
    private var data: ArrayList<CartItemDiscountModel>,
    private var listener: CartDiscountDeleteListener,
    private var isDiscountDeleteEnable: Boolean,
    private var isOthersCharges: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_discount_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position],
            position,
            listener,
            isDiscountDeleteEnable,
            isOthersCharges
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCartDiscountListItemBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: CartItemDiscountModel,
            position: Int,
            listener: CartDiscountDeleteListener,
            isDiscountDeleteEnable: Boolean,
            isOthersCharges: Boolean
        ) {
            if (isOthersCharges) {
                binding.tvDiscountIndex.visibility = View.VISIBLE
                binding.tvDiscountIndex.text = "${position + 1}."
                binding.clDiscount.setBackgroundResource(R.drawable.delivery_charge_bg)
            } else {
                binding.tvDiscountIndex.visibility = View.GONE
                binding.clDiscount.setBackgroundResource(R.drawable.total_discount_orange_bg)
            }

            if (model.type.equals(AppConstant.DISCOUNT_TYPE_RUPEES)) {
                binding.tvDiscountKey.text = model.name
                binding.tvDiscountValue.text =
                    CalculatorHelper().convertCommaSeparatedAmount(model.value, AppConstant.TWO_DECIMAL_POINTS)

            } else if (model.type.equals(AppConstant.DISCOUNT_TYPE_PERCENT)) {
                binding.tvDiscountKey.text =
                    model.name + " (" + CalculatorHelper().calculateQuantity(model.value) + "%)"
                binding.tvDiscountValue.text = CalculatorHelper().convertCommaSeparatedAmount(
                        model.calculated_value,
                        AppConstant.TWO_DECIMAL_POINTS
                )
            } else {
                binding.tvDiscountKey.text = model.name
                binding.tvDiscountValue.text =
                    CalculatorHelper().convertCommaSeparatedAmount(model.value, AppConstant.TWO_DECIMAL_POINTS)
            }

            if (isDiscountDeleteEnable) {
                binding.ivEditDiscount.visibility = View.VISIBLE

                binding.ivEditDiscount.setOnClickListener {
                    if (isOthersCharges) {
                        listener.onEditOthersCharges(
                            position,
                            model
                        )
                    } else {
                        listener.onEditDiscount(
                            position,
                            model
                        )
                    }
                }

                itemView.setOnClickListener {
                    if (isOthersCharges) {
                        listener.onEditOthersCharges(
                            position,
                            model
                        )
                    } else {
                        listener.onEditDiscount(
                            position,
                            model
                        )
                    }
                }
            } else {
                binding.ivEditDiscount.visibility = View.GONE
            }
        }
    }

    interface CartDiscountDeleteListener {
        fun onEditDiscount(position: Int, model: CartItemDiscountModel)
        fun onEditOthersCharges(position: Int, model: CartItemDiscountModel)
    }

}