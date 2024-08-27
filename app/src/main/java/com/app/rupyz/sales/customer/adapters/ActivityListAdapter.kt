package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewActivityBinding
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.CustomerFeedbackStringItem

class ActivityListAdapter : RecyclerView.Adapter<ActivityListAdapter.ActivityListVieHolder>()  {
	
	private val activityTypeList : ArrayList<CustomerFeedbackStringItem> = ArrayList()
	private var onActivitySelectListener : ((CustomerFeedbackStringItem) -> Unit)? = null
	
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ActivityListVieHolder {
		return  ActivityListVieHolder(ItemViewActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun getItemCount() : Int {
		return activityTypeList.size
	}
	
	override fun onBindViewHolder(holder : ActivityListVieHolder, position : Int) {
		if (activityTypeList.size > position){
			holder.bind(activityTypeList[position])
		}
	}
	
	
	inner class ActivityListVieHolder(itemView : ItemViewActivityBinding) : RecyclerView.ViewHolder(itemView.root){
		val binding = itemView
		fun bind(customerFeedbackStringItem  : CustomerFeedbackStringItem){
			when (customerFeedbackStringItem.id) {
				/***
				 * Check Out Activity  Static ID
				 * **/
				-999   -> {
					binding.ivActivityIcon.setImageResource(R.drawable.ic_checkout)
					binding.tvActivityName.text = itemView.context.resources.getString(R.string.checkout)
					binding.tvActivityName.setTextColor(binding.root.context.getColor(R.color.red))
					binding.bottomDivider.hideView()
				}
				/***
				 * Create  Order Activity  Static ID
				 * **/
				999909    -> {
					binding.ivActivityIcon.setImageResource(R.drawable.ic_new_order)
					binding.tvActivityName.text = itemView.context.resources.getString(R.string.new_order)
					binding.tvActivityName.setTextColor(binding.root.context.getColor(R.color.color_000000))

				}
				/***
				 * No Order  Activity  Static ID
				 * **/
				999919    -> {
					binding.ivActivityIcon.setImageResource(R.drawable.ic_no_order)
					binding.tvActivityName.text = itemView.context.resources.getString(R.string.no_order)
					binding.tvActivityName.setTextColor(binding.root.context.getColor(R.color.color_000000))

				}
				/***
				 * Record New Payment  Activity  Static ID
				 * **/
				999989    -> {
					binding.ivActivityIcon.setImageResource(R.drawable.ic_new_payment)
					binding.tvActivityName.text = itemView.context.resources.getString(R.string.new_payment)
					binding.tvActivityName.setTextColor(binding.root.context.getColor(R.color.color_000000))

				}
				/***
				 * Dynamic Activity
				 * **/
				else -> {
					binding.ivActivityIcon.setImageResource(R.drawable.no_order)
					//ImageUtils.loadImage(url = customerFeedbackStringItem.icon, imageView = binding.ivActivityIcon)
					binding.tvActivityName.text = customerFeedbackStringItem.stringValue
					binding.tvActivityName.setTextColor(binding.root.context.getColor(R.color.color_000000))

				}
			}
			binding.root.setOnClickListener {
				onActivitySelectListener?.invoke(customerFeedbackStringItem)
			}
		}
	}
	
	
	@SuppressLint("NotifyDataSetChanged")
	fun setActivities(activities : ArrayList<CustomerFeedbackStringItem>){
		this.activityTypeList.clear()
		this.activityTypeList.addAll(activities)
		notifyDataSetChanged()
	}
	
	fun setActivitySelectListener(onActivitySelectListener : ((CustomerFeedbackStringItem) -> Unit)){
		this.onActivitySelectListener = onActivitySelectListener
	}
}