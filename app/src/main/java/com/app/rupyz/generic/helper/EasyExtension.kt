package com.app.rupyz.generic.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.widget.addTextChangedListener
import com.app.rupyz.sales.gallery.adapter.SafeClickListener
import com.google.gson.Gson
import java.util.regex.Pattern


fun Context.makeToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}

inline fun <reified T> Gson.fromJson(json: String?): T {
    return this.fromJson(json, T::class.java)
}


fun Long.toHoursMinutesSeconds(): String {
    // Convert milliseconds to seconds, minutes, and hours
    val totalSeconds = this / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60

    // Calculate remaining minutes and seconds after calculating total hours
    val remainingMinutes = totalMinutes % 60
    val remainingSeconds = totalSeconds % 60

    // Format the time into hh:mm:ss format
    return String.format("%02d:%02d", totalHours, remainingMinutes, remainingSeconds)
}

fun getTimeDifference(startTime: Long, endTime: Long): String {
    // Calculate the difference in milliseconds
    val differenceInMillis = endTime - startTime

    // Convert milliseconds to seconds, minutes, and hours
    val totalSeconds = differenceInMillis / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60

    // Calculate remaining minutes and seconds after calculating total hours
    val remainingMinutes = totalMinutes % 60
    val remainingSeconds = totalSeconds % 60

    // Format the time into hh:mm:ss format
    return String.format("%02d:%02d", totalHours, remainingMinutes)
}

fun String.toCamelCaseWithSpaces(): String {
    return this.split("_").joinToString(" ") {
        it.lowercase().replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}

fun String?.divideHeadersIntoQueryParams(): Pair<Boolean, Int?> {
    val selectedValue = this?.substringAfter("selected=")?.substringBefore("&").toBoolean()
    val pageValue = this?.substringAfter("page_no=")?.toInt()
    return Pair(selectedValue, pageValue)
}

fun View.hideView() {
    this.visibility = View.GONE
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.invisibleView() {
    this.visibility = View.INVISIBLE
}

fun View.disable() {
    this.isEnabled = false
    this.isClickable = false
}


fun View.disableAndAlpha() {
    this.alpha = 0.9f
    this.isEnabled = false
    this.isClickable = false
}


fun View.disableOnlyAlpha() {
    this.alpha = 0.9f
}

fun View.enable() {
    this.isEnabled = true
    this.isClickable = true
    this.alpha = 1f
}

/**
 * Returns `true` if enum T contains an entry with the specified name.
 */
inline fun <reified T : Enum<T>> enumContains(name: String): Boolean {
    return enumValues<T>().any { it.name == name }
}

typealias ColorInt = Int

fun ColorInt.applyAlpha(alpha: Float): ColorInt {
    // assert the alpha value to be between 0.0 and 1.0
    val alphaValue = alpha.coerceIn(0.0f, 1.0f)
    // Convert the asserted alpha value to an integer (0 to 255)
    val alphaInt = (alphaValue * 255).toInt()
    // Use bitwise operations to combine the alpha value with the original color
    return (this and 0x00FFFFFF) or (alphaInt shl 24)
}

fun Int.toDp(): Int {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val fpixels = metrics.density * this
    return (fpixels + 0.5f).toInt()
}


fun Float.toDp(): Int {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val fpixels = metrics.density * this
    return (fpixels + 0.5f).toInt()
}

fun EditText.addDelayedTextChangeListener(delayMillis: Long = 1000, action: (Editable?) -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    this.addTextChangedListener {
        runnable?.let { handler.removeCallbacks(it) }
        runnable = Runnable { action(it) }
        runnable?.let { handler.postDelayed(it, delayMillis) }
    }
}

fun String.asBitmap(
    context: Context,
    textSize: Float,
    textColor: Int,
    backgroundColor: Int
): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        textSize,
        context.resources.displayMetrics
    )
    paint.color = textColor
    paint.textAlign = Paint.Align.CENTER
    paint.isAntiAlias = true
    paint.textAlign = Paint.Align.CENTER

    val textBounds = Rect()
    paint.getTextBounds(this, 0, this.length, textBounds)

    val diameter = (Math.max(textBounds.width(), textBounds.height()) * 1.5).toInt()
    val radius = diameter / 2
    val image = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)

    val backgroundPaint = Paint()
    backgroundPaint.color = backgroundColor
    canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), backgroundPaint)

    val xPos = canvas.width / 2
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
    canvas.drawText(this, xPos.toFloat(), yPos.toFloat(), paint)

    return image
}


fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return Pattern.matches(emailRegex, this)
}

fun String.isValidUrl(): Boolean {
    return Patterns.WEB_URL.matcher(this)
        .matches() && (this.startsWith("https://") || this.startsWith("http://"))
}

fun String.removeLastComma(): String {
    val lastIndex = this.lastIndexOf(',')
    return if (lastIndex != -1) {
        this.removeRange(lastIndex, lastIndex + 1)
    } else {
        this
    }
}

fun String.isParsableInt(): Boolean {
    return try {
        this.toInt()
        true
    } catch (_: Exception) {
        false
    } catch (_: NumberFormatException) {
        false
    }
}

fun decimalFormat(value: Double?): String {
    return "%.2f".format(value)
}

fun String.toSafeDoubleOrZero(): Double {
    val parsedDouble = this.toDoubleOrNull() ?: return 0.0
    return if (parsedDouble.isNaN()) 0.0 else parsedDouble
}


fun View.hideKeyboard() {
    val imm = this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideKeyboard() {
    val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0);

}

fun String.extractNumber(): Int {
    val regex = "\\d+".toRegex()
    val concatenatedNumbers = regex.findAll(this).map { it.value }.toList()
    return if (concatenatedNumbers.isEmpty().not() && concatenatedNumbers[0].isParsableInt()) {
        concatenatedNumbers[0].toInt()
    } else {
        -1
    }
}

fun String.isValidPhoneNumber(): Boolean {
    // Regular expression pattern for phone numbers
    val phoneNumberPattern = "^[+]?[6-9][0-9]{9,12}\$".toRegex()
    return this.matches(phoneNumberPattern)
}

fun View.setSafeOnClickListener(interval: Int = 500, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(interval, onSafeClick)
    setOnClickListener(safeClickListener)
}


fun View.showViewSlideIn(translationDistance: Float = 100f, duration: Long = 500) {
    this.apply {
        visibility = View.VISIBLE
        alpha = 0f
        translationY = translationDistance
        animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(android.view.animation.OvershootInterpolator()) // Adds a slight bounce effect
                .setListener(null)
                .start()
    }
}

fun View.hideViewSlideOut(translationDistance: Float = 100f, duration: Long = 500) {
    this.animate()
            .translationY(translationDistance)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(android.view.animation.AnticipateInterpolator()) // Adds a slight recoil effect
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
            .start()
}

fun <T, K> List<T>.subtractBy(other: List<T>, keySelector: (T) -> K): List<T> {
    val otherKeys = other.map(keySelector).toSet()
    return this.filterNot { item -> keySelector(item) in otherKeys }
}

fun String.isCommaSeparatedIntegers(): Boolean {
    // Define the regular expression pattern
    val pattern = "^\\d+(,\\d+)*$".toRegex()

    return if (this.isEmpty().not()) {
        // Check if the input string matches the pattern
        this.matches(pattern)
    } else {
        false
    }
}

fun String.splitString(delimiter: Char): List<String> {
    val result = ArrayList<String>() // Use ArrayList for better performance
    var startIndex = 0

    for (i in this.indices) {
        if (this[i] == delimiter) {
            result.add(this.substring(startIndex, i)) // Add substring directly
            startIndex = i + 1 // Update startIndex to the next character after the delimiter
        }
    }

    // Add the last substring if there's any remaining part
    if (startIndex < this.length) {
        result.add(this.substring(startIndex))
    }

    return result
}