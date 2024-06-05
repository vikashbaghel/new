package com.app.rupyz.sales.customforms

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import com.app.rupyz.R
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel

// Implement specific handlers for each form item type
class TextDescriptionHandler : FormItemHandler {

    private val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun handleViewFormItem(context: Context, formItem: NameAndValueSetInfoModel,
                                    binding: FormBinding, supportFragmentManager: FragmentManager) {
        super.handleViewFormItem(context, formItem, binding, supportFragmentManager)
        inputParams.setMargins(0, 10, 0, 0)


        val textView = TextView(context)
        textView.text = formItem.value
        textView.setTextSize(TypedValue.TYPE_NULL, context.resources.getDimension(R.dimen.size_14sp))
        textView.setTextColor(context.resources.getColor(R.color.black))
        // Load the font from resources and set it to the TextView
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        textView.typeface = typeface
        textView.layoutParams = inputParams

        binding.formLayout.addView(textView)
    }
}