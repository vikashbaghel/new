package com.app.rupyz.sales.reminder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemReminderListBinding
import com.app.rupyz.databinding.ReminderDateItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.DateItem
import com.app.rupyz.model_kt.ReminderItemModel
import java.text.SimpleDateFormat
import java.util.*

class DateWiseReminderListAdapter(
    private var data: ArrayList<SortReminderListByDate>,
    private var listener: IReminderListDetailsListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SortReminderListByDate.TYPE_DATE) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.reminder_date_item, parent, false)
            DateViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reminder_list, parent, false)
            MyViewHolder(itemView)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            SortReminderListByDate.TYPE_DATE -> (holder as DateViewHolder).bind(data[position] as DateItem)
            SortReminderListByDate.TYPE_GENERAL -> (holder as MyViewHolder).bindItem(
                data[position] as ReminderItemModel, listener
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    class DateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ReminderDateItemBinding.bind(itemView)

        fun bind(model: DateItem) {
            binding.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                model.date,
                SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)
            )
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemReminderListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: ReminderItemModel,
            listener: IReminderListDetailsListener
        ) {
            binding.tvCustomerName.text = model.businessName
            if (model.logoImageUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.logoImageUrl, binding.ivCustomer)
            } else {
                binding.ivCustomer.setImageResource(R.mipmap.ic_no_customer_found)
            }

            binding.ivCustomer.setOnClickListener {
                listener.viewCustomerPhoto(model)
            }
            var label = ""

            when (model.moduleType) {

                AppConstant.CUSTOMER_FEEDBACK -> {
                    label = "${AppConstant.CUSTOMER}  - ${model.feedbackType}"
                }
                AppConstant.LEAD_FEEDBACK -> {
                    label = "${AppConstant.LEAD} - ${model.feedbackType}"
                }

                AppConstant.ORDER_DISPATCH -> {
                    label = "${AppConstant.CUSTOMER}  - ${model.feedbackType}"
                }
                AppConstant.PAYMENT -> {
                    label = "${AppConstant.CUSTOMER}  - ${model.feedbackType}"
                }
                else -> {
                    label = "${model.feedbackType}"
                }
            }

            binding.tvActivityType.text = label

            if (model.comments.isNullOrEmpty().not()) {
                binding.tvComment.visibility = View.VISIBLE
                binding.tvComment.text = model.comments
            } else {
                binding.tvComment.visibility = View.GONE
            }

            binding.tvTime.text = DateFormatHelper.convertDateToTimeFormat(model.dueDatetime)

            itemView.setOnClickListener { listener.onGetReminderDetails(model) }
        }
    }

    interface IReminderListDetailsListener {
        fun onGetReminderDetails(model: ReminderItemModel)
        fun viewCustomerPhoto(model: ReminderItemModel)
    }
}