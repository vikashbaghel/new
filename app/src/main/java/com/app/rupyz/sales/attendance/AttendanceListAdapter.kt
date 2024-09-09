package com.app.rupyz.sales.attendance

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAttendanceListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AttendanceDataItem

class AttendanceListAdapter(
    private var data: ArrayList<AttendanceDataItem>,
    private var listener: IAttendanceActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemAttendanceListBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: AttendanceDataItem,
            position: Int,
            listener: IAttendanceActionListener
        ) {

            val containerParams = binding.tvDay.layoutParams as ConstraintLayout.LayoutParams

            binding.tvDate.text = model.calenderDate
            binding.tvDay.text = model.weekDay

            if (model.isWeekEnd == true) {
                binding.tvAttendanceMark.visibility = View.INVISIBLE
                binding.tvWeekend.visibility = View.VISIBLE
                binding.clAttendanceEntry.visibility = View.GONE
                binding.ivMore.visibility = View.GONE
            } else {
                binding.tvAttendanceMark.visibility = View.VISIBLE
                binding.tvWeekend.visibility = View.GONE
                binding.clAttendanceEntry.visibility = View.VISIBLE
                binding.ivMore.visibility = View.VISIBLE
            }


            if (!model.comments.isNullOrEmpty()) {
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.tvDay.layoutParams = containerParams

                binding.tvRemark.visibility = View.VISIBLE
                binding.tvRemark.text = model.comments
            } else {
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.tvDay.layoutParams = containerParams
                binding.tvRemark.visibility = View.GONE
            }

            if (!model.timeIn.isNullOrEmpty()) {
                binding.tvCheckInTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.timeIn)
            } else {
                binding.tvCheckInTime.text =
                    itemView.resources.getString(R.string.not_available)
            }

            if (!model.timeOut.isNullOrEmpty()) {
                binding.tvCheckOutTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.timeOut)
            } else {
                binding.tvCheckOutTime.text =
                    itemView.resources.getString(R.string.not_available)
            }


            if (!model.attendanceType.isNullOrEmpty()) {
                binding.tvAttendanceMark.visibility = View.VISIBLE
                binding.tvAttendanceMark.text =
                    AppConstant.getAttendanceStatus(model.attendanceType!!)

                binding.tvAttendanceMark.setTextColor(itemView.context.getColor(R.color.white))
                if (model.attendanceType.equals(AppConstant.ATTENDANCE_TYPE_PRESENT)) {
                    binding.tvAttendanceMark.setBackgroundResource(R.drawable.attendance_present_background)
                } else if (model.attendanceType.equals(AppConstant.ATTENDANCE_TYPE_CASUAL_LEAVE)) {
                    binding.tvAttendanceMark.setTextColor(itemView.context.getColor(R.color.red))
                    binding.tvAttendanceMark.setBackgroundResource(R.drawable.attendance_absent_background)
                } else {
                    binding.tvAttendanceMark.setBackgroundResource(R.drawable.attendance_half_day_background)
                }
            } else {
                binding.tvAttendanceMark.visibility = View.INVISIBLE
            }

            if (model.totalTime != null && model.totalTime != 0) {
                binding.tvTotalDurationTime.text = DateFormatHelper.getTime(model.totalTime)
            } else {
                binding.tvTotalDurationTime.text =
                    itemView.resources.getString(R.string.duration_zero)
            }


            binding.ivMore.setOnClickListener { v ->
                //creating a popup menu
                val popup =
                    PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.customer_action_menu)

                popup.menu.getItem(1).isVisible = false

                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_product -> {
                            listener.editAttendance(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }
        }
    }

    interface IAttendanceActionListener {
        fun editAttendance(model: AttendanceDataItem, position: Int)
    }
}