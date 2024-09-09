@file:Suppress("t")

package com.app.rupyz.custom_view.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.contains
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.material.textfield.TextInputEditText

class EditInputText : TextInputEditText {

    private var formFields: FormItemsItem? = null
    private var formItemType: FormItemType = FormItemType.ALPHABETS
    private val progressBar: ProgressBar = ProgressBar(context).apply {
        visibility = GONE
        layoutParams = ViewGroup.LayoutParams(
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt())
    }
   
    
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditText)
        val type = a.getString(R.styleable.EditText_editType)
        val editType = if (enumContains<FormItemType>(type ?: "")) {
            FormItemType.valueOf(type ?: FormItemType.ALPHABETS.name)
        } else {
            FormItemType.ALPHABETS
        }
        setTypeFace(context, editType)
        setTextColor(editType)
        setHintColor(editType)
        a.recycle()
    }

    private fun setDefaultStyle(formItemType: FormItemType) {

        setupProgressBar()
        setLayoutParams()
        setPadding(
            totalPaddingStart + TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                resources.displayMetrics
            ).toInt(),
            totalPaddingTop + TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5f,
                resources.displayMetrics
            ).toInt(),
            if (formItemType != FormItemType.DROPDOWN){
                totalPaddingEnd + TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10f,
                    resources.displayMetrics
                ).toInt()
            }else{
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20f,
                    resources.displayMetrics
                ).toInt()
            }
            ,
            totalPaddingBottom + TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                0f,
                resources.displayMetrics
            ).toInt()
        )

        minHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics)
                .toInt()
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        formFields?.let { formItem ->

            when (formItemType) {
                FormItemType.SHORT_ANSWER -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    minLines = 1
                    maxLines = 3
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    if (formItem.fieldProps?.name.equals("contact_person_name")){
                        filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 1250),AlphabetInputFilter())
                    }else{
                        filters =
                            arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 200))
                    }

                }

                FormItemType.LONG_ANSWER -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    minLines = 3
                    maxLines = 5
                    setLines(3)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 1250))

                }

                FormItemType.ALPHABETS -> {
                    minLines = 1
                    maxLines = 1
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    inputType = if (formItem.fieldProps?.name?.equals(AppConstant.SECTION_NAME_GST_IN) == true) {
                        InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    } else {
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    }

                    filters =
                        if (formItem.fieldProps?.name?.equals(AppConstant.SECTION_NAME_GST_IN) == true) {
                            arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 16),AlphabetWithNumberInputFilter(),InputFilter.AllCaps())
                        } else {
                            arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 1250),AlphabetInputFilter())
                        }

                    if (formItem.fieldProps?.name?.equals(AppConstant.SECTION_NAME_GST_IN) == true){
                        isAllCaps = true
                    }
                }

                FormItemType.MOBILE_NUMBER -> {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    minLines = 1
                    maxLines = 1
                    gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    filters = arrayOf(InputFilter.LengthFilter(10))
                }

                FormItemType.EMAIL_ADDRESS -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    minLines = 1
                    maxLines = 1
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    filters = arrayOf(InputFilter.LengthFilter(50),EmailInputFilter())
                }

                FormItemType.NUMBERS -> {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    minLines = 1
                    maxLines = 1
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    inputType = InputType.TYPE_CLASS_NUMBER
                    filters =
                        if (formItem.fieldProps?.name?.contains(AppConstant.SECTION_NAME_MOBILE) == true) {
                            arrayOf(InputFilter.LengthFilter(AppConstant.SECTION_NAME_MOBILE_LENGTH))
                        } else {
                            arrayOf(
                                InputFilter.LengthFilter(
                                    formItem.inputProps?.maxLength ?: 1250
                                )
                            )
                        }
                }
                FormItemType.DECIMAL -> {
                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                    minLines = 1
                    maxLines = 1
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                    filters =
                        if (formItem.fieldProps?.name?.contains(AppConstant.SECTION_NAME_MOBILE) == true) {
                            arrayOf(InputFilter.LengthFilter(AppConstant.SECTION_NAME_MOBILE_LENGTH))
                        } else {
                            arrayOf(
                                InputFilter.LengthFilter(
                                    formItem.inputProps?.maxLength ?: 1250
                                )
                            )
                        }
                }

                FormItemType.URL_INPUT -> {
                    maxLines = 1
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                    filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength ?: 1250),UrlInputFilter())
                }

                FormItemType.DATE_TIME_PICKER,
                FormItemType.DATE_PICKER,
                FormItemType.DROPDOWN,
                FormItemType.FILE_UPLOAD,
                FormItemType.MULTIPLE_CHOICE,
                FormItemType.CHECKBOX,
                FormItemType.RATING -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    minLines = 1
                    maxLines = 1
                    setLines(1)
                    isVerticalScrollBarEnabled = true
                    isVerticalFadingEdgeEnabled = true
                    movementMethod = ScrollingMovementMethod()
                    gravity = Gravity.CENTER_VERTICAL and Gravity.START
                    textAlignment = TEXT_ALIGNMENT_VIEW_START
                }
            }

            background = null
            tag = formItem.fieldProps?.name

        }
       /* if (formFields?.fieldProps?.required == true) {
            setOnFocusChangeListener { v, hasFocus ->
                if (v.isEnabled && hasFocus.not()) {
                    if (text.isNullOrBlank()) {
                        error = resources.getString(
                            R.string.isRequiredMessage,
                            formFields?.fieldProps?.label
                        )
                    } else {
                        error = null
                    }
                }
            }

            addTextChangedListener {
                if (isEnabled) {
                    if (text.isNullOrBlank()) {
                        error = resources.getString(
                            R.string.isRequiredMessage,
                            formFields?.fieldProps?.label
                        )
                    } else {
                        error = null
                    }
                }
            }

        }*/
    }

    private fun setTypeFace(context: Context, type: FormItemType) {
        try {
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            when (type) {
                FormItemType.SHORT_ANSWER,
                FormItemType.LONG_ANSWER,
                FormItemType.DATE_TIME_PICKER,
                FormItemType.DATE_PICKER,
                FormItemType.DROPDOWN,
                FormItemType.FILE_UPLOAD,
                FormItemType.MOBILE_NUMBER,
                FormItemType.EMAIL_ADDRESS,
                FormItemType.ALPHABETS,
                FormItemType.NUMBERS,
                FormItemType.MULTIPLE_CHOICE,
                FormItemType.CHECKBOX,
                FormItemType.URL_INPUT,
                FormItemType.RATING,
                FormItemType.DECIMAL
                -> {
                    setTypeface(typeface)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor(type: FormItemType) {
        try {
            when (type) {
                FormItemType.SHORT_ANSWER,
                FormItemType.LONG_ANSWER,
                FormItemType.DATE_TIME_PICKER,
                FormItemType.DATE_PICKER,
                FormItemType.DROPDOWN,
                FormItemType.FILE_UPLOAD,
                FormItemType.MOBILE_NUMBER,
                FormItemType.EMAIL_ADDRESS,
                FormItemType.ALPHABETS,
                FormItemType.NUMBERS,
                FormItemType.MULTIPLE_CHOICE,
                FormItemType.CHECKBOX,
                FormItemType.URL_INPUT,
                FormItemType.RATING,
                FormItemType.DECIMAL
                -> {
                    setTextColor(resources.getColor(R.color.color_000000, null))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHintColor(type: FormItemType) {
        try {
            when (type) {
                FormItemType.SHORT_ANSWER,
                FormItemType.LONG_ANSWER,
                FormItemType.DATE_TIME_PICKER,
                FormItemType.DATE_PICKER,
                FormItemType.DROPDOWN,
                FormItemType.FILE_UPLOAD,
                FormItemType.MOBILE_NUMBER,
                FormItemType.EMAIL_ADDRESS,
                FormItemType.ALPHABETS,
                FormItemType.NUMBERS,
                FormItemType.MULTIPLE_CHOICE,
                FormItemType.CHECKBOX,
                FormItemType.URL_INPUT,
                FormItemType.RATING,
                FormItemType.DECIMAL
                -> {
                    setHintTextColor(resources.getColor(R.color.color_727176, null))
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

    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = text.toString()
        return model
    }

    fun isRequired(): Boolean {
        return formFields?.fieldProps?.required ?: false
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

    fun setValue(text: String) {
        this.setText(text)
    }
    

    
    private fun setupProgressBar() {
        if (formItemType != FormItemType.DROPDOWN){
            progressBar.secondaryProgressTintList = ColorStateList.valueOf(resources.getColor(R.color.theme_green,null))
            progressBar.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.theme_purple,null))
            post {
                (parent as? ViewGroup)?.let { parentView ->
                    if (parentView.contains(progressBar).not()){
                        parentView.addView(progressBar)
                        adjustPadding()
                        positionProgressBar()
                    }
                }
            }
        }
    }


    
    private fun adjustPadding() {
        val padding = paddingRight + progressBar.layoutParams.width
        setPadding(paddingLeft, paddingTop, paddingRight + padding, paddingBottom)
    }
    
    
    fun showProgressBar() {
        progressBar.showView()
    }
    
    fun hideProgressBar() {
        progressBar.hideView()
    }

    fun isProgressBarShowing() : Boolean {
       return progressBar.visibility == VISIBLE
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        positionProgressBar()
    }
    
    private fun positionProgressBar() {
        progressBar.post {
            val rightPadding = paddingRight
            val left = width - rightPadding - progressBar.width
            val top = (height - progressBar.height) / 2
            progressBar.layout(left, top, left + progressBar.width, top + progressBar.height)
        }
    }

    inner class EmailInputFilter : InputFilter {
        private val emailCharacters = "[a-zA-Z0-9@._-]+"
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            if (source == null) return null
            for (i in start until end) {
                if (!source[i].toString().matches(emailCharacters.toRegex())) {
                    return ""
                }
            }
            return null
        }
    }

    inner class UrlInputFilter : InputFilter {
        private val urlCharacters = "[a-zA-Z0-9/:?&.=_%+-]+"
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            if (source == null) return null
            for (i in start until end) {
                if (!source[i].toString().matches(urlCharacters.toRegex())) {
                    return ""
                }
            }
            return null
        }
    }

    class AlphabetInputFilter : InputFilter {
        private val alphabetCharacters = "[a-zA-Z ]+"
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            if (source == null) return null
            for (i in start until end) {
                if (!source[i].toString().matches(alphabetCharacters.toRegex())) {
                    return ""
                }
            }
            return null
        }
    }

    class AlphabetWithNumberInputFilter : InputFilter {
        private val alphabetCharacters = "[a-zA-Z0-9 ]+"
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            if (source == null) return null
            for (i in start until end) {
                if (!source[i].toString().matches(alphabetCharacters.toRegex())) {
                    return ""
                }
            }
            return null
        }
    }

    private fun setLayoutParams() {
        if (layoutParams is LinearLayoutCompat.LayoutParams){
            val inputParams =  LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is LayoutParams){
            val inputParams =  LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is FrameLayout.LayoutParams){
            val inputParams =  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is ConstraintLayout.LayoutParams){
            val inputParams =  ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
        else if (layoutParams is RelativeLayout.LayoutParams){
            val inputParams =  RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            inputParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            inputParams.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
            layoutParams = inputParams
        }
    }

}
