package com.app.rupyz.sales.beat

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentCustomerListForBeatBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.custom.CustomTabAdapter
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.beatplan.*

class CustomerListForBeatFragment : BaseFragment(),
    ChooseCustomerForBeatAdapter.IAssignCustomerListener,
    SortByBottomSheetDialogFragment.ISortByCustomerListener,
    BeatCustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener,
    CustomTabAdapter.CustomTabListener {
    private lateinit var binding: FragmentCustomerListForBeatBinding

    private val beatViewModel: BeatViewModel by viewModels()
    private lateinit var customerListForAssignAdapter: ChooseCustomerForBeatAdapter
    private lateinit var customTabRvAdapter: CustomTabAdapter
    private var customerList: ArrayList<CustomerData> = ArrayList()
    private var addSubLevelSet = ArrayList<Int>()
    private var tabList = ArrayList<AllCategoryResponseModel>()
    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)
    private var status = AppConstant.ALL
    private var filterCustomerLevel = ""
    private var assignedStaffFilterApply = false

    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterParentCustomer: Pair<Int, String> = Pair(0, "")

    private var sortByOrder: String = ""
    private var filterCount = 0
    private var levelFilterApply = false
    private var customerTypeFilterApply = false
    private val beatCustomerViewModel: SelectCustomerForBeatViewModel by activityViewModels()
    private val selectRetailerViewModel: SelectRetailerForBeatViewModel by activityViewModels()
    private var updatedAddedCustomerList: ArrayList<Int> = ArrayList()
    private var updatedRemoveCustomerList: ArrayList<Int> = ArrayList()
    private var isAllCustomerChecked = true
    private var isDeSelectAllCustomer = false
    private var isCustomerSetUpdated = false
    private var isFirstTimeLoadCustomer = true
    private var isUpdatedFromSearch = false
    private var beatCustomerResponseModel = BeatCustomerResponseModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerListForBeatBinding.inflate(layoutInflater)
        return binding.root
    }


    companion object {
        private lateinit var listener: ChooseCustomerForBeatListener
        private var model: AddBeatModel? = null

        fun newInstance(
            createBeatListener: ChooseCustomerForBeatListener,
            dailyModel: AddBeatModel?
        ): CustomerListForBeatFragment {
            listener = createBeatListener
            model = dailyModel
            return CustomerListForBeatFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initRecyclerView()
        initTabLayout()

        binding.progressBar.visibility = View.VISIBLE

        binding.cbAllCustomerName.setOnCheckedChangeListener(null)

        beatCustomerResponseModel = model?.selectCustomer!!

        if (model?.allowAll == true) {
            binding.cbAllCustomerName.isChecked = model?.allowAll ?: false
            isAllCustomerChecked = model?.allowAll ?: false
        } else {
            binding.cbAllCustomerName.isChecked = false
            isAllCustomerChecked = false
        }

        loadCustomerPage()

        binding.btnAdd.setOnClickListener {
            if (!isAllCustomerChecked && updatedAddedCustomerList.isEmpty() && updatedRemoveCustomerList.isEmpty()
            ) {
                showWarningDialog()
            } else {
                beatCustomerResponseModel.selectAllCustomer = isAllCustomerChecked
                beatCustomerResponseModel.deSelectAllCustomer = isDeSelectAllCustomer
                if (isAllCustomerChecked) {
                    beatCustomerResponseModel.addCustomer = ArrayList()
                }

                if (updatedRemoveCustomerList.isEmpty()) {
                    beatCustomerResponseModel.removeCustomer = ArrayList()
                }
                model?.selectCustomer = beatCustomerResponseModel
                model?.allowAll = isAllCustomerChecked
                beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
                listener.onChooseCustomerList(
                    model!!
                )
            }
        }

        binding.btnCancel.setOnClickListener {
            listener.onCancelChooseCustomer()
        }

        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(childFragmentManager, CustomerListForBeatFragment::class.java.name)
        }

        binding.tvFilter.setOnClickListener {
            val fragment = BeatCustomerFilterBottomSheetDialogFragment.newInstance(
                this,
                filterCustomerLevel,
                filterAssignedStaff,
                filterParentCustomer
            )
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.STAFF_DETAILS, true)
            fragment.arguments = bundle
            fragment.show(childFragmentManager, CustomerListForBeatFragment::class.java.name)
        }

        binding.cbAllCustomerName.setOnCheckedChangeListener { _, isChecked ->
            beatCustomerResponseModel.isUpdatedExternally = true
            isCustomerSetUpdated = true
            isAllCustomerChecked = isChecked
            isDeSelectAllCustomer = isChecked.not()

            customerList.forEach {
                it.isSelected = isChecked
            }
            customerListForAssignAdapter.notifyDataSetChanged()

            updatedAddedCustomerList.clear()
            updatedRemoveCustomerList.clear()


            tabList.forEach {
                it.isEnable = isChecked.not()
            }
            customTabRvAdapter.notifyDataSetChanged()

            if (isChecked) {
                beatCustomerResponseModel.deSelectAllCustomer = false
                beatCustomerResponseModel.selectAllCustomer = true
                if (customerList.size >= 30) {
                    beatCustomerResponseModel.isPaginationAvailable = true
                } else {
                    beatCustomerResponseModel.selectedAllCustomerCount = customerList.size
                }
            } else {
                beatCustomerResponseModel.selectAllCustomer = false
                beatCustomerResponseModel.deSelectAllCustomer = true
            }
        }


        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                isApiLastPage = false
                isPageLoading = true
                customerList.clear()
                customerListForAssignAdapter.notifyDataSetChanged()
                loadCustomerPage()
                Utils.hideKeyboard(requireActivity())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            currentPage = 1
            isApiLastPage = false
            isPageLoading = true
            customerList.clear()
            customerListForAssignAdapter.notifyDataSetChanged()
            loadCustomerPage()
            Utils.hideKeyboard(requireActivity())
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }
            }
        })
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            customerList.clear()
            isPageLoading = true
            isApiLastPage = false
            customerListForAssignAdapter.notifyDataSetChanged()
            loadCustomerPage()
        }
    }

    private fun initTabLayout() {
        val category = AllCategoryResponseModel()
        category.name = AppConstant.ALL
        category.isSelected = true
        if (model?.allowAll == false) {
            category.isEnable = true
        }
        tabList.add(category)

        val category1 = AllCategoryResponseModel()
        category1.name = AppConstant.NOT_ASSIGNED
        if (model?.allowAll == false) {
            category1.isEnable = true
        }
        tabList.add(category1)

        val category2 = AllCategoryResponseModel()
        category2.name = AppConstant.ASSIGNED
        if (model?.allowAll == false) {
            category2.isEnable = true
        }
        tabList.add(category2)

        if (model?.id != null) {
            customTabRvAdapter.notifyDataSetChanged()
        } else {
            binding.tabLayout.visibility = View.GONE
        }
    }

    private fun showWarningDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.no_customer_selected)
        tvTitle.text = resources.getString(R.string.no_customer_selected_message)

        tvCancel.text = resources.getString(R.string.no)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            beatCustomerResponseModel.selectAllCustomer = isAllCustomerChecked
            beatCustomerResponseModel.deSelectAllCustomer = isDeSelectAllCustomer
            if (isAllCustomerChecked) {
                beatCustomerResponseModel.addCustomer = ArrayList()
            } else if (updatedRemoveCustomerList.isEmpty()) {
                beatCustomerResponseModel.removeCustomer = ArrayList()
            }
            model?.selectCustomer = beatCustomerResponseModel
            model?.allowAll = isAllCustomerChecked
            listener.onChooseCustomerList(
                model!!
            )
            beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initRecyclerView() {

        val linearLayoutManager1 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.tabLayout.layoutManager = linearLayoutManager1
        customTabRvAdapter = CustomTabAdapter(tabList, this)
        binding.tabLayout.adapter = customTabRvAdapter


        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerListForAssignAdapter = ChooseCustomerForBeatAdapter(customerList, this, false)
        binding.rvCustomerList.adapter = customerListForAssignAdapter

        binding.rvCustomerList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadCustomerPage() {
        binding.cbAllCustomerName.visibility = View.GONE
        isFirstTimeLoadCustomer = true
        binding.clEmptyData.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getListOfCustomerForBeat(
            model?.id ?: 0,
            binding.etSearch.text.toString(),
            null,
            0,
            status.uppercase(),
            false,
            filterCustomerLevel,
            model?.parentCustomer,
            ArrayList(),
            sortByOrder,
            currentPage
        )
    }

    private fun initObservers() {
        beatViewModel.customerListForBeatLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            isPageLoading = false
            if (it.error == false) {
                binding.clEmptyData.visibility = View.GONE
                if (it.data.isNullOrEmpty().not()) {

                    if (currentPage == 1) {
                        customerList.clear()
                        customerListForAssignAdapter.notifyDataSetChanged()
                    }

                    customerList.addAll(it.data!!)

                    updatedCustomerList()

                    customerListForAssignAdapter.notifyDataSetChanged()

                    if (it.data.size < 30) {
                        isApiLastPage = true
                    }

                    if (model?.allowAll == true) {
                        if (customerList.size >= 30) {
                            beatCustomerResponseModel.isPaginationAvailable = true
                        } else {
                            beatCustomerResponseModel.selectedAllCustomerCount = customerList.size
                        }
                    }

                    if (status == AppConstant.ALL && binding.etSearch.text.toString().isEmpty()) {
                        binding.cbAllCustomerName.visibility = View.VISIBLE
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        binding.clEmptyData.visibility = View.VISIBLE
                        customerList.clear()
                        customerListForAssignAdapter.notifyDataSetChanged()

                        binding.cbAllCustomerName.visibility = View.GONE
                    }
                }

                isFirstTimeLoadCustomer = false
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        beatCustomerViewModel.selectCustomerListLiveData.observe(requireActivity()) {
            if (it.isUpdatedFromSearch) {
                isUpdatedFromSearch = true

                updatedRemoveCustomerList = it.removeCustomer ?: ArrayList()
                updatedAddedCustomerList = it.addCustomer ?: ArrayList()

                beatCustomerResponseModel.addCustomer = updatedAddedCustomerList
                beatCustomerResponseModel.removeCustomer = updatedRemoveCustomerList

                if (customerList.isNotEmpty()) {
                    updatedCustomerList()
                }
            }
        }

        selectRetailerViewModel.selectCustomerListLiveData.observe(requireActivity()) {
            if (it.addCustomer.isNullOrEmpty().not()) {
                it.addCustomer?.forEach { customer ->
                    val addedCustomerExist =
                        customerList.findLast { addedCustomer -> addedCustomer.id == customer.id }
                    if (addedCustomerExist == null) {
                        addSubLevelSet = ArrayList()
                        it.addCustomer?.forEach { add ->
                            addSubLevelSet.add(add.id!!)
                        }
                    }
                }
            }

            if (it.removeCustomer.isNullOrEmpty().not()) {
                it.removeCustomer?.forEach { customer ->
                    val addSetLevel =
                        addSubLevelSet.findLast { addSetModel -> addSetModel == customer.id }

                    if (addSetLevel != null) {
                        addSubLevelSet.remove(addSetLevel)
                    } else {
                        updatedRemoveCustomerList.add(customer.id!!)
                        beatCustomerResponseModel.removeCustomer = updatedRemoveCustomerList
                    }
                }
            }
            beatCustomerResponseModel.subLevelSet = addSubLevelSet
        }
    }

    private fun updatedCustomerList() {
        customerList.forEachIndexed { index, customer ->
            if (beatCustomerResponseModel.isUpdatedExternally) {

                if (beatCustomerResponseModel.selectAllCustomer == true) {
                    if (updatedRemoveCustomerList.isEmpty().not()) {
                        val addedCustomerExist =
                            updatedRemoveCustomerList.findLast { addedCustomer -> addedCustomer == customer.id }
                        customerList[index].isSelected = addedCustomerExist == null
                    } else {
                        customerList[index].isSelected = true
                    }
                } else if (beatCustomerResponseModel.deSelectAllCustomer == true) {
                    if (updatedAddedCustomerList.isEmpty().not()) {
                        val addedCustomerExist =
                            updatedAddedCustomerList.findLast { addedCustomer -> addedCustomer == customer.id }
                        customerList[index].isSelected = addedCustomerExist != null
                    } else {
                        customerList[index].isSelected = false
                    }
                }
            } else {
                if (updatedAddedCustomerList.isEmpty().not()) {
                    val addedCustomerExist =
                        updatedAddedCustomerList.findLast { addedCustomer -> addedCustomer == customer.id }
                    if (addedCustomerExist != null) {
                        customerList[index].isSelected = true
                    } else {
                        customerList[index].isSelected = customer.isSelected
                    }
                }

                if (updatedRemoveCustomerList.isEmpty().not()) {
                    val addedCustomerExist =
                        updatedRemoveCustomerList.findLast { addedCustomer -> addedCustomer == customer.id }
                    if (addedCustomerExist != null) {
                        customerList[index].isSelected = false
                    } else {
                        customerList[index].isSelected = customer.isSelected
                    }
                }
            }
        }

        customerListForAssignAdapter.notifyDataSetChanged()
    }

    override fun setCustomerSelect(checked: Boolean, customerData: CustomerData) {
        if (!isFirstTimeLoadCustomer) {
            isCustomerSetUpdated = true
            if (checked) {
                if (updatedRemoveCustomerList.size > 0) {
                    val index = updatedRemoveCustomerList.indexOfLast { it == customerData.id }
                    if (index != -1) {
                        updatedRemoveCustomerList.removeAt(index)
                    } else {
                        updatedAddedCustomerList.add(customerData.id!!)
                    }
                } else {
                    updatedAddedCustomerList.add(customerData.id!!)
                }

            } else {
                onRemoveCustomer(customerData)
            }
        }

        beatCustomerResponseModel.addCustomer = updatedAddedCustomerList
        beatCustomerResponseModel.removeCustomer = updatedRemoveCustomerList
        beatCustomerResponseModel.isUpdatedFromSearch = false

        beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
    }

    private fun onRemoveCustomer(model: CustomerData) {
        if (updatedAddedCustomerList.size > 0) {
            val index = updatedAddedCustomerList.indexOfLast { it == model.id }
            if (index != -1) {
                updatedAddedCustomerList.removeAt(index)
            } else {
                updatedRemoveCustomerList.add(model.id!!)
            }
        } else {
            updatedRemoveCustomerList.add(model.id!!)
        }

    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1
        loadCustomerPage()
    }

    override fun applyFilter(
        customerLevel: String,
        parentCustomer: Pair<Int, String>,
        assignedStaff: Pair<Int, String>
    ) {
        filterParentCustomer = parentCustomer
        filterCustomerLevel = customerLevel
        filterAssignedStaff = assignedStaff

        if (filterAssignedStaff.first != 0 && assignedStaffFilterApply.not()) {
            ++filterCount
            assignedStaffFilterApply = true
        }

        if (filterCustomerLevel.isEmpty().not() && levelFilterApply.not()) {
            ++filterCount
            levelFilterApply = true
        }

        if (filterParentCustomer.second.isEmpty().not() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }

        if (filterAssignedStaff.first == 0 && assignedStaffFilterApply) {
            --filterCount
            assignedStaffFilterApply = false
        }

        if (filterCustomerLevel.isEmpty() && levelFilterApply) {
            --filterCount
            levelFilterApply = false
        }

        if (filterParentCustomer.second.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        currentPage = 1
        loadCustomerPage()
    }

    override fun onTabSelect(model: AllCategoryResponseModel, position: Int) {
        tabList.forEach { it.isSelected = false }
        tabList[position].isSelected = true
        customTabRvAdapter.notifyDataSetChanged()

        when (model.name) {
            AppConstant.ALL -> {
                status = AppConstant.ALL
                binding.cbAllCustomerName.visibility = View.VISIBLE
            }

            AppConstant.NOT_ASSIGNED -> {
                status = AppConstant.NOT_ASSIGNED_STATUS
                binding.cbAllCustomerName.visibility = View.GONE
            }

            else -> {
                binding.cbAllCustomerName.visibility = View.GONE
                status = AppConstant.ASSIGNED
            }
        }

        currentPage = 1
        customerList.clear()
        customerListForAssignAdapter.notifyDataSetChanged()

        loadCustomerPage()
    }
}