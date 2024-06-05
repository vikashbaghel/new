package com.app.rupyz.sales.payment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemRecordPaymentListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData

class AllRecordPaymentAdapter(
        private var data: ArrayList<RecordPaymentData>,
        private var listener: RecordPaymentActionListener,
        private var hasInternetConnection: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_record_payment_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener, hasInternetConnection)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRecordPaymentListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: RecordPaymentData,
                position: Int,
                listener: RecordPaymentActionListener,
                hasInternetConnection: Boolean
        ) {

            if (model.source.isNullOrEmpty().not()
                    && model.source.equals(AppConstant.ANDROID_OFFLINE_TAG)
                    && model.isSyncedToServer == false) {
                binding.groupOffline.visibility = View.VISIBLE
                binding.clStatus.visibility = View.GONE
            } else {
                binding.groupOffline.visibility = View.GONE
                binding.clStatus.visibility = View.VISIBLE
            }

            binding.tvCustomerName.text = "" + model.customer?.name + ", " + model.customer?.city
            binding.tvPaymentCode.text = DateFormatHelper.dateFormatEMI(model.createdAt)

            if (model.transactionTimeStamp.isNullOrEmpty().not()) {
                binding.tvTransactionDate.text =
                        DateFormatHelper.dateFormatEMI(model.transactionTimeStamp)
            }

            binding.tvOrderBy.text = model.createdBy?.firstName + " " + model.createdBy?.lastName

            binding.tvAmount.text =
                    (CalculatorHelper().convertLargeAmount(model.amount!!, AppConstant.TWO_DECIMAL_POINTS))

            binding.tvPaymentId.text = "Payment ID : " + model.paymentNumber

            binding.tvTransactionId.text = model.transactionRefNo

            binding.tvPaymentMode.text = model.paymentMode

            if (hasInternetConnection.not() || PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELETE_PAYMENT_PERMISSION,
                            false
                    )
                            .not()
            ) {
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
                            listener.onDeletePayment(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }

            when (model.status) {
                AppConstant.STATUS_APPROVED -> {
                    binding.tvStatus.text = AppConstant.STATUS_APPROVED
                    binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                    binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    binding.spinnerPaymentStatus.visibility = View.INVISIBLE
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvRejectedReason.visibility = View.GONE
                }

                AppConstant.STATUS_DISHONOUR -> {
                    binding.tvStatus.text = AppConstant.REJECTED
                    binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_rejected_text_color))
                    binding.tvStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                    binding.spinnerPaymentStatus.visibility = View.INVISIBLE
                    binding.tvStatus.visibility = View.VISIBLE

                    if (!model.rejectReason.isNullOrEmpty()) {
                        binding.tvRejectedReason.visibility = View.VISIBLE
                        binding.tvRejectedReason.text = "* " + model.rejectReason
                    }
                }

                AppConstant.STATUS_PENDING -> {

                    binding.tvStatus.text = AppConstant.STATUS_PENDING
                    if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.PAYMENT_STATUS_UPDATE_PERMISSION, false
                            )
                    ) {
                        binding.spinnerPaymentStatus.visibility = View.VISIBLE
                        binding.tvStatus.visibility = View.GONE
                        binding.tvRejectedReason.visibility = View.GONE

                        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                                itemView.context,
                                R.layout.single_text_view_spinner_green,
                                itemView.context.resources.getStringArray(R.array.payment_status)
                        )

                        arrayAdapter.setDropDownViewResource(R.layout.single_text_view_spinner_16dp_text)

                        binding.spinnerPaymentStatus.adapter = arrayAdapter

                        binding.spinnerPaymentStatus.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                    }

                                    override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View?,
                                            adapterPosition: Int,
                                            id: Long
                                    ) {
                                        if (adapterPosition != 0) {
                                            listener.onStatusChange(
                                                    binding.spinnerPaymentStatus.selectedItem.toString(),
                                                    model,
                                                    position
                                            )
                                        }
                                    }
                                }
                    } else {
                        binding.spinnerPaymentStatus.visibility = View.INVISIBLE
                        binding.tvStatus.visibility = View.VISIBLE
                        binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                        binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    }
                }
            }

            itemView.setOnClickListener { listener.getPaymentInfo(model, position) }
        }
    }


}