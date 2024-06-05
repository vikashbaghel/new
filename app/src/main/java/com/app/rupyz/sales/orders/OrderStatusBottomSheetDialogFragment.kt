package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator
import com.app.rupyz.databinding.BottomSheetOrderStatusBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.OrderStatusModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderStatusBottomSheetDialogFragment(
    var model: OrderData,
    var listener: IOrderStatusChangeListener
) :
    BottomSheetDialogFragment(), OrderStatusUpdateListAdapter.IOrderDispatchListener {
    private lateinit var binding: BottomSheetOrderStatusBinding

    private lateinit var orderStatusUpdateListAdapter: OrderStatusUpdateListAdapter
    private var orderStatusList: MutableList<OrderStatusModel> = ArrayList()

    private var orderStatusModel: OrderStatusModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetOrderStatusBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allStatusList: MutableList<String> = mutableListOf()

        when (model.deliveryStatus) {

            AppConstant.APPROVED_ORDER -> {

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.PROCESS_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Processing")
                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Ready To Dispatch")
                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Dispatch")
                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.REJECT_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Reject")
                }
            }

            AppConstant.PROCESSING_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Ready To Dispatch")
                }
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Dispatch")
                }
            }

            AppConstant.READY_TO_DISPATCH_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Dispatch")
                }
            }

            AppConstant.SHIPPED_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DELIVER_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add("Delivered")
                }
            }

            AppConstant.PARTIAL_SHIPPED_ORDER -> {
                if (model.isClosed == true) {

                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELIVER_ORDER_PERMISSION, false
                        )
                    ) {
                        allStatusList.add("Delivered")
                    }
                } else {
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DISPATCH_ORDER_PERMISSION, false
                        )
                    ) {
                        allStatusList.add("Dispatch")
                    }
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELIVER_ORDER_PERMISSION, false
                        )
                    ) {
                        allStatusList.add("Delivered")
                    }
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.CLOSE_ORDER_PERMISSION, false
                        )
                    ) {
                        allStatusList.add("Close")
                    }
                }
            }
        }

        if (allStatusList.size > 0) {
            allStatusList.forEach {
                val model = OrderStatusModel()
                model.name = it
                model.isSelected = false
                orderStatusList.add(model)
            }
        } else {
            binding.tvOrderStatusList.visibility = View.GONE
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.buttonProceed.visibility = View.GONE
        }

        initRecyclerView()

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            dismiss()
            if (orderStatusModel == null) {
                Toast.makeText(requireContext(), "Please select status!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                listener.onOrderStatusUpdate(orderStatusModel!!)
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.tvOrderStatusList.layoutManager = linearLayoutManager
        val itemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.item_divider_gray
                )
            )
        binding.tvOrderStatusList.addItemDecoration(itemDecoration)

        orderStatusUpdateListAdapter = OrderStatusUpdateListAdapter(orderStatusList, this)
        binding.tvOrderStatusList.adapter = orderStatusUpdateListAdapter
    }

    interface IOrderStatusChangeListener {
        fun onOrderStatusUpdate(model: OrderStatusModel)
    }

    override fun onUpdateStatus(model: OrderStatusModel, position: Int) {
        orderStatusModel = model

        orderStatusList.forEach { it.isSelected = false }
        orderStatusList[position].isSelected = true
        orderStatusUpdateListAdapter.notifyDataSetChanged()

    }

}