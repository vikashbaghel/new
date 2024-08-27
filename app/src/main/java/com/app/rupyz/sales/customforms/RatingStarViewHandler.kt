package com.app.rupyz.sales.customforms

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.app.rupyz.generic.helper.CustomRatingBar
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

// Implement specific handlers for each form item type
class RatingStarViewHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleViewFormItem(context: Context, formItem: NameAndValueSetInfoModel,
                                    binding: FormBinding, supportFragmentManager: FragmentManager) {
        super.handleViewFormItem(context, formItem, binding, supportFragmentManager)
        inputParams.setMargins(0, 20, 0, 0)


        var starCount = 5
        var ratingCount = 0f
        if (formItem.value.isNullOrEmpty().not()) {
            if (formItem.value!!.contains("/")) {
                val value = formItem.value!!.split("/")
                ratingCount = value[0].toFloat()
                starCount = value[1].toInt()
            } else {
                ratingCount = formItem.value!!.toFloat()
            }
        }

        // Create a rating view (you can replace this with your custom rating view or library)
        val ratingView = CustomRatingBar(context)

        ratingView.layoutParams = inputParams

        if (starCount > 5) {
            ratingView.ratingSize = (Resources.getSystem().displayMetrics.widthPixels / (starCount + (starCount / 2)))
        } else {
            ratingView.ratingSize = (Resources.getSystem().displayMetrics.widthPixels / (starCount * 2))
        }
        ratingView.numStars = starCount
        ratingView.isIndicator = true
        ratingView.ratingMargin = 7
        ratingView.rating = ratingCount


        // Add the rating view to the form layout
        binding.formLayout.addView(ratingView)
    }
}