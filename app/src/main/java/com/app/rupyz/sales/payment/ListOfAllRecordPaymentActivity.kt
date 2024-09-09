package com.app.rupyz.sales.payment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityListOfAllRecordPaymentBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.sales.beatplan.ApproveBottomSheetDialogFragment
import com.google.gson.JsonObject

class ListOfAllRecordPaymentActivity : BaseActivity(), RecordPaymentActionListener,
    PaymentRejectedBottomSheetDialogFragment.IPaymentRejectedListener, DeleteDialog.IOnClickListener,
    ApproveBottomSheetDialogFragment.IApproveActionListener {
    private lateinit var binding: ActivityListOfAllRecordPaymentBinding
    private var isRecordPaymentUpdated: Boolean = false

    private lateinit var recordPaymentAdapter: AllRecordPaymentAdapter

    private lateinit var recordPaymentViewModel: RecordPaymentViewModel
    private var recordPaymentList = ArrayList<RecordPaymentData>()

    private var currentPage = 1
    private var deletePaymentPosition: Int = -1
    private var rejectedPosition = -1

    private var rejectedPaymentModel: RecordPaymentData? = null

    private var approvePaymentModel: RecordPaymentData? = null
    private var approvePaymentPosition: Int? = null

    private var customerModel: CustomerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfAllRecordPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        recordPaymentViewModel = ViewModelProvider(this)[RecordPaymentViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE

        getPaymentList()

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }

        initRecyclerView()
        initObservers()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addPaymentRecord.setOnClickListener {
            activityResultLauncher.launch(
                Intent(
                    this@ListOfAllRecordPaymentActivity,
                    AddRecordPaymentActivity::class.java
                )
                    .putExtra(
                        AppConstant.CUSTOMER_NAME,
                        intent.getStringExtra(AppConstant.CUSTOMER_NAME)
                    )
                    .putExtra(
                        AppConstant.CUSTOMER_ID,
                        intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
                    ).putExtra(AppConstant.CUSTOMER, customerModel)
            )
        }
    }

    private fun getPaymentList() {
        recordPaymentViewModel.getRecordPaymentList(
            intent.getIntExtra(AppConstant.CUSTOMER_ID, 0), currentPage
        )
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private var activityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data?.hasExtra(AppConstant.CUSTOMER_ID) == true) {
                getPaymentList()
                isRecordPaymentUpdated = true
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCustomerList.layoutManager = linearLayoutManager
        recordPaymentAdapter = AllRecordPaymentAdapter(
            recordPaymentList,
            this,
            hasInternetConnection()
        )
        binding.rvCustomerList.adapter = recordPaymentAdapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        recordPaymentViewModel.getRecordPaymentListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let {
                    if (it.isNotEmpty()) {
                        binding.clEmptyData.visibility = View.GONE

                        recordPaymentList.clear()
                        recordPaymentList.addAll(it)
                        recordPaymentAdapter.notifyDataSetChanged()
                    } else {
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                showToast(data.message)
            }
        }
        recordPaymentViewModel.updatePaymentRecordLiveData.observe(this) { data ->
            showToast(data.message)
            data?.let {
                if (data.error == false) {
                    getPaymentList()
                }
            }
        }

        recordPaymentViewModel.deletePaymentLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            showToast(it.message)
            if (it.error == false) {
                if (deletePaymentPosition != -1) {
                    recordPaymentList.removeAt(deletePaymentPosition)
                    recordPaymentAdapter.notifyItemRemoved(deletePaymentPosition)
                    recordPaymentAdapter.notifyItemRangeChanged(
                        deletePaymentPosition,
                        recordPaymentList.size
                    )
                }
            }
        }
    }

    override fun onStatusChange(status: String, model: RecordPaymentData, position: Int) {
        if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false)) {
            if (status == AppConstant.APPROVE) {
                approvePaymentModel = model
                approvePaymentPosition = position
                val fragment = ApproveBottomSheetDialogFragment.getInstance(
                    this,
                    ListOfPaymentActivity::class.java.name
                )
                fragment.show(supportFragmentManager, AppConstant.BEAT)
            } else if (status == AppConstant.REJECT) {
                rejectedPosition = position
                val fragment = PaymentRejectedBottomSheetDialogFragment(model, this)
                fragment.show(supportFragmentManager, "Payment")
            }
        }
    }

    override fun getPaymentInfo(model: RecordPaymentData, position: Int) {
        startActivity(
            Intent(
                this,
                PaymentDetailsActivity::class.java
            ).putExtra(AppConstant.PAYMENT_INFO, model)
        )
    }

    override fun onDeletePayment(model: RecordPaymentData, position: Int) {
       // showDeleteDialog(model, position)
        DeleteDialog.showDeleteDialog(this,model.id,position, resources.getString(R.string.delete_payment),resources.getString(R.string.delete_payment_message),this)

    }

    private fun showDeleteDialog(model: RecordPaymentData, position: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_payment)
        tvTitle.text = resources.getString(R.string.delete_payment_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            deletePaymentPosition = position
            val jsonObject = JsonObject()
            jsonObject.addProperty("is_archived", true)
            binding.progressBar.visibility = View.VISIBLE
            recordPaymentViewModel.deletePayment(jsonObject, model.id!!, hasInternetConnection())
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun commentOfPaymentRejected(model: RecordPaymentData) {
        rejectedPaymentModel = model
        rejectedPaymentModel?.status = AppConstant.STATUS_DISHONOUR
        recordPaymentViewModel.updateRecordPayment(model, model.id ?: 0)
    }

    override fun onDismissDialog(model: RecordPaymentData) {
        recordPaymentList[rejectedPosition].status = model.status
        recordPaymentAdapter.notifyItemChanged(rejectedPosition)
    }

    override fun onBackPressed() {
        if (isRecordPaymentUpdated) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun approvalConformation(reason: String) {
        if (approvePaymentModel != null && approvePaymentPosition != null) {
            approvePaymentModel?.status = AppConstant.STATUS_APPROVED
            recordPaymentAdapter.notifyItemChanged(approvePaymentPosition!!)
            recordPaymentViewModel.updateRecordPayment(
                approvePaymentModel!!,
                approvePaymentModel?.id ?: 0
            )
        }
    }

    override fun onCancelApproval() {
        super.onCancelApproval()
        recordPaymentAdapter.notifyItemChanged(approvePaymentPosition!!)

    }

    override fun onDelete(model: Any, position: Any) {
        deletePaymentPosition = position as Int
        val jsonObject = JsonObject()
        jsonObject.addProperty("is_archived", true)
        binding.progressBar.visibility = View.VISIBLE
        recordPaymentViewModel.deletePayment(jsonObject, model as Int, hasInternetConnection())
    }
}