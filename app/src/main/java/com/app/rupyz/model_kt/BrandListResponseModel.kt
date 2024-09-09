package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Parcelize
data class BranListResponseModel(

	@field:SerializedName("data")
    var data: ArrayList<BrandDataItem>? = null,

	@field:SerializedName("message")
    var message: String? = null,

	@field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Entity(tableName = "brand_table")
@Parcelize
data class BrandDataItem(

	@field:SerializedName("product_count")
	var productCount: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

    @PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null
) : Parcelable
