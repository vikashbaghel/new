package com.app.rupyz.sales.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemProductCategoryFilterBinding
import com.app.rupyz.model_kt.AllCategoryResponseModel

class ProductCategoryFilterAdapter(
        private var data: ArrayList<AllCategoryResponseModel>,
        private var listener: IFilterCategoryListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_category_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductCategoryFilterBinding.bind(itemView)
        fun bindItem(
            model: AllCategoryResponseModel,
            position: Int,
            listener: IFilterCategoryListener
        ) {
            binding.tvCategory.text = model.name

            if (model.isSelected) {
                binding.tvCategory.setBackgroundColor(itemView.resources.getColor(R.color.white))
            } else {
                binding.tvCategory.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
            }

            binding.tvCategory.setOnClickListener {
                model.isSelected = true
                listener.filterCategory(position, model)
            }
        }
    }

    interface IFilterCategoryListener {
        fun filterCategory(position: Int, model: AllCategoryResponseModel)
    }
}