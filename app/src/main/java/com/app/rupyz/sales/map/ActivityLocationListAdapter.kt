package com.app.rupyz.sales.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemActivityLocationBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.toCamelCaseWithSpaces
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_DISTRIBUTOR_VISIT
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_OFFICE_VISIT
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CustomerFollowUpDataItem

class ActivityLocationListAdapter(
    private var data: MutableList<CustomerFollowUpDataItem>,
    private var listener: ICustomerFeedbackActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_location, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemActivityLocationBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: CustomerFollowUpDataItem,
            listener: ICustomerFeedbackActionListener,
        ) {
            binding.tvActivityName.text = model.feedbackType

            if (model.moduleType == AppConstant.ATTENDANCE) {
                if (model.action == AppConstant.ATTENDANCE_CHECK_IN) {
                    binding.tvActivityName.text =
                        itemView.resources.getString(R.string.start_day)
                    model.label = itemView.resources.getString(R.string.start_day)
                } else if (model.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                    binding.tvActivityName.text = itemView.resources.getString(R.string.end_day)
                    model.label = itemView.resources.getString(R.string.end_day)

                }

                if (model.subModuleType.isNullOrEmpty().not()) {
                    when (model.subModuleType) {
                        ACTIVITY_TYPE_OFFICE_VISIT -> {
                            binding.tvActivityName.text =
                                "${binding.tvActivityName.text} - ${itemView.resources.getString(R.string.ho_visit)}"
                        }

                        ACTIVITY_TYPE_DISTRIBUTOR_VISIT -> {
                            binding.tvActivityName.text = "${binding.tvActivityName.text} - ${
                                itemView.resources.getString(
                                    R.string.distributor_visit,
                                    SharedPref.getInstance()
                                        .getString(AppConstant.CUSTOMER_LEVEL_1),
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                )
                            }"
                        }

                        else -> {
                            binding.tvActivityName.text =
                                "${binding.tvActivityName.text} - ${model.subModuleType?.toCamelCaseWithSpaces()}"
                        }
                    }

                } else if (model.isAutoTimeOut != null && model.isAutoTimeOut == true) {
                    binding.tvActivityName.visibility = View.VISIBLE
                    binding.tvActivityName.text =
                        itemView.resources.getString(R.string.day_end_automatically)
                } else {
                    binding.tvActivityName.visibility = View.GONE
                }

            } else {
                when (model.moduleType) {
                    AppConstant.CUSTOMER_FEEDBACK -> {
                        model.label = "${model.customerName} - ${model.feedbackType}"
                    }

                    AppConstant.LEAD_FEEDBACK -> {
                        model.label = "${model.businessName} - ${model.feedbackType}"
                    }

                    AppConstant.ORDER_DISPATCH -> {
                        binding.tvActivityName.text = model.moduleType
                    }

                    AppConstant.PAYMENT -> {
                        binding.tvActivityName.text =
                            itemView.resources.getString(R.string.payment_collected)
                    }

                    else -> {
                        binding.tvActivityName.text =
                            model.action?.lowercase()
                                ?.replaceFirstChar(Char::titlecase) + " " + model.moduleType
                    }
                }
            }

            if (model.geoAddress.isNullOrEmpty().not()) {
                binding.tvLocation.text = model.geoAddress
                binding.tvLocation.visibility = View.VISIBLE
            } else if (model.geoLocationLat != null && model.geoLocationLong != null) {
                GeoLocationUtils.getAddress(
                    itemView.context,
                    model.geoLocationLat!!,
                    model.geoLocationLong!!
                ) { address ->
                    binding.tvLocation.text = address
                }

                binding.tvLocation.visibility = View.VISIBLE
            } else {
                binding.tvLocation.visibility = View.GONE
            }

            binding.marker.setImageResource(getActivityIconForMarker(itemView.context, model).first)
            binding.marker.backgroundTintList = ColorStateList.valueOf(
                getActivityIconForMarker(itemView.context, model).second
            )

            binding.markerText.text = DateFormatHelper.convertDateToTimeFormat(model.createdAt)

            binding.root.setOnClickListener {
                listener.getFeedbackDetails(model)
            }
        }

        private fun getActivityIconForMarker(
            context: Context,
            model: CustomerFollowUpDataItem
        ): Pair<Int, Int> {
            var label: Pair<Int, Int> = Pair(0, 0)

            when (model.moduleType) {
                AppConstant.ATTENDANCE -> {
                    if (model.action == AppConstant.ATTENDANCE_CHECK_IN) {
                        label = Pair(
                            R.drawable.ic_map_day_started,
                            context.resources.getColor(R.color.color_CCF0CC)
                        )
                    } else if (model.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                        label = Pair(
                            R.drawable.ic_map_day_ended,
                            context.resources.getColor(R.color.color_D6D6D6)
                        )
                    }
                }

                AppConstant.CUSTOMER_FEEDBACK,
                AppConstant.LEAD_FEEDBACK -> {
                    label = Pair(
                        R.drawable.ic_map_new_activity,
                        context.resources.getColor(R.color.color_F0DDCE)
                    )
                }

                AppConstant.ORDER -> {
                    label = Pair(
                        R.drawable.ic_map_new_order,
                        context.resources.getColor(R.color.color_CEDEF0)
                    )
                }

                AppConstant.PAYMENT -> {
                    label = Pair(
                        R.drawable.ic_map_new_payment,
                        context.resources.getColor(R.color.color_CCF0CC)
                    )
                }

                AppConstant.CUSTOMER -> {
                    label = Pair(
                        R.drawable.ic_map_new_customer,
                        context.resources.getColor(R.color.color_CEDEF0)
                    )
                }

                AppConstant.LEAD -> {
                    label = Pair(
                        R.drawable.ic_map_new_lead,
                        context.resources.getColor(R.color.color_CEDEF0)
                    )
                }

                else -> {
                    label = Pair(
                        R.drawable.ic_map_new_activity,
                        context.resources.getColor(R.color.color_F0DDCE)
                    )
                }
            }

            return label
        }
    }

    interface ICustomerFeedbackActionListener {
        fun getFeedbackDetails(model: CustomerFollowUpDataItem)
    }
}