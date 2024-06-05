package com.app.rupyz.databse.product

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductListTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun stringToTelescopicList(source: String?): ArrayList<TelescopicPricingModel> {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<TelescopicPricingModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToTelescopicString(list: ArrayList<TelescopicPricingModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToList(source: String?): ArrayList<PackagingLevelModel> {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<PackagingLevelModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToPackagingString(list: ArrayList<PackagingLevelModel>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToModel(source: String?): PackagingLevelModel? {
        if (source == null) {
            return PackagingLevelModel()
        }

        return gson.fromJson(source, PackagingLevelModel::class.java)
    }

    @TypeConverter
    fun modelToString(source: PackagingLevelModel?): String {
        return gson.toJson(source)
    }
}