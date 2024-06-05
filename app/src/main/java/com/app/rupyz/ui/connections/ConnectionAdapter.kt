package com.app.rupyz.ui.connections

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemPendingConnectionsBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.ConnectionListItem

class ConnectionAdapter(
    private var data: ArrayList<ConnectionListItem>,
    private var listener: ConnectionActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_connections, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPendingConnectionsBinding.bind(itemView)

        fun bindItem(model: ConnectionListItem, position: Int, listener: ConnectionActionListener) {
            ImageUtils.loadTeamImage(model.logoImage, binding.ivConnection)

            ImageUtils.loadBannerImage(model.bannerImage, binding.ivBanner)

            binding.tvConnectionName.text = StringHelper.toCamelCase(model.legalName)

            if (model.shortDescription.equals("")) {
                binding.tvConnectionInfo.visibility = View.GONE
            } else {
                binding.tvConnectionInfo.visibility = View.VISIBLE
                binding.tvConnectionInfo.text = model.shortDescription
            }

            if (model.status == "ACCEPTED") {
                binding.btnAccept.visibility = View.GONE
                binding.btnDecline.visibility = View.GONE
                binding.ivMenu.visibility = View.VISIBLE
                binding.ivMenu.setOnClickListener {
                    val popup = PopupMenu(itemView.context, binding.ivMenu)
                    popup.inflate(R.menu.connection_action_menu)

                    popup.menu.getItem(2).isVisible = false

                    popup.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.share_connection -> {
                                listener.onShareConnection(model, position)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.remove_connection, R.id.cancel_request -> {
                                listener.onRemoveConnection(model, position)
                                return@setOnMenuItemClickListener false
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
                    popup.show()
                }
            } else {
                binding.ivMenu.visibility = View.GONE
                binding.btnAccept.visibility = View.VISIBLE
                binding.btnDecline.visibility = View.VISIBLE

                binding.btnAccept.setBackgroundResource(R.drawable.network_connect_button_style)
                binding.btnAccept.text = "Accept"

                binding.btnDecline.setBackgroundResource(R.drawable.network_pending_button_style)
                binding.btnDecline.text = "Decline"

                binding.btnAccept.setOnClickListener {
                    listener.onAccept(
                        model.targetId!!,
                        position
                    )
                }
                binding.btnDecline.setOnClickListener {
                    listener.onDecline(
                        model.targetId!!,
                        position
                    )
                }
            }

            if (model.complianceRating != null) {
                initBadge(model.complianceRating, itemView)
            }

            itemView.setOnClickListener { listener.onShowInfo(model) }
        }

        private fun initBadge(compliance_rating_dot: Double, itemView: View) {
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