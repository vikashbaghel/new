package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSortByListBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.order.order_history.SortByData
import kotlin.math.min

class SortByAdapter(
    private var data: ArrayList<SortByData>,
    private var listener: SortByListener,
    private var sortingSelected: Boolean,
    private var mPosition: Int,
    private var sortingDateOrder: String = "",

    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var checkedPosition = false
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        checkedPosition = sortingSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sort_by_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data[position],
            position,
            listener,
            sortingSelected,
            mPosition,
            sortingDateOrder
        )
    }

    override fun getItemCount(): Int {
        val limit = 5
        return min(data.size, limit)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSortByListBinding.bind(itemView)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bindItem(
            model: SortByData,
            position: Int,
            listener: SortByListener,
            sortingSelected: Boolean,
            mPosition: Int,
            sortingDateOrder1: String
        ) {


            binding.tvOrder.text = model.name
            binding.ivO.setImageDrawable(itemView.context.getDrawable(model.image))
            var sortingDateOrder = ""
            sortingDateOrder = sortingDateOrder1

//            if (mPosition == position) {
//                model.isSelected = checkedPosition
//            }

            itemView.setOnClickListener {

                when (sortingDateOrder) {
                    AppConstant.SORTING_LEVEL_ASCENDING -> {
//                        binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)
//
//                        binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                        sortingDateOrder = AppConstant.SORTING_LEVEL_DESCENDING
                    }

                    AppConstant.SORTING_LEVEL_DESCENDING, "" -> {
//                        binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_enable)
//
//                        binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                        sortingDateOrder = AppConstant.SORTING_LEVEL_ASCENDING
                    }
                }

//                if (mPosition == position) {
//                    checkedPosition = true
//                } else {
//                    checkedPosition = false
//                }

                listener.onSortBySelect(model, sortingDateOrder, position, sortingSelected)
            }

            if (model.isSelected == true) {
                binding.tvOrder.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.black
                    )
                )

                if (sortingDateOrder == AppConstant.SORTING_LEVEL_ASCENDING) {
                    binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                    binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)
                } else if (sortingDateOrder.isEmpty()
                    || sortingDateOrder == AppConstant.SORTING_LEVEL_DESCENDING
                ) {
                    binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_enable)
                }
            } else {
                binding.tvOrder.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.leve_text_color
                    )
                )
                binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)
            }
        }
    }

    interface SortByListener {
        fun onSortBySelect(
            model: SortByData,
            sortByOrder: String,
            selectedPosition: Int,
            selected: Boolean
        )
    }
}






