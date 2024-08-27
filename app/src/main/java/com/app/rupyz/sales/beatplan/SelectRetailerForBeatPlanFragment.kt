package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentSelectCustomerForBeatBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRetailerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomerFilterBottomSheetDialogFragment

class SelectRetailerForBeatPlanFragment : BaseFragment(),
    ChooseCustomerForBeatAdapter.IAssignCustomerListener,
    SortByBottomSheetDialogFragment.ISortByCustomerListener,
    CustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {
    private lateinit var binding: FragmentSelectCustomerForBeatBinding
    private lateinit var beatViewModel: BeatViewModel

    private lateinit var customerListForAssignAdapter: ChooseCustomerForBeatAdapter
    private var customerList: ArrayList<CustomerData> = ArrayList()

    private var addCustomerIdSet: ArrayList<CustomerData> = ArrayList()
    private var removeCustomerIdSet: ArrayList<CustomerData> = ArrayList()

    private var addedRetailerList: ArrayList<Int> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var filterCustomerLevel = ""
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()

    private var sortByOrder: String = ""

    private var filterCount = 0
    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")

    private var assignedStaffFilterApply = false
    private var levelFilterApply = false
    private var customerTypeFilterApply = false
    private var isFirstTimeLoadCustomer = true
    private var isCustomerSetUpdated = false

    private val beatRetailerViewModel: SelectRetailerForBeatViewModel by activityViewModels()
    private var beatRetailerResponseModel = BeatRetailerResponseModel()

    companion object {
        private lateinit var listener: ChooseCustomerForBeatPlanListener
        private lateinit var model: CustomerData
        private var beatCustomerResponseModel: BeatCustomerResponseModel? = null
        private var dayListModel: BeatRouteDayListModel? = null
        fun newInstance(
            createBeatListener: ChooseCustomerForBeatPlanListener,
            dayListModel: BeatRouteDayListModel?,
            customer: CustomerData,
            addCustomerIdSet: BeatCustomerResponseModel,
        ): SelectRetailerForBeatPlanFragment {
            listener = createBeatListener
            model = customer
            this.dayListModel = dayListModel
            this.beatCustomerResponseModel = addCustomerIdSet
            return SelectRetailerForBeatPlanFragment()
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

        initRecyclerView()
        initObservers()

        binding.cbAllCustomerName.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        if (beatCustomerResponseModel?.subLevelSet.isNullOrEmpty().not()) {
            beatCustomerResponseModel?.subLevelSet?.let {
                addedRetailerList = it
            }
        }
        loadCustomerPage()

        binding.btnAdd.setOnClickListener {
            beatRetailerViewModel.setCustomerList(beatRetailerResponseModel)
            listener.onSelectRetailerList()
        }

        binding.btnCancel.setOnClickListener {
            listener.onCancelChooseCustomer()
        }

        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.tvFilter.setOnClickListener {
            val fragment = CustomerFilterBottomSheetDialogFragment.newInstance(
                this,
                filterCustomerLevel,
                filterAssignedStaff,
                filterCustomerType
            )
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerListForAssignAdapter = ChooseCustomerForBeatAdapter(customerList, this, true)
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
        binding.clEmptyData.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getListOfCustomerForBeat(
            model.beatId,
            "",
            dayListModel?.date,
            dayListModel?.beatrouteplan ?: 0,
            AppConstant.ALL.uppercase(),
            true,
            filterCustomerLevel,
            model.id,
            filterCustomerType,
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

                    it.data?.forEach { customer ->

                        if (removeCustomerIdSet.isEmpty().not()) {
                            val removeCustomerExist =
                                removeCustomerIdSet.findLast { addedCustomer -> addedCustomer.id == customer.id }
                            if (removeCustomerExist != null) {
                                customer.isSelected = false
                            }
                        }
                        if (addCustomerIdSet.isEmpty().not()) {
                            val addCustomerExist =
                                addCustomerIdSet.findLast { addedCustomer -> addedCustomer.id == customer.id }
                            if (addCustomerExist != null) {
                                customer.isSelected = true
                            }
                        }

                        customerList.add(customer)
                    }

                    customerListForAssignAdapter.notifyDataSetChanged()

                    if (it.data!!.size < 30) {
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

        beatRetailerViewModel.selectCustomerListLiveData.observe(requireActivity()) {
            if (it.addCustomer.isNullOrEmpty().not()) {
                addCustomerIdSet = it.addCustomer!!
            }
            if (it.removeCustomer.isNullOrEmpty().not()) {
                removeCustomerIdSet = it.removeCustomer!!
            }
            customerList.forEach { customer ->
                val addedCustomerExist =
                    addCustomerIdSet.findLast { addedCustomer -> addedCustomer.id == customer.id }
                customer.isSelected = addedCustomerExist != null
            }
            customerListForAssignAdapter.notifyDataSetChanged()
        }
    }


    override fun setCustomerSelect(checked: Boolean, customerData: CustomerData) {
        if (!isFirstTimeLoadCustomer) {
            isCustomerSetUpdated = true
            if (checked) {
                customerData.isSelected = true
                customerData.isPartOfParentCustomer = true
                customerData.customerParentName = model.name
                addCustomerIdSet.add(customerData)

                if (removeCustomerIdSet.size > 0) {
                    val index =
                        removeCustomerIdSet.indexOfLast { it.id == customerData.id }
                    if (index != -1) {
                        removeCustomerIdSet.removeAt(index)
                    }
                }
            } else {
                onRemoveCustomer(customerData)
            }
        }

        beatRetailerResponseModel.addCustomer = addCustomerIdSet
        beatRetailerResponseModel.removeCustomer = removeCustomerIdSet
    }

    private fun onRemoveCustomer(model: CustomerData) {
        if (addCustomerIdSet.size > 0) {
            val index = addCustomerIdSet.indexOfLast { it.id == model.id }
            if (index != -1) {
                addCustomerIdSet.removeAt(index)
            }
        }

        removeCustomerIdSet.add(model)
    }


    override fun applySortByName(order: String) {
        sortByOrder = order
        currentPage = 1
        loadCustomerPage()
    }

    override fun applyFilter(
        customerLevel: String,
        customerType: ArrayList<CustomerTypeDataItem>,
        assignedStaff: Pair<Int, String>
    ) {
        filterCustomerType = customerType
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

        if (filterCustomerType.isEmpty().not() && customerTypeFilterApply.not()) {
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

        if (filterCustomerType.isEmpty() && customerTypeFilterApply) {
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