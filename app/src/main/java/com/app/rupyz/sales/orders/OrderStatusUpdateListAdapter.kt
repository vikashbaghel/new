package com.app.rupyz.sales.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemOrderStatusListItemBinding
import com.app.rupyz.model_kt.OrderStatusModel

class OrderStatusUpdateListAdapter(
    private var data: MutableList<OrderStatusModel>,
    private var listener: IOrderDispatchListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_status_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position],
            position,
            listener
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderStatusListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: OrderStatusModel,
            position: Int,
            listener: IOrderDispatchListener
        ) {
            if (model.isSelected == true) {
                binding.ivRadioButton.setBackgroundResource(R.drawable.ic_radio_button_selected)
            } else {
                binding.ivRadioButton.setBackgroundResource(R.drawable.ic_radio_button_not_selected)
            }
            binding.tvOrderStatus.text = model.name

            itemView.setOnClickListener {
                listener.onUpdateStatus(model, position)
            }
        }
    }

    interface IOrderDispatchListener {
        fun onUpdateStatus(model: OrderStatusModel, position: Int)
    }

}