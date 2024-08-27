package com.app.rupyz.databse.order

import androidx.room.TypeConverter
import com.app.rupyz.generic.helper.fromJson
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.app.rupyz.model_kt.order.order_history.DispatchHistoryListModel
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.model_kt.order.order_history.TaxInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun stringToCartItemDiscountList(source: String?): ArrayList<CartItemDiscountModel>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<CartItemDiscountModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToCartItemDiscountString(list: ArrayList<CartItemDiscountModel>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToCartItemList(source: String?): ArrayList<CartItem>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<CartItem>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToCartItemString(list: ArrayList<CartItem>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToDispatchHistoryListList(source: String?): ArrayList<DispatchHistoryListModel>? {
        if (source == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<DispatchHistoryListModel>>() {

        }.type

        return gson.fromJson(source, listType)
    }

    @TypeConverter
    fun listToDispatchHistoryListString(list: ArrayList<DispatchHistoryListModel>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromAddressToString(value: String): Address? {
        return Gson().fromJson(value, Address::class.java)
    }

    @TypeConverter
    fun fromStringToAddressClass(data: Address?): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromCreatedByToString(value: String): CreatedBy? {
        return Gson().fromJson(value, CreatedBy::class.java)
    }

    @TypeConverter
    fun fromStringToCreatedByClass(data: CreatedBy?): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromPaymentDetailsModelToString(value: String): PaymentDetailsModel? {
        return Gson().fromJson(value, PaymentDetailsModel::class.java)
    }

    @TypeConverter
    fun fromStringToPaymentDetailsModelClass(data: PaymentDetailsModel?): String {
        return Gson().toJson(data)
    }
    @TypeConverter
    fun fromTaxInfoModelClassToString(data: TaxInfo?): String {
        return Gson().toJson(data)
    }

    @TypeConverter
    fun fromStringToTaxInfoModelClass(data : String) : TaxInfo?{
        return Gson().fromJson<TaxInfo?>(data,TaxInfo::class.java)
    }
}