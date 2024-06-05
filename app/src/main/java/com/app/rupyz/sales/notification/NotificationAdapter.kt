package com.app.rupyz.sales.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.NotificationItemsBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.model_kt.NotificationListItemModel

class NotificationAdapter(
    var list: List<NotificationListItemModel>,
    var listener: INotificationActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notification_items, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(list[position], position, listener)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NotificationItemsBinding.bind(itemView)
        fun bindItem(model: NotificationListItemModel, position: Int, listener: INotificationActionListener) {
            binding.tvTitle.text = model.title
            binding.tvTime.text = DateFormatHelper.getMonthDate(model.createdAt)
            binding.tvDescription.text = model.description

            if (model.isSeen == true) {
                binding.mainContent.setBackgroundColor(itemView.context.resources.getColor(R.color.transparent))
            } else {
                binding.mainContent.setBackgroundColor(itemView.context.resources.getColor(R.color.white))
            }

            binding.mainContent.setOnClickListener {
                listener.navigateNotificationScreen(model, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface INotificationActionListener {
        fun navigateNotificationScreen(model: NotificationListItemModel, position: Int)
    }
}