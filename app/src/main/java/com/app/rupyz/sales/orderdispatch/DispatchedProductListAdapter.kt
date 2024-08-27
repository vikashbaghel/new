package com.app.rupyz.sales.orderdispatch

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDispatchProductListBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItem

class DispatchedProductListAdapter(
    private var data: ArrayList<CartItem>,
    private var listener: IDispatchProductUncheckListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dispatch_product_list, parent, false)
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
        private var binding = ItemDispatchProductListBinding.bind(itemView)
        var textWatcher: TextWatcher? = null

        @SuppressLint("SetTextI18n")
        fun bindItem(model: CartItem, position: Int, listener: IDispatchProductUncheckListener) {

            binding.etShipmentUnit.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                    7,
                    AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
            )

            binding.tvOrderName.text = model.name
            binding.tvOrderCategory.text = model.category

            if (model.packagingUnit != null) {
                binding.tvOrderUnit.text = model.packagingUnit
            } else if (model.unit != null) {
                binding.tvOrderUnit.text = model.unit
            }


            binding.tvOrderedUnit.text = CalculatorHelper().calculateQuantity(model.qty)

            binding.cbCheck.setOnCheckedChangeListener(null)

            binding.cbCheck.isChecked = model.isSelected!!

            enableShipment(model.isSelected!!)

            binding.cbCheck.setOnCheckedChangeListener { _, check ->
                model.isSelected = check
                listener.onCheckChange(model)
                enableShipment(check)
            }


            if (model.dispatchQty != null && model.dispatchQty != 0.0) {
                binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(model.dispatchQty))
            } else if (model.totalDispatchedQty != null) {
                val dispatchedQty = model.qty?.minus(model.totalDispatchedQty!!)

                if (dispatchedQty != null && dispatchedQty > 0) {
                    model.isTotallyShipped = false
                    binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(dispatchedQty))

                    model.dispatchQty = dispatchedQty
                } else {
                    model.isTotallyShipped = true
                    binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(model.totalDispatchedQty))
                    binding.etShipmentUnit.isEnabled = false
                    binding.etShipmentUnit.setBackgroundColor(
                        itemView.context.resources.getColor(
                            R.color.ordered_unit_background
                        )
                    )
                }
            } else {
                binding.etShipmentUnit.setText(CalculatorHelper().calculateQuantity(model.qty))
            }

            textWatcher =
                MyTextWatcher(model)
        }

        fun enableTextWatcher() {
            binding.etShipmentUnit.addTextChangedListener(textWatcher)
            binding.etShipmentUnit.tag = adapterPosition
        }

        fun disableTextWatcher() {
            binding.etShipmentUnit.removeTextChangedListener(textWatcher)
        }
        private fun enableShipment(check: Boolean) {
            if (check) {
                binding.etShipmentUnit.setBackgroundResource(R.drawable.white_rectangle_with_stroke_background)
                binding.etShipmentUnit.isEnabled = true
            } else {
                binding.etShipmentUnit.setBackgroundResource(R.drawable.gray_rectangle_bg)
                binding.etShipmentUnit.isEnabled = false
            }
        }
    }


    class MyTextWatcher(
        private val model: CartItem
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            if (input.toString().isNotEmpty() && input.toString() != ".") {
                model.dispatchQty = input.toString().toDouble()
            } else {
                model.dispatchQty = 0.0
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    interface IDispatchProductUncheckListener {
        fun onCheckChange(model: CartItem)

    }
}