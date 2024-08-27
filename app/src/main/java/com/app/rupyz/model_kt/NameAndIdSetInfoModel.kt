package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.apache.commons.codec.language.bm.Lang

@Parcelize
data class NameAndIdSetInfoModel(

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("is_selected")
    var isSelected: Boolean = false

) : Parcelable


@Parcelize
data class NameAndValueSetInfoModel(

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("value")
        var value: String? = null,

        @field:SerializedName("geo_address")
        var geoAddressValue: String? = null,

        @field:SerializedName("geo_location_long")
        var geoLocationLong: Double? = null,

        @field:SerializedName("geo_location_lat")
        var geoLocationLat: Double? = null,

        @field:SerializedName("label")
        var label: String? = null,

        var isRequired: Boolean? = null,

        @field:SerializedName("is_custom")
        var isCustom: Boolean? = null,

        @field:SerializedName("type")
        var type: String? = null,

        @field:SerializedName("sub_module_type")
        var subModuleType: String? = null,

        @field:SerializedName("sub_module_id")
        var subModuleId: String? = null,

        @field:SerializedName("data_type")
        var dataType: String? = null,

        @field:SerializedName("img_urls")
        var imgUrls: ArrayList<String>? = null,

) : Parcelable

@Parcelize
data class GeoAddressValue(
    val address : String,
    val lat : LatLng,
) : Parcelable