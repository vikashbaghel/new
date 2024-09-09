package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.databinding.StaffItemBinding
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.setSafeOnClickListener


class CustomerTypeFilterAdapter() : RecyclerView.Adapter<CustomerTypeFilterAdapter.CustomerTypeFilterViewHolder>() {
	
	private val selectedCustomerType : MutableSet<String> = mutableSetOf()
	private var data : ArrayList<String> = arrayListOf()
	
	override fun getItemCount(): Int {
		return data.size
	}
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CustomerTypeFilterViewHolder {
		return CustomerTypeFilterViewHolder(StaffItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder : CustomerTypeFilterViewHolder, position : Int) {
		try {
			holder.bindItem(data[position])
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	
	inner class CustomerTypeFilterViewHolder(itemView: StaffItemBinding) : RecyclerView.ViewHolder(itemView.root) {
		val binding = itemView
		
		@SuppressLint("SetTextI18n")
		fun bindItem(data : String) {
			
			try {
				binding.staffName.text = data
				
				binding.imgStaff.hideView()
				
				binding.checkBox.isChecked = selectedCustomerType.contains(data)
				binding.checkBox.disable()
				binding.root.setSafeOnClickListener{
					binding.checkBox.isChecked = binding.checkBox.isChecked.not()
					if (binding.checkBox.isChecked){
						data.let { selectedCustomerType.add(it) }
					}else{
						selectedCustomerType.remove(data)
					}
				}
				
			} catch (e: Exception) {
				println(e.message)
			}
		}
	}
	
	fun getSelectedCustomerType(): MutableSet<String> {
		return selectedCustomerType
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelectedCustomerType(list : MutableSet<String>){
		selectedCustomerType.addAll(list)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setCustomerType(data: MutableList<String>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addCustomerType(data: MutableList<String>) {
		this.data.addAll(data)
		notifyItemRangeInserted((this.data.size- data.size),data.size)
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	
}
