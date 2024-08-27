package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTypeItemBinding
import com.app.rupyz.model_kt.order.order_history.ReceivedData


class OrderReceivedOnAdapter(
    private var data: ArrayList<ReceivedData>?,
    private var listener: OnItemReceivedOnListener,
    private val checkboxReceivedOnMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_type_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], listener,checkboxReceivedOnMap)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ActivityTypeItemBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(data: ReceivedData, listener: OnItemReceivedOnListener, checkboxReceivedOnMap: HashMap<Int, Boolean>) {
            binding.stateTxt.text = data.name
            binding.stateTxt.tag = data
            binding.stateTxt.isChecked = checkboxReceivedOnMap[adapterPosition] ?: false
            binding.stateTxt.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemReceivedOnCheck(data.name)
                } else {
                    listener.onItemReceivedOnUncheck(data.name)

                }
                checkboxReceivedOnMap[adapterPosition] = isChecked

            }
        }
    }
}
interface OnItemReceivedOnListener {
    fun onItemReceivedOnCheck(id: String?)
    fun onItemReceivedOnUncheck(id: String?)
}
