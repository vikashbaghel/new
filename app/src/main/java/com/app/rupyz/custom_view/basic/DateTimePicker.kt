package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTimePicker : LinearLayoutCompat {

    private var inputLayout: EditInputLayout? = null
    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType? = null
    private var isDatePickerEnabled: Boolean = true
    private var isTimePickerEnabled: Boolean = true

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {

        try {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is EditInputLayout) {
                    removeView(view)
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        inputLayout = if (attrs != null) {
            EditInputLayout(context, attrs)
        } else {
            EditInputLayout(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.DateTimePicker)
        isDatePickerEnabled = a.getBoolean(R.styleable.DateTimePicker_enableDatePicker, true)
        isTimePickerEnabled = a.getBoolean(R.styleable.DateTimePicker_enableTimePicker, true)
        inputLayout?.isEditable = false
        addView(inputLayout)
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
        a.recycle()
    }

    fun setDatePickerEnabled(status: Boolean) {
        isDatePickerEnabled = status
        setDefaultStyle()
    }

    fun setTimePickerEnabled(status: Boolean) {
        isTimePickerEnabled = status
        setDefaultStyle()
    }

    private fun setTypeFace(context: Context) {
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            inputLayout?.typeface = typeface
            inputLayout?.editText?.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor() {
        try {
            inputLayout?.editText?.setTextColor(resources.getColor(R.color.color_000000, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHintColor() {
        try {
            inputLayout?.editText?.setHintTextColor(resources.getColor(R.color.color_727176, null))
            inputLayout?.hintTextColor =
                ColorStateList.valueOf(resources.getColor(R.color.color_727176, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultStyle() {

        formFields?.let {
            inputLayout?.editText?.setEditTextType(
                FormItemType.DATE_TIME_PICKER,
                it
            )
        }
        inputLayout?.isEditable = false
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams

        inputLayout?.editText?.setOnClickListener {
            Utils.hideKeyboard((context as FragmentActivity))
            when {
                isDatePickerEnabled && isTimePickerEnabled -> {
                    openDatePicker(isTimePickerEnabled)
                }

                isDatePickerEnabled && isTimePickerEnabled.not() -> {
                    openDatePicker(isTimePickerEnabled)
                }

                isDatePickerEnabled.not() && isTimePickerEnabled -> {
                    openTimePicker(isDatePickerEnabled, Calendar.getInstance())
                }
            }
        }

        inputLayout?.hint  = getLabel((inputLayout?.editText?.hasFocus()?:false || inputLayout?.editText?.text.isNullOrBlank().not()), formFields)

    }


    private fun openDatePicker(timePickerEnabled: Boolean) {

        val constraint =
//            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
            CalendarConstraints.Builder().build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraint)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText(formFields?.fieldProps?.label)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            datePicker.dismiss()
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = it
            if (timePickerEnabled) {
                openTimePicker(true, calendar)
            } else {
                inputLayout?.editText?.setText(
                    DateFormatHelper.convertDateToMonthStringFormat(
                        calendar.time
                    )
                )
            }
        }

        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }

        datePicker.show(
            (context as FragmentActivity).supportFragmentManager,
            MaterialDatePicker::class.java.name
        )

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openTimePicker(datePickerEnabled: Boolean, date: Calendar) {

        val timePickerBuilder =
            TimePickerDialog(context, R.style.Widget_App_TimePicker_Clock, { _, hourOfDay, minute ->

                if (datePickerEnabled) {
                    date[Calendar.HOUR_OF_DAY] = hourOfDay
                    date[Calendar.MINUTE] = minute
                    inputLayout?.editText?.setText(
                        DateFormatHelper.convertStringToDateAndTimeFormat(
                            date.time
                        )
                    )
                } else {
                    val calendar = Calendar.getInstance(Locale.getDefault())
                    calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                    calendar[Calendar.MINUTE] = minute
                    inputLayout?.editText?.setText(
                        DateFormatHelper.convertStringToDateAndTimeFormat(
                            calendar.time
                        )
                    )
                }

            }, date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE], false)


        if (DateFormatHelper.isDate1EqualThenDate2(
                DateFormatHelper.convertDateToIsoFormat(date.time),
                DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time)
            )
        ) {
            timePickerBuilder.updateTime(
                DateFormatHelper.convertDateToCustomDateFormat(
                    Calendar.getInstance().time,
                    SimpleDateFormat("HH", Locale.ENGLISH)
                ).toInt(),
                DateFormatHelper.convertDateToCustomDateFormat(
                    Calendar.getInstance().time,
                    SimpleDateFormat("mm", Locale.ENGLISH)
                ).toInt()
            )
        } else {
            timePickerBuilder.updateTime(9, 0)
        }

        timePickerBuilder.window?.setBackgroundDrawable(
            resources.getDrawable(
                R.drawable.timepicker_background,
                null
            )
        )

        timePickerBuilder.show()

    }


    fun setDateTimePicker(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setDefaultStyle()
        setTypeFace(context)
        setTextColor()
        setHintColor()
    }

    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = inputLayout?.editText?.text.toString()
        return model
    }

    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.DATE_TIME_PICKER
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

    fun setValue(dateString: String) {
        inputLayout?.editText?.setText(dateString)
    }

}