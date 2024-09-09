package com.app.rupyz.generic.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.Locale


object GeoLocationUtils {

	private var onLocationFetchedListener : ((Address?) -> Unit)? = null
	private var retryCount = AppConstant.MAX_RETRY_COUNT
	fun getAddress(context : Context, latitude : Double, longitude : Double, onLocationFetched : (String?) -> Unit)  : GeoLocationUtils {
		if (Connectivity.hasInternetConnection(context)) {
			CoroutineScope(Dispatchers.IO).launch {
				try {
					retryCount = 0
					val geoCoder = Geocoder(context, Locale.getDefault())
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
						geoCoder.getFromLocation(latitude, longitude, 1) { addresses ->
							handleAddressResult(addresses, onLocationFetched)
						}
					} else {
						@Suppress("DEPRECATION")
						val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
						handleAddressResult(addresses, onLocationFetched)
					}
				} catch (e: IOException) {
					retry(context, latitude, longitude, onLocationFetched)
				} catch (e: Exception) {
					retry(context, latitude, longitude, onLocationFetched)
				}
			}
		} else {
			onLocationFetched.invoke("")
		}
		
		return  this
	}
	
	private fun handleAddressResult(addresses: List<Address>?, onLocationFetched: (String?) -> Unit) {
		if (addresses.isNullOrEmpty()) {
			onLocationFetched.invoke(null)
			onLocationFetchedListener?.invoke(null)
		} else {
			onLocationFetched.invoke(buildString {
				for (i in 0..addresses[0].maxAddressLineIndex) {
					append(addresses[0].getAddressLine(i)).append(" ")
				}
			}.trim())
			onLocationFetchedListener?.invoke(addresses[0])
		}
	}
	
	fun setOnLocationFetchedListener(onLocationFetched : (Address?) -> Unit){
		this.onLocationFetchedListener = onLocationFetched
	}
	
	private fun retry(context : Context, latitude : Double, longitude : Double, onLocationFetched : (String?) -> Unit) {
		if (retryCount < 5) {
			retryCount ++
			getAddress(context, latitude, longitude, onLocationFetched)
		}else{
			onLocationFetched.invoke(null)
		}
	}
}