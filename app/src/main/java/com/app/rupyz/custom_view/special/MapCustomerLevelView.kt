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
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.type.CustomerLevel
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
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.customer.CustomerViewModel
import com.google.gson.Gson


class MapCustomerLevelView : LinearLayoutCompat {

    private var binding: ItemViewSearchAndRecyclerBinding =
        ItemViewSearchAndRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputLayout: EditInputLayout = binding.etValue
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private val itemAdapter: CustomerAdapter = CustomerAdapter()
    private val customerViewModel: CustomerViewModel =
        ViewModelProvider(context as FragmentActivity)[CustomerViewModel::class.java]
    private var assignCustomerHeaders: String? = "selected=true&page_no=1"
    private var defaultHeader: String = "selected=true&page_no=1"
    private var customerId: Int = -1

    private var selectedCustomerList: HashMap<Int, CustomerData> = hashMapOf()

    //    private var selectedCustomer: CustomerData? = null
    private val linearLayoutManager: LinearLayoutManager =
        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private var isApiLastPage: Boolean = false
    private var isPageLoading: Boolean = false
    private var selectedStep: CustomerLevel? = CustomerLevel.LEVEL_ONE
    private var addCustomerSetList: ArrayList<CustomerData?> = arrayListOf()
    private var removeCustomerSetList: ArrayList<CustomerData?> = arrayListOf()
    private var formItemType: FormItemType? = null
    private val customerLevelSyncData: MutableMap<CustomerLevel, Triple<String?, MutableList<CustomerData>, Boolean>> =
        mutableMapOf()
    private var _isInEditMode = false

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
        setSpinnerItems()
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


    fun setCustomerId(customerId: Int) {
        this.customerId = customerId
        setDefaultStyle()
    }

    fun getSelectedStaff(): HashMap<Int, CustomerData> {
        return selectedCustomerList
    }

    private fun getValue(): UpdateMappingModel {
        addCustomerSetList.clear()
        for ((key, value) in selectedCustomerList) {
            if (value.isSelected?.not() == true) {
                addCustomerSetList.add(value)
            }
        }
        val addList = ArrayList(addCustomerSetList.map { it?.id })
        val removeList = ArrayList(removeCustomerSetList.map { it?.id })
        return UpdateMappingModel(
            addSet = addList,
            removeSet = removeList,
            allowAll = null,
            disallowAll = null
        )
    }

    fun setIsInEditMode(inEditMode: Boolean) {
        _isInEditMode = inEditMode
    }

