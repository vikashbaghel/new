package com.app.rupyz.sales.staffactivitytrcker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ActiveInactiveStaffInfoItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.StaffActiveInactiveInfoModel

class ActiveInActiveStaffInfoAdapter(
    private var data: ArrayList<StaffActiveInactiveInfoModel>,
    private var activityType: String,
    private var listener: IOnStaffAttendanceInfoListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.active_inactive_staff_info_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], activityType, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ActiveInactiveStaffInfoItemBinding.bind(itemView)

        fun bindItem(
            model: StaffActiveInactiveInfoModel,
            activityType: String,
            listener: IOnStaffAttendanceInfoListener
        ) {
            binding.tvStaffName.text = model.staffName

            if (model.picUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.picUrl, binding.ivProfile)
            } else {
                binding.ivProfile.setImageResource(R.mipmap.ic_user_default)
            }

            if (activityType == AppConstant.ACTIVE ||
                activityType == AppConstant.LEAVE
            ) {
                if (model.timeIn.isNullOrEmpty().not()) {
                    binding.tvCheckInTime.text =
                        DateFormatHelper.convertDateToTimeFormat(model.timeIn)
                    binding.tvCheckInTime.visibility = View.VISIBLE
                } else {
                    binding.tvCheckInTime.visibility = View.GONE
                }
            }

            if (activityType == AppConstant.LEAVE) {
                itemView.setOnClickListener {
                    listener.staffAttendanceInfo(model)
                }
            }
        }
    }

    interface IOnStaffAttendanceInfoListener {
        fun staffAttendanceInfo(model: StaffActiveInactiveInfoModel)
    }
}