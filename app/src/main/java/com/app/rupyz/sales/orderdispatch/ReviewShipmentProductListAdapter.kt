package com.app.rupyz.sales.orderdispatch

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemShipmentReviewProductListBinding
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItem

class ReviewShipmentProductListAdapter(
    private var data: ArrayList<CartItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shipment_review_product_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemShipmentReviewProductListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: CartItem, position: Int) {

            binding.tvOrderName.text = model.name
            binding.tvOrderCategory.text = model.category
            binding.tvOrderUnit.text = model.packagingUnit

            if (model.qty != null) {
                if (model.qty!! % 1 == 0.0) {
                    binding.tvOrderedUnit.text = "" + model.qty!!.toInt()
                } else{
                    binding.tvOrderedUnit.text = "" + model.qty!!
                }
            }

            binding.etShipmentUnit.isEnabled = false

            binding.ivEditQuantity.setOnClickListener {
                binding.etShipmentUnit.setBackgroundResource(R.drawable.white_rectangle_with_stroke_background)
                binding.etShipmentUnit.isEnabled = true
            }

            if (model.dispatchQty != null) {
                binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(model.dispatchQty))
            } else if (model.totalDispatchedQty != null && model.totalDispatchedQty != 0.0) {
                binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(model.totalDispatchedQty))
            }

            binding.etShipmentUnit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?, p1: Int, p2: Int, p3: Int
                ) {
                }

                override fun onTextChanged(
                    input: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                    if (input.toString().isNotEmpty()) {
                        model.dispatchQty = input.toString().toDouble()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}

            })
        }
    }
}