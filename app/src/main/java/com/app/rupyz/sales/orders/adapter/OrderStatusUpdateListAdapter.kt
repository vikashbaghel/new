package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemOrderStatusBinding
import com.app.rupyz.model_kt.OrderStatusModel

class OrderStatusUpdateListAdapter(
    private var data: MutableList<OrderStatusModel>,
    private var listener: IOrderDispatchListener,
    private var mPosition:String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_status, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position],
            position,
            listener,
            mPosition
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderStatusBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bindItem(
            model: OrderStatusModel,
            position: Int,
            listener: IOrderDispatchListener, mPosition: String
        ) {
            if (model.name == mPosition) {
                binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.black))
                itemView.setBackgroundResource(R.drawable.edit_text_grey_with_stroke_background)

            } else {
                binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.leve_text_color))
                itemView.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
            }
            binding.tvOrderStatus.text = model.name
              binding.img.setColorFilter(model.statusColor!!)
            if (mPosition==model.name)
            {
                binding.ivRadioButton.isSelected = true
            }

            itemView.setOnClickListener {
                listener.onUpdateStatus(model, position)
            }
        }
    }

    interface IOrderDispatchListener {
        fun onUpdateStatus(model: OrderStatusModel, position: Int)
    }

}