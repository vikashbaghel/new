package com.app.rupyz.sales.customforms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTimePickerHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleCreationFormItem(
        context: Context,
        formItem: FormItemsItem,
        binding: FormBinding,
        formItemModels: MutableList<NameAndValueSetInfoModel>,
        supportFragmentManager: FragmentManager,
        picsUrls: ArrayList<PicMapModel>
    ) {
        inputParams.setMargins(50, 30, 50, 0)

        val inputTextView = TextView(context)
        inputTextView.setPadding(32, 25, 32, 25)
        inputTextView.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_16sp))
        inputTextView.setTextColor(context.resources.getColor(R.color.black))
        // Load the font from resources and set it to the TextView
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        inputTextView.typeface = typeface

        inputTextView.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)

        inputTextView.layoutParams = inputParams
        inputTextView.text = formItem.inputProps?.placeholder
        inputTextView.tag = formItem.fieldProps?.name
        binding.formLayout.addView(inputTextView)
        inputTextView.setOnClickListener {
            showDatePicker(inputTextView, context, formItem, formItemModels)
        }

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.value.isNullOrBlank().not()){
                inputTextView.text = it.value
                updateModelValue(formItem.fieldProps?.name, it.value, formItemModels)
            }
        }
    }

    private fun showDatePicker(textView: TextView, context: Context, formItem: FormItemsItem, formItemModels: MutableList<NameAndValueSetInfoModel>) {
       
        val currentDate = Calendar.getInstance()

        val date = Calendar.getInstance()
        val datePicker = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            date.set(year, monthOfYear, dayOfMonth)
            val timePicker = TimePickerDialog(context, { _, hourOfDay, minute ->
                date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                date.set(Calendar.MINUTE, minute)
                textView.text = DateFormatHelper.convertStringToDateAndTimeFormat(date.time)

                updateModelValue(formItem.fieldProps?.name, DateFormatHelper.convertDateToIsoUTCFormat(date.time),
                        formItemModels)
            }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false)

            if (DateFormatHelper.isDate1EqualThenDate2(DateFormatHelper.convertDateToIsoFormat(date.time), DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time))) {
                timePicker.updateTime(DateFormatHelper.convertDateToCustomDateFormat(Calendar.getInstance().time, SimpleDateFormat("HH", Locale.ENGLISH)).toInt(), DateFormatHelper.convertDateToCustomDateFormat(Calendar.getInstance().time, SimpleDateFormat("mm", Locale.ENGLISH)).toInt())
            } else {
                timePicker.updateTime(9, 0)
            }
            timePicker.show()
        }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE])

        datePicker.datePicker.minDate = Calendar.getInstance().time.time

        datePicker.show()
    }

    private fun updateModelValue(fieldName: String?, value: String?, formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Find the model associated with the field name and update its value
        val model = formItemModels.find { it.name == fieldName }
        model?.value = value
    }
}