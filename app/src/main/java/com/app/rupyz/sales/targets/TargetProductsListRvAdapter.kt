package com.app.rupyz.sales.targets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemTargetProductsListBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.ProductMetricsModel

class TargetProductsListRvAdapter(
    private var data: ArrayList<ProductMetricsModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_target_products_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTargetProductsListBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: ProductMetricsModel, position: Int) {
            binding.tvTitle.text = model.name

            if (model.type.equals(AppConstant.TARGET_PRODUCTS_TYPE_COUNT)) {
                binding.tvTargetDetails.visibility = View.VISIBLE
                binding.tvTargetDetails.text = "Target in " + model.unit

                binding.tvTarget.text = "${model.targetValue?.toInt()}"
                binding.tvAchieved.text = "${model.currentValue?.toInt()}"

            } else if (model.type.equals(AppConstant.TARGET_PRODUCTS_TYPE_AMOUNT)) {
                binding.tvTargetDetails.visibility = View.GONE
                binding.tvTarget.text = CalculatorHelper().convertCommaSeparatedAmount(model.targetValue, AppConstant.FOUR_DECIMAL_POINTS)
                binding.tvAchieved.text = CalculatorHelper().convertCommaSeparatedAmount(model.currentValue, AppConstant.FOUR_DECIMAL_POINTS)
            }

            if (model.currentValue != 0.0) {
                val progress = (model.currentValue!! / model.targetValue!!) * 100
                binding.tvPercent.text = "${progress.toInt()}%"
                binding.pbPercent.progress = progress.toInt()
            } else {
                binding.tvPercent.text = "0%"
                binding.pbPercent.progress = 0
            }
        }
    }
}