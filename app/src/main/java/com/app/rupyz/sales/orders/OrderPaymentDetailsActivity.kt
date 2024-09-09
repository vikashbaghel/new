package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.View
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrderPaymentDetailsBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.orders.adapter.DiscountListAdapter
import com.app.rupyz.sales.orders.adapter.OtherChargesAdapter

class OrderPaymentDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderPaymentDetailsBinding

    private var orderData: OrderData? = null
    private var productData: CartItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPaymentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutPayment.hideView()
        binding.layoutProduct.hideView()

        val value = intent.getIntExtra(AppConstant.PRODUCT, 0)

        if (value == 1) {
            binding.layoutProduct.visibility()
            binding.layoutPayment.hideView()

        } else {
            binding.layoutPayment.visibility()
            binding.layoutProduct.hideView()
        }


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



        if (intent.hasExtra(AppConstant.PRODUCT_DETAILS)) {
            productData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.PRODUCT_DETAILS, CartItem::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.PRODUCT_DETAILS)
            }

            if (productData != null) {
                initProductLayout(productData!!)
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initProductLayout(model: CartItem) {
        binding.apply {
            tvToolbarTitle.text = resources.getString(R.string.product_de)

            tvPriceAmount.text = CalculatorHelper().convertLargeAmount(
                model.price ?: 0.0,
                AppConstant.TWO_DECIMAL_POINTS
            )
            tvOrderName.text = model.name.toString()
            if (model.variantName != null) {
                tvVarNo.text = model.variantName.toString()
            } else {
                tvVarNo.text = ""
                tvVariant.gone()
            }

            if (model.discountValue != null) {
                tvDisAmount.text = CalculatorHelper().convertLargeAmount(
                    model.discountValue ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
                val netRateAmount = model.price!!.toDouble().minus(model.discountValue!!.toDouble())
                tvNetAmount.text = CalculatorHelper().convertLargeAmount(
                    netRateAmount,
                    AppConstant.TWO_DECIMAL_POINTS
                )
            } else {
                tvDisAmount.text = "0"
                tvNetAmount.text =
                    CalculatorHelper().convertLargeAmount(
                        model.price ?: 0.0,
                        AppConstant.TWO_DECIMAL_POINTS
                    )

            }

            tvCatNo.text = model.category.toString()
            tvCodeNo.text = model.code.toString()
            tvNetAmountValue.text = CalculatorHelper().convertLargeAmount(
                model.totalPrice ?: 0.0,
                AppConstant.TWO_DECIMAL_POINTS
            )
            tvGstAmount.text = CalculatorHelper().convertLargeAmount(
                model.gst_amount ?: 0.0,
                AppConstant.TWO_DECIMAL_POINTS
            )
            if (model.gst_amount != null) {
                val totalAmount = (model.totalPrice ?: 0.0).plus(model.gst_amount)
                tvPdTotalAmountValue.text =
                    CalculatorHelper().convertLargeAmount(
                        totalAmount,
                        AppConstant.TWO_DECIMAL_POINTS
                    )
            } else {
                tvPdTotalAmountValue.text = tvNetAmountValue.text.toString()
            }

            ImageUtils.loadImage(
                model.displayPicUrl, ivProduct
            )
            val cal = CalculatorHelper().calculateQuantity(model.qty!! * model.packagingSize!!)
            tvQtyNo.text = buildString {
                append(cal)
                append(" ")
                append(model.unit?.replaceFirstChar(Char::titlecase))
            }
        }

    }


    private fun initPaymentLayout(model: OrderData) {
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

        val discountAdapter = DiscountListAdapter(model.discountDetails!!)
        binding.recyclerviewDiscount.adapter = discountAdapter
        discountAdapter.notifyDataSetChanged()

        binding.tvToolbarTitle.text = resources.getString(R.string.payment_details)
        val otherChargesAdapter = OtherChargesAdapter(model.chargesDetails!!)
        binding.recyclerviewOther.adapter = otherChargesAdapter
        discountAdapter.notifyDataSetChanged()

        binding.tvSubTotalAmount.text = CalculatorHelper().convertLargeAmount(
            ((model.amount ?: 0.0).plus(model.gstAmount ?: 0.0)),
            AppConstant.TWO_DECIMAL_POINTS
        )

        binding.tvGrossAmountData.text = CalculatorHelper().convertLargeAmount(
            (model.amount ?: 0.0).plus(model.discountAmount ?: 0.0),
            AppConstant.TWO_DECIMAL_POINTS
        )

        if (model.taxesInfo?.igstAmount != null) {
            binding.tvIgstNo.text =
                CalculatorHelper().convertLargeAmount(
                    model.taxesInfo?.igstAmount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
        }
        if (model.taxesInfo?.sgstAmount != null) {
            binding.tvSgstNo.text =
                CalculatorHelper().convertLargeAmount(
                    model.taxesInfo?.sgstAmount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
        }
        if (model.taxesInfo?.cgstAmount != null) {
            binding.tvCgstNo.text =
                CalculatorHelper().convertLargeAmount(
                    model.taxesInfo?.cgstAmount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
        }

        if (model.amount != null) {
            binding.tvTxtNo.text =
                CalculatorHelper().convertLargeAmount(
                    model.amount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
        }


        val totalCharge = model.deliveryCharges ?: 0.0
        val totalDiscount = model.discountAmount ?: 0.0

        binding.tvTotalDiscountAmount.text =
            CalculatorHelper().convertLargeAmount(totalDiscount, AppConstant.TWO_DECIMAL_POINTS)

        binding.tvTotalAmountOther.text =
            CalculatorHelper().convertLargeAmount(totalCharge, AppConstant.TWO_DECIMAL_POINTS)
        binding.tvTotalAmount.text = buildString {
            append(
                CalculatorHelper().convertLargeAmount(
                    model.totalAmount ?: 0.0,
                    AppConstant.TWO_DECIMAL_POINTS
                )
            )
        }

        model.paymentDetails?.let { paymentDetailsModel ->

            if (paymentDetailsModel.amount != null) {
                binding.tvPaymentAmount.text =
                    CalculatorHelper().convertCommaSeparatedAmount(
                        paymentDetailsModel.amount,
                        AppConstant.TWO_DECIMAL_POINTS
                    )
            } else {
                binding.groupPaymentAmount.visibility = View.GONE
            }

            if (paymentDetailsModel.paymentMode.isNullOrEmpty().not()) {
                binding.tvPaymentMode.text = paymentDetailsModel.paymentMode
            } else {
                binding.groupPaymentMode.visibility = View.GONE
            }

            if (paymentDetailsModel.transactionRefNo.isNullOrEmpty().not()) {
                binding.tvTransactionRef.text = paymentDetailsModel.transactionRefNo
            } else {
                binding.groupRefNo.visibility = View.GONE
            }

        }

    }
}