package com.app.rupyz.databse.customer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerListData(productList: List<CustomerData>)

    @RawQuery
    fun getCustomerData(
            query: SupportSQLiteQuery
    ): List<CustomerData>

    @Query("SELECT * FROM customer_table WHERE isSyncedToServer = 0 AND errorMessage IS NOT NULL")
    fun getCustomerDataWithError(): List<CustomerData>

    @Query("SELECT * FROM customer_table WHERE id LIKE '%' || :id || '%'")
    fun getCustomerIdData(id: Int): List<CustomerData>

    @Query("SELECT * FROM customer_table WHERE isSyncedToServer = 0")
    fun getCustomerNotSyncedData(): List<CustomerData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerTypeListData(productList: List<CustomerTypeDataItem>)

    @Query("SELECT * FROM customer_type_table WHERE name LIKE '%' || :name || '%' LIMIT :pageSize OFFSET :offset")
    fun getCustomerTypeData(name: String, pageSize: Int, offset: Int): List<CustomerTypeDataItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerAddress(list: List<CustomerAddressDataItem>)

    @Query("SELECT * FROM customer_address_table WHERE customer = :customerId")
    fun getAddressListForCustomer(customerId: Int): List<CustomerAddressDataItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflineCustomer(model: CustomerData)

    @Update
    fun updateOfflineCustomer(model: CustomerData)

    @Query("DELETE FROM customer_table WHERE id = :customerId")
    fun deleteOfflineCustomerColumn(customerId: Int)

    @Query("DELETE FROM customer_table")
    fun deleteAllCustomer()

    @Query("DELETE FROM customer_type_table")
    fun deleteAllCustomerType()

    @Query("DELETE FROM customer_address_table")
    fun deleteAllCustomerAddress()

    @Query("DELETE FROM record_activity_table")
    fun deleteAllActivity()

    @Query("DELETE FROM record_activity_table WHERE id = :id")
    fun deleteCustomerActivity(id: Int?)

    @Query("DELETE FROM customer_address_table WHERE id = :addressId")
    fun deleteCustomerAddress(addressId: Int?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflineCustomerAddress(address: CustomerAddressDataItem)

    @Query("SELECT * FROM record_activity_table WHERE moduleId = :customerId")
    fun getCustomerFeedbackList(customerId: Int): List<CustomerFollowUpDataItem>

    @Query("SELECT * FROM record_activity_table")
    fun getAllCustomerFeedbackList(): List<CustomerFollowUpDataItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerFeedbackData(model: CustomerFollowUpDataItem)

    @Query("UPDATE record_activity_table SET moduleId = :newCustomerId, isCustomerIdUpdated = 1 WHERE moduleId = :oldCustomerId")
    fun updateCustomerActivity(oldCustomerId: Int, newCustomerId: Int)

    @Query("SELECT * FROM record_activity_table WHERE isCustomerIdUpdated = 1 AND moduleType IS NOT :type")
    fun getCustomerActivityNotSyncedData(type: String): List<CustomerFollowUpDataItem>

    @Query("UPDATE customer_address_table SET customer = :newCustomerId, isCustomerIdUpdated = 1 WHERE customer = :oldCustomerId")
    fun updateCustomerAddress(oldCustomerId: Int, newCustomerId: Int)

    @Query("SELECT * FROM customer_address_table WHERE isSyncedToServer = 0")
    fun getCustomerAddressNotSyncedData(): List<CustomerAddressDataItem>

    @Query("SELECT * FROM customer_address_table WHERE isSyncedToServer = 0 AND isCustomerIdUpdated = 1")
    fun getCustomerAddressNotSyncedDataWithUpdatedCustomerID(): List<CustomerAddressDataItem>

    @Query("UPDATE customer_table SET errorMessage = :message WHERE id = :customerId")
    fun updateCustomerError(customerId: Int, message: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOfflineAttendance(model: AddCheckInOutModel)

    @Query("SELECT * FROM offline_attendance")
    fun getAttendanceData(): List<AddCheckInOutModel>?

    @Query("DELETE FROM offline_attendance")
    fun deleteAttendanceData()

}