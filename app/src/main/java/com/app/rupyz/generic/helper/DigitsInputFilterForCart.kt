package com.app.rupyz.generic.helper

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.widget.Toast

class DigitsInputFilterForCart(
    private var context: Context,
    private var mMaxLength: Double,
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
            checkValueRule(enteredValue, onlyDigitsText, source, dest)
        }
    }


    private fun checkValueRule(
        enteredValue: Double,
        onlyDigitsText: String,
        source: CharSequence,
        dest: Spanned
    ): CharSequence? {
        return handleInputRules(onlyDigitsText, source, dest)
    }

    private fun handleInputRules(
        onlyDigitsText: String,
        source: CharSequence,
        dest: Spanned
    ): CharSequence? {
        return if (isDecimalDigit(onlyDigitsText)) {
            checkRuleForDecimalDigits(onlyDigitsText)
        } else {
            checkRuleForIntegerDigits(onlyDigitsText.length, source, dest)
        }
    }

    private fun isDecimalDigit(onlyDigitsText: String): Boolean {
        return onlyDigitsText.contains(dot)
    }

    private fun checkRuleForDecimalDigits(
        onlyDigitsPart: String
    ): CharSequence? {
        val afterDotPart =
            onlyDigitsPart.substring(onlyDigitsPart.indexOf(dot), onlyDigitsPart.length - 1)
        return if (afterDotPart.length > mMaxDigitsAfterLength) {
            ""
        } else null
    }

    private fun isInRange(a: Double, b: Double, c: Double): Boolean {
        return if (b > a) c in a..b
        else c in b..a
    }

    private fun checkRuleForIntegerDigits(
        allTextLength: Int,
        source: CharSequence,
        dest: Spanned
    ): CharSequence? {
        val input = (dest.toString() + source.toString()).toDouble()

        return if (isInRange(0.0, mMaxLength, input))
            null
        else {
            Toast.makeText(
                context,
                "Product is out of stock so you can't purchase more Quantity",
                Toast.LENGTH_SHORT
            ).show()

            ""
        }
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