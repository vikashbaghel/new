package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.generic.helper.CustomRatingBar
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

class RatingBar : LinearLayoutCompat {


    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType = FormItemType.RATING

    private var ratingBar: CustomRatingBar? = null
    private var isIndicator: Boolean = false
    private var rating: Float = 0f
    private var ratingCount: Int = 5
    private var ratingMargin: Int = 0
    private var ratingBarSize: Int = 0

    private val IS_INDICATOR_DEFAULT = false
    private val RATING_DEFAULT = 0f
    private val RATING_COUNT_DEFAULT = 5
    private var RATING_SIZE_DEFAULT =
        (Resources.getSystem().displayMetrics.widthPixels / (RATING_COUNT_DEFAULT * 2))
    private val RATING_MARGIN_DEFAULT = 7

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {

        try {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is CustomRatingBar) {
                    removeView(view)
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        ratingBar = if (attrs != null) {
            CustomRatingBar(context, attrs)
        } else {
            CustomRatingBar(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)
        isIndicator = a.getBoolean(R.styleable.RatingBar_indicator, IS_INDICATOR_DEFAULT)
        rating = a.getFloat(R.styleable.RatingBar_selectedStar, RATING_DEFAULT)
        ratingCount = a.getInt(R.styleable.RatingBar_starCount, RATING_COUNT_DEFAULT)
        ratingMargin = a.getDimension(
            R.styleable.RatingBar_ratingBarMargin,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                RATING_MARGIN_DEFAULT.toFloat(),
                resources.displayMetrics
            )
        ).toInt()

        RATING_SIZE_DEFAULT =  (Resources.getSystem().displayMetrics.widthPixels / (ratingCount + (ratingCount / 2)))

        ratingBarSize = a.getDimension(
            R.styleable.RatingBar_ratingStarSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                RATING_SIZE_DEFAULT.toFloat(),
                resources.displayMetrics
            )
        ).toInt()

        ratingBar?.ratingSize = ratingBarSize
        ratingBar?.numStars = ratingCount
        ratingBar?.isIndicator = isIndicator
        ratingBar?.ratingMargin = ratingMargin
        ratingBar?.rating = rating


        addView(ratingBar)
        a.recycle()
    }


    private fun setDefaultStyle() {
        val strCount = formFields?.inputProps?.count ?: ratingCount
        ratingBar?.numStars = strCount

        ratingBarSize = (Resources.getSystem().displayMetrics.widthPixels / (strCount + (strCount / 2 )))

        ratingBar?.ratingSize = ratingBarSize

        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 80, 0)
        layoutParams = inputParams
    }

    fun setRating(rating: Float) {
        this.rating = rating
        ratingBar?.rating = rating
    }

    fun setIsIndicator(isIndicator: Boolean) {
        this.isIndicator = isIndicator
        ratingBar?.isIndicator = isIndicator
    }

    fun setRatingMargin(ratingMargin: Int) {
        this.ratingMargin = ratingMargin
        ratingBar?.ratingMargin = ratingMargin
    }

    fun setRatingCount(ratingCount: Int) {
        this.ratingCount = ratingCount
        this.ratingBarSize = if ((ratingBar?.numStars ?: ratingCount) > 5) {
            (Resources.getSystem().displayMetrics.widthPixels / (ratingCount + (ratingCount / 2)))
        } else {
            (Resources.getSystem().displayMetrics.widthPixels / (ratingCount * 2))
        }
        ratingBar?.ratingSize = this.ratingBarSize
    }

    fun setRatingBarSize(ratingBarSize: Int) {
        this.ratingBarSize = ratingBarSize
        ratingBar?.ratingSize = ratingBarSize
    }

    fun setRatingBar(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setDefaultStyle()
    }

    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = ratingBar?.rating?.toInt().toString()
        return model
    }


    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.DATE_TIME_PICKER
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

    fun setValue(it: String) {
        try {
            ratingBar?.rating = it.toFloat()
        } catch (_: Exception) {
        }
    }

}