package com.app.rupyz.sales.customforms

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

class DropDownHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleCreationFormItem(context: Context, formItem: FormItemsItem, binding: FormBinding,
                                        formItemModels: MutableList<NameAndValueSetInfoModel>,
                                        supportFragmentManager: FragmentManager) {
        inputParams.setMargins(50, 30, 50, 0)

        val inputProps = formItem.inputProps ?: return

        val customList = ArrayList<String>()
        inputProps.options?.forEach {
            customList.add(it.label ?: "")
        }


        val spinner = Spinner(context)
        // Set the dropdown mode to show the drop-down icon
        spinner.dropDownVerticalOffset = 0
        spinner.setBackgroundResource(R.drawable.bg_spinner) // Customize spinner background if needed

        spinner.setPadding(10, 5, 10, 5)
        spinner.layoutParams = inputParams
        spinner.tag = formItem.fieldProps?.name
        binding.formLayout.addView(spinner)

        spinner.adapter = ArrayAdapter(
                context, R.layout.single_text_view_spinner_16dp_text,
                customList
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // When an item is selected, update the corresponding entry in dropdownValues
                updateModelValue(formItem.fieldProps?.name,
                        parent?.getItemAtPosition(position).toString(), formItemModels)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected if needed
            }
        }

        formItemModels.firstOrNull { it.name == formItem.fieldProps?.name }?.let {
            if (it.value.isNullOrBlank().not()){
                customList.indexOfFirst { cst -> cst == it.value }.let { index ->
                    if (index != -1){
                        spinner.setSelection(index)
                        updateModelValue(formItem.fieldProps?.name, it.value, formItemModels)
                    }
                }
            }
        }
    }

    private fun updateModelValue(fieldName: String?, value: String?,
                                 formItemModels: MutableList<NameAndValueSetInfoModel>) {
        // Find the model associated with the field name and update its value
        val model = formItemModels.find { it.name == fieldName }
        model?.value = value
    }
}