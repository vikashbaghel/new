package com.app.rupyz.databse.order

import androidx.room.TypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PicMapListTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun stringToImageList(source: String?): ArrayList<PicMapModel>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<PicMapModel>>() {}.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToImageString(list: ArrayList<PicMapModel>?): String? {
        return gson.toJson(list)
    }
}