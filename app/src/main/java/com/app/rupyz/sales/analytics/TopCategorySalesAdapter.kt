package com.app.rupyz.sales.analytics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemTopCategoryBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.TopCategoryDataItem

class TopCategorySalesAdapter(
    private var data: ArrayList<TopCategoryDataItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemTopCategoryBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: TopCategoryDataItem, position: Int) {
            if (model.category != null && model.category != "") {
                binding.tvName.text = model.category
            }

            binding.tvAmount.text =
                (CalculatorHelper().convertLargeAmount(model.totalPrice!!, AppConstant.TWO_DECIMAL_POINTS))

            binding.tvSalePercentage.text = "-"
        }
    }
}
