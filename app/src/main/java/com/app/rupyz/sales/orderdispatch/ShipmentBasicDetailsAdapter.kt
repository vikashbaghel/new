package com.app.rupyz.sales.orderdispatch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemShipmentBasicDetailsBinding
import com.app.rupyz.model_kt.KeyValuePairModel

class ShipmentBasicDetailsAdapter(
    private var data: ArrayList<KeyValuePairModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shipment_basic_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemShipmentBasicDetailsBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: KeyValuePairModel) {

            binding.tvDetailsHeading.text = model.name
            binding.tvDetails.text = model.value
        }
    }
}