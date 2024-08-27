package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.FiltergallerypicsItemBinding
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.model_kt.CustomerFilterData

class CustomerFilterTypeAdapter (private var data: ArrayList<CustomerFilterData>? ) : RecyclerView.Adapter<CustomerFilterTypeAdapter.CustomerFilterTypeViewHolder>() {
	
	private lateinit var onDebounceClickListener : (position : Int, data : CustomerFilterData) -> Unit
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CustomerFilterTypeViewHolder {
		return CustomerFilterTypeViewHolder(FiltergallerypicsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder : CustomerFilterTypeViewHolder, position : Int) {
		holder.bindItem(data!![position])
	}
	
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	override fun getItemCount(): Int {
		return data!!.size
	}
	
	inner class CustomerFilterTypeViewHolder(itemView: FiltergallerypicsItemBinding) : RecyclerView.ViewHolder(itemView.root) {
		val binding = itemView
		@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
		fun bindItem(filterTypeData: CustomerFilterData) {
			binding.txtName.text = filterTypeData.name
			
			if (filterTypeData.isSelected) {
				binding.txtName.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.white, null))
				binding.txtName.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
			}
			else {
				binding.txtName.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(R.color.color_F4F4F4, null))
				binding.txtName.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_727176))
			}
			binding.txtName.setSafeOnClickListener {
				data?.forEach {
					it.isSelected = false
				}
				filterTypeData.isSelected = filterTypeData.isSelected.not()
				if (::onDebounceClickListener.isInitialized){
					onDebounceClickListener.invoke(adapterPosition, filterTypeData)
				}
				notifyDataSetChanged()
			}
		}
	}
	
	
	fun setOnDebounceClickListener(onDebounceClickListener : (position : Int, data : CustomerFilterData) -> Unit) {
		this.onDebounceClickListener = onDebounceClickListener
	}
	
}
