package com.app.rupyz.sales.lead

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityLeadDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.AddCustomerActivity
import com.app.rupyz.sales.home.SalesMainActivity

class LeadDetailsActivity : BaseActivity(),
        LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener {
    private lateinit var binding: ActivityLeadDetailsBinding
    private lateinit var leadViewModel: LeadViewModel

    private lateinit var leadModel: LeadLisDataItem
    private var isDataUpdate = false

    private lateinit var leadDetailFragmentPagerAdapter: LeadDetailFragmentPagerAdapter

    private val fragmentList = arrayListOf(AppConstant.ALL, AppConstant.CUSTOMER_FEEDBACK)

    private lateinit var popup: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        initObservers()

        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)!!

            leadViewModel.getLeadDetail(leadId = leadModel.id!!, hasInternetConnection())
        } else if (intent.hasExtra(AppConstant.LEAD_ID)) {
            leadViewModel.getLeadDetail(leadId = intent.getIntExtra(AppConstant.LEAD_ID, 0), hasInternetConnection())
        }

        initLayout()

    }

    private fun initLayout() {
        popup = PopupMenu(this, binding.ivMore)
        //inflating menu from xml resource
        popup.inflate(R.menu.customer_action_menu)

        if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.EDIT_LEAD_PERMISSION, false).not()
                || leadModel.status == AppConstant.STATUS_CONVERTED_TO_CUSTOMER
                || leadModel.status == AppConstant.REJECTED
        ) {
            if (leadModel.isSyncedToServer == null || leadModel.isSyncedToServer == true) {
                popup.menu.getItem(0).isVisible = false
            }
        }

        if (PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_LEAD_PERMISSION, false)
                        .not()
        ) {
            if (leadModel.isSyncedToServer == null || leadModel.isSyncedToServer == true) {
                popup.menu.getItem(1).isVisible = false
            }
        }

        //adding click listener
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit_product -> {
                    someActivityResultLauncher.launch(
                            Intent(this, AddNewLeadActivity::class.java).putExtra(
                                    AppConstant.LEAD_INFO,
                                    leadModel
                            )
                    )
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_inactive_customer -> {
                    showDeleteDialog(leadModel)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }

        binding.btnConvertToCustomer.setOnClickListener {
            if (leadModel.status == AppConstant.STATUS_APPROVED) {
                someActivityResultLauncher.launch(
                        Intent(
                                this,
                                AddCustomerActivity::class.java
                        ).putExtra(AppConstant.LEAD_INFO, leadModel)
                )
            }
        }

        binding.btnApprove.setOnClickListener {
            val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
                    AppConstant.STATUS_APPROVED,
                    leadModel,
                    this
            )
            fragment.show(supportFragmentManager, AllLeadListActivity::class.java.name)
        }

        binding.btnReject.setOnClickListener {
            val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
                    AppConstant.STATUS_DISHONOUR,
                    leadModel,
                    this
            )
            fragment.show(supportFragmentManager, AllLeadListActivity::class.java.name)
        }

        binding.ivMore.setOnClickListener {
            popup.show()
        }

        binding.tvBasicDetails.setOnClickListener {
            changeBasicDetailsTabColor()
            binding.viewPager.currentItem = 0
        }

        binding.tvRecordActivity.setOnClickListener {
            changeActivityTabColor()
            binding.viewPager.currentItem = 1
        }

        binding.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initTabLayout(leadModel: LeadLisDataItem) {
        leadDetailFragmentPagerAdapter = LeadDetailFragmentPagerAdapter(
                this, this.leadModel.id!!, fragmentList, leadModel
        )

        binding.viewPager.adapter = leadDetailFragmentPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    changeBasicDetailsTabColor()
                } else {
                    changeActivityTabColor()
                }
            }
        })
    }

    private fun changeActivityTabColor() {
        binding.tvBasicDetails.setBackgroundColor(resources.getColor(R.color.white))
        binding.tvRecordActivity.setBackgroundColor(resources.getColor(R.color.theme_purple))

        binding.tvBasicDetails.setTextColor(resources.getColor(R.color.expense_dark_gray))
        binding.tvRecordActivity.setTextColor(resources.getColor(R.color.white))

    }

    private fun changeBasicDetailsTabColor() {
        binding.tvBasicDetails.setBackgroundColor(resources.getColor(R.color.theme_purple))
        binding.tvRecordActivity.setBackgroundColor(resources.getColor(R.color.white))

        binding.tvBasicDetails.setTextColor(resources.getColor(R.color.white))
        binding.tvRecordActivity.setTextColor(resources.getColor(R.color.expense_dark_gray))

    }

    private var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataUpdate = true

            if (hasInternetConnection()) {
                leadViewModel.getLeadDetail(leadModel.id!!, hasInternetConnection())
            } else {
                leadModel.status = AppConstant.STATUS_CONVERTED_TO_CUSTOMER
                leadViewModel.updateOfflineLeadToCustomer(leadModel, leadModel.id!!, false)
                convertButtonStyle(leadModel.status)
            }
        }
    }

    private fun showDeleteDialog(model: LeadLisDataItem) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_lead)
        tvTitle.text = resources.getString(R.string.delete_lead_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            leadViewModel.deleteLead(model.id!!, hasInternetConnection())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initObservers() {
        leadViewModel.addLeadLiveData.observe(this) {
            ButtonStyleHelper(this).initDisableButton(
                    true,
                    binding.btnConvertToCustomer,
                    resources.getString(R.string.convert_to_customer)
            )
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        leadViewModel.leadDetailLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { model ->

                    leadModel = model

                    if (!model.businessName.isNullOrEmpty()) {
                        binding.tvToolbarTitle.text = model.businessName?.replaceFirstChar(
                                Char::titlecase
                        )
                    }

                    if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.EDIT_LEAD_PERMISSION, false) ||
                            PermissionModel.INSTANCE.getPermission(AppConstant.DELETE_LEAD_PERMISSION, false)
                    ) {
                        binding.ivMore.visibility = View.VISIBLE
                    } else {
                        binding.ivMore.visibility = View.GONE
                    }

                    if (hasInternetConnection().not()) {
                        if (leadModel.isSyncedToServer == false) {
                            binding.ivMore.visibility = View.VISIBLE
                        } else {
                            binding.ivMore.visibility = View.GONE
                        }
                    }

                    convertButtonStyle(model.status)

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

                    if (hasInternetConnection().not()) {
                        if (model.isSyncedToServer == false) {
                            binding.ivMore.visibility = View.VISIBLE
                        } else {
                            binding.ivMore.visibility = View.GONE
                        }
                    }
                }

                initTabLayout(leadModel)
            }
        }

        leadViewModel.approveRejectLeadLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            enableTouch()
            showToast(it.message)
            if (it.error == false) {
                it.data?.let { model ->
                    leadModel.status = model.status
                    isDataUpdate = true
                    convertButtonStyle(leadModel.status)
                }
            }
        }
    }

    private fun convertButtonStyle(status: String?) {
        when (status?.lowercase()) {
            AppConstant.PENDING.lowercase() -> {

                if (isStaffUser) {
                    if (leadModel.createdBy == SharedPref.getInstance()
                                    .getString(AppConstant.USER_ID)
                                    .toInt()
                    ) {
                        if (PermissionModel.INSTANCE.getPermission(
                                        AppConstant.APPROVE_SELF_LEAD_PERMISSION,
                                        false
                                )
                        ) {
                            binding.btnLayout.visibility = View.GONE
                        }
                    } else if (PermissionModel.INSTANCE.getPermission(
                                    AppConstant.APPROVE_LEAD_PERMISSION,
                                    false
                            )
                    ) {
                        binding.btnLayout.visibility = View.VISIBLE
                    } else {
                        binding.btnLayout.visibility = View.GONE
                    }
                } else {
                    binding.btnLayout.visibility = View.VISIBLE
                }

                binding.btnConvertToCustomer.visibility = View.GONE
            }

            AppConstant.APPROVED.lowercase() -> {
                binding.btnLayout.visibility = View.INVISIBLE
                binding.btnConvertToCustomer.visibility = View.VISIBLE
            }

            AppConstant.REJECTED.lowercase() -> {
                binding.btnLayout.visibility = View.INVISIBLE
                binding.btnConvertToCustomer.visibility = View.VISIBLE
                binding.btnConvertToCustomer.text = resources.getString(R.string.rejected)
                binding.btnConvertToCustomer.setBackgroundColor(resources.getColor(R.color.lead_action_text))
            }

            AppConstant.STATUS_CONVERTED_TO_CUSTOMER.lowercase() -> {
                binding.btnLayout.visibility = View.INVISIBLE
                binding.btnConvertToCustomer.visibility = View.VISIBLE
                binding.btnConvertToCustomer.text =
                        resources.getString(R.string.already_a_customer)
                binding.btnConvertToCustomer.setBackgroundColor(resources.getColor(R.color.lead_action_text))
            }
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.NOTIFICATION)) {
            startActivity(
                    Intent(
                            this,
                            SalesMainActivity::class.java
                    )
            )
        } else if (isDataUpdate) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun rejectedCommentOfLead(reason: String) {
        disableTouch()
        binding.progressBar.visibility = View.VISIBLE
        leadViewModel.approveOrRejectLead(leadModel.id ?: 0, AppConstant.REJECTED, reason)
    }

    override fun approveCommentOfLead(reason: String) {
        disableTouch()
        binding.progressBar.visibility = View.VISIBLE
        leadViewModel.approveOrRejectLead(leadModel.id ?: 0, AppConstant.APPROVED, reason)
    }
}