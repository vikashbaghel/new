package com.app.rupyz.databse.staff

import androidx.room.*
import com.app.rupyz.model_kt.AssignedRoleItem
import com.app.rupyz.model_kt.order.sales.StaffData

@Dao
interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStaffListData(list: List<StaffData>)
    @Query("SELECT * FROM staff_table WHERE name LIKE '%' || :name || '%' LIMIT :pageSize OFFSET :offset")
    fun getStaffList(name: String, pageSize: Int, offset: Int): List<StaffData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStaffRoleData(list: List<AssignedRoleItem>)

    @Query("SELECT * FROM staff_roles_table LIMIT :pageSize OFFSET :offset")
    fun getStaffRolesList(pageSize: Int, offset: Int): List<AssignedRoleItem>

    @Query("DELETE FROM staff_table")
    fun deleteAllStaff()
    @Query("DELETE FROM staff_roles_table")
    fun deleteAllStaffRoles()
}