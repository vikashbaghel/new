package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerTypeResponseModel(

    @field:SerializedName("data")
    var data: List<CustomerTypeDataItem>? = null,

    @field:SerializedName("message")
	var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Entity(tableName = "customer_type_table")
@Parcelize
data class CustomerTypeDataItem(

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("organization")
	var organization: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

    @PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("created_by_name")
	var createdByName: String? = null,

	@field:SerializedName("created_by")
	var createdBy: Int? = null
) : Parcelable
