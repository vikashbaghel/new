package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.StateItemBinding


class StatesAdapter(
    private var data: ArrayList<String>,
    private var listener: OnItemStateCheckListener,
    private val checkboxStateMap: HashMap<String, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.state_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], listener, checkboxStateMap)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun filterList(filterList: ArrayList<String>) {
        data = filterList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StateItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: String, listener: OnItemStateCheckListener,
            checkboxStateMap: HashMap<String, Boolean>
        ) {
            binding.stateTxt.text = model
            binding.stateTxt.tag = model
            binding.stateTxt.isChecked = checkboxStateMap[model] ?: false
            binding.stateTxt.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemStateCheck(model)
                } else {
                    listener.onItemStateUncheck(model)

                }
                checkboxStateMap[binding.stateTxt.tag.toString()] = isChecked
            }

        }
    }


}

interface OnItemStateCheckListener {
    fun onItemStateCheck(id: String?)
    fun onItemStateUncheck(id: String?)
}