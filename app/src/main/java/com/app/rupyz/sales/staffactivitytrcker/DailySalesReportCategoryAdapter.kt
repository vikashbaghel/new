package com.app.rupyz.sales.staffactivitytrcker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSalesCategoryBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CategoryMetricsItem

class DailySalesReportCategoryAdapter(
    private var data: ArrayList<CategoryMetricsItem>,
    private var type: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, type)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSalesCategoryBinding.bind(itemView)

        fun bindItem(model: CategoryMetricsItem, position: Int, type: String) {
            if (type == AppConstant.CUSTOMER) {
                binding.hdCategoryName.text = model.customerName
            } else {
                binding.hdCategoryName.text = model.name
            }

            binding.tvCategoryCount.text =
                CalculatorHelper().convertLargeAmount(model.amount ?: 0.00, AppConstant.TWO_DECIMAL_POINTS)
        }
    }
}