package com.app.rupyz.sales.customforms

import android.content.Context
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.app.rupyz.generic.helper.log
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

object FormItemHandlerFactory {
    fun getFormCreationHandler(formItemType: FormItemType): FormItemHandler {
        return when (formItemType) {
            FormItemType.SHORT_ANSWER,
            FormItemType.ALPHABETS,
            FormItemType.NUMBERS,
            FormItemType.LONG_ANSWER,
            FormItemType.EMAIL_ADDRESS,
            FormItemType.MOBILE_NUMBER,
            FormItemType.URL_INPUT -> InputTextHandler(formItemType.name)

            FormItemType.DATE_TIME_PICKER -> DateTimePickerHandler()
            FormItemType.DATE_PICKER -> DatePickerHandler()

            FormItemType.DROPDOWN -> DropDownHandler()
            FormItemType.FILE_UPLOAD -> ImageUploadHandler()
            FormItemType.RATING -> RatingStarHandler()
            FormItemType.CHECKBOX -> CheckBoxHandler()
            FormItemType.MULTIPLE_CHOICE -> RadioButtonHandler()

            // Add cases for other form item types
            else -> {
                // Log the unsupported type
                log("Unsupported form item type: $formItemType")
                // Return a default handler or throw an exception
                DefaultHandler()
            }
        }
    }

    fun getFormViewHandler(formItemType: FormItemType): FormItemHandler {
        return when (formItemType) {
            FormItemType.SHORT_ANSWER,
            FormItemType.ALPHABETS,
            FormItemType.NUMBERS,
            FormItemType.LONG_ANSWER,
            FormItemType.EMAIL_ADDRESS,
            FormItemType.MOBILE_NUMBER -> TextDescriptionHandler()

            FormItemType.FILE_UPLOAD -> ImageViewHandler()

            FormItemType.RATING -> RatingStarViewHandler()

            FormItemType.DATE_TIME_PICKER,
            FormItemType.DATE_PICKER,
            FormItemType.CHECKBOX,
            FormItemType.DROPDOWN,
            FormItemType.URL_INPUT,
            FormItemType.MULTIPLE_CHOICE -> CustomTextDetailHandler(formItemType.name)

            // Add cases for other form item types
            else -> {
                // Log the unsupported type
                log("Unsupported form item type: $formItemType")
                // Return a default handler or throw an exception
                DefaultHandler()
            }
        }
    }
}

// Define an enum for form item types
enum class FormItemType {
    SHORT_ANSWER,
    LONG_ANSWER,
    DATE_PICKER,
    DATE_TIME_PICKER,
    DROPDOWN,
    FILE_UPLOAD,
    MOBILE_NUMBER,
    EMAIL_ADDRESS,
    ALPHABETS,
    NUMBERS,
    MULTIPLE_CHOICE,
    CHECKBOX,
    URL_INPUT,
    RATING
}

// Define a base interface for form item handlers
interface FormItemHandler {
    fun handleCreationFormItem(context: Context, formItem: FormItemsItem, binding: FormBinding,
                               formItemModels: MutableList<NameAndValueSetInfoModel>,
                               supportFragmentManager: FragmentManager) {
    }

    fun handleViewFormItem(context: Context, formItem: NameAndValueSetInfoModel,
                           binding: FormBinding,
                           supportFragmentManager: FragmentManager) {
    }
}

// Define a class to hold the form item and binding information
class FormBinding(val formLayout: LinearLayout)
