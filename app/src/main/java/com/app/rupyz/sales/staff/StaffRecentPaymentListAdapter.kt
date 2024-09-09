package com.app.rupyz.sales.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemStaffAmountReceiveBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.model_kt.order.order_history.OrderData

class StaffRecentPaymentListAdapter(
    private var data: ArrayList<OrderData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_amount_receive, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStaffAmountReceiveBinding.bind(itemView)
        fun bindItem(model: OrderData, position: Int) {
            binding.tvAmount.text = "" + model.amount
            binding.tvCustomerName.text = "" + model.customer?.name
            binding.tvTime.text = DateFormatHelper.getOrderDate(model.createdAt)
            binding.tvModeOfPayment.text = model.paymentDetails?.paymentMode

        }
    }
}