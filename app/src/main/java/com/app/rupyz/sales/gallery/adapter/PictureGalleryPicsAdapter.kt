package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.PictureGalleryPicItemBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.gallery.PictureData

class PictureGalleryPicsAdapter(
    private var data: ArrayList<PictureData?>?,
    private var listener: GalleryPictureInfoListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.picture_gallery_pic_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position]!!, listener)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = PictureGalleryPicItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: PictureData, listener: GalleryPictureInfoListener) {
            ImageUtils.loadImage(model.image_url, binding.imageView)

            itemView.setOnClickListener {
                listener.picInfo(model)
            }
        }
    }

    interface GalleryPictureInfoListener {
        fun picInfo(pictureData: PictureData)
    }
}