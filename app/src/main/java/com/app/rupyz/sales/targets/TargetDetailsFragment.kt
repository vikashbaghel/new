package com.app.rupyz.sales.targets

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator
import com.app.rupyz.databinding.TargetsDetailsFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.ProductMetricsModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import com.app.rupyz.model_kt.StaffTargetModel
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import java.text.SimpleDateFormat
import java.util.*

class TargetDetailsFragment : BaseFragment(),
    ActiveTargetsListRvAdapter.ITargetProductActionListener {
    private lateinit var binding: TargetsDetailsFragmentBinding
    private lateinit var staffViewModel: StaffViewModel
    private lateinit var activeTargetsListRvAdapter: ActiveTargetsListRvAdapter
    private lateinit var targetProductsListRvAdapter: TargetProductsListRvAdapter

    private var targetList = ArrayList<StaffTargetModel>()
    private var productTargetList = ArrayList<ProductMetricsModel>()
    private var tab = ""
    private var staffId: Int? = null
    var currentlyActiveTarget: StaffCurrentlyActiveDataModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TargetsDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        initObservers()

        binding.mainContent.visibility = View.GONE

        arguments?.let {
            staffId = arguments?.getInt(AppConstant.STAFF_ID)

            if (arguments?.get(AppConstant.STAFF_DETAILS) != null) {

                binding.hdActiveStaff.visibility = View.VISIBLE

                binding.hdTargetViewAll.visibility = View.VISIBLE
            }

            if (arguments?.get(AppConstant.TARGET_PRODUCTS) != null) {
                if (arguments?.get(AppConstant.TARGET_PRODUCTS_LIST) != null) {
                    currentlyActiveTarget =
                        arguments?.getParcelable(AppConstant.TARGET_PRODUCTS_LIST)
                }

                if (currentlyActiveTarget != null && currentlyActiveTarget?.productMetrics.isNullOrEmpty()
                        .not()
                ) {
                    productTargetList.addAll(currentlyActiveTarget?.productMetrics!!)
                }

                initTargetProductRecyclerView()

                binding.mainContent.visibility = View.VISIBLE
            } else {
                initActiveRecyclerView()
                staffViewModel.getCurrentlyActiveTargets(staffId)
            }
        }

        binding.hdTargetViewAll.setOnClickListener {
            startActivity(
                Intent(requireContext(), FragmentContainerActivity::class.java)
                    .putExtra(AppConstant.TARGET_SALES, true)
                    .putExtra(AppConstant.USER_ID, staffId)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        staffViewModel.staffCurrentlyActiveTargetsLiveData.observe(requireActivity()) {
            if (it.error == false) {
                targetList.clear()

                if (it.data?.id != null) {
                    it.data.let { data ->

                        currentlyActiveTarget = it.data

                        binding.tvAssignedBy.text = "Assigned by - ${data.createdByName}"
                        binding.tvStaffName.text = data.name

                        binding.tvRecurring.isVisible = it.data.recurring ?: false

                        binding.tvTargetDate.text =
                            "${
                                DateFormatHelper.convertStringToCustomDateFormat(
                                    data.startDate,
                                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                                )
                            } - ${
                                DateFormatHelper.convertStringToCustomDateFormat(
                                    data.endDate,
                                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                                )
                            }"

                        if (data.targetSalesAmount != null && data.targetSalesAmount != 0.0) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_SALES,
                                    data.targetSalesAmount,
                                    data.currentSalesAmount,
                                    R.drawable.ic_target_sales
                                )
                            )
                        }

                        if (data.targetPaymentCollection != null && data.targetPaymentCollection != 0.0) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_COLLECTION,
                                    data.targetPaymentCollection,
                                    data.currentPaymentCollection,
                                    R.drawable.ic_target_collection
                                )
                            )
                        }
                        if (data.targetNewLeads != null && data.targetNewLeads != 0) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_LEADS,
                                    data.targetNewLeads.toDouble(),
                                    data.currentNewLeads?.toDouble(),
                                    R.drawable.ic_target_leads
                                )
                            )
                        }
                        if (data.targetNewCustomers != null && data.targetNewCustomers != 0) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_CUSTOMER,
                                    data.targetNewCustomers.toDouble(),
                                    data.currentNewCustomers?.toDouble(),
                                    R.drawable.ic_target_customer
                                )
                            )
                        }
                        if (data.targetCustomerVisits != null && data.targetCustomerVisits != 0) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_VISITS,
                                    data.targetCustomerVisits.toDouble(),
                                    data.currentCustomerVisits?.toDouble(),
                                    R.drawable.ic_target_visits
                                )
                            )
                        }
                        if (data.productMetrics.isNullOrEmpty().not()) {
                            targetList.add(
                                StaffTargetModel(
                                    AppConstant.TARGET_PRODUCTS,
                                    data.productMetrics?.size?.toDouble(),
                                    0.0,
                                    R.drawable.ic_target_products
                                )
                            )
                        }

                        activeTargetsListRvAdapter.notifyDataSetChanged()

                        binding.mainContent.visibility = View.VISIBLE
                    }
                } else {
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initTargetProductRecyclerView() {
        binding.rvTarget.layoutManager = LinearLayoutManager(requireContext())
        targetProductsListRvAdapter = TargetProductsListRvAdapter(productTargetList)
        val itemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.item_divider_white
                )
            )
        binding.rvTarget.addItemDecoration(itemDecoration)
        binding.rvTarget.adapter = targetProductsListRvAdapter
    }

    private fun initActiveRecyclerView() {
        binding.rvTarget.layoutManager = LinearLayoutManager(requireContext())
        activeTargetsListRvAdapter = ActiveTargetsListRvAdapter(targetList, this)
        binding.rvTarget.adapter = activeTargetsListRvAdapter
    }

    override fun getTargetProductDetails() {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.TARGET_PRODUCTS,
                true
            ).putExtra(AppConstant.TARGET_PRODUCTS_LIST, currentlyActiveTarget)
        )
    }
}