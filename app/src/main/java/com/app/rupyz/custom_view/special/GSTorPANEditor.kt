package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.basic.TextView
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.custom_view.type.TextViewType
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerPanOrGstInfoModel
import com.app.rupyz.sales.customer.CustomerViewModel

class GSTorPANEditor : LinearLayoutCompat {

    private var inputLayout: EditInputLayout? = null
    private var formFields: FormItemsItem? = null
    private val customerViewModel: CustomerViewModel = ViewModelProvider(context as FragmentActivity)[CustomerViewModel::class.java]
    private lateinit var gstOrPanVerificationListener: (Boolean, CustomerPanOrGstInfoModel) -> Unit
    private var isGSTVerified = false
    private var oldGSTNo = ""
    private var formItemType: FormItemType? = null
    private var serverErrorMessage : String? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditText)

        val hintTextView = TextView(context)
        hintTextView.setTextViewType(TextViewType.HINT)
        hintTextView.text = resources.getString(R.string.gstNoHint)

        addView(inputLayout)
        addView(hintTextView)
        inputLayout?.editText?.hideProgressBar()
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
        observeCustomerList()
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

        formFields?.let { inputLayout?.setEditTextType(FormItemType.ALPHABETS, it) }

        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams

        inputLayout?.hint = getLabel((inputLayout?.editText?.hasFocus()?:false || inputLayout?.editText?.text.isNullOrBlank().not()),formFields)


        inputLayout?.editText?.addTextChangedListener {
            if (inputLayout?.editText?.hasFocus() == true && it.toString().length >= 15 && hasInternetConnection(
                    context
                )
            ) {
                isGSTVerified = oldGSTNo.isBlank().not() && it.toString() == oldGSTNo
                checkGSTorPANValidity(it.toString())
            }else{
                isGSTVerified = oldGSTNo.isBlank().not() && it.toString() == oldGSTNo
            }
        }

    }



    fun setCustomerTypeView(formFieldsData: FormItemsItem) {
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

    fun getIsGstVerified(): Boolean {
        return isGSTVerified
    }

    private fun checkGSTorPANValidity(gstOrPanNo: String) {
        inputLayout?.editText?.showProgressBar()
        customerViewModel.getCustomerDetailsByGst(gstOrPanNo)
    }

    private fun observeCustomerList() {
        customerViewModel.customerDetailsLiveData.observe((context as FragmentActivity)) { data ->
            data?.let {
                serverErrorMessage = data.message
                inputLayout?.editText?.hideProgressBar()
                isGSTVerified = data.error == false
                if (::gstOrPanVerificationListener.isInitialized) {
                    gstOrPanVerificationListener.invoke(data.error == false, data)
                }
            }
        }
    }

    fun setOnGSTorPANVerificationListener(gstOrPanVerificationListener: (Boolean, CustomerPanOrGstInfoModel) -> Unit) {
        this.gstOrPanVerificationListener = gstOrPanVerificationListener
    }


    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.ALPHABETS
    }
    
    fun getErrorMessage() : String? {
        return serverErrorMessage
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }


    fun setValue(value: String) {
        oldGSTNo = value
        isGSTVerified = true
        inputLayout?.editText?.setText(value)
    }


}