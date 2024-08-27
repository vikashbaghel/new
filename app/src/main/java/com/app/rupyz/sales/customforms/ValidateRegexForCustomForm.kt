package com.app.rupyz.sales.customforms

import android.content.Context
import android.text.TextUtils
import com.app.rupyz.R
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import java.util.regex.Pattern

fun NameAndValueSetInfoModel.isValidateInput(context: Context): Pair<Boolean, String> {
    return when (this.type) {

        FormItemType.URL_INPUT.name -> {
            val isValid = isValidUrl(this.value)
            return Pair(isValid, context.getString(R.string.enter_valid_string, this.label))
        }

        FormItemType.MOBILE_NUMBER.name -> {
            val phoneNamePattern = Pattern.compile("[0-4]")

            val isValid = TextUtils.isEmpty(this.value).not() && this.value.toString().length == 10
                    && !phoneNamePattern.matcher(this.value.toString()[0].toString()).matches()
            return Pair(isValid, context.getString(R.string.enter_valid_string, this.label))
        }

        FormItemType.EMAIL_ADDRESS.name -> {
            val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            val isValid = this.value.isNullOrEmpty().not() && emailPattern.matches(this.value!!)
            return Pair(isValid, context.getString(R.string.enter_valid_string, this.label))
        }

        else -> Pair(true, "")
    }
}

fun isValidUrl(url: String?): Boolean {
    if (url == null) return false
    val urlPattern = """^((http|https):\/\/)+((([a-zA-Z0-9\-_]+)\.)+([a-zA-Z]{2,})|localhost)(:[0-9]{1,5})?(\/[^\s]*)?$""".toRegex()
    return urlPattern.matches(url)
}