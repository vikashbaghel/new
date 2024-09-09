package com.app.rupyz.generic.helper

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation


fun View.showWithRightToLeftAnimation() {
    this.visibility = View.VISIBLE
    val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
    val animator = ObjectAnimator.ofFloat(this, "translationX", screenWidth, 0f)
    animator.duration = 300
    animator.start()
}

fun View.hideWithRightToLeftAnimation() {
    val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
    val animator = ObjectAnimator.ofFloat(this, "translationX", 0f, screenWidth)
    animator.duration = 300
    animator.start()
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            this@hideWithRightToLeftAnimation.visibility = View.GONE
        }

        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
    })
}

fun View.visibility() {
    this@visibility.visibility = View.VISIBLE
}

fun View.gone() {
    this@gone.visibility = View.GONE
}

fun collapse(v: View) {
    val initialHeight = v.measuredHeight

    val a: Animation = object : Animation(
    ) {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms
    a.duration =
        (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
    v.startAnimation(a)
}

fun expand(v: View) {
    v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    val targetHeight = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation(
    ) {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            v.layoutParams.height = if (interpolatedTime == 1f
            ) WindowManager.LayoutParams.WRAP_CONTENT
            else (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    //a.duration = 300
    // 1dp/ms
    a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
    v.startAnimation(a)
}