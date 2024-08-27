package com.app.rupyz.sales.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.model_kt.VariantsItem

class ProductVariantsAdapter(
    private val variantList: List<VariantsItem>,
    private val listener: ProductVariantsOptionsAdapter.VariantOptionSelectedListener
) : RecyclerView.Adapter<ProductVariantsAdapter.VariantViewHolder>() {

    inner class VariantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val variantName: TextView = itemView.findViewById(R.id.variant_name)
        val optionsRecyclerView: RecyclerView = itemView.findViewById(R.id.options_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_variant_item, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        val variant = variantList[position]
        holder.variantName.text = variant.name

        holder.optionsRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL, false
        )

        if (variant.options.isNullOrEmpty().not()) {
            holder.optionsRecyclerView.adapter = ProductVariantsOptionsAdapter(
                variant.name,
                variant.options!!, listener
            )
        }
    }

    override fun getItemCount(): Int = variantList.size
}