package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentSearchCustomerForBeatBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.BeatRetailerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.order.customer.CustomerData

class SearchRetailerForBeatPlanFragment : BaseFragment(),
    ChooseCustomerForBeatAdapter.IAssignCustomerListener {
    private lateinit var binding: FragmentSearchCustomerForBeatBinding
    private lateinit var beatViewModel: BeatViewModel

    private lateinit var customerListForAssignAdapter: ChooseCustomerForBeatAdapter
    private var customerList: ArrayList<CustomerData> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var addCustomerIdSet: ArrayList<CustomerData> = ArrayList()
    private var removeCustomerIdSet: ArrayList<CustomerData> = ArrayList()

    private val beatCustomerViewModel: SelectRetailerForBeatViewModel by activityViewModels()
    private var beatCustomerResponseModel = BeatRetailerResponseModel()

    private var isCustomerSetUpdated = false
    private var isFirstTimeLoadCustomer = true

    companion object {
        private lateinit var listener: ChooseCustomerForBeatPlanListener
        private var dailyModel: BeatRouteDayListModel? = null
        private var customerSubLevel: String? = ""
        private var customerInfoForRetailer: CustomerData? = null
        fun newInstance(
            createBeatListener: ChooseCustomerForBeatPlanListener,
            dailyModel: BeatRouteDayListModel?,
            customerSubLevel: String?,
            customerInfoForRetailer: CustomerData?
        ): SearchRetailerForBeatPlanFragment {
            listener = createBeatListener
            this.dailyModel = dailyModel
            this.customerSubLevel = customerSubLevel
            this.customerInfoForRetailer = customerInfoForRetailer
            return SearchRetailerForBeatPlanFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchCustomerForBeatBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        initObservers()
        initRecyclerView()

        binding.etSearchCustomer.hint = "Search $customerSubLevel"

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            isApiLastPage = false
            isPageLoading = true
            customerList.clear()
            customerListForAssignAdapter.notifyDataSetChanged()
            Utils.hideKeyboard(requireActivity())
            loadCustomerPage()
        }

        binding.etSearchCustomer.setOnEditorActionListener { _, actionId, _ ->
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
            binding.etSearchCustomer.setText("")
        }

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
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

        binding.btnAdd.setOnClickListener {
            listener.onSaveSearchCustomerListData()
        }

        binding.btnCancel.setOnClickListener {
            listener.onCancelSearchCustomer()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            customerList.clear()
            isPageLoading = true
            customerListForAssignAdapter.notifyDataSetChanged()
            loadCustomerPage()
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
            customerInfoForRetailer?.beatId,
            binding.etSearchCustomer.text.toString(),
            dailyModel?.date,
            dailyModel?.beatrouteplan ?: 0,
            AppConstant.ALL.uppercase(),
            true,
            "",
            customerInfoForRetailer?.id,
            ArrayList(),
            AppConstant.SORTING_LEVEL_ASCENDING,
            currentPage
        )
    }

    private fun initObservers() {
        beatViewModel.customerListForBeatLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            isPageLoading = false
            if (it.error == false) {
                binding.clEmptyData.visibility = View.GONE

                if (currentPage == 1) {
                    customerList.clear()
                }
                if (it.data.isNullOrEmpty().not()) {
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
                    }
                }

                isFirstTimeLoadCustomer = false
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        beatCustomerViewModel.selectCustomerListLiveData.observe(requireActivity()) {
            beatCustomerResponseModel = it

            if (it.removeCustomer.isNullOrEmpty().not()) {
                removeCustomerIdSet = it.removeCustomer!!
            }

            if (it.addCustomer.isNullOrEmpty().not()) {
                addCustomerIdSet = it.addCustomer!!
            }
        }
    }


    override fun setCustomerSelect(checked: Boolean, customerData: CustomerData) {
        if (!isFirstTimeLoadCustomer) {
            isCustomerSetUpdated = true
            if (checked) {
                customerData.isSelected = true
                customerData.isPartOfParentCustomer = true
                customerData.customerParentName = customerInfoForRetailer?.name
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

        beatCustomerResponseModel.addCustomer = addCustomerIdSet
        beatCustomerResponseModel.removeCustomer = removeCustomerIdSet

        beatCustomerViewModel.setCustomerList(beatCustomerResponseModel)
    }

    private fun onRemoveCustomer(model: CustomerData) {
        if (addCustomerIdSet.size > 0) {
            val index = addCustomerIdSet.indexOfLast { it.id == model.id }
            if (index != -1) {
                addCustomerIdSet.removeAt(index)
            }
        }

        removeCustomerIdSet.add(model)

        customerList.forEachIndexed { index, _ ->
            if (customerList[index].id == model.id) {
                customerList[index].isSelected = false
            }
        }

        customerListForAssignAdapter.notifyDataSetChanged()
    }
}