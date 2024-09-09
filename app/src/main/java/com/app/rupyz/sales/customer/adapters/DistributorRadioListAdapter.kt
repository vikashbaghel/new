package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewRadioWithImageBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.helper.asBitmap
import com.app.rupyz.generic.helper.invisibleView
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.customer.CustomerData

class DistributorRadioListAdapter(val showRadio : Boolean = true) :
    RecyclerView.Adapter<DistributorRadioListAdapter.DistributorRadioListViewHolder>() {

    private val distributorList: MutableList<CustomerData> = mutableListOf()
    private var selectedPosition: Int = 0
    private lateinit var onItemSelectListener: (CustomerData) -> Unit
    
    

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DistributorRadioListViewHolder {
        val binding = ItemViewRadioWithImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if(showRadio){
            binding.rbRadioButton.showView()
        }else{
            binding.rbRadioButton.invisibleView()
        }
        return DistributorRadioListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return distributorList.size
    }

    override fun onBindViewHolder(holder: DistributorRadioListViewHolder, position: Int) {
        holder.bind(distributorList[position], position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCustomerList(customerList: MutableList<CustomerData>) {
        distributorList.clear()
        distributorList.addAll(customerList)
        notifyDataSetChanged()
    }

    fun addCustomer(customerList: MutableList<CustomerData>) {
        distributorList.addAll(customerList)
        notifyItemRangeInserted((distributorList.size - customerList.size), customerList.size)
    }


    inner class DistributorRadioListViewHolder(itemView: ItemViewRadioWithImageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView

        @SuppressLint("NotifyDataSetChanged")
        fun bind(customerDats: CustomerData, position: Int) {
            binding.rbRadioButton.isChecked = (selectedPosition == position)
            if (customerDats.logoImageUrl.isNullOrBlank()) {
                val name = StringHelper.printName(customerDats.name)
                binding.ivDistributorProfilePic.setImageBitmap(
                    name.trim().substring(0, (Math.min(name.length, 2))).uppercase().asBitmap(
                        binding.root.context,
                        16f,
                        Color.WHITE,
                        binding.root.context.resources.getColor(R.color.theme_color, null)
                    )
                )
            } else {
                ImageUtils.loadImage(customerDats.logoImageUrl, binding.ivDistributorProfilePic)
            }
            binding.tvDistributorName.text = customerDats.name
            binding.root.setSafeOnClickListener {
                selectedPosition = position
                if (showRadio){
                    notifyDataSetChanged()
                }
                if (::onItemSelectListener.isInitialized) {
                    onItemSelectListener.invoke(customerDats)
                }
            }
            
            if (position == distributorList.size - 1){
                binding.divider.invisibleView()
            }
            
        }
    }
    
    fun setOnItemSelectListener(onItemSelectListener: (CustomerData) -> Unit) {
        this.onItemSelectListener = onItemSelectListener
    }


    fun getSelectedDistributor(): CustomerData? {
        return if (selectedPosition == -1 || selectedPosition >= distributorList.size) {
            null
        } else {
            distributorList[selectedPosition]
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}