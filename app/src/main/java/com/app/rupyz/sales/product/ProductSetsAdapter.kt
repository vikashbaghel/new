package com.app.rupyz.sales.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ProductSetsItemBinding
import com.app.rupyz.model_kt.ProductSetsModel

class ProductSetsAdapter(
        private val sets: List<ProductSetsModel>,
) : RecyclerView.Adapter<ProductSetsAdapter.SetsViewHolder>() {

    inner class SetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       var binding = ProductSetsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_sets_item, parent, false)
        return SetsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        val sets = sets[position]

        holder.binding.rvSetsList.layoutManager = LinearLayoutManager(holder.itemView.context,
                LinearLayoutManager.HORIZONTAL, false)

        if (sets.variantSetData.isNullOrEmpty().not()) {
            holder.binding.rvSetsList.adapter = ProductSetDetailsAdapter(sets.variantSetData!!)
        }
    }

    override fun getItemCount(): Int = sets.size
}