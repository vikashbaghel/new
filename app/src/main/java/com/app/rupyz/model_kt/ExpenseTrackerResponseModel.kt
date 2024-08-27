package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExpenseTrackerResponseModel(

    @field:SerializedName("data")
    var data: List<ExpenseTrackerDataItem>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null

) : Parcelable

@Entity(tableName = "expense_head_table")
@Parcelize
data class ExpenseTrackerDataItem(

    @field:SerializedName("approval_status")
    var approvalStatus: String? = null,

    @field:SerializedName("is_resubmitted")
    var isResubmitted: Boolean? = null,

    @field:SerializedName("comments")
    var comments: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("total_items")
    var totalItems: Int? = null,

    @field:SerializedName("created_by")
    var createdBy: Int? = null,

    @field:SerializedName("end_date_time")
    var endDateTime: String? = null,

    @field:SerializedName("approved_by")
    var approvedBy: Int? = null,

    @field:SerializedName("updated_at")
    var updatedAt: String? = null,

    @field:SerializedName("total_amount")
    var totalAmount: Double? = null,

    @field:SerializedName("organization")
    var organization: Int? = null,

    @field:SerializedName("created_by_name")
    var createdByName: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("updated_by")
    var updatedBy: Int? = null,

    @PrimaryKey
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("start_date_time")
    var startDateTime: String? = null,

    @field:SerializedName("user")
    var user: Int? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("source")
    var source: String? = null,

    var isSyncedToServer: Boolean? = null
) : Parcelable

@Parcelize
data class AddTotalExpenseResponseModel(

    @field:SerializedName("data")
    var data: ExpenseTrackerDataItem? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

