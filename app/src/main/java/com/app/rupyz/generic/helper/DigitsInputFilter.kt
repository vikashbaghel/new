package com.app.rupyz.generic.helper

import android.text.InputFilter
import android.text.Spanned

class DigitsInputFilter(
    private var mMaxIntegerDigitsLength: Int,
    private var mMaxDigitsAfterLength: Int
) : InputFilter {
    private val dot = "."

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val allText = getAllText(source, dest, dstart)
        val onlyDigitsText = getOnlyDigitsPart(allText)
        return if (allText.isEmpty()) {
            null
        } else {
            val enteredValue: Double = try {
                onlyDigitsText.toDouble()
            } catch (e: NumberFormatException) {
                return ""
            }
            checkValueRule(enteredValue, onlyDigitsText)
        }
    }


    private fun checkValueRule(enteredValue: Double, onlyDigitsText: String): CharSequence? {
        return handleInputRules(onlyDigitsText)
    }

    private fun handleInputRules(onlyDigitsText: String): CharSequence? {
        return if (isDecimalDigit(onlyDigitsText)) {
            checkRuleForDecimalDigits(onlyDigitsText)
        } else {
            checkRuleForIntegerDigits(onlyDigitsText.length)
        }
    }

    private fun isDecimalDigit(onlyDigitsText: String): Boolean {
        return onlyDigitsText.contains(dot)
    }

    private fun checkRuleForDecimalDigits(onlyDigitsPart: String): CharSequence? {
        val dotIndex = onlyDigitsPart.indexOf('.')

        val beforeDotPart = onlyDigitsPart.substring(0, dotIndex)
        val afterDotPart = onlyDigitsPart.substring(dotIndex + 1)

        return if (beforeDotPart.length > mMaxIntegerDigitsLength || afterDotPart.length > mMaxDigitsAfterLength) {
            ""
        } else {
            null
        }
    }

    private fun checkRuleForIntegerDigits(allTextLength: Int): CharSequence? {
        return if (allTextLength > mMaxIntegerDigitsLength) {
            ""
        } else null
    }

    private fun getOnlyDigitsPart(text: String): String {
        return text.replace("[^0-9?!\\.]".toRegex(), "")
    }

    private fun getAllText(source: CharSequence, dest: Spanned, dstart: Int): String {
        var allText = ""
        if (dest.toString().isNotEmpty()) {
            allText = if (source.toString().isEmpty()) {
                deleteCharAtIndex(dest, dstart)
            } else {
                StringBuilder(dest).insert(dstart, source).toString()
            }
        }
        return allText
    }

    private fun deleteCharAtIndex(dest: Spanned, dstart: Int): String {
        val builder = StringBuilder(dest)
        builder.deleteCharAt(dstart)
        return builder.toString()
    }
}