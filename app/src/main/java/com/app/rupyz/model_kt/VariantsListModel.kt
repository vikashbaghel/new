package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VariantsItem(

        @field:SerializedName("variant_id")
        val variantId: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("options")
        val options: ArrayList<VariantOptionsItem>? = null
) : Parcelable

@Parcelize
data class VariantOptionsItem(

        @field:SerializedName("name")
        val name: String? = null,

        var isSelected: Boolean = false,

        @field:SerializedName("option_id")
        val optionId: String? = null
) : Parcelable


@Parcelize
data class VariantDataInfoItem(

        @field:SerializedName("identifier")
        val identifier: String? = null,

        @field:SerializedName("product_id")
        val productId: Int? = null,

        @field:SerializedName("product_data")
        val productData: ProductDetailInfoModel? = null
) : Parcelable
