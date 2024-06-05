package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerFeedbackListResponseModel(

        @field:SerializedName("data")
        var data: ArrayList<CustomerFeedbackStringItem>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null,

        var errorCode: Int? = null
) : Parcelable


@Parcelize
@Entity(tableName = "customer_feedback_list_table")
data class CustomerFeedbackStringItem(
        @PrimaryKey
        @field:SerializedName("id")
        var id: Int = 0,

        @field:SerializedName("name")
        var stringValue: String? = null
): Parcelable
