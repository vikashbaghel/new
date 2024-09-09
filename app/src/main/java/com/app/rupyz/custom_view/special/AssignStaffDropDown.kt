package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemViewDropDownCheckboxViewBinding
import com.app.rupyz.databinding.ItemViewSearchAndRecyclerBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.asBitmap
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.helper.subtractBy
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.customer.CustomerViewModel
import com.google.gson.Gson


class AssignStaffDropDown : LinearLayoutCompat {

    private var binding: ItemViewSearchAndRecyclerBinding =
        ItemViewSearchAndRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputLayout: EditInputLayout = binding.etValue
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private val itemAdapter: StaffAdapter = StaffAdapter()
    private val customerViewModel: CustomerViewModel =
        ViewModelProvider(context as FragmentActivity)[CustomerViewModel::class.java]
    private var assignStaffHeaders: String? = "selected=true&page_no=1"
    private var defaultHeader: String? = "selected=true&page_no=1"
    private var customerId: Int = -1
    private var staffList: MutableList<NameAndIdSetInfoModel> = mutableListOf()
    private var selectedStaffList: HashMap<Int, NameAndIdSetInfoModel> = hashMapOf()
    private val linearLayoutManager: LinearLayoutManager =
        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private var isApiLastPage: Boolean = false
    private var isPageLoading: Boolean = false
    private var formItemType: FormItemType? = null
    private var addStaffSetList: ArrayList<NameAndIdSetInfoModel?> = arrayListOf()
    private var removeStaffSetList: ArrayList<NameAndIdSetInfoModel?> = arrayListOf()


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
            inputLayout.typeface = typeface
            inputLayout.editText?.typeface = typeface
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextColor() {
        try {
            inputLayout.editText?.setTextColor(resources.getColor(R.color.color_000000, null))
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

    fun setCustomerId(customerId: Int) {
        this.customerId = customerId
        setDefaultStyle()
    }

    fun getSelectedStaff(): HashMap<Int, NameAndIdSetInfoModel> {
        return selectedStaffList
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {

        formFields?.let { inputLayout.editText?.setEditTextType(FormItemType.DROPDOWN, it) }
        inputLayout.isEditable = false
        if (showDropDownArrow) {
            inputLayout.editText?.setCompoundDrawablesWithIntrinsicBounds(
                null, null, resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null), null
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
                @Suppress("DEPRECATION") Html.fromHtml("<span>${formFields?.inputProps?.placeholder}</span><span style=\"color:red\"> *</span>")
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
        binding.rvItemRecycler.layoutManager = linearLayoutManager
        binding.rvItemRecycler.adapter = itemAdapter
        binding.etSearchField.addDelayedTextChangeListener(500) {
            if (customerId != -1) {
                assignStaffHeaders = defaultHeader
                loadStaff()
            }
        }
        if (customerId != -1) {
            loadStaff()
        }
        setPaginationHandler()
    }

    private fun setPaginationHandler() {
        binding.rvItemRecycler.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }


    private fun showDropDown() {
        binding.cvOptionCard.showView()
    }

    private fun hideDropDown() {
        binding.cvOptionCard.hideView()
    }

    fun setDropDown(formFieldsData: FormItemsItem, customerId: Int) {
        formFields = formFieldsData
        this.customerId = customerId
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
        model.value = Gson().toJson(getValue()).toString()
        return model
    }

    private fun getValue(): UpdateMappingModel {
        addStaffSetList.clear()
        for ((key, value) in selectedStaffList) {
            if (value.isSelected.not()) {
                addStaffSetList.add(value)
            }
        }
        val addList = ArrayList(addStaffSetList.map { it?.id })
        val removeList = ArrayList(removeStaffSetList.map { it?.id })
        return UpdateMappingModel(
            addSet = addList, removeSet = removeList, allowAll = false, disallowAll = false
        )
    }


    private fun loadStaff() {
        if (assignStaffHeaders == defaultHeader) {
            isApiLastPage = false
            binding.progressBar.showView()
        }
        customerViewModel.getStaffListWithCustomerMapping(
            customerId,
            binding.etSearchField.text.toString(),
            assignStaffHeaders,
            hasInternetConnection(context)
        )
    }

    private fun observeCustomerList() {
        customerViewModel.staffListWithCustomerMappingLiveDataWith.observe((context as FragmentActivity)) {
            binding.progressBar.hideView()
            isPageLoading = false
            if (it.data.isNullOrEmpty().not()) {
                isApiLastPage = false
                if (assignStaffHeaders == defaultHeader) {
                    staffList.clear()
                    it.data?.let { it1 -> staffList.addAll(it1) }
                    upDateStaffList(true, staffList)
                } else {
                    it.data?.let { it1 -> staffList.addAll(it1) }
                    upDateStaffList(assignStaffHeaders == defaultHeader, it.data)
                }
            } else {
                isApiLastPage = true
                if (assignStaffHeaders.equals(defaultHeader)){
                    staffList.clear()
                    upDateStaffList(true, mutableListOf())
                }
            }
            it.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty().not()) {
                    assignStaffHeaders = headers.nextParams
                    if (it.data.isNullOrEmpty()) {
                        loadStaff()
                    }
                }
            }
        }
    }

    private fun upDateStaffList(isFirstPage: Boolean, data: List<NameAndIdSetInfoModel>?) {
        if (isFirstPage) {
            itemAdapter.setStaffList(staffList)
        } else {
            data?.toMutableList()?.let { itemAdapter.addStaff(it) }
        }
    }

    private inner class StaffAdapter : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

        private val staffList: MutableList<NameAndIdSetInfoModel> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
            return StaffViewHolder(
                ItemViewDropDownCheckboxViewBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return staffList.size
        }

        override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
            holder.bind(position, staffList[position])
        }

        private inner class StaffViewHolder(itemView: ItemViewDropDownCheckboxViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val itemViewBinding = itemView
            fun bind(position: Int, staffDetails: NameAndIdSetInfoModel) {
                try {
                    itemViewBinding.staffProfilePic.setImageBitmap(
                        StringHelper.printName(
                            staffDetails.name
                        ).trim().substring(0, (Math.min(staffDetails.name?.length?:0, 2))).uppercase().asBitmap(
                            context, 16f, Color.WHITE, resources.getColor(R.color.theme_color, null)
                        )
                    )
                } catch (e: Exception) {
                    log(e.toString())
                }
                itemViewBinding.tvStaffName.text = staffDetails.name
                itemViewBinding.mcbCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (staffDetails.isSelected.not()) {
                            selectedStaffList[staffDetails.id ?: 0] = staffDetails
                        }
                        if (staffDetails.isSelected) {
                            removeStaffSetList.remove(staffDetails)
                            removeStaffSetList.removeAll { it?.id == staffDetails.id }
                        }
                    } else {
                        if (staffDetails.isSelected) {
                            removeStaffSetList.add(staffDetails)
                        }
                        selectedStaffList.remove(staffDetails.id)
                    }
                }
                itemViewBinding.parent.setOnClickListener {
                    itemViewBinding.mcbCheckBox.isChecked =
                        itemViewBinding.mcbCheckBox.isChecked.not()
                }
                if (position == staffList.size - 1) {
                    itemViewBinding.divider.hideView()
                } else {
                    itemViewBinding.divider.showView()
                }

                if (staffDetails.isSelected && removeStaffSetList.firstOrNull { it?.id == staffDetails.id } == null) {
                    itemViewBinding.mcbCheckBox.isChecked = true
                } else {
                    itemViewBinding.mcbCheckBox.isChecked =
                        selectedStaffList.contains(staffDetails.id)
                }

            }
        }


        override fun getItemViewType(position: Int): Int {
            return position
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setStaffList(list: MutableList<NameAndIdSetInfoModel>) {
            staffList.clear()
            staffList.addAll(list)
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun addStaff(list: MutableList<NameAndIdSetInfoModel>) {
            if (staffList.isEmpty()){
                staffList.clear()
                staffList.addAll(list)
                notifyDataSetChanged()
            }else{
                staffList.addAll(list)
                val subList  = list.subtractBy(staffList){ it.id }
                staffList.addAll(subList)
                notifyItemRangeInserted((staffList.size - subList.size), subList.size)
                
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
