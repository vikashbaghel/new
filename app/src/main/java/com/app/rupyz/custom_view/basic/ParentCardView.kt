package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.app.rupyz.R
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt

class ParentCardView : MaterialCardView {

    private val DEFAULT_RADIUS = 10f
    private val DEFAULT_ELEVATION = 12f
    private val DEFAULT_STROKE_WIDTH = 2f
    private var linearLayoutChild: LinearLayoutCompat? = null

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ParentCardView)

       setLayoutParams()
        setPadding(0, 20, 0, 20)
        radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_RADIUS,
            resources.displayMetrics
        )
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_STROKE_WIDTH,
            resources.displayMetrics
        ).roundToInt()
        strokeColor =
            ColorUtils.setAlphaComponent(resources.getColor(R.color.color_000000, null), 10)
        cardElevation = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_ELEVATION,
            resources.displayMetrics
        )
        elevation = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_ELEVATION,
            resources.displayMetrics
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            outlineSpotShadowColor =
                ColorUtils.setAlphaComponent(resources.getColor(R.color.color_000000, null), 10)
            outlineAmbientShadowColor =
                ColorUtils.setAlphaComponent(resources.getColor(R.color.color_000000, null), 10)
        }

        useCompatPadding = false
        clipChildren = true
        clipToPadding = true
        clipToOutline = true

        try {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is LinearLayout) {
                    removeView(view)
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        linearLayoutChild = LinearLayoutCompat(context, attrs)
        linearLayoutChild?.background = resources.getDrawable(R.drawable.card_view_custom_form_background, null)
        linearLayoutChild?.let { setInnerLayoutParams(it) }
        linearLayoutChild?.setPadding(32, 0, 32, 32)
        linearLayoutChild?.orientation = LinearLayoutCompat.VERTICAL
        linearLayoutChild?.removeAllViews()
        addView(linearLayoutChild)
        a.recycle()
    }

    fun addChild(child: View?) {
        linearLayoutChild?.addView(child)
    }


    private fun setLayoutParams() {
        if (layoutParams is LinearLayoutCompat.LayoutParams){
            val inputParams =  LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is android.widget.LinearLayout.LayoutParams){
            val inputParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is FrameLayout.LayoutParams){
            val inputParams =  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is ConstraintLayout.LayoutParams){
            val inputParams =  ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is RelativeLayout.LayoutParams){
            val inputParams =  RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
    }

    private fun setInnerLayoutParams(linearLayoutChild : ViewGroup) {
        if (layoutParams is LinearLayoutCompat.LayoutParams){
            val inputParams =  LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayoutChild.layoutParams = inputParams
        }
        else if (layoutParams is android.widget.LinearLayout.LayoutParams){
            val inputParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayoutChild.layoutParams = inputParams
        }
        else if (layoutParams is FrameLayout.LayoutParams){
            val inputParams =  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayoutChild.layoutParams = inputParams
        }
        else if (layoutParams is ConstraintLayout.LayoutParams){
            val inputParams =  ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayoutChild.layoutParams = inputParams
        }
        else if (layoutParams is RelativeLayout.LayoutParams){
            val inputParams =  RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayoutChild.layoutParams = inputParams
        }
    }
}