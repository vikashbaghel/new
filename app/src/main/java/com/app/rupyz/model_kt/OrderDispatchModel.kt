package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.order.OrderTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DispatchedOrderDetailsModel(
    @field:SerializedName("data")
    var data: DispatchedOrderModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@Parcelize
data class DispatchedOrderListModel(
    @field:SerializedName("data")
    val data: List<DispatchedOrderModel>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@TypeConverters(OrderTypeConverter::class)
@Entity(tableName = "order_dispatched_table")
@Parcelize
data class DispatchedOrderModel(
    @field:SerializedName("carry_remaining_order")
    var carryRemainingOrder: Boolean? = null,

    @field:SerializedName("is_closed")
    var isClosed: Boolean? = null,

    @field:SerializedName("order_number")
    var order_number: String? = null,

    @field:SerializedName("created_by_name")
    var createdByName: String? = null,

    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("order_id")
    var orderId: Int? = null,

    @PrimaryKey
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("total_amount")
    var totalAmount: Double? = null,

    @field:SerializedName("order")
    var order: Int? = null,

    @field:SerializedName("lr_no")
    var lrNo: String? = null,

    @field:SerializedName("invoice_number")
    var invoice_number: String? = null,

    @field:SerializedName("transporter_name")
    var transporter_name: String? = null,

    @field:SerializedName("transporter_mobile_number")
    var transporter_mobile_number: String? = null,

    @field:SerializedName("driver_mobile_number")
    var driver_mobile_number: String? = null,

    @field:SerializedName("broker_information")
    var broker_information: String? = null,

    @field:SerializedName("payment_information")
    var payment_information: String? = null,

    @field:SerializedName("freight_amount")
    var freight_amount: Double? = null,

    @field:SerializedName("vehicle_number")
    var vehicle_number: String? = null,

    @field:SerializedName("driver_name")
    var driver_name: String? = null,

    @field:SerializedName("notes")
    var notes: String? = null,

    @field:SerializedName("dispatch_order_file_url")
    var dispatchOrderFileUrl: String? = null,

    @Ignore
    @field:SerializedName("lr_images")
    var lrImages: ArrayList<Int>? = null,

    @Ignore
    @field:SerializedName("pics")
    var pics: ArrayList<PicMapModel>? = null,

    @Ignore
    @field:SerializedName("lr_images_url")
    val lrImagesUrl: ArrayList<PicMapModel>? = null,

    @field:SerializedName("items")
    var items: ArrayList<CartItem>? = null
) : Parcelable