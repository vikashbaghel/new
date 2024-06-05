package com.app.rupyz.ui.discovery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDiscoverySearchSuggestionListBinding

class DiscoverySearchSuggestionListAdapter(
    private var data: ArrayList<String>,
    private var listener: RecentSearchClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discovery_search_suggestion_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position , listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDiscoverySearchSuggestionListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: String, position: Int, listener: RecentSearchClickListener) {
            binding.tvSuggestion.text = "" + model

            itemView.setOnClickListener { listener.onSuggestionClick(model) }
        }
    }
}