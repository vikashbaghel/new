package com.app.rupyz.generic.utils

import com.app.rupyz.generic.utils.AppConstant.LIVE_LOCATION_END_TIME
import com.app.rupyz.generic.utils.AppConstant.LIVE_LOCATION_START_TIME
import java.util.Calendar

fun isTimeLessThanTo8PM(): Boolean {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    return hourOfDay in LIVE_LOCATION_START_TIME until LIVE_LOCATION_END_TIME // 7 is inclusive, 20 is exclusive
}