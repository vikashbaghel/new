package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.app.rupyz.R
import com.app.rupyz.custom_view.CustomViewUtils
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemViewCustomerLevelAndTypeBinding
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.GeoAddressValue
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.Sections

class CustomerLevelAndType : LinearLayoutCompat {

    private val binding =
        ItemViewCustomerLevelAndTypeBinding.inflate(LayoutInflater.from(context), this, true)
    private var sectionData: Sections? = null
    private var selectedStep: CustomerLevel = CustomerLevel.LEVEL_THREE
    private var oldLevel: CustomerLevel? = null
    private val viewMap: HashMap<String, View> = hashMapOf()
    private var customerId: Int = -1
    private var formItemType: FormItemType? = null
    private var formFields: FormItemsItem? = null


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    fun init(attrs: AttributeSet?) {
        tag = javaClass.name
        orientation = VERTICAL
        binding.level1.setOnClickListener { changeLevel(CustomerLevel.LEVEL_ONE) }
        binding.level2.setOnClickListener { changeLevel(CustomerLevel.LEVEL_TWO) }
        binding.level3.setOnClickListener { changeLevel(CustomerLevel.LEVEL_THREE) }

        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                .not()
        ) {
            binding.level1.text = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
        }

        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                .not()
        ) {
            binding.level2.text = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
        }

        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                .not()
        ) {
            binding.level3.text = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
        }

    }


    fun setCustomerLevelAndType(
        sectionData: Sections,
        selectedStep: CustomerLevel = CustomerLevel.LEVEL_THREE,
        customerId: Int
    ) {
        this.sectionData = sectionData
        this.selectedStep = selectedStep
        this.customerId = customerId
        createChildViews(sectionData)
    }

    private fun createChildViews(sectionData: Sections) {
        binding.levelCustomFields.removeAllViews()
        binding.levelCustomFields.showView()
        sectionData.formItems?.forEach { formItem ->
            if (formItem?.fieldProps?.name.equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL)
                    .not() && enumContains<FormItemType>(formItem?.type!!)
            ) {
                val formItemType = FormItemType.valueOf(formItem.type)
                viewMap.putAll(
                    CustomViewUtils.createAndAddCustomView(
                        context,
                        formItemType,
                        formItem,
                        binding.levelCustomFields,
                        customerId,
                        selectedStep
                    )
                )
            }
        }.also {
            changeLevel(CustomerLevel.LEVEL_THREE)
        }
    }

    fun setCustomerLevel(level: CustomerLevel, isInEditMode: Boolean) {
        if (isInEditMode) {
            oldLevel = level
        }
        changeLevel(level)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeLevel(level: CustomerLevel) {
        when (level) {
            CustomerLevel.LEVEL_ONE -> {
                selectedStep = CustomerLevel.LEVEL_ONE
                binding.level1.setTextColor(resources.getColorStateList(R.color.color_322E80, null))
                binding.level1.background =
                    resources.getDrawable(R.drawable.selected_customer_level, null)

                binding.level2.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level2.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                binding.level3.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level3.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                if (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView) {
                    (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).apply {
                        hideView()
                        setCustomerLevel(
                            CustomerLevel.LEVEL_ONE,
                            (oldLevel == CustomerLevel.LEVEL_ONE).not()
                        )
                    }
                }
            }

            CustomerLevel.LEVEL_TWO -> {
                selectedStep = CustomerLevel.LEVEL_TWO
                binding.level1.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level1.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                binding.level2.setTextColor(resources.getColorStateList(R.color.color_322E80, null))
                binding.level2.background =
                    resources.getDrawable(R.drawable.selected_customer_level, null)

                binding.level3.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level3.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                if (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView) {
                    (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).apply {
                        showView()
                        setCustomerLevel(
                            CustomerLevel.LEVEL_TWO,
                            (oldLevel == CustomerLevel.LEVEL_TWO).not()
                        )
                    }
                }
            }

            CustomerLevel.LEVEL_THREE -> {
                selectedStep = CustomerLevel.LEVEL_THREE
                binding.level1.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level1.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                binding.level2.setTextColor(resources.getColorStateList(R.color.color_727176, null))
                binding.level2.background =
                    resources.getDrawable(R.drawable.un_selected_customer_level, null)

                binding.level3.setTextColor(resources.getColorStateList(R.color.color_322E80, null))
                binding.level3.background =
                    resources.getDrawable(R.drawable.selected_customer_level, null)

                if (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView) {
                    (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).apply {
                        showView()
                        setCustomerLevel(
                            CustomerLevel.LEVEL_THREE,
                            (oldLevel == CustomerLevel.LEVEL_THREE).not()
                        )
                    }
                }
            }
        }
    }

    fun getLevel(): CustomerLevel {
        return selectedStep
    }


    fun getViewMap(): HashMap<String, View> {
        return viewMap
    }

    fun getSection(): Sections? {
        return sectionData
    }

    override fun getRootView(): View {
        return binding.root
    }

    fun setFormFields(formFields: FormItemsItem) {
        this.formFields = formFields
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

    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = when (selectedStep) {
            CustomerLevel.LEVEL_ONE -> AppConstant.CUSTOMER_LEVEL_1
            CustomerLevel.LEVEL_TWO -> AppConstant.CUSTOMER_LEVEL_2
            CustomerLevel.LEVEL_THREE -> AppConstant.CUSTOMER_LEVEL_3
        }
        return model
    }


}
