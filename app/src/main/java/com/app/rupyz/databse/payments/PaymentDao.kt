package com.app.rupyz.databse.payments

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.rupyz.model_kt.order.payment.RecordPaymentData

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPaymentListData(list: List<RecordPaymentData>)

    @Query("SELECT * FROM payment_table WHERE status LIKE '%' || :status || '%' " +
            "ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getPaymentList(status: String, pageSize: Int, offset: Int): List<RecordPaymentData>

    @Query("DELETE FROM payment_table")
    fun deleteAllPayment()

    @Query("DELETE FROM payment_table WHERE id = :id")
    fun deletePayment(id: Int?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflinePaymentData(paymentData: RecordPaymentData)

    @Query("SELECT * FROM payment_table WHERE isSyncedToServer = 0")
    fun getPaymentNotSyncedList(): List<RecordPaymentData>

    @Query("SELECT * FROM payment_table WHERE isSyncedToServer = 0 AND isCustomerIdUpdated = 1")
    fun getPaymentNotSyncedListWithUpdatedCustomerID(): List<RecordPaymentData>

    @Query("DELETE FROM payment_table WHERE id = :id")
    fun deleteOfflinePaymentColumn(id: Int)

    @Query("SELECT * FROM payment_table WHERE id = :id")
    fun getOfflinePaymentDetails(id: Int): List<RecordPaymentData>

    @Query("UPDATE payment_table SET customerId = :newCustomerId, isCustomerIdUpdated = 1 WHERE customerId = :oldCustomerId")
    fun updateCustomerPayment(oldCustomerId: Int, newCustomerId: Int)
}