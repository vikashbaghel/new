package com.app.rupyz.ui.imageupload;

public interface ImageUploadListener {
    void onCameraUpload(String fileName);
    void onGalleryUpload(String fileName);
    default void onPreview(String imageType, String imageUrl){

    }
}
