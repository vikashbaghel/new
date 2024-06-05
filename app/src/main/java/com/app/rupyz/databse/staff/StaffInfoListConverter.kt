package com.app.rupyz.databse.staff

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.JointStaffInfoModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StaffInfoListConverter {

    private val gson = Gson()
    @TypeConverter
    fun stringToStaffInfoList(source: String?): ArrayList<JointStaffInfoModel>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<JointStaffInfoModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToStaffInfoList(list: ArrayList<JointStaffInfoModel>?): String? {
        return gson.toJson(list)
    }
}