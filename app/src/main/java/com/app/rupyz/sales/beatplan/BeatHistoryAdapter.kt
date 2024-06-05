package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemBeatPlanHistoryBinding
import com.app.rupyz.model_kt.CustomerFollowUpDataItem

class BeatHistoryAdapter(
    private var data: ArrayList<CustomerFollowUpDataItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beat_plan_history, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBeatPlanHistoryBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: CustomerFollowUpDataItem,
            position: Int
        ) {

            binding.tvActivityLogs.text = model.message

            binding.tvActivityName.text =
                model.action?.lowercase()
                    ?.replaceFirstChar(Char::titlecase) + " " + model.moduleType
        }
    }
}