package com.app.rupyz.ui.discovery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDiscoveryAllProductListBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.ProductItem

class DiscoveryAllProductListAdapter(
    private var data: ArrayList<ProductItem>,
    private var listener: DiscoverySelectedListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discovery_all_product_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position , listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDiscoveryAllProductListBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bindItem(model: ProductItem, position: Int, listener: DiscoverySelectedListener) {
            binding.tvConnectionName.text = model.source?.name
            binding.tvSoldBy.text = StringHelper.toCamelCase(model.source?.legalName)
            binding.tvLocation.text = model.source?.city + ", " + model.source?.state
            binding.tvLikeCount.text = "" + model.source?.likeCount
            binding.tvViewCount.text = "" + model.source?.viewCount

            binding.tvPrice.text = "" + model.source?.minPrice + " - " +  model.source?.maxPrice

            ImageUtils.loadImage(model.source?.pics, binding.ivConnection)

            itemView.setOnClickListener { listener.onProductClick(model.source?.orgSlug, model.source)}
            binding.tvSoldBy.setOnClickListener { listener.onOrgClick(model.source?.orgSlug)}
            binding.ivShare.setOnClickListener { listener.onProductShare(model.source)}
        }
    }
}