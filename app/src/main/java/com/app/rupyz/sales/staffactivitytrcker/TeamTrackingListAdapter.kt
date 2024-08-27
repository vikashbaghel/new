package com.app.rupyz.sales.staffactivitytrcker


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemStaffTrackingInfoBinding
import com.app.rupyz.databinding.StaffTcPcHeaderLayoutBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.StaffTrackingActivityModules
import java.text.DecimalFormat


class TeamTrackingListAdapter(
    private var data: ArrayList<StaffTrackingActivityModules>?,
    private var listener: ITeamTrackingListener,
    private var isHierarchyExist: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val header = 0
    private val item = 1

    private var headerVisible = true

    private var headerText: Triple<Int?, Int?, Double?> = Triple(0, 0, 0.0)

    private var dateRange: String = AppConstant.TODAY
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == header) {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.staff_tc_pc_header_layout, parent, false)
            HeaderViewHolder(view)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_staff_tracking_info, parent, false)
            return MyViewHolder(itemView)
        }

    }

    fun setHeaderText(text: Triple<Int?, Int?, Double?>) {
        headerText = text
        notifyItemChanged(0)
    }

    fun changeDateRange(dateRange: String) {
        this.dateRange = dateRange
        notifyDataSetChanged()
    }

    internal class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = StaffTcPcHeaderLayoutBinding.bind(itemView)
        fun bindItem(pair: Triple<Int?, Int?, Double?>) {
            binding.tvTcCount.text = "${pair.first}"
            binding.tvPcCount.text = "${pair.second}"
            if (pair.third != null && pair.third!!.toInt() != 0) {
                binding.tvOrderValue.visibility = View.VISIBLE
                binding.tvOrderValue.text = CalculatorHelper().convertLargeAmount(
                    pair.third!!,
                    AppConstant.TWO_DECIMAL_POINTS
                )
            } else {
                binding.tvOrderValue.visibility = View.GONE
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bindItem(headerText)
        } else {
            (holder as MyViewHolder).bindItem(
                data!![position - 1],
                listener,
                isHierarchyExist,
                dateRange
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && headerVisible) header else item
    }


    override fun getItemCount(): Int {
        return if (headerVisible) data!!.size + 1 else data!!.size
    }

    private fun getActualItemPosition(position: Int): Int {
        return if (headerVisible) position - 1 else position
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStaffTrackingInfoBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: StaffTrackingActivityModules,
            listener: ITeamTrackingListener,
            isHierarchyExist: Boolean,
            dateRange: String
        ) {

            if (isHierarchyExist.not()) {
                binding.clDistanceTravelled.visibility = View.GONE
            } else {
                binding.clDistanceTravelled.visibility = View.VISIBLE
            }

            binding.mainContent.setPadding(0, 0, 0, 12)

            if (model.staffName.isNullOrEmpty().not()) {
                val staffName: StringBuilder = StringBuilder().append("${model.staffName}")
                binding.tvStaffName.visibility = View.VISIBLE

                if ((model.staffId ?: 0) == SharedPref.getInstance().getString(AppConstant.USER_ID)
                        .toInt()
                ) {
                    staffName.append(" (You)")
                    binding.tvStaffName.text = staffName.toString()
                } else {
                    binding.tvStaffName.text = staffName.toString()
                }
            } else {
                binding.tvStaffName.visibility = View.INVISIBLE
            }

            if (model.isFakeLocationDetected == true) {
                binding.tvFakeLocationDetected.visibility = View.VISIBLE
            } else {
                binding.tvFakeLocationDetected.visibility = View.GONE
            }

            if (model.picUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.picUrl, binding.ivProfile)
            } else {
                binding.ivProfile.setImageResource(R.mipmap.ic_user_default)
            }

            binding.tvStaffMeeting.text = "${model.tcCount}"

            if (model.distanceTravelled != null && model.distanceTravelled != 0.0) {
                val df = DecimalFormat("0.000")
                binding.tvDistanceTravelled.text = df.format(model.distanceTravelled)
            } else {
                binding.tvDistanceTravelled.text = "0"
            }

            if (model.startDay.isNullOrEmpty().not()) {
                binding.tvStaffCheckInTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.startDay)
            } else {
                binding.tvStaffCheckInTime.text =
                    itemView.resources.getString(R.string.empty_duration_for_total_time)
            }

            if (model.endDay.isNullOrEmpty().not()) {
                binding.tvStaffCheckOutTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.endDay)
            } else {
                binding.tvStaffCheckOutTime.text =
                    itemView.resources.getString(R.string.empty_duration_for_total_time)
            }

            if (model.duration != null && model.duration != 0) {
                binding.tvStaffTotalCheckedInTime.visibility = View.VISIBLE
                binding.tvStaffTotalCheckedInTime.text =
                    DateFormatHelper.getTimeInMinFormat(model.duration)
            } else {
                binding.tvStaffTotalCheckedInTime.visibility = View.INVISIBLE
            }

            if (dateRange != AppConstant.TODAY) {
                binding.tvStaffCheckInTime.visibility = View.INVISIBLE
                binding.tvStaffCheckOutTime.visibility = View.INVISIBLE
                binding.tvStaffTotalCheckedInTime.visibility = View.INVISIBLE
            } else {
                binding.tvStaffCheckInTime.visibility = View.VISIBLE
                binding.tvStaffCheckOutTime.visibility = View.VISIBLE
            }

            binding.tvOrderCount.text = "${model.pcCount}"

            binding.tvOrdersAmount.text = CalculatorHelper().convertLargeAmount(
                model.orderCount ?: 0.0, AppConstant.TWO_DECIMAL_POINTS
            )

            binding.clStaffActivity.setOnClickListener {
                listener.getStaffInfo(model)
            }

            binding.clOrderCount.setOnClickListener {
                listener.getStaffInfo(model)
            }

            itemView.setOnClickListener {
                listener.getStaffInfo(model)
            }
        }
    }

    interface ITeamTrackingListener {
        fun getStaffInfo(model: StaffTrackingActivityModules)
    }
}