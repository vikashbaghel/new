package com.app.rupyz.ui.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDiscoveryFilterBinding
import com.app.rupyz.model_kt.LocationFilterItem

class LocationFilterAdapter(private var data: ArrayList<LocationFilterItem>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discovery_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDiscoveryFilterBinding.bind(itemView)
        fun bindItem(model: LocationFilterItem, position: Int, onClickListener: OnClickListener) {
            binding.tvItems.text = model.name

            itemView.setOnClickListener {
                model.position = position
                onClickListener.onClick(model)
            }

            if (model.isSelected){
                itemView.setBackgroundResource(R.drawable.details_button_style)
                binding.tvItems.setTextColor(itemView.context.resources.getColor(R.color.white))
            } else{
                itemView.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
                binding.tvItems.setTextColor(itemView.context.resources.getColor(R.color.tab_un_selected_color))
            }
        }

    }

    class OnClickListener(val clickListener: (meme: LocationFilterItem) -> Unit) {
        fun onClick(meme: LocationFilterItem) = clickListener(meme)
    }
}