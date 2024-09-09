package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.ItemSalesListAllCustomerBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.dialog.checkIn.CheckInDialogFragment.Companion.customerData
import com.app.rupyz.dialog.checkIn.CheckedInDialogFragment
import com.app.rupyz.dialog.checkIn.ICheckInClickListener
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.invisibleView
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity


class ListOfAllCustomerAdapter(
    private var data: ArrayList<CustomerData>,
    private var listener: CustomerActionListener,
    private val fragmentManager: FragmentManager,
    private var moreOptionEnable: Boolean,
    private var hasInternetConnection: Boolean,
    private var showCheckInButton: Boolean = false,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isAnyUserCheckIn: Boolean = false
    private var checkInUserdata: CustomerData? = null
    private var customerListItemListener: ((CustomerData, LIST_OF_CUSTOMER_ACTIONS) -> Unit)? = null

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
            fragmentManager,
            moreOptionEnable,
            hasInternetConnection,

            )
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSalesListAllCustomerBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bindItem(
            model: CustomerData,
            position: Int,
            listener: CustomerActionListener,
            fragementManager: FragmentManager,
            moreOptionEnable: Boolean,
            hasInternetConnection: Boolean
        ) {

            val containerParams = binding.tvNewOrder.layoutParams as ConstraintLayout.LayoutParams
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

            /* if (model.customerLevel.isNullOrEmpty().not()) {*/
            binding.tvCustomerLevel.visibility = View.VISIBLE
            binding.tvCustomerLevel.text = SharedPref.getInstance().getString(model.customerLevel)
            if (model.customerLevel.isNullOrBlank()) {
                binding.tvCustomerLevel.visibility = View.GONE
            }
            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(
                            itemView.resources.getColor(R.color.customer_level_one_background)
                        )
                    binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_one_text_color))
                    binding.tvDistributorsCount.invisibleView()
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(itemView.resources.getColor(R.color.customer_level_two_background))
                    binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_two_text_color))

                    if ((model.parentsCount ?: 0) > 0) {
                        val string = buildString {
                            append(model.parentsCount)
                            append(" ")
                            append(
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                                    ?: binding.root.context.resources.getString(R.string.primary_customer)
                            )
                        }
                        val spannable = SpannableString(string);
                        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0);

                        binding.tvDistributorsCount.text = spannable
                        binding.tvDistributorsCount.visibility = View.VISIBLE
                    } else {
                        binding.tvDistributorsCount.invisibleView()
                    }

                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    binding.tvCustomerLevel.backgroundTintList =
                        ColorStateList.valueOf(
                            itemView.resources.getColor(R.color.customer_level_three_background)
                        )

                    binding.tvCustomerLevel.setTextColor(itemView.resources.getColor(R.color.customer_level_three_text_color))

                    if ((model.parentsCount ?: 0) > 0) {
                        val string = buildString {
                            append(model.parentsCount)
                            append(" ")
                            append(
                                SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                    ?: binding.root.context.resources.getString(R.string.distributor)
                            )
                        }
                        val spannable = SpannableString(string);
                        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0);

                        binding.tvDistributorsCount.text = spannable
                        binding.tvDistributorsCount.visibility = View.VISIBLE
                    } else {
                        binding.tvDistributorsCount.invisibleView()
                    }
                }
            }
            /* }*/
            /* else {
                 binding.tvCustomerLevel.visibility = View.GONE
             }*/

            if (model.contactPersonName.isNullOrBlank().not()) {
                binding.tvParentCustomerName.text = model.contactPersonName
                binding.tvParentCustomerName.showView()
            } else {
                binding.tvParentCustomerName.invisibleView()
            }

            /*binding.tvParentCustomerName.setOnClickListener {
                listener.getCustomerParentDetails(model, position)
            }*/


            if (model.mobile.isNullOrEmpty().not()) {
                binding.ivPhoneCall.isEnabled = true
                binding.ivPhoneCall.alpha = 1f
            } else {
                binding.ivPhoneCall.isEnabled = false
                binding.ivPhoneCall.alpha = 0.3f
            }

            if (model.city.isNullOrEmpty().not()) {
                binding.tvLocation.text = model.city
                binding.tvLocation.visibility = View.VISIBLE

                if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
                    binding.tvLocation.paintFlags =
                        binding.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                } else {
                    binding.tvLocation.paintFlags =
                        binding.tvLocation.paintFlags or Paint.ANTI_ALIAS_FLAG
                }

                binding.tvLocation.setOnClickListener {
                    listener.viewCustomerLocation(model)
                }
            } else {
                binding.tvLocation.visibility = View.GONE
            }


            val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
            val isTelephonicEnabled =
                SharedPref.getInstance().getBoolean(AppConstant.TELEPHONIC_ORDER, false)

            if (checkSetting) {
                if (model.checkInTime.isNullOrBlank().not()) {
                    isAnyUserCheckIn = true
                    checkInUserdata = model
                    binding.tvNewOrder.visibility = View.GONE
                    binding.btnActivity.gone()
                    binding.btnCheckedIn.visibility()
                } else {
                    binding.btnCheckedIn.hideView()
                    binding.tvNewOrder.visibility = View.VISIBLE
                    binding.btnActivity.visibility = View.GONE
                    if (isTelephonicEnabled) {
                        binding.view.showView()
                        binding.imgPop.showView()
                    } else {
                        binding.view.hideView()
                        binding.imgPop.hideView()
                    }
                }
            } else {
                binding.btnActivity.visibility = View.VISIBLE
                binding.tvNewOrder.visibility = View.GONE
            }




            binding.tvDistributorsCount.setSafeOnClickListener {
                openDistributorList(model, position)
            }

            binding.btnActivity.setOnClickListener {
                customerListItemListener?.invoke(model, LIST_OF_CUSTOMER_ACTIONS.CHOSE_ACTIVITY)
            }

            binding.tvNewOrder.setOnClickListener {
                customerListItemListener?.invoke(model, LIST_OF_CUSTOMER_ACTIONS.CHECK_IN)
            }

            binding.btnCheckedIn.setOnClickListener {
                listener.onGetCustomerInfo(model, false)
            }

            binding.imgPop.setOnClickListener {

                //creating a popup menu
                val popup =
                    PopupMenu(it.context, binding.tvNewOrder, Gravity.CENTER)
                //inflating menu from xml resource
                popup.inflate(R.menu.check_in_menu)


                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.menu_check_in -> {
                            customerListItemListener?.invoke(
                                model,
                                LIST_OF_CUSTOMER_ACTIONS.CHECK_IN
                            )
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_telephonic -> {
                            if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.CREATE_ORDER_PERMISSION,
                                    false
                                )
                            ) {

                                if (isAnyUserCheckIn.not()) {
                                    SharedPref.getInstance().clearCart()
                                    if (SharedPref.getInstance()
                                            .getBoolean(CUSTOMER_LEVEL_ORDER, false)
                                    ) {
                                        val selectedStep = model.customerLevel?.let {
                                            when (it) {
                                                AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                                                AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                                                AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                                                else -> null
                                            }
                                        }
                                        if ((selectedStep == null) || selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) {

                                            listener.createTelephonicOrder(
                                                model,
                                                model.parentsCount != 1
                                            )
                                            /*binding.root.context.startActivity(
                                                    Intent(
                                                            binding.root.context,
                                                            CustomerDetailActivity::class.java
                                                          )
                                                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                                            .putExtra(
                                                                    AppConstant.CUSTOMER_TYPE,
                                                                    model.customerLevel
                                                                     )
                                                            .putExtra(AppConstant.DISTRIBUTOR_SELECTOR, true)
                                                            .putExtra(AppConstant.IS_TELEPHONIC_ORDER, true)
                                                            .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
                                                                              )*/
                                        } else {
                                            customerData.let { customerData ->
                                                binding.root.context.startActivity(
                                                    Intent(
                                                        binding.root.context,
                                                        CreateNewOrderForCustomerActivity::class.java
                                                    )
                                                        .putExtra(AppConstant.CUSTOMER, model)
                                                        .putExtra(
                                                            AppConstant.CUSTOMER_NAME,
                                                            model.name
                                                        )
                                                        .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                                        .putExtra(
                                                            AppConstant.PAYMENT_INFO,
                                                            model.paymentTerm
                                                        )
                                                        .putExtra(
                                                            AppConstant.IS_TELEPHONIC_ORDER,
                                                            true
                                                        )
                                                )
                                            }
                                        }
                                    } else {
                                        customerData.let { customerData ->
                                            binding.root.context.startActivity(
                                                Intent(
                                                    binding.root.context,
                                                    CreateNewOrderForCustomerActivity::class.java
                                                )
                                                    .putExtra(AppConstant.CUSTOMER, model)
                                                    .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                                                    .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                                    .putExtra(
                                                        AppConstant.PAYMENT_INFO,
                                                        model.paymentTerm
                                                    )
                                                    .putExtra(AppConstant.IS_TELEPHONIC_ORDER, true)
                                            )
                                        }
                                    }
                                } else {
                                    checkInUserdata?.let { checkInUserdata ->
                                        val fragment = CheckedInDialogFragment.getInstance(
                                            checkInUserdata.id ?: 0, buildString {
                                                append(binding.root.context.resources.getString(R.string.you_are_check_in_at))
                                                append(" ")
                                                append(checkInUserdata.name)
                                            }, object : ICheckInClickListener {
                                                override fun onConfirm(ckRequest: CheckInRequest) {
                                                    customerListItemListener?.invoke(
                                                        model,
                                                        LIST_OF_CUSTOMER_ACTIONS.RELOAD
                                                    )
                                                }

                                            })
                                        fragment.show(
                                            fragementManager,
                                            DeleteDialogFragment::class.java.name
                                        )
                                    }
                                }


                            } else {
                                Toast.makeText(
                                    MyApplication.instance,
                                    MyApplication.instance.getString(R.string.create_order_permission),
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }


            if (hasInternetConnection
                && moreOptionEnable
                && PermissionModel.INSTANCE.getPermission(
                    AppConstant.EDIT_CUSTOMER_PERMISSION,
                    false
                )
                || hasInternetConnection
                && moreOptionEnable
                && PermissionModel.INSTANCE.getPermission(
                    AppConstant.DELETE_CUSTOMER_PERMISSION,
                    false
                )
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

            binding.ivCustomer.setOnClickListener {
                listener.viewCustomerPhoto(model)
            }

            itemView.setOnClickListener { listener.onGetCustomerInfo(model, false) }

            if (showCheckInButton.not()) {
                binding.tvNewOrder.hideView()
                binding.btnActivity.hideView()
                binding.btnCheckedIn.hideView()
            }

        }

        fun openDistributorList(data: CustomerData, i: Int) {
            val level = data.customerLevel?.let {
                when (it) {
                    AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                    AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                    AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                    else -> null
                }
            }
            data.id?.let { id ->
                level?.let { level ->
                    val distributorListDialog = DistributorListDialog.getInstance(
                        customerId = id,
                        customerLevel = level,
                        onDistributorSelected = object : (CustomerData) -> Unit {
                            override fun invoke(model: CustomerData) {
                                listener.onGetCustomerInfo(model, false)
                            }
                        })
                    fragmentManager.let { fragmentManager ->
                        distributorListDialog.show(
                            fragmentManager,
                            DistributorListDialog::class.java.name
                        )
                    }
                }
            }


        }
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun setOnOptionClickListener(listener: (CustomerData, LIST_OF_CUSTOMER_ACTIONS) -> Unit) {
        customerListItemListener = listener
    }

    enum class LIST_OF_CUSTOMER_ACTIONS { CHOSE_ACTIVITY, CHECK_IN, RELOAD }
}