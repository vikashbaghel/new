package com.app.rupyz.sales.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemStaffInfoListBinding
import com.app.rupyz.databinding.ItemStaffListForAssignBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.JointStaffInfoModel
import com.app.rupyz.model_kt.order.sales.StaffData


class StaffInfoListAdapter(
        private var data: ArrayList<JointStaffInfoModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_staff_info_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemStaffInfoListBinding.bind(itemView)

        fun bindItem(model: JointStaffInfoModel) {
            binding.tvCustomerName.text = model.name

            if (model.picUrl.isNullOrEmpty().not()) {
                ImageUtils.loadTeamImage(model.picUrl, binding.ivProfile)
            } else {
                binding.ivProfile.setImageResource(R.mipmap.ic_user_default)
            }
        }
    }
}