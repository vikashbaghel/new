package com.app.rupyz.generic.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createID(): Int {
    val now = Date()
    return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
}