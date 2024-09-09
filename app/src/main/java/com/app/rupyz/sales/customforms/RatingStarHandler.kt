package com.app.rupyz.sales.customforms

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.app.rupyz.generic.helper.CustomRatingBar
import com.app.rupyz.generic.helper.isParsableInt
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel


// Implement specific handlers for each form item type
class RatingStarHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleCreationFormItem(
        context: Context,
        formItem: FormItemsItem,
        binding: FormBinding,
        formItemModels: MutableList<NameAndValueSetInfoModel>,
        supportFragmentManager: FragmentManager,
        picsUrls: ArrayList<PicMapModel>
    ) {
        inputParams.setMargins(50, 30, 50, 0)

        val existingModel = formItemModels.find { it.name == formItem.fieldProps?.name }

        val inputProps = formItem.inputProps ?: return

        // Get the star count from input props
        val starCount = inputProps.count ?: 5

        // Create a rating view (you can replace this with your custom rating view or library)
        val ratingView = CustomRatingBar(context)

        ratingView.layoutParams = inputParams

        if (starCount > 5) {
            ratingView.ratingSize = (Resources.getSystem().displayMetrics.widthPixels / (starCount + (starCount / 2)))
        } else {
            ratingView.ratingSize = (Resources.getSystem().displayMetrics.widthPixels / (starCount * 2))
        }

        ratingView.numStars = starCount
        ratingView.ratingMargin = 7
        ratingView.isIndicator = false
        ratingView.rating = 0f

        // Add the rating view to the form layout
        binding.formLayout.addView(ratingView)

        // Set up listeners to update the model value when the rating changes
        ratingView.onRatingChangedListener = CustomRatingBar.OnRatingChangedListener { oldRating, newRating ->
            existingModel?.value = "${newRating.toInt()}/${inputProps.count ?: 5}"
        }

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.value.isNullOrBlank().not()){
                if (it.value?.isParsableInt() == true){
                    ratingView.rating = it.value?.toInt()?.toFloat()?:0f
                    existingModel?.value = "${it.value?.toInt()}/${inputProps.count ?: 5}"
                }
            }
        }
    }
}