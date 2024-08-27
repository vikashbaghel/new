package com.app.rupyz.ui.organization.profile.activity.addphotos

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAddPhotoListBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.AddedPhotoModel
import java.io.File

class ProductPhotoListAdapter(
    private var data: ArrayList<AddedPhotoModel?>,
    private var listener: OnImageDeleteListener,
    private var isCoverPic: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeItem = 0
    private val viewTypeLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == viewTypeItem) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_photo_list, parent, false)
            return MyViewHolder(itemView)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_add_photo_item, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindItem(
                data[position]!!, position, listener, isCoverPic
            )
        } else if (holder is LoadingViewHolder) {
            holder.bindItem(listener)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] == null) viewTypeLoading else viewTypeItem
    }

    class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindItem(listener: OnImageDeleteListener) {

            itemView.setOnClickListener { listener.onAddImage() }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAddPhotoListBinding.bind(itemView)
        fun bindItem(
            model: AddedPhotoModel,
            position: Int,
            listener: OnImageDeleteListener,
            isCoverPic: Boolean
        ) {
            if (model.type == AppConstant.DOCUMENT) {
                binding.ivProductImage.setPadding(60, 60, 60, 60)
                if (model.imagePath!!.contains("?")) {
                    val stringBuilder = model.imagePath!!.split("?")
                    if (stringBuilder[0].contains(".pdf")) {
                        binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                        binding.ivProductImage.setPadding(0, 0, 0, 0)

                    }
                }
                else
                {
                    ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
                }

            } else {
                if (model.onEditProduct) {
                    if (model.imagePath!!.contains("?")) {
                        val stringBuilder = model.imagePath!!.split("?")
                        if (stringBuilder[0].contains(".pdf")) {
                            binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                            binding.ivProductImage.setPadding(0, 0, 0, 0)

                        }
                    }
                    else
                    {
                        ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
                        //binding.ivProductImage.setImageURI(Uri.fromFile(File(model.imagePath!!)))

                    }
                   // ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
                    binding.mainContent.setOnClickListener { listener.onEditAlreadyUploadedImage() }
                } else {

                    binding.ivProductImage.setPadding(0, 0, 0, 0)

                    if (model.imagePath!!.contains("?")) {
                        val stringBuilder = model.imagePath!!.split("?")
                        if (stringBuilder[0].contains(".pdf")) {
                            binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                            binding.ivProductImage.setPadding(0, 0, 0, 0)

                        }
                    }
                    else
                    {
                        ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
                        //binding.ivProductImage.setImageURI(Uri.fromFile(File(model.imagePath!!)))

                    }
                    binding.mainContent.setOnClickListener {
                        listener.onEditImage(
                            model,
                            position
                        )
                    }
                }
            }

            if (model.isDisplayPicEnable) {
                binding.ivRadioButton.visibility = View.VISIBLE
            } else {
                binding.ivRadioButton.visibility = View.GONE
            }


            if (model.isSelect) {
                binding.mainContent.setBackgroundResource(R.drawable.purple_5dp_corner_background)
                binding.ivRadioButton.setImageResource(R.drawable.check)
            } else {
                binding.mainContent.setBackgroundResource(R.drawable.price_seekbar)
                binding.ivRadioButton.setImageResource(R.drawable.holo_increment_circle)
            }

            if (isCoverPic.not()) {
                binding.mainContent.setBackgroundResource(R.drawable.price_seekbar)
                binding.ivRadioButton.visibility = View.GONE
            }

            binding.ivRadioButton.setOnClickListener { listener.onImageSelect(model, position) }

            binding.imgDelete.setOnClickListener {
                listener.onDeleteImage(
                    position,
                    model.timeStamp
                )
            }
        }
    }

    interface OnImageDeleteListener {
        fun onDeleteImage(position: Int, timeStamp: Long?){}
        fun onImageSelect(model: AddedPhotoModel, position: Int){}
        fun onEditImage(model: AddedPhotoModel, position: Int){}
        fun onAddImage()
        fun onEditAlreadyUploadedImage(){}
    }
}