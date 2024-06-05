package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSegmentListAllCustomerBinding
import com.app.rupyz.model_kt.order.customer.SegmentDataItem

class ListOfAllSegmentAdapter(
    private var data: ArrayList<SegmentDataItem>,
    private var listener: SegmentActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_segment_list_all_customer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSegmentListAllCustomerBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: SegmentDataItem, position: Int, listener: SegmentActionListener) {
            binding.tvSegmentName.text = model.name
            binding.tvDiscountValue.text = model.discountValue.toString()
            binding.ivMore.setOnClickListener { v ->
                //creating a popup menu
                val popup =
                    PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.customer_action_menu)
                popup.menu.getItem(1).isVisible = false
                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_product -> {
                            listener.onEdit(model, position)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }
        }
    }
}