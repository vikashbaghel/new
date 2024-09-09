package com.app.rupyz.generic.utils

object StringModificationUtils {

    fun convertCamelCase(str: String) : String {
        val words = str.split(" ").toMutableList()

        var output = ""

        for (word in words) {
            output += word.replaceFirstChar(Char::titlecase) + " "
        }

        return output.trim()
    }
}