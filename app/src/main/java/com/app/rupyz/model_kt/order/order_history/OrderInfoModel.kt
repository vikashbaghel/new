package com.app.rupyz.model_kt.order.order_history

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.CustomerTypeConverters
import com.app.rupyz.databse.order.OrderTypeConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.DeviceInformationModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderInfoModel(

        @field: SerializedName("data")
        var data: List<OrderData>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@TypeConverters(OrderTypeConverter::class,
        CustomerTypeConverters::class,
        PicMapListTypeConverter::class)
@Entity(tableName = "order_table")
@Parcelize
data class OrderData(

        @field:SerializedName("amount")
        var amount: Double? = null,

        @field:SerializedName("purchase_order_url")
        var purchaseOrderUrl: String? = null,

        @field:SerializedName("address")
        var address: Address? = null,

        @field:SerializedName("purchase_order")
        var purchaseOrder: Int? = null,

        @field:SerializedName("discount_amount")
        var discountAmount: Double? = null,

        @field:SerializedName("payment_status")
        var paymentStatus: String? = null,

        @field:SerializedName("delivery_charges")
        var deliveryCharges: Double? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("source")
        var source: String? = null,

        @field:SerializedName("created_by")
        var createdBy: CreatedBy? = null,

        @field:SerializedName("gst_amount")
        var gstAmount: Double? = null,

        @field:SerializedName("total_amount")
        var totalAmount: Double? = null,

        @field:SerializedName("organization")
        var organization: Int? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("discount_details")
        var discountDetails: ArrayList<CartItemDiscountModel>? = null,

        @field:SerializedName("dispatch_history_list")
        var dispatchHistoryList: ArrayList<DispatchHistoryListModel>? = null,

        @field:SerializedName("invoice")
        var invoice: Int? = null,

        @field:SerializedName("remaining_payment_days")
        var remainingPaymentDays: Int? = null,

        @field:SerializedName("payment_option_check")
        var paymentOptionCheck: String? = null,

        @field:SerializedName("is_paid")
        var isPaid: Boolean? = null,

        @field:SerializedName("is_fully_shipped")
        var isFullyShipped: Boolean? = null,

        @field:SerializedName("is_closed")
        var isClosed: Boolean? = null,

        @field:SerializedName("order_id")
        var orderId: String? = null,

        @field:SerializedName("charges_details")
        var chargesDetails: ArrayList<CartItemDiscountModel>? = null,

        @field:SerializedName("items")
        var items: ArrayList<CartItem>? = null,

        @field:SerializedName("delivery_status")
        var deliveryStatus: String? = null,

        @field:SerializedName("comment")
        var comment: String? = null,

        @field:SerializedName("admin_comment")
        var adminComment: String? = null,

        @field:SerializedName("customer_level")
        var customerLevel: String? = null,

        @field:SerializedName("order_images_info")
        var orderImagesInfo: ArrayList<PicMapModel>? = null,

        @field:SerializedName("reject_reason")
        var rejectReason: String? = null,

        @field:SerializedName("customer")
        var customer: CustomerData? = null,

        @field:SerializedName("customer_id")
        var customerId: Int? = null,

        @field:SerializedName("fullfilled_by")
        var fullFilledBy: CustomerData? = null,

        @field:SerializedName("fullfilled_by_id")
        var fullFilledById: Int? = null,

        @field:SerializedName("payment_details")
        var paymentDetails: PaymentDetailsModel? = null,

        var orderStatusChange: Boolean? = null,

        @field:SerializedName("payment_mode")
        var paymentMode: String? = null,

        @field:SerializedName("transaction_ref_no")
        var transactionRefNo: String? = null,

        @field:SerializedName("payment_amount")
        var paymentAmount: Double? = null,

        var customerName: String? = null,

        var customerPaymentTerms: String? = null,

        @field:SerializedName("order_images")
        var orderImages: ArrayList<Int>? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,

        var isSyncedToServer: Boolean? = null,

        var isCustomerIdUpdated: Boolean? = null,

        @field:SerializedName("battery_optimization")
        var batteryOptimisation: Boolean? = null,

        @field:SerializedName("location_permission")
        var locationPermission: Boolean? = null,

        @field:SerializedName("device_information")
        var deviceInformation: DeviceInformationModel? = null,

        @field:SerializedName("battery_percent")
        var batteryPercent: Int? = null,
        
        @field:SerializedName("geo_address")
        var geoAddress: String? = null

) : Parcelable


@Parcelize
data class PaymentDetailsModel(

        @field:SerializedName("status")
        var status: String? = null,

        @field:SerializedName("amount")
        var amount: Double? = null,

        @field:SerializedName("payment_mode")
        var paymentMode: String? = null,

        @field:SerializedName("transaction_ref_no")
        var transactionRefNo: String? = null,

        @field:SerializedName("payment_number")
        var paymentNumber: String? = null
) : Parcelable


@Parcelize
data class Address(

        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("pincode")
        var pincode: String? = null,

        @field:SerializedName("city")
        var city: String? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("address_line_1")
        var addressLine1: String? = null,

        @field:SerializedName("state")
        var state: String? = null,

        @field:SerializedName("address_line_2")
        var addressLine2: String? = null
) : Parcelable

@Parcelize
data class CreatedBy(

        @field:SerializedName("last_name")
        var lastName: String? = null,

        @field:SerializedName("first_name")
        var firstName: String? = null,

        @field:SerializedName("email")
        var email: String? = null
) : Parcelable


@Parcelize
data class DispatchHistoryListModel(
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("created_by_name")
        var createdByName: String? = null,
) : Parcelable

@Parcelize
data class CreateOrderResponseModel(
        @field:SerializedName("data")
        var data: OrderData? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable






