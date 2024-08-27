package com.app.rupyz.sales.customforms

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.generic.base.BrowserActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import java.text.SimpleDateFormat
import java.util.Locale

// Implement specific handlers for each form item type
class CustomTextDetailHandler(private val type: String) : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleViewFormItem(context: Context, formItem: NameAndValueSetInfoModel,
                                    binding: FormBinding, supportFragmentManager: FragmentManager) {
        super.handleViewFormItem(context, formItem, binding, supportFragmentManager)
        inputParams.setMargins(0, 10, 0, 0)


        val textView = TextView(context)
        textView.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_14sp))
        textView.setTextColor(context.resources.getColor(R.color.black))
        // Load the font from resources and set it to the TextView
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        textView.typeface = typeface
        textView.layoutParams = inputParams

        when (type) {
            FormItemType.DATE_TIME_PICKER.name -> {
                textView.text = DateFormatHelper.convertIsoToMonthAndTimeFormat(formItem.value)
            }

            FormItemType.DATE_PICKER.name -> {
                textView.text = DateFormatHelper.convertStringToCustomDateFormat(formItem.value,
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()))
            }

            FormItemType.DROPDOWN.name -> {
                textView.text = formItem.value
            }

            FormItemType.CHECKBOX.name -> {
                textView.text = formItem.value
            }

            FormItemType.MULTIPLE_CHOICE.name -> {
                textView.text = formItem.value
            }

            FormItemType.URL_INPUT.name -> {
                val spannableString = SpannableString(formItem.value)
                spannableString.setSpan(UnderlineSpan(), 0, formItem.value!!.length, 0)
                textView.text = spannableString
                textView.setTextColor(context.resources.getColor(R.color.text_color_blue))

                textView.setOnClickListener {
                    context.startActivity(Intent(context, BrowserActivity::class.java).putExtra(AppConstant.URL, formItem.value))
                }
            }

        }

        binding.formLayout.addView(textView)
    }
}