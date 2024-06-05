package com.app.rupyz.adapter.network

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.NetworkOrgListInsideItemBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.NetworkDataItem
import com.app.rupyz.ui.network.views.NetworkConnectListener
import com.squareup.picasso.Picasso

class NetworkOrgAdapter(
    private var data: MutableList<NetworkDataItem>,
    private var listener: NetworkConnectListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.network_org_list_inside_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NetworkOrgListInsideItemBinding.bind(itemView)
        fun bindItem(model: NetworkDataItem, position: Int, listener: NetworkConnectListener) {

            if (model.legalName != null) {
                val legalName = StringHelper.toCamelCase(model.legalName)
                binding.txtOrgName.text = legalName
            } else {
                binding.txtOrgName.text = ""
            }

            if (model.shortDescription != null) {
                binding.txtShortDesc.text = model.shortDescription
                binding.txtShortDesc.visibility = View.VISIBLE
            } else {
                binding.txtShortDesc.visibility = View.INVISIBLE
            }

            if (model.status != null && !model.status.equals("")) {
                if (model.status == "PENDING") {
                    binding.btnCheckScore.text = "Pending"
                    binding.btnCheckScore.isEnabled = false
                    binding.btnCheckScore.setBackgroundResource(R.drawable.network_pending_button_style)
                    binding.btnCheckScore.setTextColor(itemView.context.getColor(R.color.white))
                } else if (model.status == "ACCEPTED") {
                    binding.btnCheckScore.isEnabled = false
                    binding.btnCheckScore.text = "Connected"
                    binding.btnCheckScore.setTextColor(itemView.context.getColor(R.color.white))
                    binding.btnCheckScore.setBackgroundResource(R.drawable.active_status_bg_style)

                }
            } else {
                binding.btnCheckScore.text = "Connect"
                binding.btnCheckScore.isEnabled = true
                binding.btnCheckScore.setBackgroundResource(R.drawable.connect_background_with_border)
                binding.btnCheckScore.setTextColor(itemView.context.getColor(R.color.theme_purple))
            }

            if (model.mutualConnectionsCount != null && model.mutualConnectionsCount != 0){
                binding.groupMutualConnection.visibility = View.VISIBLE
                binding.tvSharedConnection.text = "" + model.mutualConnectionsCount + " shared connection"
            } else{
                binding.groupMutualConnection.visibility = View.INVISIBLE
            }

            if (model.complianceRating != null) {
                initBadge(model.complianceRating)
            }

            if (model.logoImage != null && model.logoImage.isNotEmpty()) {
                binding.userPrefix.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                Picasso.get().load(model.logoImage).into(binding.imageView)
            } else {
                binding.userPrefix.visibility = View.VISIBLE
                binding.imageView.visibility = View.INVISIBLE

                if (model.legalName != null && model.legalName.isNotEmpty()) {
                    binding.userPrefix.text =
                        StringHelper.getPrefix(model.legalName.substring(0, 1))
                }
            }

            if (model.bannerImage != null){
                ImageUtils.loadBannerImage(model.bannerImage, binding.ivBanner)
                binding.ivBanner.visibility = View.VISIBLE
            } else{
                binding.ivBanner.visibility = View.GONE
            }

            binding.btnCheckScore.setOnClickListener {
                listener.onConnect(
                    model,
                    position
                )
            }

            binding.llMain.setOnClickListener { listener.openProfile(model.slug!!) }

        }

        private fun initBadge(compliance_rating_dot: Double) {
            try {
                when (compliance_rating_dot.toInt()) {
                    1 -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_amateur)
                    }
                    2 -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_basic)
                    }
                    3 -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_upcoming)
                    }
                    4 -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_respacted)
                    }
                    5 -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_iconic)
                    }
                    else -> {
                        binding.ivRating.setImageResource(R.mipmap.ic_badge_amateur)
                    }
                }
                binding.ivRating.visibility = View.VISIBLE
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}