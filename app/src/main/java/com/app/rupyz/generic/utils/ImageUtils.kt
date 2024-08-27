package com.app.rupyz.generic.utils

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.app.rupyz.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object ImageUtils {

    fun loadImage(url: String?, imageView: ImageView) {
        if (imageView.context != null) {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(R.mipmap.no_photo_available)
                .diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.IMMEDIATE)
                .into(imageView)
        }
    }

    fun loadImageWithPlaceHolder(url: String?, imageView: ImageView, placeholder: BitmapDrawable?) {
        if (imageView.context != null) {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.IMMEDIATE)
                .into(imageView)
        }
    }

    fun loadTeamImage(url: String?, imageView: ImageView) {
        if (imageView.context != null) {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(0.5f)
                .timeout(4000)
                .placeholder(R.mipmap.ic_user_default)
                .error(R.mipmap.ic_user_default)
                .diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.IMMEDIATE)
                .into(imageView)
        }
    }

    fun loadBannerImage(url: String?, imageView: ImageView) {
        if (imageView.context != null) {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(R.mipmap.no_photo_available)
                .diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.IMMEDIATE)
                .into(imageView)
        }
    }

    fun loadCustomImage(url: String?, imageView: ImageView, placeholder: Int) {
        if (imageView.context != null) {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.IMMEDIATE)
                .into(imageView)
        }
    }


}