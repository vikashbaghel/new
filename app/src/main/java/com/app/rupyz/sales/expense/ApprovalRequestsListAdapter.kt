package com.app.rupyz.sales.expense

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ApprovalRequestListItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.ExpenseTrackerDataItem


class ApprovalRequestsListAdapter(
    private var data: ArrayList<ExpenseTrackerDataItem>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.approval_request_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ApprovalRequestListItemBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: ExpenseTrackerDataItem) {


            binding.tvStaffName.text = model.createdByName
            binding.tvExpenseName.text = model.name

            if (!model.endDateTime.isNullOrEmpty()) {
                binding.tvExpenseDate.text =
                    DateFormatHelper.getMonthDate(model.startDateTime) + " - " + DateFormatHelper.getMonthDate(
                        model.endDateTime
                    )
            } else {
                binding.tvExpenseDate.text =
                    DateFormatHelper.getMonthDate(model.startDateTime)
            }

            binding.tvItemsAdded.text = "${model.totalItems}"
            binding.tvExpenseCaptured.text =
                "${CalculatorHelper().convertCommaSeparatedAmount(model.totalAmount, AppConstant.TWO_DECIMAL_POINTS)}"

            binding.tvExpenseStatus.text = model.status

            if (model.isResubmitted == true) {
                binding.tvExpenseReSubmitted.visibility = View.VISIBLE
            } else {
                binding.tvExpenseReSubmitted.visibility = View.GONE
            }

            if (model.status == AppConstant.REJECTED) {
                binding.tvExpenseStatus.setBackgroundResource(R.drawable.expense_tracker_rejected_background)
                binding.tvExpenseStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_rejected_text_color))

                if (model.comments != null) {
                    binding.tvRejectedReason.visibility = View.VISIBLE
                    binding.tvRejectedReason.text = model.comments
                }
            } else if (model.status == AppConstant.PENDING_EXPENSE) {
                binding.tvExpenseStatus.setBackgroundResource(R.drawable.expense_tracker_pending_background)
                binding.tvExpenseStatus.setTextColor(itemView.context.resources.getColor(R.color.white))
            } else {
                binding.tvRejectedReason.visibility = View.GONE
                binding.tvExpenseStatus.setBackgroundResource(R.drawable.expense_tracker_green_background)
                binding.tvExpenseStatus.setTextColor(itemView.context.resources.getColor(R.color.white))
            }

        }
    }
}