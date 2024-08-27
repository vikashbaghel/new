@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemAddPhotoListBinding
import com.app.rupyz.dialog.ImageUploadBottomSheet
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.FormItemsItem
import java.io.File

class ImageSelector : RecyclerView {


    /***
     *
     * @property onImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
     *
     ***/
    private var isGalleryPhotoDisabled = false

    /***
     *
     * @property onImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
     *
     ***/
    var onImageClickedListener: OnImageClickedListener = object : OnImageClickedListener {
        override fun onAddImage() {
            super.onAddImage()
            openPhotoPicker(isGalleryPhotoDisabled)
        }
        
        override fun onEditImage(model : AddedPhotoModel, position : Int) {
            super.onEditImage(model, position)
            openPhotoPicker(isGalleryPhotoDisabled, model,position)
        }

        override fun onDeleteImage(position: Int, timeStamp: Long?) {
            super.onDeleteImage(position, timeStamp)
            photoList.removeAt(position)
        }
    }

    /***
     *
     * @property photoList contains the list of Images
     *
     ***/
    private var photoList: ArrayList<AddedPhotoModel?> = arrayListOf()

    /***
     *
     * @property isCoverPic is the picture a cover picture
     *
     ***/
    private var isCoverPic: Boolean = false

    /***
     *
     * @property photoAdapter is the listener for the click events on image items like delete button, Edit button etc
     *
     ***/
    private var photoAdapter: ProductPhotoListAdapter? =
        ProductPhotoListAdapter(photoList, onImageClickedListener, isCoverPic)

    /***
     *
     * @property onImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
     *
     ***/
    private var isPhotoPickerRecycler: Boolean = true


    /***
     *
     * @property onImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
     *
     ***/
    private var imageCount: Int = 1

    private var formFields: FormItemsItem? = null

    private var formItemType: FormItemType = FormItemType.FILE_UPLOAD
    
