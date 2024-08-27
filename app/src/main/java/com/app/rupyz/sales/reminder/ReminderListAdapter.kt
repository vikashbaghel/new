package com.app.rupyz.sales.reminder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemReminderListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.ReminderItemModel


class ReminderListAdapter(
    private var data: ArrayList<ReminderItemModel>,
    private var listener: IReminderListDetailsListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemReminderListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: ReminderItemModel,
            position: Int,
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

            if (model.comments.isNullOrEmpty().not()){
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