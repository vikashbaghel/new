package com.app.rupyz.generic.utils

import android.content.Context
import android.location.Geocoder
import java.util.Locale


object GeoLocationUtils {

    fun getAddress(context: Context, latitude: Double, longitude: Double): String? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(latitude, longitude, 1)
        return if (address.isNullOrEmpty().not()) {
            buildString {
//				if (address?.get(0)?.featureName.isNullOrBlank().not() && address?.get(0)?.featureName?.contains("Unnamed",true) == false){
//					append(address?.get(0)?.featureName?:"")
//					append(if(address?.get(0)?.featureName.isNullOrBlank()){ " " } else { "" })
//				}
//				append(address?.get(0)?.locality?:"")
//				append(if(address?.get(0)?.locality.isNullOrBlank().not()){ " " } else { "" })
//				append(address?.get(0)?.subLocality?:"")
//				append(if(address?.get(0)?.subLocality.isNullOrBlank().not()){ " " } else { "" })
//				append(address?.get(0)?.adminArea?:"")
//				append(if(address?.get(0)?.adminArea.isNullOrBlank().not()){ " " } else { "" })
//				append(address?.get(0)?.subAdminArea?:"")
//				append(if(address?.get(0)?.subAdminArea.isNullOrBlank().not()){ " " } else { "" })
                for (i in 0..(address?.get(0)?.maxAddressLineIndex ?: 0)) {
                    append(address?.get(0)?.getAddressLine(i))
                    append(if (address?.get(0)?.getAddressLine(i).isNullOrBlank().not()) {
                        " "
                    } else {
                        ""
                    })
                }
//				append(address?.get(0)?.getAddressLine(0))
//				append(address?.get(0)?.countryName?:"")
//				append(if(address?.get(0)?.countryName.isNullOrBlank().not()){ " " } else { "" })
//				append(address?.get(0)?.postalCode?:"")
            }
        } else {
            null
        }
    }
}