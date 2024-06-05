package com.app.rupyz.sales.orderdispatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemLrPhotoListBinding
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter.ProductImageClickListener

class LrPhotoListAdapter(
    private var data: ArrayList<PicMapModel>,
    private var listener: ProductImageClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lr_photo_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemLrPhotoListBinding.bind(itemView)

        fun bindItem(model: PicMapModel, position: Int, listener: ProductImageClickListener) {
            if (model.url!!.contains("?")) {
                val stringBuilder = model.url!!.split("?")
                if (stringBuilder[0].contains(".pdf")) {
                    binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                    binding.ivProductImage.setPadding(60, 60, 60, 60)
                    itemView.setOnClickListener { listener.onPdfClick(position) }
                }
            } else {
                ImageUtils.loadImage(model.url, binding.ivProductImage)
                itemView.setOnClickListener { listener.onImageClick(position) }
            }
        }
    }
}