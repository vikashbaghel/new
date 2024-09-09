package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetChooseActivityBinding
import com.app.rupyz.dialog.checkIn.CheckOutConfiramation
import com.app.rupyz.dialog.checkIn.CheckOutDialog
import com.app.rupyz.dialog.checkIn.CheckOutViewModel
import com.app.rupyz.dialog.checkIn.ICheckOutConClickListener
import com.app.rupyz.dialog.checkIn.ICheckOutConfirmationClickListener
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CheckoutRequest
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.adapters.ActivityListAdapter
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChooseActivityBottomSheet : BottomSheetDialogFragment(),
    ICheckOutConClickListener {

    private lateinit var binding: BottomSheetChooseActivityBinding
    private val checkOutViewModel: CheckOutViewModel by viewModels()
    private val activityList: ArrayList<CustomerFeedbackStringItem> = arrayListOf()
    private val activityListAdapter: ActivityListAdapter = ActivityListAdapter()
    private var customerData: CustomerData? = null
    private var isActivitySet = false
    private var onOrderCreateListener: () -> Unit = {}
    private var moduleType: String? = null
    private val activityViewModel: StaffActivityViewModel by activityViewModels()
    private var noOrderId : Int = -1
    
    private var activityResultContract : ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val orderMessage = resources.getString(R.string.activity_added_successfully)
                val checkSetting = SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)
                activity?.let { activity->
                    CheckOutConfiramation.showCheckOutConfirmationDialog(activity, customerName = customerData?.name ?: "", orderMessage, customerData?.id?: 0, object : ICheckOutConfirmationClickListener{
                        override fun onCheckOutConfirm(customerName : String, customerID : Int) {
                            CheckOutDialog.showCheckOutDialog(
                                activity,
                                customerName = customerData?.name ?: "",
                                customerID = customerData?.id?: 0,
                                listener = this@ChooseActivityBottomSheet)
                        }

                        override fun openActivity() {

                        }

                        override fun dismissed() {
                            dismiss()
                        }

                    }, checkSetting)
                }
                
            }else{
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChooseActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::binding.isInitialized) {

            binding.rvActivityList.adapter = activityListAdapter
            setListener()
            initObservers()
            if (activityViewModel.getFollowUpListLiveData.value == null || activityViewModel.getFollowUpListLiveData.value?.data == null){
                activityViewModel.getFollowUpList()
            }else{
                activityViewModel.getFollowUpListLiveData.postValue(activityViewModel.getFollowUpListLiveData.value)
            }
            if (isActivitySet) {
                binding.progressbar.hideView()
            }
            if (activityListAdapter.itemCount == 0) {
                activityListAdapter.setActivities(activityList)
            }
        }
    }


    private fun setListener() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        activityListAdapter.setActivitySelectListener { activityType ->
            when (activityType.id) {
                /***
                 * Check Out Activity  Static ID
                 * **/
                -999 -> {
                    context?.let { context ->
                        customerData?.let {
                            CheckOutDialog.showCheckOutDialog(
                                context = context,
                                customerName = customerData?.name ?: "",
                                customerID = customerData?.id ?: 0,
                                listener = this
                            )
                        }
                    }
                }
                /***
                 * Create  Order Activity  Static ID
                 * **/
                999909 -> {

                    onOrderCreateListener.invoke()
                    dismiss()
                }
                /***
                 * No Order  Activity  Static ID
                 * **/
                999919 -> {
                    customerData?.let { customerData ->
                        activityResultContract?.launch(
                            Intent(context, CustomFormActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER, customerData)
                                .putExtra(AppConstant.CUSTOMER_ID, customerData.id ?: 0)
                                .putExtra(AppConstant.FEEDBACK_ID, noOrderId)
                                .putExtra(AppConstant.ACTIVITY_TYPE, moduleType)
                        )
                    }
                }
                /***
                 * Record New Payment  Activity  Static ID
                 * **/
                999989 -> {
                    customerData?.let { customerData ->
                        activity?.let { activity ->
                            if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
                                activityResultContract?.launch(Intent(activity, AddRecordPaymentActivity::class.java)
                                    .putExtra(AppConstant.CUSTOMER, customerData)
                                    .putExtra(AppConstant.CUSTOMER_NAME, customerData.name)
                                    .putExtra(AppConstant.CUSTOMER_ID, customerData.id)
                                )
                            } else {
                                if (activity is BaseActivity) {
                                    activity.showToast(resources.getString(R.string.payment_permission))
                                }else{
                                    Toast.makeText(activity, resources.getString(R.string.payment_permission), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                }
                
                /***
                 * Dynamic Activity
                 * **/
                else -> {
                    customerData?.let { customerData ->
                        activityResultContract?.launch(
                            Intent(context, CustomFormActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER, customerData)
                                .putExtra(AppConstant.CUSTOMER_ID, customerData.id ?: 0)
                                .putExtra(AppConstant.FEEDBACK_ID, activityType.id)
                                .putExtra(AppConstant.ACTIVITY_TYPE, moduleType)
                        )
                    }
                }
            }
        }
    }

    fun setOnCreateOrderListener(listener: () -> Unit) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
            onOrderCreateListener = listener
        }else{
            activity?.let { activity -> if (activity is BaseActivity) activity.showToast(resources.getString(R.string.create_order_permission)) else Toast.makeText(activity, resources.getString(R.string.create_order_permission), Toast.LENGTH_LONG).show()  }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (isActivitySet && ::binding.isInitialized) {
            binding.progressbar.hideView()
        }
    }

    fun setCustomerData(customerData: CustomerData) {
        this.customerData = customerData
    }


    fun setModuleType(activityType: String) {
        moduleType = activityType
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        checkOutViewModel.getCheckOut().observe(this) { data ->
            if (data.error == false) {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).showToast(data.message)
                }
                val intent = Intent(activity, ListOfCustomerActivity::class.java)
                startActivity(intent)
            } else {
                if (data.errorCode != 0) {
                    if (data.errorCode != null && data.errorCode == 403) {
                        if (activity is BaseActivity) {
                            (activity as BaseActivity).logout()
                        }
                    } else {
                        if (activity is BaseActivity) {
                            (activity as BaseActivity).showToast(data.message)
                        }else{
                            Toast.makeText(activity, data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            if (data.errorCode != 0) {
                dismiss()
            }
        }
        activityViewModel.getFollowUpListLiveData.observe(this) { response ->
            if (response.error == false) {
                response.data?.let { activityTypeList ->
                    val list = arrayListOf<CustomerFeedbackStringItem>()
                    list.addAll(activityTypeList)
                    val noOrder = activityTypeList.firstOrNull { it.stringValue?.equals(AppConstant.NO_ORDER,true) == true }
                    if (activityTypeList.isEmpty().not()) {
                        isActivitySet = true
                        if (::binding.isInitialized) {
                            binding.progressbar.hideView()
                        }
                    }
                    list.add(0,CustomerFeedbackStringItem(
                     /***
                     * Record New Payment  Activity  Static ID
                     * **/
                     id = 999909, stringValue = AppConstant.ORDER))
                    if (noOrder != null){
                        noOrderId = noOrder.id
                        list.add(1, CustomerFeedbackStringItem(
                                /***
                                 * No Order  Activity  Static ID
                                 * **/
                                id = 999919, stringValue = AppConstant.NO_ORDER))
                        list.removeAll { it.id == noOrderId }
                        list.add(2, CustomerFeedbackStringItem(
                                /***
                                 * Record New Payment  Activity  Static ID
                                 * **/
                                id = 999989, stringValue = AppConstant.NEW_PAYMENT))
                    }else{
                        list.add(1, CustomerFeedbackStringItem(
                                /***
                                 * Record New Payment  Activity  Static ID
                                 * **/
                                id = 999989, stringValue = AppConstant.NEW_PAYMENT))
                    }
                    if (SharedPref.getInstance().getBoolean(AppConstant.CHECK_IN, false)) {
                        list.add(CustomerFeedbackStringItem(
                                /***
                                 * Check Out Activity  Static ID
                                 * **/
                                -999, stringValue = AppConstant.LBL_CHECK_OUT))
                    }
                    activityList.clear()
                    activityList.addAll(list)
                    activityListAdapter.setActivities(activityList)
                }
            } else {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).showToast(response.message)
                }
            }
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        checkOutViewModel.clear()
        super.onDismiss(dialog)
    }
    
    
    
   /* override fun onConfirm(customerName: String, customerID: Int) {

        CheckOutDialog.showCheckOutDialog(
            activity,
            customerName = customerData?.name ?: "",
            customerID = customerID?: 0,
            listener = this
        )

    }*/

    override fun onCheckoutConfirm(customerName: String, customerID: Int) {
        val checkoutRequest = CheckoutRequest(customerID)
        context?.let { context->  hasInternetConnection(context) }?.let { hasInternetConnection ->
            checkOutViewModel.getCheckOutData(checkoutRequest, hasInternetConnection)
        }
    }
    
    

}