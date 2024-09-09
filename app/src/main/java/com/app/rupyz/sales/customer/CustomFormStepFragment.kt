package com.app.rupyz.sales.customer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.custom_view.CustomViewUtils
import com.app.rupyz.custom_view.basic.EditInputLayout
import com.app.rupyz.custom_view.basic.ParentCardView
import com.app.rupyz.custom_view.basic.TextView
import com.app.rupyz.custom_view.special.CustomerLevelAndType
import com.app.rupyz.custom_view.special.GSTorPANEditor
import com.app.rupyz.custom_view.special.PinCodeEditor
import com.app.rupyz.custom_view.special.StateDropDown
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.custom_view.type.TextViewType
import com.app.rupyz.databinding.FragmentCustomFormStepBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.enumContains
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomFormCreationData



class CustomFormStepFragment() : BaseFragment() {

    private lateinit var binding: FragmentCustomFormStepBinding
    private lateinit var viewModel: CustomerViewModel
    private val sharedCustomerViewModel by activityViewModels<SharedCustomerViewModel>()
    private var pageNUmber: Int = 1
    private var customerId: Int = 0
    private val viewMap: HashMap<String, View> = hashMapOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomFormStepBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        initObservers()
        pageNUmber = arguments?.getInt("pageNUmber") ?: 1
        customerId = arguments?.getInt(AppConstant.CUSTOMER_ID, 0) ?: 0

        viewModel.getAddNewCustomerFormData(pageNUmber, hasInternetConnection())

