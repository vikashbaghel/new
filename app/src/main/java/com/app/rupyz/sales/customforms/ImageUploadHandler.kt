package com.app.rupyz.sales.customforms

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.ProductPhotoListAdapter
import com.google.gson.annotations.SerializedName
import kotlin.random.Random

class ImageUploadHandler : FormItemHandler, MultipleImageUploadListener,
        ProductPhotoListAdapter.OnImageDeleteListener {
    private lateinit var addPhotoListAdapter: ProductPhotoListAdapter
    private var photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private var multiplePicCount = 0

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private var context: Context? = null
    private var supportFragmentManager: FragmentManager? = null

    private var formItemModels: MutableList<NameAndValueSetInfoModel>? = null

    private var formItem: FormItemsItem? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun handleCreationFormItem(context: Context, formItem: FormItemsItem, binding: FormBinding,
                                        formItemModels: MutableList<NameAndValueSetInfoModel>,
                                        supportFragmentManager: FragmentManager) {

        this.context = context
        this.supportFragmentManager = supportFragmentManager
        this.formItemModels = formItemModels
        this.formItem = formItem

        inputParams.setMargins(20, 30, 50, 0)

        val recycler = RecyclerView(context)
        val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(25, 15, 0, 0)
        recycler.layoutParams = params
        recycler.layoutManager = GridLayoutManager(context, 3)
        addPhotoListAdapter = ProductPhotoListAdapter(photoModelList, this, false)
        recycler.adapter = addPhotoListAdapter
        binding.formLayout.addView(recycler)

        recycler.layoutParams = inputParams

        photoModelList.clear()
        photoModelList.add(null)
        addPhotoListAdapter.notifyItemInserted(0)

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.imgUrls.isNullOrEmpty().not()){
                it.imgUrls?.forEach {  url ->
                    photoModelList.add(AddedPhotoModel(
                        imageId = Random.nextInt(),
                        imagePath = url,
                        onEditProduct = true
                    ))
                }
                addPhotoListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateModelValue(fieldName: String?, value: ArrayList<AddedPhotoModel?>) {
        // Find the model associated with the field name and update its value
        val model = formItemModels?.find { it.name == fieldName }
        val stringBuilder = StringBuilder()
        value.filterNotNull().forEachIndexed { index, addedPhotoModel ->
            if (addedPhotoModel.onEditProduct.not()) {
                stringBuilder.append(addedPhotoModel.imagePath)
                if (index != value.filterNotNull().size - 1) {
                    stringBuilder.append(",")
                }
            }
        }

        model?.value = stringBuilder.toString()
    }

    override fun onCameraUpload(
            fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAll { it == null }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoModelList.removeIf { it == null }
            }
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            addPhotoListAdapter.notifyItemInserted(photoModelList.size)

            if (photoModelList.size < 6) {
                photoModelList.add(0,null)
                addPhotoListAdapter.notifyItemInserted(photoModelList.size)
            }

            updateModelValue(formItem?.fieldProps?.name, photoModelList)

        } else {
            Toast.makeText(
                    context, context?.getString(R.string.upload_max_six_images),
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }

    override fun onGallerySingleUpload(
            fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            addPhotoListAdapter.notifyItemInserted(photoModelList.size)

            if (photoModelList.size < 6) {
                photoModelList.add(null)
                addPhotoListAdapter.notifyItemInserted(photoModelList.size)
            }

            updateModelValue(formItem?.fieldProps?.name, photoModelList)

        } else {
            Toast.makeText(context, "getString(R.string.upload_max_six_images)",
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }

    override fun onGalleryMultipleUpload(
            fileList: List<String>
            ?
    ) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                addedPhotoModel.isDisplayPicEnable = false
                photoModelList.add(addedPhotoModel)

                addPhotoListAdapter.notifyItemInserted(photoModelList.size)
            }

            if (photoModelList.size < 6) {
                photoModelList.add(null)
                addPhotoListAdapter.notifyItemInserted(photoModelList.size)
            }

            multiplePicCount += fileList.size

            updateModelValue(formItem?.fieldProps?.name, photoModelList)

        } else {
            Toast.makeText(
                    context,
                    context?.getString(R.string.upload_max_six_images),
                    Toast.LENGTH_SHORT
            )
                    .show()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteImage(
            position: Int, key: Long?
                              ) {
        if (photoModelList.size == 1) {
            photoModelList.clear()
            pics.clear()
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            photoModelList.removeAt(position)
            addPhotoListAdapter.notifyItemRemoved(position)
            addPhotoListAdapter.notifyDataSetChanged()

            if (pics.isNotEmpty()) {
                pics.removeAt(position)
            }
//            if (photoModelList.size < 6 && photoModelList[photoModelList.size - 1] != null) {
//                photoModelList.add(null)
//                addPhotoListAdapter.notifyItemInserted(photoModelList.size)
//            }

            updateModelValue(formItem?.fieldProps?.name, photoModelList)
        }
        multiplePicCount--
    }

    override fun onAddImage() {
        val fragment = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)

        val bundle = Bundle()
        bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
        fragment.arguments = bundle

        supportFragmentManager?.let {
            fragment.show(
                    it, AppConstant.IMAGE_UPLOAD_TAG
            )
        }
    }
}