package com.app.rupyz.ui.discovery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemBadgeListBinding
import com.app.rupyz.model_kt.BadgeFilterItem

class BadgeListAdapter(private var data: ArrayList<BadgeFilterItem>,private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, onClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemBadgeListBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: BadgeFilterItem, position: Int, onClickListener: OnClickListener) {
            model.position = position
            if (position != 5) {
                binding.tvItems.text = model.name + " +"
            } else{
                binding.tvItems.text = model.name
            }

            itemView.setOnClickListener { onClickListener.onClick(model) }


            if (model.isSelected){
                itemView.setBackgroundResource(R.drawable.details_button_style)
                binding.tvItems.setTextColor(itemView.context.resources.getColor(R.color.white))
            } else{
                itemView.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
                binding.tvItems.setTextColor(itemView.context.resources.getColor(R.color.tab_un_selected_color))
            }
        }
    }

    class OnClickListener(val clickListener: (meme: BadgeFilterItem) -> Unit) {
        fun onClick(meme: BadgeFilterItem) = clickListener(meme)
    }
}