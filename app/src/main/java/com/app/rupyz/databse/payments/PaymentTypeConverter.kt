package com.app.rupyz.databse.payments

import androidx.room.TypeConverter
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.CreatedBy
import com.app.rupyz.model_kt.order.order_history.DispatchHistoryListModel
import com.app.rupyz.model_kt.order.order_history.PaymentDetailsModel
import com.app.rupyz.model_kt.order.payment.Customer
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PaymentTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromCustomerToString(value: String): Customer? {
        return Gson().fromJson(value, Customer::class.java)
    }

    @TypeConverter
    fun fromStringToCustomerClass(data: Customer?): String? {
        return Gson().toJson(data)
    }
}