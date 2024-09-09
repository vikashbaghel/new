package com.app.rupyz.databse

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NameAndValueSetInfoListConverter {

    private val gson = Gson()
    @TypeConverter
    fun stringToCategoryList(source: String?): ArrayList<NameAndValueSetInfoModel>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<NameAndValueSetInfoModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToCategoryList(list: ArrayList<NameAndValueSetInfoModel>?): String? {
        return gson.toJson(list)
    }
}