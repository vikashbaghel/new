package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomerFilterItemBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.customer.CustomerData

class CustomerAdapter(
    private var data: ArrayList<CustomerData>?, private var listener: OnItemCheckCustomerListener,
    private val checkboxCustomerMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_filter_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            (holder as MyViewHolder).bindItem(data!![position], listener, checkboxCustomerMap)
        } catch (e: Exception) {
            Log.e("CustomLinearLayoutManager", "$position")
        }


    }


    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = CustomerFilterItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bindItem(
            data: CustomerData,
            listener: OnItemCheckCustomerListener,
            checkboxCustomerMap: HashMap<Int, Boolean>
        ) {
            binding.customerName.text = data.name
            binding.customerName.tag = data.id

            if (data.logoImageUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(data.logoImageUrl.toString(), binding.imgCustomer)
            } else {
                binding.imgCustomer.setImageResource(R.mipmap.no_photo_available)
            }

            binding.checkBox.isChecked = checkboxCustomerMap[binding.customerName.tag] ?: false
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemCustomerCheck(data.id!!)

                } else {
                    listener.onItemCustomerUncheck(data.id!!)

                }
                checkboxCustomerMap[binding.customerName.tag.toString().toInt()] = isChecked
            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

interface OnItemCheckCustomerListener {
    fun onItemCustomerCheck(id: Int?)
    fun onItemCustomerUncheck(id: Int?)
}