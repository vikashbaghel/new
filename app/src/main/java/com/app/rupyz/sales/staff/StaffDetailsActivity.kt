package com.app.rupyz.sales.staff

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityStaffDetailsBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.order.sales.StaffData
import com.google.android.material.tabs.TabLayout
import kotlin.math.roundToInt

class StaffDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffDetailsBinding
    private lateinit var staffDetailsTabLayoutAdapter: StaffDetailsTabLayoutAdapter

    private lateinit var staffData: StaffData

    private lateinit var staffViewModel: StaffViewModel

    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        initObservers()

        if (intent.hasExtra(AppConstant.STAFF_ID)) {
            binding.progressBar.visibility = View.VISIBLE
            staffViewModel.getStaffById(intent.getIntExtra(AppConstant.STAFF_ID, 0))
        }


        binding.ivCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${staffData.mobile}")
            startActivity(intent)
        }

        binding.ivWhatsApp.setOnClickListener {
            val uri =
                Uri.parse("https://api.whatsapp.com/send?phone=+91${staffData.mobile} &text=Hi, ${staffData.name}")
            val sendIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(sendIntent)
        }

        binding.ivBack.setOnClickListener { finish() }

        if (PermissionModel.INSTANCE.getPermission(
                AppConstant.EDIT_STAFF_PERMISSION,
                false
            ) && PermissionModel.INSTANCE.getPermission(AppConstant.DEACTIVATE_STAFF_PERMISSION, false)
        ) {
            binding.ivMore.visibility = View.VISIBLE
        } else {
            binding.ivMore.visibility = View.GONE
        }

        binding.ivMore.setOnClickListener { v ->
            //creating a popup menu
            val popup = PopupMenu(v.context, binding.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_edit_and_delete)

            if (!PermissionModel.INSTANCE.getPermission(AppConstant.EDIT_STAFF_PERMISSION, false)) {
                popup.menu.getItem(0).isVisible = false
            }

            if (!PermissionModel.INSTANCE.getPermission(
                    AppConstant.DEACTIVATE_STAFF_PERMISSION, false
                )
            ) {
                popup.menu.getItem(1).isVisible = false
            }

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.edit_product -> {
                        isUpdate = true
                        someActivityResultLauncher.launch(
                            Intent(
                                this,
                                AddNewStaffMemberActivity::class.java
                            ).putExtra(AppConstant.STAFF_ID, staffData.id)
                        )
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete_product -> {
                        staffViewModel.deleteStaff(staffData.id!!)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            staffViewModel.getStaffById(intent.getIntExtra(AppConstant.STAFF_ID, 0))
        }
    }

    private fun initObservers() {
        staffViewModel.getStaffByIdData().observe(this) { data ->
            data.data?.let { _ ->
                binding.progressBar.visibility = View.GONE
                binding.clMain.visibility = View.VISIBLE

                staffData = data.data

                if (!isUpdate) {
                    initTabLayout(staffData)
                }

                binding.tvName.text = staffData.name
                binding.tvId.text = staffData.employeeId
                binding.tvRole.text = staffData.roles?.get(0) ?: ""

                if (staffData.last_location_lat != null
                    && staffData.last_location_lat?.roundToInt() != 0
                    && staffData.last_location_long != null
                    && staffData.last_location_long?.roundToInt() != 0){
                    binding.groupLastLocation.visibility = View.VISIBLE

                    if (staffData.last_location_at.isNullOrEmpty().not()){
                        binding.tvLastActiveTime.text = DateFormatHelper.convertIsoToMonthAndTimeFormat(staffData.last_location_at)
                    }
                } else {
                    binding.groupLastLocation.visibility = View.GONE
                }
            }
        }

        staffViewModel.deleteStaffByIdLiveData.observe(this) {
            if (it.error == false) {
                finish()
            }
        }

        binding.ivMapLocation.setOnClickListener{
            openMap()
        }

        binding.tvLastActiveTime.setOnClickListener {
            openMap()
        }
    }

    private fun openMap() {
        Utils.openMap(
            this,
            staffData.last_location_lat,
            staffData.last_location_long,
            ""
        )
    }

    private fun initTabLayout(staffData: StaffData) {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.TARGET))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.BEAT_ROUTE))

        staffDetailsTabLayoutAdapter = StaffDetailsTabLayoutAdapter(
            supportFragmentManager, binding.tabLayout.tabCount, staffData
        )
        binding.viewPager.adapter = staffDetailsTabLayoutAdapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.viewPager.offscreenPageLimit = 3

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


}