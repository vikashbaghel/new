package com.app.rupyz.sales.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.VariantOptionsItemBinding
import com.app.rupyz.model_kt.VariantOptionsItem

class ProductVariantsOptionsAdapter(
        private var variantName: String?,
        private val optionsList: ArrayList<VariantOptionsItem>,
        private val listener: VariantOptionSelectedListener
) : RecyclerView.Adapter<ProductVariantsOptionsAdapter.OptionViewHolder>() {

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = VariantOptionsItemBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.variant_options_item, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = optionsList[position]

        holder.binding.optionName.text = option.name

        if (option.isSelected) {
            // Change background color for selected option
            holder.binding.optionName.setBackgroundResource(R.drawable.white_with_blue_stroke_4dp_background)
            holder.binding.optionName.setTextColor(holder.itemView.resources.getColor(R.color.theme_purple))
        } else {
            // Reset background color for unselected options
            holder.binding.optionName.setBackgroundResource(R.drawable.gray_stroke_707070_4dp_empty_bg)
            holder.binding.optionName.setTextColor(holder.itemView.resources.getColor(R.color.product_category_color))
        }

        holder.itemView.setOnClickListener {
            option.isSelected = true
            listener.onVariantOptionSelected(variantName, option)
        }
    }

    override fun getItemCount(): Int = optionsList.size


    interface VariantOptionSelectedListener {
        fun onVariantOptionSelected(variantName: String?, mode: VariantOptionsItem)
    }
}