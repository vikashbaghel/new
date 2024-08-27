package com.app.rupyz.sales.analytics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemTopProductBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.TopProductDataItem

class TopProductSalesAdapter(
    private var data: ArrayList<TopProductDataItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_product, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemTopProductBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: TopProductDataItem, position: Int) {
            if (model.name != null && model.name != "") {
                binding.tvName.text = model.name
            }

            binding.tvAmount.text =
                (CalculatorHelper().convertLargeAmount(model.totalPrice!!, AppConstant.TWO_DECIMAL_POINTS))
            binding.tvQuantity.text = "${model.dispatchQty}"

            binding.tvSalePercentage.text = "-"
        }
    }
}