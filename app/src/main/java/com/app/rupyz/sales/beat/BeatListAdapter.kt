package com.app.rupyz.sales.beat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemBeatListInfoBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.BeatListDataItem

class BeatListAdapter(
    private var data: ArrayList<BeatListDataItem>,
    private var listener: IBeatActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beat_list_info, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBeatListInfoBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: BeatListDataItem, position: Int, listener: IBeatActionListener) {
            binding.tvBeatName.text = model.name
            if (model.locality.isNullOrEmpty()
                    .not() && model.customerCount != null && model.customerCount != 0
            ) {
                binding.tvLocalityAndCustomer.visibility = View.VISIBLE

                binding.tvLocalityAndCustomer.text = itemView.resources.getString(
                    R.string.beat_customer_with_locality, model.locality ?: "".trim(),
                    "${model.customerCount}"
                )
            } else {
                binding.tvLocalityAndCustomer.visibility = View.VISIBLE
                if (model.locality.isNullOrEmpty().not()) {
                    binding.tvLocalityAndCustomer.text = model.locality
                } else if (model.customerCount != null && model.customerCount != 0) {
                    binding.tvLocalityAndCustomer.text = "${model.customerCount} Customers"
                } else {
                    binding.tvLocalityAndCustomer.visibility = View.GONE
                }
            }

            if (model.parentCustomerLogoImageUrl.isNullOrEmpty().not()) {
                ImageUtils.loadCustomImage(
                    model.parentCustomerLogoImageUrl,
                    binding.ivCustomer,
                    R.mipmap.ic_no_customer_found
                )
            } else {
                binding.ivCustomer.setImageResource(R.mipmap.ic_no_customer_found)
            }

            if (model.parentCustomerName.isNullOrEmpty().not()) {
                binding.tvCustomerName.text = model.parentCustomerName
                binding.groupCustomerInfo.visibility = View.VISIBLE
            } else {
                binding.groupCustomerInfo.visibility = View.GONE
            }

            binding.ivMore.setOnClickListener { v ->
                val popup =
                    PopupMenu(v.context, binding.ivMore)
                popup.inflate(R.menu.menu_edit_and_delete)
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.delete_product -> {
                            listener.onDeleteBeat(
                                position,
                                model
                            )
                            return@setOnMenuItemClickListener true
                        }

                        R.id.edit_product -> {
                            listener.onEditBeat(
                                position,
                                model
                            )
                            return@setOnMenuItemClickListener false
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popup.show()
            }

            itemView.setOnClickListener {
                listener.setBeatSelect(model)
            }
        }
    }

    interface IBeatActionListener {
        fun setBeatSelect(model: BeatListDataItem)
        fun onDeleteBeat(position: Int, model: BeatListDataItem)
        fun onEditBeat(position: Int, model: BeatListDataItem)
    }
}