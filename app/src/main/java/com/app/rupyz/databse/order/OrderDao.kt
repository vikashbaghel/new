package com.app.rupyz.databse.order

import androidx.annotation.Nullable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.order.order_history.OrderData

@Dao
interface OrderDao {

    @Nullable
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderListData(list: List<OrderData>)

    @Query(
        "SELECT * FROM order_table WHERE deliveryStatus LIKE '%' || :status || '%' " +
                "AND (customerLevel = :customerLevel OR :customerLevel IS '' OR :customerLevel IS NULL) " +
                "AND (fullFilledById = :fulfilledById OR :fulfilledById IS NULL) " +
                "ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset"
    )
    fun getOrderList(
        status: String,
        fulfilledById: Int?,
        customerLevel: String,
        pageSize: Int,
        offset: Int
    ): List<OrderData>

    @Query("SELECT * FROM order_table WHERE customerId = :customerId ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getOrderListWithCustomerMapping(
        customerId: Int,
        pageSize: Int,
        offset: Int
    ): List<OrderData>

    @Query("SELECT * FROM order_table WHERE id = :id")
    fun getOrderDetailsById(
        id: Int
    ): List<OrderData>
    
    @Nullable
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderDispatchListData(list: List<DispatchedOrderModel>)


    @Query("SELECT * FROM order_dispatched_table WHERE id = :dispatchId AND `order` = :orderId")
    fun getOrderDispatchDetailsById(
        orderId: Int,
        dispatchId: Int
    ): List<DispatchedOrderModel>
    
    @Nullable
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrderToDatabase(cartListResponseModel: OrderData)

    @Query("DELETE FROM order_table")
    fun deleteAllOrder()

    @Query("DELETE FROM order_dispatched_table")
    fun deleteAllOrderDispatchList()

    @Query("DELETE FROM order_table WHERE id = :orderId")
    fun deleteOfflineOrderData(orderId: Int?)
    
    @Nullable
    @Update
    fun updateOrder(model: OrderData)
    @Query("SELECT * FROM order_table WHERE isSyncedToServer = 0")
    fun getOrderNotSyncedData(): List<OrderData>

    @Query("SELECT * FROM order_table WHERE isSyncedToServer = 0 AND isCustomerIdUpdated = 1")
    fun getOrderNotSyncedDataWithUpdatedCustomerID(): List<OrderData>

    @Query("UPDATE order_table SET customerId = :newCustomerId, isCustomerIdUpdated = 1 WHERE customerId = :oldCustomerId")
    fun updateCustomerOrder(oldCustomerId: Int, newCustomerId: Int)

    @Transaction
    @Query("UPDATE order_table SET address = :updatedJsonData WHERE orderId = :orderId")
    fun updateOrderJsonData(orderId: String?, updatedJsonData: String)
}