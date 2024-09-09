package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.res.ResourcesCompat
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import kotlinx.coroutines.currentCoroutineContext

class DropDown : LinearLayoutCompat {

    private var inputLayout: EditInputLayout? = null
    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType? = null
    private var showDropDownArrow: Boolean = true
    private val spinnerList: MutableList<String> = mutableListOf()
    private var isPopupShown = false


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
        val a = context.obtainStyledAttributes(attrs, R.styleable.DropDown)
        showDropDownArrow = a.getBoolean(R.styleable.DropDown_showDropDownArrow, true)
        inputLayout?.isEditable = false
        addView(inputLayout)
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
        a.recycle()
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {

        formFields?.let {
            inputLayout?.editText?.setEditTextType(
                FormItemType.DROPDOWN,
                it
            )
        }
        inputLayout?.isEditable = false
        if (showDropDownArrow) {
            inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
        }
        spinnerList.clear()
        spinnerList.addAll(formFields?.inputProps?.options?.map { it.value ?: "" } ?: listOf())
        setSpinnerItems()
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams

        inputLayout?.hint = getLabel((inputLayout?.editText?.hasFocus()?:false || inputLayout?.editText?.text.isNullOrBlank().not()), formFields)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSpinnerItems() {
        val listPopupWindow = ListPopupWindow(context, null, androidx.appcompat.R.attr.listPopupWindowStyle)
        listPopupWindow.anchorView = inputLayout
        val adapter =
            ArrayAdapter(context, R.layout.single_text_view_spinner_16dp_text, spinnerList)
        listPopupWindow.isModal = true
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (showDropDownArrow) {
                inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                    null
                )
            }
            if (position < spinnerList.size) {
                inputLayout?.editText?.setText(spinnerList[position])
            }
            listPopupWindow.dismiss()
            isPopupShown = false
        }
        inputLayout?.editText?.setOnClickListener {
            if (showDropDownArrow) {
                inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_inverted_black, null),
                    null
                )
            }
            if (isPopupShown) {
                listPopupWindow.dismiss()
            } else {
                listPopupWindow.show()
            }
            isPopupShown = !isPopupShown
        }
        listPopupWindow.setOnDismissListener {
            if (showDropDownArrow) {
                inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                    null
                )
            }
            isPopupShown = false
        }

    }

    fun setDropDown(formFieldsData: FormItemsItem) {
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
        return formItemType ?: FormItemType.DROPDOWN
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

    fun setValue(value: String) {
        inputLayout?.editText?.setText(value)
    }
}
