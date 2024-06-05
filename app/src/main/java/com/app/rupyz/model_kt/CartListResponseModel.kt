package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.order.OrderTypeConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItemDiscountModel(
    var name: String? = null,
    var value: Double? = null,
    var calculated_value: Double? = null,
    var type: String? = null
) : Parcelable

@Parcelize
data class CartItem(

    var qty: Double? = null,

    @field:SerializedName("nanoid")
    val nanoid: String? = null,

    @field:SerializedName("price")
    var price: Double? = null,

    @field:SerializedName("price_after_discount")
    var priceAfterDiscount: Double? = null,

    @SerializedName("telescope_pricing")
    var telescopePricing: List<TelescopicPricingModel>? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("pics_urls")
    val picsUrls: List<String>? = null,

    @field:SerializedName("gst")
    var gst: Double? = null,

    var gstAmount: Double? = null,

    @field:SerializedName("discounted_price")
    var discountedPrice: Double? = null,

    @field:SerializedName("discount_value")
    var discountValue: Double? = null,

    @field:SerializedName("discount_details")
    var discount_details: CartItemDiscountModel? = null,

    @field:SerializedName("discount_type")
    var discountType: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("dispatch_qty")
    var dispatchQty: Double? = null,

    @field:SerializedName("total_dispatched_qty")
    var totalDispatchedQty: Double? = null,

    @field:SerializedName("category")
    var category: String? = null,

    @field:SerializedName("mrp_unit")
    var mrpUnit: String? = null,

    @field:SerializedName("packaging_unit")
    var packagingUnit: String? = null,

    @field:SerializedName("unit")
    var unit: String? = null,

    @field:SerializedName("packaging_size")
    var packagingSize: Double? = null,

    @field:SerializedName("isAddedToCart")
    var isAddedToCart: Boolean? = false,

    var isSelected: Boolean? = false,

    var isTotallyShipped: Boolean? = false,

    @field:SerializedName("isDiscountOnParticularItem")
    var isDiscountOnParticularItem: Boolean? = false,

    @field:SerializedName("is_offer_price_applied")
    var isOfferPriceApplied: Boolean? = false,

    @field:SerializedName("hsn_code")
    val hsnCode: String? = null,

    @field:SerializedName("display_pic_url")
    val displayPicUrl: String? = null,

    @field:SerializedName("product_url")
    val productUrl: String? = null,

    @field:SerializedName("qty_amount")
    val qtyAmount: Double? = null,

    @field:SerializedName("total_price")
    val totalPrice: Double? = null,

    @field:SerializedName("total_gst_amount")
    val total_gst_amount: Double? = null,

    @field:SerializedName("gst_amount")
    val gst_amount: Double? = null,

    @field:SerializedName("price_without_gst")
    val price_without_gst: Double? = null,

    @field:SerializedName("original_price")
    val original_price: Double? = null,

    @field:SerializedName("gst_calculated")
    val gst_calculated: Double? = null,

    @field:SerializedName("code")
    var code: String? = null,

    @field:SerializedName("sub_segment")
    val subSegment: String? = null,

    @field:SerializedName("industry")
    val industry: String? = null,

    @field:SerializedName("per_price")
    val perPrice: Double? = null,

    @field:SerializedName("packaging_level")
    var packagingLevel: List<PackagingLevelModel>? = null,

    var selectedPackagingLevel: PackagingLevelModel? = null,

    var updateOrder: Boolean = false,

    @field:SerializedName("segment")
    val segment: String? = null,

    @field:SerializedName("is_out_of_stock")
    var isOutOfStock: Boolean? = null,

    @field:SerializedName("gst_exclusive")
    var gst_exclusive: Boolean? = null
) : Parcelable

