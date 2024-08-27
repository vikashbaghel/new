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
import com.app.rupyz.sales.orders.adapter.OrderStatusUpdateListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderStatusBottomSheetDialogFragment(
    var model: OrderData,
    var statusName:String,
    var listener: IOrderStatusChangeListener
) :
    BottomSheetDialogFragment(), OrderStatusUpdateListAdapter.IOrderDispatchListener {
    private lateinit var binding: BottomSheetOrderStatusBinding

    private lateinit var orderStatusUpdateListAdapter: OrderStatusUpdateListAdapter
    private var orderStatusList: MutableList<OrderStatusModel> = ArrayList()

    private var orderStatusModel: OrderStatusModel? = null
    private var orderStatus: String? = null

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
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogNewTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allStatusList: MutableList<OrderStatusModel> = mutableListOf()
       // allStatusList.add(OrderStatusModel(name = statusName, isSelected = true,statusColor = resources.getColor(R.color.order_status_green)))
        orderStatus=model.deliveryStatus

        when (model.deliveryStatus) {


            AppConstant.APPROVED_ORDER -> {

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.PROCESS_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add(OrderStatusModel(name="Processing", statusColor = resources.getColor(R.color.order_status_green)))
                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add(OrderStatusModel(name="Ready To Dispatch", statusColor = resources.getColor(R.color.discount_applied_yellow)))

                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add(OrderStatusModel(name="Dispatch", statusColor = resources.getColor(R.color.green)))

                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.REJECT_ORDER_PERMISSION, false
                    )
                ) {
                    allStatusList.add(OrderStatusModel(name="Reject", statusColor = resources.getColor(R.color.red)))

                }
            }

            AppConstant.PROCESSING_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                    )
                ) {

                    allStatusList.add(OrderStatusModel(name="Ready To Dispatch", statusColor = resources.getColor(R.color.discount_applied_yellow)))

                }
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {

                    allStatusList.add(OrderStatusModel(name="Dispatch", statusColor = resources.getColor(R.color.discount_applied_yellow)))

                }
            }

            AppConstant.READY_TO_DISPATCH_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                    )
                ) {

                    allStatusList.add(OrderStatusModel(name="Dispatch", statusColor = resources.getColor(R.color.discount_applied_yellow)))

                }
            }

            AppConstant.SHIPPED_ORDER -> {
                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DELIVER_ORDER_PERMISSION, false
                    )
                ) {

                    allStatusList.add(OrderStatusModel(name="Delivered", statusColor = resources.getColor(R.color.green)))

                }
            }

            AppConstant.PARTIAL_SHIPPED_ORDER -> {
                if (model.isClosed == true) {

                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELIVER_ORDER_PERMISSION, false
                        )
                    ) {

                        allStatusList.add(OrderStatusModel(name="Delivered", statusColor = resources.getColor(R.color.green)))

                    }
                } else {
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DISPATCH_ORDER_PERMISSION, false
                        )
                    ) {

                        allStatusList.add(OrderStatusModel(name="Dispatch", statusColor = resources.getColor(R.color.discount_applied_yellow)))

                    }
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELIVER_ORDER_PERMISSION, false
                        )
                    ) {

                        allStatusList.add(OrderStatusModel(name="Delivered", statusColor = resources.getColor(R.color.green)))

                    }
                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.CLOSE_ORDER_PERMISSION, false
                        )
                    ) {

                        allStatusList.add(OrderStatusModel(name="Close", statusColor = resources.getColor(R.color.red)))

                    }
                }
            }
        }

        if (allStatusList.size > 0) {
            orderStatusList.addAll(allStatusList)

        } else {
            binding.tvOrderStatusList.visibility = View.GONE
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.buttonProceed.visibility = View.GONE
        }

        initRecyclerView()

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        /*binding.buttonProceed.setOnClickListener {

            if (orderStatusModel?.name == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_select_status), Toast.LENGTH_SHORT)
                    .show()
            } else {
                listener.onOrderStatusUpdate(orderStatusModel!!)
            }
            dismiss()
        }*/
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


        orderStatusUpdateListAdapter = OrderStatusUpdateListAdapter(orderStatusList, this, orderStatus!!)
        binding.tvOrderStatusList.adapter = orderStatusUpdateListAdapter
    }

    interface IOrderStatusChangeListener {
        fun onOrderStatusUpdate(model: OrderStatusModel)

    }

    override fun onUpdateStatus(model: OrderStatusModel, position: Int) {
        //orderStatusModel = model
        listener.onOrderStatusUpdate(model)
        dismiss()

        orderStatusList.forEach { it.isSelected = false }
        orderStatusList[position].isSelected = true
        orderStatusUpdateListAdapter.notifyDataSetChanged()

    }

}