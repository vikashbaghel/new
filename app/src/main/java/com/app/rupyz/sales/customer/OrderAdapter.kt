package com.app.rupyz.sales.customer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewOrderListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.helper.asBitmap
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.home.OrderStatusActionListener

class OrderAdapter(private var data : ArrayList<OrderData>, private var mContext : Context, private var listener : OrderStatusActionListener, private var hasInternetConnection : Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	
	override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_order_list, parent, false)
		return MyViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder : RecyclerView.ViewHolder, position : Int) {
		(holder as MyViewHolder).bindItem(data[position], position, mContext, listener, hasInternetConnection)
	}
	
	override fun getItemCount() : Int {
		return data.size
	}
	
	class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
		val binding = ItemViewOrderListBinding.bind(itemView)
		
		fun bindItem(model : OrderData, position : Int, context : Context, listener : OrderStatusActionListener, hasInternetConnection : Boolean) {
			
			
			binding.tvCustomerName.text = model.customer?.name?.replaceFirstChar(Char::titlecase)
			try{
				binding.ivUserProfileImage.setImageBitmap(StringHelper.printName(model.customer?.name).trim().substring(0, (Math.min(model.customer?.name?.length?:0, 2))).uppercase().asBitmap(binding.root.context, 16f, Color.WHITE, binding.root.context.resources.getColor(R.color.theme_color, null)))
			}catch (e: Exception){
				log(e.toString())
			}
			
			binding.tvOrderDate.text = DateFormatHelper.getOrderDate(model.createdAt)
			
			binding.tvOrderPrice.text = CalculatorHelper().convertLargeAmount(model.totalAmount ?: 0.0, AppConstant.TWO_DECIMAL_POINTS)
			
			binding.tvOrderStatus.text = buildString {
				append(model.deliveryStatus)
				append(" ")
				append(context.resources.getString(R.string.order))
			}
			
			itemView.setOnClickListener { listener.onGetOrderInfo(model, position) }
			
			
		}
	}
}