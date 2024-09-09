package com.app.rupyz.sales.address

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAddressListBinding
import com.app.rupyz.databinding.MenuEditBinding
import com.app.rupyz.generic.utils.AppConstant
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
                buildString {
                    append(model.addressLine1)
                    append(", ")
                    append(model.city)
                    append(", ")
                    append(model.state)
                    append(", ")
                    append(model.pincode)
                }

            binding.mainContent.setOnClickListener {
                listener.onSelectAddress(
                    model,
                    adapterPosition
                )
            }

            binding.tvPrimary.isVisible = model.isDefault

            /*if (model.isSelected) {
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
            }*/
            
            binding.ivEdit.setOnClickListener {
                //creating a popup menu
                val elevation = binding.mainContent.elevation
                val translationZ = binding.mainContent.translationZ
//            val popup = PopupMenu(this, binding.ivShippingAddressMoreOptions, Gravity.NO_GRAVITY)
                // Inflate a custom view for the PopupWindow
                val view = MenuEditBinding.inflate(LayoutInflater.from(binding.root.context)) // Inflate your custom layout
                
                // Create the PopupWindow
                val popupWindow = PopupWindow(view.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                
                // Set elevation for the PopupWindow
                popupWindow.elevation = elevation
                binding.mainContent.elevation = 0f
                binding.mainContent.translationZ = 0f
                //inflating menu from xml resource\
                
                //adding click listener
                view.edit.setOnClickListener {
                    popupWindow.dismiss()
                    listener.onEditAddress(model, position)
                }
                
                popupWindow.setOnDismissListener {
                    binding.mainContent.elevation = elevation
                    binding.mainContent.translationZ = translationZ
                }
                
                //displaying the popup
                popupWindow.showAsDropDown(binding.ivEdit)
                
            }
            
           /* binding.ivEdit.setOnClickListener { listener.onEditAddress(model, position) }*/
        }
    }

    interface AddressListener {
        fun onSelectAddress(model: CustomerAddressDataItem, position: Int)
        fun onEditAddress(model: CustomerAddressDataItem, position: Int)
    }
}