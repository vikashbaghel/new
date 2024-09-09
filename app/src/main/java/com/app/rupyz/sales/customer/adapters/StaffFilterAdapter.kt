package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffItemBinding
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.sales.StaffData


class StaffFilterAdapter( ) : RecyclerView.Adapter<StaffFilterAdapter.StaffFilterViewHolder>() {
	
	private val selectedStaff : MutableSet<Int> = mutableSetOf()
	private var data: ArrayList<StaffData>  = arrayListOf()
	
	override fun getItemCount(): Int {
		return data.size
	}
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : StaffFilterViewHolder {
		return StaffFilterViewHolder(StaffItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder : StaffFilterViewHolder, position : Int) {
		try {
			holder.bindItem(data[position])
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	
	inner class StaffFilterViewHolder(itemView: StaffItemBinding) : RecyclerView.ViewHolder(itemView.root) {
		val binding = itemView
		
		@SuppressLint("SetTextI18n")
		fun bindItem(data : StaffData) {
			
			try {
				binding.staffName.text = data.name
				binding.staffName.tag = data.user
				
				if (data.profilePicUrl.isNullOrEmpty().not()) {
					ImageUtils.loadImage(data.profilePicUrl, binding.imgStaff)
				} else {
					binding.imgStaff.setImageResource(R.mipmap.no_photo_available)
				}
				
				binding.checkBox.isChecked = selectedStaff.contains(data.id)
				binding.checkBox.disable()
				binding.root.setSafeOnClickListener{
					binding.checkBox.isChecked = binding.checkBox.isChecked.not()
					if (binding.checkBox.isChecked){
						data.id?.let { selectedStaff.add(it) }
					}else{
						selectedStaff.remove(data.id)
					}
				}
				
			} catch (e: Exception) {
				println(e.message)
			}
		}
	}
	
	fun getSelectedStaffList(): MutableSet<Int> {
		return selectedStaff
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelectedStaff(list : MutableSet<Int>){
		selectedStaff.addAll(list)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setStaffList(data: MutableList<StaffData>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addStaffList(data: MutableList<StaffData>) {
		this.data.addAll(data)
		notifyItemRangeInserted((this.data.size- data.size),data.size)
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	
}
