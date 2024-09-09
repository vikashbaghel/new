package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailsResponseModel(

    @field:SerializedName("data")
    var data: ProductDetailInfoModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable
