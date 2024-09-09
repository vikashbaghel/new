package com.app.rupyz.databse.customer

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntListConverter {

    private val gson = Gson()
    @TypeConverter
    fun stringToCategoryList(source: String?): ArrayList<Int?>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<Int?>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToCategoryList(list: ArrayList<Int?>?): String? {
        return gson.toJson(list)
    }
}