    private var isOnEditMode = false


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    /***
     * @property init is used to initialize the view
     *
     * @param attrs is the attribute set for the view
     ***/
    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckBox)
        photoList.clear()
        photoList.add(null)
        if (isPhotoPickerRecycler) {
            photoAdapter = ProductPhotoListAdapter(photoList, onImageClickedListener, isCoverPic)
            layoutManager = GridLayoutManager(context, 3)
            adapter = photoAdapter
            isNestedScrollingEnabled = false
            stopNestedScroll()
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
        a.recycle()
    }

    /***
     *  @property setRecyclerLayoutManager is used to set the layout manager for the recycler view
     *
     *  @param _layoutManager is the layout manager for the recycler view
     ***/
    fun setRecyclerLayoutManager(_layoutManager: LayoutManager) {
        layoutManager = _layoutManager
    }

    /***
     *  @property photoAdapter is the adapter class for the image picker recycler view
     *
     *  @param listener is the listener for the click events on image items like delete button, Edit button etc
     ***/
    fun setImageClickedListener(listener: OnImageClickedListener) {
        this.onImageClickedListener = listener
        photoAdapter?.setListener(listener)
    }

    /***
     *  @property photoList is the list of Photos that are already added
     *
     *  @param data - ArrayList<AddedPhotoModel?> is the list of Photos that are already added
     ***/
    @SuppressLint("NotifyDataSetChanged")
    fun setPhotoList(data: ArrayList<AddedPhotoModel?>) {
        isOnEditMode = true
        photoList.clear()
        photoList.addAll(data)
        if (photoList.filterNotNull().size >= (formFields?.inputProps?.maxCount ?: 1)) {
            photoList.remove(null)
        }
        photoAdapter?.notifyDataSetChanged()
    }

    /***
     * @property isCoverPic is used to show the cover image
     *
     * @param isCoverPic
     ***/
    fun isCoverPic(isCoverPic: Boolean) {
        this.isCoverPic = isCoverPic
    }

    /***
     * @property isPhotoPickerRecycler is used to show the photo picker recycler view
     *
     * @param isPhotoPickerRecycler
     ***/
    fun isPhotoPickerRecycler(isPhotoPickerRecycler: Boolean) {
        this.isPhotoPickerRecycler = isPhotoPickerRecycler
    }

    /***
     * @property isGalleryPhotoDisabled is used to disable gallery image pickup option
     *
     * @param isGalleryPhotoDisabled
     ***/
    fun isGalleryPhotoDisabled(isGalleryPhotoDisabled: Boolean) {
        this.isGalleryPhotoDisabled = isGalleryPhotoDisabled
    }


    /***
     * @since  01/05/2024
     *
     * @see openPhotoPicker method is used to open the photo picker dialog fragment
     *
     * @param  galleryPhotoDisabled to disable gallery image pickup option
     ***/
    private fun openPhotoPicker(galleryPhotoDisabled : Boolean, model : AddedPhotoModel? = null, position : Int?= null) {
        Utils.hideKeyboard((context as FragmentActivity))
        if (model != null && position != null){
            val frag = ImageUploadBottomSheet(1) {
                if (it.isNotEmpty()) {
                    val photo = it.get(0)
                    val data = AddedPhotoModel(
                            imagePath = photo,
                            timeStamp = System.currentTimeMillis(),
                            type = null)
                    photoList.set(position,data)
                    photoAdapter?.notifyDataSetChanged()
                }
            }
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, galleryPhotoDisabled)
            frag.arguments = bundle
            
            frag.show(
                    (context as FragmentActivity).supportFragmentManager,
                    AppConstant.IMAGE_UPLOAD_TAG
                     )
        }
        else{
            val frag = ImageUploadBottomSheet(imageCount) {
                if (it.isNotEmpty()) {
                    val pList = (arrayListOf<AddedPhotoModel?>())
                    it.forEach {
                        val data = AddedPhotoModel(
                                imagePath = it,
                                timeStamp = System.currentTimeMillis(),
                                type = null
                                                  )
                        photoList.add(data)
                        pList.add(data)
                    }
                    if (photoList.filterNotNull().size >= (formFields?.inputProps?.maxCount ?: 1)) {
                        photoList.remove(null)
                    }
                    photoAdapter?.notifyDataSetChanged()
                }
            }
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, galleryPhotoDisabled)
            frag.arguments = bundle
            
            frag.show(
                    (context as FragmentActivity).supportFragmentManager,
                    AppConstant.IMAGE_UPLOAD_TAG
                     )
        }
       

    }


    fun getPhotoList(): ArrayList<AddedPhotoModel?> {
        return photoList
    }


    /***
     * @since 01/05/2024
     *
     * @property ProductPhotoListAdapter will only be used if @param isPhotoPickerRecycler is true
     * @property ProductPhotoListAdapter is an adapter class for the image picker recycler view
     *
     * @param isCoverPic
     * @param listener OnImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
     * @param data - ArrayList<AddedPhotoModel?> is the list of Photos that are already added
     ***/
    private inner class ProductPhotoListAdapter(
        private var data: ArrayList<AddedPhotoModel?>,
        private var listener: OnImageClickedListener?,
        private var isCoverPic: Boolean
    ) : Adapter<ViewHolder>() {

        private val viewTypeItem = 0
        private val viewTypeLoading = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return if (viewType == viewTypeItem) {
                MyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_add_photo_list, parent, false)
                )
            } else {
                LoadingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_view_upload_photo, parent, false)
                )
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder is MyViewHolder) {
                holder.bindItem(data[position]!!, position, listener, isCoverPic)
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

        fun setListener(onImageClickedListener: OnImageClickedListener) {
            listener = onImageClickedListener
        }

        inner class LoadingViewHolder(itemView: View) : ViewHolder(itemView) {
            fun bindItem(listener: OnImageClickedListener?) {

                itemView.setOnClickListener { listener?.onAddImage() }
            }
        }

        inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {
            private val binding = ItemAddPhotoListBinding.bind(itemView)
            fun bindItem(
                model: AddedPhotoModel,
                position: Int,
                listener: OnImageClickedListener?,
                isCoverPic: Boolean
            ) {
                if (model.type == AppConstant.DOCUMENT) {
                    binding.ivProductImage.setPadding(60, 60, 60, 60)
                    binding.ivProductImage.setImageResource(R.drawable.ic_pdf)
                } else {
                    if (model.onEditProduct) {
                        ImageUtils.loadImage(model.imagePath, binding.ivProductImage)
                        binding.mainContent.setOnClickListener { listener?.onEditAlreadyUploadedImage() }
                    } else {

                        binding.ivProductImage.setPadding(0, 0, 0, 0)
                        binding.ivProductImage.setImageURI(Uri.fromFile(File(model.imagePath!!)))
                        binding.mainContent.setOnClickListener {
                            listener?.onEditImage(model, position)
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

                binding.ivRadioButton.setOnClickListener {
                    listener?.onImageSelect(
                        model,
                        position
                    )
                }

                if (isOnEditMode){
                    binding.imgDelete.setImageResource(R.drawable.ic_reload)
                }else{
                    binding.imgDelete.setImageResource(R.drawable.ic_delete)
                }
                
                binding.imgDelete.setOnClickListener {
                    if (position < data.size) {
                        listener?.apply {
                            if (isOnEditMode == true){
                                onEditImage(model,position)
                            }else{
                                onDeleteImage(position, model.timeStamp)
                                data.remove(model)
                                notifyItemRemoved(position)
                                if (data.filterNotNull().size < (formFields?.inputProps?.maxCount ?: 1)) {
                                    if (data.contains(null).not()) {
                                        photoList.add(0, null)
                                        notifyItemInserted(0)
                                    }
                                }
                            }
                           
                        }
                    }

                }
            }
        }
    }

    fun setFormFields(formFields: FormItemsItem) {
        this.formFields = formFields
    }

    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.DATE_TIME_PICKER
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

}


/***
 * @since 01/05/2024
 *
 * @property OnImageClickedListener
 *
 * @see OnImageClickedListener is the listener for the click events on image items like delete button, Edit button etc
 ***/
interface OnImageClickedListener {
    /***
     * @property onDeleteImage is executed when delete button is clicked
     ***/
    fun onDeleteImage(position: Int, timeStamp: Long?) {
        /***  - method is empty ***/
    }

    /***
     * @property onImageSelect is executed when image is selected
     ***/
    fun onImageSelect(model: AddedPhotoModel, position: Int) {
        /***  - method is empty ***/
    }

    /***
     * @property onEditImage is executed when an edit button is clicked
     ***/
    fun onEditImage(model: AddedPhotoModel, position: Int) {
        /***  - method is empty ***/
    }

    /***
     * @property onAddImage is executed when an image is added if @param filePath is null then use adapter to get the image list
     ***/
    fun onAddImage() {
        /***  - method is empty ***/
    }

    /***
     * @property onAddFile is executed when an File is added
     ***/
    fun onAddFile(filePath: String?) {
        /***  - method is empty ***/
    }

    /***
     * @property onEditAlreadyUploadedImage
     ***/
    fun onEditAlreadyUploadedImage() {
        /***  - method is empty ***/
    }


}
