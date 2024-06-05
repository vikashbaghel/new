package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerAddressListResponseModel(

	@field:SerializedName("data")
    var data: List<CustomerAddressDataItem>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable


@Parcelize
data class CustomerAddressApiResponseModel(

	@field:SerializedName("data")
	val data: CustomerAddressDataItem? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Entity(tableName = "customer_address_table")
@Parcelize
data class CustomerAddressDataItem(

	@field:SerializedName("pincode")
	var pincode: String? = null,

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("city")
	var city: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("address_line_1")
	var addressLine1: String? = null,

	@field:SerializedName("updated_by")
	var updatedBy: Int? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

    @PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("state")
	var state: String? = null,

	@field:SerializedName("address_line_2")
	var addressLine2: String? = null,

	@field:SerializedName("created_by")
	var createdBy: Int? = null,

	@field:SerializedName("customer")
	var customer: Int? = null,

	var isSelected: Boolean = false,

	@field:SerializedName("is_default")
	var isDefault: Boolean = false,

	@field:SerializedName("source")
	var source: String? = null,

	var isSyncedToServer: Boolean? = null,

	var isCustomerIdUpdated: Boolean? = null,

) : Parcelable
