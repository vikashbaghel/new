package com.app.rupyz.sales.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSelfiePicBinding
import java.io.File


class SelfieListForAttendanceAdapter(
        private var data: ArrayList<String>,
        private var listener: ISelfieActionListener
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selfie_pic, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSelfiePicBinding.bind(itemView)
        fun bindItem(path: String, listener: ISelfieActionListener) {
            binding.ivImage.setImageURI(Uri.fromFile(File(path)))

            binding.imgDelete.setOnClickListener {
                listener.onRemoveSelfie(adapterPosition)
            }
        }
    }

    interface ISelfieActionListener {
        fun onRemoveSelfie(position: Int)
    }
}