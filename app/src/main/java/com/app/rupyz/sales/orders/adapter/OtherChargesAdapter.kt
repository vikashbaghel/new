package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.DiscountItemBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItemDiscountModel

class OtherChargesAdapter(
    private var data: MutableList<CartItemDiscountModel>,

    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.discount_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position]


        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = DiscountItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bindItem(
            model: CartItemDiscountModel,

            ) {

            binding.tvDiscount.text = model.name
            binding.tvDiscountAmount.text = CalculatorHelper().convertLargeAmount(
                model.value ?: 0.0,
                AppConstant.TWO_DECIMAL_POINTS
            )

        }
    }


}