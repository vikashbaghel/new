package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
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


class ChooseCustomerForBeatAdapter(
    private var data: ArrayList<CustomerData>,
    private var listener: IAssignCustomerListener,
    private var isRetailer: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_list_for_beat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener, isRetailer)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCustomerListForBeatBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: CustomerData,
            position: Int,
            listener: IAssignCustomerListener,
            isRetailer: Boolean
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

            if (model.customerCount != null && model.customerCount != 0 && isRetailer) {
                binding.view1.visibility = View.VISIBLE
                binding.tvCustomerCount.visibility = View.VISIBLE

                var customerSubLevel = ""
                when (model.customerLevel) {
                    AppConstant.CUSTOMER_LEVEL_1 -> customerSubLevel =
                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)

                    AppConstant.CUSTOMER_LEVEL_2 -> customerSubLevel =
                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                }

                binding.tvCustomerCount.text = itemView.resources.getString(
                    R.string.retailer_count_for_beat,
                    "${model.customerCount}", customerSubLevel
                )

                binding.tvCustomerCount.paintFlags =
                    binding.tvCustomerCount.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                binding.tvCustomerCount.setOnClickListener {
                    listener.chooseRetailer(model, position)
                }
            } else {
                binding.view1.visibility = View.INVISIBLE
                binding.tvCustomerCount.visibility = View.GONE
            }

            if (model.isPartOfParentCustomer == true) {
                binding.tvParentCustomerName.visibility = View.VISIBLE
                binding.tvParentCustomerName.text = "${
                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                } : ${model.customerParentName}"
            } else {
                binding.tvParentCustomerName.visibility = View.GONE
            }

            binding.cbCustomerName.setOnCheckedChangeListener(null)

            binding.cbCustomerName.isChecked = model.isSelected == true

            binding.mainContent.setOnClickListener {
                binding.cbCustomerName.isChecked = binding.cbCustomerName.isChecked.not()
                model.isSelected = binding.cbCustomerName.isChecked
            }

            binding.cbCustomerName.setOnCheckedChangeListener { _, isChecked ->
                model.isSelected = isChecked
                listener.setCustomerSelect(isChecked, model)
            }
        }
    }


  /*   fun onViewRecycled(holder: MyViewHolder) {
        holder.binding.cbCustomerName.setOnCheckedChangeListener(null)
        super.onViewRecycled(holder)
    }*/

    interface IAssignCustomerListener {
        fun setCustomerSelect(checked: Boolean, customerData: CustomerData)
        fun chooseRetailer(model: CustomerData, position: Int) {}
    }
}