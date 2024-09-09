package com.app.rupyz.generic.helper

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

object StringExtension {

    fun getLocationPermissionChangeFromAllowToUsingTheAppText(): SpannableStringBuilder {
        // Define the parts of the text
        val part1 = SpannableString("Location permission changed from ")
        part1.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part2 = SpannableString("Always allow")
        part2.setSpan(StyleSpan(Typeface.BOLD), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part2.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part3 = SpannableString(" to ")
        part3.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part4 = SpannableString("Allow while using this app")
        part4.setSpan(StyleSpan(Typeface.BOLD), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part4.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Combine all parts into a single SpannableStringBuilder
        return SpannableStringBuilder().apply {
            append(part1)
            append(part2)
            append(part3)
            append(part4)
        }
    }

    fun getLocationPermissionChangeFromUsingTheAppToAllowText(): SpannableStringBuilder {
        // Define the parts of the text
        val part1 = SpannableString("Location permission changed from ")
        part1.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part2 = SpannableString("Allow while using this app")
        part2.setSpan(StyleSpan(Typeface.BOLD), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part2.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part3 = SpannableString(" to ")
        part3.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part4 = SpannableString("Always allow")
        part4.setSpan(StyleSpan(Typeface.BOLD), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part4.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Combine all parts into a single SpannableStringBuilder
        return SpannableStringBuilder().apply {
            append(part1)
            append(part2)
            append(part3)
            append(part4)
        }
    }

    fun getLocationPermissionChangeFromAllowToDoNotAllowText(): SpannableStringBuilder {
        // Define the parts of the text
        val part1 = SpannableString("Location permission changed from ")
        part1.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part2 = SpannableString("Allow while using this app")
        part2.setSpan(StyleSpan(Typeface.BOLD), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part2.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part3 = SpannableString(" to ")
        part3.setSpan(ForegroundColorSpan(Color.parseColor("#727176")), 0, part3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val part4 = SpannableString("Don't Allow")
        part4.setSpan(StyleSpan(Typeface.BOLD), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        part4.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, part4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Combine all parts into a single SpannableStringBuilder
        return SpannableStringBuilder().apply {
            append(part1)
            append(part2)
            append(part3)
            append(part4)
        }
    }


    fun trimProductCodeFromScanner(result: String): String {
        return result.replace("RUP-", "").trim()
    }
}