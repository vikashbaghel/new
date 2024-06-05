package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.View
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrderPaymentDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.StringModificationUtils
import com.app.rupyz.model_kt.order.order_history.OrderData

class OrderPaymentDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderPaymentDetailsBinding

    private var orderData: OrderData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPaymentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.ORDER)) {
            orderData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.ORDER, OrderData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.ORDER)
            }

            if (orderData != null) {
                initPaymentLayout(orderData!!)
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initPaymentLayout(model: OrderData) {

        binding.tvOrderDate.text = resources.getString(R.string.order_date,
                DateFormatHelper.getMonthDate(model.createdAt))

        if (model.source.isNullOrEmpty()
                        .not() && model.source.equals(AppConstant.STORE_FRONT)
        ) {
            binding.groupStoreFront.visibility = View.VISIBLE
            binding.tvStoreFrontView.setOnClickListener {
                val fragment = InfoBottomSheetDialogFragment()
                val bundle = Bundle()
                bundle.putString(
                        AppConstant.HEADING,
                        resources.getString(R.string.storefront_order)
                )
                bundle.putString(
                        AppConstant.MESSAGE,
                        resources.getString(R.string.storefront_order_message)
                )
                fragment.arguments = bundle
                fragment.show(supportFragmentManager, AppConstant.STORE_FRONT)
            }
        } else {
            binding.groupStoreFront.visibility = View.GONE
        }


        if (model.paymentOptionCheck.isNullOrEmpty().not()) {
            if (model.paymentOptionCheck == AppConstant.CREDIT_DAYS_API) {
                binding.tvToolbarTitle.text =
                        "Credit Days " + model.remainingPaymentDays
            } else {
                when (model.paymentOptionCheck) {
                    AppConstant.FULL_PAYMENT_IN_ADVANCE_API ->
                        binding.tvToolbarTitle.text =
                                StringModificationUtils.convertCamelCase(AppConstant.FULL_PAYMENT_IN_ADVANCE.lowercase())

                    AppConstant.PARTIAL_PAYMENT_API -> {
                        binding.tvToolbarTitle.text =
                                StringModificationUtils.convertCamelCase(AppConstant.PARTIAL_PAYMENT.lowercase())
                        if (model.remainingPaymentDays != null) {
                            binding.tvRemainingDays.text = resources.getString(R.string.remaing_payment_days,
                                    "${model.remainingPaymentDays}")
                            binding.groupRemainingDays.visibility = View.VISIBLE
                        }
                    }

                    AppConstant.PAYMENT_ON_DELIVERY_API ->
                        binding.tvToolbarTitle.text =
                                StringModificationUtils.convertCamelCase(AppConstant.PAYMENT_ON_DELIVERY.lowercase())
                }
            }
        }

        binding.tvOrderNo.text = model.orderId

        model.paymentDetails?.let { paymentDetailsModel ->

            if (paymentDetailsModel.amount != null) {
                binding.tvPaymentAmount.text =
                        CalculatorHelper().convertCommaSeparatedAmount(paymentDetailsModel.amount,
                                AppConstant.TWO_DECIMAL_POINTS)
                binding.groupPaymentAmount.visibility = View.VISIBLE
            } else {
                binding.groupPaymentAmount.visibility = View.GONE
            }

            if (paymentDetailsModel.paymentMode.isNullOrEmpty().not()) {
                binding.tvPaymentMode.text = paymentDetailsModel.paymentMode
                binding.groupPaymentMode.visibility = View.VISIBLE
            } else {
                binding.groupPaymentMode.visibility = View.GONE
            }

            if (paymentDetailsModel.transactionRefNo.isNullOrEmpty().not()) {
                binding.tvTransactionRef.text = paymentDetailsModel.transactionRefNo
                binding.groupRefNo.visibility = View.VISIBLE
            } else {
                binding.groupRefNo.visibility = View.GONE
            }

        }

    }
}