    fun getIsInEditMode(): Boolean {
        return _isInEditMode
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {
        customerLevelSyncData.clear()
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSpinnerItems() {
        binding.etSearchField.clearFocus()
        binding.rvItemRecycler.setHasFixedSize(true)
        binding.rvItemRecycler.layoutManager = linearLayoutManager
        binding.rvItemRecycler.adapter = itemAdapter
        binding.etSearchField.addDelayedTextChangeListener(500) {
            if (customerId != -1) {
                assignCustomerHeaders = defaultHeader
                selectedStep?.let { it1 -> loadCustomerLevel(it1, assignCustomerHeaders) }
            }
        }
        if (customerId != -1) {
            selectedStep?.let { it1 -> loadCustomerLevel(it1, assignCustomerHeaders) }
        }
        setPaginationHandler()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    private fun setSpinnerItems(clearList: Boolean, spinnerList: MutableList<CustomerData>) {
        if (clearList) {
            itemAdapter.setCustomerList(spinnerList)
            itemAdapter.notifyDataSetChanged()
        } else {
            itemAdapter.addCustomer(spinnerList)
            itemAdapter.notifyDataSetChanged()
        }
    }

    private fun setPaginationHandler() {
        binding.rvItemRecycler.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                selectedStep?.let { loadCustomerLevel(it, assignCustomerHeaders) }
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

    fun setCustomerTypeView(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        setDefaultStyle()
        setTypeFace(context)
        setTextColor()
        setHintColor()
    }

    fun getFieldValue(): NameAndValueSetInfoModel? {
        return when (selectedStep) {
            CustomerLevel.LEVEL_ONE -> null
            CustomerLevel.LEVEL_TWO, CustomerLevel.LEVEL_THREE -> {
                val model = NameAndValueSetInfoModel()
                model.name = if (BuildConfig.FLAVOR == "dev") {
                    "select_parents"
                } else {
                    formFields?.fieldProps?.name
                }
                model.label = formFields?.fieldProps?.label
                model.isRequired = formFields?.fieldProps?.required
                model.isCustom = formFields?.isCustom
                model.type = formFields?.type
                model.subModuleType = formFields?.type
                model.subModuleId = formFields?.fieldProps?.name
                model.value = Gson().toJson(getValue()).toString()
                model
            }

            null -> null
        }
    }
    
    fun isAllMappedParentLelDeselected(assignedDistributorCount : Int) : Boolean {
        return ((assignedDistributorCount == removeCustomerSetList.size) && selectedCustomerList.isEmpty())
    }


    fun setCustomerLevel(level: CustomerLevel, clearEditMode: Boolean) {
        if (clearEditMode) {
            _isInEditMode = false
        } else {
            _isInEditMode = true
        }
        selectedCustomerList.clear()
        addCustomerSetList.clear()
        removeCustomerSetList.clear()
        if (inputLayout.editText?.text.isNullOrBlank().not()) {
            inputLayout.editText?.text = null
        }
        if (customerLevelSyncData[level] != null && binding.etSearchField.text.isNullOrBlank()) {
            customerLevelSyncData[level]?.apply {
                itemAdapter.setCustomerList(arrayListOf())
                assignCustomerHeaders = first
                selectedStep = level
                isApiLastPage = third
                setSpinnerItems(true, second)
            }
        } else {
            itemAdapter.setCustomerList(arrayListOf())
            assignCustomerHeaders = defaultHeader
            selectedStep = level
            loadCustomerLevel(level, assignCustomerHeaders)
        }
        
        inputLayout?.hint  = getLabel((inputLayout?.editText?.hasFocus()?:false || inputLayout?.editText?.text.isNullOrBlank().not()), formFields,level)
        
    }
    
    fun getLabel(hasFocus : Boolean, formFields : FormItemsItem?, level : CustomerLevel) : CharSequence? {
        return if (hasFocus) {
            if (formFields?.fieldProps?.required == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml("<span>${ getLabelText(level) }</span><span style=\"color:red\"> *</span>", Html.FROM_HTML_MODE_COMPACT)
                } else {
                    @Suppress("DEPRECATION") Html.fromHtml("<span>${getLabelText(level)}</span><span style=\"color:red\"> *</span>")
                }
            } else {
                getLabelText(level)
            }
        } else {
            if (formFields?.inputProps?.placeholder == null) {
                if (formFields?.fieldProps?.required == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml("<span>${getLabelText(level)}</span><span style=\"color:red\"> *</span>", Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        @Suppress("DEPRECATION") Html.fromHtml("<span>${getLabelText(level)}</span><span style=\"color:red\"> *</span>")
                    }
                } else {
                    getLabelText(level)
                }
            } else {
                if (formFields.fieldProps?.required == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml("<span>${getLabelText(level)}</span><span style=\"color:red\"> *</span>", Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        @Suppress("DEPRECATION") Html.fromHtml("<span>${getLabelText(level)}</span><span style=\"color:red\"> *</span>")
                    }
                } else {
                    getLabelText(level)
                }
            }
        }
    }
    
    private  fun getLabelText( level : CustomerLevel) : String{
      return  when(level){
            CustomerLevel.LEVEL_TWO   -> {
                if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty().not()) {
                    buildString {
                        append(resources.getString(R.string.map_level))
                        append(" ")
                        append(SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1))
                    }
                }else{
                    buildString {
                        append(resources.getString(R.string.map_level))
                        append(" ")
                        append(resources.getString(R.string.super_stockist))
                    }
                }
            }
            CustomerLevel.LEVEL_THREE   -> {
                if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty().not()) {
                    buildString {
                        append(resources.getString(R.string.map_level))
                        append(" ")
                        append(SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2))
                    }
                }else{
                    buildString {
                        append(resources.getString(R.string.map_level))
                        append(" ")
                        append(resources.getString(R.string.distributor))
                    }
                }
            }
            CustomerLevel.LEVEL_ONE -> {
                buildString {
                    append(formFields?.fieldProps?.label)
                }
            }
        }
    }

    private fun loadCustomerLevel(customerLevel: CustomerLevel, currentPage: String?) {
        if (customerLevel != CustomerLevel.LEVEL_ONE) {
            this.assignCustomerHeaders = currentPage
            customerViewModel.getCustomerListMapped(
                customerId = customerId,
                name = if (binding.etSearchField.text.isNullOrBlank()) {
                    ""
                } else {
                    binding.etSearchField.text.toString()
                },
                filterCustomerLevel = when (customerLevel) {
                    CustomerLevel.LEVEL_ONE -> AppConstant.CUSTOMER_LEVEL_1
                    CustomerLevel.LEVEL_TWO -> AppConstant.CUSTOMER_LEVEL_2
                    CustomerLevel.LEVEL_THREE -> AppConstant.CUSTOMER_LEVEL_3
                },
                filterCustomerType = ArrayList(),
                ignoreMapping = false,
                sortByOrder = AppConstant.SORTING_LEVEL_ASCENDING,
                header = assignCustomerHeaders ?: defaultHeader,
                hasInternetConnection = hasInternetConnection(context)
            )
            if (defaultHeader == assignCustomerHeaders) {
                binding.progressBar.showView()
            }
        }
    }


    private fun observeCustomerList() {
        customerViewModel.getCustomerListData().observe((context as FragmentActivity)) { data ->
            binding.progressBar.hideView()
            isPageLoading = false
            data.data?.let { customerDataList ->
                if (customerDataList.isNotEmpty()) {
                    selectedStep?.let {
                        if (binding.etSearchField.text.isNullOrBlank().not()) {
                            setSpinnerItems(
                                defaultHeader == assignCustomerHeaders,
                                customerDataList.toMutableList()
                            )
                        } else {
                            val list = customerLevelSyncData[it]?.second ?: arrayListOf()
                            list.addAll(customerDataList)
                            customerLevelSyncData[it] = Triple(assignCustomerHeaders, list, false)
                            setSpinnerItems(assignCustomerHeaders == defaultHeader, list)
                        }

                    }
                }
            }
            data.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty().not()) {
                    assignCustomerHeaders = headers.nextParams
                    if (data.data.isNullOrEmpty()) {
                        loadCustomerLevel(
                            selectedStep ?: CustomerLevel.LEVEL_ONE,
                            assignCustomerHeaders
                        )
                    }
                } else {
                    selectedStep?.let {
                        if (binding.etSearchField.text.isNullOrBlank().not()) {
                            setSpinnerItems(assignCustomerHeaders == defaultHeader, mutableListOf())
                        } else {
                            val list = customerLevelSyncData[it]?.second ?: arrayListOf()
                            customerLevelSyncData.put(it, Triple(assignCustomerHeaders, list, true))
                        }
                    }
                    isApiLastPage = true
                }
            }
        }
    }


    private inner class CustomerAdapter :
        RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

        private val customerList: MutableList<CustomerData> = mutableListOf()
        private val idSet: MutableSet<Int> = mutableSetOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
            val layout = ItemViewDropDownCheckboxViewBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
            return CustomerViewHolder(layout)
        }

        override fun getItemCount(): Int {
            return customerList.size
        }

        override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
            holder.bind(position, customerList[position])
        }

        private inner class CustomerViewHolder(itemView: ItemViewDropDownCheckboxViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val itemViewBinding = itemView
            fun bind(position: Int, customerDetails: CustomerData) {
                try {
                    if (customerDetails.logoImageUrl.isNullOrBlank()) {
                        itemViewBinding.staffProfilePic.setImageBitmap(
                            StringHelper.printName(
                                customerDetails.name
                            ).trim().substring(0, (Math.min(customerDetails.name?.length?:0, 2))).uppercase().asBitmap(
                                context,
                                16f,
                                Color.WHITE,
                                resources.getColor(R.color.theme_color, null)
                            )
                        )
                    } else {
                        ImageUtils.loadImage(
                            customerDetails.logoImageUrl, itemViewBinding.staffProfilePic
                        )
                    }
                } catch (e: Exception) {
                    log(e.toString())
                }
                itemViewBinding.tvStaffName.text = customerDetails.name

                itemViewBinding.mcbCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (customerDetails.isSelected?.not() == true) {
                            selectedCustomerList[customerDetails.id ?: 0] = customerDetails
                        }
                        if (customerDetails.isSelected == true) {
                            removeCustomerSetList.remove(customerDetails)
                            removeCustomerSetList.removeAll { it?.id == customerDetails.id }
                        }
                    } else {
                        if (customerDetails.isSelected == true) {
                            removeCustomerSetList.add(customerDetails)
                        }
                        selectedCustomerList.remove(customerDetails.id)
                    }
                }

                itemViewBinding.parent.setOnClickListener {
                    itemViewBinding.mcbCheckBox.isChecked =
                        itemViewBinding.mcbCheckBox.isChecked.not()
                }


                if (position == customerList.size - 1) {
                    itemViewBinding.divider.hideView()
                } else {
                    itemViewBinding.divider.showView()
                }

                if ((customerDetails.isSelected == true) && (removeCustomerSetList.firstOrNull { it?.id == customerDetails.id } == null)) {
                    itemViewBinding.mcbCheckBox.isChecked = true
                } else {
                    itemViewBinding.mcbCheckBox.isChecked =
                        selectedCustomerList.contains(customerDetails.id)
                }
            }
        }


        override fun getItemViewType(position: Int): Int {
            return position
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setCustomerList(list: MutableList<CustomerData>) {
            customerList.clear()
            idSet.clear()
            customerList.addAll(list)
            idSet.addAll(list.map { it.id ?: 0 })
            notifyDataSetChanged()
        }

        fun addCustomer(list: MutableList<CustomerData>) {
            try {
                val customerOldSize = customerList.size
                list.forEach {
                    if (idSet.contains(it.id).not()) {
                        customerList.add(it)
                        idSet.add(it.id ?: 0)
                    }
                }
                if (customerList.size - customerOldSize > 0) {
                    notifyItemRangeInserted(customerOldSize, (customerList.size - customerOldSize))
                }
            } catch (_: Exception) {
            } catch (_: ConcurrentModificationException) {
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

    private fun getNextLevel(level: CustomerLevel): CustomerLevel? {
        return when (level) {
            CustomerLevel.LEVEL_ONE -> null
            CustomerLevel.LEVEL_TWO -> CustomerLevel.LEVEL_ONE
            CustomerLevel.LEVEL_THREE -> CustomerLevel.LEVEL_TWO
        }
    }

}
