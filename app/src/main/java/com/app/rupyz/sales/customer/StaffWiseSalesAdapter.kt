package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCustomerWiseSalesBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.StaffWiseSalesDataItem

class StaffWiseSalesAdapter(
        private var data: ArrayList<StaffWiseSalesDataItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_customer_wise_sales, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCustomerWiseSalesBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: StaffWiseSalesDataItem, position: Int) {
            if (model.userName != null && model.userName != "") {
                binding.tvCustomer.text = model.userName
            } else {
                binding.tvCustomer.text = "Admin"
            }
            binding.tvOrders.text = "" + model.totalCountOrders

            binding.tvSaleValue.text =
                    (CalculatorHelper().convertLargeAmount(model.totalAmountSales!!, AppConstant.TWO_DECIMAL_POINTS))

            binding.tvOrderPercentage.text =
                    (CalculatorHelper().convertLargeAmount(model.totalAmountdispatched!!, AppConstant.TWO_DECIMAL_POINTS))
        }
    }
}