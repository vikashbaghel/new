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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
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
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.ui.more.MoreViewModel
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM


class AssignPricingGroupDropDown : LinearLayoutCompat {

    private var binding: ItemViewSearchAndRecyclerBinding =
        ItemViewSearchAndRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputLayout: EditInputLayout = binding.etValue
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private val itemAdapter: BeatAdapter = BeatAdapter()
    private val moreViewModel: MoreViewModel =
        ViewModelProvider(context as FragmentActivity)[MoreViewModel::class.java]
    private var assignPricingGroupHeaders: String? = "selected=true&page_no=1"
    private var defaultHeader: String? = "selected=true&page_no=1"
    private var pricingGroupList: MutableList<NameAndIdSetInfoModel> = mutableListOf()
    private var selectedPricingGroup: NameAndIdSetInfoModel? = null
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
        observeCustomerList()
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

    fun setValue(name: String?, pricingGroupId: String?) {
        inputLayout.editText?.setText(name)
        try {
            selectedPricingGroup = NameAndIdSetInfoModel(name = name, id = pricingGroupId?.toInt())
        } catch (_: Exception) {
        }
    }


    fun getSelectedPriceGroup(): NameAndIdSetInfoModel? {
        return selectedPricingGroup
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
        itemAdapter.setOnItemSelectListener { _, priceData ->
            if (showDropDownArrow) {
                inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                    null
                )
            }
            selectedPricingGroup = priceData
            inputLayout.editText?.setText(priceData.name)
            inputLayout?.isEndIconVisible = true
            hideDropDown()
        }
        binding.etSearchField.addDelayedTextChangeListener(500) {
            it?.let { itemAdapter.filterPriceGroups(it.toString()) }
        }
        loadPricingGroupList()
        
        inputLayout?.setEndIconOnClickListener {
            inputLayout?.editText?.text = null
            inputLayout?.isEndIconVisible = false
            if (showDropDownArrow) {
                inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
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

    private fun showDropDown() {
        binding.cvOptionCard.showView()
    }

    private fun hideDropDown() {
        binding.cvOptionCard.hideView()
    }

    fun setDropDown(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setTypeFace(context)
        setTextColor()
        setHintColor()
        setDefaultStyle()
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
        model.value = if (selectedPricingGroup?.id == null){ null } else { selectedPricingGroup?.id.toString() }
        return model
    }

    private fun loadPricingGroupList() {
        if (assignPricingGroupHeaders == defaultHeader) {
            pricingGroupList.clear()
            binding.progressBar.showView()
        }
        moreViewModel.getPricingGroupList()
    }

    private fun observeCustomerList() {
        moreViewModel.pricingGroupLiveData.observe((context as FragmentActivity)) {
            if (it.error == false) {
                binding.progressBar.hideView()
                if (it.data.isNullOrEmpty().not()) {
                    pricingGroupList.clear()
                    it.data?.let { it1 -> pricingGroupList.addAll(it1) }
                    itemAdapter.setPricingGroupList(pricingGroupList)
                }
            }
        }
    }


    private inner class BeatAdapter : RecyclerView.Adapter<BeatAdapter.BeatViewHolder>() {

        private val pgList: MutableList<NameAndIdSetInfoModel> = mutableListOf()
        private lateinit var onItemSelectListener: (Int, NameAndIdSetInfoModel) -> Unit

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
            return BeatViewHolder(
                ItemViewDropDownTextViewBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return pgList.size
        }

        override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
            holder.bind(position, pgList[position])
        }

        private inner class BeatViewHolder(itemView: ItemViewDropDownTextViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val itemViewBinding = itemView
            fun bind(position: Int, beadDetails: NameAndIdSetInfoModel) {
                itemViewBinding.tvName.text = beadDetails.name
                itemViewBinding.root.setOnClickListener {
                    if (::onItemSelectListener.isInitialized) {
                        onItemSelectListener(adapterPosition, beadDetails)
                    }
                }
                if (position == pricingGroupList.size - 1) {
                    itemViewBinding.divider.hideView()
                } else {
                    itemViewBinding.divider.showView()
                }
            }
        }


        override fun getItemViewType(position: Int): Int {
            return position
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setPricingGroupList(list: MutableList<NameAndIdSetInfoModel>) {
            pgList.clear()
            pgList.addAll(list)
            notifyDataSetChanged()
        }

        fun setOnItemSelectListener(onItemSelectListener: (Int, NameAndIdSetInfoModel) -> Unit) {
            this.onItemSelectListener = onItemSelectListener
        }


        @SuppressLint("NotifyDataSetChanged")
        fun filterPriceGroups(searchText: String?) {
            if (searchText.isNullOrBlank()) {
                pricingGroupList.let { pgList.clear(); pgList.addAll(it) }
                notifyDataSetChanged()
            } else {
                pricingGroupList.filter { it.name?.contains(searchText, true) == true }
                    .let { pgList.clear(); pgList.addAll(it) }
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
