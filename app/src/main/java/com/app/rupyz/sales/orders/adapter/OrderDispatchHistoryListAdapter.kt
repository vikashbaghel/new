package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemOrderDispatchListItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.model_kt.order.order_history.DispatchHistoryListModel

class OrderDispatchHistoryListAdapter(
    private var data: ArrayList<DispatchHistoryListModel>,
    private var listener: IOrderDispatchListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_dispatch_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position], listener
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderDispatchListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: DispatchHistoryListModel,
            listener: IOrderDispatchListener
        ) {
            binding.tvHistoryDate.text =
                DateFormatHelper.convertIsoToDateAndTimeFormat(model.createdAt)
            itemView.setOnClickListener { listener.onGetDispatchHistory(model) }
        }
    }

    interface IOrderDispatchListener {
        fun onGetDispatchHistory(model: DispatchHistoryListModel)
    }

}