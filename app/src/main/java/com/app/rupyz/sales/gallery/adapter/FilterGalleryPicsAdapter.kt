package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.FiltergallerypicsItemBinding
import com.app.rupyz.model_kt.gallery.FilterData


class FilterGalleryPicsAdapter(
    private var data: ArrayList<FilterData>?,
    private val listener: DebounceClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filtergallerypics_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], listener)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FiltergallerypicsItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(data: FilterData, listener: DebounceClickListener) {
            binding.txtName.text = data.name

            if (data.isSelected) {
                binding.txtName.backgroundTintList =
                    ColorStateList.valueOf(
                        itemView.resources.getColor(R.color.white))
                binding.txtName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.black))
            }
            else {
                binding.txtName.backgroundTintList =
                    ColorStateList.valueOf(
                        itemView.resources.getColor(R.color.color_F4F4F4)
                    )
                binding.txtName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.color_727176
                    )
                )
            }
            binding.txtName.setOnClickListener {
                listener.onDebounceClick(adapterPosition, data)
            }
        }
    }
}

