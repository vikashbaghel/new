package com.app.rupyz.databse.customer

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.sales.UpdateMappingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CustomerTypeConverters {

    private val gson = Gson()
    @TypeConverter
    fun fromCustomerDataToString(value: String): CustomerData? {
        return Gson().fromJson(value, CustomerData::class.java)
    }

    @TypeConverter
    fun fromStringToCustomerDataClass(data: CustomerData?): String? {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromUpdateMappingModelToString(value: String): UpdateMappingModel? {
        return Gson().fromJson(value, UpdateMappingModel::class.java)
    }

    @TypeConverter
    fun fromStringToUpdateMappingModelClass(data: UpdateMappingModel?): String? {
        return Gson().toJson(data)
    }
}