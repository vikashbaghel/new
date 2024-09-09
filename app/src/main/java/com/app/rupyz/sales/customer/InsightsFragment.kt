package com.app.rupyz.sales.customer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.rupyz.databinding.FragmentInsightsBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.DataInsights


class InsightsFragment : BaseFragment() {


    private lateinit var binding: FragmentInsightsBinding
    private val customerInsightsModel: CustomerInsightsModel by viewModels()
    private var customerId: Int? = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsightsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customerId = arguments?.getInt(AppConstant.CUSTOMER_ID)
        initObservers()


    }
    
    
    
    override fun onResume() {
        super.onResume()
        customerInsightsModel.getCustomerInsightsData(customerId!!, hasInternetConnection())
    }
    
    private fun initObservers() {
        binding.progressBar.visibility = View.VISIBLE
        customerInsightsModel.getCustomerInsights().observe(requireActivity()) { data ->
            binding.progressBar.visibility = View.GONE

            if (data.error == false) {
                if (data.data != null) {
                    dataValue(data.data)
                }


            } else {
                showToast(data.message)
            }
        }

    }

    private fun dataValue(data: DataInsights) {
        binding.apply {
            tvPcCount.text = data.lifetimePcCount.toString()
            tvTcCount.text = data.lifetimeTcCount.toString()
            if((data.lifetimePcCount != null && data.lifetimeTcCount != null) && (data.lifetimePcCount != 0 && data.lifetimeTcCount != 0)){
                try {
                    val output = ((data.lifetimePcCount.toDouble()/ data.lifetimeTcCount.toDouble()) * 100 )
                    tvOutputCount.text = buildString {
                        if (output > 99) { append(output.toInt()) } else{ append(CalculatorHelper().calculateQuantity(output)) }
                        append("%")
                    }
                }catch (e: Exception){
                    tvOutputCount.text = buildString {
                        append(CalculatorHelper().calculateQuantity(0.0))
                        append("%")
                    }
                }
            }else{
                tvOutputCount.text = buildString {
                    append(CalculatorHelper().calculateQuantity(0.0))
                    append("%")
                }
            }
            

            if (data.lastVisitDate != null) {
                tvLastVisitDate.text = DateFormatHelper.getMonthDate(data.lastVisitDate)
            }

            if (data.lastOrderDate != null) {
                tvLastOrderDate.text = DateFormatHelper.getMonthDate(data.lastOrderDate)
            }
            tvPcOutput.text =
                CalculatorHelper().convertLargeAmount(
                    data.totalOrderAmount
                        ?: 0.0, AppConstant.TWO_DECIMAL_POINTS
                )


            tvLastOrderValue.text =
                CalculatorHelper().convertLargeAmount(
                    data.lastOrderValue
                        ?: 0.0, AppConstant.TWO_DECIMAL_POINTS
                )

            tvAvgOrderValueHistoric.text =

                CalculatorHelper().convertLargeAmount(
                    data.avgOrderValueHistoric
                        ?: 0.0, AppConstant.TWO_DECIMAL_POINTS
                )

        }
    }
}