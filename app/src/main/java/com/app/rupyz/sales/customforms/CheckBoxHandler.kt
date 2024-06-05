package com.app.rupyz.sales.customforms

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import io.sentry.protocol.App

// Implement specific handlers for each form item type
class CheckBoxHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    // Create a list to store the checked options
    private val checkedOptions = mutableListOf<String>()

    override fun handleCreationFormItem(context: Context, formItem: FormItemsItem, binding: FormBinding,
                                        formItemModels: MutableList<NameAndValueSetInfoModel>,
                                        supportFragmentManager: FragmentManager) {
        inputParams.setMargins(50, 30, 50, 0)

        val inputProps = formItem.inputProps ?: return

        // Create a rating view (you can replace this with your custom rating view or library)

        inputProps.options?.forEach { option ->
            val checkBox = CheckBox(context)
            checkBox.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_14sp))
            checkBox.setTextColor(context.resources.getColor(R.color.black))
            // Load the font from resources and set it to the TextView
            val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            checkBox.typeface = typeface

            checkBox.layoutParams = inputParams
            checkBox.text = option.label
            // Set layout parameters to wrap content
            binding.formLayout.addView(checkBox)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // If the CheckBox is checked, add its label to the checkedOptions list
                    checkedOptions.add(option.label!!)
                } else {
                    // If the CheckBox is unchecked, remove its label from the checkedOptions list
                    checkedOptions.remove(option.label)
                }

                // Here you can send the updated checkedOptions list
                // For example, you can call a function to handle the checked list
                handleCheckedOptions(checkedOptions, formItem, formItemModels)
            }
        }

    }

    // Function to handle the checked list
    private fun handleCheckedOptions(checkedOptions: List<String>, formItem: FormItemsItem,
                                     formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Create a model for the rating form item and add it to the list

        val stringBuilder = StringBuilder()
        checkedOptions.forEachIndexed { index, opt ->
            stringBuilder.append(opt)
            if (index != checkedOptions.size - 1) {
                stringBuilder.append(",")
            }
        }


        val existingModel = formItemModels.find { it.name == formItem.fieldProps?.name }

        if (existingModel != null) {
            // Update the existing model's value
            existingModel.value = stringBuilder.toString()
        }
    }
}