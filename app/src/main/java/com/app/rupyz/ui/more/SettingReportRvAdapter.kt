package com.app.rupyz.ui.more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSettingReportAdapterBinding

class SettingReportRvAdapter(
    var list: ArrayList<String>,
    var type: String,
    var listener: IReportSettingListener
) :
    RecyclerView.Adapter<SettingReportRvAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSettingReportAdapterBinding.bind(itemView)

        fun bindItem(
            details: String,
            position: Int,
            listener: IReportSettingListener,
            type: String
        ) {
            binding.tvDetails.text = details

            binding.ivRemove.setOnClickListener {
                listener.removeItem(type, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting_report_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(list[position], position, listener, type)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface IReportSettingListener {
        fun removeItem(type: String, position: Int)
    }
}