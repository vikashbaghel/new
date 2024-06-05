package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCustomerListForBeatBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.customer.CustomerData


class MappedCustomerInfoForTransferAdapter(
        private var data: ArrayList<CustomerData>
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_customer_list_for_beat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCustomerListForBeatBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: CustomerData,
                position: Int
        ) {
            binding.tvCustomerName.text = model.name

            if (model.logoImageUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.logoImageUrl, binding.ivCustomer)
            }

            binding.tvLocation.text = model.city

            binding.tvAuthorizePersonName.text = model.contactPersonName

            if (model.customerLevel.isNullOrEmpty().not()) {
                binding.tvCustomerLevel.visibility = View.VISIBLE
                binding.tvCustomerLevel.text =
                        SharedPref.getInstance().getString(model.customerLevel)

                when (model.customerLevel) {
                    AppConstant.CUSTOMER_LEVEL_1 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(
                                        itemView.resources.getColor(R.color.customer_level_one_background)
                                )
                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_one_text_color))
                    }

                    AppConstant.CUSTOMER_LEVEL_2 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(itemView.resources.getColor(R.color.customer_level_two_background))
                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_two_text_color))
                    }

                    AppConstant.CUSTOMER_LEVEL_3 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(
                                        itemView.resources.getColor(R.color.customer_level_three_background)
                                )

                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_three_text_color))
                    }
                }
            } else {
                binding.tvCustomerLevel.visibility = View.GONE
            }

            binding.view1.visibility = View.INVISIBLE
            binding.tvCustomerCount.visibility = View.GONE
            binding.cbCustomerName.visibility = View.GONE
            binding.tvParentCustomerName.visibility = View.GONE
        }
    }
}