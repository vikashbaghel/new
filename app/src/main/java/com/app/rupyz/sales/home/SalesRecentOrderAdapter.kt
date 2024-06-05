package com.app.rupyz.sales.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSalesRecentOrderBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.order.order_history.OrderData

class SalesRecentOrderAdapter(
        private var data: ArrayList<OrderData>,
        private var mContext: Context,
        private var listener: OrderStatusActionListener,
        private var hasInternetConnection: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sales_recent_order, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, mContext, listener, hasInternetConnection)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSalesRecentOrderBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: OrderData, position: Int, context: Context, listener: OrderStatusActionListener,
                hasInternetConnection: Boolean
        ) {

            val containerParams =
                    binding.spinnerOrderStatus.layoutParams as ConstraintLayout.LayoutParams
            containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            binding.spinnerOrderStatus.layoutParams = containerParams

            binding.tvOrderId.text = "Order ID : " + model.orderId

            binding.tvOrderName.text =
                    model.customer?.name?.replaceFirstChar(Char::titlecase) + ", " + model.customer?.city?.replaceFirstChar(
                            Char::titlecase
                    )

            if (model.source.isNullOrEmpty()
                            .not() && model.source.equals(AppConstant.STORE_FRONT)
            ) {
                binding.groupStoreFront.visibility = View.VISIBLE
                binding.tvStoreFrontView.setOnClickListener {
                    listener.getStoreFrontInfo()
                }
            } else {
                binding.groupStoreFront.visibility = View.GONE
            }

            if (model.source.isNullOrEmpty().not() && model.source.equals(AppConstant.ANDROID_OFFLINE_TAG)
                    && model.isSyncedToServer == false) {
                binding.groupOffline.visibility = View.VISIBLE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.spinnerOrderStatus.layoutParams = containerParams
            } else {
                binding.groupOffline.visibility = View.GONE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.spinnerOrderStatus.layoutParams = containerParams
            }

            binding.tvTime.text = DateFormatHelper.getOrderDate(model.createdAt)

            binding.tvOrderPrice.text =
                    CalculatorHelper().convertLargeAmount(model.totalAmount
                            ?: 0.0, AppConstant.TWO_DECIMAL_POINTS)

            if (model.orderStatusChange != null && !model.orderStatusChange!!) {
                binding.spinnerOrderStatus.visibility = View.GONE
                binding.tvOrderStatus.visibility = View.VISIBLE
                binding.tvOrderStatus.text = model.deliveryStatus

            } else {
                binding.spinnerOrderStatus.visibility = View.VISIBLE
                binding.tvOrderStatus.visibility = View.GONE
            }

            if (model.paymentOptionCheck.isNullOrEmpty().not()) {
                binding.tvPaymentMode.visibility = View.VISIBLE
                if (model.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                    binding.tvPaymentMode.text = "Credit Days " + model.remainingPaymentDays
                } else {
                    when (model.paymentOptionCheck) {
                        AppConstant.FULL_PAYMENT_IN_ADVANCE_API -> binding.tvPaymentMode.text =
                                AppConstant.FULL_PAYMENT_IN_ADVANCE

                        AppConstant.PARTIAL_PAYMENT_API -> binding.tvPaymentMode.text =
                                AppConstant.PARTIAL_PAYMENT

                        AppConstant.PAYMENT_ON_DELIVERY_API -> binding.tvPaymentMode.text =
                                AppConstant.PAYMENT_ON_DELIVERY
                    }
                }

                binding.tvPaymentMode.paintFlags =
                        binding.tvPaymentMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                binding.tvPaymentMode.visibility = View.GONE
            }

            if (hasInternetConnection &&
                    PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_ORDER_PERMISSION, false)) {
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


            binding.tvCreatedBy.text = model.createdBy?.firstName + " " + model.createdBy?.lastName

            if (model.fullFilledBy?.name != null) {
                binding.tvFullFiledBy.text = "${model.fullFilledBy?.name}"
                binding.groupFullFilledBy.visibility = View.VISIBLE
            } else {
                binding.groupFullFilledBy.visibility = View.GONE
            }

            var arrayAdapter: ArrayAdapter<String>? = null

            if (model.isClosed == true) {
                binding.tvClose.visibility = View.VISIBLE
            } else {
                binding.tvClose.visibility = View.GONE
            }


            when (model.deliveryStatus) {
                AppConstant.RECEIVED_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add(AppConstant.RECEIVED_ORDER)

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.APPROVE_ORDER_PERMISSION, false
                                )
                        ) {
                            allStatusList.add("Approve")
                        }

                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.REJECT_ORDER_PERMISSION, false
                                )
                        ) {
                            allStatusList.add("Reject")
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

                    arrayAdapter = ArrayAdapter(
                            context, R.layout.single_text_view_spinner_green, allStatusList
                    )


                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.APPROVED_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()

                    allStatusList.add(AppConstant.APPROVED)

                    if (hasInternetConnection) {
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

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }

                    arrayAdapter = ArrayAdapter(
                            context, R.layout.single_text_view_spinner_green, allStatusList
                    )

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.PROCESSING_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add("Processing")

                    if (hasInternetConnection) {
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

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility =
                                View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = model.deliveryStatus
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }

                    arrayAdapter = ArrayAdapter(
                            context, R.layout.single_text_view_spinner_green, allStatusList
                    )

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.READY_TO_DISPATCH_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add("Ready To Dispatch")

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.DISPATCH_ORDER_PERMISSION, false
                                )
                        ) {
                            allStatusList.add("Dispatch")
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

                    arrayAdapter = ArrayAdapter(
                            context, R.layout.single_text_view_spinner_green, allStatusList
                    )

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.SHIPPED_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add("Dispatched")

                    if (hasInternetConnection) {
                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.DELIVER_ORDER_PERMISSION, false
                                )
                        ) {
                            allStatusList.add("Delivered")
                        }
                    }

                    if (allStatusList.size == 1) {
                        binding.spinnerOrderStatus.visibility = View.INVISIBLE
                        binding.tvOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.text = AppConstant.getOrderStatus(
                                model.deliveryStatus!!, AppConstant.FIND_ORDER_VALUE_FROM_KEY
                        )
                        binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_approved_background)
                        binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                    } else {
                        binding.spinnerOrderStatus.visibility = View.VISIBLE
                        binding.tvOrderStatus.visibility = View.GONE
                    }

                    arrayAdapter = ArrayAdapter(
                            context,
                            R.layout.single_text_view_spinner_green,
                            context.resources.getStringArray(R.array.order_status_for_shipped)
                    )

                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.PARTIAL_SHIPPED_ORDER -> {
                    val allStatusList: MutableList<String> = mutableListOf()
                    allStatusList.add("Partial Dispatched")

                    binding.spinnerOrderStatus.visibility = View.VISIBLE

                    arrayAdapter = if (model.isClosed == true) {

                        if (hasInternetConnection && PermissionModel.INSTANCE.getPermission(
                                        AppConstant.DELIVER_ORDER_PERMISSION, false
                                )
                        ) {
                            allStatusList.add("Delivered")
                            binding.spinnerOrderStatus.visibility = View.VISIBLE
                            binding.tvOrderStatus.visibility = View.GONE

                        } else {
                            binding.tvOrderStatus.text = AppConstant.getOrderStatus(
                                    model.deliveryStatus!!, AppConstant.FIND_ORDER_KEY_FROM_VALUE
                            )
                            binding.spinnerOrderStatus.visibility = View.INVISIBLE
                            binding.tvOrderStatus.visibility = View.VISIBLE
                            binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_approved_background)
                            binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
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
                    binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                    binding.tvOrderStatus.text = model.deliveryStatus
                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.ORDER_REJECTED -> {
                    binding.spinnerOrderStatus.visibility = View.INVISIBLE
                    binding.tvOrderStatus.visibility = View.VISIBLE
                    binding.tvOrderStatus.text = AppConstant.ORDER_REJECTED

                    if (!model.rejectReason.isNullOrEmpty()) {
                        binding.tvRejectedReason.visibility = View.VISIBLE
                        binding.tvRejectedReason.text = "* " + model.rejectReason
                    }

                    binding.tvOrderStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                    binding.tvOrderStatus.setTextColor(itemView.context.resources.getColor(R.color.sale_overview_view_all))

                    containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    binding.spinnerOrderStatus.layoutParams = containerParams
                }

                else -> {
                    binding.spinnerOrderStatus.visibility = View.INVISIBLE
                    binding.tvOrderStatus.visibility = View.GONE
                    binding.tvRejectedReason.visibility = View.GONE
                }
            }

            binding.spinnerOrderStatus.adapter = arrayAdapter
            arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            itemView.setOnClickListener { listener.onGetOrderInfo(model, position) }

            binding.spinnerOrderStatus.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, arrayAdapterposition: Int, id: Long
                        ) {
                            if (AppConstant.getOrderStatus(
                                            model.deliveryStatus!!, AppConstant.FIND_ORDER_VALUE_FROM_KEY
                                    ) != AppConstant.getOrderStatusForApiFilter(binding.spinnerOrderStatus.selectedItem.toString())
                            ) {

                                listener.onStatusChange(
                                        model, position, binding.spinnerOrderStatus.selectedItem.toString()
                                )
                            }
                        }
                    }

        }
    }
}