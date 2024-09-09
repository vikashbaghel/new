package com.app.rupyz.ui.more

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.databinding.ActivitySettingBinding
import com.app.rupyz.generic.helper.disable
import com.app.rupyz.generic.helper.enable
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.CHECK_IMAGE_INPUT
import com.app.rupyz.generic.utils.AppConstant.CHECK_IMAGE_REQUIRED
import com.app.rupyz.generic.utils.AppConstant.CHECK_IN
import com.app.rupyz.generic.utils.AppConstant.CUSTOMER_LEVEL_ORDER
import com.app.rupyz.generic.utils.AppConstant.END_DAY_PHOTO_MANDATE
import com.app.rupyz.generic.utils.AppConstant.START_DAY_PHOTO_MANDATE
import com.app.rupyz.generic.utils.AppConstant.TELEPHONIC_ORDER
import com.app.rupyz.generic.utils.SharePrefConstant.STAFF_AND_CUSTOMER_MAPPING
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CustomerLevelConfigModel
import com.app.rupyz.model_kt.PreferenceData


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var moreViewModel: MoreViewModel

    private var minimumOrderAmount = 0

    private val updatePreferencesModel = PreferenceData()

    private var customerLevelOneName: String? = ""
    private var customerLevelTwoName: String? = ""
    private var customerLevelThreeName: String? = ""

    var rotationAngle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        initObservers()

        binding.mainContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        moreViewModel.getPreferencesInfo()

        binding.switchSecondLevelOrdering.setOnCheckedChangeListener { _, check ->

            if (check.not()) {
                binding.switchAutoDelivered.isEnabled = false
                binding.switchAutoApproved.isEnabled = false
                binding.switchAutoDelivered.isChecked = false
                binding.switchAutoApproved.isChecked = false
            } else {
                binding.switchAutoDelivered.isEnabled = true
                binding.switchAutoApproved.isEnabled = true
            }
        }

        binding.switchDisableCheck.setOnCheckedChangeListener { _, check ->
            if (check.not()) {
                binding.switchDisableImageInput.isChecked = false
                binding.switchDisableMandatory.isChecked = false
                binding.switchEnableTelephonicOrder.isChecked = false
                
                binding.switchDisableImageInput.disable()
                binding.switchDisableMandatory.disable()
                binding.switchEnableTelephonicOrder.disable()
            } else {
                binding.switchDisableImageInput.enable()
                binding.switchDisableMandatory.enable()
                binding.switchEnableTelephonicOrder.enable()
            }
        }

        binding.switchLocationTracking.setOnCheckedChangeListener { _, check ->

            if (check.not()) {
                binding.switchLiveLocation.isEnabled = false
                binding.switchGeoFencing.isEnabled = false
                binding.switchLiveLocation.isChecked = false
                binding.switchGeoFencing.isChecked = false
            } else {
                binding.switchLiveLocation.isEnabled = true
                binding.switchGeoFencing.isEnabled = true
            }
        }

        binding.switchDisableMandatory.setOnCheckedChangeListener { _, check ->
            if (check) {
                binding.switchDisableImageInput.isChecked = true
               // binding.switchDisableImageInput.isEnabled  = true
            }
        }
   
        binding.switchDisableImageInput.setOnCheckedChangeListener { _, check ->
            if (check.not()) {
                binding.switchDisableMandatory.isChecked = false
            }
        }

        binding.switchAutoDelivered.setOnCheckedChangeListener { _, check ->
            if (check) {
                binding.switchAutoApproved.isChecked = true
            }
        }

        binding.switchAutoApproved.setOnCheckedChangeListener { _, check ->
            if (check.not()) {
                binding.switchAutoDelivered.isChecked = false
            }
        }


        binding.etMinimumAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    updatePreferencesModel.minimumOrderAmount = p0.toString().toInt()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.view6.setOnClickListener {
            binding.customerLevelOrderSubModule.isVisible =
                binding.customerLevelOrderSubModule.isVisible.not()

            if (binding.clCustomerLevelNames.isVisible) {
                binding.clCustomerLevel.performClick()
            }

            rotationAngle = if (rotationAngle == 0) 180 else 0

            binding.iv8.animate().rotation(rotationAngle.toFloat()).setDuration(300).start()

            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding.clCustomerLevel.setOnClickListener {
            binding.clCustomerLevelNames.isVisible =
                binding.clCustomerLevelNames.isVisible.not()

            rotationAngle = if (rotationAngle == 0) 180 else 0

            binding.ivCustomerLevel.animate().rotation(rotationAngle.toFloat()).setDuration(300)
                .start()

            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        binding.btnSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            updatePreferencesModel.staffCustomerMapping = binding.switchMapping.isChecked
            updatePreferencesModel.enableHierarchyManagement = binding.switchHierarchyManagement.isChecked
            updatePreferencesModel.locationTracking = binding.switchLocationTracking.isChecked
            updatePreferencesModel.activityGeoFencing = binding.switchGeoFencing.isChecked
            updatePreferencesModel.locationTracking = binding.switchLiveLocation.isChecked
            updatePreferencesModel.liveLocationTracking = binding.switchLiveLocation.isChecked
            updatePreferencesModel.enableRolesPermission = binding.switchRolePermission.isChecked
            updatePreferencesModel.disableGalleryPhoto = binding.switchDisableGalleryPhoto.isChecked
            updatePreferencesModel.activityCheckInImageRequired = binding.switchDisableMandatory.isChecked
            updatePreferencesModel.activityCheckInShowImageInput = binding.switchDisableImageInput.isChecked
            updatePreferencesModel.activityCheckInRequired = binding.switchDisableCheck.isChecked
            updatePreferencesModel.enableAnalyticsCalculation = binding.switchAnalyticsView.isChecked
            updatePreferencesModel.enableCustomerCategoryMapping = binding.switchCategoryMapping.isChecked
            updatePreferencesModel.blockScreenshotsInProducts = binding.switchProductScreenshot.isChecked
            updatePreferencesModel.enableCustomerLevelOrder = binding.switchSecondLevelOrdering.isChecked
            updatePreferencesModel.autoDispatchOrders = binding.switchAutoDelivered.isChecked
            updatePreferencesModel.autoApproveOrders = binding.switchAutoApproved.isChecked
            updatePreferencesModel.autoApproveBeatPlan = binding.switchAutoApprovedBeatPlan.isChecked
            updatePreferencesModel.allowOfflineMode = binding.switchOfflineMode.isChecked
            updatePreferencesModel.activityAllowTelephonicOrder = binding.switchEnableTelephonicOrder.isChecked
            updatePreferencesModel.mandatePhotoOnStartDay = binding.scMandatePhotoWhileDayStartDay.isChecked
            updatePreferencesModel.mandatePhotoOnEndDay = binding.scMandatePhotoWhileDayEndDay.isChecked
            updatePreferencesModel.minimumOrderAmount = binding.etMinimumAmount.text.toString().toInt()
            val configModel = updatePreferencesModel.customerLevelConfig ?: CustomerLevelConfigModel()
            configModel.LEVEL_1 = binding.etCustomerLevelOne.text.toString()
            configModel.LEVEL_2 = binding.etCustomerLevelTwo.text.toString()
            configModel.LEVEL_3 = binding.etCustomerLevelThree.text.toString()
            updatePreferencesModel.customerLevelConfig = configModel

            binding.progressBar.visibility = View.VISIBLE
            SharedPref.getInstance().putBoolean(CHECK_IN, updatePreferencesModel.activityCheckInRequired!!)
            SharedPref.getInstance().putBoolean(START_DAY_PHOTO_MANDATE, updatePreferencesModel.mandatePhotoOnStartDay!!)
            SharedPref.getInstance().putBoolean(END_DAY_PHOTO_MANDATE, updatePreferencesModel.mandatePhotoOnEndDay!!)
            SharedPref.getInstance().putBoolean(TELEPHONIC_ORDER, updatePreferencesModel.activityAllowTelephonicOrder!!)
            SharedPref.getInstance().putBoolean(CUSTOMER_LEVEL_ORDER, updatePreferencesModel.enableCustomerLevelOrder!!)
            moreViewModel.updatePreferences(updatePreferencesModel)
            SharedPref.getInstance().putBoolean(CHECK_IMAGE_REQUIRED, updatePreferencesModel.activityCheckInImageRequired!!)
            SharedPref.getInstance().putBoolean(CHECK_IMAGE_INPUT, updatePreferencesModel.activityCheckInShowImageInput!!)
            SharedPref.getInstance().putBoolean(AppConstant.GEO_FENCING_ENABLE, updatePreferencesModel.activityGeoFencing!!)

        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.imgClose.setOnClickListener { finish() }
    }


    private fun initObservers() {

        moreViewModel.preferenceLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let {
                    if (it.staffCustomerMapping != null) {

                        binding.switchMapping.isChecked = it.staffCustomerMapping ?: true
                        binding.switchLocationTracking.isChecked = it.locationTracking ?: false
                        binding.switchLiveLocation.isChecked = it.locationTracking ?: false
                        binding.switchLiveLocation.isChecked = it.liveLocationTracking ?: false
                        binding.switchRolePermission.isChecked = it.enableRolesPermission ?: false
                        binding.switchGeoFencing.isChecked = it.activityGeoFencing ?: false
                        binding.switchHierarchyManagement.isChecked = it.enableHierarchyManagement ?: true
                        binding.switchDisableGalleryPhoto.isChecked = it.disableGalleryPhoto ?: false
                        binding.switchDisableCheck.isChecked = it.activityCheckInRequired ?: false
                        binding.switchDisableMandatory.isChecked = it.activityCheckInImageRequired ?: false
                        binding.switchDisableImageInput.isChecked = it.activityCheckInShowImageInput ?: false
                        binding.switchAnalyticsView.isChecked = it.enableAnalyticsCalculation ?: false
                        binding.switchCategoryMapping.isChecked = it.enableCustomerCategoryMapping ?: false
                        binding.switchSecondLevelOrdering.isChecked = it.enableCustomerLevelOrder ?: false
                        binding.switchAutoDelivered.isChecked = it.autoDispatchOrders ?: false
                        binding.switchAutoApproved.isChecked = it.autoApproveOrders ?: false
                        binding.switchProductScreenshot.isChecked = it.blockScreenshotsInProducts ?: false
                        binding.switchAutoApprovedBeatPlan.isChecked = it.autoApproveBeatPlan ?: false
                        binding.switchOfflineMode.isChecked = it.allowOfflineMode ?: false
                        binding.switchEnableTelephonicOrder.isChecked =  it.activityAllowTelephonicOrder?:false
                        binding.scMandatePhotoWhileDayStartDay.isChecked = it.mandatePhotoOnStartDay?:false
                        binding.scMandatePhotoWhileDayEndDay.isChecked = it.mandatePhotoOnEndDay?:false

                        SharedPref.getInstance().putBoolean(STAFF_AND_CUSTOMER_MAPPING, it.staffCustomerMapping ?: true)

                        it.customerLevelConfig?.let { level ->
                            updatePreferencesModel.customerLevelConfig = level
                            customerLevelOneName = level.LEVEL_1
                            binding.etCustomerLevelOne.setText(level.LEVEL_1)
                            customerLevelTwoName = level.LEVEL_2
                            binding.etCustomerLevelTwo.setText(level.LEVEL_2)
                            customerLevelThreeName = level.LEVEL_3
                            binding.etCustomerLevelThree.setText(level.LEVEL_3)
                        }
                    }

                    if (it.minimumOrderAmount != null) {
                        minimumOrderAmount = it.minimumOrderAmount!!
                        binding.etMinimumAmount.setText(it.minimumOrderAmount.toString())
                    } else {
                        binding.etMinimumAmount.setText("0")
                    }

                    binding.mainContent.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "${data.message}", Toast.LENGTH_SHORT).show()
            }
        }

        moreViewModel.updatePreferenceLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}