package com.app.rupyz.model_kt.order.payment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.databse.order.OrderTypeConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.databse.payments.PaymentTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecordPaymentInfoModel(

        @field: SerializedName("data")
        var data: List<RecordPaymentData>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable

@Parcelize
data class RecordPaymentDetailModel(

        @field: SerializedName("data")
        var data: RecordPaymentData? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,
) : Parcelable

@TypeConverters(PaymentTypeConverter::class, OrderTypeConverter::class,
        IntListConverter::class, PicMapListTypeConverter::class)
@Entity(tableName = "payment_table")
@Parcelize
data class RecordPaymentData(

        @field:SerializedName("amount")
        var amount: Double? = null,

        @field:SerializedName("payment_mode")
        var paymentMode: String? = null,

        @field:SerializedName("transaction_ref_no")
        var transactionRefNo: String? = null,

        @field:SerializedName("payment_number")
        var paymentNumber: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("reject_reason")
        var rejectReason: String? = null,

        @field:SerializedName("created_by")
        var createdBy: CreatedBy? = null,

        @field:SerializedName("payment_approved_at")
        var paymentApprovedAt: String? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("organization")
        var organization: Int? = null,

        @field:SerializedName("updated_by")
        var updatedBy: CreatedBy? = null,

        @field:SerializedName("comment")
        var comment: String? = null,

        @field:SerializedName("transaction_timestamp")
        var transactionTimeStamp: String? = null,

        @field:SerializedName("customer_id")
        var customerId: Int? = null,

        @field:SerializedName("payment_images")
        var paymentImages: ArrayList<Int>? = null,

        @field:SerializedName("payment_images_info")
        var paymentImagesInfo: ArrayList<PicMapModel>? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("customer")
        var customer: Customer? = null,

        @field:SerializedName("status")
        var status: String? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,
        
        @field:SerializedName("source")
        var source: String? = null,

        @field:SerializedName("geo_address")
        var geoAddress: String? = null,

        var isSyncedToServer: Boolean? = null,

        var isCustomerIdUpdated: Boolean? = null,
) : Parcelable

@Parcelize
data class Customer(
        @field:SerializedName("customer_type")
        var customerType: String? = null,

        @field:SerializedName("city")
        var city: String? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("mobile")
        var mobile: String? = null,

        @field:SerializedName("state")
        var state: String? = null,

        @field:SerializedName("contact_person_name")
        var contactPersonName: String? = null,

        @field:SerializedName("gstin")
        var gstin: String? = null,

        @field:SerializedName("profile_logo")
        var profileLogo: String? = null,

        @field:SerializedName("email")
        var email: String? = null
) : Parcelable
