package com.app.rupyz.generic.utils

import com.app.rupyz.model_kt.CartItem
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToInt

class CalculatorHelper {

    private val dfFourDecimal = DecimalFormat("0.0000")
    private val dfTwoDecimal = DecimalFormat("0.00")

    fun convertLargeAmount(amount: Double, decimalPoints: Int): String {
        if (decimalPoints == 4) {
            dfFourDecimal.roundingMode = RoundingMode.HALF_UP
        } else {
            dfTwoDecimal.roundingMode = RoundingMode.HALF_UP
        }
        return if (amount.roundToInt().toString().length > 7) {
            var convertAmount = ""
            val cr = amount / 10000000.0
            convertAmount = dfTwoDecimal.format(cr) + " cr"
            "\u20B9" + convertAmount
        } else {
            convertCommaSeparatedAmount(amount, decimalPoints)!!
        }
    }

    fun convertCommaSeparatedAmount(amount: Double?, decimalPoints: Int): String? {
        val format: DecimalFormat = DecimalFormat.getCurrencyInstance(Locale("en", "in")) as DecimalFormat
        format.maximumFractionDigits = decimalPoints
        if (amount != null) {
            var value = format.format(amount)
            return if (value.contains(".0000")) {
                value = value.replace(".0000", "")
                value
            } else if (value.contains(".00")) {
                value = value.replace(".00", "")
                value
            } else if (value.contains(".00")) {
                value = value.replace(".0", "")
                value
            } else {
                value
            }
        } else {
            return ""
        }
    }

    fun convertCommaSeparatedAmountWithoutSymbol(amount: String?): String? {
        dfTwoDecimal.roundingMode = RoundingMode.HALF_UP
        val price = java.lang.Double.valueOf(amount!!)
        var value = dfTwoDecimal.format(price)
        return if (value.contains(".00")) {
            value = value.replace(".00", "")
            value
        } else {
            value
        }
    }

    private fun formatTwoDigitDecimalPoint(amount: Double?): String? {
        val dfTwoDecimal = DecimalFormat("0.00")
        dfTwoDecimal.roundingMode = RoundingMode.HALF_UP
        var value = dfTwoDecimal.format(amount)

        return if (value.contains(".00")) {
            value = value.replace(".00", "")
            value
        } else {
            value
        }
    }

    fun formatDoubleDecimalPoint(amount: Double?, decimalPoints: Int): String? {
        var value = ""
        if (decimalPoints == 4) {
            dfFourDecimal.roundingMode = RoundingMode.HALF_UP
            value = dfFourDecimal.format(amount)
        } else {
            dfTwoDecimal.roundingMode = RoundingMode.HALF_UP
            value = dfTwoDecimal.format(amount)
        }

        return if (value.contains(".0000")) {
            value = value.replace(".0000", "")
            value
        } else if (value.contains(".00")) {
            value = value.replace(".00", "")
            value
        } else if (value.contains(".00")) {
            value = value.replace(".0", "")
            value
        } else {
            value
        }
    }


    fun calculateFinalProductPriceAfterDiscount(
            model: CartItem,
            price: Double,
            decimalPoints: Int
    ): String {
        var priceWithGst = 0.0
        var finalPerProductPrice = 0.0

        val qty = if (model.selectedPackagingLevel != null) {
            model.qty!! * model.selectedPackagingLevel?.size!!
        } else if (model.packagingLevel.isNullOrEmpty().not()) {
            model.qty!! * model.packagingLevel!![0].size!!
        } else {
            model.qty!!
        }

        priceWithGst = if (model.gst_exclusive == true) {
            calculateGstPercentAmount(
                    qty,
                    price,
                    model.gst!!,
                    model.gst_exclusive!!,
                    decimalPoints
            ).third
        } else {
            price * qty
        }

        finalPerProductPrice = roundDecimalPoint(priceWithGst, decimalPoints)
        return convertLargeAmount(finalPerProductPrice, decimalPoints)
    }

