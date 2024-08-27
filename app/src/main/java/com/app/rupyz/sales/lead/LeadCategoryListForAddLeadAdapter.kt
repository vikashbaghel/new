package com.app.rupyz.sales.lead

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CategoryListItemForAddLeadBinding
import com.app.rupyz.model_kt.LeadCategoryDataItem

class LeadCategoryListForAddLeadAdapter (
    private var data: List<LeadCategoryDataItem>,
    private var listener: AllCategoryListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_list_item_for_add_lead, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = CategoryListItemForAddLeadBinding.bind(itemView)
        fun bindItem(
            model: LeadCategoryDataItem,
            position: Int,
            listener: AllCategoryListener
        ) {
            binding.tvCategory.text = model.name.toString()
            binding.tvCategory.setOnClickListener { listener.onSelect(model, position) }
        }
    }

    interface AllCategoryListener {
        fun onSelect(model: LeadCategoryDataItem, position: Int)
    }
}
