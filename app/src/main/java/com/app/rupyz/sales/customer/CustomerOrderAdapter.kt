package com.app.rupyz.sales.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemViewCustomerOrderBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.asBitmap
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.OrderStatusModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.filter.OrderStatusByBottomSheetDialogFragment
import com.app.rupyz.sales.home.CustomerOrderAdapter.CustomerOrderViewHolder


class CustomerOrderAdapter(private var data: ArrayList<OrderData>, private var mContext: Context, private var listener: OrderStatusActionListener, private var hasInternetConnection: Boolean, private var fragmentManager: FragmentManager) : RecyclerView.Adapter<CustomerOrderViewHolder>() {
    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerOrderViewHolder {
        return CustomerOrderViewHolder(ItemViewCustomerOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    
    override fun onBindViewHolder(holder : CustomerOrderViewHolder, position : Int) {
        holder.bindItem(data[position], position, mContext, listener, hasInternetConnection, fragmentManager)
    }
    

    override fun getItemCount(): Int {
        return data.size
    }

   inner class CustomerOrderViewHolder(itemView: ItemViewCustomerOrderBinding) : RecyclerView.ViewHolder(itemView.root), OrderStatusByBottomSheetDialogFragment.IStatusListener {
     
        val binding = itemView
        private var model1 = OrderData()


        @SuppressLint("SetTextI18n")
        fun bindItem(model: OrderData, position: Int, context: Context, listener: OrderStatusActionListener, hasInternetConnection: Boolean, fragmentManager: FragmentManager) {
            model1 = model
            val allStatusList: ArrayList<OrderStatusModel> = ArrayList()
            val containerParams = binding.spinnerOrderStatus.layoutParams as ConstraintLayout.LayoutParams
            containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            binding.spinnerOrderStatus.layoutParams = containerParams
            
            
            val  creatorName  = buildString {
                append( model.createdBy?.firstName?.replaceFirstChar(Char::titlecase))
                append(" ")
                append(model.createdBy?.lastName?.replaceFirstChar(Char::titlecase))
            }
            binding.tvOrderName.text = model.orderId
            binding.tvOrderCreatorName.text = creatorName
            binding.ivOrderCreatorImage.setImageBitmap( creatorName.trim().substring(0, (Math.min(creatorName.length, 2))).uppercase().asBitmap(
                    binding.root.context,
                    16f,
                    Color.WHITE,
                    binding.root.context.resources.getColor(R.color.theme_color, null)))
                    
                   

            if (model.source.isNullOrEmpty().not() && model.source.equals(AppConstant.STORE_FRONT)) {
                binding.tvStoreFrontView.visibility = View.VISIBLE
            } else {
                binding.tvStoreFrontView.visibility = View.GONE
            }

            if (model.source.isNullOrEmpty()
                    .not() && model.source.equals(AppConstant.ANDROID_OFFLINE_TAG)
                && model.isSyncedToServer == false
            ) {
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.spinnerOrderStatus.layoutParams = containerParams
            } else {
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.spinnerOrderStatus.layoutParams = containerParams
            }

            binding.tvHdOrderPrice.text =
                CalculatorHelper().convertLargeAmount(
                    model.totalAmount
                        ?: 0.0, AppConstant.TWO_DECIMAL_POINTS
                )

            if (model.orderStatusChange != null && !model.orderStatusChange!!) {
                binding.spinnerOrderStatus.visibility = View.GONE
                binding.tvOrderStatus.visibility = View.VISIBLE
                binding.tvOrderStatus.text = model.deliveryStatus

            } else {
                binding.spinnerOrderStatus.visibility = View.VISIBLE
                binding.tvOrderStatus.visibility = View.GONE
            }
            if (hasInternetConnection &&
                PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_ORDER_PERMISSION, false)
            ) {
                binding.ivMore.visibility = View.VISIBLE
            } else {
                binding.ivMore.visibility = View.GONE
            }

            if (hasInternetConnection.not()) {
                if (model.isSyncedToServer == false) {
                    binding.ivMore.visibility = View.VISIBLE
                } else {
                    binding.ivMore.visibility = View.GONE
                }
            }

            binding.ivMore.setOnClickListener { v ->
                //creating a popup menu
                val popup = PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_delete)
                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.delete -> {
                            listener.onDeleteOrder(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }


            binding.tvHdCreatedByColon.text = DateFormatHelper.getMonthDate(model.createdAt)
            if (model.expectedDeliveryDate != null) {
                binding.deliveryDate.text =
                    DateFormatHelper.getMonthDate(model.expectedDeliveryDate)
            } else {
                binding.deliveryDate.text = ""
                binding.tvHdDeliveryDate.gone()
            }




            if (model.isClosed == true) {
                binding.tvClose.visibility = View.VISIBLE
            } else {
                binding.tvClose.visibility = View.GONE
            }


            when (model.deliveryStatus) {
                AppConstant.RECEIVED_ORDER -> {
                    binding.spinnerOrderStatus.text = AppConstant.RECEIVED_ORDER
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_received
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_recived)

                    allStatusList.add(
                        OrderStatusModel(
                            name = AppConstant.RECEIVED_ORDER,
                            statusColor = MyApplication.instance.getColor(R.color.color_received)
                        )
                    )

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.APPROVE_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Approve",
                                    statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                )
                            )

                        }

                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.REJECT_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Reject",
                                    statusColor = MyApplication.instance.getColor(R.color.color_reject)
                                )
                            )

                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.APPROVED_ORDER -> {
                    binding.spinnerOrderStatus.text = AppConstant.APPROVED
                    allStatusList.add(
                        OrderStatusModel(
                            name = AppConstant.APPROVED,
                            statusColor = MyApplication.instance.getColor(R.color.color_approve)
                        )
                    )
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_approve
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_spinner_green)

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.PROCESS_ORDER_PERMISSION, false
                            )
                        ) {

                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Processing",
                                    statusColor = MyApplication.instance.getColor(R.color.color_processing)
                                )
                            )

                        }

                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Ready To Dispatch",
                                    statusColor = MyApplication.instance.getColor(R.color.color_processing)
                                )
                            )

                        }

                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.DISPATCH_ORDER_PERMISSION, false
                            )
                        ) {

                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Dispatch",
                                    statusColor = MyApplication.instance.getColor(R.color.color_processing)
                                )
                            )

                        }

                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.REJECT_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Reject",
                                    statusColor = MyApplication.instance.getColor(R.color.color_reject)
                                )
                            )


                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }
                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.PROCESSING_ORDER -> {
                    binding.spinnerOrderStatus.text = "Processing"
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_processing
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_dipatch)
                    allStatusList.add(
                        OrderStatusModel(
                            "Processing",
                            statusColor = MyApplication.instance.getColor(R.color.color_processing)
                        )
                    )

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.READY_TO_DISPATCH_ORDER_PERMISSION, false
                            )
                        ) {

                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Ready To Dispatch",
                                    statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                )
                            )

                        }
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.DISPATCH_ORDER_PERMISSION, false
                            )
                        ) {

                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Dispatch",
                                    statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                )
                            )


                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility =
                            View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }
                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.READY_TO_DISPATCH_ORDER -> {
                    binding.spinnerOrderStatus.text = "Ready To Dispatch"
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_processing
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_dipatch)

                    allStatusList.add(
                        OrderStatusModel(
                            name = "Ready To Dispatch",
                            statusColor = MyApplication.instance.getColor(R.color.color_processing)
                        )
                    )



                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.DISPATCH_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Dispatch",
                                    statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                )
                            )


                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.SHIPPED_ORDER -> {
                    binding.spinnerOrderStatus.text = "Dispatched"
                    allStatusList.add(
                        OrderStatusModel(
                            "Dispatched",
                            statusColor = MyApplication.instance.getColor(R.color.color_approve)
                        )
                    )
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_processing
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_dipatch)


                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.DELIVER_ORDER_PERMISSION, false
                            )
                        ) {
                            allStatusList.add(
                                OrderStatusModel(
                                    name = "Delivered",
                                    statusColor = MyApplication.instance.getColor(R.color.color_processing)
                                )
                            )


                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = AppConstant.getOrderStatus(
                            model.deliveryStatus!!, AppConstant.FIND_ORDER_VALUE_FROM_KEY
                        )
                        binding.tvOrderStatus.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.payment_approved_text_color
                            )
                        )
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }
                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.PARTIAL_SHIPPED_ORDER -> {
                    binding.spinnerOrderStatus.text = "Partial Dispatched"
                    //val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add(
                        OrderStatusModel(
                            "Partial Dispatched",
                            statusColor = MyApplication.instance.getColor(R.color.color_approve)
                        )
                    )
                    binding.spinnerOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_processing
                        )
                    )
                    binding.spinnerOrderStatus.setBackgroundResource(R.drawable.bg_dipatch)


                    binding.spinnerOrderStatus.visibility = View.VISIBLE

                    if (model.isClosed == true) {

                        if (hasInternetConnection && PermissionModel.INSTANCE.getPermission(
                                AppConstant.DELIVER_ORDER_PERMISSION, false
                            )
                        ) {

                            allStatusList.add(
                                OrderStatusModel(
                                    "Delivered",
                                    statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                )
                            )

                            binding.tvOrderStatus.visibility = View.GONE

                        } else {
                            binding.tvOrderStatus.text = AppConstant.getOrderStatus(
                                model.deliveryStatus!!, AppConstant.FIND_ORDER_KEY_FROM_VALUE
                            )
                            binding.spinnerOrderStatus.visibility = View.INVISIBLE
                            binding.tvOrderStatus.visibility = View.VISIBLE
                            binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_approved_background)
                            binding.tvOrderStatus.setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.payment_approved_text_color
                                )
                            )
                        }

                        ArrayAdapter(
                            context,
                            R.layout.single_text_view_spinner_green,
                            allStatusList
                        )
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE

                        if (hasInternetConnection) {
                            if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.DISPATCH_ORDER_PERMISSION, false
                                )
                            ) {

                                allStatusList.add(
                                    OrderStatusModel(
                                        "Dispatch",
                                        statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                    )
                                )

                            }
                            if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.DELIVER_ORDER_PERMISSION, false
                                )
                            ) {
                                allStatusList.add(
                                    OrderStatusModel(
                                        "Delivered",
                                        statusColor = MyApplication.instance.getColor(R.color.color_approve)
                                    )
                                )

                            }
                            if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.CLOSE_ORDER_PERMISSION, false
                                )
                            ) {
                                allStatusList.add(
                                    OrderStatusModel(
                                        "Close",
                                        statusColor = MyApplication.instance.getColor(R.color.color_reject)
                                    )
                                )

                            }
                        }

                        if (allStatusList.size == 1) {
                            binding.spinnerOrderStatus.visibility = View.INVISIBLE
                            binding.tvOrderStatus.visibility = View.VISIBLE
                            binding.tvOrderStatus.text = model.deliveryStatus
                        } else {
                            binding.spinnerOrderStatus.visibility = View.VISIBLE
                            binding.tvOrderStatus.visibility = View.GONE
                        }

                        ArrayAdapter(
                            context, R.layout.single_text_view_spinner_green, allStatusList
                        )
                    }

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.DELIVERED_ORDER, AppConstant.ORDER_CLOSE -> {
                    binding.spinnerOrderStatus.visibility = View.INVISIBLE
                    binding.tvOrderStatus.visibility = View.VISIBLE
                    binding.tvOrderStatus.setBackgroundResource(R.drawable.bg_spinner_green)

                    binding.tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.payment_approved_text_color
                        )
                    )
                    binding.tvOrderStatus.text = model.deliveryStatus
                    allStatusList.add(
                        OrderStatusModel(
                            name = model.deliveryStatus!!,
                            statusColor = MyApplication.instance.getColor(R.color.color_approve)
                        )

                    )

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.ORDER_REJECTED -> {
                    binding.spinnerOrderStatus.visibility = View.INVISIBLE
                    binding.tvOrderStatus.visibility = View.VISIBLE
                    binding.tvOrderStatus.text = AppConstant.ORDER_REJECTED
                    allStatusList.add(
                        OrderStatusModel(
                            name = AppConstant.ORDER_REJECTED,
                            statusColor = MyApplication.instance.getColor(R.color.color_reject)
                        )
                    )



                    if (!model.rejectReason.isNullOrEmpty()) {
                        binding.tvRejectedReason.visibility = View.VISIBLE
                        binding.tvRejectedReason.text = "* " + model.rejectReason

                    }

                    binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                    binding.tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.sale_overview_view_all
                        )
                    )

                    containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    binding.spinnerOrderStatus.layoutParams = containerParams
                }

                else -> {
                    binding.spinnerOrderStatus.visibility = View.INVISIBLE
                    binding.tvOrderStatus.visibility = View.GONE
                    binding.tvRejectedReason.visibility = View.GONE
                }
            }

            binding.spinnerOrderStatus.setOnClickListener {


                val fragment = OrderStatusByBottomSheetDialogFragment.newInstance(
                    allStatusList,
                    this,
                    listener,
                    binding.spinnerOrderStatus.text.toString()
                )

                fragment.show(
                    fragmentManager,
                    OrderStatusByBottomSheetDialogFragment::class.java.name
                )
            }

            itemView.setOnClickListener { listener.onGetOrderInfo(model, position) }

        }


        override fun applyStatus(
            status: String,
            orderStatusActionListener1: OrderStatusActionListener, position: Int
        ) {
            if (AppConstant.getOrderStatus(
                    model1.deliveryStatus!!, AppConstant.FIND_ORDER_VALUE_FROM_KEY
                ) != AppConstant.getOrderStatusForApiFilter(status)
            ) {

                orderStatusActionListener1.onStatusChange(
                    model1, position, status
                )
            }
        }
    }
}