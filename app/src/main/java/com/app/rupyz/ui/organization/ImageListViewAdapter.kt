package com.app.rupyz.ui.organization

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.app.rupyz.R
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.ImageUtils

class ImageListViewAdapter(private var list: MutableList<ImageViewModel>) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(collection.context)
        val layout = inflater.inflate(R.layout.item_org_image, collection, false) as ViewGroup
        val ivOrg = layout.findViewById<ImageView>(R.id.iv_org)
        ImageUtils.loadImage(list[position].image_url, ivOrg)
        collection.addView(layout)
        return layout
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}