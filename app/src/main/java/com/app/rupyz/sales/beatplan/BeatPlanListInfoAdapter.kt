package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.BeatPlanInfoItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatRouteDayListModel
import java.text.SimpleDateFormat
import java.util.*

class BeatPlanListInfoAdapter(
    var data: ArrayList<BeatRouteDayListModel>,
    var targetVisited: Boolean,
    var listener: IBeatPlanListInfoListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.beat_plan_info_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position],
            targetVisited,
            position,
            listener
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = BeatPlanInfoItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: BeatRouteDayListModel,
            targetVisited: Boolean,
            position: Int,
            listener: IBeatPlanListInfoListener
        ) {
            binding.tvTitle.text = model.beatName
            binding.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                model.date,
                SimpleDateFormat("dd MMM yy, EE", Locale.ENGLISH)
            )

            binding.tvCancel.visibility = View.GONE
            binding.tvTitle.paintFlags =
                binding.tvTitle.paintFlags or Paint.ANTI_ALIAS_FLAG

            if (model.isCancelled == true) {
                binding.clBeatPlanInfo.visibility = View.GONE
                binding.groupCustomerCount.visibility = View.GONE
                binding.groupHeading.visibility = View.GONE
                binding.groupNightStay.visibility = View.GONE
                binding.groupNewLeadCount.visibility = View.GONE
                binding.tvCancel.visibility = View.VISIBLE
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.sales_text_color_light_black))
                binding.tvTitle.paintFlags =
                    binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            } else if (model.moduleType.equals(AppConstant.HOLIDAY)) {
                binding.groupCustomerCount.visibility = View.GONE
                binding.clBeatPlanInfo.visibility = View.GONE
                binding.groupHeading.visibility = View.GONE
                binding.groupNightStay.visibility = View.GONE
                binding.groupNewLeadCount.visibility = View.GONE
                binding.tvTitle.text = itemView.resources.getString(R.string.leave)
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.holiday_beat_color))
            } else {
                binding.groupHeading.visibility = View.VISIBLE
                binding.clBeatPlanInfo.visibility = View.VISIBLE
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.black))

                var visibilityCount = 0
                if (model.targetCustomersCount != null && model.targetCustomersCount != 0) {
                    binding.tvCustomerCountForBeat.text = "${model.targetCustomersCount}"
                    binding.tvVisitedCount.text = "${model.achievedCustomersCount ?: 0}"
                    binding.groupCustomerCount.visibility = View.VISIBLE
                    visibilityCount++
                } else {
                    binding.groupCustomerCount.visibility = View.GONE
                }

                if (model.targetLeadsCount != null && model.targetLeadsCount != 0) {
                    binding.tvNewLeadCountForBeat.text = "${model.targetLeadsCount}"
                    binding.tvNewLeadVisitedCount.text = "${model.achievedLeadsCount ?: 0}"
                    binding.groupNewLeadCount.visibility = View.VISIBLE
                    visibilityCount++
                } else {
                    binding.groupNewLeadCount.visibility = View.GONE
                }

                if (visibilityCount == 0) {
                    binding.groupHeading.visibility = View.GONE
                }
            }

            itemView.setOnClickListener {
                listener.onBeatPlanInfo(model, position)
            }
        }
    }

    interface IBeatPlanListInfoListener {
        fun onBeatPlanInfo(model: BeatRouteDayListModel, position: Int)
    }
}
