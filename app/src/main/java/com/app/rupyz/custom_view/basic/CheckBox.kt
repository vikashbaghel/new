package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.material.checkbox.MaterialCheckBox

class CheckBox : LinearLayoutCompat {

    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType? = null
    private var checkBoxList: MutableList<MaterialCheckBox> = mutableListOf()
    private val checkList: MutableList<String> = mutableListOf()

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
        orientation = VERTICAL
        try {
            removeAllViews()
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckBox)
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
        checkBoxList.clear()
        checkList.clear()
        formFields?.inputProps?.options?.forEach {
            val checkBox = MaterialCheckBox(context)
            checkBox.tag = it.value
            checkBox.text = it.label
            checkBox.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            checkBox.setTextColor(resources.getColor(R.color.color_000000, null))
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkList.add(it.value ?: "")
                } else {
                    checkList.remove(it.value ?: "")
                }
            }
            checkBox.isChecked = checkList.contains(it.value)
            addView(checkBox)
            checkBoxList.add(checkBox)
        }
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams
    }


    fun setCheckBox(formFieldsData: FormItemsItem) {
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
        model.value = checkList.joinToString(",")
        return model
    }

    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.CHECKBOX
    }


    fun getCheckList(): MutableList<String> {
        return checkList
    }

    fun setValue(it: String) {
        val list = it.split(",")
        if (list.filterNotNull().isEmpty().not()) {
            checkList.addAll(list)
            checkBoxList.forEach {
                if (checkList.contains(it.text.toString().trim())) {
                    it.checkedState = MaterialCheckBox.STATE_CHECKED
                    it.isChecked = true
                    it.isSelected = true
                }
            }
        }
    }
}