package com.app.rupyz.generic.helper

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Element

object BlurUtils {
    private const val BLUR_RADIUS = 25f
    private const val BLUR_REPEAT = 4

    fun blur(context: Context, image: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(image)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, image)
        val output = Allocation.createFromBitmap(renderScript, outputBitmap)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setRadius(BLUR_RADIUS)

        // Apply the blur multiple times
        for (i in 0 until BLUR_REPEAT) {
            scriptIntrinsicBlur.setInput(input)
            scriptIntrinsicBlur.forEach(output)
            output.copyTo(outputBitmap)
            input.copyFrom(outputBitmap)  // Prepare input for next blur round
        }

        renderScript.destroy()
        return outputBitmap
    }
}