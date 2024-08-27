package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.databinding.StaffItemBinding
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.model_kt.BeatListDataItem


class BeatFilterAdapter( ) : RecyclerView.Adapter<BeatFilterAdapter.BeatFilterViewHolder>() {
	
	private val selectedBeatList : MutableSet<Int> = mutableSetOf()
	private var data: ArrayList<BeatListDataItem> = arrayListOf()
	
	
	override fun getItemCount(): Int {
		return data.size
	}
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : BeatFilterViewHolder {
		return BeatFilterViewHolder(StaffItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder : BeatFilterViewHolder, position : Int) {
		try {
			holder.bindItem(data[position])
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	
	inner class BeatFilterViewHolder(itemView: StaffItemBinding) : RecyclerView.ViewHolder(itemView.root) {
		val binding = itemView
		
		@SuppressLint("SetTextI18n")
		fun bindItem(data : BeatListDataItem) {
			
			try {
				binding.staffName.text = data.name
				
				/*if (data.parentCustomerLogoImageUrl.isNullOrEmpty().not()) {
					ImageUtils.loadImage(data.parentCustomerLogoImageUrl, binding.imgStaff)
				} else {
					binding.imgStaff.setImageResource(R.mipmap.no_photo_available)
				}*/
				
				binding.imgStaff.hideView()
				
				binding.checkBox.isChecked = selectedBeatList.contains(data.id)
				binding.checkBox.disable()
				binding.root.setSafeOnClickListener{
					binding.checkBox.isChecked = binding.checkBox.isChecked.not()
					if (binding.checkBox.isChecked){
						data.id?.let { selectedBeatList.add(it) }
					}else{
						selectedBeatList.remove(data.id)
					}
				}
			} catch (e: Exception) {
				println(e.message)
			}
		}
	}
	
	fun getSelectedBeatList(): MutableSet<Int> {
		return selectedBeatList
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelectedBeats(list : MutableSet<Int>){
		selectedBeatList.addAll(list)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setBeats(data: MutableList<BeatListDataItem>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addBeats(data: MutableList<BeatListDataItem>) {
		this.data.addAll(data)
		notifyItemRangeInserted((this.data.size- data.size),data.size)
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	
}
