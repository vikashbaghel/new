package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.material.textfield.TextInputLayout

class EditInputLayout : TextInputLayout {

    private var editText: EditInputText? = null
    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType = FormItemType.ALPHABETS
    private var defaultStrokeColor: Int = resources.getColor(R.color.color_322E80, null)
    private var defaultInactiveStrokeColor: Int = resources.getColor(R.color.color_DDDDDD, null)
    private var defaultStrokeWidth: Int = 2
    private var defaultHintColor: Int = resources.getColor(R.color.color_727176, null)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(
        context, attrs, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_Dense
    ) {
        init(attrs)
    }

    constructor(context: Context) : super(
        context, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_Dense
    ) {
        init(null)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        gravity = Gravity.CENTER_VERTICAL
        foregroundGravity = Gravity.CENTER_VERTICAL
        try {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is EditInputText) {
                    removeView(view)
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, "init: $e")
        }

        boxStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, defaultStrokeWidth.toFloat(), resources.displayMetrics
        ).toInt()
        boxBackgroundMode = BOX_BACKGROUND_OUTLINE

        editText = if (attrs != null) {
            EditInputText(context, attrs)
        } else {
            EditInputText(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditText)
        val type = a.getString(R.styleable.EditText_editType)
        val editType = if (enumContains<FormItemType>(type ?: "")) {
            FormItemType.valueOf(type ?: FormItemType.ALPHABETS.name)
        } else {
            FormItemType.ALPHABETS
        }
        defaultStrokeColor = a.getColor(
            R.styleable.EditText_strokeColor, resources.getColor(R.color.color_322E80, null)
        )

        setTypeFace(context, editType)
        setTextColor(editType)
        setHintColor(editType)
        setDefaultStyle(editType)
        addView(editText)

        isHintAnimationEnabled = true
        isExpandedHintEnabled = true
        isHintEnabled = true
        isTransitionGroup = true

        a.recycle()
    }

    private fun setDefaultStyle(formItemType: FormItemType) {
        formFields?.let { editText?.setEditTextType(formItemType, it) }

        setLayoutParams()

        boxStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, defaultStrokeWidth.toFloat(), resources.displayMetrics
        ).toInt()
        boxStrokeColor = defaultInactiveStrokeColor

        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_window_focused)
        )
        val colors = intArrayOf(
            defaultInactiveStrokeColor, // focused
            defaultInactiveStrokeColor, // unfocused
            defaultInactiveStrokeColor, // enabled
            defaultInactiveStrokeColor,  // disabled
            defaultInactiveStrokeColor,  // window focused
        )
        setBoxStrokeColorStateList(ColorStateList(states, colors))
        defaultHintTextColor = (ColorStateList.valueOf(defaultHintColor))
        hintTextColor = (ColorStateList.valueOf(defaultHintColor))

        val focusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                boxStrokeColor = defaultStrokeColor
                setBoxStrokeColorStateList(ColorStateList.valueOf(defaultStrokeColor))
                defaultHintTextColor = (ColorStateList.valueOf(defaultStrokeColor))
                hintTextColor = (ColorStateList.valueOf(defaultStrokeColor))
            } else {
                if (editText?.text.isNullOrBlank().not()) {
                    val focusedStates = arrayOf(
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf(-android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_active),
                        intArrayOf(-android.R.attr.state_active),
                    )
                    val focusedColors = intArrayOf(
                        defaultStrokeColor, // focused
                        defaultStrokeColor, // unfocused
                        defaultStrokeColor,  // active
                        defaultStrokeColor  // inactive
                    )

                    setBoxStrokeColorStateList(ColorStateList(focusedStates, focusedColors))
                    defaultHintTextColor = ColorStateList.valueOf(defaultStrokeColor)
                    hintTextColor = ColorStateList.valueOf(defaultStrokeColor)
                    boxStrokeColor = defaultStrokeColor
                } else {
                    setBoxStrokeColorStateList(ColorStateList(states, colors))
                    defaultHintTextColor = ColorStateList.valueOf(defaultHintColor)
                    hintTextColor = ColorStateList.valueOf(defaultHintColor)
                    boxStrokeColor = defaultInactiveStrokeColor
                }
            }
            hint = getLabel((hasFocus || editText?.text.isNullOrBlank().not()), formFields)
        }

        onFocusChangeListener = focusChangeListener
        editText?.onFocusChangeListener = focusChangeListener

        boxBackgroundMode = BOX_BACKGROUND_OUTLINE/* dividerPadding = 10*/

        when (formItemType) {
            FormItemType.SHORT_ANSWER -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.LONG_ANSWER -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.ALPHABETS -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.MOBILE_NUMBER -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.EMAIL_ADDRESS -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.NUMBERS -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.URL_INPUT -> {
                endIconMode = END_ICON_NONE
            }

            FormItemType.DATE_TIME_PICKER, FormItemType.DATE_PICKER, FormItemType.DROPDOWN, FormItemType.FILE_UPLOAD, FormItemType.MULTIPLE_CHOICE, FormItemType.CHECKBOX, FormItemType.DECIMAL, FormItemType.RATING -> {
                endIconMode = END_ICON_NONE
            }
        }


        setBoxCornerRadii(10f, 10f, 10f, 10f)
        hint = getLabel(
            (editText?.hasFocus() ?: false || editText?.text.isNullOrBlank().not()), formFields
        )


    }

    private fun setLayoutParams() {
        when (layoutParams) {
            is LinearLayoutCompat.LayoutParams -> {
                val inputParams = LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = inputParams
            }

            is LayoutParams -> {
                val inputParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = inputParams
            }

            is FrameLayout.LayoutParams -> {
                val inputParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = inputParams
            }

            is ConstraintLayout.LayoutParams -> {
                val inputParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = inputParams
            }

            is RelativeLayout.LayoutParams -> {
                val inputParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                inputParams.topMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                inputParams.marginStart = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.marginEnd = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics
                ).toInt()
                inputParams.bottomMargin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = inputParams
            }

            else -> {
                val inputParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams = inputParams
            }
        }
    }


    private fun setTypeFace(context: Context, type: FormItemType) {
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            when (type) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.DATE_TIME_PICKER, FormItemType.DATE_PICKER, FormItemType.DROPDOWN, FormItemType.FILE_UPLOAD, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.MULTIPLE_CHOICE, FormItemType.CHECKBOX, FormItemType.URL_INPUT, FormItemType.RATING, FormItemType.DECIMAL -> {
                    editText?.setTypeface(typeface)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor(type: FormItemType) {
        try {
            when (type) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.DATE_TIME_PICKER, FormItemType.DATE_PICKER, FormItemType.DROPDOWN, FormItemType.FILE_UPLOAD, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.MULTIPLE_CHOICE, FormItemType.CHECKBOX, FormItemType.URL_INPUT, FormItemType.RATING, FormItemType.DECIMAL -> {
                    editText?.setTextColor(resources.getColor(R.color.color_000000, null))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHintColor(type: FormItemType) {
        try {
            when (type) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.DATE_TIME_PICKER, FormItemType.DATE_PICKER, FormItemType.DROPDOWN, FormItemType.FILE_UPLOAD, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.MULTIPLE_CHOICE, FormItemType.CHECKBOX, FormItemType.URL_INPUT, FormItemType.RATING, FormItemType.DECIMAL -> {
                    editText?.setHintTextColor(resources.getColor(R.color.color_727176, null))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEditTextType(itemType: FormItemType, formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        formItemType = itemType
        setTypeFace(context, itemType)
        setTextColor(itemType)
        setHintColor(itemType)
        setDefaultStyle(itemType)

    }

    override fun getEditText(): EditInputText? {
        return editText
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
        model.value = editText?.text.toString()
        return model
    }

    fun setValue(name: String?) {
        editText?.setText(name)
    }

    var isEditable = true
        set(value) {
            field = value
            if (value) {
                editText?.isFocusable = true
                editText?.isEnabled = true
                editText?.isFocusableInTouchMode = true
                editText?.isCursorVisible = true

                isFocusable = true
                isEnabled = true
                isFocusableInTouchMode = true
            } else {
                editText?.isFocusable = false
                editText?.isEnabled = true
                editText?.isFocusableInTouchMode = false
                editText?.isCursorVisible = false

                isFocusable = false
                isEnabled = true
                isFocusableInTouchMode = false

            }
        }


    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }


}

