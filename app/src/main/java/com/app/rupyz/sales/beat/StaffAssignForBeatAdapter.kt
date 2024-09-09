package com.app.rupyz.sales.beat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCustomerListForAssignBinding
import com.app.rupyz.databinding.ItemStaffDataAssigToBeatBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.StaffModelForBeatData


class StaffAssignForBeatAdapter(
    private var data: ArrayList<StaffModelForBeatData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_data_assig_to_beat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemStaffDataAssigToBeatBinding.bind(itemView)

        fun bindItem(model: StaffModelForBeatData, position: Int) {
            binding.tvName.text = model.name

            binding.tvId.text = itemView.resources.getString(R.string.staff_id, "${model.userId}")

            if (model.roles.isNullOrEmpty().not()){
                binding.tvRole.text = "${model.roles?.get(0)}"
            }

            if (model.profilePicUrl.isNullOrEmpty().not()){
                ImageUtils.loadTeamImage(model.profilePicUrl, binding.ivUserImage)
                binding.tvStaffIcon.visibility = View.GONE
            } else {
                binding.tvStaffIcon.text =
                    StringHelper.printName(model.name).trim().substring(0, 1).uppercase()
                binding.tvStaffIcon.visibility = View.VISIBLE
            }
        }
    }
}