        if (requireActivity() is NewAddCustomerActivity) {
            (requireActivity() as NewAddCustomerActivity).apply {
                showProgress()
            }
        } else {
            binding.progressBar.showView()
        }
    }

    private fun initObservers() {

        viewModel.addNewCustomerFormData.observe(viewLifecycleOwner) { customFormCreationModel ->
            if (customFormCreationModel.error == false) {
                binding.parent.removeAllViews()
                customFormCreationModel.data?.let { data ->
                    data.sections?.forEach { section ->
                        if (section?.formItems.isNullOrEmpty().not()
                            && (section?.formItems?.filter
                            { it?.status != AppConstant.VISIBLE }?.size == section?.formItems?.size).not()
                        ) {
                            val parent = ParentCardView(requireActivity()).apply {
                                id = View.generateViewId()
                            }

                            val sectionTitle = TextView(requireActivity()).apply {
                                text = section?.name
                                id = View.generateViewId()
                                setTextViewType(TextViewType.TITLE)
                            }
                            parent.addChild(sectionTitle)

                            val customerLevelAndType = section?.formItems?.firstOrNull {
                                it?.fieldProps?.name.toString()
                                    .equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL, true)
                            }
                            if (customerLevelAndType != null) {
                                val itemView = CustomerLevelAndType(requireActivity()).apply {
                                    id = View.generateViewId()
                                    setCustomerLevelAndType(
                                        section,
                                        CustomerLevel.LEVEL_ONE,
                                        customerId
                                    )
                                    setFormFields(customerLevelAndType)
                                    setFormItemType(FormItemType.DROPDOWN)
                                }
                                customerLevelAndType.fieldProps?.name?.let {
                                    viewMap[it] = itemView
                                }
                                viewMap.putAll(itemView.getViewMap())
                                parent.addChild(itemView)
                            } else {
                                section?.formItems?.forEach { field ->
                                    if (enumContains<FormItemType>(field?.type!!)) {
                                        val formItemType = FormItemType.valueOf(field.type)
                                        viewMap.putAll(
                                            CustomViewUtils.createAndAddCustomView(
                                                requireActivity(),
                                                formItemType,
                                                field,
                                                parent,
                                                customerId,
                                                CustomerLevel.LEVEL_ONE
                                            )
                                        )
                                    }
                                }
                            }
                            binding.parent.addView(parent)
                        }
                    }
                }
                setConstrains()
                customFormCreationModel.data?.let { sections ->
                    sharedCustomerViewModel.setCustomFormViewData(
                        hashMapOf(
                            Pair(
                                pageNUmber,
                                Pair(sections, viewMap)
                            )
                        )
                    )
                }
                if (requireActivity() is NewAddCustomerActivity) {
                    (requireActivity() as NewAddCustomerActivity).apply {
                        hideProgress()
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed(
                        { binding.progressBar.hideView() },
                        1000
                    )
                }
            }
        }

    }

    private fun setConstrains() {
        viewMap[AppConstant.SECTION_NAME_GST_IN]?.let { view ->
            if (view is GSTorPANEditor) {
                view.setOnGSTorPANVerificationListener { isGSTCorrect, customerPanOrGstInfoModel ->
                    if (isGSTCorrect) {
                        viewMap[AppConstant.SECTION_NAME_BUSINESS_NAME]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.legalName)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_BUSINESS_OWNER_NAME]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(buildString {
                                    append((customerPanOrGstInfoModel.data?.firstName ?: ""))
                                    append(if (customerPanOrGstInfoModel.data?.firstName != null) { " " } else { "" })
                                    append((customerPanOrGstInfoModel.data?.middleName ?: ""))
                                    append(if (customerPanOrGstInfoModel.data?.middleName != null) { " " } else { "" })
                                    append((customerPanOrGstInfoModel.data?.lastName ?: ""))
                                })
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_2]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.addressLine2)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_ADDRESS_LINE_1]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.addressLine1)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.pincode)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_CITY]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.city)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_STATE]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(customerPanOrGstInfoModel.data?.state)
                            }
                            if (it is StateDropDown) {
                                it.setValue(customerPanOrGstInfoModel.data?.state)
                            }
                        }
                    } else {
                        showToast(customerPanOrGstInfoModel.message)
                    }

                }
            }
        }
        viewMap[AppConstant.SECTION_NAME_PIN_CODE]?.let { view ->
            if (view is PinCodeEditor) {
                view.setOnPostCodeDetailsFetchedListener { isPinCodeCorrect, postalOfficeResponseModel ->
                    if (isPinCodeCorrect && postalOfficeResponseModel.postOffice.isNullOrEmpty()
                            .not()
                    ) {
                        viewMap[AppConstant.SECTION_NAME_CITY]?.let {
                            if (it is EditInputLayout) {
                                it.setValue(postalOfficeResponseModel.postOffice?.get(0)?.district)
                            }
                        }
                        viewMap[AppConstant.SECTION_NAME_STATE]?.let {
                            if (it is StateDropDown) {
                                it.setValue(postalOfficeResponseModel.postOffice?.get(0)?.state)
                            }
                        }
                    } else {
                        showToast(postalOfficeResponseModel.message)
                    }

                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (pageNUmber == 4) {
            reCreatePreviewPage(sharedCustomerViewModel.backupData)
        }
    }

    private fun reCreatePreviewPage(data: HashMap<Int, Pair<CustomFormCreationData, HashMap<String, View>>>) {
        if (pageNUmber == 4) {
            binding.parent.removeAllViews()
            if (data[1] != null) {
                val pageOneSections = data[1]?.first?.sections
                val pageOneDataMap = data[1]?.second
                pageOneSections?.forEach { section ->
                    val parent =
                        ParentCardView(requireActivity()).apply { id = View.generateViewId() }
                    val sectionTitle = TextView(requireActivity()).apply {
                        text = section?.name
                        id = View.generateViewId()
                        setTextViewType(TextViewType.TITLE)
                    }
                    parent.addChild(sectionTitle)

                    section?.formItems?.forEach { formItem ->
                        if (enumContains<FormItemType>(formItem?.type!!)) {
                            val formItemType = FormItemType.valueOf(formItem.type)
                            if (pageOneDataMap != null) {
                                CustomViewUtils.createKeyValueViews(
                                    context = requireContext(),
                                    formItemType = formItemType,
                                    data = formItem,
                                    parent = parent,
                                    viewMap = pageOneDataMap,
                                    customerId = customerId
                                )
                            }
                        }
                    }

                    binding.parent.addView(parent)
                }
            }
            if (data[2] != null) {
                val pageSecondSections = data[2]?.first?.sections
                val pageSecondDataMap = data[2]?.second
                pageSecondSections?.forEach { section ->
                    val parent =
                        ParentCardView(requireActivity()).apply { id = View.generateViewId() }
                    val sectionTitle = TextView(requireActivity()).apply {
                        text = section?.name
                        id = View.generateViewId()
                        setTextViewType(TextViewType.TITLE)
                    }
                    parent.addChild(sectionTitle)
                    val customerLevelAndType = section?.formItems?.firstOrNull {
                        it?.fieldProps?.name.toString()
                            .equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL, true)
                    }
                    if (customerLevelAndType != null) {
                        section.formItems.forEach { formItem ->
                            if (formItem?.fieldProps?.name.equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL)
                                    .not() && enumContains<FormItemType>(formItem?.type!!)
                            ) {
                                val formItemType = FormItemType.valueOf(formItem.type)
                                if (pageSecondDataMap != null) {
                                    CustomViewUtils.createKeyValueViews(
                                        context = requireContext(),
                                        formItemType = formItemType,
                                        data = formItem,
                                        parent = parent,
                                        viewMap = pageSecondDataMap,
                                        customerId = customerId
                                    )
                                }
                            }
                        }
                    } else {
                        section?.formItems?.forEach { formItem ->
                            if (enumContains<FormItemType>(formItem?.type!!)) {
                                val formItemType = FormItemType.valueOf(formItem.type)
                                if (pageSecondDataMap != null) {
                                    CustomViewUtils.createKeyValueViews(
                                        context = requireContext(),
                                        formItemType = formItemType,
                                        data = formItem,
                                        parent = parent,
                                        viewMap = pageSecondDataMap,
                                        customerId = customerId
                                    )
                                }
                            }
                        }
                    }
                    binding.parent.addView(parent)
                }
            }
            if (data[3] != null) {
                val pageThreeSections = data[3]?.first?.sections
                val pageThreeDataMap = data[3]?.second
                pageThreeSections?.forEach { section ->
                    val parent =
                        ParentCardView(requireActivity()).apply { id = View.generateViewId() }
                    val sectionTitle = TextView(requireActivity()).apply {
                        text = section?.name
                        id = View.generateViewId()
                        setTextViewType(TextViewType.TITLE)
                    }
                    parent.addChild(sectionTitle)
                    val customerLevelAndType = section?.formItems?.firstOrNull {
                        it?.fieldProps?.name.toString()
                            .equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL, true)
                    }
                    if (customerLevelAndType != null) {
                        section.formItems.forEach { formItem ->
                            if (formItem?.fieldProps?.name.equals(AppConstant.SECTION_NAME_CUSTOMER_LEVEL)
                                    .not() && enumContains<FormItemType>(formItem?.type!!)
                            ) {
                                val formItemType = FormItemType.valueOf(formItem.type)
                                if (pageThreeDataMap != null) {
                                    CustomViewUtils.createKeyValueViews(
                                        context = requireContext(),
                                        formItemType = formItemType,
                                        data = formItem,
                                        parent = parent,
                                        viewMap = pageThreeDataMap,
                                        customerId = customerId
                                    )
                                }
                            }
                        }
                    } else {
                        section?.formItems?.forEach { formItem ->
                            if (enumContains<FormItemType>(formItem?.type!!)) {
                                val formItemType = FormItemType.valueOf(formItem.type)
                                if (pageThreeDataMap != null) {
                                    CustomViewUtils.createKeyValueViews(
                                        context = requireContext(),
                                        formItemType = formItemType,
                                        data = formItem,
                                        parent = parent,
                                        viewMap = pageThreeDataMap,
                                        customerId = customerId
                                    )
                                }
                            }
                        }
                    }
                    binding.parent.addView(parent)
                }
            }
        }
    }


}