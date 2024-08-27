package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.OrderItemCartListItemBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.PackagingLevelModel

class OrderItemListAdapter(
    private var data: ArrayList<CartItem>?,
    private var listener: IOnItemListener
     ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item_cart_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position], position,listener)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun filterActivityList(filterList: ArrayList<CartItem>) {
        data = filterList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = OrderItemCartListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "StringFormatInvalid")
        fun bindItem(model: CartItem, position: Int,listener: IOnItemListener) {
            if (model.price == null) {
                model.price = model.priceAfterDiscount
            }

            binding.tvOrderName.text = model.name?.replaceFirstChar(Char::titlecase)

            if (model.code != null) {
                binding.tvOrderNanoId.text = model.code
            } else {
                binding.tvOrderNanoId.text = ""
            }

            if (model.category != null) {
                if (model.variantName!=null)
                {
                    binding.tvOrderCat.text = model.variantName+" | "+model.category?.replaceFirstChar(Char::titlecase)

                }
                else
                {
                    binding.tvOrderCat.text =model.category?.replaceFirstChar(Char::titlecase)+" "+ CalculatorHelper().calculateQuantity(model.qty)

                }
               // binding.tvOrderCategory.text = model.category?.replaceFirstChar(Char::titlecase)

            } else {
               // binding.tvOrderCategory.text = ""
                binding.tvOrderCat.text = ""
            }

            if (model.totalPrice != null) {
                val doublePrice = model.totalPrice
                binding.tvOrderTotalPrice.text =
                    (CalculatorHelper().convertLargeAmount(
                        doublePrice,
                        AppConstant.FOUR_DECIMAL_POINTS
                    ))
            } else {
                binding.tvOrderTotalPrice.text = "0.0"
            }


            binding.tvPackagingSize.text =
                CalculatorHelper().calculateQuantity(model.qty)


            if (model.packagingUnit != null) {
                binding.tvUnit.text = model.packagingUnit?.replaceFirstChar(Char::titlecase)
            } else {
                binding.tvUnit.text = ""
            }

            if (model.gst != null) {
                binding.hdGst.text =
                    CalculatorHelper().calculateGst(model.gst!!, model.gst_exclusive!!)
                        .replace(":", "")

                binding.tvGst.text =
                    CalculatorHelper().convertCommaSeparatedAmount(
                        model.gst_amount ?: 0.0,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )
            }

            if (model.selectedPackagingLevel == null) {
                val packagingLevelModel = PackagingLevelModel()
                packagingLevelModel.unit = model.packagingUnit
                packagingLevelModel.size = model.packagingSize

                model.selectedPackagingLevel = packagingLevelModel
            }

            ImageUtils.loadImage(model.displayPicUrl, binding.ivProduct)
            binding.ivPackage.setOnClickListener {
                listener.onItemData(model)
            }

            if (model.discount_details != null && model.discount_details?.type != null) {
                model.priceAfterDiscount = model.price
                model.price = model.original_price

                binding.groupDiscount.visibility = View.VISIBLE
                binding.tvOrderPrice.text =
                    CalculatorHelper().convertLargeAmount(
                        model.priceAfterDiscount!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

                model.discount_details?.let {
                    if (it.type?.lowercase() == AppConstant.DISCOUNT_TYPE_OFFER_PRICE.lowercase()) {
                        binding.hdDiscount.text =
                            itemView.resources.getString(R.string.offer_price_applied)
                        binding.tvDiscountAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmount(
                                it.value,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )
                    } else if (it.type?.lowercase() == AppConstant.DISCOUNT_TYPE_RUPEES.lowercase()
                        || it.type?.lowercase() == "rupees"
                    ) {
                        binding.hdDiscount.text = itemView.resources.getString(R.string.discount)
                        binding.tvDiscountAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmount(
                                it.value,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )
                    } else {
                        binding.hdDiscount.text =
                            "Discount (${CalculatorHelper().calculateQuantity(it.value)}%)"

                        binding.tvDiscountAmount.text =
                            CalculatorHelper().convertCommaSeparatedAmount(
                                model.discountValue ?: 0.0,
                                AppConstant.FOUR_DECIMAL_POINTS
                            )
                    }
                }

                binding.groupDiscountedPriceAfterDiscount.visibility = View.VISIBLE

                binding.tvOrderDiscountedPrice.text =
                    CalculatorHelper().convertLargeAmount(
                        model.price!!,
                        AppConstant.FOUR_DECIMAL_POINTS
                    )

            } else {
                binding.groupDiscount.visibility = View.GONE
                binding.tvOrderPrice.text = CalculatorHelper().convertLargeAmount(
                    model.price!!,
                    AppConstant.FOUR_DECIMAL_POINTS
                )
                binding.groupDiscountedPriceAfterDiscount.visibility = View.GONE
            }


            var rotationAngle = 0

            binding.clMoreDetails.setOnClickListener {
                binding.clOtherDetails.isVisible = binding.clOtherDetails.isVisible.not()

                rotationAngle = if (rotationAngle == 0) 180 else 0

                binding.ivDetails.animate().rotation(rotationAngle.toFloat()).setDuration(300)
                    .start()

            }

            if (model.packagingSize != null && model.qty != null) {
                val cal = CalculatorHelper().calculateQuantity(model.qty!! * model.packagingSize!!)

                binding.tvOrderQty.text = buildString {
                    append(
                        itemView.context.getString(
                            R.string.product_quantity_without_pre_text_string1
                        )
                    )
                    append(" ")
                    append(cal)
                    append(" ")
                    append(model.unit?.replaceFirstChar(Char::titlecase))
                }
            }
        }

    }}

interface IOnItemListener
{
    fun onItemData(model:CartItem)
}