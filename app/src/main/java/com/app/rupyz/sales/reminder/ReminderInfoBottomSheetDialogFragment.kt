package com.app.rupyz.sales.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.CustomerLevel
import com.app.rupyz.databinding.BottomSheetReminderInfoLayoutBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment.IMockLocationActionListener
import com.app.rupyz.dialog.checkIn.CheckInDialogFragment
import com.app.rupyz.dialog.checkIn.CheckOutAlert
import com.app.rupyz.dialog.checkIn.CheckOutViewModel
import com.app.rupyz.dialog.checkIn.CheckedInDialogFragment
import com.app.rupyz.dialog.checkIn.ICheckInClickListener
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils.ILocationPermissionListener
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.findUserIsInGeoFencingArea
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.ReminderItemModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.ChooseActivityBottomSheet
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener
import com.app.rupyz.sales.lead.AllLeadListActivity
import com.app.rupyz.sales.lead.LeadApproveOrRejectedBottomSheetDialog
import com.app.rupyz.sales.lead.LeadViewModel
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReminderInfoBottomSheetDialogFragment : BottomSheetDialogFragment(),
        ProductImageViewPagerAdapter.ProductImageClickListener, IMockLocationActionListener,
        LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener,ILocationPermissionListener
{

    private lateinit var binding: BottomSheetReminderInfoLayoutBinding
    private val activityList: ArrayList<CustomerFeedbackStringItem> = arrayListOf()
    private var chooseActivityBottomSheet : ChooseActivityBottomSheet = ChooseActivityBottomSheet()


    private val customerViewModel: CustomerViewModel by viewModels()
    private val checkOutViewModel : CheckOutViewModel by viewModels()
    private val leadViewModel: LeadViewModel by viewModels()
    private var isDataChange: Boolean = false
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private var  customerID :Int?=null
    private val activityViewModel: StaffActivityViewModel by viewModels()
    private var leadActionModel: LeadLisDataItem? = null
    private lateinit var onItemUpdatedListener: (Boolean) -> Unit
    private var fragment: MarkAttendanceBottomSheetDialogFragment? = null

    private var someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            if (::onItemUpdatedListener.isInitialized){
                onItemUpdatedListener.invoke(isDataChange)
            }
            dismiss()
        }
    }
    
    val isStaffUser: Boolean
        get() {
            val appAccessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
            return appAccessType != AppConstant.ACCESS_TYPE_MASTER
        }
    
    /**
     *
     * Location Gathering Part
     *
     */
    private lateinit var locationPermissionUtils : LocationPermissionUtils
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback
    private var currentGeoLocationLat : Double = 0.00
    private var currentGeoLocationLong : Double = 0.00
    private var currentGeoAddress : String? = null
    
    /**************************************************************/

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
        activityViewModel.getFollowUpList()

        initObservers()
        initRecyclerView()
        
        /** Location Gathering Part **/
        locationPermissionUtils = LocationPermissionUtils(this, requireActivity())
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getUserCurrentLocation()
        /********************************************************/
        
        
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

       // chooseActivityBottomSheet.setActivities(arrayListOf())
        chooseActivityBottomSheet.setModuleType(reminderModel?.moduleType!!)
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
            popup.inflate(R.menu.menu_edit_and_delete)

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {

                    R.id.delete_product -> {
                        listener?.onDeleteReminder(reminderModel)
                        dismiss()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.edit_product -> {
                        someActivityResultLauncher.launch(
                            Intent(requireActivity(), CustomFormActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER_FEEDBACK, reminderModel?.followup_id)
                                .putExtra(AppConstant.CUSTOMER_ID, reminderModel?.moduleId)
                                .putExtra(AppConstant.ACTIVITY_TYPE, reminderModel?.moduleType)
                        )
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
            return hasInternetConnection(requireContext())
        }

    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 6)
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
        checkOutViewModel.getCheckOut().observe(this) { data ->
            if (data.error == false) {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
               // showToast(data.message)

            } else {
                if (data.errorCode != 0){
                    Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                    //showToast(data.message)

                }
            }

        }
        activityViewModel.getFollowUpListLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    activityList.clear()
                    activityList.addAll(list)
                    list.let { activityList ->
                        //chooseActivityBottomSheet.setActivities(activityList)
                    }
                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                   // logout()
                } else {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    //showToast(it.message)
                }
            }
        }
        customerViewModel.getCheckIn().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
           // binding.clLeadDetails.progressBar.visibility = View.GONE
            if (data.error == false) {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                if (customerID!=null)
                {
                    startActivity(  Intent(
                        requireActivity(),
                        CustomerDetailActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, customerID))
                }

            }
            else{
                val fragment = CheckedInDialogFragment.getInstance(customerID!!,data.message!!)
                fragment.show(childFragmentManager, DeleteDialogFragment::class.java.name)

            }
        }

        customerViewModel.getCustomerByIdData().observe(this) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { model ->
                    initCustomerData(model)
                    chooseActivityBottomSheet.setCustomerData(model)
                }
            } else {
                Toast.makeText(requireContext(), "${data.message}", Toast.LENGTH_SHORT).show()
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
        if (model.state.isNullOrEmpty().not()) {
            binding.clLeadDetails.tvLocation.text = model.state
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

        if (model.state.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvLocation.text = model.state
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

        binding.clCustomerDetails.tvNewOrder.setOnClickListener{

            val fragment = CheckInDialogFragment.getInstance(model, object :
                ICheckInClickListener {
                override fun onConfirm(model: CheckInRequest) {

                    if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
                        SharedPref.getInstance().clearCart()
                        customerID=model.customer_id
                        model.geo_location_lat = currentGeoLocationLat
                        model.geo_location_long = currentGeoLocationLong
                        customerViewModel.getCheckInData(model, hasInternetConnection)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.create_order_permission), Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            })

            fragment.show(childFragmentManager, DeleteDialogFragment::class.java.name)

        }


        val checkSetting=SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
        if (model.checkInTime.isNullOrBlank().not()){
            binding.clCustomerDetails.tvNewOrder.visibility=View.GONE
            binding.clCustomerDetails.btnActivity.visibility=View.VISIBLE
        }else{
            if (checkSetting)
            {
                binding.clCustomerDetails.tvNewOrder.visibility=View.VISIBLE
                binding.clCustomerDetails.btnActivity.visibility=View.GONE
            }
            else
            {
                binding.clCustomerDetails.btnActivity.visibility=View.VISIBLE
                binding.clCustomerDetails.tvNewOrder.visibility=View.GONE
            }
        }

        binding.clCustomerDetails.imgPop.setOnClickListener {

            //creating a popup menu
            val popup =
                PopupMenu(it.context, binding.clCustomerDetails.tvNewOrder, Gravity.CENTER)
            //inflating menu from xml resource
            popup.inflate(R.menu.check_in_menu)


            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_check_in -> {
                        
                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                            model?.let { checkGeoFencingAndStartCheckIn(it) }
                        } else {
                            showStartDayDialog(object : (Boolean) -> Unit {
                                override fun invoke(onSuccessStartDay : Boolean) {
                                    if (onSuccessStartDay) {
                                        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
                                            model?.let { checkGeoFencingAndStartCheckIn(it) }
                                        }
                                    }
                                }
                            })
                        }

                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_telephonic -> {
                        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
                            SharedPref.getInstance().clearCart()
                            it.context.startActivity(
                                Intent(MyApplication.instance, CreateNewOrderForCustomerActivity::class.java)
                                    .putExtra(AppConstant.CUSTOMER, model)
                                    .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                                    .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                    .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
                            )
                        } else {
                            //Toast.makeText(MyApplication.instance.applicationContext,MyApplication.instance.getString(R.string.create_order_permission,Toast.LENGTH_LONG))
                            Toast.makeText(
                                MyApplication.instance,
                                MyApplication.instance.getString(R.string.create_order_permission),Toast.LENGTH_LONG).show()
                            //MyApplication.instance.showToast(resources.getString(R.string.create_order_permission))
                        }

                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }
        binding.clCustomerDetails.btnActivity.setOnClickListener {
            if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)){
                model?.let { customeData -> checkGeoFencingAndOpenChooser(customeData) }
            }else{
                showStartDayDialog( object : (Boolean) -> Unit{
                    override fun invoke(onSuccessStartDay : Boolean) {
                        if (onSuccessStartDay){
                            if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)){
                                model?.let { customeData -> checkGeoFencingAndOpenChooser(customeData) }
                            }
                        }
                    }
                })
            }
        }

        binding.clCustomerDetails.ivMore.visibility = View.GONE

        binding.clCustomerDetails.ivPhoneCall.setOnClickListener {
            onCall(model.mobile)
        }
        binding.clCustomerDetails.ivWhatsCall.setOnClickListener {
            onWCall(model.mobile, model.name)
        }

        chooseActivityBottomSheet.setOnCreateOrderListener {
            
            if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
                val  selectedStep = model.customerLevel?.let {
                    when (it) {
                        AppConstant.CUSTOMER_LEVEL_1 -> CustomerLevel.LEVEL_ONE
                        AppConstant.CUSTOMER_LEVEL_2 -> CustomerLevel.LEVEL_TWO
                        AppConstant.CUSTOMER_LEVEL_3 -> CustomerLevel.LEVEL_THREE
                        else -> null
                    }
                }
                if (((selectedStep == null) || selectedStep == CustomerLevel.LEVEL_TWO || selectedStep == CustomerLevel.LEVEL_THREE) && SharedPref.getInstance().getBoolean(CUSTOMER_LEVEL_ORDER,false)) {
                    context?.startActivity(Intent(context, CustomerDetailActivity::class.java)
                                                   .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                                   .putExtra(AppConstant.CUSTOMER_TYPE, model.customerLevel)
                                                   .putExtra(AppConstant.DISTRIBUTOR_SELECTOR,true)
                                          )
                }else{
                    model.let { customerData ->
                        context?.startActivity(
                                Intent(context, CreateNewOrderForCustomerActivity::class.java).putExtra(AppConstant.CUSTOMER, customerData)
                                        .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                        .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                                        .putExtra(AppConstant.PAYMENT_INFO, customerData.paymentTerm)
                                              )
                    }
                }
            }else{
                Toast.makeText(requireActivity(), resources.getString(R.string.create_order_permission), Toast.LENGTH_LONG).show()
            }
            
            
        }

      /*  binding.clCustomerDetails.tvRecordActivity.setOnClickListener {
            startActivity(
                    Intent(requireContext(), CustomFormActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            )
        }*/

       /* binding.clCustomerDetails.tvRecordPayment.setOnClickListener {
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
        }*/
    }
    
    fun checkGeoFencingAndOpenChooser(customeData : CustomerData) {
        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false) ){
            registerGeofenceUpdates(customeData, object : (Boolean) -> Unit{
                override fun invoke(isInsideArea : Boolean) {
                    if (isInsideArea){
                        chooseActivityBottomSheet.show(childFragmentManager, ChooseActivityBottomSheet::class.java.name)
                    }else{
                        CheckOutAlert.showCheckOutAlertDialog(requireActivity(), customeData.name ?: "")
                    }
                }
            })
        }else{
            chooseActivityBottomSheet.show(childFragmentManager, ChooseActivityBottomSheet::class.java.name)
        }
    }
    
    fun checkGeoFencingAndStartCheckIn(customeData : CustomerData) {
        if (SharedPref.getInstance().getBoolean(AppConstant.GEO_FENCING_ENABLE, false)) {
            getUserCurrentLocation()
            registerGeofenceUpdates(customeData, object : (Boolean) -> Unit {
                override fun invoke(isInsideArea : Boolean) {
                    if (isInsideArea) {
                        if ((customeData.checkInTime.isNullOrEmpty())) {
                            customeData?.let { customerData ->
                                val fragment = CheckInDialogFragment.getInstance(customerData, object :
                                    ICheckInClickListener {
                                    override fun onConfirm(model : CheckInRequest) {
                                        model.geo_location_lat = currentGeoLocationLat
                                        model.geo_location_long = currentGeoLocationLong
                                        customerViewModel.getCheckInData(model, hasInternetConnection(requireActivity()))
                                    }
                                    
                                })
                                fragment.show(childFragmentManager, DeleteDialogFragment::class.java.name)
                            }
                        }
                    } else {
                        CheckOutAlert.showCheckOutAlertDialog(requireActivity(), customeData.name ?: "")
                    }
                }
            })
            
        }
        else {
            if ((customeData.checkInTime.isNullOrEmpty())) {
                customeData?.let { customerData ->
                    val fragment = CheckInDialogFragment.getInstance(customerData, object :
                        ICheckInClickListener {
                        override fun onConfirm(model : CheckInRequest) {
                            model.geo_location_lat = currentGeoLocationLat
                            model.geo_location_long = currentGeoLocationLong
                            customerViewModel.getCheckInData(model, hasInternetConnection(requireActivity()))
                        }
                        
                    })
                    fragment.show(childFragmentManager, DeleteDialogFragment::class.java.name)
                }
            }
        }
    }
    
    private fun showStartDayDialog(onStartDaySuccess : (Boolean) -> Unit) {
        if (childFragmentManager.fragments.firstOrNull { it.tag?.equals(MarkAttendanceBottomSheetDialogFragment::class.java.name) == true } == null) {
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(
                    object : IStartDayActionListener {
                        override fun onDismissDialogForStartDay() {
                            super.onDismissDialogForStartDay()
                            onStartDaySuccess.invoke(false)
                        }
                        
                        override fun onSuccessfullyMarkAttendance() {
                            super.onSuccessfullyMarkAttendance()
                            
                            onStartDaySuccess.invoke(true)
                        }
                    },
                    currentGeoLocationLat,
                    currentGeoLocationLong
                                                                          )
            fragment?.show(
                    childFragmentManager,
                    MarkAttendanceBottomSheetDialogFragment::class.java.name
                          )
        } else {
            if (fragment?.isVisible == false && fragment?.isAdded == false) {
                fragment?.dismiss()
                childFragmentManager.fragments.remove(fragment)
                fragment?.show(childFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
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


    fun setOnItemUpdatedListener(onItemUpdatedListener : (Boolean) -> Unit){
        this.onItemUpdatedListener = onItemUpdatedListener
    }



    /*override fun onConfirm(customerName: String, customerID: Int) {
        val checkoutRequest = CheckoutRequest(customerID)
        checkOutViewModel.getCheckOutData(checkoutRequest,
            hasInternetConnection
        )
    }*/
    
    
    /**
     *
     * Location Gathering Part
     *
     */
    
    
    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(requireActivity())) {
                setUpdatedLocationListener()
            } else {
                locationPermissionUtils.showEnableGpsDialog()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getUserCurrentLocation()
        } else {
            if (isStaffUser && PermissionModel.INSTANCE.getPermission(AppConstant.LOCATION_TRACKING, false)) {
                getUserCurrentLocation()
            }
        }
    }
    
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationPermissionUtils.setActivityResult(resultCode, requestCode, data)
    }
    
    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<String>, grantResults : IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
    }
    
    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setWaitForAccurateLocation(false).setMinUpdateIntervalMillis(1000).setMaxUpdateDelayMillis(100).build()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult : LocationResult) {
                super.onLocationResult(locationResult)
                if (activity != null){
                    locationResult.lastLocation?.let {
                        if (Utils.isMockLocation(it).not()) {
                            currentGeoLocationLat = it.latitude
                            currentGeoLocationLong = it.longitude
                            GeoLocationUtils.getAddress(requireActivity(), longitude = currentGeoLocationLong, latitude = currentGeoLocationLat) { address ->
                                currentGeoAddress = address
                            }
                        } else {
                            val fragment = MockLocationDetectedDialogFragment.getInstance(this@ReminderInfoBottomSheetDialogFragment)
                            fragment.isCancelable = false
                            fragment.show(childFragmentManager, MockLocationDetectedDialogFragment::class.java.name)
                        }
                    }
                }
            }
        }
        
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }
    override fun onPdfClick(position: Int,url:String) {
        super.onPdfClick(position,url)
        if (url!=null)
        {
            FileUtils.openPdf(url, requireContext())
        }


    }
    
    private fun registerGeofenceUpdates(customerModel : CustomerData, onInsideFence : (Boolean) -> Unit) {
        val isUserInsideGeoFencing = if (customerModel?.mapLocationLat != 0.0 && customerModel?.mapLocationLong != 0.0) {
            findUserIsInGeoFencingArea(customerModel?.mapLocationLat ?: 0.0, customerModel?.mapLocationLong ?: 0.0, currentGeoLocationLat, currentGeoLocationLong).first
        } else {
            true
        }
        
        if (isUserInsideGeoFencing) {
            onInsideFence.invoke(true)
        } else {
            onInsideFence.invoke(false)
        }
    }

}