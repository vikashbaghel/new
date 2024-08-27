package com.app.rupyz.sales.lead


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityLeadDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.NewAddCustomerActivity
import com.app.rupyz.sales.home.SalesMainActivity

class LeadDetailsActivity : BaseActivity(),
    LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener,
    DeleteDialog.IOnClickListener {
    private var isDataDeleted: Boolean = false
    private lateinit var binding: ActivityLeadDetailsBinding
    private val leadViewModel: LeadViewModel by viewModels<LeadViewModel>()

    private lateinit var leadModel: LeadLisDataItem
    private var isDataUpdate = false

    private lateinit var leadDetailFragmentPagerAdapter: LeadDetailFragmentPagerAdapter

    private val fragmentList = arrayListOf(AppConstant.ALL, AppConstant.CUSTOMER_FEEDBACK)

    private lateinit var popup: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()

        binding.clCustomerTab.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.LEAD_INFO)) {
            leadModel = intent.getParcelableExtra(AppConstant.LEAD_INFO)!!

            leadViewModel.getLeadDetail(leadId = leadModel.id!!, hasInternetConnection())

            initLayout()
        } else if (intent.hasExtra(AppConstant.LEAD_ID)) {
            leadViewModel.getLeadDetail(
                leadId = intent.getIntExtra(
                    AppConstant.LEAD_ID,
                    0
                ), hasInternetConnection()
            )
        }
    }

    private fun initLayout() {
        popup = PopupMenu(this, binding.ivMore)
        //inflating menu from xml resource
        popup.inflate(R.menu.customer_action_menu)

        if (PermissionModel.INSTANCE.getPermission(
                AppConstant.EDIT_LEAD_PERMISSION, false
            ).not()
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
                    DeleteDialog.showDeleteDialog(
                        this,
                        leadModel.id,
                        0,
                        resources.getString(R.string.delete_lead),
                        resources.getString(R.string.delete_lead_message),
                        this
                    )
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
                        NewAddCustomerActivity::class.java
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
            onBackPressedDispatcher.onBackPressed()
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
        binding.tvBasicDetails.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            )
        )
        binding.tvRecordActivity.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.theme_purple
            )
        )

        binding.tvBasicDetails.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.expense_dark_gray
            )
        )
        binding.tvRecordActivity.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            )
        )

    }

    private fun changeBasicDetailsTabColor() {
        binding.tvBasicDetails.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.theme_purple
            )
        )
        binding.tvRecordActivity.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            )
        )

        binding.tvBasicDetails.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.white
            )
        )
        binding.tvRecordActivity.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.expense_dark_gray
            )
        )

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


    private fun initObservers() {
        leadViewModel.addLeadLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                intent.putExtra(AppConstant.DELETE_LEAD, true)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        leadViewModel.leadDetailLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { model ->

                    leadModel = model
                    initLayout()

                    if (!model.businessName.isNullOrEmpty()) {
                        binding.tvToolbarTitle.text = model.businessName?.replaceFirstChar(
                            Char::titlecase
                        )
                    }

                    if (PermissionModel.INSTANCE.getPermission(
                            AppConstant.EDIT_LEAD_PERMISSION, false
                        ) ||
                        PermissionModel.INSTANCE.getPermission(
                            AppConstant.DELETE_LEAD_PERMISSION,
                            false
                        )
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

                binding.clCustomerTab.visibility = View.VISIBLE
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
                binding.btnConvertToCustomer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.lead_action_text
                    )
                )
            }

            AppConstant.STATUS_CONVERTED_TO_CUSTOMER.lowercase() -> {
                binding.btnLayout.visibility = View.INVISIBLE
                binding.btnConvertToCustomer.visibility = View.VISIBLE
                binding.btnConvertToCustomer.text =
                    resources.getString(R.string.already_a_customer)
                binding.btnConvertToCustomer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.lead_action_text
                    )
                )
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

    override fun onDelete(model: Any, position: Any) {
        isDataDeleted = true
        binding.progressBar.visibility = View.VISIBLE
        leadViewModel.deleteLead(model as Int, hasInternetConnection())
    }
}