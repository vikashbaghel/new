package com.app.rupyz.databse.lead

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.rupyz.model_kt.LeadCategoryDataItem
import com.app.rupyz.model_kt.LeadLisDataItem

@Dao
interface LeadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLeadData(list: List<LeadLisDataItem>)

    @Query("SELECT * FROM lead_table WHERE businessName LIKE '%' || :name || '%' " +
            "AND leadCategoryName LIKE '%' || :category || '%' ORDER BY createdAt DESC " +
            "LIMIT :pageSize OFFSET :offset")
    fun getLeadList(
            name: String,
            category: String,
            pageSize: Int,
            offset: Int
    ): List<LeadLisDataItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLeadCategoryData(list: List<LeadCategoryDataItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflineLead(jsonObject: LeadLisDataItem)

    @Query("SELECT * FROM lead_table WHERE id = :leadId")
    fun getLeadList(leadId: Int): List<LeadLisDataItem>

    @Query("SELECT * FROM lead_category_table")
    fun getLeadCategoryList(): List<LeadCategoryDataItem>

    @Query("DELETE FROM lead_table")
    fun deleteAllLead()

    @Query("DELETE FROM lead_category_table")
    fun deleteAllLeadCategory()

    @Update
    fun updateOfflineLead(leadData: LeadLisDataItem)

    @Query("DELETE FROM lead_table WHERE id = :leadId")
    fun deleteOfflineLead(leadId: Int)

    @Query("SELECT * FROM lead_table WHERE isSyncedToServer = 0")
    fun getLeadNotSyncedData():  List<LeadLisDataItem>

    @Query("UPDATE record_activity_table SET moduleId = :newCustomerId, isCustomerIdUpdated = 1 WHERE moduleId = :oldCustomerId")
    fun updateLeadActivity(oldCustomerId: Int, newCustomerId: Int)
}