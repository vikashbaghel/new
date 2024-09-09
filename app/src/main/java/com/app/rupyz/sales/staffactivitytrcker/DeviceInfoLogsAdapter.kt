package com.app.rupyz.sales.staffactivitytrcker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.DeviceActivityLogsItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.StringExtension
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.DeviceActivityListItem

class DeviceInfoLogsAdapter(
    private var data: ArrayList<DeviceActivityListItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_activity_logs_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DeviceActivityLogsItemBinding.bind(itemView)

        fun bindItem(
            model: DeviceActivityListItem
        ) {

            if (model.activityTimeStamp.isNullOrEmpty().not()) {
                binding.time.text =
                    DateFormatHelper.convertDateToTimeFormat(model.activityTimeStamp)
            }

            if (model.batteryPercent != null) {
                binding.tvBatteryPercent.text = "${model.batteryPercent}"
            }

            var viewCount = 0
            binding.groupDevice.visibility = View.GONE
            binding.tvComment.visibility = View.GONE

            if (model.activityType.isNullOrEmpty().not()) {
                if (model.activityType == AppConstant.LOG_IN) {
                    binding.tvActivityType.text = itemView.resources.getString(R.string.logged_in)
                    binding.icActivityType.setImageResource(R.drawable.ic_logged_in)
                } else if (model.activityType == AppConstant.LOG_OUT) {
                    binding.tvActivityType.text = itemView.resources.getString(R.string.logged_out)
                    binding.icActivityType.setImageResource(R.drawable.ic_logged_out)
                }

                if (model.deviceInformation?.model != null) {
                    viewCount++
                    binding.groupDevice.visibility = View.VISIBLE
                    binding.tvDevice.text =
                        "${model.deviceInformation?.manufacturer} - ${model.deviceInformation?.device}"
                } else {
                    binding.groupDevice.visibility = View.GONE
                }
            } else if (model.mockLocation != null && model.mockLocation == true) {
                binding.tvActivityType.text = itemView.resources.getString(R.string.fake_location)
                binding.icActivityType.setImageResource(R.drawable.ic_fake_location)
                binding.tvComment.visibility = View.VISIBLE
                binding.tvComment.text =
                    itemView.resources.getString(R.string.fake_location_message)
                viewCount++
            } else if (model.locationPermission != null && model.locationPermission == false) {
                binding.tvActivityType.text = itemView.resources.getString(R.string.location_off)
                binding.icActivityType.setImageResource(R.drawable.ic_location_off)

            } else if (model.locationPermissionType.isNullOrEmpty().not()) {
                if (model.locationPermissionType == AppConstant.LocationPermissionType.BACKGROUND) {
                    viewCount++
                    binding.tvActivityType.text =
                        itemView.resources.getString(R.string.permission_changed)
                    binding.icActivityType.setImageResource(R.drawable.ic_permission_changed)
                    binding.tvComment.visibility = View.VISIBLE
                    binding.tvComment.text =
                        StringExtension.getLocationPermissionChangeFromUsingTheAppToAllowText()

                } else if (model.locationPermissionType == AppConstant.LocationPermissionType.FOREGROUND) {
                    viewCount++
                    binding.tvActivityType.text =
                        itemView.resources.getString(R.string.permission_changed)
                    binding.icActivityType.setImageResource(R.drawable.ic_permission_changed)
                    binding.tvComment.visibility = View.VISIBLE
                    binding.tvComment.text =
                        StringExtension.getLocationPermissionChangeFromAllowToUsingTheAppText()

                } else if (model.locationPermissionType == AppConstant.LocationPermissionType.NOT_PROVIDED) {
                    viewCount++
                    binding.tvActivityType.text =
                        itemView.resources.getString(R.string.permission_changed)
                    binding.icActivityType.setImageResource(R.drawable.ic_permission_changed)
                    binding.tvComment.visibility = View.VISIBLE
                    binding.tvComment.text =
                        StringExtension.getLocationPermissionChangeFromAllowToDoNotAllowText()
                }
            } else if (model.internetStatus != null) {
                if (model.internetStatus == true) {
                    binding.tvActivityType.text = itemView.resources.getString(R.string.internet_on)
                    binding.icActivityType.setImageResource(R.drawable.ic_internet_on)
                } else if (model.internetStatus == false) {
                    binding.tvActivityType.text =
                        itemView.resources.getString(R.string.internet_off)
                    binding.icActivityType.setImageResource(R.drawable.ic_internet_off)
                }
            }

            if (model.isSystemPowerSaving != null && model.isSystemPowerSaving == true) {
                viewCount++
                binding.groupMobilePowerSaver.visibility = View.VISIBLE
                binding.tvMobileBatterySaverMode.text =
                    if (model.isSystemPowerSaving == true) "On" else "Off"
            } else {
                binding.groupMobilePowerSaver.visibility = View.GONE
            }

            if (model.isAppPowerSaving != null && model.isAppPowerSaving == true) {
                binding.tvMobileBatterySaverMode.text =
                    if (model.isAppPowerSaving == true) "On" else "Off"
                binding.groupAppPowerSaver.visibility = View.VISIBLE
                viewCount++
            } else {
                binding.groupAppPowerSaver.visibility = View.GONE
            }

            if (viewCount > 0) {
                binding.view1.visibility = View.VISIBLE
            } else {
                binding.view1.visibility = View.GONE
            }
        }
    }
}