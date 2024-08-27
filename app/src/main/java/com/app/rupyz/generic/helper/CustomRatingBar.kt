@file:Suppress("MemberVisibilityCanBePrivate")

package com.app.rupyz.generic.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.app.rupyz.R
import com.app.rupyz.generic.helper.CustomRatingBar.SavedState.Companion.IS_INDICATOR_DEFAULT
import com.app.rupyz.generic.helper.CustomRatingBar.SavedState.Companion.RATING_COUNT_DEFAULT
import com.app.rupyz.generic.helper.CustomRatingBar.SavedState.Companion.RATING_DEFAULT
import com.app.rupyz.generic.helper.CustomRatingBar.SavedState.Companion.RATING_MARGIN_DEFAULT
import com.app.rupyz.generic.helper.CustomRatingBar.SavedState.Companion.RATING_SIZE_DEFAULT

open class CustomRatingBar : View , View.OnTouchListener {
	
	var isIndicator: Boolean = false
		set(value) {
			field = value
			setOnTouchListener(if (isIndicator) null else this)
		}
	var rating: Float = 0f
		set(value) {
			var newRating = value
			if (newRating < 0) {
				newRating = 0f
			} else if (newRating > numStars) {
				newRating = numStars.toFloat()
			}
			field = value
			onRatingChangedListener?.onRatingChange(rating, newRating)
			invalidate()
		}
	var onRatingChangedListener: OnRatingChangedListener? = null
	private var bitmapEmpty: Bitmap? = null
	private var bitmapHalf: Bitmap? = null
	private var bitmapFilled: Bitmap? = null
	private val rect = Rect()
	var numStars: Int = 5
		set(value) {
			field = value
			requestLayout()
		}
	var ratingSize: Int = 0
		set(value) {
			field = value
			requestLayout()
		}
	var ratingMargin: Int = 0
		set(value) {
			field = value
			requestLayout()
		}
	
	constructor(context: Context) : super(context) {
		init(context, null)
	}
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(context, attrs)
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init(context, attrs)
	}
	
	private fun init(context: Context, attrs: AttributeSet?) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar)
		isIndicator = typedArray.getBoolean(R.styleable.CustomRatingBar_isIndicator, IS_INDICATOR_DEFAULT)
		rating = typedArray.getFloat(R.styleable.CustomRatingBar_rating, RATING_DEFAULT)
		numStars = typedArray.getInteger(R.styleable.CustomRatingBar_ratingCount, RATING_COUNT_DEFAULT)
		bitmapEmpty = BitmapFactory.decodeResource(context.resources, typedArray.getResourceId(R.styleable.CustomRatingBar_ratingEmpty, R.drawable.ic_empty_star))
		bitmapHalf = BitmapFactory.decodeResource(context.resources, typedArray.getResourceId(R.styleable.CustomRatingBar_ratingHalf, R.drawable.ic_empty_star))
		bitmapFilled = BitmapFactory.decodeResource(context.resources, typedArray.getResourceId(R.styleable.CustomRatingBar_ratingFilled, R.drawable.ic_filled_star))
		ratingSize = typedArray.getDimension(R.styleable.CustomRatingBar_ratingSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RATING_SIZE_DEFAULT.toFloat(), resources.displayMetrics)).toInt()
		ratingMargin = typedArray.getDimension(R.styleable.CustomRatingBar_ratingMargin, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RATING_MARGIN_DEFAULT.toFloat(), resources.displayMetrics)).toInt()
		
		if (ratingSize < 0) {
			throw IllegalArgumentException("Rating size < 0 is not possible")
		}
		if (numStars < 1) {
			throw IllegalArgumentException("Rating count < 1 is not possible")
		}
		
		typedArray.recycle()
		
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		setMeasuredDimension(resolveSize(ratingSize * numStars + ratingMargin * (numStars - 1), widthMeasureSpec) , resolveSize(ratingSize, heightMeasureSpec))
	}
	
	override fun onDraw(canvas: Canvas) {
		setOnTouchListener(if (isIndicator) null else this)
		if (bitmapEmpty != null && bitmapHalf != null && bitmapFilled != null) {
			rect.set(0, 0, ratingSize, ratingSize)
			var fullDrawablesCount = rating.toInt()
			val emptyDrawablesCount = numStars - Math.round(rating)
			val isDrawableHalf = rating - fullDrawablesCount >= 0.25f && rating - fullDrawablesCount < 0.75f
			
			if (rating - fullDrawablesCount >= 0.75f) {
				fullDrawablesCount++
			}
			drawBitmaps(fullDrawablesCount, canvas, isDrawableHalf, emptyDrawablesCount)
		}
	}
	
	private fun drawBitmaps(fullDrawablesCount: Int, canvas: Canvas, isDrawableHalf: Boolean, emptyDrawablesCount: Int) {
		for (i in 0 until fullDrawablesCount) {
			drawRating(canvas, bitmapFilled as Bitmap)
		}
		if (isDrawableHalf) {
			drawRating(canvas, bitmapHalf as Bitmap)
		}
		for (i in 0 until emptyDrawablesCount) {
			drawRating(canvas, bitmapEmpty as Bitmap)
		}
	}
	
	private fun drawRating(canvas: Canvas, bitmap: Bitmap) {
		canvas.drawBitmap(bitmap, null, rect, null)
		rect.offset(ratingSize + ratingMargin, 0)
	}
	
	override fun onTouch(v: View, event: MotionEvent): Boolean {
		when (event.actionMasked) {
			MotionEvent.ACTION_DOWN -> return true
			MotionEvent.ACTION_UP -> {
				rating = Math.round(event.x / width * numStars + 0.5).toFloat()
				return false
			}
		}
		return super.onTouchEvent(event)
	}
	
	override fun onSaveInstanceState(): Parcelable {
		val savedState = SavedState(super.onSaveInstanceState())
		savedState.rating = rating
		savedState.isIndicator = isIndicator
		return savedState
	}
	
	override fun onRestoreInstanceState(state: Parcelable) {
		if (state is SavedState) {
			super.onRestoreInstanceState(state.superState)
			rating = state.rating
			isIndicator = state.isIndicator
		} else {
			super.onRestoreInstanceState(state)
		}
	}
	
	internal class SavedState : BaseSavedState {
		
		var rating: Float = 0f
		var isIndicator: Boolean = false
		
		constructor(superState: Parcelable?) : super(superState)
		
		private constructor(source: Parcel?) : super(source) {
			this.rating = source?.readFloat() ?: RATING_DEFAULT
			this.isIndicator = source?.readInt() != 0
		}
		
		override fun writeToParcel(dest: Parcel , flags: Int) {
			super.writeToParcel(dest, flags)
			dest.writeFloat(this.rating)
			dest.writeInt(if (this.isIndicator) 1 else 0)
		}
		
		companion object {
			
			const val IS_INDICATOR_DEFAULT = false
			const val RATING_DEFAULT = 2.5f
			const val RATING_COUNT_DEFAULT = 5
			const val RATING_SIZE_DEFAULT = 48
			const val RATING_MARGIN_DEFAULT = 10
			
		}
	}
	
	fun interface OnRatingChangedListener {
		fun onRatingChange(oldRating: Float, newRating: Float)
	}
}

