package com.app.rupyz.generic.utils

fun String.splitFullName(): Pair<String, String> {
    val names = this.split(" ")
    val firstName = names.firstOrNull() ?: ""
    val lastName = names.drop(1).joinToString(" ")
    return Pair(firstName, lastName)
}