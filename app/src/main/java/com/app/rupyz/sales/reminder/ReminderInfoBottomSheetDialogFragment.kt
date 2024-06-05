package com.app.rupyz.sales.reminder

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetReminderInfoLayoutBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.ReminderItemModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.lead.AllLeadListActivity
import com.app.rupyz.sales.lead.LeadApproveOrRejectedBottomSheetDialog
import com.app.rupyz.sales.lead.LeadViewModel
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReminderInfoBottomSheetDialogFragment : BottomSheetDialogFragment(),
        ProductImageViewPagerAdapter.ProductImageClickListener,
        LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener {
    private lateinit var binding: BottomSheetReminderInfoLayoutBinding

    private val customerViewModel: CustomerViewModel by viewModels()
    private val leadViewModel: LeadViewModel by viewModels()

    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private var leadActionModel: LeadLisDataItem? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetReminderInfoLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private var reminderModel: ReminderItemModel? = null
        private var listener: IRemindersListener? = null
        fun newInstance(
                reminderItemModel: ReminderItemModel,
                listener: IRemindersListener
        ): ReminderInfoBottomSheetDialogFragment {
            this.reminderModel = reminderItemModel
            this.listener = listener
            return ReminderInfoBottomSheetDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initRecyclerView()

        if (reminderModel?.moduleType.isNullOrEmpty().not() && reminderModel?.moduleId != null) {

            binding.shimmerCustomer.visibility = View.VISIBLE

            if (reminderModel!!.moduleType.equals(AppConstant.CUSTOMER_FEEDBACK)) {
                if (PermissionModel.INSTANCE.getPermission(
                                AppConstant.GET_CUSTOMER_PERMISSION,
                                false
                        )
                ) {
                    customerViewModel.getCustomerById(
                            reminderModel?.moduleId!!,
                            hasInternetConnection
                    )
                }
            } else if (reminderModel!!.moduleType.equals(AppConstant.LEAD_FEEDBACK)) {
                if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_LEAD_PERMISSION, false)) {
                    leadViewModel.getLeadDetail(leadId = reminderModel?.moduleId!!, hasInternetConnection)
                }
            }
        }

        var label = ""

        when (reminderModel?.moduleType) {

            AppConstant.CUSTOMER_FEEDBACK -> {
                label = "${AppConstant.CUSTOMER}  - ${reminderModel?.feedbackType}"
            }

            AppConstant.LEAD_FEEDBACK -> {
                label = "${AppConstant.LEAD} - ${reminderModel?.feedbackType}"
            }

            AppConstant.ORDER_DISPATCH -> {
                label = "${AppConstant.CUSTOMER}  - ${reminderModel?.feedbackType}"
            }

            AppConstant.PAYMENT -> {
                label = "${AppConstant.CUSTOMER}  - ${reminderModel?.feedbackType}"
            }

            else -> {
                label = "${reminderModel?.feedbackType}"
            }
        }

        if (reminderModel?.picsUrls.isNullOrEmpty().not()) {
            pics.addAll(reminderModel?.picsUrls!!)
            addPhotoListAdapter.notifyDataSetChanged()
            binding.groupImages.visibility = View.VISIBLE
        } else {
            binding.groupImages.visibility = View.GONE
        }

        binding.tvActivityType.text = label

        if (reminderModel?.comments.isNullOrEmpty().not()) {
            binding.groupComments.visibility = View.VISIBLE
            binding.tvComment.text = reminderModel?.comments
        } else {
            binding.groupComments.visibility = View.GONE
        }

        binding.tvTime.text = DateFormatHelper.convertDateToTimeFormat(reminderModel?.dueDatetime)

        binding.ivMoreReminderAction.setOnClickListener {
            //creating a popup menu
            val popup =
                    PopupMenu(requireContext(), binding.ivMoreReminderAction)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_delete)

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.delete_product -> {
                        listener?.onDeleteReminder(reminderModel)
                        dismiss()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.edit_product -> {
                        listener?.onEditReminder(reminderModel)
                        dismiss()
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    val hasInternetConnection: Boolean
        get() {
            return Connectivity.hasInternetConnection(requireContext())
        }

    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 3)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter
    }

    private fun initObservers() {
        leadViewModel.leadDetailLiveData.observe(this) {
            binding.shimmerCustomer.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { model ->
                    initLeadData(model)
                }
            }
        }

        customerViewModel.getCustomerByIdData().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { model ->
                    initCustomerData(model)
                }
            } else {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
            }
        }

        leadViewModel.approveRejectLeadLiveData.observe(this) {
            Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                initLeadStatus(it.data)
            }
        }
    }

    private fun initLeadData(model: LeadLisDataItem) {
        leadActionModel = model
        if (model.city.isNullOrEmpty().not()) {
            binding.clLeadDetails.tvLocation.text = model.city
            binding.clLeadDetails.tvLocation.visibility = View.VISIBLE

            binding.clLeadDetails.tvLocation.paintFlags =
                    binding.clLeadDetails.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            binding.clLeadDetails.tvLocation.setOnClickListener {
                viewCustomerLocation(
                        model.mapLocationLat,
                        model.mapLocationLong,
                        model.businessName
                )
            }
        } else {
            binding.clLeadDetails.tvLocation.visibility = View.GONE
        }

        if (model.source.isNullOrEmpty()
                        .not() && model.source.equals(AppConstant.STORE_FRONT)
        ) {
            binding.clLeadDetails.tvStoreFrontView.visibility = View.VISIBLE
            binding.clLeadDetails.tvStoreFrontView.setOnClickListener {
                val fragment = InfoBottomSheetDialogFragment()
                val bundle = Bundle()
                bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_lead))
                bundle.putString(
                        AppConstant.MESSAGE,
                        resources.getString(R.string.storefront_lead_message)
                )
                fragment.arguments = bundle
                fragment.show(childFragmentManager, AppConstant.STORE_FRONT)
            }
        } else {
            binding.clLeadDetails.tvStoreFrontView.visibility = View.GONE
        }

        binding.clLeadDetails.tvCreatedBy.text = requireContext().getString(
                R.string.lead_created_by, model.createdByName
        )

        binding.clLeadDetails.tvCategory.text = model.leadCategoryName?.replaceFirstChar(
                Char::titlecase
        )

        binding.clLeadDetails.tvBusinessName.text = model.businessName?.replaceFirstChar(
                Char::titlecase
        )

        binding.clLeadDetails.tvAuthorizePersonName.text = model.contactPersonName

        binding.clLeadDetails.tvCreatedOn.text = requireContext().getString(
                R.string.lead_created_on, DateFormatHelper.getMonthDate(model.createdAt)
        )

        binding.clLeadDetails.ivPhoneCall.setOnClickListener {
            onCall(model.mobile)
        }

        binding.clLeadDetails.ivWhatsCall.setOnClickListener {
            onWCall(model.mobile, model.businessName)
        }

        binding.clLeadDetails.ivMore.visibility = View.GONE

        initLeadStatus(model)

        binding.clLeadDetails.tvRecordActivity.setOnClickListener {
            if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
                startActivity(
                        Intent(
                                requireContext(),
                                CustomFormActivity::class.java
                        ).putExtra(AppConstant.CUSTOMER_ID, model.id)
                                .putExtra(AppConstant.LEAD, model)
                                .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.LEAD_FEEDBACK)
                )
            } else {
                Toast.makeText(requireContext(),
                        resources.getString(R.string.you_dont_have_permission_to_perform_this_action),
                        Toast.LENGTH_SHORT).show()
            }
        }

        binding.clLeadDetails.root.visibility = View.VISIBLE
    }

    private fun initLeadStatus(model: LeadLisDataItem?) {
        when (model?.status) {
            AppConstant.STATUS_APPROVED -> {
                binding.clLeadDetails.tvStatus.text = AppConstant.STATUS_APPROVED
                binding.clLeadDetails.tvStatus.setTextColor(requireContext().resources.getColor(R.color.payment_approved_text_color))
                binding.clLeadDetails.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                binding.clLeadDetails.spinnerLeadStatus.visibility = View.INVISIBLE
                binding.clLeadDetails.tvStatus.visibility = View.VISIBLE
            }

            AppConstant.REJECTED -> {
                binding.clLeadDetails.tvStatus.text = AppConstant.REJECTED
                binding.clLeadDetails.tvStatus.setTextColor(requireContext().resources.getColor(R.color.payment_rejected_text_color))
                binding.clLeadDetails.tvStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                binding.clLeadDetails.spinnerLeadStatus.visibility = View.INVISIBLE
                binding.clLeadDetails.tvStatus.visibility = View.VISIBLE
            }

            AppConstant.STATUS_CONVERTED_TO_CUSTOMER -> {
                binding.clLeadDetails.tvStatus.text = AppConstant.CUSTOMER
                binding.clLeadDetails.tvStatus.setTextColor(requireContext().resources.getColor(R.color.customer_text_status))
                binding.clLeadDetails.tvStatus.setBackgroundResource(R.drawable.customer_status_background)
                binding.clLeadDetails.spinnerLeadStatus.visibility = View.INVISIBLE
                binding.clLeadDetails.tvStatus.visibility = View.VISIBLE
            }

            AppConstant.STATUS_PENDING -> {

                if (SharedPref.getInstance()
                                .getString(AppConstant.APP_ACCESS_TYPE) == AppConstant.ACCESS_TYPE_STAFF
                ) {
                    if (model.createdBy == SharedPref.getInstance()
                                    .getString(AppConstant.USER_ID)
                                    .toInt()
                    ) {
                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.APPROVE_SELF_LEAD_PERMISSION,
                                        false
                                )
                        ) {
                            binding.clLeadDetails.spinnerLeadStatus.visibility = View.VISIBLE
                            binding.clLeadDetails.tvStatus.visibility = View.GONE
                        } else {
                            binding.clLeadDetails.spinnerLeadStatus.visibility = View.INVISIBLE
                            binding.clLeadDetails.tvStatus.visibility = View.VISIBLE
                            binding.clLeadDetails.tvStatus.text = AppConstant.PENDING
                            binding.clLeadDetails.tvStatus.setTextColor(
                                    requireContext().resources.getColor(
                                            R.color.payment_approved_text_color
                                    )
                            )
                            binding.clLeadDetails.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                        }
                    } else if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.APPROVE_LEAD_PERMISSION,
                                    false
                            )
                    ) {
                        binding.clLeadDetails.spinnerLeadStatus.visibility = View.VISIBLE
                        binding.clLeadDetails.tvStatus.visibility = View.GONE
                    } else {
                        binding.clLeadDetails.spinnerLeadStatus.visibility = View.INVISIBLE
                        binding.clLeadDetails.tvStatus.visibility = View.VISIBLE
                        binding.clLeadDetails.tvStatus.text = AppConstant.PENDING
                        binding.clLeadDetails.tvStatus.setTextColor(
                                requireContext().resources.getColor(
                                        R.color.payment_approved_text_color
                                )
                        )
                        binding.clLeadDetails.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                    }
                } else {
                    binding.clLeadDetails.spinnerLeadStatus.visibility = View.VISIBLE
                    binding.clLeadDetails.tvStatus.visibility = View.GONE
                }

                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        R.layout.single_text_view_spinner_green,
                        requireContext().resources.getStringArray(R.array.payment_status)
                )

                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.clLeadDetails.spinnerLeadStatus.adapter = arrayAdapter

                binding.clLeadDetails.spinnerLeadStatus.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    adapter_position: Int,
                                    id: Long
                            ) {
                                if (binding.clLeadDetails.spinnerLeadStatus.selectedItem.toString()
                                        == AppConstant.APPROVED_ORDER
                                ) {
                                    onApprovedLead(model)
                                } else if (binding.clLeadDetails.spinnerLeadStatus.selectedItem.toString()
                                        == AppConstant.STATUS_DISHONOUR
                                ) {
                                    onRejectedLead(model)
                                }
                            }
                        }
            }
        }
    }

    private fun initCustomerData(model: CustomerData) {
        binding.clCustomerDetails.tvCustomerName.text =
                model.name?.replaceFirstChar(Char::titlecase)

        if (model.logoImageUrl.isNullOrEmpty().not()) {
            ImageUtils.loadImage(model.logoImageUrl, binding.clCustomerDetails.ivCustomer)

            binding.clCustomerDetails.ivCustomer.setOnClickListener {
                viewCustomerPhoto(model)
            }
        }

        if (model.customerLevel.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvCustomerLevel.text =
                    SharedPref.getInstance().getString(model.customerLevel)
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.VISIBLE

            var customerSubLevelName = ""
            var customerSubLevelCount = 0
            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    requireContext().getColor(R.color.customer_level_one_background)
                            )
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(
                            requireContext().getColor(
                                    R.color.customer_level_one_text_color
                            )
                    )
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(requireContext().getColor(R.color.customer_level_two_background))
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(
                            requireContext().getColor(
                                    R.color.customer_level_two_text_color
                            )
                    )

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                                "${
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                                } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

                        spannable.setSpan(
                                ForegroundColorSpan(requireContext().getColor(R.color.theme_purple)),
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

                        binding.clCustomerDetails.tvParentCustomerName.text = spannable
                        binding.clCustomerDetails.tvParentCustomerName.visibility = View.VISIBLE
                    }
                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    requireContext().getColor(R.color.customer_level_three_background)
                            )

                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(
                            requireContext().getColor(
                                    R.color.customer_level_three_text_color
                            )
                    )

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                                "${
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

                        spannable.setSpan(
                                ForegroundColorSpan(requireContext().getColor(R.color.theme_purple)),
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

                        binding.clCustomerDetails.tvParentCustomerName.text = spannable
                        binding.clCustomerDetails.tvParentCustomerName.visibility = View.VISIBLE
                    }
                }
            }

        } else {
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.GONE
        }


        binding.clCustomerDetails.tvParentCustomerName.setOnClickListener {
            if (model.customerParent != null) {
                startActivity(
                        Intent(requireContext(), CustomerDetailActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER_ID, model.customerParent)
                )
            }
        }

        if (!model.contactPersonName.isNullOrEmpty()) {
            binding.clCustomerDetails.tvAuthorizePersonName.text =
                    model.contactPersonName?.replaceFirstChar(Char::titlecase)
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.GONE
        }

        if (model.mobile.isNullOrEmpty().not()) {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = true
            binding.clCustomerDetails.ivPhoneCall.alpha = 1f
            binding.clCustomerDetails.ivWhatsCall.isEnabled = true
            binding.clCustomerDetails.ivWhatsCall.alpha = 1f
        } else {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = false
            binding.clCustomerDetails.ivPhoneCall.alpha = 0.3f
            binding.clCustomerDetails.ivWhatsCall.isEnabled = false
            binding.clCustomerDetails.ivWhatsCall.alpha = 0.3f
        }

        if (model.city.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvLocation.text = model.city
            binding.clCustomerDetails.tvLocation.visibility = View.VISIBLE

            if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
                binding.clCustomerDetails.tvLocation.paintFlags =
                        binding.clCustomerDetails.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                binding.clCustomerDetails.tvLocation.paintFlags =
                        binding.clCustomerDetails.tvLocation.paintFlags or Paint.ANTI_ALIAS_FLAG
            }

            binding.clCustomerDetails.tvLocation.setOnClickListener {
                viewCustomerLocation(model.mapLocationLat, model.mapLocationLong, model.name)
            }
        } else {
            binding.clCustomerDetails.tvLocation.visibility = View.GONE
        }

        binding.clCustomerDetails.root.visibility = View.VISIBLE

        binding.clCustomerDetails.tvNewOrder.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
                SharedPref.getInstance().clearCart()
                startActivity(
                        Intent(requireContext(), CreateNewOrderForCustomerActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER, model)
                                .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                                .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
                )
            } else {
                Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.create_order_permission), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.clCustomerDetails.ivMore.visibility = View.GONE

        binding.clCustomerDetails.ivPhoneCall.setOnClickListener {
            onCall(model.mobile)
        }
        binding.clCustomerDetails.ivWhatsCall.setOnClickListener {
            onWCall(model.mobile, model.name)
        }

        binding.clCustomerDetails.tvRecordActivity.setOnClickListener {
            startActivity(
                    Intent(requireContext(), CustomFormActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            )
        }

        binding.clCustomerDetails.tvRecordPayment.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
                startActivity(
                        Intent(
                                requireContext(),
                                AddRecordPaymentActivity::class.java
                        )
                                .putExtra(AppConstant.CUSTOMER, model)
                                .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                                .putExtra(AppConstant.CUSTOMER_ID, model.id)
                )
            } else {
                Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.payment_permission),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun viewCustomerLocation(
            mapLocationLat: Double?,
            mapLocationLong: Double?,
            name: String?
    ) {
        if (mapLocationLat != 0.0 && mapLocationLong != 0.0) {
            Utils.openMap(requireContext(), mapLocationLat, mapLocationLong, name)
        } else {
            Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_location_found),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(requireContext(), OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.customer_pic_not_available),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(requireContext(), OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(requireContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

    fun onCall(mobile: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${mobile}")
        startActivity(intent)
    }

    fun onWCall(mobile: String?, contactPersonName: String?) {
        val uri =
                Uri.parse("https://api.whatsapp.com/send?phone=+91${mobile} &text=Hi, $contactPersonName")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    interface IRemindersListener {
        fun onDeleteReminder(model: ReminderItemModel?)
        fun onEditReminder(model: ReminderItemModel?)
    }

    fun onApprovedLead(model: LeadLisDataItem) {
        val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
                AppConstant.STATUS_APPROVED,
                model,
                this
        )
        fragment.show(childFragmentManager, AllLeadListActivity::class.java.name)
    }

    fun onRejectedLead(model: LeadLisDataItem) {
        leadActionModel = model
        val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
                AppConstant.STATUS_DISHONOUR,
                model,
                this
        )
        fragment.show(childFragmentManager, AllLeadListActivity::class.java.name)
    }

    override fun rejectedCommentOfLead(reason: String) {
        leadViewModel.approveOrRejectLead(leadActionModel?.id ?: 0, AppConstant.REJECTED, reason)
    }

    override fun approveCommentOfLead(reason: String) {
        leadViewModel.approveOrRejectLead(leadActionModel?.id ?: 0, AppConstant.APPROVED, reason)
    }

}