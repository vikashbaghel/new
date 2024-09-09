package com.app.rupyz.model_kt.order.dashboard

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DashboardIndoModel(

    @field: SerializedName("data")
    var data: DashboardData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable

@Parcelize
@Entity(tableName = "dashboard_table")
data class DashboardData(

    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("products")
    var products: Int? = null,

    @field:SerializedName("customers")
    var customers: Int? = null,

    @field:SerializedName("orders")
    var orders: Int? = null,

    @field:SerializedName("staff")
    var staff: Int? = null,

    @field:SerializedName("beat")
    var beat: Int? = null,

    @field:SerializedName("distributor_count")
    var distributorCount: Int? = null,

    @field:SerializedName("segment_count")
    var segmentCount: Int? = null,

    @field:SerializedName("lead")
    var leadCount: Int? = null,

    @field:SerializedName("payment")
    var payment: Int? = null,

    @field:SerializedName("reimbursementtracker")
    var reimbursementTracker: Int? = null,

    @field:SerializedName("reimbursement")
    var reimbursement: Int? = null,

    @field:SerializedName("customer_address")
    var customerAddress: Int? = null,

    @field:SerializedName("order_dispatch")
    var orderDispatch: Int? = null
) : Parcelable