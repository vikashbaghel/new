package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllCategoryInfoModel(

	@field:SerializedName("headers")
	val headers: Headers? = null,

	@field:SerializedName("data")
	var data: ArrayList<AllCategoryResponseModel>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Entity(tableName = "product_category_table")
@Parcelize
data class AllCategoryResponseModel(

	@field:SerializedName("is_selected")
	var isSelected: Boolean = false,

	var isEnable: Boolean = false,

	@field:SerializedName("name")
	var name: String? = null,

	@PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("customer_id")
	var customerId: Int? = null
) : Parcelable

@Parcelize
data class Headers(

	@field:SerializedName("next_params")
	val nextParams: String? = null
) : Parcelable
