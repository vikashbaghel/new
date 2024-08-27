package com.app.rupyz.sales.gallery

import android.os.Bundle
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityPictureGalleryBinding
import com.app.rupyz.generic.base.BaseActivity

class PictureGalleryActivity : BaseActivity() {
    private lateinit var binding: ActivityPictureGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(R.id.frame_container, AllPictureListFragment())
        }

        binding.imgClose.setOnClickListener {
            finish()
            //AppConstant.SELECT_COUNT=2
        }
    }
}