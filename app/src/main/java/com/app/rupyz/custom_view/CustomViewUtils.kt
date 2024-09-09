package com.app.rupyz.custom_view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.rupyz.adapter.ImageWithPhotoValueAdapter
import com.app.rupyz.adapter.PhotoLabelModel
import com.app.rupyz.custom_view.basic.CheckBox
import com.app.rupyz.custom_view.basic.DateTimePicker
import com.app.rupyz.custom_view.basic.DropDown
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.basic.EditInputText
import com.app.rupyz.custom_view.basic.ImageSelector
import com.app.rupyz.custom_view.basic.ParentCardView
import com.app.rupyz.custom_view.basic.RadioBox
import com.app.rupyz.custom_view.basic.RatingBar
import com.app.rupyz.custom_view.basic.TextView
import com.app.rupyz.custom_view.special.AssignBeatDropDown
import com.app.rupyz.custom_view.special.AssignPricingGroupDropDown
import com.app.rupyz.custom_view.special.AssignProductCategoryMappingDropDown
import com.app.rupyz.custom_view.special.AssignStaffDropDown
import com.app.rupyz.custom_view.special.CustomerLevelAndType
import com.app.rupyz.custom_view.special.CustomerMapAddressPicker
import com.app.rupyz.custom_view.special.CustomerTypeSelector
import com.app.rupyz.custom_view.special.GSTorPANEditor
import com.app.rupyz.custom_view.special.MapCustomerLevelView
import com.app.rupyz.custom_view.special.PaymentTermsDropDown
import com.app.rupyz.custom_view.special.PinCodeEditor
import com.app.rupyz.custom_view.special.StateDropDown
import com.app.rupyz.custom_view.special.WhatsAppNumberEditor
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.custom_view.type.CustomerLevel.LEVEL_ONE
import com.app.rupyz.custom_view.type.CustomerLevel.LEVEL_THREE
import com.app.rupyz.custom_view.type.CustomerLevel.LEVEL_TWO
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.custom_view.type.TextViewType
import com.app.rupyz.databinding.ItemViewLabelRecyclerVerticalBinding
import com.app.rupyz.databinding.ItemViewLabelValuePhotoBinding
import com.app.rupyz.databinding.ItemViewLabelValueTextBinding
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.isCommaSeparatedIntegers
import com.app.rupyz.generic.helper.isParsableInt
import com.app.rupyz.generic.helper.isValidEmail
import com.app.rupyz.generic.helper.isValidPhoneNumber
import com.app.rupyz.generic.helper.isValidUrl
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.splitString
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.GeoAddressValue
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.NewUpdateCustomerInfoModel
import com.app.rupyz.model_kt.OrgBeatModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import java.util.regex.Pattern

object CustomViewUtils {

