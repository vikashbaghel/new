package com.app.rupyz.sales.preference

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.UserPreferencesFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.UserPreferenceData
import com.app.rupyz.ui.more.MoreViewModel
import com.google.gson.JsonObject

class UserPreferencesFragment : BaseFragment() {
    private lateinit var binding: UserPreferencesFragmentBinding
    private lateinit var moreViewModel: MoreViewModel

    private var apiNotificationValue = true
    private var apiWhatsAppEmiValue = true

    private var updatedUserPreferenceModel = UserPreferenceData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = UserPreferencesFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        initObservers()

        moreViewModel.getUserPreferencesInfo()

        initLayout()
    }

    private fun initLayout() {
        binding.switchNotification.setOnCheckedChangeListener { _, check ->
            updatedUserPreferenceModel.pushNotifications = check
            moreViewModel.setUserPreferencesInfo(updatedUserPreferenceModel)
        }

        if (isStaffUser() && SharedPref.getInstance().getBoolean(AppConstant.ENABLE_ORG_OFFLINE_MODE, false)) {
            binding.clOfflineMode.visibility = View.VISIBLE
        } else {
            binding.clOfflineMode.visibility = View.GONE
        }

        binding.tvViewStatus.setOnClickListener {
            startActivity(Intent(requireContext(), DownloadOfflineDataActivity::class.java))
        }
    }

    private fun showDialog(check: Boolean) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        if (check) {
            tvHeading.text = resources.getString(R.string.enable_offline_mode)
            tvTitle.text = resources.getString(R.string.enable_offline_mode_message)
        } else {
            tvHeading.text = resources.getString(R.string.disable_offline_mode)
            tvTitle.text = resources.getString(R.string.disable_offline_mode_message)
        }

        ivClose.setOnClickListener {
            binding.switchOfflineMode.isEnabled = true
            onResume()
            dialog.dismiss()
        }

        tvCancel.visibility = View.GONE

        if (check) {
            tvDelete.text = resources.getString(R.string.yes_enable)
        } else {
            tvDelete.text = resources.getString(R.string.yes_disable)
        }

        tvDelete.setOnClickListener {
            binding.switchOfflineMode.isEnabled = true
            if (check) {
                if (hasInternetConnection()) {
                    startActivity(Intent(requireContext(), DownloadOfflineDataActivity::class.java))
                } else {
                    showToast("No internet connection!!")
                }
            } else {
                SharedPref.getInstance().putBoolean(SharePrefConstant.ENABLE_OFFLINE_DATA, false)
                SharedPref.getInstance().putModelClass(AppConstant.ANDROID_OFFLINE_TAG, null)
                binding.switchOfflineMode.isChecked = false
                binding.tvViewStatus.visibility = View.GONE
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initObservers() {
        moreViewModel.userPreferenceLiveData.observe(requireActivity()) {

            if (it.error == false) {
                if (it.data?.pushNotifications != null) {
                    binding.switchNotification.isChecked = it.data.pushNotifications!!
                    apiNotificationValue = it.data.pushNotifications!!
                }

                if (it.data?.whatsappEmi != null) {
                    apiWhatsAppEmiValue = it.data.whatsappEmi!!
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchOfflineMode.setOnCheckedChangeListener(null)

        binding.switchOfflineMode.isChecked =
            SharedPref.getInstance().getBoolean(SharePrefConstant.ENABLE_OFFLINE_DATA, false)

        if (binding.switchOfflineMode.isChecked){
            binding.tvViewStatus.visibility = View.VISIBLE
        } else {
            binding.tvViewStatus.visibility = View.GONE
            SharedPref.getInstance().putModelClass(AppConstant.ANDROID_OFFLINE_TAG, null)
        }

        binding.switchOfflineMode.setOnCheckedChangeListener { _, check ->
            if (hasInternetConnection()) {
                binding.switchOfflineMode.isEnabled = false
                showDialog(check)
            } else {
                if (check) {
                    binding.switchOfflineMode.isChecked = false
                    showToast(resources.getString(R.string.make_sure_have_internet_connection))
                }
            }
        }
    }
}