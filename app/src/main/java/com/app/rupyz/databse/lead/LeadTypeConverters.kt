package com.app.rupyz.databse.lead

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LeadTypeConverters {

    private val gson = Gson()
    @TypeConverter
    fun fromLeadDataToString(value: String): LeadLisDataItem? {
        return Gson().fromJson(value, LeadLisDataItem::class.java)
    }

    @TypeConverter
    fun fromStringToLeadLisDataItemClass(data: LeadLisDataItem?): String? {
        return Gson().toJson(data)
    }
}