    fun createAndAddCustomView(
        context: Context,
        formItemType: FormItemType,
        data: FormItemsItem,
        parent: ViewGroup,
        customerId: Int,
        customerLevel: CustomerLevel,
    ): HashMap<String, View> {
        val viewMap: HashMap<String, View> = hashMapOf()
        if (data.status == AppConstant.VISIBLE) {
            when (formItemType) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.URL_INPUT, FormItemType.DECIMAL -> {
                    if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_GST_IN, true)) {

                        val itemView = GSTorPANEditor(context)
                        itemView.id = View.generateViewId()
                        itemView.setCustomerTypeView(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_GST_IN] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_PIN_CODE,
                            true
                        )
                    ) {
                        val itemView = PinCodeEditor(context)
                        itemView.id = View.generateViewId()
                        itemView.setCustomerTypeView(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_PIN_CODE] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_GEO_ADDRESS,
                            true
                        )
                    ) {
                        val itemView = CustomerMapAddressPicker(context)
                        itemView.id = View.generateViewId()
                        itemView.setMapData(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_MOBILE,
                            true
                        )
                    ) {
                        val itemView = WhatsAppNumberEditor(context)
                        itemView.id = View.generateViewId()
                        itemView.setWhatsAppMobileViewType(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_MOBILE] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else {
                        val itemView = EditInputLayout(context)
                        itemView.id = View.generateViewId()
                        itemView.setEditTextType(formItemType, data)
                        viewMap[data.fieldProps?.name ?: ""] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    }
                }

                FormItemType.DATE_PICKER -> {
                    val itemView = DateTimePicker(context)
                    itemView.id = View.generateViewId()
                    itemView.setTimePickerEnabled(false)
                    itemView.setDatePickerEnabled(true)
                    itemView.setDateTimePicker(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(itemView)
                        }
                    }
                }

                FormItemType.DATE_TIME_PICKER -> {
                    val itemView = DateTimePicker(context)
                    itemView.id = View.generateViewId()
                    itemView.setTimePickerEnabled(true)
                    itemView.setDatePickerEnabled(true)
                    itemView.setDateTimePicker(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(itemView)
                        }
                    }
                }

                FormItemType.DROPDOWN -> {
                    if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_TYPE,
                            true
                        )
                    ) {
                        val itemView = CustomerTypeSelector(context)
                        itemView.id = View.generateViewId()
                        itemView.setCustomerTypeView(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_PARENT,
                            true
                        )
                    ) {
                        val itemView = MapCustomerLevelView(context)
                        itemView.id = View.generateViewId()
                        itemView.setCustomerTypeView(data)
                        itemView.setFormItemType(formItemType)
                        itemView.setCustomerLevel(customerLevel, false)
                        itemView.setCustomerId(customerId)
                        viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_STATE, true)) {
                        val itemView = StateDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_STATE] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE,
                            true
                        )
                    ) {
                        val itemView = PaymentTermsDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_STAFF,
                            true
                        )
                    ) {
                        val itemView = AssignStaffDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data, customerId)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_SELECT_STAFF] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_BEAT,
                            true
                        )
                    ) {
                        val itemView = AssignBeatDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data, customerId)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_SELECT_BEAT] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRICING_GROUP,
                            true
                        )
                    ) {
                        val itemView = AssignPricingGroupDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY,
                            true
                        )
                    ) {
                        val itemView = AssignProductCategoryMappingDropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data, customerId)
                        itemView.setFormItemType(formItemType)
                        viewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    } else {
                        val itemView = DropDown(context)
                        itemView.id = View.generateViewId()
                        itemView.setDropDown(data)
                        viewMap[data.fieldProps?.name ?: ""] = itemView
                        when (parent) {
                            is ParentCardView -> {
                                parent.addChild(itemView)
                            }

                            else -> {
                                parent.addView(itemView)
                            }
                        }
                    }
                }

                FormItemType.FILE_UPLOAD -> {
                    val itemView = ImageSelector(context)
                    itemView.id = View.generateViewId()
                    itemView.setFormItemType(formItemType)
                    itemView.setFormFields(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(itemView)
                        }
                    }
                }

                FormItemType.CHECKBOX -> {

                    val sectionTitle = TextView(context).apply {
                        text = getLabel(false, data)
                        id = View.generateViewId()
                        setTextViewType(TextViewType.LABEL)
                    }

                    val itemView = CheckBox(context)
                    itemView.id = View.generateViewId()
                    itemView.setCheckBox(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(sectionTitle)
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(sectionTitle)
                            parent.addView(itemView)
                        }
                    }
                }

                FormItemType.RATING -> {

                    val sectionTitle = TextView(context).apply {
                        text = getLabel(false, data)
                        id = View.generateViewId()
                        setTextViewType(TextViewType.LABEL)
                    }

                    val itemView = RatingBar(context)
                    itemView.id = View.generateViewId()
                    itemView.setFormItemType(formItemType)
                    itemView.setRatingBar(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(sectionTitle)
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(sectionTitle)
                            parent.addView(itemView)
                        }
                    }
                }

                FormItemType.MULTIPLE_CHOICE -> {

                    val sectionTitle = TextView(context).apply {
                        text = getLabel(false, data)
                        id = View.generateViewId()
                        setTextViewType(TextViewType.LABEL)
                    }

                    val itemView = RadioBox(context)
                    itemView.id = View.generateViewId()
                    itemView.setRadioBox(data)
                    viewMap[data.fieldProps?.name ?: ""] = itemView
                    when (parent) {
                        is ParentCardView -> {
                            parent.addChild(sectionTitle)
                            parent.addChild(itemView)
                        }

                        else -> {
                            parent.addView(sectionTitle)
                            parent.addView(itemView)
                        }
                    }

                }
            }
        }
        return viewMap
    }

    fun createKeyValueViews(
        context: Context,
        formItemType: FormItemType,
        data: FormItemsItem,
        parent: ViewGroup,
        viewMap: HashMap<String, View>,
        customerId: Int
    ) {
        val keyValueViewMap: HashMap<String, View> = hashMapOf()
        if (data.status == AppConstant.VISIBLE) {
            when (formItemType) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.URL_INPUT, FormItemType.DECIMAL -> {
                    if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_GST_IN, true)) {
                        if (viewMap[AppConstant.SECTION_NAME_GST_IN] is GSTorPANEditor) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_GST_IN] = view
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_PIN_CODE,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_PIN_CODE] is PinCodeEditor) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_PIN_CODE] as PinCodeEditor).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_PIN_CODE] = view
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_GEO_ADDRESS,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] is CustomerMapAddressPicker) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] as CustomerMapAddressPicker).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.geoAddressValue
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] = view
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is EditInputLayout) {
                            val dataFieldValue = (viewMap[data.fieldProps?.name
                                ?: ""] as EditInputLayout).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[data.fieldProps?.name ?: ""] = view
                        }
                    }
                }

                FormItemType.DATE_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        val view = getKeyValueLayout(
                            context = context,
                            key = dataFieldValue.label,
                            value = dataFieldValue.value
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }

                FormItemType.DATE_TIME_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        val view = getKeyValueLayout(
                            context = context,
                            key = dataFieldValue.label,
                            value = dataFieldValue.value
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }

                FormItemType.DROPDOWN -> {
                    if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_LEVEL,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerLevelAndType) {
                            val dataFieldValue = (viewMap[data.fieldProps?.name
                                ?: ""] as CustomerLevelAndType).getFieldValue()
                            val dataFieldLevel =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getLevel()
                            val level = when (dataFieldLevel) {
                                LEVEL_ONE -> {
                                    if (SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                                            .not()
                                    ) {
                                        SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_1)
                                    } else {
                                        AppConstant.SUPER_STOCKIST_LEVEL
                                    }
                                }

                                LEVEL_TWO -> {
                                    if (SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                                            .not()
                                    ) {
                                        SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_2)
                                    } else {
                                        AppConstant.DISTRIBUTOR_LEVEL
                                    }
                                }

                                LEVEL_THREE -> {
                                    if (SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                                            .not()
                                    ) {
                                        SharedPref.getInstance()
                                            .getString(AppConstant.CUSTOMER_LEVEL_3)
                                    } else {
                                        AppConstant.RETAILERS_LEVEL
                                    }
                                }
                            }
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = level
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] = view

                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerTypeSelector) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE] as CustomerTypeSelector).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE] = view
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_PARENT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is MapCustomerLevelView) {
                            val dataFieldKey =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).getFieldValue()
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).getSelectedStaff()
                            if (dataFieldKey != null) {
                                val view = getMapCustomerLevelView(
                                    context = context,
                                    key = AppConstant.PARENT_NAME,
                                    value = dataFieldValue.values,
                                    cornerRadius = 10f
                                )
                                addViewToParent(parent, view)
                                keyValueViewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] = view
                            }
                        }
                    } else if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_STATE, true)) {
                        if (viewMap[data.fieldProps?.name ?: ""] is StateDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_STATE] as StateDropDown).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_STATE] = view
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is PaymentTermsDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE] as PaymentTermsDropDown).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE] = view
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_STAFF,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignStaffDropDown) {
                            if (customerId == 0 || customerId == -1) {
                                val dataFieldKey =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_STAFF] as AssignStaffDropDown).getFieldValue()
                                val dataFieldValue =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_STAFF] as AssignStaffDropDown).getSelectedStaff()
                                val view = getStaffListView(
                                    context = context,
                                    key = dataFieldKey.label,
                                    value = dataFieldValue.values,
                                    cornerRadius = 50f
                                )
                                addViewToParent(parent, view)
                                keyValueViewMap[AppConstant.SECTION_NAME_SELECT_STAFF] = view
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_BEAT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignBeatDropDown) {
                            if (customerId == 0 || customerId == -1) {
                                val dataFieldKey =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_BEAT] as AssignBeatDropDown).getFieldValue()
                                val dataFieldValue =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_BEAT] as AssignBeatDropDown).getSelectedBeat()
                                val view = getOrgBeatView(
                                    context = context,
                                    key = dataFieldKey.label,
                                    value = dataFieldValue.values,
                                    cornerRadius = 50f
                                )
                                addViewToParent(parent, view)
                                keyValueViewMap[AppConstant.SECTION_NAME_SELECT_BEAT] = view
                            }
                        }

                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRICING_GROUP,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignPricingGroupDropDown) {
                            val dataFieldKey =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] as AssignPricingGroupDropDown).getFieldValue()
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] as AssignPricingGroupDropDown).getSelectedPriceGroup()
                            if (dataFieldValue?.name != null) {
                                val view = getKeyValueLayout(
                                    context = context,
                                    key = dataFieldKey.label,
                                    value = dataFieldValue?.name.toString()
                                )
                                addViewToParent(parent, view)
                                keyValueViewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] =
                                    view
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name
                                ?: ""] is AssignProductCategoryMappingDropDown
                        ) {
                            if (customerId == 0 || customerId == -1) {
                                val dataFieldKey =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] as AssignProductCategoryMappingDropDown).getFieldValue()
                                val dataFieldValue =
                                    (viewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] as AssignProductCategoryMappingDropDown).getSelectedProductCategories()
                                val view = getAllCategoryResponseView(
                                    context = context,
                                    key = dataFieldKey.label,
                                    value = dataFieldValue.values,
                                    cornerRadius = 50f
                                )
                                addViewToParent(parent, view)
                                keyValueViewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] =
                                    view
                            }
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is DropDown) {
                            val dataFieldValue =
                                (viewMap[data.fieldProps?.name ?: ""] as DropDown).getFieldValue()
                            val view = getKeyValueLayout(
                                context = context,
                                key = dataFieldValue.label,
                                value = dataFieldValue.value
                            )
                            addViewToParent(parent, view)
                            keyValueViewMap[data.fieldProps?.name ?: ""] = view
                        }
                    }
                }

                FormItemType.FILE_UPLOAD -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is ImageSelector) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as ImageSelector).getPhotoList()
                        val view = getImageSelectorView(
                            context = context,
                            key = data.fieldProps?.label,
                            value = dataFieldValue
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }

                FormItemType.CHECKBOX -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is CheckBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as CheckBox).getFieldValue()
                        val view = getKeyValueLayout(
                            context = context,
                            key = data.fieldProps?.label,
                            value = dataFieldValue.value
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }

                FormItemType.RATING -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RatingBar) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RatingBar).getFieldValue()
                        val view = getKeyValueLayout(
                            context = context,
                            key = data.fieldProps?.label,
                            value = dataFieldValue.value
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }

                FormItemType.MULTIPLE_CHOICE -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RadioBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RadioBox).getFieldValue()
                        val view = getKeyValueLayout(
                            context = context,
                            key = data.fieldProps?.label,
                            value = dataFieldValue.value
                        )
                        addViewToParent(parent, view)
                        keyValueViewMap[data.fieldProps?.name ?: ""] = view
                    }
                }
            }
        }
    }

    fun validateViews(
        formItemType: FormItemType,
        data: FormItemsItem,
        viewMap: HashMap<String, View>,
        assignedDistributorCount: Int
    ): HashMap<String, Pair<String?, Boolean>> {
        val validationMap: HashMap<String, Pair<String?, Boolean>> = hashMapOf()
        if (data.status == AppConstant.VISIBLE) {
            validationMap[data.fieldProps?.name ?: ""] = Pair(null, false)
            when (formItemType) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.MOBILE_NUMBER, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.DECIMAL -> {
                    if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_GST_IN, true)) {
                        if (viewMap[AppConstant.SECTION_NAME_GST_IN] is GSTorPANEditor) {
                            val fieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).getFieldValue()
                            val isVerified =
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).getIsGstVerified()
                            val serverErrorMessage =
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).getErrorMessage()
                            if (data.fieldProps?.required == true) {
                                validationMap[AppConstant.SECTION_NAME_GST_IN] =
                                    Pair(serverErrorMessage, isVerified)
                            } else {
                                validationMap[AppConstant.SECTION_NAME_GST_IN] = Pair(
                                    if (isVerified.not()) {
                                        serverErrorMessage
                                    } else {
                                        null
                                    }, (isVerified || fieldValue.value.isNullOrBlank())
                                )
                            }
                        }
                    } else if (data.fieldProps?.name?.contains(
                            AppConstant.SECTION_NAME_MOBILE,
                            true
                        ) == true
                    ) {
                        if (data.fieldProps.name.equals(AppConstant.SECTION_NAME_MOBILE, true)) {
                            if (viewMap[AppConstant.SECTION_NAME_MOBILE] is WhatsAppNumberEditor) {
                                val isMobileNumberUnique =
                                    (viewMap[AppConstant.SECTION_NAME_MOBILE] as WhatsAppNumberEditor).getIsMobileNumberUnique()
                                val dataFieldValue =
                                    (viewMap[AppConstant.SECTION_NAME_MOBILE] as WhatsAppNumberEditor).getFieldValue()
                                val errorMessage =
                                    (viewMap[AppConstant.SECTION_NAME_MOBILE] as WhatsAppNumberEditor).getErrorMessage()
                                if (data.fieldProps?.required == true) {
                                    validationMap[AppConstant.SECTION_NAME_MOBILE] = Pair(
                                            if (isMobileNumberUnique.not()) {
                                                errorMessage
                                            } else {
                                                null
                                            },
                                        ((isMobileNumberUnique))
                                    )
                                } else {
                                    validationMap[AppConstant.SECTION_NAME_MOBILE] =
                                        Pair(null, true)
                                }
                            }
                        } else if (viewMap[data.fieldProps.name] is EditInputLayout) {
                            val dataFieldValue =
                                (viewMap[data.fieldProps.name] as EditInputLayout).getFieldValue()
                            if (data.fieldProps.required == true) {
                                validationMap[data.fieldProps.name] = Pair(
                                    null,
                                    ((dataFieldValue.value.isNullOrBlank()
                                        .not()) && (dataFieldValue.value?.isValidPhoneNumber()
                                        ?: false))
                                )
                            } else {
                                validationMap[data.fieldProps.name] = Pair(
                                    null,
                                    (dataFieldValue.value.isNullOrBlank() || dataFieldValue.value?.length == AppConstant.SECTION_NAME_MOBILE_LENGTH)
                                )
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_PIN_CODE,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_PIN_CODE] is PinCodeEditor) {
                            val isValid =
                                (viewMap[AppConstant.SECTION_NAME_PIN_CODE] as PinCodeEditor).getIsAValidPinCode()
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_PIN_CODE] as PinCodeEditor).getFieldValue()
                            if (data.fieldProps?.required == true) {
                                validationMap[AppConstant.SECTION_NAME_PIN_CODE] =
                                    Pair(null, isValid)
                            } else {
                                validationMap[AppConstant.SECTION_NAME_PIN_CODE] =
                                    Pair(null, isValid || dataFieldValue.value.isNullOrBlank())
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_GEO_ADDRESS,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] is CustomerMapAddressPicker) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] as CustomerMapAddressPicker).getFieldValue()
                            if (data.fieldProps?.required == true) {
                                validationMap[AppConstant.SECTION_NAME_GEO_ADDRESS] = Pair(
                                    null,
                                    dataFieldValue.value.isNullOrBlank()
                                        .not() && dataFieldValue.geoLocationLat != null && dataFieldValue.geoLocationLong != null
                                )
                            } else {
                                validationMap[AppConstant.SECTION_NAME_GEO_ADDRESS] =
                                    Pair(null, true)
                            }
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is EditInputLayout) {
                            val dataFieldValue = (viewMap[data.fieldProps?.name
                                ?: ""] as EditInputLayout).getFieldValue()
                            if (data.fieldProps?.required == true) {
                                validationMap[data.fieldProps.name ?: ""] =
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                            } else {
                                validationMap[data.fieldProps?.name ?: ""] = Pair(null, true)
                            }
                        }
                    }
                }

                FormItemType.EMAIL_ADDRESS -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is EditInputLayout) {
                        val dataFieldValue = (viewMap[data.fieldProps?.name
                            ?: ""] as EditInputLayout).getFieldValue()
                        if (data.fieldProps?.required == true) {
                            validationMap[data.fieldProps.name ?: ""] = Pair(
                                null,
                                dataFieldValue.value.isNullOrBlank()
                                    .not() && (dataFieldValue.value?.isValidEmail() == true)
                            )
                        } else {
                            validationMap[data.fieldProps?.name ?: ""] = Pair(
                                null,
                                dataFieldValue.value.isNullOrEmpty() || (dataFieldValue.value?.isValidEmail() == true)
                            )
                        }
                    }
                }

                FormItemType.URL_INPUT -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is EditInputLayout) {
                        val dataFieldValue = (viewMap[data.fieldProps?.name
                            ?: ""] as EditInputLayout).getFieldValue()
                        if (data.fieldProps?.required == true) {
                            validationMap[data.fieldProps.name ?: ""] = Pair(
                                null,
                                dataFieldValue.value.isNullOrBlank()
                                    .not() && (dataFieldValue.value?.isValidUrl() == true)
                            )
                        } else {
                            validationMap[data.fieldProps?.name ?: ""] = Pair(
                                null,
                                dataFieldValue.value.isNullOrEmpty() || (dataFieldValue.value?.isValidUrl() == true)
                            )
                        }
                    }
                }

                FormItemType.DATE_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        if (data.fieldProps?.required == true) {
                            validationMap[data.fieldProps.name ?: ""] =
                                Pair(null, dataFieldValue.value.isNullOrBlank().not())
                        } else {
                            validationMap[data.fieldProps?.name ?: ""] = Pair(null, true)
                        }
                    }
                }

                FormItemType.DATE_TIME_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        if (data.fieldProps?.required == true) {
                            validationMap[data.fieldProps.name ?: ""] =
                                Pair(null, dataFieldValue.value.isNullOrBlank().not())
                        } else {
                            validationMap[data.fieldProps?.name ?: ""] = Pair(null, true)
                        }
                    }
                }

                FormItemType.DROPDOWN -> {
                    if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_LEVEL,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerLevelAndType) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getLevel()
                            val dataFieldMap =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getViewMap()
                            if (data.fieldProps?.required == true) {
                                validationMap[data.fieldProps.name ?: ""] = when (dataFieldValue) {
                                    LEVEL_ONE -> Pair(null, true)
                                    LEVEL_TWO -> {
                                        if ((dataFieldMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView)) {
                                            (dataFieldMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).let { view ->
                                                Pair(
                                                    null,
                                                    (view.getSelectedStaff().isEmpty()
                                                        .not() || (view.getIsInEditMode() && view.isAllMappedParentLelDeselected(
                                                        assignedDistributorCount
                                                    ).not()))
                                                )
                                            }
                                        } else {
                                            Pair(null, false)
                                        }
                                    }

                                    LEVEL_THREE -> {
                                        if ((dataFieldMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView)) {
                                            (dataFieldMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).let { view ->
                                                Pair(
                                                    null,
                                                    (view.getSelectedStaff().isEmpty()
                                                        .not() || (view.getIsInEditMode() && view.isAllMappedParentLelDeselected(
                                                        assignedDistributorCount
                                                    ).not()))
                                                )
                                            }
                                        } else {
                                            Pair(null, false)
                                        }
                                    }
                                }
                            } else {
                                validationMap[data.fieldProps?.name ?: ""] = Pair(null, true)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerTypeSelector) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE] as CustomerTypeSelector).getFieldValue()
                            if (data.fieldProps?.required == true) {
                                validationMap[data.fieldProps.name ?: ""] =
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                            } else {
                                validationMap[data.fieldProps?.name ?: ""] = Pair(null, true)
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_PARENT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is MapCustomerLevelView) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).getSelectedStaff()
                            val isInEditMode =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).getIsInEditMode()
                            val isAllMappedParentLelDeselected =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).isAllMappedParentLelDeselected(
                                    assignedDistributorCount
                                ).not()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    if (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] is CustomerLevelAndType) {
                                        val levelValue =
                                            (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getLevel()
                                        when (levelValue) {
                                            LEVEL_ONE -> Pair(null, true)
                                            LEVEL_TWO, LEVEL_THREE -> {
                                                Pair(
                                                    null,
                                                    (dataFieldValue.isEmpty()
                                                        .not() || (isInEditMode && isAllMappedParentLelDeselected))
                                                )
                                            }
                                        }
                                    } else {
                                        Pair(
                                            null,
                                            (dataFieldValue.isEmpty()
                                                .not() || (isInEditMode && isAllMappedParentLelDeselected))
                                        )
                                    }
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_STATE, true)) {
                        if (viewMap[data.fieldProps?.name ?: ""] is StateDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_STATE] as StateDropDown).getFieldValue()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is PaymentTermsDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE] as PaymentTermsDropDown).getFieldValue()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_STAFF,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignStaffDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_STAFF] as AssignStaffDropDown).getSelectedStaff()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.isEmpty().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_BEAT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignBeatDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_BEAT] as AssignBeatDropDown).getSelectedBeat()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.isEmpty().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRICING_GROUP,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignPricingGroupDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] as AssignPricingGroupDropDown).getFieldValue()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name
                                ?: ""] is AssignProductCategoryMappingDropDown
                        ) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] as AssignProductCategoryMappingDropDown).getSelectedProductCategories()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.isEmpty().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is DropDown) {
                            val dataFieldValue =
                                (viewMap[data.fieldProps?.name ?: ""] as DropDown).getFieldValue()
                            validationMap[data.fieldProps?.name ?: ""] =
                                if (data.fieldProps?.required == true) {
                                    Pair(null, dataFieldValue.value.isNullOrBlank().not())
                                } else {
                                    Pair(null, true)
                                }
                        }
                    }
                }

                FormItemType.FILE_UPLOAD -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is ImageSelector) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as ImageSelector).getPhotoList()
                        validationMap[data.fieldProps?.name ?: ""] =
                            if (data.fieldProps?.required == true) {
                                Pair(null, dataFieldValue.isEmpty().not())
                            } else {
                                Pair(null, true)
                            }
                    }
                }

                FormItemType.CHECKBOX -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is CheckBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as CheckBox).getCheckList()
                        validationMap[data.fieldProps?.name ?: ""] =
                            if (data.fieldProps?.required == true) {
                                Pair(null, dataFieldValue.isEmpty().not())
                            } else {
                                Pair(null, true)
                            }
                    }
                }

                FormItemType.RATING -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RatingBar) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RatingBar).getFieldValue()
                        validationMap[data.fieldProps?.name ?: ""] =
                            if (data.fieldProps?.required == true) {
                                Pair(null, dataFieldValue.value.isNullOrBlank().not())
                            } else {
                                Pair(null, true)
                            }
                    }
                }

                FormItemType.MULTIPLE_CHOICE -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RadioBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RadioBox).getFieldValue()
                        validationMap[data.fieldProps?.name ?: ""] =
                            if (data.fieldProps?.required == true) {
                                Pair(null, dataFieldValue.value.isNullOrBlank().not())
                            } else {
                                Pair(null, true)
                            }
                    }
                }
            }
        }
        return validationMap
    }

    fun getDataToSendToServer(
        formItemType: FormItemType,
        data: FormItemsItem,
        viewMap: HashMap<String, View>
    ): HashMap<String, NameAndValueSetInfoModel?> {
        val serverDataMap: HashMap<String, NameAndValueSetInfoModel?> = hashMapOf()
        if (data.status == AppConstant.VISIBLE) {
            serverDataMap[data.fieldProps?.name ?: ""] = null
            when (formItemType) {
                FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.URL_INPUT, FormItemType.DECIMAL -> {
                    if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_GST_IN, true)) {
                        if (viewMap[AppConstant.SECTION_NAME_GST_IN] is GSTorPANEditor) {
                            val fieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).getFieldValue()
                            serverDataMap[AppConstant.SECTION_NAME_GST_IN] = fieldValue

                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_PIN_CODE,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_PIN_CODE] is PinCodeEditor) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_PIN_CODE] as PinCodeEditor).getFieldValue()
                            serverDataMap[AppConstant.SECTION_NAME_PIN_CODE] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_GEO_ADDRESS,
                            true
                        )
                    ) {
                        if (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] is CustomerMapAddressPicker) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] as CustomerMapAddressPicker).getFieldValue()
                            serverDataMap[AppConstant.SECTION_NAME_GEO_ADDRESS] = dataFieldValue
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is EditInputLayout) {
                            val dataFieldValue = (viewMap[data.fieldProps?.name
                                ?: ""] as EditInputLayout).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        } else if (viewMap[data.fieldProps?.name ?: ""] is WhatsAppNumberEditor) {
                            val dataFieldValue = (viewMap[data.fieldProps?.name
                                ?: ""] as WhatsAppNumberEditor).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }

                    }
                }

                FormItemType.DATE_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                    }
                }

                FormItemType.DATE_TIME_PICKER -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is DateTimePicker) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as DateTimePicker).getFieldValue()
                        serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                    }
                }

                FormItemType.DROPDOWN -> {
                    if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_LEVEL,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerLevelAndType) {
                            val dataFieldMap =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getViewMap()
                            val sectionData =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getSection()
                            val fieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] as CustomerLevelAndType).getFieldValue()
                            serverDataMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL] = fieldValue
                            sectionData?.formItems?.forEach { formItem ->
                                if (formItem?.fieldProps?.name.equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL)
                                        .not()
                                ) {
                                    if (enumContains<FormItemType>(formItem?.type!!)) {
                                        val type = FormItemType.valueOf(formItem.type)
                                        serverDataMap.putAll(
                                            getDataToSendToServer(
                                                formItemType = type,
                                                data = formItem,
                                                viewMap = dataFieldMap
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is CustomerTypeSelector) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE] as CustomerTypeSelector).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_CUSTOMER_PARENT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is MapCustomerLevelView) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).getFieldValue()
                            dataFieldValue?.let {
                                serverDataMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] =
                                    dataFieldValue
                            }
                        }
                    } else if (data.fieldProps?.name.equals(AppConstant.SECTION_NAME_STATE, true)) {
                        if (viewMap[data.fieldProps?.name ?: ""] is StateDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_STATE] as StateDropDown).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is PaymentTermsDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE] as PaymentTermsDropDown).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_STAFF,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignStaffDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_STAFF] as AssignStaffDropDown).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_BEAT,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignBeatDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_BEAT] as AssignBeatDropDown).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRICING_GROUP,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name ?: ""] is AssignPricingGroupDropDown) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP] as AssignPricingGroupDropDown).getFieldValue()
                            if (dataFieldValue.value != null) {
                                serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                            }
                        }
                    } else if (data.fieldProps?.name.equals(
                            AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY,
                            true
                        )
                    ) {
                        if (viewMap[data.fieldProps?.name
                                ?: ""] is AssignProductCategoryMappingDropDown
                        ) {
                            val dataFieldValue =
                                (viewMap[AppConstant.SECTION_NAME_SELECT_PRODUCT_CATEGORY] as AssignProductCategoryMappingDropDown).getFieldValue()
                            if (dataFieldValue.value != null) {
                                serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                            }
                        }
                    } else {
                        if (viewMap[data.fieldProps?.name ?: ""] is DropDown) {
                            val dataFieldValue =
                                (viewMap[data.fieldProps?.name ?: ""] as DropDown).getFieldValue()
                            serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                        }
                    }
                }

                FormItemType.FILE_UPLOAD -> {
//					if (viewMap[data.fieldProps?.name ?: ""] is ImageSelector) {
//						val dataFieldValue = (viewMap[data.fieldProps?.name ?: ""] as ImageSelector).getPhotoList()
//						serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue.map { it?.imagePath }.joinToString { "," }
//					}
                }

                FormItemType.CHECKBOX -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is CheckBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as CheckBox).getFieldValue()
                        serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                    }
                }

                FormItemType.RATING -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RatingBar) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RatingBar).getFieldValue()
                        serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                    }
                }

                FormItemType.MULTIPLE_CHOICE -> {
                    if (viewMap[data.fieldProps?.name ?: ""] is RadioBox) {
                        val dataFieldValue =
                            (viewMap[data.fieldProps?.name ?: ""] as RadioBox).getFieldValue()
                        serverDataMap[data.fieldProps?.name ?: ""] = dataFieldValue
                    }
                }
            }
        }
        return serverDataMap
    }

    fun getFileDataToSendToServer(
        formItemType: FormItemType,
        data: FormItemsItem,
        viewMap: HashMap<String, View>
    ): HashMap<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>> {
        val serverDataMap: HashMap<String, Pair<FormItemsItem, ArrayList<AddedPhotoModel?>?>> =
            hashMapOf()
        if (data.status == AppConstant.VISIBLE) {
            serverDataMap[data.fieldProps?.name ?: ""] = Pair(data, null)
            if (viewMap[data.fieldProps?.name ?: ""] is ImageSelector) {
                val dataFieldValue =
                    (viewMap[data.fieldProps?.name ?: ""] as ImageSelector).getPhotoList()
                serverDataMap[data.fieldProps?.name ?: ""] = Pair(data, dataFieldValue)
            }
        }
        return serverDataMap
    }

    fun setEditDataInFields(
        formItemType: FormItemType,
        data: NameAndValueSetInfoModel,
        viewMap: HashMap<String, View>,
        beatList: ArrayList<Int>,
        pricingGroupId: String?
    ) {

        when (formItemType) {
            FormItemType.SHORT_ANSWER, FormItemType.LONG_ANSWER, FormItemType.MOBILE_NUMBER, FormItemType.EMAIL_ADDRESS, FormItemType.ALPHABETS, FormItemType.NUMBERS, FormItemType.URL_INPUT, FormItemType.DECIMAL -> {
                if (data.name.equals(AppConstant.SECTION_NAME_GST_IN, true)) {
                    if (viewMap[AppConstant.SECTION_NAME_GST_IN] is GSTorPANEditor) {
                        if (data.value.isNullOrBlank().not()) {
                            data.value?.let {
                                (viewMap[AppConstant.SECTION_NAME_GST_IN] as GSTorPANEditor).setValue(
                                    it
                                )
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_PIN_CODE, true)) {
                    if (viewMap[AppConstant.SECTION_NAME_PIN_CODE] is PinCodeEditor) {
                        if (data.value.isNullOrBlank().not()) {
                            data.value?.let {
                                (viewMap[AppConstant.SECTION_NAME_PIN_CODE] as PinCodeEditor).setValue(
                                    it
                                )
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_GEO_ADDRESS, true)) {
                    if (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] is CustomerMapAddressPicker) {
                        if (data.geoAddressValue.isNullOrBlank().not()) {
                            data.geoAddressValue?.let {
                                (viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS] as CustomerMapAddressPicker).setValue(
                                    it,
                                    data.geoLocationLat ?: 0.0,
                                    data.geoLocationLong ?: 0.0
                                )
                            }
                        }
                    }
                } else {
                    if (viewMap[data.name ?: ""] is WhatsAppNumberEditor) {
                        if (data.value.isNullOrBlank().not()) {
                            log("${data.name}     ${data.value}")
                            data.value?.let {
                                (viewMap[data.name ?: ""] as WhatsAppNumberEditor).setValue(it)
                            }
                        }
                    }
                    if (viewMap[data.name ?: ""] is EditInputLayout) {
                        if (data.value.isNullOrBlank().not()) {
                            log("${data.name}     ${data.value}")
                            data.value?.let {
                                (viewMap[data.name ?: ""] as EditInputLayout).setValue(it)
                            }
                        }
                    }
                    if (viewMap[data.name ?: ""] is EditInputText) {
                        if (data.value.isNullOrBlank().not()) {
                            log("${data.name}     ${data.value}")
                            data.value?.let {
                                (viewMap[data.name ?: ""] as EditInputText).setValue(it)
                            }
                        }
                    }
                }
            }

            FormItemType.DATE_PICKER -> {
                if (viewMap[data.name ?: ""] is DateTimePicker) {
                    if (data.value.isNullOrBlank().not()) {
                        data.value?.let {
                            (viewMap[data.name ?: ""] as DateTimePicker).setValue(it)
                        }
                    }
                }
            }

            FormItemType.DATE_TIME_PICKER -> {
                if (viewMap[data.name ?: ""] is DateTimePicker) {
                    if (data.value.isNullOrBlank().not()) {
                        data.value?.let {
                            (viewMap[data.name ?: ""] as DateTimePicker).setValue(it)
                        }
                    }
                }
            }

            FormItemType.DROPDOWN -> {
                if (data.name.equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL, true)) {
                    if (viewMap[data.name ?: ""] is CustomerLevelAndType) {
                        if (data.value.isNullOrBlank().not()) {
                            data.value?.let {
                                when (it) {
                                    AppConstant.CUSTOMER_LEVEL_1 -> {
                                        (viewMap[data.name
                                            ?: ""] as CustomerLevelAndType).setCustomerLevel(
                                            LEVEL_ONE,
                                            true
                                        )
                                    }

                                    AppConstant.CUSTOMER_LEVEL_2 -> {
                                        (viewMap[data.name
                                            ?: ""] as CustomerLevelAndType).setCustomerLevel(
                                            LEVEL_TWO,
                                            true
                                        )
                                    }

                                    AppConstant.CUSTOMER_LEVEL_3 -> {
                                        (viewMap[data.name
                                            ?: ""] as CustomerLevelAndType).setCustomerLevel(
                                            LEVEL_THREE,
                                            true
                                        )
                                    }
                                }
                            }
                        }
                        (viewMap[data.name ?: ""] as CustomerLevelAndType).getViewMap().apply {
                            if (this[AppConstant.SECTION_NAME_CUSTOMER_PARENT] is MapCustomerLevelView) {
                                (viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT] as MapCustomerLevelView).setIsInEditMode(
                                    true
                                )
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_CUSTOMER_TYPE, true)) {
                    if (viewMap[data.name ?: ""] is CustomerTypeSelector) {
                        if (data.value.isNullOrBlank().not()) {
                            data.value?.let {
                                (viewMap[data.name ?: ""] as CustomerTypeSelector).setValue(it)
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_CUSTOMER_PARENT, true)) {
                    if (viewMap[data.name ?: ""] is MapCustomerLevelView) {
                        (viewMap[data.name ?: ""] as MapCustomerLevelView).setIsInEditMode(true)
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_STATE, true)) {
                    if (viewMap[data.name ?: ""] is StateDropDown) {
                        if (data.value.isNullOrBlank().not()) {
                            if (viewMap[data.name ?: ""] is StateDropDown) {
                                data.value?.let {
                                    (viewMap[data.name ?: ""] as StateDropDown).setValue(it)
                                }
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE, true)) {
                    if (viewMap[data.name ?: ""] is PaymentTermsDropDown) {
                        if (data.value.isNullOrBlank().not()) {
                            if (viewMap[data.name ?: ""] is PaymentTermsDropDown) {
                                data.value?.let {
                                    (viewMap[data.name ?: ""] as PaymentTermsDropDown).setValue(it)
                                }
                            }
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_SELECT_BEAT, true)) {
                    if (viewMap[data.name ?: ""] is AssignBeatDropDown) {
                        beatList.let {
                            (viewMap[data.name ?: ""] as AssignBeatDropDown).setAlreadyAddedBead(it)
                        }
                    }
                } else if (data.name.equals(AppConstant.SECTION_NAME_SELECT_PRICING_GROUP, true)) {
                    if (viewMap[data.name ?: ""] is AssignPricingGroupDropDown) {
                        data.value.let { pricingGroupName ->
                            pricingGroupId?.let { pricingGroupId ->
                                (viewMap[data.name ?: ""] as AssignPricingGroupDropDown).setValue(
                                    pricingGroupName,
                                    pricingGroupName
                                )
                            }
                        }
                    }
                } else {
                    if (data.value.isNullOrBlank().not()) {
                        if (viewMap[data.name ?: ""] is DropDown) {
                            data.value?.let {
                                (viewMap[data.name ?: ""] as DropDown).setValue(it)
                            }
                        }
                    }
                }
            }

            FormItemType.FILE_UPLOAD -> {
                if (viewMap[data.name ?: ""] is ImageSelector) {
                    if (data.imgUrls.isNullOrEmpty().not()) {
                        if (viewMap[data.name ?: ""] is ImageSelector) {
                            val imageList: ArrayList<AddedPhotoModel?> = arrayListOf()
                            val ids : List<String>? = if (data.value?.isCommaSeparatedIntegers() == true){
                                if ((data.value?.split(Pattern.quote(","))?.size ?: 0) >= (data.imgUrls?.size ?: 0)){
                                    data.value?.split(Pattern.quote(","))
                                }
                                else if ((data.value?.split(",")?.size ?: 0) >= (data.imgUrls?.size ?: 0)){
                                    data.value?.split(",")
                                }else{
                                    data.value?.splitString(',')
                                }
                            }else{
                                data.value?.let { listOf(it) }?: listOf()
                            }
                            /*val ids = data.value?.split(Pattern.quote(","))*/
                            data.imgUrls?.forEachIndexed { index, value ->
                                imageList.add(
                                    AddedPhotoModel(
                                        imageId = if (ids != null && index < ids.size && ids[index].isParsableInt()) {
                                            ids[index].toInt()
                                        } else {
                                            null
                                        }, imagePath = value, onEditProduct = true
                                    )
                                )
                            }
                            data.value?.let {
                                (viewMap[data.name ?: ""] as ImageSelector).setPhotoList(imageList)
                            }
                        }
                    }
                }
            }

            FormItemType.CHECKBOX -> {
                if (viewMap[data.name ?: ""] is CheckBox) {
                    if (data.value.isNullOrBlank().not()) {
                        if (viewMap[data.name ?: ""] is CheckBox) {
                            data.value?.let {
                                (viewMap[data.name ?: ""] as CheckBox).setValue(it)
                            }
                        }
                    }
                }
            }

            FormItemType.RATING -> {
                if (viewMap[data.name ?: ""] is RatingBar) {
                    if (data.value.isNullOrBlank().not()) {
                        if (viewMap[data.name ?: ""] is RatingBar) {
                            data.value?.let {
                                (viewMap[data.name ?: ""] as RatingBar).setValue(it)
                            }
                        }
                    }
                }
            }

            FormItemType.MULTIPLE_CHOICE -> {

                if (data.value.isNullOrBlank().not()) {
                    if (viewMap[data.name ?: ""] is RadioBox) {
                        data.value?.let {
                            (viewMap[data.name ?: ""] as RadioBox).setValue(it)
                        }
                    }
                }
            }
        }
    }

    fun setEditDataInFieldsForOldUser(
        data: NewUpdateCustomerInfoModel.Data,
        viewMap: HashMap<String, View>
    ) {

        viewMap[AppConstant.SECTION_NAME_CUSTOMER_LEVEL]?.let { view ->
            if (view is CustomerLevelAndType) {
                when {
                    data.customerLevel.equals(AppConstant.CUSTOMER_LEVEL_1, true) -> {
                        view.setCustomerLevel(LEVEL_ONE, true)
                    }

                    data.customerLevel.equals(AppConstant.CUSTOMER_LEVEL_2, true) -> {
                        view.setCustomerLevel(LEVEL_TWO, true)
                    }

                    data.customerLevel.equals(AppConstant.CUSTOMER_LEVEL_3, true) -> {
                        view.setCustomerLevel(LEVEL_THREE, true)
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_CUSTOMER_PARENT]?.let { view ->
            if (view is MapCustomerLevelView) {
                data.customerParentName?.let { name ->
                    data.customerParent?.let { id ->
                        try {
                            view.setIsInEditMode(true)
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_CUSTOMER_TYPE]?.let { view ->
            if (view is CustomerTypeSelector) {
                data.customerType?.let { type ->
                    try {
                        view.setValue(type)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_WHATSAPP_MOBILE]?.let { view ->
            if (view is EditInputLayout) {
                data.whatsappOpt?.let { opted ->
                    try {
                        if (opted) {
                            data.mobile?.let { mobile ->
                                view.setValue(mobile)
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is WhatsAppNumberEditor) {
                data.whatsappOpt?.let { opted ->
                    try {
                        if (opted) {
                            data.mobile?.let { mobile ->
                                view.setValue(mobile)
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_GST_IN]?.let { view ->
            if (view is GSTorPANEditor) {
                data.gstin?.let { gstin ->
                    try {
                        view.setValue(gstin)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_NAME]?.let { view ->
            if (view is EditInputLayout) {
                data.name?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_OWNER_NAME]?.let { view ->
            if (view is EditInputLayout) {
                data.contactPersonName?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_MOBILE]?.let { view ->
            if (view is EditInputLayout) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is WhatsAppNumberEditor) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let { view ->
            if (view is CustomerMapAddressPicker) {
                data.geoAddress?.let { geoAddress ->
                    try {
                        if (data.geoAddress.isNullOrBlank().not()) {
                            view.setValue(
                                address = geoAddress,
                                geoLocationLat = data.geoLocationLat ?: 0.0,
                                geoLocationLong = data.geoLocationLong ?: 0.0
                            )
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
            if (view is EditInputLayout) {
                data.addressLine1?.let { addressLine1 ->
                    try {
                        view.setValue(addressLine1)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_2]?.let { view ->
            if (view is EditInputLayout) {
                data.addressLine2?.let { addressLine2 ->
                    try {
                        view.setValue(addressLine2)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
            if (view is PinCodeEditor) {
                data.pincode?.let { pincode ->
                    try {
                        view.setValue(pincode)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_CITY]?.let { view ->
            if (view is EditInputLayout) {
                data.city?.let { city ->
                    try {
                        view.setValue(city)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_STATE]?.let { view ->
            if (view is EditInputLayout) {
                data.state?.let { state ->
                    try {
                        view.setValue(state)
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is StateDropDown) {
                data.state?.let { state ->
                    try {
                        view.setValue(state)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_LOGO_IMAGE]?.let { view ->
            if (data.logoImageUrl.isNullOrEmpty().not()) {
                if (view is ImageSelector) {
                    val imageList: ArrayList<AddedPhotoModel?> = arrayListOf()
                    imageList.add(
                        AddedPhotoModel(
                            imageId = data.logoImage,
                            imagePath = data.logoImageUrl,
                            onEditProduct = true
                        )
                    )
                    view.setPhotoList(imageList)
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_PAYMENT_TYPE]?.let { view ->
            if (view is EditInputLayout) {
                data.paymentTerm?.let { paymentTerm ->
                    try {
                        view.setValue(paymentTerm)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_CREDIT_LIMIT]?.let { view ->
            if (view is EditInputLayout) {
                data.creditLimit?.let { creditLimit ->
                    try {
                        view.setValue("$creditLimit")
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_OUTSTANDING_AMOUNT]?.let { view ->
            if (view is EditInputLayout) {
                data.outstandingAmount?.let { outstandingAmount ->
                    try {
                        view.setValue("$outstandingAmount")
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_SELECT_BEAT]?.let { view ->
            if (view is AssignBeatDropDown) {
                data.beats.let { beats ->
                    try {
                        view.setAlreadyAddedBead(beats)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_SELECT_PRICING_GROUP]?.let { view ->
            if (view is AssignPricingGroupDropDown) {
                data.pricingGroup.let { pricingGroup ->
                    try {
                        data.pricingGroupName?.let { pricingGroupName ->
                            view.setValue(pricingGroupName, pricingGroup.toString().trim())
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

    }

    fun setDataForLeadToCustomerConversion(data: LeadLisDataItem, viewMap: HashMap<String, View>) {

        viewMap[AppConstant.SECTION_NAME_GST_IN]?.let { view ->
            if (view is GSTorPANEditor) {
                data.gstin?.let { gstin ->
                    try {
                        view.setValue(gstin)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_NAME]?.let { view ->
            if (view is EditInputLayout) {
                data.businessName?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_OWNER_NAME]?.let { view ->
            if (view is EditInputLayout) {
                data.contactPersonName?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_MOBILE]?.let { view ->
            if (view is EditInputLayout) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is WhatsAppNumberEditor) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name, false)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_WHATSAPP_MOBILE]?.let { view ->
            if (view is EditInputLayout) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name)
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is WhatsAppNumberEditor) {
                data.mobile?.let { name ->
                    try {
                        view.setValue(name, false)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_GEO_ADDRESS]?.let { view ->
            if (view is CustomerMapAddressPicker) {
                data.geoAddress?.let { geoAddress ->
                    try {
                        if (data.geoAddress.isNullOrBlank().not()) {
                            view.setValue(
                                address = geoAddress,
                                geoLocationLat = data.geoLocationLat ?: 0.0,
                                geoLocationLong = data.geoLocationLong ?: 0.0
                            )
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let { view ->
            if (view is EditInputLayout) {
                data.addressLine1?.let { addressLine1 ->
                    try {
                        view.setValue(addressLine1)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_2]?.let { view ->
            if (view is EditInputLayout) {
                data.addressLine2?.let { addressLine2 ->
                    try {
                        view.setValue(addressLine2)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
            if (view is PinCodeEditor) {
                data.pincode?.let { pincode ->
                    try {
                        view.setValue(pincode)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_CITY]?.let { view ->
            if (view is EditInputLayout) {
                data.city?.let { city ->
                    try {
                        view.setValue(city)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_STATE]?.let { view ->
            if (view is EditInputLayout) {
                data.state?.let { state ->
                    try {
                        view.setValue(state)
                    } catch (_: Exception) {
                    }
                }
            }
            if (view is StateDropDown) {
                data.state?.let { state ->
                    try {
                        view.setValue(state)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        viewMap[AppConstant.SECTION_NAME_BUSINESS_LOGO_IMAGE]?.let { view ->
            if (data.logoImageUrl.isNullOrEmpty().not()) {
                if (view is ImageSelector) {
                    val imageList: ArrayList<AddedPhotoModel?> = arrayListOf()
                    imageList.add(
                        AddedPhotoModel(
                            imageId = data.imageLogo,
                            imagePath = data.logoImageUrl,
                            onEditProduct = true
                        )
                    )
                    view.setPhotoList(imageList)
                }
            }
        }
    }


    /*********************************************************************************************************************************************************************
     *
     *  																Extended Return Functions
     *
     ********************************************************************************************************************************************************************/


    private fun addViewToParent(parent: ViewGroup, view: ConstraintLayout) {
        when (parent) {
            is ParentCardView -> {
                parent.addChild(view)
            }

            else -> {
                parent.addView(view)
            }
        }
    }


    private fun getKeyValueLayout(
        context: Context,
        key: String?,
        value: String?
    ): ConstraintLayout {
        val binding: ItemViewLabelValueTextBinding =
            ItemViewLabelValueTextBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.tvValue.text = value
        return binding.root
    }


    private fun getImageSelectorView(
        context: Context,
        key: String?,
        value: ArrayList<AddedPhotoModel?>
    ): ConstraintLayout {
        val photoList = arrayListOf<PicMapModel>()
        value.forEach {
            if (it != null && it.imagePath.isNullOrBlank().not()) {
                photoList.add(PicMapModel(it.imageId, it.imagePath))
            }
        }
        val addPhotoListAdapter = LrPhotoListAdapter(photoList) {
            if (photoList.size > 0) {
                val imageListModel = OrgImageListModel()
                val imageViewModelArrayList = ArrayList<ImageViewModel>()
                for (pic in photoList) {
                    val model = ImageViewModel(0, 0, pic.url)
                    imageViewModelArrayList.add(model)
                }
                imageListModel.data = imageViewModelArrayList
                context.startActivity(
                    Intent(context, OrgPhotosViewActivity::class.java).putExtra(
                        AppConstant.PRODUCT_INFO,
                        imageListModel
                    ).putExtra(AppConstant.IMAGE_POSITION, photoList)
                )
            } else {
                Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show()
            }
        }
        val binding: ItemViewLabelValuePhotoBinding =
            ItemViewLabelValuePhotoBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.rvPhotoRecycler.isNestedScrollingEnabled = false
        binding.rvPhotoRecycler.stopNestedScroll()
        binding.rvPhotoRecycler.setHasFixedSize(true)
        binding.rvPhotoRecycler.adapter = addPhotoListAdapter
        return binding.root
    }


    private fun getMapCustomerLevelView(
        context: Context,
        key: String?,
        value: MutableCollection<CustomerData>,
        cornerRadius: Float
    ): ConstraintLayout {
        val photoList = arrayListOf<PhotoLabelModel>()
        value.forEach {
            if (it != null) {
                photoList.add(PhotoLabelModel(id = it.id, url = it.logoImageUrl, label = it.name))
            }
        }
        val addPhotoListAdapter = ImageWithPhotoValueAdapter(photoList, cornerRadius, true)
        val binding: ItemViewLabelRecyclerVerticalBinding =
            ItemViewLabelRecyclerVerticalBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.rvPhotoRecycler.isNestedScrollingEnabled = false
        binding.rvPhotoRecycler.stopNestedScroll()
        binding.rvPhotoRecycler.setHasFixedSize(true)
        binding.rvPhotoRecycler.adapter = addPhotoListAdapter
        return binding.root
    }


    private fun getStaffListView(
        context: Context,
        key: String?,
        value: MutableCollection<NameAndIdSetInfoModel>,
        cornerRadius: Float
    ): ConstraintLayout {
        val photoList = arrayListOf<PhotoLabelModel>()
        value.forEach {
            if (it != null) {
                photoList.add(PhotoLabelModel(id = it.id, url = null, label = it.name))
            }
        }
        val addPhotoListAdapter = ImageWithPhotoValueAdapter(photoList, cornerRadius, true)
        val binding: ItemViewLabelRecyclerVerticalBinding =
            ItemViewLabelRecyclerVerticalBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.rvPhotoRecycler.isNestedScrollingEnabled = false
        binding.rvPhotoRecycler.stopNestedScroll()
        binding.rvPhotoRecycler.setHasFixedSize(true)
        binding.rvPhotoRecycler.adapter = addPhotoListAdapter
        return binding.root
    }


    private fun getOrgBeatView(
        context: Context,
        key: String?,
        value: MutableCollection<OrgBeatModel>,
        cornerRadius: Float
    ): ConstraintLayout {
        val photoList = arrayListOf<PhotoLabelModel>()
        value.forEach {
            if (it != null) {
                photoList.add(PhotoLabelModel(id = it.id, url = null, label = it.name))
            }
        }
        val addPhotoListAdapter = ImageWithPhotoValueAdapter(photoList, cornerRadius, false)
        val binding: ItemViewLabelRecyclerVerticalBinding =
            ItemViewLabelRecyclerVerticalBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.rvPhotoRecycler.isNestedScrollingEnabled = false
        binding.rvPhotoRecycler.stopNestedScroll()
        binding.rvPhotoRecycler.setHasFixedSize(true)
        binding.rvPhotoRecycler.adapter = addPhotoListAdapter
        return binding.root
    }


    private fun getAllCategoryResponseView(
        context: Context,
        key: String?,
        value: MutableCollection<AllCategoryResponseModel>,
        cornerRadius: Float
    ): ConstraintLayout {
        val photoList = arrayListOf<PhotoLabelModel>()
        value.forEach {
            if (it != null) {
                photoList.add(PhotoLabelModel(id = it.id, url = null, label = it.name))
            }
        }
        val addPhotoListAdapter = ImageWithPhotoValueAdapter(photoList, cornerRadius, false)
        val binding: ItemViewLabelRecyclerVerticalBinding =
            ItemViewLabelRecyclerVerticalBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvKey.text = buildString {
            append(key)
            append(" : ")
        }
        binding.rvPhotoRecycler.isNestedScrollingEnabled = false
        binding.rvPhotoRecycler.stopNestedScroll()
        binding.rvPhotoRecycler.setHasFixedSize(true)
        binding.rvPhotoRecycler.adapter = addPhotoListAdapter
        return binding.root
    }


    fun getLabel(hasFocus: Boolean, formFields: FormItemsItem?): CharSequence? {
        return if (hasFocus) {
            if (formFields?.fieldProps?.required == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(
                        "<span>${formFields?.fieldProps?.label}</span><span style=\"color:red\"> *</span>",
                        Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    @Suppress("DEPRECATION") Html.fromHtml("<span>${formFields?.fieldProps?.label}</span><span style=\"color:red\"> *</span>")
                }
            } else {
                formFields?.fieldProps?.label
            }
        } else {
            if (formFields?.inputProps?.placeholder == null) {
                if (formFields?.fieldProps?.required == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(
                            "<span>${formFields?.fieldProps?.label}</span><span style=\"color:red\"> *</span>",
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        @Suppress("DEPRECATION") Html.fromHtml("<span>${formFields?.fieldProps?.label}</span><span style=\"color:red\"> *</span>")
                    }
                } else {
                    formFields?.fieldProps?.label
                }
            } else {
                if (formFields.fieldProps?.required == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(
                            "<span>${formFields?.inputProps?.placeholder}</span><span style=\"color:red\"> *</span>",
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    } else {
                        @Suppress("DEPRECATION") Html.fromHtml("<span>${formFields?.inputProps?.placeholder}</span><span style=\"color:red\"> *</span>")
                    }
                } else {
                    formFields?.inputProps?.placeholder
                }
            }
        }
    }


}
