package com.app.rupyz.sales.customforms

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class ImageViewHandler : FormItemHandler, ProductImageViewPagerAdapter.ProductImageClickListener {
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private var context: Context? = null
    override fun handleViewFormItem(context: Context, formItem: NameAndValueSetInfoModel, binding: FormBinding, supportFragmentManager: FragmentManager) {
        super.handleViewFormItem(context, formItem, binding, supportFragmentManager)
        inputParams.setMargins(0, 20, 0, 0)

        val recycler = RecyclerView(context)
        val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(25, 15, 0, 0)
        recycler.layoutParams = params


        if (formItem.imgUrls.isNullOrEmpty().not()) {

            formItem.imgUrls?.forEach {
                val picMap = PicMapModel()
                picMap.url = it
                pics.add(picMap)
            }

            recycler.layoutManager = GridLayoutManager(context, 6)
            addPhotoListAdapter = LrPhotoListAdapter(pics, this)
            recycler.adapter = addPhotoListAdapter

            binding.formLayout.addView(recycler)

            recycler.layoutParams = inputParams
        }
    }

    override fun onPdfClick(position: Int,url:String) {
        super.onPdfClick(position,url)
        if (url!=null)
        {
            FileUtils.openPdf(url, context!!)
        }


    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            context?.startActivity(
                    Intent(context, OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

}