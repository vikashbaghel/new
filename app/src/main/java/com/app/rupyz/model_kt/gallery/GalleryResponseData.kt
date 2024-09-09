package com.app.rupyz.model_kt.gallery

data class GalleryResponseData(
    val `data`: List<PictureData?>?=null,
    var error: Boolean?=null,
    var message: String?=null
)