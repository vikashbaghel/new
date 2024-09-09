package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomerFilterItemBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.AdminData
import com.app.rupyz.model_kt.order.customer.CustomerData

class OrderTakenAdminAdapter(
    private var data: ArrayList<AdminData>?, private var listener: OnItemCheckAdminListener,
    private val checkboxAdminMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_filter_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            (holder as MyViewHolder).bindItem(data!![position], listener, checkboxAdminMap)
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
            data: AdminData,
            listener: OnItemCheckAdminListener,
            checkboxAdminMap: HashMap<Int, Boolean>
        ) {
            binding.customerName.text = data.name
            binding.customerName.tag = data.userId

            if (data.pic_url.isNullOrEmpty().not()) {
                ImageUtils.loadImage(data.pic_url.toString(), binding.imgCustomer)
            } else {
                binding.imgCustomer.setImageResource(R.mipmap.no_photo_available)
            }

            binding.checkBox.isChecked = checkboxAdminMap[binding.customerName.tag] ?: false
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemAdminCheck(data.userId!!)

                } else {
                    listener.onItemAdminUncheck(data.userId!!)

                }
                checkboxAdminMap[binding.customerName.tag.toString().toInt()] = isChecked
            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

interface OnItemCheckAdminListener {
    fun onItemAdminCheck(id: Int?)
    fun onItemAdminUncheck(id: Int?)
}