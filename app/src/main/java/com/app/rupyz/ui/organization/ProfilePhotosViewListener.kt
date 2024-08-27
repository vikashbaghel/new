package com.app.rupyz.ui.organization

import com.app.rupyz.generic.model.org_image.ImageViewModel

interface ProfilePhotosViewListener {
    fun onViewPhotos(position: Int)
    fun onDeletePhotos(
        position: Int,
        imageViewModel: ImageViewModel
    )
}