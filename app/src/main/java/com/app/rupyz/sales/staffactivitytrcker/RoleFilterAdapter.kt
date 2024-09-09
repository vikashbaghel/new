package com.app.rupyz.sales.staffactivitytrcker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.StateItemBinding
import com.app.rupyz.generic.helper.log
import com.app.rupyz.model_kt.AssignedRoleItem


class RoleFilterAdapter(
    private var data: ArrayList<AssignedRoleItem>,
    private var listener: OnRoleCheckListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.state_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun filterList(filterList: ArrayList<AssignedRoleItem>) {
        data = filterList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StateItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: AssignedRoleItem, listener: OnRoleCheckListener
        ) {
            binding.stateTxt.text = model.name
            binding.stateTxt.tag = model.name


            binding.stateTxt.setOnCheckedChangeListener(null)
            binding.stateTxt.isChecked = model.isSelected ?: false

            binding.stateTxt.setOnCheckedChangeListener { _, isChecked ->
                model.isSelected = isChecked
                listener.onRoleCheckChange(model.name, isChecked)
            }
        }
    }


}

interface OnRoleCheckListener {
    fun onRoleCheckChange(id: String?, isCheck: Boolean)
}