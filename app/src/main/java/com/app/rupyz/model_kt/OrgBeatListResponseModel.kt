package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrgBeatListResponseModel(

	@field:SerializedName("data")
	var data: List<OrgBeatModel>? = null,

	@field:SerializedName("headers")
	val headers: Headers? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Entity(tableName = "org_beat_list_table")
@Parcelize
data class OrgBeatModel(

	@field:SerializedName("updated_at")
	var updatedAt: String? = null,

	@field:SerializedName("organization")
	var organization: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("updated_by")
	var updatedBy: String? = null,

	@field:SerializedName("created_at")
	var createdAt: String? = null,

	@PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("customer_count")
	var customerCount: Int? = null,

	@field:SerializedName("created_by")
	var createdBy: Int? = null,

	@Ignore
	@field:SerializedName("customer_set")
	var customerSet: List<NameAndIdSetInfoModel?>? = null
) : Parcelable
