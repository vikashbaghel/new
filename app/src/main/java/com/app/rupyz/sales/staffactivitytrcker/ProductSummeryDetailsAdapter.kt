package com.app.rupyz.sales.staffactivitytrcker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemProductSummeryBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.ProductMetricsItem

class ProductSummeryDetailsAdapter(
    private var data: ArrayList<ProductMetricsItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_summery, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductSummeryBinding.bind(itemView)

        fun bindItem(model: ProductMetricsItem) {
            binding.hdProductName.text = model.name

            binding.tvProductQuantity.text = CalculatorHelper().calculateQuantity(model.qty) + " ${model.unit}"

            binding.tvAmount.text =
                CalculatorHelper().convertLargeAmount(model.amount ?: 0.00, AppConstant.TWO_DECIMAL_POINTS)
        }
    }
}