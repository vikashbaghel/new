package com.app.rupyz.sales.beatplan

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentSelectCustomerForBeatBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.order.customer.CustomerData

class SelectCustomerForBeatPlanFragment : BaseFragment(),
    ChooseCustomerForBeatAdapter.IAssignCustomerListener,
    SortByBottomSheetDialogFragment.ISortByCustomerListener,
    BeatCustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {

    private lateinit var binding: FragmentSelectCustomerForBeatBinding
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var customerListForAssignAdapter: ChooseCustomerForBeatAdapter
    private var customerList: ArrayList<CustomerData> = ArrayList()
    var addSubLevelSet = ArrayList<Int>()
    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1
    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)
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

    companion object {
        private lateinit var listener: ChooseCustomerForBeatPlanListener
        private lateinit var model: BeatRouteDayListModel

        fun newInstance(
            createBeatListener: ChooseCustomerForBeatPlanListener,
            dailyModel: BeatRouteDayListModel
        ): SelectCustomerForBeatPlanFragment {
            listener = createBeatListener
            model = dailyModel
            return SelectCustomerForBeatPlanFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCustomerForBeatBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        initObservers()
        initRecyclerView()

        binding.progressBar.visibility = View.VISIBLE

        binding.cbAllCustomerName.setOnCheckedChangeListener(null)

        if (model.isUpdate == true || model.isFirstTime == false) {
            binding.cbAllCustomerName.isChecked = model.allowAllCustomers ?: false
            isAllCustomerChecked = model.allowAllCustomers ?: false
            beatCustomerResponseModel = model.selectDayBeat!!
        } else {
            binding.cbAllCustomerName.isChecked = false
            isAllCustomerChecked = false
        }

        loadCustomerPage()

        binding.btnAdd.setOnClickListener {
            if (!isAllCustomerChecked && updatedAddedCustomerList.isEmpty()
                    && beatCustomerResponseModel.subLevelSet.isNullOrEmpty()
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
                model.selectDayBeat = beatCustomerResponseModel
                model.allowAllCustomers = isAllCustomerChecked
                listener.onChooseCustomerList(model)
                beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
            }
        }

        binding.btnCancel.setOnClickListener {
            listener.onCancelChooseCustomer()
        }

        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.tvFilter.setOnClickListener {
            val fragment = BeatCustomerFilterBottomSheetDialogFragment.newInstance(
                this,
                filterCustomerLevel,
                filterAssignedStaff,
                filterParentCustomer
            )
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
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
            model.selectDayBeat = beatCustomerResponseModel
            model.allowAllCustomers = isAllCustomerChecked
            listener.onChooseCustomerList(model)
            beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initRecyclerView() {
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
        isFirstTimeLoadCustomer = true
        binding.clEmptyData.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getListOfCustomerForBeat(
            model.beatId,
            "",
            model.date,
            model.beatrouteplan ?: 0,
            AppConstant.ALL.uppercase(),
            true,
            filterCustomerLevel,
            filterParentCustomer.first,
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
                } else {
                    if (currentPage == 1) {
                        isApiLastPage = true

                        binding.clEmptyData.visibility = View.VISIBLE

                        customerList.clear()
                        customerListForAssignAdapter.notifyDataSetChanged()
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

                updatedRemoveCustomerList = it.removeCustomer?: ArrayList()
                updatedAddedCustomerList = it.addCustomer?: ArrayList()

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

    override fun chooseRetailer(customerData: CustomerData, position: Int) {
        listener.onChooseRetailer(model, customerData, beatCustomerResponseModel)
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
}