package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeadCategoryResponseModel(
    @field:SerializedName("data")
    val data: LeadCategoryDataItem? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null

) : Parcelable


@Parcelize
data class LeadCategoryListResponseModel(
    @field:SerializedName("data")
    var data: ArrayList<LeadCategoryDataItem>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null

) : Parcelable

@Entity(tableName = "lead_category_table")
@Parcelize
data class LeadCategoryDataItem(
    @field:SerializedName("name")
    var name: String? = null,

    @PrimaryKey
    @field:SerializedName("id")
    var id: Int? = null,

    var isSelected: Boolean? = null
) : Parcelable
