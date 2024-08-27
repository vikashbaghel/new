package com.app.rupyz.sales.staff

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffsTotalAmountReceivedDetailsFragmentBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.orders.OrderDetailActivity

class TotalAmountReceivedDetailsFragment(var staffData: StaffData, var customerType: String) :
    Fragment(), OrderStatusActionListener {

    private lateinit var binding: StaffsTotalAmountReceivedDetailsFragmentBinding
    private lateinit var staffRecentPaymentListAdapter: StaffRecentPaymentListAdapter
    private lateinit var salesAnOrderDetailsViewModel: SalesAnOrderDetailsViewModel

    private var recentOrderList: ArrayList<OrderData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StaffsTotalAmountReceivedDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {
        salesAnOrderDetailsViewModel =
            ViewModelProvider(this)[SalesAnOrderDetailsViewModel::class.java]

        binding.tvTotalAmountReceive.text =
            CalculatorHelper().convertCommaSeparatedAmount(staffData.total_payment_amount_received, AppConstant.TWO_DECIMAL_POINTS)

        initRecyclerView()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE

        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
            if (customerType.isNotEmpty()) {
                salesAnOrderDetailsViewModel.getRecentPaymentListById(
                    staffData.id!!,
                    AppConstant.CUSTOMER
                )
            } else {
                salesAnOrderDetailsViewModel.getRecentPaymentListById(
                    staffData.id!!,
                    AppConstant.STAFF
                )
            }
        } else {
            binding.hdRecentSales.visibility = View.GONE
        }

    }

    fun initObservers() {
        salesAnOrderDetailsViewModel.recentPaymentLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE

            if (it.data.isNullOrEmpty().not()) {
                recentOrderList.addAll(it.data!!)
                recentOrderList.forEach { orderData ->
                    orderData.orderStatusChange = false
                }
                staffRecentPaymentListAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentPayment.layoutManager = linearLayoutManager
        staffRecentPaymentListAdapter = StaffRecentPaymentListAdapter(recentOrderList)
        binding.rvRecentPayment.adapter = staffRecentPaymentListAdapter
    }


    override fun onStatusChange(model: OrderData, position: Int, status: String) {
    }

    override fun onGetOrderInfo(model: OrderData, position: Int) {
        startActivity(
            Intent(
                context,
                OrderDetailActivity::class.java
            ).putExtra(AppConstant.ORDER_ID, model.id)
        )
    }

    override fun onDeleteOrder(model: OrderData, position: Int) {
//        showDeleteDialog(model)
    }

    override fun getStoreFrontInfo() {
        val fragment = InfoBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_order))
        bundle.putString(
            AppConstant.MESSAGE,
            resources.getString(R.string.storefront_order_message)
        )
        fragment.arguments = bundle
        fragment.show(childFragmentManager, AppConstant.STORE_FRONT)
    }

}