package com.app.rupyz.generic.utils

import android.content.Context
import android.content.Intent

fun <T> Context.navigateActivity(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)
}