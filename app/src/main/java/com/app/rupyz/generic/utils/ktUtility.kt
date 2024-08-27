package com.app.rupyz.generic.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.app.rupyz.R

object ktUtility {
	
	fun whiteGradient(activity: Context): GradientDrawable {
		val colors: IntArray = intArrayOf(
				ContextCompat.getColor(activity, R.color.white),
				ContextCompat.getColor(activity, R.color.white),
		                                 )
		val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TL_BR, colors)
		val arr2 = FloatArray(8)
		arr2[0] = 70f
		arr2[1] = 40f
		gradientDrawable.cornerRadii = arr2
		gradientDrawable.setStroke(3, ContextCompat.getColor(activity, R.color.white))
		
		return gradientDrawable
	}
	
}