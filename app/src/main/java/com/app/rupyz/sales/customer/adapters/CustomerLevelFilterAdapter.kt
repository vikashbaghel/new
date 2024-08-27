package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.databinding.StaffItemBinding
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.setSafeOnClickListener


class CustomerLevelFilterAdapter() : RecyclerView.Adapter<CustomerLevelFilterAdapter.CustomerLevelFilterViewHolder>() {
	
	private val selectedCustomerLevel : MutableSet<String> = mutableSetOf()
	private var data : ArrayList<Pair<String, String>> = arrayListOf()
	
	override fun getItemCount(): Int {
		return data.size
	}
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CustomerLevelFilterViewHolder {
		return CustomerLevelFilterViewHolder(StaffItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder : CustomerLevelFilterViewHolder, position : Int) {
		try {
			holder.bindItem(data[position])
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	
	inner class CustomerLevelFilterViewHolder(itemView: StaffItemBinding) : RecyclerView.ViewHolder(itemView.root) {
		val binding = itemView
		
		@SuppressLint("SetTextI18n")
		fun bindItem(data : Pair<String, String>) {
			
			try {
				binding.staffName.text = data.second
				
				binding.imgStaff.hideView()
				
				binding.checkBox.isChecked = selectedCustomerLevel.contains(data.first)
				binding.checkBox.disable()
				binding.root.setSafeOnClickListener{
					binding.checkBox.isChecked = binding.checkBox.isChecked.not()
					if (binding.checkBox.isChecked){
						data.let { selectedCustomerLevel.add(it.first) }
					}else{
						selectedCustomerLevel.remove(data.first)
					}
				}
				
			} catch (e: Exception) {
				println(e.message)
			}
		}
	}
	
	fun getSelectedCustomerLevel(): MutableSet<String> {
		return selectedCustomerLevel
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelectedCustomerLevel(list : MutableSet<String>){
		selectedCustomerLevel.addAll(list)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setCustomerLevel(data: MutableList<Pair<String, String>>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addCustomerLevel(data: MutableList<Pair<String, String>>) {
		this.data.addAll(data)
		notifyItemRangeInserted((this.data.size- data.size),data.size)
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	
}
