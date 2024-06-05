package com.app.rupyz.ui.organization.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.VISIBLE
import com.app.rupyz.databinding.ActivityOrgPhotosViewBinding
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.ui.organization.ImageListViewAdapter


class OrgPhotosViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrgPhotosViewBinding
    private var orgImageListModel: OrgImageListModel? = null
    private var imageList: MutableList<ImageViewModel> = ArrayList()
    private lateinit var adapter: ImageListViewAdapter
    private var position: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgPhotosViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.PRODUCT_INFO)) {
            orgImageListModel = intent.getParcelableExtra(AppConstant.PRODUCT_INFO)
        }

        if (intent.hasExtra(AppConstant.IMAGE_POSITION)) {
            position = intent.getIntExtra(AppConstant.IMAGE_POSITION, 0)
        }

        if (orgImageListModel != null) {
            orgImageListModel!!.data?.let { imageList.addAll(it) }
        }

        adapter = ImageListViewAdapter(imageList)

        binding.vpPhotos.adapter = adapter
        binding.vpPhotos.currentItem = position

        if (imageList.size > 1) {
            binding.tvPosition.text = "" + (position + 1) + "/" + imageList.size
        } else {
            binding.tvPosition.visibility = View.INVISIBLE
        }

        binding.vpPhotos.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.tvPosition.text = "" + (position + 1) + "/" + imageList.size

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.ivBack.setOnClickListener { finish() }
    }
}