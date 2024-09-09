package com.app.rupyz.sales.customforms

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel


// Implement specific handlers for each form item type
class InputTextHandler(private val type: String) : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleCreationFormItem(
        context: Context,
        formItem: FormItemsItem,
        binding: FormBinding,
        formItemModels: MutableList<NameAndValueSetInfoModel>,
        supportFragmentManager: FragmentManager,
        picsUrls: ArrayList<PicMapModel>
    ) {
        inputParams.setMargins(50, 30, 50, 0)

        val editText = EditText(context)
        // Customize editText as per SHORT_ANSWER type

        editText.setPadding(32, 25, 32, 25)
        editText.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_16sp))
        editText.setTextColor(context.resources.getColor(R.color.black))
        // Load the font from resources and set it to the TextView
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        editText.typeface = typeface
        editText.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
        editText.layoutParams = inputParams
        editText.hint = formItem.inputProps?.placeholder
        binding.formLayout.addView(editText)
        editText.tag = formItem.fieldProps?.name

        when (type) {
            FormItemType.SHORT_ANSWER.name -> {
                editText.minLines = 1
                editText.maxLines = 3
                editText.gravity = Gravity.TOP
                editText.filters = arrayOf(InputFilter.LengthFilter(
                        formItem.inputProps?.maxLength ?: 200))
            }

            FormItemType.LONG_ANSWER.name -> {
                editText.minLines = 3
                editText.maxLines = 5
                editText.gravity = Gravity.TOP
                editText.filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength
                        ?: 1250))
            }

            FormItemType.MOBILE_NUMBER.name -> {
                editText.minLines = 1
                editText.maxLines = 1
                editText.gravity = Gravity.TOP
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf(InputFilter.LengthFilter(10))
            }

            FormItemType.EMAIL_ADDRESS.name -> {
                editText.minLines = 1
                editText.maxLines = 1
                editText.gravity = Gravity.TOP
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                editText.filters = arrayOf(InputFilter.LengthFilter(50))
            }

            FormItemType.ALPHABETS.name -> {
                editText.minLines = 1
                editText.maxLines = 1
                editText.gravity = Gravity.TOP
                editText.setAlphabeticWithSpaceAndMaxLength(formItem.inputProps?.maxLength
                        ?: 250)
            }

            FormItemType.NUMBERS.name -> {
                editText.minLines = 1
                editText.maxLines = 1
                editText.gravity = Gravity.TOP
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength
                        ?: 1250))
            }

            FormItemType.URL_INPUT.name -> {
                editText.maxLines = 1
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                editText.gravity = Gravity.TOP
                editText.filters = arrayOf(InputFilter.LengthFilter(formItem.inputProps?.maxLength
                        ?: 1250))
            }
        }


        // Check if the editText already has a parent
        val parent = editText.parent
        if (parent != null && parent is ViewGroup) {
            // Remove the editText from its current parent
            parent.removeView(editText)
        }

        binding.formLayout.addView(editText)
        editText.tag = formItem.fieldProps?.name

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the corresponding model's value when text changes
                // This part may need adjustments based on your model structure
                updateModelValue(formItem.fieldProps?.name, s?.toString(), formItemModels)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.value.isNullOrBlank().not()){
                editText.setText(it.value)
                updateModelValue(formItem.fieldProps?.name, it.value, formItemModels)
            }
        }

    }

    private fun updateModelValue(fieldName: String?, value: String?,
                                 formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Find the model associated with the field name and update its value
        val model = formItemModels.find { it.name == fieldName }
        model?.value = value
    }

    private fun EditText.setAlphabeticWithSpaceAndMaxLength(maxLength: Int) {
        val alphabeticWithSpaceFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val builder = StringBuilder()
            for (i in start until end) {
                val char = source[i]
                if (char.isLetter() || char.isWhitespace()) {
                    builder.append(char)
                }
            }
            builder.toString()
        }

        val lengthFilter = InputFilter.LengthFilter(maxLength)

        // Apply both filters to the EditText
        this.filters = arrayOf(alphabeticWithSpaceFilter, lengthFilter)
    }
}