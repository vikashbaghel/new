package com.app.rupyz.sales.filter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDateFilterBinding
import com.app.rupyz.model_kt.DateFilterModel

class DateFilterRvAdapter(
    private var data: ArrayList<DateFilterModel>,
    private var listener: IDateFilterSelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemDateFilterBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: DateFilterModel, position: Int, listener: IDateFilterSelectedListener) {
            binding.tvTitle.text = model.title

            if (model.isSelected == true) {
                binding.ivRadioButton.setImageResource(R.drawable.ic_radio_button_selected)
            } else {
                binding.ivRadioButton.setImageResource(R.drawable.ic_radio_button_not_selected)
            }

            itemView.setOnClickListener {
                listener.onDateSelected(model, position)
            }
        }
    }

    interface IDateFilterSelectedListener {
        fun onDateSelected(model: DateFilterModel, position: Int)
    }
}