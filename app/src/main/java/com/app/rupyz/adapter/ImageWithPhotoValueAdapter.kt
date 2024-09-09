package com.app.rupyz.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewImageAndPhotoBinding
import com.app.rupyz.databinding.ItemViewShowMoreBinding
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.helper.asBitmap
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.ImageUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.parcelize.Parcelize

class ImageWithPhotoValueAdapter(private val photoList: MutableList<PhotoLabelModel>, val cornerRadius: Float, val showPhoto: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false
    private val visibleItemCount = 3 // Show 5 items initially

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  when(viewType){
            ImageWithPhotoViewType.TXT_WITH_IMAGE.value -> {
                ImageWithPhotoValueViewHolder(ItemViewImageAndPhotoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else -> {
                ShowMoreViewHolder(ItemViewShowMoreBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ShowMoreViewHolder -> {
                holder.bind()
                if (isExpanded){
                    holder.binding.tvViewMore.text = holder.binding.root.context.resources.getString(R.string.show_less)
                }else{
                    holder.binding.tvViewMore.text = holder.binding.root.context.resources.getString(R.string.read_more)
                }

            }
            is ImageWithPhotoValueViewHolder -> {
                if (position < photoList.size){
                    holder.bind(photoList[position])
                }
            }
        }
    }

    inner class ImageWithPhotoValueViewHolder(itemView : ItemViewImageAndPhotoBinding) : RecyclerView.ViewHolder(itemView.root){
        val binding = itemView
        fun bind(data : PhotoLabelModel){
            if (showPhoto){
                binding.ivItemImage.showView()
                if (data.url?.contains("?") == true) {
                    val stringBuilder = data.url.split("?")
                    if (stringBuilder[0].contains(".pdf")) {
                        binding.ivItemImage.setImageResource(R.drawable.ic_pdf)
                        binding.ivItemImage.setPadding(60, 60, 60, 60)
                    }else{
                        ImageUtils.loadImage(data.url, binding.ivItemImage)
                    }
                }
                else {
                    if (data.url == null){
                        try{
                            binding.ivItemImage.setImageBitmap(StringHelper.printName(data.label).trim().substring(0, (Math.min(data.label?.length?:0, 2))).uppercase().asBitmap(binding.root.context,16f, Color.WHITE,binding.root.context.resources.getColor(R.color.theme_color,null)))
                        }catch (e: Exception){
                            log(e.toString())
                        }
                    }else{
                        val placeholder : BitmapDrawable? = try{
                            BitmapDrawable(binding.root.context.resources, StringHelper.printName(data.label).trim().substring(0, (Math.min(data.label?.length?:0, 2))).uppercase().asBitmap(binding.root.context,16f, Color.WHITE,binding.root.context.resources.getColor(R.color.theme_color,null)))
                        }catch (e: Exception){
                            null
                        }
                        ImageUtils.loadImageWithPlaceHolder(data.url, binding.ivItemImage,placeholder)
                    }
                }
                val shapeAppearanceModel = ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, cornerRadius)
                    .setTopRightCorner(CornerFamily.ROUNDED, cornerRadius)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, cornerRadius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, cornerRadius)
                    .build()
                binding.ivItemImage.shapeAppearanceModel = shapeAppearanceModel
            }else{
                binding.ivItemImage.hideView()
            }
            binding.tvItemName.text = data.label

        }
    }

    inner class ShowMoreViewHolder (itemView : ItemViewShowMoreBinding) : RecyclerView.ViewHolder(itemView.root){
        val binding = itemView
        @SuppressLint("NotifyDataSetChanged")
        fun bind(){
            binding.tvViewMore.setOnClickListener {
                isExpanded = !isExpanded
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
       return if (isExpanded){
            if (position < photoList.size){
                ImageWithPhotoViewType.TXT_WITH_IMAGE.value
            }else{
                ImageWithPhotoViewType.SHOW_MORE.value
            }
        }else{
            if (position < visibleItemCount){
                ImageWithPhotoViewType.TXT_WITH_IMAGE.value
            }else{
                ImageWithPhotoViewType.SHOW_MORE.value
            }
        }
    }

    override fun getItemCount(): Int {
        return if (photoList.size > visibleItemCount){
            if (isExpanded) {
                photoList.size + 1
            } else {
                visibleItemCount + 1 // Visible items + "Show More" button
            }
        }else{
            photoList.size
        }

    }

}


enum class ImageWithPhotoViewType(val value : Int) { TXT_WITH_IMAGE(0), SHOW_MORE(1) }

@Parcelize
data class PhotoLabelModel(
    val id : Int? = null,
    val url : String? = null,
    val label : String? = null
) :  Parcelable