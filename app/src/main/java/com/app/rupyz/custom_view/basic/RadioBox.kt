package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioGroup
import androidx.core.content.res.ResourcesCompat
import com.app.rupyz.R
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton

class RadioBox : RadioGroup {

    private var formFields: FormItemsItem? = null
    private var radioBoxList: MutableList<MaterialRadioButton> = mutableListOf()
    private var selectedValue: String = ""

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        try {
            removeAllViews()
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.RadioBox)
        setDefaultStyle()
        a.recycle()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {
        try {
            removeAllViews()
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        radioBoxList.clear()
        formFields?.inputProps?.options?.forEach {
            val radioBox = MaterialRadioButton(context)
            radioBox.tag = it.value
            radioBox.text = it.label
            radioBox.textAlignment = TEXT_ALIGNMENT_VIEW_START
            radioBox.gravity = Gravity.CENTER_VERTICAL
            radioBox.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            radioBox.setTextColor(resources.getColor(R.color.color_000000, null))
            addView(radioBox)
            radioBoxList.add(radioBox)
            setOnCheckedChangeListener { _, checkedId ->
                selectedValue = findViewById<MaterialRadioButton>(checkedId).tag.toString()
            }
        }
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams
    }


    fun setRadioBox(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setDefaultStyle()
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
        model.value = selectedValue
        return model
    }

    fun setValue(value: String) {
        val list = value.split(",")
        if (list.isEmpty().not()) {
            radioBoxList.forEach { radioBox ->
                if (radioBox.text.toString().trim().equals(value,true)) {
                    radioBox.isChecked = true
                    radioBox.isSelected = true
                }
            }
        }
    }
}