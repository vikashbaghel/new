package com.app.rupyz.sales.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAddressListBinding
import com.app.rupyz.model_kt.CustomerAddressDataItem

class AddressListAdapter(
        private var data: ArrayList<CustomerAddressDataItem>,
        private var listener: AddressListener
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemAddressListBinding.bind(itemView)

        fun bindItem(model: CustomerAddressDataItem, position: Int, listener: AddressListener) {
            binding.tvCompanyName.text = model.name
            binding.tvAddress.text =
                    model.addressLine1 + ", " + model.city + ", " + model.state + ", " + model.pincode

            binding.mainContent.setOnClickListener { listener.onSelectAddress(model, adapterPosition) }

            binding.tvPrimary.isVisible = model.isDefault ?: false

            if (model.isSelected) {
                if (model.isDefault == true) {
                    binding.mainContent.setBackgroundResource(R.drawable.primary_address_selection_background)
                } else {
                    binding.mainContent.setBackgroundResource(R.drawable.address_selection_background)
                }
                binding.ivRadioButton.setImageResource(R.drawable.ic_radio_button_selected)
            } else {
                if (model.isDefault == true) {
                    binding.mainContent.setBackgroundResource(R.drawable.white_background_10dp_corner_for_primary)
                } else {
                    binding.mainContent.setBackgroundResource(R.drawable.white_background_10dp_corner)
                }
                binding.ivRadioButton.setImageResource(R.drawable.ic_radio_button_not_selected)
            }

            binding.ivEdit.setOnClickListener { listener.onEditAddress(model, position) }
        }
    }

    interface AddressListener {
        fun onSelectAddress(model: CustomerAddressDataItem, position: Int)
        fun onEditAddress(model: CustomerAddressDataItem, position: Int)
    }
}