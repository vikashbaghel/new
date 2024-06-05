package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCustomerActivityBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.toCamelCaseWithSpaces
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_DISTRIBUTOR_VISIT
import com.app.rupyz.generic.utils.AppConstant.ACTIVITY_TYPE_OFFICE_VISIT
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import kotlin.math.roundToInt

class CustomerActivityAdapter(
        private var data: ArrayList<CustomerFollowUpDataItem>,
        private var listener: ICustomerFeedbackActionListener,
        private var customerType: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_customer_activity, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCustomerActivityBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
                model: CustomerFollowUpDataItem,
                listener: ICustomerFeedbackActionListener,
        ) {
            binding.tvActivityName.text = model.feedbackType

            binding.tvDate.text =
                    DateFormatHelper.convertIsoToDateAndTimeFormat(model.createdAt)

            if (model.picsUrls.isNullOrEmpty().not()) {
                binding.ivImageExist.visibility = View.VISIBLE
            } else {
                binding.ivImageExist.visibility = View.GONE
            }

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
                    binding.tvSubModuleType.visibility = View.VISIBLE

                    when(model.subModuleType){
                        ACTIVITY_TYPE_OFFICE_VISIT -> {
                            binding.tvSubModuleType.text = itemView.resources.getString(R.string.ho_visit)
                        }

                        ACTIVITY_TYPE_DISTRIBUTOR_VISIT -> {
                            binding.tvSubModuleType.text = itemView.resources.getString(R.string.distributor_visit,
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1),
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2))
                        }
                        else -> {
                            binding.tvSubModuleType.text = model.subModuleType?.toCamelCaseWithSpaces()
                        }
                    }

                } else  if (model.isAutoTimeOut != null && model.isAutoTimeOut == true) {
                    binding.tvSubModuleType.visibility = View.VISIBLE
                    binding.tvSubModuleType.text = itemView.resources.getString(R.string.day_end_automatically)
                } else {
                    binding.tvSubModuleType.visibility = View.GONE
                }

            } else {

                binding.tvSubModuleType.visibility = View.VISIBLE
                if (!model.businessName.isNullOrEmpty()) {
                    binding.tvSubModuleType.text = model.businessName
                    model.label = "${model.businessName} - ${model.moduleType}"
                } else if (!model.customerName.isNullOrEmpty()) {
                    binding.tvSubModuleType.text = model.customerName
                    model.label = "${model.customerName} - ${model.moduleType}"
                } else {
                    binding.tvSubModuleType.visibility = View.GONE
                }

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

//            if (model.message.isNullOrEmpty().not()) {
//                binding.tvComment.text = model.message
//                binding.tvComment.visibility = View.VISIBLE
//            } else {
//                binding.tvComment.visibility = View.GONE
//            }

            val address =
                    model.city?.replaceFirstChar(Char::titlecase) + " , " + model.state?.replaceFirstChar(
                            Char::titlecase
                    ) + " - " + model.pincode
            val adStr = address.replace("null", "")

            if (adStr.trim().length > 5) {
                binding.tvLocation.text = adStr
                binding.tvLocation.visibility = View.VISIBLE
            } else {
                binding.tvLocation.visibility = View.GONE
            }

            if (model.geoLocationLat != null && model.geoLocationLat?.roundToInt() != 0) {
                binding.ivMapLocation.visibility = View.VISIBLE
            } else {
                binding.ivMapLocation.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                listener.getFeedbackDetails(model)
            }

            binding.ivMapLocation.setOnClickListener {
                listener.getMapLocation(model)
            }
        }
    }

    interface ICustomerFeedbackActionListener {
        fun getFeedbackDetails(model: CustomerFollowUpDataItem)
        fun getMapLocation(model: CustomerFollowUpDataItem)
    }
}