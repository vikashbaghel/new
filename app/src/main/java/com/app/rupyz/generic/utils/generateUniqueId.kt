package com.app.rupyz.generic.utils

fun generateUniqueId(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}