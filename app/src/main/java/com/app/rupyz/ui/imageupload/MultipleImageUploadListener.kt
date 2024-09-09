package com.app.rupyz.ui.imageupload

interface MultipleImageUploadListener {
    fun onCameraUpload(fileName: String?)
    fun onGallerySingleUpload(fileName: String?)
    fun onGalleryMultipleUpload(fileList: List<String>?)
    fun onUploadPdf() {}
}