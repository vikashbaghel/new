package com.app.rupyz.sales.beatplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CategoryListItemForAddLeadBinding
import com.app.rupyz.model_kt.NameAndIdSetInfoModel

class StaffNameRvAdapter(
    private var data: List<NameAndIdSetInfoModel>,
    private var listener: IStaffSelectListener
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
            model: NameAndIdSetInfoModel,
            position: Int,
            listener: IStaffSelectListener
        ) {
            binding.tvCategory.text = model.name.toString()
            binding.tvCategory.setOnClickListener {
                listener.onAssignedStaffSelect(
                    model,
                    position
                )
            }
        }
    }

    interface IStaffSelectListener {
        fun onAssignedStaffSelect(model: NameAndIdSetInfoModel, position: Int)
    }
}
