package com.app.rupyz.generic.helper

import android.content.Context
import android.widget.ScrollView
import android.widget.Toast
import com.google.gson.Gson


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
        it.lowercase().replaceFirstChar {char->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}

fun String?.divideHeadersIntoQueryParams(): Pair<Boolean, Int?>{
    val selectedValue = this?.substringAfter("selected=")?.substringBefore("&").toBoolean()
    val pageValue = this?.substringAfter("page_no=")?.toInt()
    return Pair(selectedValue, pageValue)
}