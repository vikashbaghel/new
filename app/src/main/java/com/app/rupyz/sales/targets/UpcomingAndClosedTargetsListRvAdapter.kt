package com.app.rupyz.sales.targets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemUpcomingAndCloedTargetListBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class UpcomingAndClosedTargetsListRvAdapter(
    private var model: ArrayList<StaffCurrentlyActiveDataModel>,
    private var listener: ITargetProductActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_and_cloed_target_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(model[position], position, listener)
    }

    override fun getItemCount(): Int {
        return model.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUpcomingAndCloedTargetListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: StaffCurrentlyActiveDataModel,
            position: Int,
            listener: ITargetProductActionListener
        ) {

            binding.tvStaffName.text = model.name
            binding.tvAssignedBy.text = "Assigned by - ${model.createdByName}"

            binding.tvRecurring.isVisible = model.recurring ?: false

            binding.tvTargetDate.text =
                "${
                    DateFormatHelper.convertStringToCustomDateFormat(
                        model.startDate,
                        SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                    )
                } - ${
                    DateFormatHelper.convertStringToCustomDateFormat(
                        model.endDate,
                        SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                    )
                }"

            // SALES -------------
            if (model.targetSalesAmount != null) {
                var targetAmount =
                    CalculatorHelper().convertCommaSeparatedAmount(model.targetSalesAmount, AppConstant.TWO_DECIMAL_POINTS)

                if (targetAmount!!.contains(".00")) {
                    targetAmount = targetAmount.replace(".00", "")
                }
                binding.tvTargetSales.text = targetAmount

            } else {
                binding.tvTargetSales.text = " - "
            }

            if (model.currentSalesAmount != null) {
                var achievedAmount =
                    CalculatorHelper().convertCommaSeparatedAmount(model.currentSalesAmount, AppConstant.TWO_DECIMAL_POINTS)
                if (achievedAmount!!.contains(".00")) {
                    achievedAmount = achievedAmount.replace(".00", "")
                }

                binding.tvAchievedSales.text = achievedAmount
            } else {
                binding.tvAchievedSales.text = " - "
            }

            if (model.targetSalesAmount != null && model.targetSalesAmount != 0.0
                && model.currentSalesAmount != null && model.currentSalesAmount != 0.0
            ) {
                val progress =
                    (model.currentSalesAmount.toDouble() / model.targetSalesAmount.toDouble()) * 100
                binding.tvPercentSales.text = "${progress.roundToInt()}%"
            } else {
                binding.tvPercentSales.text = "0%"
            }

            // COLLECTIONS ------------------------

            if (model.targetPaymentCollection != null) {
                var targetAmount =
                    CalculatorHelper().convertCommaSeparatedAmount(model.targetPaymentCollection, AppConstant.TWO_DECIMAL_POINTS)

                if (targetAmount!!.contains(".00")) {
                    targetAmount = targetAmount.replace(".00", "")
                }
                binding.tvTargetCollections.text = targetAmount

            } else {
                binding.tvTargetCollections.text = " - "
            }

            if (model.currentPaymentCollection != null) {
                var achievedAmount =
                    CalculatorHelper().convertCommaSeparatedAmount(model.currentPaymentCollection, AppConstant.TWO_DECIMAL_POINTS)
                if (achievedAmount!!.contains(".00")) {
                    achievedAmount = achievedAmount.replace(".00", "")
                }

                binding.tvAchievedCollections.text = achievedAmount
            } else {
                binding.tvAchievedCollections.text = " - "
            }

            if (model.targetPaymentCollection != null && model.targetPaymentCollection != 0.0
                && model.currentPaymentCollection != null && model.currentPaymentCollection != 0.0
            ) {
                val progress =
                    (model.currentPaymentCollection.toDouble() / model.targetPaymentCollection.toDouble()) * 100
                binding.tvPercentCollections.text = "${progress.roundToInt()}%"
            } else {
                binding.tvPercentCollections.text = "0%"
            }


            // LEADS ------------------------

            if (model.targetNewLeads != null) {
                binding.tvTargetLead.text = "${model.targetNewLeads.toInt()}"
            } else {
                binding.tvTargetLead.text = " - "
            }

            if (model.currentNewLeads != null) {
                binding.tvAchievedLead.text = "${model.currentNewLeads.toInt()}"
            } else {
                binding.tvAchievedLead.text = " - "
            }

            if (model.targetNewLeads != null && model.targetNewLeads != 0
                && model.currentNewLeads != null && model.currentNewLeads != 0
            ) {
                val progress =
                    (model.currentNewLeads.toDouble() / model.targetNewLeads.toDouble()) * 100
                binding.tvPercentLead.text = "${progress.roundToInt()}%"
            } else {
                binding.tvPercentLead.text = "0%"
            }

            // CUSTOMER ------------------------

            if (model.targetNewCustomers != null) {
                binding.tvTargetCustomer.text = "${model.targetNewCustomers.toInt()}"
            } else {
                binding.tvTargetCustomer.text = " - "
            }

            if (model.currentNewCustomers != null) {
                binding.tvAchievedCustomer.text = "${model.currentNewCustomers.toInt()}"
            } else {
                binding.tvAchievedCustomer.text = " - "
            }

            if (model.targetNewCustomers != null && model.targetNewCustomers != 0
                && model.currentNewCustomers != null && model.currentNewCustomers != 0
            ) {
                val progress =
                    (model.currentNewCustomers.toDouble() / model.targetNewCustomers.toDouble()) * 100
                binding.tvPercentCustomer.text = "${progress.roundToInt()}%"
            } else {
                binding.tvPercentCustomer.text = "0%"
            }

            // VISITS ------------------------

            if (model.targetCustomerVisits != null) {
                binding.tvTargetVisits.text = "${model.targetCustomerVisits.toInt()}"
            } else {
                binding.tvTargetVisits.text = " - "
            }

            if (model.currentCustomerVisits != null) {
                binding.tvAchievedVisits.text = "${model.currentCustomerVisits.toInt()}"
            } else {
                binding.tvAchievedVisits.text = " - "
            }

            if (model.targetCustomerVisits != null && model.targetCustomerVisits != 0 &&
                model.currentCustomerVisits != null && model.currentCustomerVisits != 0
            ) {
                val progress =
                    (model.currentCustomerVisits.toDouble() / model.targetCustomerVisits.toDouble()) * 100
                binding.tvPercentVisits.text = "${progress.roundToInt()}%"
            } else {
                binding.tvPercentVisits.text = "0%"
            }


            // PRODUCTS -----------------------

            if (model.productMetrics.isNullOrEmpty().not()) {
                binding.clProduct.visibility = View.VISIBLE
                var percent = 0.0
                model.productMetrics?.forEach { metrics ->
                    if (metrics.currentValue != null && metrics.currentValue != 0.0 && metrics.targetValue != null && metrics.targetValue != 0.0) {
                        percent += ((metrics.currentValue.toDouble() / metrics.targetValue.toDouble()) * 100)
                    }
                }
                val totalAchieve = percent / model.productMetrics?.size!!

                binding.tvPercentProduct.text = "${totalAchieve.roundToInt()}%"
                binding.tvTargetProduct.text = "#${model.productMetrics.size}"
                binding.tvAchievedProduct.text = " - "

                binding.clProduct.setOnClickListener {
                    listener.getTargetProductDetails(model)
                }
            } else {
                binding.clProduct.visibility = View.GONE
            }

            binding.mainContent.setOnClickListener {
                binding.groupInfo.isVisible = binding.groupInfo.isVisible.not()
            }
        }
    }

    interface ITargetProductActionListener {
        fun getTargetProductDetails(model: StaffCurrentlyActiveDataModel)
    }
}