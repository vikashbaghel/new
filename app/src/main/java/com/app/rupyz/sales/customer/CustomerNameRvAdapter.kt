package com.app.rupyz.sales.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CategoryListItemForAddLeadBinding
import com.app.rupyz.model_kt.order.customer.CustomerData

class CustomerNameRvAdapter (
    private var data: List<CustomerData>,
    private var listener: ICustomerSelectListener
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
            model: CustomerData,
            position: Int,
            listener: ICustomerSelectListener
        ) {
            binding.tvCategory.text = model.name.toString()
            binding.tvCategory.setOnClickListener { listener.onCustomerSelect(model, position) }
        }
    }

    interface ICustomerSelectListener {
        fun onCustomerSelect(model: CustomerData, position: Int)
    }
}
