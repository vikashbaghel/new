package com.app.rupyz.sales.expense

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ExpenseTrackerListItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.ExpenseTrackerDataItem

class ExpensesTrackerListAdapter(
        private var data: ArrayList<ExpenseTrackerDataItem>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.expense_tracker_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ExpenseTrackerListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: ExpenseTrackerDataItem) {

            val containerParams =
                    binding.tvItemsAdded.layoutParams as ConstraintLayout.LayoutParams
            containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvItemsAdded.layoutParams = containerParams


            if (model.source.isNullOrEmpty().not()
                    && model.source.equals(AppConstant.ANDROID_OFFLINE_TAG)
                    && model.isSyncedToServer == false) {
                binding.groupOffline.visibility = View.VISIBLE
                binding.tvExpenseStatus.visibility = View.GONE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.tvItemsAdded.layoutParams = containerParams
            } else {
                binding.groupOffline.visibility = View.GONE
                binding.tvExpenseStatus.visibility = View.VISIBLE

                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.tvItemsAdded.layoutParams = containerParams
            }

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

            binding.tvItemsAdded.text = "${model.totalItems ?: 0}"
            binding.tvExpenseCaptured.text =
                    "${CalculatorHelper().convertCommaSeparatedAmount(model.totalAmount ?: 0.0, AppConstant.TWO_DECIMAL_POINTS)}"

            binding.tvExpenseStatus.text = model.status

            if (model.status == AppConstant.REJECTED) {
                binding.tvExpenseStatus.setBackgroundResource(R.drawable.expense_tracker_rejected_background)
                binding.tvExpenseStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_rejected_text_color))

                if (model.comments != null) {
                    binding.tvRejectedReason.visibility = View.VISIBLE
                    binding.tvRejectedReason.text = model.comments
                }
            } else {
                binding.tvRejectedReason.visibility = View.GONE
                binding.tvExpenseStatus.setBackgroundResource(R.drawable.expense_tracker_green_background)
                binding.tvExpenseStatus.setTextColor(itemView.context.resources.getColor(R.color.white))
            }
        }
    }

}