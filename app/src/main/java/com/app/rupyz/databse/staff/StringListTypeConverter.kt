package com.app.rupyz.databse.staff

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListTypeConverter {

    private val gson = Gson()
    @TypeConverter
    fun stringToList(source: String?): ArrayList<String> {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<String>>() {
        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToString(list: ArrayList<String>): String? {
        return gson.toJson(list)
    }
}