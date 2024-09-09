package com.app.rupyz.sales.staffactivitytrcker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.FilterSingleStaffItemBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.sales.StaffData


class FilterStaffAdapter(
    private var data: ArrayList<StaffData>?,
    private var listener: OnStaffItemCheckListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_single_staff_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            (holder as MyViewHolder).bindItem(data!![position], listener)
        } catch (e: Exception) {
            println()
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FilterSingleStaffItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: StaffData, listener: OnStaffItemCheckListener
        ) {

            try {

                binding.staffName.text = data.name
                binding.staffName.tag = data.user

                if (data.profilePicUrl.isNullOrEmpty().not()) {
                    ImageUtils.loadImage(data.profilePicUrl, binding.imgStaff)
                } else {
                    binding.imgStaff.setImageResource(R.mipmap.ic_user_default)
                }

                if (data.isSelected) {
                    binding.rbStaff.setImageResource(R.drawable.ic_radio_button_selected)
                } else {
                    binding.rbStaff.setImageResource(R.drawable.ic_radio_button_not_selected)
                }

                binding.rbStaff.setOnClickListener {
                    listener.onStaffCheckChange(data, adapterPosition)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnStaffItemCheckListener {
        fun onStaffCheckChange(model: StaffData, adapterPosition: Int)
    }
}


