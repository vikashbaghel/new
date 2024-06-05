package com.app.rupyz.sales.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySalesMainBinding
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.base.BrowserActivity
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.network.ApiInterface
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_INFO
import com.app.rupyz.generic.utils.SharePrefConstant.PROFILE_IMAGE
import com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.StringModificationUtils
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.OrganizationInfoModel
import com.app.rupyz.model_kt.UserInfoData
import com.app.rupyz.sales.notification.NotificationActivity
import com.app.rupyz.sales.notification.NotificationViewModel
import com.app.rupyz.sales.organization.AddNewAdminActivity
import com.app.rupyz.sales.organization.JoinOrganizationActivity
import com.app.rupyz.sales.organization.OrganizationViewModel
import com.app.rupyz.sales.reminder.ReminderListActivity
import com.app.rupyz.sales.reminder.RemindersViewModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.more.ReportSettingActivity
import com.app.rupyz.ui.more.SettingActivity
import com.app.rupyz.ui.organization.onboarding.activity.BusinessDetailsActivity
import com.app.rupyz.ui.user.UserInfoActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class SalesMainActivity : BaseActivity(), OrganizationListAdapter.IOrgSelectListener,
        LocationPermissionUtils.ILocationPermissionListener, SalesFragment.UpdateMainDataListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivitySalesMainBinding
    private lateinit var mApiInterface: ApiInterface
    private var mUtil: Utility? = null

    private var orgList: ArrayList<OrganizationInfoModel> = ArrayList()
    private lateinit var organizationListAdapter: OrganizationListAdapter

    private var doubleBackToExitPressedOnce = false
    private lateinit var userModel: UserInfoData
    private lateinit var organizationViewModel: OrganizationViewModel

    private var profileSelectedPosition = -1

    private lateinit var notificationViewModel: NotificationViewModel
    private val reminderViewModel: RemindersViewModel by viewModels()
    private val activityViewModel: StaffActivityViewModel by viewModels()

    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private lateinit var mainFragmentPagerAdapter: MainFragmentPagerAdapter

    private val fragmentList = arrayListOf(
            AppConstant.HOME, AppConstant.ACTIVITY_TYPE, AppConstant.EXPENSE_STATUS, AppConstant.BEAT
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]

        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        mApiInterface = ApiClient.getRetrofit().create(
                ApiInterface::class.java
        )

        mUtil = Utility(this)

        initObservers()
        initLayout()
        initData()
    }

    private fun initRecyclerView() {
        userModel = Gson().fromJson(
                SharedPref.getInstance().getString(USER_INFO), UserInfoData::class.java
        )

        orgList.clear()

        orgList.addAll(userModel.orgIds)
        binding.navigationLayout.rvOrgList.layoutManager = LinearLayoutManager(this)
        organizationListAdapter = OrganizationListAdapter(orgList, this)
        binding.navigationLayout.rvOrgList.adapter = organizationListAdapter
    }

    private fun initLayout() {

        if (isStaffUser) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding.mainContent.navHeaderView.ivBurgerMenu.visibility = View.GONE
            binding.mainContent.navHeaderView.ivUser.setOnClickListener {
                updateProfileResultLauncher.launch(Intent(this, NavigationActivity::class.java))
            }
        } else {
            binding.mainContent.navHeaderView.ivUser.setOnClickListener {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }

            binding.mainContent.navHeaderView.ivBurgerMenu.setOnClickListener {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        initBottomNavigation()

        binding.mainContent.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.mainContent.container.isUserInputEnabled = false

        binding.navigationLayout.tvCompanyName.setOnClickListener {
            if (binding.navigationLayout.clOrgList.isVisible) {
                binding.navigationLayout.clOrgList.visibility = View.GONE
            } else {
                binding.navigationLayout.clOrgList.visibility = View.VISIBLE
            }
        }

        binding.navigationLayout.clNavigation.setOnClickListener {
            if (binding.navigationLayout.clOrgList.isVisible) {
                binding.navigationLayout.clOrgList.visibility = View.GONE
            }
        }


        binding.navigationLayout.llMyBusiness.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount >= 1) {
                supportFragmentManager.popBackStack()
            }
            closeDrawer()
        }

        binding.navigationLayout.clJoinNewOrg.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, JoinOrganizationActivity::class.java))
            }, 300)

        }

        binding.navigationLayout.clAddNewOrg.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(
                        Intent(this, BusinessDetailsActivity::class.java)
                )
            }, 300)

        }

        binding.navigationLayout.clAddNewAdmin.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(
                        Intent(this, AddNewAdminActivity::class.java)
                )
            }, 300)

        }

        binding.navigationLayout.llSetting.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, SettingActivity::class.java))
            }, 300)
        }

        binding.navigationLayout.llReportSetting.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, ReportSettingActivity::class.java))
            }, 300)
        }

        binding.navigationLayout.llMyProfile.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                someActivityResultLauncher.launch(Intent(this, UserInfoActivity::class.java))
            }, 300)

        }

        binding.navigationLayout.llShareApp.setOnClickListener {
            closeDrawer()

            Handler(Looper.myLooper()!!).postDelayed({
                Utility.shareAppUsingBitmap(
                        this, BitmapFactory.decodeResource(resources, R.drawable.whats_app_share)
                )
            }, 300)
        }

        binding.navigationLayout.llRateApp.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                Utility.rateApp(this)
            }, 300)
        }

        binding.navigationLayout.llTermsCondition.setOnClickListener {
            closeDrawer()

            Handler(Looper.myLooper()!!).postDelayed({
                initOpenBrowser(
                        AppConstant.TERMS_URL, "Terms of Service"
                )
            }, 300)
        }

        binding.navigationLayout.llPrivacyPolicy.setOnClickListener {
            closeDrawer()
            Handler(Looper.myLooper()!!).postDelayed({
                initOpenBrowser(
                        AppConstant.POLICY_URL, "Privacy Policy"
                )
            }, 300)
        }

        binding.navigationLayout.llLogout.setOnClickListener {
            closeDrawer()
            logout()
        }

        binding.navigationLayout.llMore.setOnClickListener {
            closeDrawer()

            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(
                        Intent(
                                this, FragmentContainerActivity::class.java
                        ).putExtra(AppConstant.KNOW_MORE_TYPE, true)
                )
            }, 300)
        }

        binding.navigationLayout.llProfileSetting.setOnClickListener {
            closeDrawer()

            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(
                        Intent(
                                this, FragmentContainerActivity::class.java
                        ).putExtra(AppConstant.PROFILE_SLUG, true)
                )
            }, 300)
        }

        reminderViewModel.getReminderList(AppConstant.TODAY, null, 1)
        notificationViewModel.getNotificationList(1)

        binding.mainContent.navHeaderView.ivNotification.setOnClickListener {
            if (hasInternetConnection()) {
                notificationActivityResultLauncher.launch(
                        Intent(
                                this, NotificationActivity::class.java
                        )
                )
            } else {
                showToast(resources.getString(R.string.this_feature_isn_t_available_offline))
            }
        }

        binding.mainContent.navHeaderView.tvNotificationCount.setOnClickListener {
            if (hasInternetConnection()) {
                notificationActivityResultLauncher.launch(
                        Intent(
                                this, NotificationActivity::class.java
                        )
                )
            } else {
                showToast(resources.getString(R.string.this_feature_isn_t_available_offline))
            }
        }

        binding.mainContent.navHeaderView.ivReminder.setOnClickListener {
            if (hasInternetConnection()) {
                reminderActivityResultLauncher.launch(
                        Intent(
                                this, ReminderListActivity::class.java
                        )
                )
            } else {
                showToast(resources.getString(R.string.this_feature_isn_t_available_offline))
            }
        }

        binding.mainContent.navHeaderView.tvReminderCount.setOnClickListener {
            if (hasInternetConnection()) {
                reminderActivityResultLauncher.launch(
                        Intent(
                                this, ReminderListActivity::class.java
                        )
                )
            } else {
                showToast(resources.getString(R.string.this_feature_isn_t_available_offline))
            }
        }

        activityViewModel.getFollowUpList()
    }

    private fun initBottomNavigation() {
        mainFragmentPagerAdapter = MainFragmentPagerAdapter(this, fragmentList, this)
        binding.mainContent.container.adapter = mainFragmentPagerAdapter

        binding.mainContent.container.currentItem = 0
        binding.mainContent.bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    private var updateProfileResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            organizationViewModel.getProfileInfo()
        }
    }

    private var notificationActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            notificationViewModel.getNotificationList(1)
        }
    }

    private var reminderActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            notificationViewModel.getNotificationList(1)
        }
    }


    private fun initOpenBrowser(url: String, title: String) {
        val intent = Intent(this, BrowserActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    private fun closeDrawer() {
        if (binding.navigationLayout.clOrgList.isVisible) {
            binding.navigationLayout.clOrgList.visibility = View.GONE
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun initData() {
        if (SharedPref.getInstance().getInt(ORG_ID) == 0) {
            logout()
        } else if (isStaffUser) {

            if (SharedPref.getInstance().getString(PROFILE_IMAGE).isNullOrEmpty().not()) {
                binding.mainContent.navHeaderView.userPrefix.visibility = View.GONE
                ImageUtils.loadImage(
                        SharedPref.getInstance().getString(PROFILE_IMAGE),
                        binding.mainContent.navHeaderView.ivUser
                )
            } else if (SharedPref.getInstance().getString(LEGAL_NAME).isNullOrEmpty().not()) {
                try {
                    binding.mainContent.navHeaderView.userPrefix.text =
                            StringHelper.printName(SharedPref.getInstance().getString(LEGAL_NAME))
                                    .trim().substring(0, 1)
                    binding.mainContent.navHeaderView.userPrefix.visibility = View.VISIBLE
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        } else if (SharedPref.getInstance().getString(ORG_INFO).isNullOrEmpty().not()) {
            val orgModel = Gson().fromJson(
                    SharedPref.getInstance().getString(ORG_INFO), OrganizationInfoModel::class.java
            )

            if (!orgModel.legalName.isNullOrEmpty()) {
                binding.navigationLayout.tvUserPrefix.text =
                        StringHelper.printName(orgModel.legalName).trim { it <= ' ' }.substring(0, 1)

                binding.mainContent.navHeaderView.userPrefix.text =
                        StringHelper.printName(orgModel.legalName).trim { it <= ' ' }.substring(0, 1)

                if (orgModel.logoImageUrl.isNullOrEmpty().not()) {
                    ImageUtils.loadImage(
                            orgModel.logoImageUrl, binding.mainContent.navHeaderView.ivCompanyLogo
                    )
                    binding.mainContent.navHeaderView.toolbarCompanyName.visibility = View.GONE
                    binding.mainContent.navHeaderView.ivCompanyLogo.visibility = View.VISIBLE
                } else {
                    binding.mainContent.navHeaderView.toolbarCompanyName.visibility = View.VISIBLE
                    binding.mainContent.navHeaderView.ivCompanyLogo.visibility = View.GONE

                    binding.mainContent.navHeaderView.toolbarCompanyName.text =
                            StringModificationUtils.convertCamelCase(orgModel.legalName.lowercase())
                }

                binding.navigationLayout.tvCompanyName.text =
                        StringModificationUtils.convertCamelCase(orgModel.legalName.lowercase())

            }

            initRecyclerView()
        } else {
            organizationViewModel.getProfileInfo()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> binding.mainContent.container.currentItem = 0
            R.id.navigation_activity -> binding.mainContent.container.currentItem = 1
            R.id.navigation_expenses -> binding.mainContent.container.currentItem = 2
            R.id.beat -> binding.mainContent.container.currentItem = 3
        }
        return true
    }

    private fun initObservers() {
        organizationViewModel.profileLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { mData ->

                    if (isStaffUser) {
                        if (mData.profilePicUrl.isNullOrEmpty().not()) {
                            binding.mainContent.navHeaderView.userPrefix.visibility = View.GONE
                            ImageUtils.loadImage(
                                    mData.profilePicUrl, binding.mainContent.navHeaderView.ivUser
                            )
                        }
                    }
                    if (mData.orgIds.size > 0) {
                        mData.orgIds[0].isSelected = true
                        if (profileSelectedPosition == -1) {
                            mData.orgIds[0].isSelected = true
                            SharedPref.getInstance().putModelClass(ORG_INFO, mData.orgIds[0])
                        } else {
                            mData.orgIds[profileSelectedPosition].isSelected = true
                            SharedPref.getInstance()
                                    .putModelClass(ORG_INFO, mData.orgIds[profileSelectedPosition])
                        }
                    }

                    SharedPref.getInstance().putModelClass(USER_INFO, mData)

                    initData()
                }
            }
        }

        notificationViewModel.notificationLiveData.observe(this) {
            if (it.error == false) {
                if (it.data?.unreadCount != null && it.data.unreadCount != 0) {
                    binding.mainContent.navHeaderView.tvNotificationCount.text =
                            "${it.data.unreadCount}"
                    binding.mainContent.navHeaderView.tvNotificationCount.visibility = View.VISIBLE
                } else {
                    binding.mainContent.navHeaderView.tvNotificationCount.visibility = View.GONE
                }
            }
        }

        reminderViewModel.reminderListLiveData.observe(this) {
            if (it.error == false) {
                if (it.data?.count != null && it.data.count != 0) {
                    binding.mainContent.navHeaderView.tvReminderCount.text =
                            "${it.data.count}"
                    binding.mainContent.navHeaderView.tvReminderCount.visibility = View.VISIBLE
                } else {
                    binding.mainContent.navHeaderView.tvReminderCount.visibility = View.GONE
                }
            }
        }

        activityViewModel.getFollowUpListLiveData.observe(this) { model ->
            if (model.error == false && model.data.isNullOrEmpty().not()) {
                DatabaseLogManager.getInstance().insetFeedbackList(model.data!!)
            }
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            organizationViewModel.getProfileInfo()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (binding.mainContent.container.currentItem == 0) {
            if (doubleBackToExitPressedOnce) {
                onBackPressedDispatcher.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true

            showToast(resources.getString(R.string.alert_press_again))

            Handler(Looper.getMainLooper()).postDelayed(
                    { doubleBackToExitPressedOnce = false }, 2000
            )
        } else {
            binding.mainContent.bottomNavigationView.selectedItemId = R.id.navigation_home
        }


    }

    override fun onOrgSelect(org: OrganizationInfoModel, position: Int) {
        profileSelectedPosition = position
        userModel.orgIds.forEachIndexed { index, organizationInfoModel ->
            organizationInfoModel.isSelected = index == position
        }

        SharedPref.getInstance().putModelClass(USER_INFO, userModel)

        SharedPref.getInstance().putModelClass(ORG_INFO, org)
        SharedPref.getInstance().putInt(ORG_ID, org.id!!)

        Handler(Looper.myLooper()!!).postDelayed({
            closeDrawer()
        }, 300)

        initData()
        initBottomNavigation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationPermissionUtils.setActivityResult(resultCode, requestCode, data)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionUtils.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun updateCompanyLogo(model: UserInfoData) {
        if (this@SalesMainActivity.isFinishing.not()) {
            try {
                if (model.logoImage.isNullOrEmpty().not()) {
                    binding.mainContent.navHeaderView.ivCompanyLogo.visibility = View.VISIBLE
                    binding.mainContent.navHeaderView.toolbarCompanyName.visibility = View.GONE
                    ImageUtils.loadCustomImage(
                            model.logoImage,
                            binding.mainContent.navHeaderView.ivCompanyLogo,
                            R.mipmap.ic_rupyz_logo_header
                    )
                } else if (model.orgName.isNullOrEmpty().not()) {
                    binding.mainContent.navHeaderView.ivCompanyLogo.visibility = View.GONE
                    binding.mainContent.navHeaderView.toolbarCompanyName.visibility = View.VISIBLE
                    binding.mainContent.navHeaderView.toolbarCompanyName.text =
                            StringModificationUtils.convertCamelCase(model.orgName!!.lowercase())
                }

                if (model.profilePicUrl.isNullOrEmpty().not()) {
                    binding.mainContent.navHeaderView.toolbarCompanyName.visibility = View.GONE
                    binding.mainContent.navHeaderView.userPrefix.visibility = View.GONE
                    ImageUtils.loadImage(
                            model.profilePicUrl, binding.mainContent.navHeaderView.ivUser
                    )
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

}