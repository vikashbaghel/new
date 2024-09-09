package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.BeatInfoItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.BeatPlanModel

class BeatPlanInfoAdapter(
    var data: ArrayList<BeatPlanModel>,
    var staffDetailsView: Boolean,
    var approval: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.beat_info_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MyViewHolder).bindItem(
            data[position],
            position,
            staffDetailsView,
            approval
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = BeatInfoItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: BeatPlanModel,
            position: Int,
            staffDetailsView: Boolean,
            approval: Boolean
        ) {
            binding.tvTitle.text = model.name

            if (approval) {
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.beat_hint_color))

                if (model.profilePicUrl.isNullOrEmpty().not()) {
                    try {
                        ImageUtils.loadTeamImage(model.profilePicUrl, binding.ivUser)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                binding.ivUser.visibility = View.VISIBLE
                if (model.user_name.isNullOrEmpty().not()) {
                    binding.tvUserName.text = model.user_name
                    binding.tvUserName.visibility = View.VISIBLE
                } else {
                    binding.tvUserName.visibility = View.GONE
                }

            } else {
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.black))
                binding.tvUserName.visibility = View.GONE
                binding.ivUser.visibility = View.GONE
            }

            binding.tvDate.text = "${DateFormatHelper.convertSanctionDate(model.startDate)} - ${
                DateFormatHelper.convertSanctionDate(model.endDate)
            }"

            binding.tvBeatStatus.text = model.status

            when (model.status) {
                AppConstant.APPROVED.uppercase() -> {
                    binding.tvBeatStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    binding.tvBeatStatus.setTextColor(itemView.resources.getColor(R.color.payment_approved_text_color))
                }

                AppConstant.PENDING.uppercase() -> {
                    binding.tvBeatStatus.setBackgroundResource(R.drawable.status_pending_background)
                    binding.tvBeatStatus.setTextColor(itemView.resources.getColor(R.color.pending_text_color))
                }
                AppConstant.REJECTED.uppercase() -> {
                    binding.tvBeatStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                    binding.tvBeatStatus.setTextColor(itemView.resources.getColor(R.color.payment_rejected_text_color))
                }
                AppConstant.COMPLETED.uppercase() -> {
                    binding.tvBeatStatus.setBackgroundResource(R.drawable.status_closed_background)
                    binding.tvBeatStatus.setTextColor(itemView.resources.getColor(R.color.closed_text_color))
                }
            }

            if (model.isActive == true){
                binding.tvActiveStatus.visibility = View.VISIBLE
            } else {
                binding.tvActiveStatus.visibility = View.GONE
            }

            val containerParams = binding.tvDate.layoutParams as ConstraintLayout.LayoutParams

            if (model.status == "REJECTED" && model.rejectReason.isNullOrEmpty().not()) {
                binding.tvRejectedReason.visibility = View.VISIBLE
                binding.tvRejectedReason.text = model.rejectReason
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            } else {
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.tvRejectedReason.visibility = View.GONE
            }
        }
    }
}
