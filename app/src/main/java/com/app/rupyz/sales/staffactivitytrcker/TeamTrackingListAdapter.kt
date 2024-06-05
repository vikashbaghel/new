package com.app.rupyz.sales.staffactivitytrcker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemStaffTrackingInfoBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import java.text.DecimalFormat

class TeamTrackingListAdapter(
        private var data: ArrayList<StaffTrackingActivityModules>?,
        private var listener: ITeamTrackingListener
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_staff_tracking_info, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], listener)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStaffTrackingInfoBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: StaffTrackingActivityModules, listener: ITeamTrackingListener) {
            binding.clMapView.visibility = View.INVISIBLE
            binding.clDailySalesReport.visibility = View.GONE

            binding.mainContent.setPadding(0, 0, 0, 12)

            if (model.staffName.isNullOrEmpty().not()) {
                binding.tvStaffName.text = "${model.staffName}"
                binding.tvStaffName.visibility = View.VISIBLE
            } else {
                binding.tvStaffName.visibility = View.GONE
            }

            binding.tvStaffMeeting.text = "${model.meetings}"
            if (model.distanceTravelled != null && model.distanceTravelled != 0.0) {
                val df = DecimalFormat("0.000")
                binding.tvDistanceTravelled.text = df.format(model.distanceTravelled)
            } else {
                binding.tvDistanceTravelled.text = "0"
            }

            binding.tvOrderCount.text = "${model.orderCount}"

            binding.tvOrdersAmount.text = CalculatorHelper().convertCommaSeparatedAmount(
                    model.orderAmount, AppConstant.TWO_DECIMAL_POINTS)

            binding.tvLeadCount.text = "${model.leadCount}"

            itemView.setOnClickListener {
                listener.getStaffInfo(model)
            }
        }
    }

    interface ITeamTrackingListener {
        fun getStaffInfo(model: StaffTrackingActivityModules)
    }
}