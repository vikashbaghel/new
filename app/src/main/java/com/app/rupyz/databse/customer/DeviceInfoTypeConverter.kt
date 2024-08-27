package com.app.rupyz.databse.customer

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.DeviceInformationModel
import com.google.gson.Gson

class DeviceInfoTypeConverter {
    @TypeConverter
    fun fromDeviceInformationModelToString(value: String): DeviceInformationModel? {
        return Gson().fromJson(value, DeviceInformationModel::class.java)
    }

    @TypeConverter
    fun fromStringToDeviceInformationModelClass(data: DeviceInformationModel?): String? {
        return Gson().toJson(data)
    }
}