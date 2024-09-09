package com.app.rupyz.sales.customforms

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

// Implement specific handlers for each form item type
class RadioButtonHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private val keyValueMap = hashMapOf<String,Int>()

    override fun handleCreationFormItem(
        context: Context,
        formItem: FormItemsItem,
        binding: FormBinding,
        formItemModels: MutableList<NameAndValueSetInfoModel>,
        supportFragmentManager: FragmentManager,
        picsUrls: ArrayList<PicMapModel>
    ) {
        inputParams.setMargins(50, 30, 10, 0)

        val radioGroup = RadioGroup(context)
        radioGroup.layoutParams = inputParams
        radioGroup.orientation = VERTICAL
        binding.formLayout.addView(radioGroup)

        val inputProps = formItem.inputProps ?: return

        // Create a rating view (you can replace this with your custom rating view or library)

        val childInputParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        childInputParams.setMargins(0, 10, 30, 0)

        inputProps.options?.forEach { option ->
            val radioButton = RadioButton(context)
            radioButton.text = option.label
            radioButton.layoutParams = childInputParams
            radioButton.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_14sp))
            radioButton.setTextColor(context.resources.getColor(R.color.black))
            radioButton.typeface = Typeface.create("poppins_regular", Typeface.NORMAL)

            // Set a unique id for each radio button
            radioButton.id = View.generateViewId()

            // Add the radio button to the RadioGroup
            radioGroup.addView(radioButton)

            // Set the OnCheckedChangeListener for each radio button
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Here you can send the updated checkedOptions list
                    handleCheckedOptions(option.label!!, formItem, formItemModels)
                }
            }

            option.label?.let { keyValueMap[it] = radioButton.id }
        }

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.value.isNullOrBlank().not()){
                keyValueMap[it.value]?.let { id ->
                    radioGroup.check(id)
                    it.value?.let { it1 -> handleCheckedOptions(it1, formItem, formItemModels) }
                }
            }
        }

    }

    // Function to handle the checked list
    private fun handleCheckedOptions(checkedOptions: String, formItem: FormItemsItem,
                                     formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Create a model for the rating form item and add it to the list
        val existingModel = formItemModels.find { it.name == formItem.fieldProps?.name }

        if (existingModel != null) {
            // Update the existing model's value
            existingModel.value = checkedOptions
        }
    }
}