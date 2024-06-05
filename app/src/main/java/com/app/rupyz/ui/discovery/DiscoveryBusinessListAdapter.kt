package com.app.rupyz.ui.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemDiscoveryBuisinessListBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.OrgItem
import com.squareup.picasso.Picasso

class DiscoveryBusinessListAdapter(
    private var data: ArrayList<OrgItem>,
    private var listener: DiscoverySelectedListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discovery_buisiness_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDiscoveryBuisinessListBinding.bind(itemView)
        fun bindItem(model: OrgItem, position: Int, listener: DiscoverySelectedListener) {
            binding.tvConnectionName.text = StringHelper.toCamelCase(model.source?.legalName)
            binding.tvLocation.text = model.source?.city + ", " + model.source?.state
            if (!model.source?.shortDescription.isNullOrBlank()) {
                binding.tvConnectionInfo.text = model.source?.shortDescription
            } else if (!model.source?.businessNature.isNullOrBlank()){
                binding.tvConnectionInfo.text = model.source?.businessNature
            } else{
                binding.tvConnectionInfo.text = ""
            }

            if (model.source?.logoImage != null && model.source.logoImage.isNotEmpty()) {
                binding.orgPrefix.visibility = View.GONE
                binding.ivConnection.visibility = View.VISIBLE
                Picasso.get().load(model.source.logoImage).into(binding.ivConnection)
            } else {
                binding.orgPrefix.visibility = View.VISIBLE
                binding.ivConnection.visibility = View.INVISIBLE

                if (model.source?.legalName != null && model.source.legalName.isNotEmpty()) {
                    binding.orgPrefix.text =
                        StringHelper.getPrefix(model.source.legalName.substring(0, 1))
                }
            }

            if (model.source?.bannerImage != null) {
                ImageUtils.loadBannerImage(model.source.bannerImage, binding.ivBanner)
                binding.ivBanner.visibility = View.VISIBLE
            } else {
                binding.ivBanner.visibility = View.GONE
            }


            if (model.source?.complianceRating != null) {
                initBadge(model.source.complianceRating, itemView)
            }

            itemView.setOnClickListener { listener.onOrgClick(model.source?.orgSlug) }
        }

        private fun initBadge(compliance_rating_dot: Double, itemView: View) {
            try {
                when (compliance_rating_dot.toInt()) {
                    1 -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_amateur)
                    }
                    2 -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_basic)
                    }
                    3 -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_upcoming)
                    }
                    4 -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_respacted)
                    }
                    5 -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_iconic)
                    }
                    else -> {
                        binding.ivBusinessRating.setImageResource(R.mipmap.ic_badge_amateur)
                    }
                }
                binding.ivBusinessRating.visibility = View.VISIBLE
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }
}