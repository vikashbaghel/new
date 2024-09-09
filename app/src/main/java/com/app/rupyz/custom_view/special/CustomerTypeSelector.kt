package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils.getLabel
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.sales.customer.CustomerViewModel
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM

class CustomerTypeSelector : LinearLayoutCompat {

    private var inputLayout: EditInputLayout? = null
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private var currentPage: Int = 1
    private val customerTypeSpinnerList: MutableList<String> = mutableListOf()
    private val customerViewModel: CustomerViewModel =
        ViewModelProvider(context as FragmentActivity)[CustomerViewModel::class.java]
    private val listPopupWindow = ListPopupWindow(context, null, androidx.appcompat.R.attr.listPopupWindowStyle)
    private val adapter =
        ArrayAdapter(context, R.layout.single_text_view_spinner_16dp_text, customerTypeSpinnerList)
    private var formItemType: FormItemType? = null
    private var isPopupShown = false
    private var pageMap = mutableMapOf<Int,MutableList<String>>()


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
        observeCustomerType()
        currentPage = 1
        loadCustomerType(1)
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

        formFields?.let { inputLayout?.editText?.setEditTextType(FormItemType.DROPDOWN, it) }
        inputLayout?.isEditable = false
        if (showDropDownArrow) {
            inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
        }
        customerTypeSpinnerList.clear()
        val inputParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inputParams.setMargins(10, 20, 10, 0)
        layoutParams = inputParams

        inputLayout?.hint = getLabel((inputLayout?.editText?.hasFocus()?:false || inputLayout?.editText?.text.isNullOrBlank().not()),formFields)


        listPopupWindow.anchorView = inputLayout
        listPopupWindow.isModal = true
        listPopupWindow.setDropDownGravity(Gravity.NO_GRAVITY)
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
            if (position < this.customerTypeSpinnerList.size) {
                inputLayout?.editText?.setText(this.customerTypeSpinnerList[position])
                inputLayout?.isEndIconVisible = true
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
        inputLayout?.setEndIconOnClickListener {
            inputLayout?.editText?.text = null
            inputLayout?.isEndIconVisible = false
            if (showDropDownArrow) {
                inputLayout?.editText?.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                        null
                                                                             )
            }
        }
        inputLayout?.isEndIconVisible = false
        inputLayout?.endIconMode = END_ICON_CUSTOM
        inputLayout?.endIconDrawable = resources.getDrawable(R.drawable.ic_close_red,null)
        inputLayout?.setEndIconTintList(null)

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSpinnerItems(clearList: Boolean, spinnerList: MutableList<String>) {

        val data : MutableList<String> = mutableListOf()
        pageMap.values.forEach{
            data.addAll(it)
        }
        
        adapter.clear()
        adapter.addAll(data)
        adapter.notifyDataSetChanged()

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

    private fun loadCustomerType(currentPage: Int) {
        if (currentPage == 1){
            inputLayout?.editText?.showProgressBar()
        }
        this.currentPage = currentPage
        customerViewModel.getCustomerTypeList(
            "",
            currentPage,
            Connectivity.hasInternetConnection(context)
        )
    }

    private fun observeCustomerType() {
        customerViewModel.customerTypeLiveData.observe((context as FragmentActivity)) { data ->
            inputLayout?.editText?.hideProgressBar()
            data.data?.let { customerDataList ->
                if (customerDataList.isNotEmpty()) {
                    if (currentPage == 1) {
                        this.customerTypeSpinnerList.clear()
                    }
                    val newData = mutableListOf<String>()
                    customerDataList.forEach { customerData ->
                        this.customerTypeSpinnerList.add(customerData.name ?: "")
                        newData.add(customerData.name ?: "")
                    }
                    
                    if (pageMap.contains(currentPage).not()){
                        pageMap.put(currentPage,newData)
                        setSpinnerItems(currentPage == 1, newData)
                    }
                    loadCustomerType((currentPage + 1))
                }
            }
        }
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

    fun setValue(type: String) {
        inputLayout?.editText?.setText(type)
    }


}