    fun calculateGstPercentAmount(
            qty: Double,
            price: Double,
            gstPercentage: Double,
            isGSTExcluded: Boolean,
            decimalPoints: Int
    ): Triple<String, Double, Double> {
        return if (isGSTExcluded.not()) {
            var gstAmount = (qty * price * gstPercentage) / (100 + gstPercentage)
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            var priceWithoutGst = (qty * price) - gstAmount
            priceWithoutGst = roundDecimalPoint(priceWithoutGst, decimalPoints)
            Triple(convertCommaSeparatedAmount(gstAmount, decimalPoints)!!, gstAmount, priceWithoutGst)
        } else {
            var gstAmount = (qty * price * gstPercentage) / 100
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            var priceWithGst = (qty * price) + gstAmount
            priceWithGst = roundDecimalPoint(priceWithGst, decimalPoints)
            Triple(convertCommaSeparatedAmount(gstAmount, decimalPoints)!!, gstAmount, priceWithGst)
        }
    }

    fun calculateGstAmountForSingleUnit(
            price: Double,
            gstPercentage: Double,
            isGSTExcluded: Boolean,
            decimalPoints: Int
    ): String? {
        return if (isGSTExcluded.not()) {
            var gstAmount = (price * gstPercentage) / (100 + gstPercentage)
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            convertCommaSeparatedAmount(gstAmount, decimalPoints)
        } else {
            var gstAmount = (price * gstPercentage) / 100
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            convertCommaSeparatedAmount(gstAmount, decimalPoints)
        }
    }

    fun calculatePriceWithoutGst(
            qty: Double,
            price: Double,
            gstPercentage: Double,
            isGSTExcluded: Boolean,
            decimalPoints: Int
    ): Double {
        return if (isGSTExcluded.not()) {
            var gstAmount = (qty * price * gstPercentage) / (100 + gstPercentage)
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            var priceWithoutGst = (qty * price) - gstAmount
            priceWithoutGst = roundDecimalPoint(priceWithoutGst, decimalPoints)
            priceWithoutGst
        } else {
            var gstAmount = (qty * price * gstPercentage) / 100
            gstAmount = roundDecimalPoint(gstAmount, decimalPoints)
            var priceWithoutGst = (qty * price)
            priceWithoutGst = roundDecimalPoint(priceWithoutGst, decimalPoints)
            priceWithoutGst
        }
    }


    fun calculateGst(gstPercentage: Double, isGSTExcluded: Boolean): String {
        var isGstWhole = false
        if (gstPercentage % 1 == 0.0) {
            isGstWhole = true
        }
        return if (isGSTExcluded) {
            if (isGstWhole) {
                "GST (${gstPercentage.toInt()}%) extra :"
            } else {
                "GST ($gstPercentage%) extra :"
            }
        } else {
            if (isGstWhole) {
                "GST (${gstPercentage.toInt()}%) incl :"
            } else {
                "GST ($gstPercentage%) incl :"
            }
        }
    }

    fun calculateQuantity(qty: Double?): String {
        return if (qty != null) {
            if ((qty % 1) == 0.0) {
                qty.toInt().toString()
            } else {
                formatTwoDigitDecimalPoint(qty)!!
            }
        } else {
            ""
        }
    }

    fun roundDecimalPoint(amount: Double?, decimalPoints: Int): Double {
        dfTwoDecimal.roundingMode = RoundingMode.HALF_UP
        return if (decimalPoints == 4) {
            dfFourDecimal.format(amount).toDouble()
        } else {
            dfTwoDecimal.format(amount).toDouble()
        }
    }

    fun calculateTotalOrderQuantity(
            items: ArrayList<CartItem>,
            shipment: Boolean
    ): ArrayList<Map.Entry<String, Double>> {
        val hashMap: HashMap<String, Double> = HashMap()
        items.forEach {
            if (hashMap.containsKey(it.packagingUnit?.lowercase())) {
                val quantity: Double? = if (shipment) {
                    hashMap[it.packagingUnit?.lowercase()]?.plus(it.dispatchQty!!)
                } else {
                    hashMap[it.packagingUnit?.lowercase()]?.plus(it.qty!!)
                }
                hashMap[it.packagingUnit?.lowercase() ?: ""] = quantity ?: 0.0
            } else {
                val quantity: Double? = if (shipment) {
                    it.dispatchQty
                } else {
                    it.qty
                }

                hashMap[it.packagingUnit?.lowercase() ?: ""] = quantity ?: 0.0
            }
        }

        val entrySet: Set<Map.Entry<String, Double>> = hashMap.entries

        return ArrayList(entrySet)
    }
}