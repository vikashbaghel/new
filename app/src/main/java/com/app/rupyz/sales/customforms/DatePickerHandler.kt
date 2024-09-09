package com.app.rupyz.sales.customforms

import android.app.DatePickerDialog
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
import java.util.Calendar

class DatePickerHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val myCalendar = Calendar.getInstance()

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

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

    private fun showDatePicker(textView: TextView, context: Context, formItem: FormItemsItem,
                               formItemModels: MutableList<NameAndValueSetInfoModel>) {
        val dialog = DatePickerDialog(
                context,
                android.R.style.ThemeOverlay_Material_Dialog,
                mStartDateSetListener,
                year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.datePicker.minDate =
                Calendar.getInstance().time.time
        dialog.show()

        mStartDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = day
                    updateStartDate(textView, formItem, formItemModels)
                }
    }

    private fun updateStartDate(textView: TextView, formItem: FormItemsItem,
                                formItemModels: MutableList<NameAndValueSetInfoModel>) {
        textView.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)

        updateModelValue(formItem.fieldProps?.name,
                DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time),
                formItemModels)
    }

    private fun updateModelValue(fieldName: String?, value: String?,
                                 formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Find the model associated with the field name and update its value
        val model = formItemModels.find { it.name == fieldName }
        model?.value = value
    }
}