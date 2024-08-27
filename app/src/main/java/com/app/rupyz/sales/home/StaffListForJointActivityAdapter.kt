package com.app.rupyz.sales.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemStaffListForAssignBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.sales.StaffData


class StaffListForJointActivityAdapter(
        private var data: ArrayList<StaffData>,
        private var listener: IAssignStaffListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_staff_list_for_assign, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemStaffListForAssignBinding.bind(itemView)

        fun bindItem(model: StaffData, position: Int, listener: IAssignStaffListener) {
            binding.tvCustomerName.text = model.name

            if (model.profilePicUrl.isNullOrEmpty().not()) {
                ImageUtils.loadTeamImage(model.profilePicUrl, binding.ivProfile)
            } else {
                binding.ivProfile.setImageResource(R.mipmap.ic_user_default)
            }

            binding.cbCustomerName.setOnCheckedChangeListener(null)

            binding.cbCustomerName.isChecked = model.isSelected == true

            binding.cbCustomerName.setOnCheckedChangeListener { _, isChecked ->
                listener.selectStaffForJointActivity(isChecked, model)
            }
        }
    }

    interface IAssignStaffListener {
        fun selectStaffForJointActivity(checked: Boolean, model: StaffData?)
    }
}