package com.app.rupyz.generic.utils

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.annotations.NotNull

object DimenUtils {
    fun getWindowVisibleHeight(@NotNull activity: AppCompatActivity): Int {
        val rectangle = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.bottom - rectangle.top
    }
}