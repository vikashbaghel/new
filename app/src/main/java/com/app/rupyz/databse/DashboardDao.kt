package com.app.rupyz.databse

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.OrgBeatModel
import com.app.rupyz.model_kt.order.dashboard.DashboardData

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboard_table")
    fun getDashboardData() : List<DashboardData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDashboardData(article: DashboardData) : Long

    @Query("DELETE FROM dashboard_table")
    fun deleteDashboardData()

    @RawQuery
    fun updateDashboardData(query: SupportSQLiteQuery) : List<DashboardData>

    @Query("DELETE FROM customer_feedback_list_table")
    fun deleteFeedbackListData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedbackListData(data: List<CustomerFeedbackStringItem>)

    @Query("SELECT * FROM customer_feedback_list_table")
    fun getFeedbackListData() : List<CustomerFeedbackStringItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBeatData(data: List<OrgBeatModel>)

    @Query("SELECT * FROM org_beat_list_table WHERE name LIKE '%' || :name || '%' ")
    fun getBeatListData(name: String) : List<OrgBeatModel>

}