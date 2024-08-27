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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemViewDropDownCheckboxViewBinding
import com.app.rupyz.databinding.ItemViewSearchAndRecyclerBinding
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.addDelayedTextChangeListener
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.OrgBeatModel
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.app.rupyz.sales.beatplan.BeatViewModel
import com.google.gson.Gson


class AssignBeatDropDown : LinearLayoutCompat {

    private var binding: ItemViewSearchAndRecyclerBinding =
        ItemViewSearchAndRecyclerBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputLayout: EditInputLayout = binding.etValue
    private var formFields: FormItemsItem? = null
    private var showDropDownArrow: Boolean = true
    private val itemAdapter: BeatAdapter = BeatAdapter()
    private val beatViewModel: BeatViewModel =
        ViewModelProvider(context as FragmentActivity)[BeatViewModel::class.java]
    private var assignBeatHeaders: String? = "selected=true&page_no=1"
    private var defaultHeader: String? = "selected=true&page_no=1"
    private var customerId: Int = -1
    private var beatList: MutableList<OrgBeatModel> = mutableListOf()
    private var selectedBeatList: HashMap<Int, OrgBeatModel> = hashMapOf()
    private val linearLayoutManager: LinearLayoutManager =
        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private var isApiLastPage: Boolean = false
    private var isPageLoading: Boolean = false
    private var formItemType: FormItemType? = null
    private var addBeatSetList: ArrayList<Int?> = arrayListOf()
    private var removeBeatSetList: ArrayList<Int?> = arrayListOf()
    private var alreadyAddedBeatList: ArrayList<Int> = arrayListOf()


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

    fun setAlreadyAddedBead(alreadyAddedBeatList: ArrayList<Int>) {
        this.alreadyAddedBeatList = alreadyAddedBeatList
    }

    fun setCustomerId(customerId: Int) {
        this.customerId = customerId
        setDefaultStyle()
    }

    fun getSelectedBeat(): HashMap<Int, OrgBeatModel> {
        return selectedBeatList
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
        binding.rvItemRecycler.layoutManager = linearLayoutManager
        binding.rvItemRecycler.adapter = itemAdapter
        binding.etSearchField.addDelayedTextChangeListener(500) {
            if (customerId != -1) {
                assignBeatHeaders = defaultHeader
                loadBeat()
            }
        }
        if (customerId != -1) {
            loadBeat()
        }
        setPaginationHandler()
    }

    private fun setPaginationHandler() {
        binding.rvItemRecycler.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                loadBeat()
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
    
    fun getFormField() : FormItemsItem? {
        return formFields
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
        addBeatSetList.clear()
        for ((key, value) in selectedBeatList) {
            if (alreadyAddedBeatList.contains(value.id).not()) {
                addBeatSetList.add(value.id)
            }
        }
        return UpdateMappingModel(
            addSet = addBeatSetList,
            removeSet = removeBeatSetList,
            allowAll = false,
            disallowAll = false
        )
    }

    private fun loadBeat() {
        if (assignBeatHeaders == defaultHeader) {
            isApiLastPage = false
            binding.progressBar.showView()
        }
        beatViewModel.getCustomerBeatMapping(
            customerId,
            binding.etSearchField.text.toString(),
            assignBeatHeaders,
            hasInternetConnection(context)
        )
    }

    private fun observeCustomerList() {
        beatViewModel.orgBeatListLiveData.observe((context as FragmentActivity)) {
            binding.progressBar.hideView()
            isPageLoading = false
            if (it.data.isNullOrEmpty().not()) {
                isApiLastPage = false
                if (assignBeatHeaders == defaultHeader) {
                    beatList.clear()
                    it.data?.let { it1 -> beatList.addAll(it1) }
                    upDateBeatList(true, beatList)
                } else {
                    it.data?.let { it1 -> beatList.addAll(it1) }
                    upDateBeatList(assignBeatHeaders == defaultHeader, it.data)
                }
            } else {
                isApiLastPage = true
            }
            it.headers?.let { headers ->
                if (headers.nextParams.isNullOrEmpty().not()) {
                    assignBeatHeaders = headers.nextParams
                    if (it.data.isNullOrEmpty()) {
                        loadBeat()
                    }
                }
            }
        }
    }

    private fun upDateBeatList(isFirstPage: Boolean, data: List<OrgBeatModel>?) {
        if (isFirstPage) {
            itemAdapter.setBeatList(beatList)
        } else {
            data?.toMutableList()?.let { itemAdapter.addStaff(it) }
        }
    }

    private inner class BeatAdapter : RecyclerView.Adapter<BeatAdapter.BeatViewHolder>() {

        private val beatList: MutableList<OrgBeatModel> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
            return BeatViewHolder(
                ItemViewDropDownCheckboxViewBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return beatList.size
        }

        override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
            holder.bind(position, beatList[position])
        }

        private inner class BeatViewHolder(itemView: ItemViewDropDownCheckboxViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val itemViewBinding = itemView
            fun bind(position: Int, beatDetails: OrgBeatModel) {
                try {
                    itemViewBinding.staffProfilePic.hideView()
                } catch (e: Exception) {
                    log(e.toString())
                }
                itemViewBinding.tvStaffName.text = beatDetails.name
                itemViewBinding.mcbCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (alreadyAddedBeatList.contains(beatDetails.id).not()) {
                            selectedBeatList[beatDetails.id ?: 0] = beatDetails
                        }
                        if (alreadyAddedBeatList.contains(beatDetails.id)) {
                            removeBeatSetList.remove(beatDetails.id)
                        }
                    } else {
                        if (alreadyAddedBeatList.contains(beatDetails.id)) {
                            removeBeatSetList.add(beatDetails.id)
                        }
                        selectedBeatList.remove(beatDetails.id)
                    }
                }
                itemViewBinding.parent.setOnClickListener {
                    itemViewBinding.mcbCheckBox.isChecked =
                        itemViewBinding.mcbCheckBox.isChecked.not()
                }

                if (alreadyAddedBeatList.contains(beatDetails.id) && removeBeatSetList.contains(beatDetails.id).not()) {
                    itemViewBinding.mcbCheckBox.isChecked = true
                } else {
                    itemViewBinding.mcbCheckBox.isChecked = selectedBeatList.contains(beatDetails.id)
                }

                if (position == beatList.size - 1) {
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
        fun setBeatList(list: MutableList<OrgBeatModel>) {
            beatList.clear()
            beatList.addAll(list)
            notifyDataSetChanged()
        }

        fun addStaff(list: MutableList<OrgBeatModel>) {
            beatList.addAll(list)
            notifyItemRangeInserted((beatList.size - list.size), list.size)
        }

    }

    fun setAlreadyAddedBeat(list: ArrayList<Int>) {
        alreadyAddedBeatList = list
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
