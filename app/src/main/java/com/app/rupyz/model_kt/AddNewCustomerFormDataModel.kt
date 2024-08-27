package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class AddNewCustomerFormDataModel(

    @field:SerializedName("custom_form_data")
    var customFormData: ArrayList<NameAndValueSetInfoModel?>? = null,
    
    @field:SerializedName("lead")
    var lead: Int? = null,
    
    @field:SerializedName("activity_geo_address")
    var activityGeoAddress: String? = null,
    
    @field:SerializedName("map_location_lat")
    var mapLocationLat: Double? = null,
    
    
    @field:SerializedName("map_location_long")
    var mapLocationLong: Double? = null,
    
    @field:SerializedName("geo_location_lat")
    var geoLocationLat: Double? = null,
    
    
    @field:SerializedName("geo_location_long")
    var geoLocationLong: Double? = null,
    
) : Parcelable

