package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemViewDropDownTextViewBinding
import com.app.rupyz.databinding.ItemViewSearchAndRecyclerBinding
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel


class StateDropDown : LinearLayoutCompat {

    private var binding: ItemViewSearchAndRecyclerBinding =
        ItemViewSearchAndRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputLayout: EditInputLayout = binding.etValue
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private val itemAdapter: StateAdapter = StateAdapter()
    private var formItemType: FormItemType? = null


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
        val a = context.obtainStyledAttributes(attrs, R.styleable.DropDown)
        showDropDownArrow = a.getBoolean(R.styleable.DropDown_showDropDownArrow, true)
        inputLayout.isEditable = false
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
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
            inputLayout.editText?.setHintTextColor(resources.getColor(R.color.color_727176, null))
            inputLayout.hintTextColor =
                ColorStateList.valueOf(resources.getColor(R.color.color_727176, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setValue(name: String?) {
        inputLayout.editText?.setText(name)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {

        formFields?.let { inputLayout.editText?.setEditTextType(FormItemType.DROPDOWN, it) }
        inputLayout.isEditable = false
        if (showDropDownArrow) {
            inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
        }

        inputLayout.editText?.setOnClickListener {
            if (binding.cvOptionCard.visibility == View.VISIBLE) {
                hideDropDown()
                if (showDropDownArrow) {
                    inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                        null
                    )
                }
            } else {
                showDropDown()
                if (showDropDownArrow) {
                    inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.ic_arrow_drop_down_inverted_black, null),
                        null
                    )
                }
            }
        }

        inputLayout.hint = if (formFields?.fieldProps?.required == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    "<span>${formFields?.inputProps?.placeholder}</span><span style=\"color:red\"> *</span>",
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml("<span>${formFields?.inputProps?.placeholder}</span><span style=\"color:red\"> *</span>")
            }
        } else {
            formFields?.inputProps?.placeholder
        }
        setSpinnerItems()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSpinnerItems() {
        binding.etSearchField.clearFocus()
        binding.rvItemRecycler.setHasFixedSize(true)
        binding.rvItemRecycler.adapter = itemAdapter
        itemAdapter.setOnItemSelectListener { _, stateName ->
            if (showDropDownArrow) {
                inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                    null
                )
            }
            inputLayout.editText?.setText(stateName)
            hideDropDown()
        }
        binding.etSearchField.addDelayedTextChangeListener(100) {
            it?.let { itemAdapter.filterStates(it.toString()) }
        }
    }

    private fun showDropDown() {
        binding.cvOptionCard.showView()
    }

    private fun hideDropDown() {
        binding.cvOptionCard.hideView()
    }

    fun setDropDown(formFieldsData: FormItemsItem) {
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

    private inner class StateAdapter : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

        private val originalStates = resources.getStringArray(R.array.statesList).toList()
        private val states = resources.getStringArray(R.array.statesList).toMutableList()
        private lateinit var onItemSelectListener: (Int, String) -> Unit

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
            return StateViewHolder(
                ItemViewDropDownTextViewBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return states.size
        }

        override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
            holder.bind(position, states[position].toString())
        }

        private inner class StateViewHolder(itemView: ItemViewDropDownTextViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val itemViewBinding = itemView
            fun bind(position: Int, stateName: String) {
                itemViewBinding.tvName.text = stateName
                itemViewBinding.root.setOnClickListener {
                    if (::onItemSelectListener.isInitialized) {
                        onItemSelectListener(adapterPosition, stateName)
                    }
                }
                if (position == states.size - 1) {
                    itemViewBinding.divider.hideView()
                } else {
                    itemViewBinding.divider.showView()
                }
            }
        }

        fun setOnItemSelectListener(onItemSelectListener: (Int, String) -> Unit) {
            this.onItemSelectListener = onItemSelectListener
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        fun getFirstItem(): String? {
            return if (states.isNotEmpty()) {
                states[0]
            } else {
                null
            }
        }


        @SuppressLint("NotifyDataSetChanged")
        fun filterStates(searchText: String?) {
            if (searchText.isNullOrBlank()) {
                originalStates.let { states.clear(); states.addAll(it) }
                notifyDataSetChanged()
            } else {
                originalStates.filter { it.contains(searchText, true) }
                    .let { states.clear(); states.addAll(it) }
                notifyDataSetChanged()
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

}
