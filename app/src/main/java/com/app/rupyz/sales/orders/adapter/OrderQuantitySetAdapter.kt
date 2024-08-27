package com.app.rupyz.sales.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemOrderQuantitySetBinding
import com.app.rupyz.generic.utils.CalculatorHelper


class OrderQuantitySetAdapter(
    private var data: ArrayList<Map.Entry<String, Double>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_quantity_set, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemOrderQuantitySetBinding.bind(itemView)

        fun bindItem(model: Map.Entry<String, Double>, position: Int) {
            binding.tvUnit.text = model.component1().capitalize()
            binding.tvQty.text = CalculatorHelper().calculateQuantity(model.component2())
        }
    }
}