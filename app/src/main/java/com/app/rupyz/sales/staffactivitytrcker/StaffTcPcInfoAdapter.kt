package com.app.rupyz.sales.staffactivitytrcker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffTcPcInfoItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.toCamelCaseWithSpaces
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.StaffTcPcInfoModelItem

class StaffTcPcInfoAdapter(
    private var data: ArrayList<StaffTcPcInfoModelItem>,
    private var activityType: String,
    private var listener: StaffTcPcInfoListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staff_tc_pc_info_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], activityType, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StaffTcPcInfoItemBinding.bind(itemView)

        fun bindItem(
            model: StaffTcPcInfoModelItem,
            activityType: String,
            listener: StaffTcPcInfoListener
        ) {
            binding.tvStaffName.text = model.customerName


            if (activityType == AppConstant.STAFF_TC_INFO) {
                binding.tvOrderValue.visibility = View.INVISIBLE
            } else {
                if (model.orderValue != null) {
                    binding.tvOrderValue.text = CalculatorHelper().convertLargeAmount(
                        model.orderValue,
                        AppConstant.TWO_DECIMAL_POINTS
                    )
                }
                binding.tvOrderValue.visibility = View.VISIBLE
            }

            if (model.picsUrls.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.picsUrls, binding.ivProfile)
            } else {
                binding.ivProfile.setImageResource(R.mipmap.ic_user_default)
            }

            if (model.checkIn.isNullOrEmpty().not()) {
                binding.tvStaffCheckInTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.checkIn)
            } else {
                binding.tvStaffCheckInTime.text =
                    itemView.resources.getString(R.string.empty_duration_for_total_time)
            }

            if (model.checkOut.isNullOrEmpty().not()) {
                binding.tvStaffCheckOutTime.text =
                    DateFormatHelper.convertDateToTimeFormat(model.checkOut)
            } else {
                binding.tvStaffCheckOutTime.text =
                    itemView.resources.getString(R.string.empty_duration_for_total_time)
            }

            if (model.geoAddress.isNullOrEmpty().not()) {
                binding.tvLocation.text = model.geoAddress
                binding.groupLocation.visibility = View.VISIBLE
            } else if (model.geoLocationLat != null && model.geoLocationLat != 0.0
                && model.geoLocationLong != null && model.geoLocationLong != 0.0
            ) {
                GeoLocationUtils.getAddress(
                    itemView.context,
                    model.geoLocationLat,
                    model.geoLocationLong
                ) {
                    binding.tvLocation.text = it
                    binding.groupLocation.visibility = View.VISIBLE
                }
            } else {
                binding.groupLocation.visibility = View.GONE
            }

            if (model.geoLocationLat != null && model.geoLocationLat != 0.0
                && model.geoLocationLong != null && model.geoLocationLong != 0.0
            ) {
                binding.tvLocation.setTextColor(itemView.resources.getColor(R.color.out_of_stock_red))

                binding.tvLocation.setOnClickListener {
                    listener.getLocationInfo(model)
                }
            }

            if (model.customerLogoUrl.isNullOrEmpty().not()) {
                ImageUtils.loadImage(model.customerLogoUrl, binding.ivProfile)
            }

            if (model.activityType.isNullOrEmpty().not()) {
                binding.tvActivityType.text = model.activityType?.toCamelCaseWithSpaces()
            }

            if (model.comments.isNullOrEmpty().not()) {
                binding.tvComment.text = model.comments
                binding.tvComment.visibility = View.VISIBLE
            } else {
                binding.tvComment.visibility = View.GONE
            }

            itemView.setOnClickListener {
                listener.getActivityDetails(model)
            }
        }
    }

    interface StaffTcPcInfoListener {
        fun getLocationInfo(model: StaffTcPcInfoModelItem)
        fun getActivityDetails(model: StaffTcPcInfoModelItem)
    }
}