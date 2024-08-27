package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTypeItemBinding
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.order_history.ReceivedData


class OrderStatusAdapter(
    private var data: ArrayList<AllCategoryResponseModel>?,
    private var listener: OnItemStatusListener,
    private val checkboxStatusOnMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_type_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], listener,checkboxStatusOnMap)
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
        fun bindItem(data: AllCategoryResponseModel, listener: OnItemStatusListener, checkboxStatusOnMap: HashMap<Int, Boolean>) {
            binding.stateTxt.text = data.name
            binding.stateTxt.tag = data
            binding.stateTxt.isChecked = checkboxStatusOnMap[adapterPosition] ?: false
            binding.stateTxt.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemStatusCheck(data.name)
                } else {
                    listener.onItemStatusUncheck(data.name)

                }
                checkboxStatusOnMap[adapterPosition] = isChecked

            }
        }
    }
}
interface OnItemStatusListener {
    fun onItemStatusCheck(id: String?)
    fun onItemStatusUncheck(id: String?)
}
