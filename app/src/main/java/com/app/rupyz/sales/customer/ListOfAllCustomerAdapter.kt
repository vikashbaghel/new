package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSalesListAllCustomerBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.customer.CustomerData

class ListOfAllCustomerAdapter(
        private var data: ArrayList<CustomerData>,
        private var listener: CustomerActionListener,
        private var moreOptionEnable: Boolean,
        private var hasInternetConnection: Boolean
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sales_list_all_customer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
                data[position],
                position,
                listener,
                moreOptionEnable,
                hasInternetConnection
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSalesListAllCustomerBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: CustomerData,
                position: Int,
                listener: CustomerActionListener,
                moreOptionEnable: Boolean,
                hasInternetConnection: Boolean
        ) {

            val containerParams =
                    binding.tvNewOrder.layoutParams as ConstraintLayout.LayoutParams
            containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvNewOrder.layoutParams = containerParams

            binding.tvCustomerName.text = model.name?.replaceFirstChar(Char::titlecase)

            if (model.logoImageUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.logoImageUrl, binding.ivCustomer)
            } else {
                binding.ivCustomer.setImageResource(R.mipmap.ic_no_customer_found)
            }

            if (model.isSyncedToServer == false) {
                binding.groupOffline.visibility = View.VISIBLE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                binding.tvNewOrder.layoutParams = containerParams

                if (model.errorMessage.isNullOrEmpty().not()) {
                    binding.tvOfflineErrorMessage.visibility = View.VISIBLE
                    binding.tvOfflineErrorMessage.text = model.errorMessage
                } else {
                    binding.tvOfflineErrorMessage.visibility = View.GONE
                }
            } else {
                binding.groupOffline.visibility = View.GONE
                containerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                binding.tvNewOrder.layoutParams = containerParams
            }

            if (model.customerLevel.isNullOrEmpty().not()) {
                binding.tvCustomerLevel.visibility = View.VISIBLE
                binding.tvCustomerLevel.text =
                        SharedPref.getInstance().getString(model.customerLevel)

                when (model.customerLevel) {
                    AppConstant.CUSTOMER_LEVEL_1 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(
                                        itemView.resources.getColor(R.color.customer_level_one_background)
                                )
                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_one_text_color))
                        binding.tvParentCustomerName.visibility = View.GONE
                    }

                    AppConstant.CUSTOMER_LEVEL_2 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(itemView.resources.getColor(R.color.customer_level_two_background))
                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_two_text_color))

                        if (model.customerParentName.isNullOrEmpty().not()) {
                            val spannable = SpannableString(
                                    "${
                                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                                    } : ${model.customerParentName}"
                            )

                            val start = spannable.length - model.customerParentName?.length!!

                            spannable.setSpan(
                                    ForegroundColorSpan(itemView.resources.getColor(R.color.theme_purple)),
                                    start, // start
                                    spannable.length, // end
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )

                            spannable.setSpan(
                                    UnderlineSpan(),
                                    start, // start
                                    spannable.length, // end
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )

                            binding.tvParentCustomerName.text = spannable
                            binding.tvParentCustomerName.visibility = View.VISIBLE
                        } else {
                            binding.tvParentCustomerName.visibility = View.GONE
                        }
                    }

                    AppConstant.CUSTOMER_LEVEL_3 -> {
                        binding.tvCustomerLevel.backgroundTintList =
                                ColorStateList.valueOf(
                                        itemView.resources.getColor(R.color.customer_level_three_background)
                                )

                        binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_three_text_color))

                        if (model.customerParentName.isNullOrEmpty().not()) {
                            val spannable = SpannableString(
                                    "${
                                        SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                    } : ${model.customerParentName}"
                            )

                            val start = spannable.length - model.customerParentName?.length!!

                            spannable.setSpan(
                                    ForegroundColorSpan(itemView.resources.getColor(R.color.theme_purple)),
                                    start, // start
                                    spannable.length, // end
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )

                            spannable.setSpan(
                                    UnderlineSpan(),
                                    start, // start
                                    spannable.length, // end
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )

                            binding.tvParentCustomerName.text = spannable
                            binding.tvParentCustomerName.visibility = View.VISIBLE
                        } else {
                            binding.tvParentCustomerName.visibility = View.GONE
                        }
                    }
                }
            } else {
                binding.tvCustomerLevel.visibility = View.GONE
            }

            binding.tvParentCustomerName.setOnClickListener {
                listener.getCustomerParentDetails(model, position)
            }

            if (!model.contactPersonName.isNullOrEmpty()) {
                binding.tvAuthorizePersonName.text =
                        model.contactPersonName?.replaceFirstChar(Char::titlecase)
                binding.tvAuthorizePersonName.visibility = View.VISIBLE
            } else {
                binding.tvAuthorizePersonName.visibility = View.GONE
            }

            if (model.mobile.isNullOrEmpty().not()) {
                binding.ivPhoneCall.isEnabled = true
                binding.ivPhoneCall.alpha = 1f
                binding.ivWhatsCall.isEnabled = true
                binding.ivWhatsCall.alpha = 1f
            } else {
                binding.ivPhoneCall.isEnabled = false
                binding.ivPhoneCall.alpha = 0.3f
                binding.ivWhatsCall.isEnabled = false
                binding.ivWhatsCall.alpha = 0.3f
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
                binding.tvLocation.visibility = View.GONE
            }

            binding.tvNewOrder.setOnClickListener {
                listener.onNewOrder(
                        model,
                        position
                )
            }

            if (hasInternetConnection
                    && moreOptionEnable
                    && PermissionModel.INSTANCE.getPermission(AppConstant.EDIT_CUSTOMER_PERMISSION, false)
                    || hasInternetConnection
                    && moreOptionEnable
                    && PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_CUSTOMER_PERMISSION, false)
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
                val popup =
                        PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.customer_action_menu)

                if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.EDIT_CUSTOMER_PERMISSION,
                                false
                        ).not()
                ) {
                    popup.menu.getItem(0).isVisible = false
                }

                if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.DELETE_CUSTOMER_PERMISSION,
                                false
                        ).not()
                ) {
                    popup.menu.getItem(1).isVisible = false
                }

                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_product -> {
                            listener.onEdit(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_inactive_customer -> {
                            listener.onInActiveCustomer(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }

            binding.ivPhoneCall.setOnClickListener {
                listener.onCall(
                        model,
                        position
                )
            }
            binding.ivWhatsCall.setOnClickListener {
                listener.onWCall(
                        model,
                        position
                )
            }

            binding.ivCustomer.setOnClickListener {
                listener.viewCustomerPhoto(model)
            }

            itemView.setOnClickListener { listener.onGetCustomerInfo(model) }

            binding.tvRecordActivity.setOnClickListener { listener.recordCustomerActivity(model) }

        }
    }
}