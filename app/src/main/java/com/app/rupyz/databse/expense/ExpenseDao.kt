package com.app.rupyz.databse.expense

import androidx.room.*
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpenseHeadData(list: List<ExpenseTrackerDataItem>)

    @Query("SELECT * FROM expense_head_table WHERE status LIKE '%' || :status || '%' ORDER BY createdAt DESC LIMIT :pageSize OFFSET :offset")
    fun getExpenseHeadList(status: String, pageSize: Int, offset: Int): List<ExpenseTrackerDataItem>

    @Query("SELECT * FROM expense_head_table WHERE id = :rtId")
    fun getExpenseHeadDetail(rtId: Int): List<ExpenseTrackerDataItem>

    @Query("DELETE FROM expense_head_table")
    fun deleteAllExpense()

    @Query("DELETE FROM expense_head_table WHERE id = :id")
    fun deleteExpense(id: Int?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpenseListData(list: List<ExpenseDataItem>)

    @Query("SELECT * FROM expenses_list_table WHERE reimbursementtracker = :rtId ORDER BY createdAt DESC ")
    fun getExpenseList(rtId: Int): List<ExpenseDataItem>

    @Query("SELECT * FROM expenses_list_table WHERE isSyncedToServer = 0 ")
    fun getExpenseNotSyncedList(): List<ExpenseDataItem>

    @Query("SELECT * FROM expenses_list_table WHERE id = :rtId")
    fun getExpenseDetail(rtId: Int): List<ExpenseDataItem>

    @Query("UPDATE expenses_list_table SET reimbursementtracker = :newId, isUpdateReimbursementTracker = 1 WHERE reimbursementtracker = :oldId")
    fun updateExpenseListWithNewTrackerId(oldId: Int?, newId: Int?)

    @Query("UPDATE expense_head_table SET totalAmount = totalAmount + :amount, totalItems = totalItems + :items WHERE id = :rtId")
    fun updateExpenseHead(rtId: Int?, amount: Double?, items: Int)

    @Query("UPDATE expense_head_table SET totalAmount = totalAmount + :amount WHERE id = :rtId")
    fun updateExpenseHeadAmount(rtId: Int?, amount: Double?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflineExpenseData(model: ExpenseDataItem)

    @Update
    fun updateOfflineExpenseData(model: ExpenseDataItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOfflineExpenseHeadData(model: ExpenseTrackerDataItem)

    @Query("DELETE FROM expense_head_table WHERE id = :rtId")
    fun deleteExpenseHeadColumn(rtId: Int)

    @Query("DELETE FROM expenses_list_table WHERE id = :remId")
    fun deleteExpenseColumn(remId: Int)

    @Query("SELECT * FROM expense_head_table WHERE isSyncedToServer = 0")
    fun getExpenseHeadNotSyncedData(): List<ExpenseTrackerDataItem>

    @Query("SELECT * FROM expenses_list_table WHERE isSyncedToServer = 0 AND isUpdateReimbursementTracker = 1")
    fun getExpenseListNotSyncedData(): List<ExpenseDataItem>

    @Query("DELETE FROM expenses_list_table WHERE id = :id")
    fun deleteExpenseList(id: Int?)

    @Query("DELETE FROM expenses_list_table")
    fun deleteAllExpenseList()


}