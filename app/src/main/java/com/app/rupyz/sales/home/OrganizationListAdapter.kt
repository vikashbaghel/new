package com.app.rupyz.sales.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemOrgListBinding
import com.app.rupyz.model_kt.OrganizationInfoModel

class OrganizationListAdapter(
    private var data: ArrayList<OrganizationInfoModel>,
    private var listener: IOrgSelectListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_org_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemOrgListBinding.bind(itemView)
        private var selectedPosition = -1

        fun bindItem(model: OrganizationInfoModel, position: Int, listener: IOrgSelectListener) {
            binding.tvOrgName.text = model.legalName

            itemView.setOnClickListener {
                selectedPosition = position
                listener.onOrgSelect(model, position)
            }

            if (selectedPosition == position || model.isSelected == true) {
                binding.ivOrgSelect.visibility = View.VISIBLE
                binding.tvOrgName.setTextColor(itemView.context.getColor(R.color.theme_purple))
            } else {
                binding.ivOrgSelect.visibility = View.INVISIBLE
                binding.tvOrgName.setTextColor(itemView.context.getColor(R.color.black))
            }
        }
    }

    interface IOrgSelectListener{
        fun onOrgSelect(org: OrganizationInfoModel, position: Int)
    }
}