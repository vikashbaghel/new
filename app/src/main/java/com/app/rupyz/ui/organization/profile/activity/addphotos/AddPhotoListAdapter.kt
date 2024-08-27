package com.app.rupyz.ui.organization.profile.activity.addphotos

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAddPhotoListBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.AddedPhotoModel
import java.io.File

class AddPhotoListAdapter(
    private var data: ArrayList<AddedPhotoModel>,
    var listener: OnImageDeleteListener,
    var isCoverPic: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_photo_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener, isCoverPic)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAddPhotoListBinding.bind(itemView)
        fun bindItem(
            model: AddedPhotoModel,
            position: Int,
            listener: OnImageDeleteListener,
            isCoverPic: Boolean
        ) {
            if (model.onEditProduct) {
                if (model.imagePath!!.contains("?")) {
                    val stringBuilder = model.imagePath!!.split("?")
                    if (stringBuilder[0].contains(".pdf")) {
                        binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                        binding.ivProductImage.setPadding(0, 0, 0, 0)
                    }
                }
                else{

                    ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
            }} else {
                binding.ivProductImage.setImageURI(Uri.fromFile(File(model.imagePath!!)))
            }




            if (model.isSelect) {
                binding.mainContent.setBackgroundResource(R.drawable.purple_5dp_corner_background)
                binding.ivRadioButton.setImageResource(R.drawable.check)
            } else {
                binding.mainContent.setBackgroundResource(R.drawable.price_seekbar)
                binding.ivRadioButton.setImageResource(R.drawable.holo_increment_circle)
            }

            if (!isCoverPic) {
                binding.mainContent.setBackgroundResource(R.drawable.price_seekbar)
                binding.ivRadioButton.visibility = View.GONE
            }

            binding.mainContent.setOnClickListener { listener.onImageSelect(model, position) }

            binding.imgDelete.setOnClickListener {
                listener.onDeleteImage(
                    position,
                    model.timeStamp
                )
            }
        }
    }

    interface OnImageDeleteListener {
        fun onDeleteImage(position: Int, timeStamp: Long?)
        fun onImageSelect(model: AddedPhotoModel, position: Int)
    }
}