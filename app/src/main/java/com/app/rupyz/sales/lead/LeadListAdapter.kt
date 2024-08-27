package com.app.rupyz.sales.lead

import android.annotation.SuppressLint
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
import com.app.rupyz.databinding.LeadListItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.LeadLisDataItem

class LeadListAdapter(
        private var data: ArrayList<LeadLisDataItem>,
        private var listener: ILeadActionListener,
        private var isStaffUser: Boolean,
        private var hasInternet: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.lead_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
                data[position], position, listener, isStaffUser, hasInternet
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LeadListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: LeadLisDataItem,
                position: Int,
                listener: ILeadActionListener,
                isStaffUser: Boolean,
                hasInternet: Boolean
        ) {

            val containerParams =
                    binding.tvRecordActivity.layoutParams as ConstraintLayout.LayoutParams
            containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvRecordActivity.layoutParams = containerParams


            if (model.source.isNullOrEmpty().not()
                    && model.source.equals(AppConstant.ANDROID_OFFLINE_TAG)
                    && model.isSyncedToServer == false) {
                binding.groupOffline.visibility = View.VISIBLE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.tvRecordActivity.layoutParams = containerParams
            } else {
                binding.groupOffline.visibility = View.GONE
                containerParams.topToBottom = binding.tvCreatedOn.id
                containerParams.bottomToBottom = binding.tvCreatedOn.id
                binding.tvRecordActivity.layoutParams = containerParams
            }


            if (model.city.isNullOrEmpty().not()) {
                binding.tvLocation.text = model.city
                binding.tvLocation.visibility = View.VISIBLE

                if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
                    binding.tvLocation.paintFlags =
                        binding.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                } else {
                    binding.tvLocation.paintFlags = binding.tvLocation.paintFlags or Paint.ANTI_ALIAS_FLAG
                }

                binding.tvLocation.setOnClickListener {
                    listener.viewCustomerLocation(model)
                }
            } else {
                binding.tvLocation.visibility = View.INVISIBLE
            }

            if (model.source.isNullOrEmpty()
                            .not() && model.source.equals(AppConstant.STORE_FRONT)
            ) {
                binding.tvStoreFrontView.visibility = View.VISIBLE
                binding.tvStoreFrontView.setOnClickListener {
                    listener.getStoreFrontInfo()
                }
            } else {
                binding.tvStoreFrontView.visibility = View.GONE
            }

            binding.tvCreatedBy.text = itemView.resources.getString(
                    R.string.lead_created_by, model.createdByName
            )

            binding.tvCategory.text = model.leadCategoryName?.replaceFirstChar(
                    Char::titlecase
            )

            binding.tvBusinessName.text = model.businessName?.replaceFirstChar(
                    Char::titlecase
            )

            binding.tvAuthorizePersonName.text = model.contactPersonName

            binding.tvCreatedOn.text = itemView.resources.getString(
                    R.string.lead_created_on, DateFormatHelper.getMonthDate(model.createdAt)
            )

            binding.ivPhoneCall.setOnClickListener {
                listener.onCall(model)
            }

            binding.ivWhatsCall.setOnClickListener {
                listener.onWCall(model)
            }

            itemView.setOnClickListener { listener.getLeadDetails(model, position) }

            if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.EDIT_LEAD_PERMISSION,
                            false
                    ).not()
                    &&
                    PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELETE_LEAD_PERMISSION,
                            false
                    ).not()
            ) {
                binding.ivMore.visibility = View.GONE
            } else {
                binding.ivMore.visibility = View.VISIBLE
            }

            if (hasInternet.not()) {
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
                popup.inflate(R.menu.customer_action_menu)

                if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.EDIT_LEAD_PERMISSION, false).not()
                        || model.status == AppConstant.STATUS_CONVERTED_TO_CUSTOMER
                        || model.status == AppConstant.REJECTED
                ) {
                    if (model.isSyncedToServer == null || model.isSyncedToServer == true) {
                        popup.menu.getItem(0).isVisible = false
                    }
                }

                if (PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_LEAD_PERMISSION, false)
                                .not()
                ) {
                    if (model.isSyncedToServer == null || model.isSyncedToServer == true) {
                        popup.menu.getItem(1).isVisible = false
                    }
                }

                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_product -> {
                            listener.onEditLead(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_inactive_customer -> {
                            listener.onDeleteLead(model, position)
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
                    binding.spinnerLeadStatus.visibility = View.INVISIBLE
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvRecordActivity.visibility = View.VISIBLE
                }

                AppConstant.REJECTED -> {
                    binding.tvStatus.text = AppConstant.REJECTED
                    binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_rejected_text_color))
                    binding.tvStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                    binding.spinnerLeadStatus.visibility = View.INVISIBLE
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvRecordActivity.visibility = View.GONE
                }

                AppConstant.STATUS_CONVERTED_TO_CUSTOMER -> {
                    binding.tvStatus.text = AppConstant.CUSTOMER
                    binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.customer_text_status))
                    binding.tvStatus.setBackgroundResource(R.drawable.customer_status_background)
                    binding.spinnerLeadStatus.visibility = View.INVISIBLE
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvRecordActivity.visibility = View.GONE
                }

                AppConstant.STATUS_PENDING -> {
                    binding.tvRecordActivity.visibility = View.VISIBLE
                    if (hasInternet) {
                        if (isStaffUser) {
                            if (model.createdBy == SharedPref.getInstance()
                                            .getString(AppConstant.USER_ID)
                                            .toInt()
                            ) {
                                if (PermissionModel.INSTANCE.getPermission(
                                                AppConstant.APPROVE_SELF_LEAD_PERMISSION,
                                                false
                                        )
                                ) {
                                    binding.spinnerLeadStatus.visibility = View.VISIBLE
                                    binding.tvStatus.visibility = View.GONE
                                } else {
                                    binding.spinnerLeadStatus.visibility = View.INVISIBLE
                                    binding.tvStatus.visibility = View.VISIBLE
                                    binding.tvStatus.text = AppConstant.PENDING
                                    binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                                    binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                                }
                            } else if (PermissionModel.INSTANCE.getPermission(
                                            AppConstant.APPROVE_LEAD_PERMISSION,
                                            false
                                    )
                            ) {
                                binding.spinnerLeadStatus.visibility = View.VISIBLE
                                binding.tvStatus.visibility = View.GONE
                            } else {
                                binding.spinnerLeadStatus.visibility = View.INVISIBLE
                                binding.tvStatus.visibility = View.VISIBLE
                                binding.tvStatus.text = AppConstant.PENDING
                                binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                                binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                            }
                        } else {
                            binding.spinnerLeadStatus.visibility = View.VISIBLE
                            binding.tvStatus.visibility = View.GONE
                        }
                    } else {
                        binding.spinnerLeadStatus.visibility = View.INVISIBLE
                        binding.tvStatus.visibility = View.VISIBLE
                        binding.tvStatus.text = AppConstant.PENDING
                        binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.payment_approved_text_color))
                        binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    }

                    val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                            itemView.context,
                            R.layout.single_text_view_spinner_green,
                            itemView.context.resources.getStringArray(R.array.payment_status)
                    )

                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.spinnerLeadStatus.adapter = arrayAdapter

                    binding.spinnerLeadStatus.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        adapterPosition: Int,
                                        id: Long
                                ) {
                                    if (binding.spinnerLeadStatus.selectedItem.toString() == AppConstant.APPROVE) {
                                        listener.onApprovedLead(model, position)
                                    } else if (binding.spinnerLeadStatus.selectedItem.toString() == AppConstant.REJECT) {
                                        listener.onRejectedLead(model, position)
                                    }
                                }
                            }
                }

                else -> binding.clStatus.visibility = View.GONE
            }

            binding.tvRecordActivity.setOnClickListener {
                listener.onRecordActivity(model)
            }
        }
    }

    interface ILeadActionListener {
        fun getLeadDetails(model: LeadLisDataItem, position: Int)
        fun onCall(model: LeadLisDataItem)
        fun onWCall(model: LeadLisDataItem)
        fun onEditLead(model: LeadLisDataItem, position: Int)
        fun onApprovedLead(model: LeadLisDataItem, position: Int) {}
        fun onRejectedLead(model: LeadLisDataItem, position: Int) {}
        fun onDeleteLead(model: LeadLisDataItem, position: Int)
        fun onRecordActivity(model: LeadLisDataItem)
        fun getStoreFrontInfo()
        fun viewCustomerLocation(model: LeadLisDataItem)
    }
}